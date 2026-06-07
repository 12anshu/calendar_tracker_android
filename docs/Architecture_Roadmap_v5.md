# Smart Expense Calendar

## Architecture & Roadmap V5

### Version: June 2026

---

# 1. Project Vision

Smart Expense Calendar is an Android application that automatically analyzes financial SMS messages and transforms them into meaningful financial insights without requiring manual expense entry.

Primary goals:

* Automatic transaction detection
* Expense and income tracking
* Merchant recognition
* Category classification
* Credit card settlement tracking
* Calendar-based financial visualization
* Budget planning
* Spending analytics

---

# 2. Core Design Principles

## Rule #1

Detection and Extraction are separate concerns.

Detection answers:

* Is this financial?
* What type of message is it?

Extraction answers:

* Amount
* Direction
* Mode
* Event Type
* Merchant
* Category

---

## Rule #2

Always validate using real SMS datasets.

Current validation dataset:

* 3043 SMS messages
* Real production device data
* Used as source of truth

No extraction logic should be added without dataset validation.

---

## Rule #3

Developer Dashboard is the experimentation environment.

All new detection and extraction logic must be validated in:

* Financial Detection Lab
* Transaction Extraction Lab

before production integration.

---

# 3. Current System Architecture

SMS
↓
FinancialDetector
↓
MessageTypeDetector
↓
AmountExtractor
↓
DirectionExtractor
↓
ModeExtractor
↓
FinancialEventTypeExtractor
↓
MerchantExtractor
↓
MerchantNormalizer
↓
CategoryEngine (Planned)
↓
SettlementEngine (Planned)
↓
CalendarEngine (Planned)

---

# 4. Detection Layer

## FinancialDetector

Purpose:

Determine whether a message is financial.

Status:

✅ Stable

Validation:

* Dataset validated
* Production quality

---

## MessageTypeDetector

Supported Types:

* TRANSACTION
* INFORMATION
* OBLIGATION
* PROMOTIONAL

Status:

✅ Stable

Validation:

* Production quality

---

# 5. Extraction Layer

## AmountExtractor

Purpose:

Extract transaction amount.

Examples:

* INR 500
* Rs.500
* ₹500

Accuracy:

~97%

Status:

✅ Stable

---

## DirectionExtractor

Values:

* CREDIT
* DEBIT
* UNKNOWN

Accuracy:

~97%

Status:

✅ Stable

---

## ModeExtractor

Values:

* UPI
* CARD
* BANK_TRANSFER
* EMI
* AUTO_DEBIT
* CASH
* WALLET
* UNKNOWN

Accuracy:

~94%

Status:

✅ Stable

---

## FinancialEventTypeExtractor

Supported Events:

* EXPENSE
* INCOME
* REFUND
* TRANSFER
* CREDIT_CARD_PAYMENT
* CREDIT_CARD_SPEND
* EMI_PAYMENT
* CASH_WITHDRAWAL
* CASH_DEPOSIT
* INVESTMENT
* UNKNOWN

Accuracy:

~99%

Status:

✅ Stable

Important Decision:

Direction and EventType remain separate concepts.

Example:

CREDIT may represent:

* Income
* Refund
* Transfer

and should never be treated as equivalent.

---

## MerchantExtractor

Current Strategy:

1. UPI Merchant Extraction
2. Line-Based Merchant Extraction
3. Context Pattern Extraction
4. VPA Extraction
5. Merchant Validation
6. Merchant Normalization

Architecture Decision:

Avoid bank-specific extraction logic.

Prefer:

* Context-based extraction
* Candidate validation
* Generic merchant recognition

Current Merchant Coverage:

~96%

Status:

✅ Stable

Production Ready

---

## MerchantNormalizer

Purpose:

Convert merchant variations into a canonical merchant.

Examples:

ZEPTO MARKETPLACE
→ Zepto

BUNDL TECHNOLOGIES
→ Swiggy

ETERNALLIM
→ Zomato

Status:

✅ Stable

---

# 6. Current Metrics

Latest Known Metrics

Financial SMS:

