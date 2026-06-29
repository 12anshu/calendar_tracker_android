# SMART Expense Tracker - SMS Engine Implementation Tracker (Architecture v1)

Legend

* ✅ Completed
* 🚧 In Progress
* ⬜ Pending


sms/
│
├── qualification/
│   ├── QualificationEngine                      ⬜
│   ├── SenderValidator                          ⬜
│   ├── MessageQualifier                         ⬜
│   └── QualificationResult                      ⬜
│
├── classification/
│   │
│   ├── financial/
│   │   ├── FinancialClassifier                  🚧
│   │   ├── FinancialDetector                    ⬜
│   │   └── FinancialResult                      ✅
│   │
│   ├── messagetype/
│   │   ├── MessageTypeClassifier                🚧
│   │   ├── MessageTypeDetector                  ⬜
│   │   └── MessageTypeResult                    ✅
│   │
│   ├── direction/
│   │   ├── DirectionClassifier                  🚧
│   │   ├── DirectionDetector                    ⬜
│   │   └── DirectionResult                      ✅
│   │
│   ├── event/
│   │   ├── FinancialEventClassifier             🚧
│   │   ├── FinancialEventDetector               ⬜
│   │   └── FinancialEventResult                 ✅
│   │
│   └── mode/
│       ├── TransactionModeClassifier            🚧
│       ├── TransactionModeDetector              ⬜
│       └── TransactionModeResult                ✅
│
├── entity/
│   │
│   ├── framework/
│   │   ├── contract/
│   │   │   ├── EntityDiscovery                  ✅
│   │   │   ├── EntityAssessor                   ✅
│   │   │   ├── EntityResolver                   ✅
│   │   │   └── EntityNormalizer                 ✅
│   │   │
│   │   ├── enum/
│   │   │   └── DiscoveryMethod                  ✅
│   │   │
│   │   ├── model/
│   │   │   ├── EntityWindow                     ✅
│   │   │   ├── EntityAssessment                 ✅
│   │   │   ├── ResolvedEntity                   ✅
│   │   │   ├── NormalizedEntity                 ✅
│   │   │   └── EntityResult                     ✅
│   │   │
│   │   └── pipeline/
│   │       └── EntityProcessingPipeline         ✅
│   │
│   └── merchant/
│       ├── discovery/
│       │   ├── MerchantDiscovery                ✅
│       │   ├── MerchantDiscoveryStrategy        ✅
│       │   ├── AfterPrepositionDiscovery        ✅
│       │   ├── LinePatternDiscovery             ✅
│       │   └── KnownMerchantDiscovery           ✅
│       │
│       ├── assessment/
│       │   ├── MerchantAssessor                 🚧
│       │   ├── MerchantAssessmentRule           ✅
│       │   ├── MerchantAssessmentResult         ✅
│       │   ├── MerchantContextAssessmentRule    ⬜
│       │   ├── MerchantStructureAssessmentRule  ⬜
│       │   ├── MerchantRegistryAssessmentRule   ⬜
│       │   └── MerchantScoreEvaluator           ⬜
│       │
│       ├── resolution/
│       │   ├── MerchantResolver                 ⬜
│       │   └── ResolutionPolicy                 ⬜
│       │
│       ├── normalization/
│       │   ├── MerchantNormalizer               ⬜
│       │   ├── MerchantAliasRegistry            ⬜
│       │   └── MerchantCanonicalizer            ⬜
│       │
│       ├── patterns/
│       │   ├── MerchantDiscoveryPatterns        ✅
│       │   ├── MerchantVocabulary               ✅
│       │   └── MerchantRegexPatterns            ✅
│       │
│       ├── registry/
│       │   └── MerchantRegistry                 ✅
│       │
│       └── pipeline/
│           └── MerchantProcessingPipeline       ✅
│
├── business/
│   ├── category/                                ⬜
│   ├── duplicate/                               ⬜
│   ├── recurring/                               ⬜
│   ├── insights/                                ⬜
│   └── budget/                                  ⬜
│
├── context/
│   ├── QualificationContext                     ⬜
│   ├── ExtractionContext                        ✅
│   └── BusinessContext                          ⬜
│
├── enums/
│   ├── MessageType                              ✅
│   ├── TransactionDirection                     ✅
│   ├── TransactionMode                          ✅
│   └── FinancialEventType                       ✅
│
├── registry/
│   ├── SenderRegistry                           ⬜
│   ├── BankRegistry                             ⬜
│   └── InstitutionRegistry                      ⬜
│
├── common/
│   ├── constants/                               ⬜
│   ├── patterns/                                ⬜
│   ├── regex/                                   ⬜
│   ├── scoring/                                 ⬜
│   └── utils/                                   ⬜
│
└── dashboard/
    ├── SMSAnalysisDashboard                     ⬜
    ├── PipelineDebugger                         ⬜
    └── ExportManager                            ⬜

