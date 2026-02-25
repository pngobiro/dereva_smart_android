# Implementation Plan: B2C Learner App

## Overview

This implementation plan breaks down the B2C Learner App into incremental coding tasks. The app is a Flutter-based mobile application with offline-first architecture, AI tutoring via Gemini 3.1 Pro, and integration with M-Pesa for payments. Each task builds on previous work, with property-based tests placed close to implementation to catch errors early.

## Tasks

- [x] 1. Project setup and core infrastructure
  - Initialize Flutter project with required dependencies (flutter_bloc, sqflite, dio, shared_preferences)
  - Set up project structure with feature-based organization
  - Configure Firebase/Supabase backend connection
  - Create dependency injection setup
  - Set up testing framework (flutter_test, mockito, test_check for property-based testing)
  - _Requirements: All_

- [x] 2. Implement local database layer
  - [x] 2.1 Create SQLite database schema
    - Define all tables (users, modules, lessons, media_assets, questions, test_results, study_sessions, achievements, ai_cache, sync_metadata)
    - Implement database migration system
    - Create database helper class for initialization
    - _Requirements: 6.1, 6.2, 6.3_
  
  - [x] 2.2 Implement repository pattern base classes
    - Create abstract Repository interface
    - Implement base repository with common CRUD operations
    - Add offline/online data source switching logic
    - _Requirements: 6.1, 6.3_

- [x] 3. Implement authentication module
  - [x] 3.1 Create authentication service and models
    - Implement User, AuthResult, AuthState models
    - Create AuthenticationService with phone/password registration
    - Implement SMS verification code sending and validation
    - Add login and logout functionality
    - Implement password reset flow
    - _Requirements: 11.1, 11.2, 11.3_

  
  - [ ]* 3.2 Write property test for user registration
    - **Property 38: User Registration with Valid Credentials**
    - **Validates: Requirements 11.1, 11.2**
  
  - [ ]* 3.3 Write property test for password reset
    - **Property 39: Password Reset Flow Completion**
    - **Validates: Requirements 11.3**
  
  - [x] 3.4 Implement authentication UI screens
    - Create registration screen with phone number and password inputs
    - Create login screen
    - Create SMS verification screen
    - Create password reset screen
    - Add form validation and error display
    - _Requirements: 11.1, 11.2, 11.3_
  
  - [x] 3.5 Implement secure password storage
    - Configure bcrypt hashing with minimum 12 rounds
    - Implement TLS 1.3 for all network requests
    - _Requirements: 11.4, 11.5_

- [x] 4. Implement content management module
  - [x] 4.1 Create content models and repository
    - Implement Module, Lesson, MediaAsset, CurriculumTopic models
    - Create ContentRepository with local and remote data sources
    - Implement module download functionality with progress tracking
    - Add offline availability checking
    - _Requirements: 4.1, 4.2, 4.6, 4.7, 6.1_
  
  - [ ]* 4.2 Write property test for module download
    - **Property 17: Module Download Enables Offline Access**
    - **Validates: Requirements 6.1**
  
  - [ ]* 4.3 Write property test for license category modules
    - **Property 3: License Category Determines Module Set**
    - **Validates: Requirements 2.5**
  
  - [ ]* 4.4 Write property test for multimedia content
    - **Property 8: All Topics Have Multimedia Content**
    - **Validates: Requirements 4.2, 4.7**
  
  - [x] 4.5 Implement content sync service
    - Create background sync service for content updates
    - Implement conflict resolution (server state wins for content)
    - Add sync metadata tracking
    - _Requirements: 6.3_
  
  - [ ]* 4.6 Write property test for progress sync
    - **Property 19: Progress Sync Preserves All Changes**
    - **Validates: Requirements 6.3**
  
  - [x] 4.7 Create content UI components
    - Build module list screen with offline indicators
    - Create lesson viewer with text, images, and video support
    - Implement video player with low-bandwidth optimization
    - Add download manager UI with progress indicators
    - _Requirements: 4.2, 4.7, 6.7, 10.5_
  
  - [ ]* 4.8 Write property test for offline content indicators
    - **Property 21: Offline Content Clearly Indicated**
    - **Validates: Requirements 6.7**

