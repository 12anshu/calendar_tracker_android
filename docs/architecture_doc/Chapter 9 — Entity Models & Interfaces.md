# Chapter 9 — Entity Models & Interfaces

## 9.1 Purpose

This chapter defines the common models and contracts shared across every Entity Intelligence implementation.

These models are generic and reusable.

Entity-specific implementations should extend or specialize these models without modifying the framework.

---

# 9.2 Core Models

The Entity Intelligence Framework consists of the following core models.

```text
EntityWindow
        ↓
EntityAssessment
        ↓
ResolvedEntity
        ↓
NormalizedEntity
        ↓
EntityResult
```

Each model represents one stage of the Entity Processing Pipeline.

---

# 9.3 EntityWindow

Represents a text region discovered during the Discovery stage.

Responsibilities

* Original text
* Start position
* End position
* Discovery strategy
* Discovery metadata

EntityWindow contains no confidence or scoring information.

---

# 9.4 EntityAssessment

Represents the evaluation of an EntityWindow.

Responsibilities

* Reference to EntityWindow
* Confidence
* Score
* Evidence
* Assessment metadata

Assessment enriches the discovered window without modifying it.

---

# 9.5 ResolvedEntity

Represents the winning entity selected during Resolution.

Responsibilities

* Selected assessment
* Resolution strategy
* Resolution reason

Exactly one ResolvedEntity is produced for Version 1.

---

# 9.6 NormalizedEntity

Represents the canonical version of the resolved entity.

Responsibilities

* Canonical name
* Original value
* Alias mapping
* Registry information

Normalization standardizes representation while preserving meaning.

---

# 9.7 EntityResult

Represents the final immutable output of the Entity Intelligence Framework.

Contains

* Canonical Entity
* Original Window
* Confidence
* Score
* Discovery Strategy
* Assessment Summary
* Resolution Summary
* Normalization Summary

EntityResult becomes part of the Extraction Context.

---

# 9.8 Core Interfaces

Every Entity Intelligence implementation should provide implementations for the following contracts.

```text
EntityDiscovery

EntityAssessment

EntityResolver

EntityNormalizer
```

Each interface maps directly to one processing stage.

---

# 9.9 Extension Model

Every new entity implementation should provide:

* Discovery implementation
* Assessment implementation
* Resolution implementation
* Normalization implementation

while continuing to use the shared framework models.

No framework model should require modification when introducing a new entity.

---

# 9.10 Design Principles

The Entity Models follow these principles.

* Generic
* Immutable
* Reusable
* Explainable
* Extensible
* Entity Independent

---

# 9.11 Chapter Summary

The Entity Intelligence Framework is built upon a small set of generic models and interfaces.

These models provide a consistent contract across all entity implementations while allowing each entity to supply its own processing logic without changing the underlying framework.
