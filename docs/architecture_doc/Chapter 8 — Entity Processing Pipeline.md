# Chapter 8 — Entity Processing Pipeline

## 8.1 Purpose

The Entity Processing Pipeline defines the standard lifecycle followed by every Entity Intelligence implementation.

Regardless of the entity type, processing always follows the same five stages.

```text
Discovery
      ↓
Assessment
      ↓
Resolution
      ↓
Normalization
      ↓
Result
```

This lifecycle is mandatory and forms the foundation of the Entity Intelligence Framework.

---

# 8.2 Stage 1 — Discovery

## Purpose

Locate every possible text window that may represent the target entity.

### Responsibilities

* Identify candidate windows
* Apply multiple discovery strategies
* Preserve original text
* Preserve position within SMS
* Record discovery strategy

### Output

Entity Windows

Discovery never evaluates correctness.

---

# 8.3 Stage 2 — Assessment

## Purpose

Evaluate every discovered window using contextual and structural evidence.

### Responsibilities

* Collect evidence
* Calculate confidence
* Calculate score
* Validate contextual compatibility
* Preserve assessment details

### Output

Window Assessments

Assessment never selects a winner.

---

# 8.4 Stage 3 — Resolution

## Purpose

Resolve multiple assessments into a single entity.

### Responsibilities

* Compare assessments
* Resolve conflicts
* Select highest-confidence entity
* Handle ambiguity
* Produce explainable decision

### Output

Resolved Entity

If no assessment satisfies the confidence threshold, UNKNOWN should be returned.

---

# 8.5 Stage 4 — Normalization

## Purpose

Convert the resolved entity into its canonical representation.

### Responsibilities

* Alias resolution
* Registry lookup
* Canonical naming
* Standardization

### Output

Normalized Entity

Normalization never changes business meaning.

---

# 8.6 Stage 5 — Result

## Purpose

Produce the immutable Entity Result consumed by downstream engines.

### Entity Result

* Original Window
* Canonical Entity
* Confidence
* Score
* Discovery Strategy
* Assessment Summary
* Resolution Summary

Entity Results become part of the Extraction Context.

---

# 8.7 Processing Rules

Every Entity Processing Pipeline must follow these rules.

* Execute stages sequentially.
* Never skip stages.
* Never modify previous stage output.
* Every stage produces immutable output.
* Every decision must be explainable.
* Prefer UNKNOWN over incorrect results.

---

# 8.8 Extension Points

The framework supports extension by adding:

* Discovery Strategies
* Assessment Rules
* Resolution Policies
* Normalization Rules

No architectural changes should be required to support new entity types.

---

# 8.9 Chapter Summary

The Entity Processing Pipeline provides a reusable, deterministic and explainable lifecycle for every Entity Intelligence implementation.

By separating Discovery, Assessment, Resolution, Normalization and Result generation, the framework remains modular, extensible and easy to test while supporting multiple financial entities through a common architecture.
