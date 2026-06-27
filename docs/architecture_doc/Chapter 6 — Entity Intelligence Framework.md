# Chapter 6 — Entity Intelligence Framework

## 6.1 Purpose

The Entity Intelligence Framework is responsible for discovering, evaluating, resolving and normalizing real-world entities participating in a financial transaction.

Unlike Classification Engines, Entity Intelligence Engines operate on an unbounded search space.

Examples of entities include:

* Merchant
* Payee
* Employer
* Account
* Instrument
* Reference Number

The framework provides a reusable architecture that can be implemented for any financial entity without requiring architectural redesign.

Merchant Intelligence is the first implementation of this framework.

---

# 6.2 Design Philosophy

Entity Discovery is fundamentally different from Classification.

Classification chooses one value from a finite set.

Example:

```text
Direction

Debit
Credit
Unknown
```

Entity Discovery operates on an unlimited domain.

Example:

```
Amazon
Swiggy
Netflix
Rahul Kumar
Infosys
BESCOM
IRCTC
```

Therefore Entity Discovery requires a different architectural approach.

Instead of selecting from predefined labels, the framework progressively discovers and evaluates candidate entities until the highest-confidence entity can be resolved.

---

# 6.3 Entity Intelligence Lifecycle

Every Entity Intelligence Engine must implement the same lifecycle.

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

This lifecycle is mandatory for every Entity Intelligence implementation.

No stages may be skipped.

No additional stages should be introduced without creating a new architecture version.

---

# 6.4 Stage 1 — Discovery

Purpose

Discover every possible text region that may represent a real-world entity.

Output

Entity Windows.

Responsibilities

* Locate potential entity windows.
* Apply multiple discovery strategies.
* Preserve original text.
* Preserve positional information.
* Record discovery strategy.

Discovery does **not** decide correctness.

Its responsibility ends after discovering possible entity windows.

---

# 6.5 Stage 2 — Assessment

Purpose

Evaluate every discovered window.

Assessment enriches each window with supporting evidence.

Examples of evidence include:

* Structural grammar
* Transaction context
* Financial event compatibility
* Direction compatibility
* Registry match
* Pattern confidence
* Positional confidence

Output

Window Assessments.

Assessment never removes windows.

It only measures confidence.

---

# 6.6 Stage 3 — Resolution

Purpose

Resolve competing assessments into a single entity.

Responsibilities

* Compare all assessments.
* Resolve conflicts.
* Select the highest-confidence entity.
* Handle ambiguity.
* Produce explainable decisions.

Resolution produces exactly one resolved entity in Version 1.

If no assessment satisfies the minimum confidence threshold, the engine should return UNKNOWN.

---

# 6.7 Stage 4 — Normalization

Purpose

Convert resolved entities into canonical representations.

Examples

```
AMZN
Amazon Pay
AMZN MKTPLACE
```

↓

```
Amazon
```

Responsibilities

* Alias resolution
* Canonical naming
* Registry lookup
* Future learning support

Normalization never changes the semantic meaning of the resolved entity.

It only standardizes representation.

---

# 6.8 Stage 5 — Result

Purpose

Produce the final immutable entity result.

Every Entity Result should contain:

* Original Window
* Canonical Entity
* Confidence
* Evidence Summary
* Discovery Strategy
* Resolution Strategy

Entity Results become part of the Extraction Context consumed by downstream Business Intelligence Engines.

---

# 6.9 Entity Intelligence Law

Every Entity Intelligence Engine within SMART Expense Tracker shall implement exactly five stages.

```
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

This law applies to every entity implementation, including:

* Merchant Intelligence
* Payee Intelligence
* Employer Intelligence
* Account Intelligence
* Instrument Intelligence
* Reference Intelligence

This lifecycle guarantees consistency across the SMS Engine.

---

# 6.10 Framework Characteristics

The Entity Intelligence Framework is designed to be:

* Generic
* Extensible
* Explainable
* Evidence Driven
* Immutable
* Context Aware
* Bank Independent
* Strategy Based

Future entity implementations should require only new discovery strategies, assessment logic and normalization rules.

The framework itself should remain unchanged.

---

# 6.11 Framework Benefits

Using a shared Entity Intelligence Framework provides several advantages.

### Consistency

Every entity follows the same processing lifecycle.

### Reusability

Architectural components can be reused across multiple entity implementations.

### Testability

Each lifecycle stage can be tested independently.

### Extensibility

New entity types can be introduced without modifying the framework.

### Explainability

Every entity result can be traced back to its supporting evidence.

### Maintainability

Business logic remains isolated within clearly defined lifecycle stages.

---

# 6.12 Chapter Summary

The Entity Intelligence Framework defines a reusable architecture for discovering real-world entities from financial messages.

Unlike Classification Engines, Entity Intelligence Engines operate on unlimited search spaces.

Every implementation follows the same five-stage lifecycle:

```
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

Merchant Intelligence is the first implementation of this framework.

Future entity engines will reuse the same architecture while providing entity-specific discovery, assessment and normalization logic.
