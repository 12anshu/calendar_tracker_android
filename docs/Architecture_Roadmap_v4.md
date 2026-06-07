# Smart Expense Calendar

## Architecture & Continuation Guide

### Version: June 2026

---

# 1. Project Vision

Smart Expense Calendar is an Android application that automatically analyzes SMS messages and builds a financial timeline/calendar without requiring manual expense entry.

Primary goals:

* Automatic transaction detection from SMS
* Expense and income tracking
* Credit card settlement tracking
* Transfer detection
* Calendar-based financial view
* Budget and spending analytics

---

# 2. Core Design Principles

### Rule #1

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

### Rule #2

Always validate against real SMS datasets before production integration.

Current validation dataset:

* 3043 SMS messages
* Exported from real device
* Used as source of truth

---

### Rule #3

Developer Dashboard is the experimentation area.

New extractors should be validated inside:

Transaction Extraction Lab

before integration into production flows.

---

# 3. Current Processing Pipeline

Current pipeline:

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

Future:

MerchantNormalizer
↓
CategoryEngine
↓
TransactionSettlementEngine
↓
Calendar Engine

---

# 4. Detection Layer

## FinancialDetector

Purpose:

Determine whether SMS is financial or non-financial.

Status:

Stable.

Validated against 3043 SMS dataset.

---

## MessageTypeDetector

Supported message types:

* TRANSACTION
* INFORMATION
* OBLIGATION
* PROMOTIONAL

Status:

Stable.

Unknown financial messages reduced dramatically during tuning.

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

Production quality.

---

## DirectionExtractor

Enum:

TransactionDirection

Values:

* CREDIT
* DEBIT
* UNKNOWN

Purpose:

Determine money inflow or outflow.

Accuracy:

~93%

Status:

Stable.

---

## ModeExtractor

Enum:

TransactionMode

Values:

* CARD
* UPI
* BANK_TRANSFER
* EMI
* AUTO_DEBIT
* WALLET
* CASH
* UNKNOWN

Accuracy:

~94%

Status:

Stable.

---

## FinancialEventTypeExtractor

Uses:

FinancialEventType enum

Values:

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

Important decision:

Do NOT overload Direction.

Direction:

* CREDIT
* DEBIT

EventType:

* Expense
* Income
* Refund
* Transfer
* Credit Card Payment

These are separate concepts.

Status:

Stable.

---

## MerchantExtractor

Current strategy:

1. Meal card detection
2. UPI merchant extraction
3. Line-based extraction
4. Pattern extraction
5. VPA extraction

Status:

Working.

Merchant coverage:

~41–44%

This is acceptable because many SMS messages genuinely contain no merchant.

---

## MerchantNormalizer

Purpose:

Normalize merchant variants.

Examples:

ZEPTO MARKETPLACE PRIV
→ Zepto

BUNDL TECHNOLOGIES
→ Swiggy

ETERNALLIM
→ Zomato

Status:

Working.

Expected to grow over time.

---

# 6. Current Extraction Metrics

Latest known state:

Financial SMS:

~2350

Transaction SMS:

1529

Amount Extraction:

~97%

Direction Detection:

~93%

Mode Detection:

~94%

Merchant Coverage:

~41–44%

Unknown Event Types:

<1%

Current extraction pipeline considered stable.

---

# 7. Merchant Insights Phase

Current planned phase.

Features:

* Top Merchants
* Unique Merchant Count
* Merchant Search
* Merchant Coverage Metrics

UI should use normalized merchant names only.

---

# 8. Next Major Milestone

## CategoryEngine

Purpose:

Convert merchants into categories.

Examples:

Zepto
→ Grocery

BigBasket
→ Grocery

Swiggy
→ Food

Zomato
→ Food

Uber
→ Transport

Netflix
→ Entertainment

Amazon
→ Shopping

Flipkart
→ Shopping

This is the next major implementation after Merchant Insights.

---

# 9. Future Roadmap

## Phase 1

Merchant Insights

## Phase 2

Category Engine

## Phase 3

Calendar Analytics

Examples:

Daily spending
Weekly spending
Monthly spending

---

## Phase 4

Credit Card Settlement Engine

Detect:

* Credit card spends
* Credit card bill payments
* Settlements

Avoid double counting.

---

## Phase 5

Transfer Detection Engine

Detect:

* Self transfers
* Internal account movements

Avoid counting as expenses.

---

## Phase 6

Budget Engine

Category budgets.

Examples:

Food budget
Travel budget
Shopping budget

---

## Phase 7

Reports

Examples:

Top merchants
Top categories
Monthly spending trends
Subscription tracking

---

# 10. Important Lessons Learned

## Lesson 1

Never tune using assumptions.

Always validate against exported SMS dataset.

---

## Lesson 2

Keyword matching can create substring bugs.

Example:

FD

matched

HDFC

and incorrectly classified transactions as INVESTMENT.

Use full phrases whenever possible.

---

## Lesson 3

Merchant normalization is as important as merchant extraction.

Without normalization:

Zepto
ZEPTO MARKETPLACE PRIV
ZEPTO MARKETPLACE

become separate merchants.

---

## Lesson 4

Direction and EventType must remain separate.

Bad:

Credit = Income

Good:

Credit + Refund
Credit + Transfer
Credit + Income

are distinct business concepts.

---

# 11. Developer Dashboard Purpose

Developer Dashboard exists for:

* SMS analysis
* Detection validation
* Extraction validation
* Exporting CSV
* Auditing rules

It is the primary experimentation environment before production integration.

---

# 12. Current Recommendation

Current priority order:

1. Merchant Insights UI
2. CategoryEngine
3. Calendar Analytics
4. Credit Card Settlement Engine
5. Transfer Detection Engine
6. Budget Engine
7. Reports

Do not redesign the extraction architecture unless real SMS validation demonstrates a problem.
