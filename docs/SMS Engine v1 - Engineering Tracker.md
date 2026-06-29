# SMART Expense Tracker

# SMS Engine v1 - Engineering Tracker

---

# Project Status

**Architecture Version:** 1.0 (Frozen)

**Implementation Status:** In Progress

**Current Phase:** Foundation

**Overall Progress:** 12%

---

# Engineering Rules

Before implementing any feature:

* Follow Architecture v1
* Never redesign architecture during implementation
* One milestone at a time
* Compile after every milestone
* Dashboard validation before marking complete
* Prefer UNKNOWN over incorrect detection
* Every completed bug becomes a regression test

---

# Phase 1 — Foundation

| Task                       | Status |
| -------------------------- | ------ |
| Architecture Documentation | ✅      |
| Package Structure          | ✅      |
| Core Enums                 | ✅      |
| Shared Contexts            | 🚧     |
| Framework Models           | ✅      |
| Framework Contracts        | ✅      |
| Framework Pipeline         | ✅      |

---

# Phase 2 — Qualification Layer

| Task                   | Status |
| ---------------------- | ------ |
| Sender Validation      | ⬜      |
| Message Qualification  | ⬜      |
| Qualification Result   | ✅      |
| Qualification Context  | ✅      |
| Qualification Pipeline | ⬜      |
| Unit Tests             | ⬜      |

---

# Phase 3 — Classification Layer

| Task                         | Status |
| ---------------------------- | ------ |
| Financial Detector           | ⬜      |
| Message Type Detector        | ⬜      |
| Direction Detector           | ⬜      |
| Financial Event Detector     | ⬜      |
| Transaction Mode Detector    | ⬜      |
| Classification Result Models | ✅      |
| Classification Pipeline      | ⬜      |
| Extraction Context           | 🚧     |
| Unit Tests                   | ⬜      |

---

# Phase 4 — Entity Intelligence Framework

## Framework

| Task                  | Status |
| --------------------- | ------ |
| Entity Models         | ✅      |
| Entity Contracts      | ✅      |
| Entity Pipeline       | ✅      |
| Framework Integration | ⬜      |

---

## Merchant Intelligence

### Discovery

| Task                       | Status |
| -------------------------- | ------ |
| Discovery Framework        | ✅      |
| After Preposition Strategy | ✅      |
| Line Pattern Strategy      | ✅      |
| Known Merchant Strategy    | ✅      |
| Registry                   | ✅      |
| Patterns                   | ✅      |
| Vocabulary                 | ✅      |
| Regex                      | ✅      |
| Dashboard Validation       | ⬜      |

---

### Assessment

| Task                 | Status |
| -------------------- | ------ |
| Assessment Framework | 🚧     |
| Context Rule         | ⬜      |
| Structure Rule       | ⬜      |
| Registry Rule        | ⬜      |
| Score Evaluator      | ⬜      |
| Dashboard Validation | ⬜      |

---

### Resolution

| Task                 | Status |
| -------------------- | ------ |
| Resolver             | ⬜      |
| Resolution Policy    | ⬜      |
| Dashboard Validation | ⬜      |

---

### Normalization

| Task                 | Status |
| -------------------- | ------ |
| Normalizer           | ⬜      |
| Alias Registry       | ⬜      |
| Canonicalizer        | ⬜      |
| Dashboard Validation | ⬜      |

---

# Phase 5 — Payee Intelligence

| Task          | Status |
| ------------- | ------ |
| Discovery     | ⬜      |
| Assessment    | ⬜      |
| Resolution    | ⬜      |
| Normalization | ⬜      |

---

# Phase 6 — Account Intelligence

| Task          | Status |
| ------------- | ------ |
| Discovery     | ⬜      |
| Assessment    | ⬜      |
| Resolution    | ⬜      |
| Normalization | ⬜      |

---

# Phase 7 — Instrument Intelligence

| Task          | Status |
| ------------- | ------ |
| Discovery     | ⬜      |
| Assessment    | ⬜      |
| Resolution    | ⬜      |
| Normalization | ⬜      |

---

# Phase 8 — Business Intelligence

| Task                | Status |
| ------------------- | ------ |
| Category Engine     | ⬜      |
| Duplicate Detection | ⬜      |
| Recurring Detection | ⬜      |
| Budget Engine       | ⬜      |
| Insights Engine     | ⬜      |

---

# Phase 9 — Dashboard

| Task                   | Status |
| ---------------------- | ------ |
| SMS Analysis           | ⬜      |
| Pipeline Visualization | ⬜      |
| Statistics             | ⬜      |
| Export                 | ⬜      |
| Regression Dashboard   | ⬜      |

---

# Phase 10 — Release Readiness

| Task               | Status |
| ------------------ | ------ |
| Unit Tests         | ⬜      |
| Integration Tests  | ⬜      |
| Regression Tests   | ⬜      |
| Performance Tests  | ⬜      |
| Beta Release       | ⬜      |
| Play Store Release | ⬜      |

---

# Technical Debt

*None*

---

# Current Sprint

## Sprint Goal

Complete the Qualification and Classification layers so that a fully populated `ExtractionContext` is produced according to the frozen architecture.

---

# Next Milestone

1. Complete Shared Contexts
2. Complete Qualification Pipeline
3. Complete Classification Pipeline
4. Validate ExtractionContext
5. Resume Merchant Assessment

---

# Definition of Done

A task is considered complete only if:

* ✅ Architecture compliant
* ✅ Code compiled
* ✅ Unit tested
* ✅ Dashboard validated
* ✅ Regression checked
* ✅ Engineering review completed
