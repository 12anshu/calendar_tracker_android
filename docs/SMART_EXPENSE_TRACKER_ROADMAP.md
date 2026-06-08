# SMART EXPENSE TRACKER

## Product Roadmap & Execution Plan

**Version:** 1.0
**Last Updated:** August 2026
**Owner:** Ashu Singh

---

# 1. PRODUCT VISION

## Mission

Help users understand where their money goes without requiring manual expense entry.

The application should automatically:

* Read SMS transactions
* Extract financial events
* Categorize expenses
* Generate insights
* Show spending patterns
* Help users save money

while keeping all data private and stored locally on the device.

---

# 2. PRODUCT GOALS

## Primary Goal

Build India's most accurate privacy-focused SMS-based expense tracking application.

## Secondary Goals

* Achieve 95%+ transaction extraction accuracy
* Achieve 95%+ merchant extraction accuracy
* Achieve 90%+ category classification accuracy
* Become a trusted alternative to manual expense trackers
* Launch successfully on Google Play Store
* Reach first 1,000 users organically
* Convert users to premium subscriptions

---

# 3. CORE DIFFERENTIATORS

## Automated SMS Intelligence

Automatically detects:

* UPI Payments
* Card Payments
* ATM Withdrawals
* Salary Credits
* Refunds
* Transfers
* Wallet Transactions
* Bank Charges
* EMI Payments

## Calendar-Based Financial View

Users can:

* View daily spending
* View daily income
* Identify salary days
* Identify high-spending days
* Analyze monthly spending behavior

## Privacy First

* No bank login
* No account linking
* No internet required
* No cloud dependency
* Local-only data storage

---

# 4. DEVELOPMENT ROADMAP

---

# PHASE 1 - SMS INTELLIGENCE ENGINE

## Sender Validation Engine

### Status

✅ Completed

### Features

* Financial sender detection
* Sender confidence scoring
* Noise filtering
* Bank identification
* Sender normalization

### Goal
* Filter out personal messages or chats
* Identify and validate legitimate financial senders (Banks, Wallets, UPI)
* Map sender headers to specific financial institutions for better categorization

### Progress

100%

---

## Message Type Detection

### Status

✅ Completed

### Features

* Debit detection
* Credit detection
* Transfer detection
* Refund detection
* ATM withdrawal detection
* Balance update detection
* Failed Transaction detection

### Progress

100%

---

## Financial Event Detection

### Status

✅ Completed

### Features

* Multi-stage classification
* Confidence scoring
* Event categorization
* Sender validation integration

### Progress

100%

---

## SMS Keyword Registry

### Status

✅ Completed

### Features

* Centralized keyword management
* Phrase matching support
* Reusable architecture

### Progress

100%

---

## SMS Parser

### Status

🚧 In Progress

### Features

* Amount extraction
* Merchant extraction
* Transaction channel extraction
* Transaction type extraction
* Reference number extraction

### Current Metrics

* Merchant Found: 1093
* Merchant Missing: 192
* Coverage: 85%

### Target

* Merchant Accuracy: 95%+
* Transaction Accuracy: 95%+

### Priority

🔥 Critical

---

# PHASE 2 - TRANSACTION ENGINE

## Transaction Normalization

### Status

🚧 In Progress

### Objectives

* Create unified transaction objects
* Normalize merchant names
* Normalize transaction types
* Standardize extracted data

### Priority

🔥 Critical

---

## Duplicate Detection

### Status

⏳ Pending

### Objectives

Prevent duplicate entries caused by:

* Duplicate SMS
* Bank retries
* Notification resends

### Priority

🔥 High

---

## Data Validation Layer

### Status

⏳ Pending

### Objectives

Validate:

* Amount
* Merchant
* Sender
* Date
* Transaction Type

### Priority

🔥 High

---

# PHASE 3 - CATEGORY ENGINE

## Auto Categorization

### Status

⏳ Pending

### Categories

* Food
* Shopping
* Travel
* Entertainment
* Medical
* Education
* Salary
* Investment
* EMI
* Bills
* Recharge
* Fuel

### Priority

🔥 High

---

## Merchant Mapping Engine

### Status

⏳ Pending

### Examples

* Swiggy → Food
* Zomato → Food
* Amazon → Shopping
* Flipkart → Shopping

### Priority

🔥 High

---

## User Learning System

### Status

🔮 Future

### Example

User changes:

Amazon → Electronics

Application remembers future mapping.

---

# PHASE 4 - DASHBOARD & ANALYTICS

## Dashboard

### Status

🚧 Partial

### Features

* Total Expenses
* Total Income
* Savings Summary
* Monthly Trends

### Priority

🔥 High

---

## Expense Analytics

### Status

⏳ Pending

### Features

* Category breakdown
* Spending trends
* Monthly comparison
* Weekly comparison

### Priority

🔥 High

---

## Smart Insights

### Status

⏳ Pending

### Examples

* You spent 20% more this month.
* Food spending increased by ₹2,000.
* Highest spending day was June 15.

### Priority

🔥 High

---

## Spending Predictions

### Status

🔮 Future

### Examples

* Predicted monthly spending
* Forecasted savings

---

# PHASE 5 - CALENDAR VIEW

## Expense Calendar

### Status

⏳ Pending

### Features

* Daily expense view
* Daily income view
* Monthly summary
* Expense heatmap

### Importance

⭐ Flagship Feature

### Priority

🔥 Critical

---

## Bill Reminder Calendar

### Status

🔮 Future

### Features

* Rent reminders
* EMI reminders
* Credit card due reminders

---

# PHASE 6 - BUDGET MODULE

## Budget Planning

### Status

