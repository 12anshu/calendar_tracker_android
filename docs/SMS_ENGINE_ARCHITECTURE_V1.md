# SMART Expense Tracker

# SMS Engine Architecture v1

**Version:** 1.0 (Draft)
**Status:** In Design Review
**Author:** Ashutosh Kumar
**Architecture Style:** Layered Pipeline with Progressive Enrichment
**Last Updated:** June 2026

---

# Table of Contents

1. Problem Statement
2. SMS Engine Philosophy
3. Domain Model
4. Architectural Principles
5. Overall SMS Engine Architecture
6. Merchant Engine Architecture
7. Data Models
8. Component Responsibilities
9. Package Structure
10. Sequence Diagrams
11. Error Handling Strategy
12. Testing Strategy
13. Future Roadmap

---

# Chapter 1 — Problem Statement

## 1.1 Background

SMART Expense Tracker automatically extracts financial transactions from SMS messages received from banks, credit card providers, UPI applications, wallets, merchants, telecom providers, employers, and other financial institutions.

Unlike APIs, SMS messages have no universal schema. Every institution uses different formats, terminology, abbreviations, and message structures. The same financial event may be described in dozens of different ways.

Examples:

```
Spent Rs.540 at Swiggy
```

```
Txn of INR 540 at Swiggy
```

```
Payment of Rs.540 to Swiggy successful
```

```
Swiggy has received your payment of Rs.540
```

Although the wording differs, each message represents the same underlying financial event.

The SMS Engine exists to transform these unstructured messages into structured financial information.

---

## 1.2 Engineering Challenge

Traditional SMS parsing relies heavily on regular expressions and bank-specific rules.

While this works for small datasets, it quickly becomes difficult to maintain because:

* Every bank uses different wording.
* Message formats evolve over time.
* New payment methods appear regularly.
* The number of merchants is effectively unlimited.

Therefore, the SMS Engine must be designed as a generic financial understanding engine rather than a collection of message-specific parsers.

---

## 1.3 Objectives

The SMS Engine shall:

* Detect financial messages with high accuracy.
* Identify the type of financial message.
* Determine transaction direction.
* Detect financial events.
* Discover participating financial entities.
* Normalize extracted information.
* Produce structured data suitable for budgeting, analytics and insights.

The architecture must be:

* Bank independent
* Maintainable
* Extensible
* Testable
* Explainable
* High performance

---

## 1.4 Scope

Version 1 of the SMS Engine focuses on:

* Financial Detection
* Message Type Classification
* Direction Extraction
* Financial Event Detection
* Merchant Discovery
* Transaction Categorization

Future versions will extend the same architecture to:

* Payee Discovery
* Account Discovery
* Instrument Discovery
* Reference Number Discovery
* AI-assisted Classification
* Learning Engine

---

## 1.5 Non-Goals

The Merchant Engine is **not** responsible for:

* Budget calculations
* Duplicate detection
* Recurring transaction detection
* User-defined rules
* Spending insights
* Recommendation systems

Those responsibilities belong to downstream engines.

The Merchant Engine has a single responsibility:

> Discover the primary financial counterparty involved in a transaction.

---

# Chapter 2 — SMS Engine Philosophy

The SMS Engine is designed using a **Layered Pipeline Architecture**.

Each layer performs exactly one responsibility and enriches the data produced by previous layers.

No layer should repeat work already completed by an earlier stage.

---

## Engine Families

The SMS Engine consists of four architectural families.

### 1. Qualification Engines

Purpose:

Determine whether an incoming message should enter the financial parsing pipeline.

Examples:

* Message Qualification
* Source Validation
* Sender Validation
* Message Integrity Validation

Output:

A qualification result indicating whether downstream processing should continue.

---

### 2. Classification Engines

Purpose:

Produce one value from a fixed set of possible outputs.

Examples:

* Financial Detection
* Message Type
* Transaction Direction
* Financial Event
* Transaction Mode

Characteristics:

* Evidence driven
* Rule based
* Small output space
* High confidence

Architecture:

```
Detector
    ↓
Evidence Collection
    ↓
Resolver
    ↓
Classification Result
```

---

### 3. Entity Discovery Engines

Purpose:

Discover entities from an unlimited search space.

Examples:

* Merchant
* Payee
* Employer
* Account
* Instrument
* Reference Number

Characteristics:

* Unlimited output space
* Candidate discovery
* Evidence accumulation
* Resolution
* Normalization

Architecture:

```
Window Discovery
        ↓
Candidate Building
        ↓
Evidence Collection
        ↓
Resolution
        ↓
Normalization
        ↓
Entity Result
```

---

### 4. Derivation Engines

Purpose:

Generate higher-level business information using outputs produced by previous engines.

Examples:

* Category
* Duplicate Detection
* Budget Impact
* Spending Insights
* Recurring Transactions

