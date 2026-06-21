package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import com.example.smartexpensecalendar.sms.config.MerchantRegistry
import com.example.smartexpensecalendar.sms_engine.detector.DetectionPatterns
import java.util.regex.Pattern

object MerchantExtractor {

    private data class MerchantCandidate(val text: String, val baseScore: Int, val score: Int = 0)

    fun extractMerchant(smsText: String): String? {
        val candidates = mutableListOf<MerchantCandidate>()

        // 1. Specific High-Confidence Patterns (80-90 pts)
        candidates.addAll(extractHighConfidenceCandidates(smsText))

        // 2. Generic Patterns (50-60 pts)
        candidates.addAll(extractGenericPatterns(smsText))

        // 3. Line-based Heuristics (40 pts)
        candidates.addAll(extractFromLines(smsText))

        // 4. Score and Pick Winner
        return evaluateCandidates(candidates, smsText)
    }

    private fun extractHighConfidenceCandidates(body: String): List<MerchantCandidate> {
        val list = mutableListOf<MerchantCandidate>()

        // NEFT/IMPS specific
        val neftPatterns = listOf(
            Pattern.compile("NEFT\\s+Cr-[A-Z0-9]+-([A-Z0-9\\s]+?)(?=\\s+-|\\s+\\.|-|$)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("IMPS-[0-9]+-([A-Z0-9\\s]+?)(?=\\s+-|\\s+\\.|-|$)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("NEFT\\s+INWARD-[A-Z0-9]+-([A-Z0-9\\s]+?)(?=\\s+-|\\s+\\.|-|$)", Pattern.CASE_INSENSITIVE)
        )
        neftPatterns.forEach { p ->
            val m = p.matcher(body)
            if (m.find()) m.group(1)?.let { list.add(MerchantCandidate(it, 90)) }
        }

        // Transfer anchors (Deep Research Patterns)
        val transferPatterns = listOf(
            "(?i)to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)\\s+via\\s+(?:NEFT|IMPS|RTGS|UPI|BANK|TRANSFER)",
            "(?i)transferred to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\s+via|\\s+Ref|\\.|\\s+on|$)",
            "(?i)to pay\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+The|\\s+Ref|$)",
            "(?i)towards\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+Ref|\\s+via|$)",
            "(?i)beneficiary\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\s+is|\\.|\\s+Ref|$)",
            "(?i)payment made to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+Ref|$)",
            "(?i)For IMPS\\s+-(.+?)-" 
        )
        transferPatterns.forEach { p ->
            val m = Pattern.compile(p).matcher(body)
            if (m.find()) m.group(1)?.let { list.add(MerchantCandidate(it, 85)) }
        }

        // UPI Handles
        val upiMatcher = Pattern.compile("@UPI[_ ]+([A-Za-z0-9 ]+)", Pattern.CASE_INSENSITIVE).matcher(body)
        if (upiMatcher.find()) upiMatcher.group(1)?.let { 
            list.add(MerchantCandidate(it.replace("\\b\\d+\\b$".toRegex(), ""), 80)) 
        }

        return list
    }

    private fun extractGenericPatterns(body: String): List<MerchantCandidate> {
        val list = mutableListOf<MerchantCandidate>()
        val patterns = listOf(
            "(?i)\\bon\\s+([A-Za-z][A-Za-z0-9 .&@_\\-]{2,50}?)(?:\\.|,|\\s+via|\\s+using|\\s+for|$)",
            "(?i)At\\s+([A-Za-z0-9@._\\-]+?)\\s+by UPI",
            "(?i)at (.+?)(?: on| via|\\.|,|$)",
            "(?i)spent at (.+?)(?: on| via|\\.|,|$)",
            "(?i)paid to (.+?)(?: on| via|\\.|,|$)",
            "(?i)merchant[: ]+(.+?)(?:\\.|,|$)",
            "(?i)for (.+?)(?: on| via|\\.|,|$)"
        )
        patterns.forEach { p ->
            val m = Pattern.compile(p).matcher(body)
            if (m.find()) m.group(1)?.let { list.add(MerchantCandidate(it, 55)) }
        }
        return list
    }

    private fun extractFromLines(body: String): List<MerchantCandidate> {
        val list = mutableListOf<MerchantCandidate>()
        body.lines().forEach { line ->
            val text = line.trim()
            if (text.length in 3..40 && !ExtractionUtils.containsAmount(text) && !ExtractionUtils.containsDate(text)) {
                list.add(MerchantCandidate(text, 40))
            }
        }
        return list
    }

    private fun evaluateCandidates(candidates: List<MerchantCandidate>, fullBody: String): String? {
        if (candidates.isEmpty()) return null

        val scored = candidates.map { candidate ->
            var finalScore = candidate.baseScore
            val text = candidate.text.trim()
            val upper = text.uppercase()

            // 1. REJECTION RULES (Strict)
            if (text.length < 3) return@map candidate.copy(score = -1000)
            
            // --- NEW ROBUST STRUCTURAL CHECK ---
            // If the candidate matches our dynamic Bank Structure regex, it's a technical word, not a merchant.
            if (DetectionPatterns.bankStructureRegex.containsMatchIn(text)) return@map candidate.copy(score = -1000)
            if (DetectionPatterns.bankEntityRegex.containsMatchIn(text)) return@map candidate.copy(score = -1000)
            
            if (text.all { !it.isLetter() }) return@map candidate.copy(score = -1000)

            // 2. INVALID PHRASES PENALTY (-100)
            if (isInvalidMerchant(upper)) finalScore -= 100

            // 3. REGISTRY BONUS (+40)
            val isKnown = MerchantRegistry.merchants.any { def ->
                def.canonicalName.uppercase() == upper || def.aliases.any { it.uppercase() == upper }
            }
            if (isKnown) finalScore += 40

            // 4. POSITION BONUS (+10 if in first 30% of message)
            if (fullBody.indexOf(text) < fullBody.length * 0.3) finalScore += 10

            candidate.copy(score = finalScore)
        }

        // Pick highest score >= 50
        val winner = scored.filter { it.score >= 50 }.maxByOrNull { it.score }
        return winner?.text?.let { cleanAndNormalize(it) }
    }

    private fun cleanAndNormalize(raw: String): String? {
        val cleaned = raw.replace("(?i)^at ".toRegex(), "")
            .replace("(?i)^paid to ".toRegex(), "")
            .replace("(?i)AMZN".toRegex(), "Amazon")
            .replace("[._*@#-]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        return MerchantNormalizer.normalize(cleaned)
    }

    private val invalidMerchantPhrases = setOf(
        "NEFT TXN", "VIA NEFT", "SUCCESSFULLY CREDITED", "DEBITED", "CREDITED", 
        "BANK ACCOUNT", "ACCOUNT NO", "A/C NO", "CARD ENDING", "LIMIT", "AVAILABLE",
        "NOT YOU?", "CALL", "ANY ASSISTANCE", "TRACKING IS", "RECEIVED!", "OTP", "REF"
    )

    private fun isInvalidMerchant(upper: String): Boolean {
        return invalidMerchantPhrases.any { upper.contains(it) }
    }
}