- [x] 5. Checkpoint - Ensure authentication and content management work
  - Ensure all tests pass, ask the user if questions arise.

- [-] 6. Implement mock test engine
  - [x] 6.1 Create question bank and test models
    - Implement Question, MockTest, TestResult, PerformanceAnalytics models
    - Create question bank repository with local storage
    - Implement question difficulty classification
    - _Requirements: 5.1, 5.8_
  
  - [x] 6.2 Implement test generation algorithm
    - Create algorithm to generate 50-question tests
    - Ensure proportional distribution across curriculum topics
    - Include category-specific questions based on license type
    - Randomize question and option order
    - Prevent duplicate questions within same test
    - _Requirements: 5.1, 5.8_
  
  - [ ]* 6.3 Write property test for test generation
    - **Property 10: Mock Tests Have Exactly 50 Questions**
    - **Validates: Requirements 5.1, 5.8**
  
  - [ ] 6.4 Implement test evaluation and scoring
    - Create test submission and answer validation logic
    - Implement 80% pass mark enforcement (40/50 questions)
    - Calculate topic-based scores for analytics
    - _Requirements: 5.2, 5.4_
  
  - [ ]* 6.5 Write property test for pass mark enforcement
    - **Property 11: Pass Mark Enforcement at 80%**
    - **Validates: Requirements 5.2**
  
  - [ ]* 6.6 Write property test for test explanations
    - **Property 13: All Test Questions Have Explanations**
    - **Validates: Requirements 5.4**
  
  - [ ] 6.7 Implement test timing and offline support
    - Add timer functionality matching NTSA test duration
    - Implement auto-submit when time expires
    - Enable offline test taking
    - _Requirements: 5.3, 6.2_
  
  - [ ]* 6.8 Write property test for test time limits
    - **Property 12: Test Time Limits Applied Consistently**
    - **Validates: Requirements 5.3**
  
  - [ ]* 6.9 Write property test for offline mock tests
    - **Property 18: Offline Mock Tests Function Identically**
    - **Validates: Requirements 6.2**
  
  - [ ] 6.10 Implement performance analytics
    - Create analytics calculation for weak areas identification
    - Implement trend analysis over time
    - Calculate average scores
    - _Requirements: 5.5, 12.2, 12.3_
  
  - [ ]* 6.11 Write property test for weak area identification
    - **Property 14: Performance Analytics Identify Weak Areas**
    - **Validates: Requirements 5.5, 12.2**
  
  - [ ]* 6.12 Write property test for average score calculation
    - **Property 42: Average Score Calculation**
    - **Validates: Requirements 12.3**
  
  - [ ] 6.13 Create mock test UI screens
    - Build test start screen with instructions
    - Create test question screen with timer
    - Implement answer selection UI
    - Create test results screen with score and explanations
    - Add performance analytics dashboard
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [ ] 7. Implement progress tracking module
  - [ ] 7.1 Create progress tracking service
    - Implement ProgressTracker with study session recording
    - Add completion percentage calculation
    - Implement streak tracking logic
    - Create achievement badge system
    - _Requirements: 7.1, 7.2, 7.3, 7.4_
  
  - [ ]* 7.2 Write property test for study time accumulation
    - **Property 22: Study Time Accumulation**
    - **Validates: Requirements 7.1, 12.4**
  
  - [ ]* 7.3 Write property test for completion percentage
    - **Property 23: Completion Percentage Calculation**
    - **Validates: Requirements 7.2**
  
  - [ ]* 7.4 Write property test for streak calculation
    - **Property 24: Streak Calculation Accuracy**
    - **Validates: Requirements 7.3**
  
  - [ ]* 7.5 Write property test for badge awards
    - **Property 25: Milestone Badge Awards**
    - **Validates: Requirements 7.4**
  
  - [ ] 7.6 Implement badge awarding logic
    - Create logic for "Ready for NTSA" badge (3 consecutive passes at 80%+)
    - Implement Certificate of Competence generation
    - _Requirements: 5.6, 5.7_
  
  - [ ]* 7.7 Write property test for NTSA badge
    - **Property 15: Badge Award After Three Consecutive Passes**
    - **Validates: Requirements 5.6**
  
  - [ ]* 7.8 Write property test for certificate generation
    - **Property 16: Certificate Generation for Consistent Passers**
    - **Validates: Requirements 5.7**
  
  - [ ] 7.9 Create progress tracking UI
    - Build progress dashboard with completion percentages
    - Create achievement badges display
    - Implement streak counter UI
    - Add visual progress indicators for modules
    - Create performance trend charts
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.7, 12.1_
  
  - [ ]* 7.10 Write property test for progress indicators
    - **Property 28: Progress Indicators for All Modules**
    - **Validates: Requirements 7.7**
  
  - [ ]* 7.11 Write property test for performance trends
    - **Property 41: Performance Trend Visualization**
    - **Validates: Requirements 12.1**