Architecture:

```
Context
    ↓
Business Rules
    ↓
Learning
    ↓
Derived Result
```

---

## Core Philosophy

Every engine belongs to exactly one architectural family.

Classification Engines must never be implemented as Entity Discovery Engines.

Entity Discovery Engines must never behave like Classification Engines.

This separation keeps the architecture consistent, maintainable and extensible.

---

# Chapter 3 — Domain Model

## 3.1 Core Principle

The SMS Engine models **real-world entities**, not text.

Text extracted from an SMS is only evidence.

The final goal is to identify the corresponding real-world entity.

---

## 3.2 Merchant

Definition:

The primary real-world financial counterparty participating in a transaction.

Examples:

* Swiggy
* Amazon
* Infosys
* BESCOM
* Rahul Kumar

The Merchant is **not necessarily a shop**.

It represents whichever entity financially interacted with the user.

---

## 3.3 Merchant Window

A Merchant Window is a region of SMS text that may contain a Merchant.

Examples:

```
Victoria Spirits
```

```
bharatpe@ybl
```

A Merchant Window is **not** a Merchant.

---

## 3.4 Merchant Candidate

A Merchant Candidate is a Merchant Window enriched with evidence.

It contains:

* Window
* Discovery Strategy
* Evidence
* Score
* Confidence

A Merchant Candidate is still unresolved.

---

## 3.5 Merchant Result

The final resolved Merchant produced by the Merchant Engine.

Contains:

* Canonical Merchant
* Confidence
* Resolution Evidence
* Discovery Strategy
* Normalized Value

Exactly one Merchant Result is produced for each transaction in Version 1.

---

## 3.6 Merchant Alias

Different textual representations of the same Merchant.

Examples:

* AMZN → Amazon
* SWIGGYINDIA → Swiggy
* AMAZON PAY → Amazon

Alias resolution belongs exclusively to the Normalization stage.

---

## 3.7 Merchant Registry

A knowledge base containing known merchants and aliases.

The Registry validates and normalizes.

It never discovers merchants.

---

## 3.8 Merchant Evidence

Evidence supporting a Merchant Candidate.

Examples:

* Structured Window
* Standalone Window
* Embedded Identifier
* Registry Match
* Context Match

Evidence is accumulated throughout the pipeline.

---

## 3.9 Domain Flow

```
SMS
    ↓
Merchant Window
    ↓
Merchant Candidate
    ↓
Merchant Result
    ↓
Canonical Merchant
```

Every stage progressively enriches the previous stage.

No stage skips intermediate processing.

# Chapter 4 — Architectural Principles

This chapter defines the engineering principles that govern the entire SMS Engine.

These principles are **architecture rules**, not implementation guidelines.

Every component developed in the SMS Engine must comply with these principles.

---

# 4.1 Layered Intelligence

The SMS Engine is designed as a layered pipeline.

Each layer consumes knowledge produced by previous layers and enriches it further.

No layer should repeat work already completed by an earlier stage.

Example:

```
Message Qualification
        ↓
Financial Detection
        ↓
Message Type
        ↓
Direction
        ↓
Financial Event
        ↓
Entity Discovery
        ↓
Business Derivation
```

Merchant Discovery already receives:

* Qualification Result
* Financial Result
* Message Type
* Direction
* Financial Event

Therefore Merchant Discovery must never attempt to classify these again.

---

# 4.2 Single Responsibility Principle

Every component must have exactly one responsibility.

Examples:

### Financial Detector

Responsible for:

* Detecting whether a message is financial.

Not Responsible for:

* Direction
* Merchant
* Category

---

### Merchant Window Provider

Responsible for:

* Discovering candidate windows.

Not Responsible for:

* Resolution
* Normalization
* Categorization

---

### Merchant Resolver

Responsible for:

* Selecting the best Merchant Candidate.

Not Responsible for:

* Window discovery
* Alias resolution
* Category mapping

---

### Merchant Normalizer

Responsible for:

* Converting aliases into canonical merchant names.

Not Responsible for:

* Discovery
* Resolution

Every class should have only one reason to change.

---

# 4.3 Progressive Enrichment

Information should be enriched gradually.

Each processing stage adds information.

No stage should overwrite previous outputs.

Example:

```
Raw SMS
      ↓
Qualified Message
      ↓
Financial Result
      ↓
Message Type
      ↓
Direction
      ↓
Financial Event
      ↓
Merchant Window
      ↓
Merchant Candidate
      ↓
Merchant Result
```

Earlier stages remain immutable.

Later stages only add information.

---

# 4.4 Evidence Driven Decisions

Every decision made by the engine must be explainable.

Every classification or entity discovery result should contain supporting evidence.

Example:

Merchant: Swiggy

Evidence:

* Structured Pattern
* "Paid To"
* Debit Transaction
* Card Spend
* Registry Match