~2350+

Transaction SMS:

1529+

Amount Accuracy:

~97%

Direction Accuracy:

~97%

Mode Accuracy:

~94%

Event Type Accuracy:

~99%

Merchant Coverage:

~96%

Unknown Event Types:

<1%

---

# 7. Developer Validation Platform

Developer Dashboard includes:

## Financial Detection Lab

Purpose:

Validate financial classification quality.

Features:

* Pattern Analysis
* Misclassification Review
* CSV Export

---

## Transaction Extraction Lab

Purpose:

Validate extraction quality.

Features:

* Amount Validation
* Merchant Validation
* Event Type Validation
* Coverage Metrics
* CSV Export
* Merchant Missing Export
* Unknown Event Export

Status:

✅ Complete

---

# 8. Production Modules

## SMS Processing Engine

Status:

✅ Complete

---

## Transaction Intelligence Engine

Status:

✅ Complete

---

## Merchant Intelligence Engine

Status:

✅ Complete

---

## Category Engine

Status:

🚧 Planned

Purpose:

Convert merchant into spending category.

Examples:

Zepto → Grocery

Swiggy → Food

Uber → Transport

Netflix → Entertainment

Amazon → Shopping

Expected Components:

* CategoryMatcher
* CategoryRules
* CategoryNormalizer
* TransactionCategory

Priority:

HIGH

---

# 9. Next Major Milestones

## Phase 1 – Merchant Insights

Status:

80% Complete

Features:

* Top Merchants
* Merchant Search
* Merchant Coverage
* Merchant Analytics

Remaining:

* Merchant Detail Screen
* Merchant Spending History
* Merchant Trend Analysis

---

## Phase 2 – Category Engine

Status:

Next Priority

Features:

* Auto Category Assignment
* Category Rules
* Category Analytics

---

## Phase 3 – Transaction Database Layer

Features:

* Persist Extracted Transactions
* Edit Transactions
* Transaction Search
* Transaction Filters

---

## Phase 4 – Calendar Engine

Features:

* Daily Spending
* Monthly Spending
* Calendar Visualization
* Transaction Timeline

---

## Phase 5 – Credit Card Settlement Engine

Features:

* Credit Card Spend Detection
* Bill Payment Detection
* Auto Settlement Matching
* Double Counting Prevention

---

## Phase 6 – Transfer Detection Engine

Features:

* Self Transfer Recognition
* Internal Account Movement Detection
* Expense Exclusion

---

## Phase 7 – Budget Engine

Features:

* Category Budgets
* Monthly Budget Limits
* Overspending Alerts

---

## Phase 8 – Reports & Analytics

Features:

* Merchant Reports
* Category Reports
* Monthly Trends
* Subscription Tracking
* Spending Insights

---

# 10. Target Package Structure

com.example.smartexpensecalendar

├── core/
│   ├── database/
│   ├── common/
│   ├── analytics/
│   └── utils/
│
├── sms/
│   ├── detection/
│   ├── extraction/
│   ├── merchant/
│   ├── category/
│   ├── sender/
│   └── normalization/
│
├── transactions/
│   ├── data/
│   ├── domain/
│   ├── presentation/
│   └── settlement/
│
├── calendar/
│   ├── data/
│   ├── domain/
│   └── presentation/
│
├── budget/
│   ├── data/
│   ├── domain/
│   └── presentation/
│
├── developer/
│   ├── data/
│   ├── domain/
│   ├── engine/
│   └── presentation/
│
├── ui/
│   ├── navigation/
│   ├── components/
│   └── theme/
│
├── di/
└── MainActivity.kt

---

# 11. Current Recommendation

Current Priority Order:

1. Freeze Extraction Engine
2. Finalize Architecture Refactoring
3. Merchant Insights UI
4. Category Engine
5. Transaction Storage Layer
6. Calendar Engine
7. Credit Card Settlement Engine
8. Budget Engine
9. Reports & Analytics

The SMS Intelligence Layer is considered largely solved.

Future effort should focus on converting extracted intelligence into user-facing financial value.