- [ ] 8. Checkpoint - Ensure mock tests and progress tracking work
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 9. Implement AI tutor module
  - [ ] 9.1 Create AI tutor service and models
    - Implement TutorResponse, StudyRecommendation models
    - Create AITutorService with Gemini 3.1 Pro API integration
    - Implement conversation context management (last 10 exchanges)
    - Add language detection logic
    - _Requirements: 3.1, 3.2_
  
  - [ ] 9.2 Implement response caching for offline access
    - Create AI response cache in SQLite
    - Implement common question identification and caching
    - Add cache lookup for offline queries
    - _Requirements: 6.5, 6.6_
  
  - [ ]* 9.3 Write property test for language consistency
    - **Property 5: AI Tutor Language Consistency**
    - **Validates: Requirements 3.2, 9.5**
  
  - [ ]* 9.4 Write property test for cached responses
    - **Property 20: Cached Questions Accessible Offline**
    - **Validates: Requirements 6.5**
  
  - [ ] 9.5 Implement personalized recommendations
    - Create recommendation engine based on weak areas
    - Integrate with test performance analytics
    - _Requirements: 3.7_
  
  - [ ]* 9.6 Write property test for personalized recommendations
    - **Property 7: Personalized Recommendations Based on Weak Areas**
    - **Validates: Requirements 3.7**
  
  - [ ]* 9.7 Write property test for road sign visual references
    - **Property 6: Road Sign Questions Include Visual References**
    - **Validates: Requirements 3.4**
  
  - [ ] 9.8 Create AI tutor UI
    - Build chat interface with message history
    - Implement language toggle (English/Swahili)
    - Add visual reference display for road signs
    - Create study recommendations panel
    - Add offline indicator and cached response badge
    - _Requirements: 3.1, 3.2, 3.4, 3.7, 6.5_

- [ ] 10. Implement school sync module
  - [ ] 10.1 Create school sync service
    - Implement DrivingSchool, ModuleSchedule, ProgressReport models
    - Create SchoolSyncService with school code validation
    - Implement module unlock schedule synchronization
    - Add progress sharing with school
    - _Requirements: 2.1, 2.2, 2.6_
  
  - [ ]* 10.2 Write property test for school linking
    - **Property 1: School Linking Establishes Bidirectional Relationship**
    - **Validates: Requirements 2.1**
  
  - [ ]* 10.3 Write property test for module unlocking
    - **Property 2: Module Unlocking Respects Schedule**
    - **Validates: Requirements 2.2**
  
  - [ ]* 10.4 Write property test for progress sharing
    - **Property 4: Progress Sharing Preserves Data Integrity**
    - **Validates: Requirements 2.6**
  
  - [ ] 10.5 Implement leaderboard functionality
    - Create leaderboard ranking calculation
    - Add opt-in/opt-out preference
    - Implement anonymized school average comparison
    - _Requirements: 7.5, 12.6_
  
  - [ ]* 10.6 Write property test for leaderboard ranking
    - **Property 26: Leaderboard Ranking Accuracy**
    - **Validates: Requirements 7.5**
  
  - [ ]* 10.7 Write property test for school average comparison
    - **Property 44: School Average Comparison**
    - **Validates: Requirements 12.6**
  
  - [ ] 10.8 Create school sync UI
    - Build school code entry screen
    - Create school profile display
    - Add module unlock schedule viewer
    - Implement leaderboard UI (optional)
    - _Requirements: 2.1, 2.2, 7.5_