The engine should never produce results without evidence.

This improves:

* Debugging
* Regression Testing
* Confidence Scoring
* Future AI Integration

---

# 4.5 Bank Independence

Business logic must never depend on a specific bank.

Avoid implementations such as:

```
if (bank == HDFC)
```

or

```
if (sender.contains("ICICI"))
```

Institution-specific knowledge should be stored in:

* Configuration
* Registries
* Pattern Definitions

Architecture must remain bank independent.

---

# 4.6 Strategy Based Discovery

Entity Discovery must be implemented using independent discovery strategies.

Examples:

* Structured Strategy
* Standalone Strategy
* Embedded Strategy
* Registry Strategy
* AI Strategy (Future)

Each strategy should operate independently.

Adding a new strategy must not require modifying existing strategies.

---

# 4.7 Fail Safe Principle

When evidence is insufficient, the engine must prefer UNKNOWN over an incorrect result.

Incorrect high-confidence results are significantly more harmful than conservative UNKNOWN results.

Example:

Preferred:

```
Merchant = UNKNOWN
Confidence = 20
```

Avoid:

```
Merchant = Block Card
Confidence = 95
```

The engine should only produce high-confidence results when supported by sufficient evidence.

---

# 4.8 Context Before Parsing

Every downstream engine should receive structured context from previous engines.

Entity Discovery should never rebuild context already available.

Context may include:

* Qualification Result
* Financial Result
* Message Type
* Direction
* Financial Event
* Transaction Mode

This improves consistency while reducing duplicated logic.

---

# 4.9 Generic Before Specific

The architecture should solve generic financial parsing problems before introducing institution-specific optimizations.

Example:

Preferred:

* Structured Merchant Window
* Embedded Identifier
* Standalone Entity

Avoid:

* HDFC Merchant Rule
* SBI Merchant Rule
* ICICI Merchant Rule

Institution-specific behavior belongs inside configurable patterns rather than architectural components.

---

# 4.10 Composition Over Conditional Logic

Architecture should be built by composing independent components rather than creating increasingly complex conditional statements.

Preferred:

```
Merchant Engine

↓

Structured Provider

↓

Standalone Provider

↓

Embedded Provider

↓

Resolver
```

Avoid:

```
if (...)
else if (...)
else if (...)
else if (...)
```

New functionality should be added by introducing new components rather than modifying existing ones.

---

# 4.11 Observable Pipeline

Every processing stage must expose intermediate outputs.

Examples:

* Qualification Result
* Financial Result
* Message Type
* Direction
* Merchant Windows
* Merchant Candidates
* Merchant Evidence
* Final Merchant

The engine should always support debugging without requiring internal code inspection.

This principle is especially important for the SMS Analysis Dashboard.

---

# 4.12 Immutable Processing

Processing stages should produce new immutable results rather than modifying existing objects.

Example:

```
MerchantWindow

↓

MerchantCandidate

↓

MerchantResult
```

Instead of repeatedly modifying the same object.

Immutable processing simplifies:

* Debugging
* Unit Testing
* Regression Testing
* Parallel Processing

---

# 4.13 Extensibility

The architecture must support future entity extraction without redesign.

Future engines include:

* Payee
* Employer
* Account
* Instrument
* Reference Number

These engines should reuse the same architectural framework developed for Merchant Discovery.

---

# 4.14 Architecture Stability

Architecture should remain stable throughout implementation.

Implementation must follow the architecture.

Implementation difficulties should be documented and addressed in future architecture versions rather than causing continuous redesign.

Version 1 of the architecture should remain stable after approval.

Subsequent improvements should be introduced as Version 2 enhancements after sufficient production evidence has been collected.

---

# Chapter Summary

The SMS Engine follows a layered, evidence-driven pipeline architecture designed around progressive enrichment.

The architecture emphasizes:

* Single Responsibility
* Explainability
* Maintainability
* Extensibility
* Testability
* Bank Independence

These principles apply uniformly across every engine within the SMS processing pipeline.

# Chapter 5 — Overall SMS Engine Architecture

## 5.1 Architectural Overview

The SMS Engine is designed as a **Layered Progressive Enrichment Pipeline**.

Each processing stage performs exactly one responsibility and enriches the information produced by previous stages.

No engine should repeat work already completed by an earlier engine.

The architecture follows four major layers:

1. Message Qualification
2. Classification
3. Entity Intelligence
4. Business Intelligence

Each layer builds upon knowledge generated by previous layers.

---

# 5.2 High Level Architecture