⏳ Pending

### Features

Users can create budgets for:

* Food
* Shopping
* Travel
* Entertainment
* Bills

### Priority

🔥 High

---

## Budget Alerts

### Status

⏳ Pending

### Alerts

* 80% Used
* 90% Used
* Budget Exceeded

### Priority

🔥 High

---

# PHASE 7 - SEARCH & FILTERS

## Smart Search

### Status

⏳ Pending

### Search By

* Merchant
* Amount
* Category
* Date
* Sender

### Priority

🔥 High

---

## Advanced Filters

### Status

⏳ Pending

### Filters

* Debit
* Credit
* UPI
* Card
* ATM
* Wallet

---

# PHASE 8 - REPORTING

## CSV Export

### Status

🚧 Partial

### Features

* Transaction Export
* Category Export

---

## PDF Reports

### Status

⏳ Pending

### Features

* Monthly Reports
* Category Reports
* Expense Summary Reports

---

# PHASE 9 - SECURITY

## App Lock

### Status

⏳ Pending

### Features

* PIN Lock
* Fingerprint Lock
* Face Unlock

### Priority

🔥 High

---

## Database Encryption

### Status

⏳ Pending

### Features

* Encrypted transaction data
* Secure local storage

### Priority

🔥 High

---

# PHASE 10 - PLAY STORE RELEASE

## Legal Documents

### Status

⏳ Pending

### Required

* Privacy Policy
* Terms & Conditions
* Data Usage Policy

### Priority

🔥 Critical

---

## Play Store Assets

### Status

⏳ Pending

### Required

* App Logo
* Screenshots
* Feature Graphics
* Store Listing

### Priority

🔥 Critical

---

## Testing

### Status

⏳ Pending

### Devices

* Samsung
* Xiaomi
* Vivo
* Oppo
* Motorola
* OnePlus

### Priority

🔥 Critical

---

# 5. MONETIZATION ROADMAP

## Stage 1 - Free Launch

### Goal

Acquire users and feedback.

### Duration

3–6 Months

### Features

All core features free.

---

## Stage 2 - Freemium

### Free Features

* SMS Tracking
* Dashboard
* Calendar
* Basic Categories

### Premium Features

* Advanced Insights
* PDF Reports
* Budget Planning
* Unlimited History

### Pricing

₹99/month

OR

₹799/year

---

## Stage 3 - AI Premium

### Features

* AI Expense Coach
* Saving Recommendations
* Financial Health Score
* Spending Predictions

### Pricing

₹149/month

---

# 6. TRIAL STRATEGY

## Recommended Model

### Free Forever

Core Features remain free.

### Premium Trial

14 Days

### Trial Includes

* Advanced Analytics
* Reports
* Budget Planner
* AI Insights

After trial ends:

User returns to free version.

---

# 7. MARKETING ROADMAP

## Pre Launch

### Goal

100 Beta Users

### Sources

* Friends
* Family
* LinkedIn
* Reddit
* Facebook Groups

---

## Launch

### Platforms

* Google Play Store
* LinkedIn
* Reddit
* Twitter/X

---

## ASO Strategy

### Keywords

* Expense Tracker
* Money Manager
* SMS Expense Tracker
* Budget Planner
* UPI Expense Tracker
* Expense Manager

---

## Content Marketing

### Content Ideas

* Monthly savings tips
* Expense management guides
* Budgeting strategies
* Product demo videos

---

## Referral Program

### Future Feature

Invite Friend

Reward:

* 1 Month Premium

---

# 8. FUTURE ROADMAP

## AI Expense Coach

Examples:

* How can I save ₹5,000 this month?
* What category is overspending?

---

## Investment Tracking

* Mutual Funds
* Stocks
* SIPs
* Fixed Deposits

---

## Family Accounts

* Shared Budgets
* Shared Expenses
* Shared Reports

---

## Cloud Backup

* Google Drive
* Dropbox

---

## Cross Platform Sync

* Android
* Web
* iOS

---

# 9. SUCCESS METRICS

## Technical Goals

* Merchant Accuracy > 95%
* Category Accuracy > 90%
* Crash Rate < 1%
* App Startup < 2 Seconds

---

## User Growth Goals

* 100 Users
* 1,000 Users
* 10,000 Users
* 50,000 Users
* 100,000 Users

---

## Revenue Goals

* First Subscription
* ₹10,000 MRR
* ₹50,000 MRR
* ₹1,00,000 MRR

---

# 10. CURRENT SPRINT (ACTIVE)

## Sprint Goal

Merchant Extraction Accuracy 95%+

### Tasks

* Improve Merchant Detection
* Handle Missing Merchant Patterns
* Improve Regex Extraction
* Improve Merchant Normalization
* Increase Coverage From 85% To 95%

### Status

🚧 In Progress

---

# 11. NEXT SPRINT

## Goals

1. Complete Transaction Engine
2. Duplicate Detection
3. Data Validation
4. Category Engine
5. Dashboard Analytics

---

# 12. MVP RELEASE CHECKLIST

## Must Have Before Release

* SMS Detection
* Transaction Extraction
* Merchant Extraction
* Auto Categories
* Dashboard
* Calendar View
* Search
* CSV Export
* Security Lock
* Privacy Policy
* Play Store Listing

### MVP Status

Overall Progress: ~65%

Target Release: Version 1.0

---

# FINAL PRODUCT OBJECTIVE

Build the most accurate, privacy-focused, SMS-powered expense tracking application in India with:

* 95%+ Merchant Accuracy
* 90%+ Category Accuracy
* Beautiful Calendar View
* Intelligent Insights
* Premium Analytics
* Sustainable Subscription Revenue