- [ ] 11. Implement payment module
  - [ ] 11.1 Create payment service and models
    - Implement PaymentRequest, PaymentResult, SubscriptionStatus models
    - Create PaymentService with M-Pesa Daraja API integration
    - Implement STK push flow
    - Add payment status polling
    - _Requirements: 8.2, 8.3, 8.4_
  
  - [ ]* 11.2 Write property test for payment processing
    - **Property 29: Payment Processing and Feature Unlocking**
    - **Validates: Requirements 8.2, 8.3**
  
  - [ ] 11.3 Implement promo code and referral system
    - Create promo code validation and discount application
    - Implement referral code tracking and discount processing
    - Add school commission calculation (20% revenue share)
    - _Requirements: 8.5, 8.6, 8.7_
  
  - [ ]* 11.4 Write property test for school commission
    - **Property 30: School Commission Calculation**
    - **Validates: Requirements 8.5**
  
  - [ ]* 11.5 Write property test for promo codes
    - **Property 31: Promo Code Discount Application**
    - **Validates: Requirements 8.6**
  
  - [ ]* 11.6 Write property test for referral discounts
    - **Property 32: Referral Discount Processing**
    - **Validates: Requirements 8.7**
  
  - [ ] 11.7 Implement payment error handling
    - Add error message parsing from M-Pesa responses
    - Implement retry logic for failed payments
    - Create payment history tracking
    - _Requirements: 8.8_
  
  - [ ]* 11.8 Write property test for payment failures
    - **Property 33: Payment Failure Error Handling**
    - **Validates: Requirements 8.8**
  
  - [ ] 11.9 Create payment UI screens
    - Build subscription selection screen (free, one-time, monthly)
    - Create M-Pesa payment screen with phone number input
    - Implement payment status screen with loading indicator
    - Add promo code and referral code entry
    - Create payment history screen
    - _Requirements: 8.1, 8.2, 8.3, 8.6, 8.7_

- [ ] 12. Checkpoint - Ensure AI tutor, school sync, and payments work
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 13. Implement 3D Model Town module
  - [ ] 13.1 Create Model Town models and service
    - Implement TrafficScenario, TrafficElement, ScenarioResult models
    - Create ModelTownService with scenario loading
    - Implement scenario evaluation logic
    - _Requirements: 4.4, 4.5_
  
  - [ ]* 13.2 Write property test for Model Town feedback
    - **Property 9: Model Town Interactions Provide Feedback**
    - **Validates: Requirements 4.5**
  
  - [ ] 13.3 Create 3D Model Town UI
    - Build 3D rendering engine for traffic scenarios
    - Implement drag-and-drop interaction
    - Add real-time feedback display
    - Create scenario completion tracking
    - _Requirements: 4.4, 4.5_

- [ ] 14. Implement notification module
  - [ ] 14.1 Create notification service
    - Implement NotificationService with local notification scheduling
    - Add study reminder scheduling based on user preferences
    - Implement achievement and module unlock notifications
    - _Requirements: 7.6_
  
  - [ ]* 14.2 Write property test for notification scheduling
    - **Property 27: Notification Scheduling Respects Preferences**
    - **Validates: Requirements 7.6**
  
  - [ ] 14.3 Create notification settings UI
    - Build notification preferences screen
    - Add time and day selection for study reminders
    - Implement notification toggle switches
    - _Requirements: 7.6_

- [ ] 15. Implement multi-language support
  - [ ] 15.1 Create localization infrastructure
    - Set up Flutter localization with English and Swahili
    - Create translation files for all UI strings
    - Implement language selection persistence
    - _Requirements: 9.1, 9.2_
  
  - [ ]* 15.2 Write property test for UI language completeness
    - **Property 34: UI Language Completeness**
    - **Validates: Requirements 9.1**
  
  - [ ]* 15.3 Write property test for language selection
    - **Property 35: Language Selection Affects All Content**
    - **Validates: Requirements 9.2**
  
  - [ ] 15.4 Add bilingual content support
    - Ensure all theory content has English and Swahili versions
    - Implement language-aware test question generation
    - _Requirements: 9.3, 9.4_
  
  - [ ]* 15.5 Write property test for bilingual content
    - **Property 36: Theory Content Bilingual Availability**
    - **Validates: Requirements 9.3**
  
  - [ ]* 15.6 Write property test for test language
    - **Property 37: Test Questions in Selected Language**
    - **Validates: Requirements 9.4**

