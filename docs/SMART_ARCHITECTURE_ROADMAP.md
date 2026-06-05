# SMART Architecture Roadmap

This document outlines the core architecture and design decisions for the Smart Expense Calendar (SMART) project.

---

# Current Agreed Architecture

```text
Raw SMS
    ↓
Sender Validation
    ↓
SMS Normalization
    ↓
Transaction Detection
    ↓
Amount Extraction
    ↓
Direction Detection
    ↓
Financial Event Detection
    ↓
Payment Method Detection
    ↓
Merchant Detection
    ↓
Merchant Normalization
    ↓
Category Detection
    ↓
Rule Engine
    ↓
Transaction Linking
    ↓
Insights Engine
```

---

# Core Design Decisions

### Calendar

```text
Expense Only
```

Do NOT show:
```text
Income
Net Flow
```
on calendar.

---

### Credits

Store them.

Examples:
```text
Salary
Refund
Cashback
Interest
Transfer Received
```

Needed for:
```text
Insights
Refund Matching
Card Settlement
Account Analysis
```

---

### Categories

Categories answer:
```text
Where money went?
```

Examples:
```text
Food
Groceries
Shopping
Travel
Fuel
Rent
Healthcare
```

---

### FinancialEventType

Answers:
```text
What kind of money movement?
```

Examples:
```text
EXPENSE
INCOME
TRANSFER
CARD_PAYMENT
REFUND
SALARY
EMI
INTEREST
```

---

### PaymentMethod

Answers:
```text
How money moved?
```

Examples:
```text
UPI
CREDIT_CARD
DEBIT_CARD
NEFT
IMPS
RTGS
```

---

### Merchant Mapping

```text
Merchant
    ↓
Category
```

User override always wins.

---

### Confidence

Every parsed transaction has:
```text
0 - 100
```

Used later for:
```text
Needs Review
Auto Learning
Rule Suggestions
```

---

### Internal Transfers

Must not become expenses.

Example:
```text
Salary Account
 ↓
Transfer
 ↓
Spending Account
```

Not expense.

---

### Credit Card Payments

Must not become expenses.

Example:
```text
Card Spend
 ↓
Bill Payment
```

Bill payment settles existing spend.
Not new expense.

---

# Current Progress

Completed:
- [x] FinancialEventType
- [x] TransactionDirection
- [x] PaymentMethod
- [x] Parser upgraded
- [x] Confidence scoring
- [x] Persist FinancialEventType
- [x] Persist PaymentMethod
- [x] Persist Confidence
- [x] SMS Normalizer created
- [x] Parser integration started

---

# Next Planned Module

**Sender Validation Engine**

Objective: Filter out personal/promotional SMS that might look like transactions.

Classification types:
- BANK
- CARD
- UPI
- PROMOTIONAL
- PERSONAL
- UNKNOWN
