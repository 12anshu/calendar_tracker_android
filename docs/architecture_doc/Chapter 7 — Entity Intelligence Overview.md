# Chapter 7 — Entity Intelligence Overview

## 7.1 Purpose

The Entity Intelligence Framework provides a generic architecture for discovering and understanding financial entities present within SMS messages.

Rather than building separate architectures for each entity type, all entities reuse the same processing pipeline while providing their own discovery, assessment, resolution and normalization rules.

This approach ensures consistency, maintainability and long-term extensibility.

---

# 7.2 Supported Entities

The SMS Engine is designed to support multiple entity types.

| Entity           | Purpose                        | Status    |
| ---------------- | ------------------------------ | --------- |
| Merchant         | Primary financial counterparty | Version 1 |
| Payee            | Recipient of funds             | Planned   |
| Account          | Financial account involved     | Planned   |
| Instrument       | Payment instrument used        | Planned   |
| Employer         | Income source                  | Planned   |
| Reference Number | Transaction identifier         | Planned   |
| Beneficiary      | Beneficiary account            | Future    |

---

# 7.3 Shared Processing Model

Every entity follows the same processing lifecycle.

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

Only the implementation logic differs.

The processing lifecycle remains identical for every entity.

---

# 7.4 Architectural Independence

Each entity implementation is independent.

Adding a new entity should require only:

* Discovery Rules
* Assessment Rules
* Resolution Logic
* Normalization Rules

The framework itself should never require modification.

---

# 7.5 Current Scope

Version 1 focuses on implementing Merchant Intelligence.

All remaining entities have been considered during architecture design to ensure future implementation without structural changes.

---

# 7.6 Chapter Summary

The Entity Intelligence Framework is designed as a reusable platform capable of supporting multiple financial entities.

Merchant Intelligence serves as the first implementation while the architecture remains flexible enough to support additional entities in future releases without redesign.
