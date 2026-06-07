# SMART Expense Tracker - Architecture Roadmap V3

## Project Vision

SMART Expense Tracker is a production-grade Android financial intelligence platform that uses SMS as a primary financial data source.

Goals:

* Parse financial SMS messages
* Detect financial events
* Extract transaction information
* Identify merchants and parties
* Categorize transactions
* Link related transactions
* Generate financial insights
* Handle real-world banking SMS variations

The architecture is intentionally domain-driven and extensible.

---

# Core Processing Pipeline

SMS Received
↓
Sender Analysis
↓
SMS Normalization
↓
Detection Engine
↓
Extraction Engine
↓
Enrichment Engine
↓
Business Rules Engine
↓
Transaction Linking
↓
Persistence
↓
Insights Engine

---

# Design Principles

## 1. Detection != Extraction

Detection determines:

* Is this financial?
* What kind of message is it?
* What event occurred?
* Which channel was used?
* Which fields likely exist?

Extraction determines:

* Amount
* Account
* Receiver
* Merchant Candidate
* Reference Number

Detection never extracts values.

---

## 2. Receiver != Merchant

Important architectural decision.

Examples:

Swiggy
Receiver = Swiggy
Merchant = Swiggy

Pratap Reddy
Receiver = Pratap Reddy
Merchant = null

Credit Card XXXX1234
Receiver = Credit Card
Merchant = null

Therefore:

rawSenderParty

rawReceiverParty

rawMerchantCandidate

must be stored separately.

---

## 3. Merchant Processing Is Enrichment

Merchant detection should never directly parse SMS.

Correct flow:

SMS
↓
Party Extraction
↓
rawMerchantCandidate
↓
Merchant Cleanup
↓
Merchant Match
↓
Merchant Normalize
↓
Merchant

---

# Phase 1 - Sender Analysis

Purpose:

Identify sender type.

Examples:

BANK
CARD
UPI
UNKNOWN

Status:

Implemented

Files:

SenderValidationEngine

SenderRegistry

---

# Phase 2 - SMS Normalization

Purpose:

Normalize SMS before processing.

Examples:

PYU*SWIGGY FOOD
↓
PYU SWIGGY FOOD

WWWBIGBASKETCOM
↓
WWW BIGBASKET COM

Status:

Implemented

Files:

SMSNormalizer

NoiseWordRegistry

---

# Phase 3 - Detection Engine

Purpose:

Determine message meaning before extraction.

Detection consists of:

FinancialDetector
↓
MessageTypeDetector
↓
FinancialEventDetector
↓
ChannelDetector
↓
FieldDetector
↓
DetectionEngine

DetectionEngine will orchestrate all detectors.

---

## Detection Taxonomy

### MessageType

enum class MessageType {

TRANSACTION,

OBLIGATION,

INFORMATION,

PROMOTIONAL,

UNKNOWN

}

Examples:

TRANSACTION

* Money moved

OBLIGATION

* EMI Due
* Credit Card Bill Due
* UPI Collect Request

INFORMATION

* Balance Alert
* Statement Generated

PROMOTIONAL

* Loan Offer
* Card Upgrade Offer

---

### FinancialEventType

Already exists.

DO NOT CREATE TransactionType.

Use FinancialEventType everywhere.

enum class FinancialEventType {

EXPENSE,

INCOME,

TRANSFER,

CARD_PAYMENT,

REFUND,

SALARY,

EMI,

INVESTMENT,

CASH_WITHDRAWAL,

INTEREST,

CASHBACK,

REVERSAL,

FEE,

UNKNOWN

}

---

### TransactionChannel

enum class TransactionChannel {

UPI,

CARD,

BANK_ACCOUNT,

IMPS,

NEFT,

RTGS,

CASH,

CHEQUE,

MANDATE,

UNKNOWN

}

---

### DetectedField

enum class DetectedField {

AMOUNT,

ACCOUNT,

CARD,

PAYMENT_METHOD,

RECEIVER,

MERCHANT_CANDIDATE,

LOAN_ACCOUNT,

REFERENCE_NUMBER,

BALANCE

}

---

# FinancialDetector

Purpose:

Answer only:

Is this SMS financial?

It does NOT determine event type.

---

## FinancialDetectionResult

data class FinancialDetectionResult(

val isFinancial: Boolean,

val confidence: Int,

val score: Int,

val matchedSignals: Set<String>

)

---

## FinancialDetector Scoring Pipeline

Normalize
↓
Strong Signals
↓
Medium Signals
↓
Pattern Signals
↓
Negative Signals
↓
Multiple Signal Bonus
↓
Threshold Evaluation

---

## Strong Signals

Uses:

expenseKeywords

incomeKeywords

transferKeywords

refundKeywords

salaryKeywords

investmentKeywords

interestKeywords

cashbackKeywords

feeKeywords

emiKeywords

cardPaymentKeywords

---

## Medium Signals

Uses:

financialSignals

---

## Pattern Signals

Uses:

DetectionPatterns

Patterns are separated from keywords.

Keywords detect meaning.

Patterns detect structure.

---

## DetectionPatterns

Contains:

amountRegex

upiRegex

accountPatterns

cardPatterns

