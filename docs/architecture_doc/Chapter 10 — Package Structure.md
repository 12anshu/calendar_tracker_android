# Chapter 10 — Package Structure

## 10.1 Purpose

This chapter defines the physical organization of the SMS Engine.

The package structure mirrors the architectural layers defined in previous chapters.

The objective is to ensure:

* High cohesion
* Low coupling
* Clear ownership
* Easy navigation
* Independent testing
* Long-term maintainability

---

# 10.2 High Level Package Structure

```text
sms/

├── qualification/
│
├── normalization/
│
├── classification/
│   ├── financial/
│   ├── messagetype/
│   ├── direction/
│   ├── event/
│   └── mode/
│
├── entity/
│   ├── framework/
│   ├── merchant/
│   ├── payee/          (Future)
│   ├── account/        (Future)
│   ├── instrument/     (Future)
│   ├── employer/       (Future)
│   └── reference/      (Future)
│
├── business/
│   ├── category/
│   ├── duplicate/
│   ├── recurring/
│   ├── insights/
│   └── budget/
│
├── models/
│
├── context/
│
├── registry/
│
├── common/
│
└── dashboard/
```

---

# 10.3 Qualification Package

Responsible for validating incoming messages before entering the processing pipeline.

Contains:

* Message Qualification
* Source Validation
* Sender Validation
* Qualification Result

---

# 10.4 Classification Package

Responsible for classifying financial characteristics.

Contains:

* Financial Detection
* Message Type
* Direction
* Financial Event
* Transaction Mode

Each classifier is independent.

---

# 10.5 Entity Package

Responsible for discovering financial entities.

The package is divided into two layers.

### Framework

Contains reusable components shared across all entities.

Examples:

* Entity Pipeline
* Common Interfaces
* Shared Models
* Base Contracts

### Entity Implementations

Contains entity-specific logic.

Examples:

* Merchant
* Payee
* Account
* Instrument
* Employer
* Reference

Each entity implementation remains independent.

---

# 10.6 Business Package

Responsible for business interpretation.

Contains:

* Category Classification
* Duplicate Detection
* Recurring Detection
* Budget Processing
* Spending Insights

Business logic should never appear inside Entity Intelligence.

---

# 10.7 Models Package

Contains immutable models shared across the framework.

Examples:

* SMS Models
* Context Models
* Entity Models
* Classification Models
* Business Models

Models must remain implementation independent.

---

# 10.8 Context Package

Contains shared processing context.

Examples:

* Qualification Context
* Extraction Context
* Business Context

Context objects become the communication contract between engines.

---

# 10.9 Registry Package

Contains reusable knowledge bases.

Examples:

* Merchant Registry
* Alias Registry
* Bank Registry
* Institution Registry
* Sender Registry

Registries contain data only.

Business logic should remain outside registries.

---

# 10.10 Common Package

Contains utilities shared across the SMS Engine.

Examples:

* Constants
* Pattern Utilities
* Scoring Utilities
* Text Utilities
* Evidence Utilities

Common utilities must remain generic.

---

# 10.11 Dashboard Package

Provides internal tooling for development and validation.

Examples:

* SMS Analysis Dashboard
* Debug Export
* Pipeline Visualization
* Statistics
* Regression Reports

This package is intended for development and testing only.

---

# 10.12 Dependency Rules

The package dependencies follow a strict direction.

```text
Qualification
        ↓
Classification
        ↓
Entity
        ↓
Business
```

Rules:

* Upper layers must never depend on lower layers.
* Entity packages must never depend on Business packages.
* Business packages may consume Entity results.
* Shared models and contexts may be used by all layers.

---

# 10.13 Design Principles

The package structure follows these principles:

* Feature-oriented organization
* Layered architecture
* Dependency inversion
* Single responsibility
* Reusability
* Extensibility

The physical structure should always reflect the logical architecture.

---

# 10.14 Chapter Summary

The SMS Engine package structure mirrors the architectural layers defined throughout this document.

Each package has a clearly defined responsibility and dependency direction, ensuring the implementation remains modular, maintainable and scalable as additional entity types and business capabilities are introduced.