```text
                    Incoming SMS
                         │
                         ▼
┌──────────────────────────────────────┐
│      Message Qualification Layer     │
└──────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────┐
│      Message Normalization Layer      │
└──────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────┐
│      Classification Layer             │
│--------------------------------------│
│ Financial Detection                   │
│ Message Type Classification           │
│ Direction Classification              │
│ Financial Event Classification        │
│ Transaction Mode Classification       │
└──────────────────────────────────────┘
                         │
                         ▼
                 Extraction Context
                         │
                         ▼
┌──────────────────────────────────────┐
│      Entity Intelligence Layer        │
│--------------------------------------│
│ Merchant Intelligence                 │
│ Payee Intelligence (Future)           │
│ Account Intelligence (Future)         │
│ Instrument Intelligence (Future)      │
│ Reference Intelligence (Future)       │
└──────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────┐
│     Business Intelligence Layer       │
│--------------------------------------│
│ Category Classification               │
│ Duplicate Detection                   │
│ Recurring Detection                   │
│ Budget Impact                         │
│ Spending Insights                     │
│ AI Recommendations (Future)           │
└──────────────────────────────────────┘

```

---

# 5.3 Processing Pipeline

The Processing Pipeline defines **execution order**.

Every qualified SMS passes through the following sequence.

```
Incoming SMS

↓

Message Qualification

↓

Message Normalization

↓

Financial Detection

↓

Message Type Classification

↓

Direction Classification

↓

Financial Event Classification

↓

Transaction Mode Classification

↓

Extraction Context

↓

Entity Intelligence

↓

Business Intelligence
```

Every engine executes only after its prerequisite information becomes available.

---

# 5.4 Knowledge Pipeline

While the Processing Pipeline describes execution order, the Knowledge Pipeline describes how information grows during processing.

```
Raw SMS

↓

Qualified Message

↓

Financial Message

↓

Transaction Message

↓

Debit / Credit Transaction

↓

Financial Event Known

↓

Merchant Known

↓

Category Known

↓

Business Insights Available
```

Every stage reduces ambiguity and increases understanding of the original SMS.

---

# 5.5 Layer Responsibilities

## Layer 1 — Message Qualification

Purpose:

Determine whether an incoming SMS should enter the parsing pipeline.

Responsibilities:

* Sender Validation
* Source Trust Evaluation
* Message Integrity Validation
* Spam Detection (Future)
* Qualification Score

Output:

Qualified Message

---

## Layer 2 — Classification

Purpose:

Identify the characteristics of the financial message.

Responsibilities:

* Financial Detection
* Message Type
* Direction
* Financial Event
* Transaction Mode

Output:

Structured transaction metadata.

---

## Layer 3 — Entity Intelligence

Purpose:

Identify real-world entities participating in the transaction.

Responsibilities:

* Merchant Discovery
* Merchant Resolution
* Merchant Normalization

Future:

* Payee
* Employer
* Instrument
* Account
* Reference Number

Output:

Resolved financial entities.

---

## Layer 4 — Business Intelligence

Purpose:

Transform extracted entities into meaningful financial information.

Responsibilities:

* Category Classification
* Duplicate Detection
* Recurring Transaction Detection
* Budget Impact
* Spending Analytics
* AI Recommendations (Future)

Output:

Business-ready transaction.

---

# 5.6 Extraction Context

The Extraction Context represents the cumulative knowledge generated by previous engines.

Instead of repeatedly parsing the original SMS, downstream engines consume the Extraction Context.

Typical information includes:

* Qualification Result
* Financial Result
* Message Type
* Direction
* Financial Event
* Transaction Mode
* Amount
* Currency
* Account Information
* Sender Information

The Extraction Context becomes the single source of truth for downstream processing.

---

# 5.7 Engine Contract

Every Engine in the SMS Pipeline follows the same contract.

Input

* Structured Context
* Original SMS

Processing

* Perform one responsibility only

Output

* Immutable Result
* Evidence
* Confidence
* Updated Processing Context

An Engine must never modify outputs produced by previous engines.

---

# 5.8 Why This Order?

The execution order is intentional.

Message Qualification precedes all processing because unqualified messages should not consume downstream resources.

Classification precedes Entity Intelligence because entity extraction requires contextual understanding of the transaction.

Entity Intelligence precedes Business Intelligence because business rules depend on correctly identified entities.

This dependency chain minimizes ambiguity while maximizing accuracy.

---

# 5.9 Architectural Characteristics

The SMS Engine is designed to be:

* Layered
* Modular
* Explainable
* Evidence Driven
* Bank Independent
* Extensible
* Testable
* Deterministic
* Observable

Every new capability should integrate into one of the four architectural layers without requiring structural redesign.

---

# 5.10 Chapter Summary

The SMS Engine Architecture consists of four independent processing layers:

1. Message Qualification
2. Classification
3. Entity Intelligence
4. Business Intelligence

Each layer progressively enriches the transaction while maintaining strict separation of responsibilities.

The Processing Pipeline defines execution order.

The Knowledge Pipeline defines information growth.

Together they form the architectural foundation for the SMART Expense Tracker SMS Engine.
