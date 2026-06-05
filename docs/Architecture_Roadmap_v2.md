SMART Expense Tracker - Continue Architecture & Implementation

## Project Context

This is a personal finance Android application called SMART Expense Tracker.

Primary goal:

* Parse financial SMS messages
* Detect financial events
* Extract transaction information
* Categorize transactions
* Link related transactions
* Generate insights

We are intentionally building a production-grade SMS financial engine and NOT a simple regex-based parser.

## Current Philosophy

We use:

Detection
↓
Extraction
↓
Enrichment
↓
Business Rules
↓
Transaction Linking
↓
Persistence

instead of mixing everything inside SMSParser.

We want a domain-driven architecture rather than continuously adding if-else conditions.

## Current Finalized Architecture

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

1. Sender Analysis

---

Purpose:
Determine sender type.

Examples:
BANK
CARD
UPI
UNKNOWN

Current Status:
Implemented

Files:
SenderValidationEngine
SenderRegistry

---

2. SMS Normalization

---

Purpose:
Normalize SMS before processing.

Examples:

PYU*SWIGGY FOOD
↓
PYU SWIGGY FOOD

Current Status:
Implemented

Files:
SMSNormalizer

---

3. Detection Engine

---

Purpose:
Answer:

* Is this financial?
* What kind of message?
* What financial event?
* Which channel?
* Which fields probably exist?

Detection DOES NOT extract values.

---

## Detection Taxonomy

MessageType

enum class MessageType {

```
TRANSACTION,

OBLIGATION,

INFORMATION,

PROMOTIONAL,

UNKNOWN
```

}

Examples:

TRANSACTION

* Money moved

OBLIGATION

* EMI due
* Credit card bill due
* Collect request

INFORMATION

* Balance alert
* Statement generated

PROMOTIONAL

* Loan offers
* Card upgrade offers

---

## FinancialEventType

Already exists.

enum class FinancialEventType {

```
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
```

}

DO NOT CREATE TransactionType.
Use FinancialEventType everywhere.

---

## TransactionChannel

enum class TransactionChannel {

```
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
```

}

---

## DetectedField

enum class DetectedField {

```
AMOUNT,

ACCOUNT,

CARD,

PAYMENT_METHOD,

RECEIVER,

MERCHANT_CANDIDATE,

LOAN_ACCOUNT,

REFERENCE_NUMBER,

BALANCE
```

}

---

## Detection Engine Design

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

Later these will be composed into DetectionEngine.

---

## Current Detection Progress

Implemented:

MessageType.kt

TransactionChannel.kt

DetectedField.kt

FinancialDetectionResult.kt

FinancialDetector.kt

DetectionPatterns.kt

DetectionConstants.kt

SMSKeywordRegistry.kt

---

## FinancialDetectionResult

data class FinancialDetectionResult(

```
val isFinancial: Boolean,

val confidence: Int,

val score: Int,

val matchedSignals: Set<String>
```

)

---

## FinancialDetector Design

Scoring stages:

1. Strong Signals

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

2. Medium Signals

Uses:

financialSignals

3. Pattern Signals

Uses:

DetectionPatterns

4. Negative Signals

Uses:

negativeFinancialKeywords

5. Multiple Signal Bonus

Applied when multiple strong indicators exist.

---

## DetectionPatterns

DetectionPatterns.kt contains:

amountRegex

upiRegex

accountPatterns

cardPatterns

balancePatterns

Patterns are intentionally separate from SMSKeywordRegistry.

Keywords and Patterns are different concepts.

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

4. Extraction Engine (NOT STARTED)

---

Purpose:
Extract actual values.

Output:

amount

direction

paymentMethod

accountSuffix

rawSenderParty

rawReceiverParty

rawMerchantCandidate

---

## Important Learning

Receiver != Merchant

Examples:

Swiggy
Receiver = Swiggy
Merchant = Swiggy

Pratap Reddy
Receiver = Pratap Reddy
Merchant = null

Credit Card 1234
Receiver = Card
Merchant = null

Therefore:

rawSenderParty

rawReceiverParty

rawMerchantCandidate

must remain separate.

---

5. Enrichment Engine (NOT STARTED)

---

Purpose:
Convert extracted data into business meaning.

Includes:

Merchant Cleanup

Merchant Matching

Merchant Normalization

Merchant Confidence

Category Detection

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

6. Business Rules Engine

---

Partially Implemented

Examples:

Internal Transfer

Credit Card Settlement

Refund Linking

Salary Detection

EMI Handling

---

7. Transaction Linking

---

Partially Implemented

Examples:

Expense
↓
Refund

Card Spend
↓
Card Payment

---

8. Persistence

---

Current fields:

amount

merchant

financialEventType

paymentMethod

confidence

accountSuffix

linkedId

Future additions:

rawSenderParty

rawReceiverParty

rawMerchantCandidate

merchantConfidence

senderType

---

## Directory Structure

com.example.smartexpensecalendar

├── config/
│
│   ├── MerchantRegistry.kt
│   ├── SenderRegistry.kt
│   ├── NoiseWordRegistry.kt
│   ├── SMSKeywordRegistry.kt
│   └── DetectionConstants.kt
│
├── sms/
│
│   ├── detection/
│   │
│   │   ├── FinancialDetector.kt
│   │   ├── FinancialDetectionResult.kt
│   │   ├── DetectionPatterns.kt
│   │   ├── MessageType.kt
│   │   ├── TransactionChannel.kt
│   │   ├── DetectedField.kt
│   │
│   ├── extraction/
│   │
│   │   ├── ExtractionEngine.kt
│   │   ├── AmountExtractor.kt
│   │   ├── DirectionExtractor.kt
│   │   ├── PaymentMethodExtractor.kt
│   │   ├── AccountExtractor.kt
│   │   └── PartyExtractor.kt
│   │
│   ├── merchant/
│   │
│   │   ├── MerchantCleanupEngine.kt
│   │   ├── MerchantMatcher.kt
│   │   ├── MerchantMatchResult.kt
│   │   └── MerchantNormalizer.kt
│   │
│   ├── sender/
│   │
│   │   └── SenderValidationEngine.kt
│   │
│   ├── SMSNormalizer.kt
│   └── SMSParser.kt

---

## Current Status Summary

Completed:

✓ FinancialEventType
✓ TransactionDirection
✓ PaymentMethod
✓ Confidence
✓ Sender Analysis
✓ SMSNormalizer
✓ Amount Extraction
✓ Direction Detection
✓ Payment Method Detection
✓ Account Detection
✓ Detection Foundation
✓ FinancialDetector

Partially Complete:

⚠ Detection Layer
⚠ Business Rules
⚠ Transaction Linking

Not Started:

✗ MessageTypeDetector
✗ FinancialEventDetector
✗ ChannelDetector
✗ FieldDetector
✗ Extraction Engine Refactor
✗ PartyExtractor
✗ Merchant Engine
✗ Merchant Confidence
✗ Category Engine V2

## Next Step When Continuing

Review FinancialDetector implementation and validate against real SMS samples.

Then implement:

1. MessageTypeDetector
2. FinancialEventDetector
3. ChannelDetector
4. FieldDetector

Only after Detection Layer is complete, start Extraction Engine refactor.
