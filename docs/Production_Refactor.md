# Smart Expense Calendar

## Continuation Guide

### Last Updated: June 2026

---

# Current Project State

The SMS Intelligence Layer has been successfully validated using a real-world dataset.

Current validation dataset:

* 3043 SMS messages
* Real production device export

Current transaction dataset:

* 1529 transaction SMS

---

# Current Metrics

Amount Accuracy

~97%

Direction Accuracy

~97%

Event Type Accuracy

~99%

Merchant Coverage

~96%

Unknown Event Types

<1%

---

# Completed Modules

## Detection Layer

Status: COMPLETE

Components:

* FinancialDetector
* MessageTypeDetector
* SenderValidationEngine

Notes:

Validated against real SMS dataset.

No major changes planned.

---

## Extraction Layer

Status: COMPLETE

Components:

* AmountExtractor
* DirectionExtractor
* ModeExtractor
* FinancialEventTypeExtractor
* MerchantExtractor
* MerchantNormalizer

Notes:

Merchant extraction achieved approximately 96% coverage.

Avoid adding bank-specific extraction logic.

Prefer generic context-based extraction.

MerchantExtractor is considered feature complete.

Only bug fixes should be made.

---

## Developer Dashboard

Status: COMPLETE

Components:

* DeveloperDashboardScreen
* Financial Detection Lab
* Transaction Extraction Lab

Capabilities:

* SMS analysis
* Merchant validation
* CSV export
* Unknown event export
* Merchant missing export

Purpose:

Acts as the validation environment before production integration.

---

# Architecture Decision

IMPORTANT

There are currently two extraction systems.

System A

Developer Dashboard

Uses:

* AmountExtractor
* DirectionExtractor
* ModeExtractor
* FinancialEventTypeExtractor
* MerchantExtractor

System B

Production Application

Uses:

* SMSParser

Current Problem:

SMSParser contains duplicate extraction logic.

This means:

Developer Dashboard accuracy != Production accuracy

This must be fixed before any new major feature work.

---

# Current Priority

## Milestone

Extraction Engine Unification

Goal:

Use the same extraction engine in:

* Developer Dashboard
* Production App

Target Architecture:

SMS
↓
SMSSyncWorker
↓
SMSParser
↓
Shared Extractors
↓
ExpenseRepository
↓
Room
↓
Calendar

---

# Refactoring Plan

Step 1

Refactor SMSParser.

Replace internal extraction logic with:

* AmountExtractor
* DirectionExtractor
* ModeExtractor
* FinancialEventTypeExtractor
* MerchantExtractor
* MerchantNormalizer

Keep:

SMSParser.parse()

public API unchanged.

---

Step 2

Validate production pipeline.

Flow:

SMSSyncWorker
↓
SMSParser
↓
ExpenseRepository

Verify:

* Merchant
* Event Type
* Direction
* Payment Method

match Developer Dashboard results.

---

Step 3

Remove duplicate logic from SMSParser.

Delete:

* detectDirection()
* detectPaymentMethod()
* extractMerchant()
* extractUPIMerchant()
* extractMerchantFromLines()
* extractMerchantFromPatterns()
* extractMerchantFromVPA()
* cleanMerchant()

after migration is verified.

---

Step 4

Regression testing.

Compare:

Developer Dashboard results

vs

Production database results.

They must match.

---

# Frozen Components

Do NOT redesign:

FinancialDetector

MessageTypeDetector

AmountExtractor

DirectionExtractor

ModeExtractor

FinancialEventTypeExtractor

MerchantExtractor

MerchantNormalizer

These modules are considered stable.

---

# Next Major Feature

Category Engine

Status:

Not Started

Purpose:

Convert merchants into categories.

Examples:

Zepto -> Grocery

Swiggy -> Food

Uber -> Transport

Amazon -> Shopping

Netflix -> Entertainment

Planned Components:

* CategoryEngine
* CategoryMatcher
* CategoryRules
* CategoryNormalizer
* TransactionCategory

Start only after Extraction Engine Unification is complete.

---

# Future Roadmap

Phase 1

Extraction Engine Unification

Phase 2

Category Engine

Phase 3

Transaction Storage Enhancements

Phase 4

Calendar Analytics

Phase 5

Credit Card Settlement Engine

Phase 6

Transfer Detection Engine

Phase 7

Budget Engine

Phase 8

Reports & Insights

---

# Important Rules

Rule 1

Never add bank-specific merchant extraction rules unless absolutely required.

Prefer generic context-based extraction.

---

Rule 2

Always validate changes using exported SMS datasets.

Never tune using assumptions.

---

Rule 3

Developer Dashboard is the source of truth for extraction quality.

---

Rule 4

Do not introduce a new TransactionEntity.

Reuse existing ExpenseEntity architecture.

---

Rule 5

Keep one extraction engine only.

Future improvements must automatically benefit both:

Developer Dashboard

and

Production App.

---

# Next Session Starting Point

First task:

Review SMSParser and replace duplicate extraction logic with shared extractors.

No other feature work should begin before this milestone is completed.