balancePatterns

Examples:

swiggy@okhdfcbank

paytm-blinkit@ptybl

A/c XX1234

Card ending 5678

INR 500

---

## Negative Signals

Uses:

negativeFinancialKeywords

Examples:

OTP

LOGIN OTP

VERIFICATION CODE

PASSWORD RESET

---

## Multiple Signal Bonus

Applied when multiple strong indicators exist.

Example:

Amount + UPI + Account + Paid

Very high confidence financial SMS.

---

## DetectionConstants

Contains:

FINANCIAL_THRESHOLD

STRONG_SIGNAL_SCORE

MEDIUM_SIGNAL_SCORE

AMOUNT_PATTERN_SCORE

ACCOUNT_PATTERN_SCORE

CARD_PATTERN_SCORE

UPI_PATTERN_SCORE

BALANCE_PATTERN_SCORE

NEGATIVE_SIGNAL_SCORE

MULTIPLE_SIGNAL_BONUS

---

# Detection Layer Implementation Order

1. FinancialDetector
2. MessageTypeDetector
3. FinancialEventDetector
4. ChannelDetector
5. FieldDetector
6. DetectionEngine

Current Status:

FinancialDetector Completed

Remaining:

MessageTypeDetector

FinancialEventDetector

ChannelDetector

FieldDetector

DetectionEngine

---

# Phase 4 - Extraction Engine

Purpose:

Extract structured values.

Output:

amount

direction

paymentMethod

accountSuffix

rawSenderParty

rawReceiverParty

rawMerchantCandidate

referenceNumber

Current Status:

Not Started

Planned Components:

ExtractionEngine

AmountExtractor

DirectionExtractor

PaymentMethodExtractor

AccountExtractor

PartyExtractor

ReferenceExtractor

---

# Phase 5 - Enrichment Engine

Purpose:

Convert extracted values into business meaning.

---

## Merchant Pipeline

rawMerchantCandidate
↓
MerchantCleanupEngine
↓
MerchantMatcher
↓
MerchantNormalizer
↓
merchant
merchantConfidence

---

## Category Pipeline

merchant
↓
CategoryDetector
↓
category

---

Current Status:

Not Started

---

# Phase 6 - Business Rules Engine

Purpose:

Apply financial intelligence.

Examples:

Internal Transfer

Credit Card Settlement

Refund Recognition

Salary Detection

EMI Processing

Subscription Handling

Current Status:

Partially Implemented

---

# Phase 7 - Transaction Linking

Purpose:

Link related transactions.

Examples:

Expense
↓
Refund

Card Spend
↓
Card Payment

Transfer
↓
Transfer Reversal

Current Status:

Partially Implemented

---

# Phase 8 - Persistence

Current Fields:

amount

merchant

financialEventType

paymentMethod

confidence

accountSuffix

linkedId

Future Fields:

rawSenderParty

rawReceiverParty

rawMerchantCandidate

merchantConfidence

senderType

transactionChannel

messageType

---

# Project Structure

com.example.smartexpensecalendar

├── config/
│
├── MerchantRegistry.kt
├── SenderRegistry.kt
├── NoiseWordRegistry.kt
├── SMSKeywordRegistry.kt
├── DetectionConstants.kt
│
├── sms/
│
├── detection/
│
├── FinancialDetector.kt
├── FinancialDetectionResult.kt
├── DetectionPatterns.kt
├── MessageType.kt
├── TransactionChannel.kt
├── DetectedField.kt
│
├── extraction/
│
├── ExtractionEngine.kt
├── AmountExtractor.kt
├── DirectionExtractor.kt
├── PaymentMethodExtractor.kt
├── AccountExtractor.kt
├── PartyExtractor.kt
├── ReferenceExtractor.kt
│
├── merchant/
│
├── MerchantCleanupEngine.kt
├── MerchantMatcher.kt
├── MerchantMatchResult.kt
├── MerchantNormalizer.kt
│
├── sender/
│
├── SenderValidationEngine.kt
│
├── SMSNormalizer.kt
├── SMSParser.kt

---

# Current Progress Summary

Completed

✓ FinancialEventType

✓ TransactionDirection

✓ PaymentMethod

✓ Confidence Persistence

✓ Sender Analysis

✓ SMSNormalizer

✓ Amount Extraction

✓ Direction Detection

✓ Payment Method Detection

✓ Account Detection

✓ MessageType

✓ TransactionChannel

✓ DetectedField

✓ SMSKeywordRegistry V2

✓ DetectionConstants

✓ DetectionPatterns

✓ FinancialDetectionResult

✓ FinancialDetector

---

In Progress

⚠ Detection Layer

Remaining:

MessageTypeDetector

FinancialEventDetector

ChannelDetector

FieldDetector

DetectionEngine

---

Not Started

✗ Extraction Engine Refactor

✗ PartyExtractor

✗ Merchant Engine

✗ Merchant Confidence

✗ Category Engine V2

✗ Business Rules Refactor

✗ Transaction Linking Refactor

---

# Immediate Next Step

Implement MessageTypeDetector using the same scoring-based architecture used by FinancialDetector.

Do NOT modify SMSParser until the complete Detection Layer is finished.