- [ ] 16. Implement eCitizen integration and PDL support
  - [ ] 16.1 Create eCitizen deep linking
    - Implement deep link to eCitizen PDL application portal
    - Create PDL application guide content
    - Add required documents checklist
    - _Requirements: 1.1, 1.2, 1.5_
  
  - [ ] 16.2 Create PDL tracking UI
    - Build PDL application status tracking interface
    - Add reference number entry screen
    - Implement status display (mock for now, as eCitizen has no API)
    - _Requirements: 1.3, 1.4_

- [ ] 17. Implement user profile management
  - [ ] 17.1 Create profile service
    - Implement profile update functionality
    - Add license category selection
    - Implement account deletion with data removal
    - _Requirements: 11.6, 11.7_
  
  - [ ]* 17.2 Write property test for profile updates
    - **Property 40: Profile Update Persistence**
    - **Validates: Requirements 11.6**
  
  - [ ] 17.3 Create profile UI screens
    - Build profile view and edit screen
    - Add license category selector
    - Implement account deletion confirmation flow
    - _Requirements: 11.6, 11.7_

- [ ] 18. Implement analytics and recommendations
  - [ ] 18.1 Create analytics service
    - Implement comprehensive performance analytics
    - Add actionable study recommendations generation
    - _Requirements: 12.5_
  
  - [ ]* 18.2 Write property test for study recommendations
    - **Property 43: Actionable Study Recommendations**
    - **Validates: Requirements 12.5**
  
  - [ ] 18.2 Create analytics UI
    - Build comprehensive analytics dashboard
    - Add weak areas visualization
    - Implement study recommendations panel
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6_

- [ ] 19. Optimize for low-end devices
  - [ ] 19.1 Implement performance optimizations
    - Add image caching and compression
    - Implement lazy loading for lists
    - Optimize database queries with indexes
    - Add memory management for video playback
    - _Requirements: 10.1, 10.2_
  
  - [ ] 19.2 Implement low-data mode
    - Create data usage tracking
    - Add low-bandwidth video quality selection
    - Implement background data limiting (10MB/day)
    - _Requirements: 6.4, 10.6_
  
  - [ ] 19.3 Test on low-end devices
    - Verify app runs on devices with 2GB RAM
    - Test on Android 8.0 and iOS 12.0
    - Measure and optimize launch time (target < 5 seconds)
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6_

- [ ] 20. Integration and final wiring
  - [ ] 20.1 Wire all modules together
    - Connect authentication with all protected features
    - Link content management with progress tracking
    - Integrate mock tests with analytics and badges
    - Connect school sync with content unlocking
    - Wire payments with feature access control
    - _Requirements: All_
  
  - [ ]* 20.2 Write integration tests
    - Test complete user registration and onboarding flow
    - Test end-to-end mock test taking and results
    - Test offline mode switching and sync
    - Test payment flow and premium feature unlocking
    - _Requirements: All_
  
  - [ ] 20.3 Implement app navigation and routing
    - Create main navigation structure
    - Implement deep linking for notifications
    - Add authentication guards for protected routes
    - _Requirements: All_

- [ ] 21. Final checkpoint - Comprehensive testing
  - Ensure all tests pass, ask the user if questions arise.
  - Verify all 44 correctness properties are tested
  - Run full test suite (unit, property, widget, integration)
  - Test on both Android and iOS devices
  - Verify offline mode works completely
  - Test low-data mode and low-end device performance

## Notes

- Tasks marked with `*` are optional property-based tests that can be skipped for faster MVP
- Each property test should run minimum 100 iterations
- Property tests should include comment tags: `// Feature: b2c-learner-app, Property {N}: {property_text}`
- Checkpoints ensure incremental validation and provide opportunities for user feedback
- All tasks reference specific requirements for traceability
- The implementation follows offline-first architecture throughout
- Security requirements (TLS 1.3, bcrypt) must be verified during implementation
