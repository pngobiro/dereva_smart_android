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

  
  - [-] 3.2 Write property test for user registration
    - **Property 38: User Registration with Valid Credentials**
    - **Validates: Requirements 11.1, 11.2**
  
  - [ ] 3.3 Write property test for password reset
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
  
  - [ ] 4.2 Write property test for module download
    - **Property 17: Module Download Enables Offline Access**
    - **Validates: Requirements 6.1**
  
  - [ ] 4.3 Write property test for license category modules
    - **Property 3: License Category Determines Module Set**
    - **Validates: Requirements 2.5**
  
  - [ ] 4.4 Write property test for multimedia content
    - **Property 8: All Topics Have Multimedia Content**
    - **Validates: Requirements 4.2, 4.7**
  
  - [x] 4.5 Implement content sync service
    - Create background sync service for content updates
    - Implement conflict resolution (server state wins for content)
    - Add sync metadata tracking
    - _Requirements: 6.3_
  
  - [ ] 4.6 Write property test for progress sync
    - **Property 19: Progress Sync Preserves All Changes**
    - **Validates: Requirements 6.3**
  
  - [x] 4.7 Create content UI components
    - Build module list screen with offline indicators
    - Create lesson viewer with text, images, and video support
    - Implement video player with low-bandwidth optimization
    - Add download manager UI with progress indicators
    - _Requirements: 4.2, 4.7, 6.7, 10.5_
  
  - [ ] 4.8 Write property test for offline content indicators
    - **Property 21: Offline Content Clearly Indicated**
    - **Validates: Requirements 6.7**

- [x] 5. Checkpoint - Ensure authentication and content management work
  - Ensure all tests pass, ask the user if questions arise.

- [x] 6. Implement mock test engine
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
  
  - [x] 6.3 Write property test for test generation
    - **Property 10: Mock Tests Have Exactly 50 Questions**
    - **Validates: Requirements 5.1, 5.8**
  
  - [x] 6.4 Implement test evaluation and scoring
    - Create test submission and answer validation logic
    - Implement 80% pass mark enforcement (40/50 questions)
    - Calculate topic-based scores for analytics
    - _Requirements: 5.2, 5.4_
  
  - [x] 6.5 Write property test for pass mark enforcement
    - **Property 11: Pass Mark Enforcement at 80%**
    - **Validates: Requirements 5.2**
  
  - [x] 6.6 Write property test for test explanations
    - **Property 13: All Test Questions Have Explanations**
    - **Validates: Requirements 5.4**
  
  - [x] 6.7 Implement test timing and offline support
    - Add timer functionality matching NTSA test duration
    - Implement auto-submit when time expires
    - Enable offline test taking
    - _Requirements: 5.3, 6.2_
  
  - [x] 6.8 Write property test for test time limits
    - **Property 12: Test Time Limits Applied Consistently**
    - **Validates: Requirements 5.3**
  
  - [x] 6.9 Write property test for offline mock tests
    - **Property 18: Offline Mock Tests Function Identically**
    - **Validates: Requirements 6.2**
  
  - [x] 6.10 Implement performance analytics
    - Create analytics calculation for weak areas identification
    - Implement trend analysis over time
    - Calculate average scores
    - _Requirements: 5.5, 12.2, 12.3_
  
  - [x] 6.11 Write property test for weak area identification
    - **Property 14: Performance Analytics Identify Weak Areas**
    - **Validates: Requirements 5.5, 12.2**
  
  - [x] 6.12 Write property test for average score calculation
    - **Property 42: Average Score Calculation**
    - **Validates: Requirements 12.3**
  
  - [x] 6.13 Create mock test UI screens
    - Build test start screen with instructions
    - Create test question screen with timer
    - Implement answer selection UI
    - Create test results screen with score and explanations
    - Add performance analytics dashboard
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 7. Implement progress tracking module
  - [x] 7.1 Create progress tracking service
    - Implement ProgressTracker with study session recording
    - Add completion percentage calculation
    - Implement streak tracking logic
    - Create achievement badge system
    - _Requirements: 7.1, 7.2, 7.3, 7.4_
  
  - [x] 7.2 Write property test for study time accumulation
    - **Property 22: Study Time Accumulation**
    - **Validates: Requirements 7.1, 12.4**
  
  - [x] 7.3 Write property test for completion percentage
    - **Property 23: Completion Percentage Calculation**
    - **Validates: Requirements 7.2**
  
  - [x] 7.4 Write property test for streak calculation
    - **Property 24: Streak Calculation Accuracy**
    - **Validates: Requirements 7.3**
  
  - [x] 7.5 Write property test for badge awards
    - **Property 25: Milestone Badge Awards**
    - **Validates: Requirements 7.4**
  
  - [x] 7.6 Implement badge awarding logic
    - Create logic for "Ready for NTSA" badge (3 consecutive passes at 80%+)
    - Implement Certificate of Competence generation
    - _Requirements: 5.6, 5.7_
  
  - [x] 7.7 Write property test for NTSA badge
    - **Property 15: Badge Award After Three Consecutive Passes**
    - **Validates: Requirements 5.6**
  
  - [ ] 7.8 Write property test for certificate generation
    - **Property 16: Certificate Generation for Consistent Passers**
    - **Validates: Requirements 5.7**
  
  - [ ] 7.9 Create progress tracking UI
    - Build progress dashboard with completion percentages
    - Create achievement badges display
    - Implement streak counter UI
    - Add visual progress indicators for modules
    - Create performance trend charts
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.7, 12.1_
  
  - [ ] 7.10 Write property test for progress indicators
    - **Property 28: Progress Indicators for All Modules**
    - **Validates: Requirements 7.7**
  
  - [ ] 7.11 Write property test for performance trends
    - **Property 41: Performance Trend Visualization**
    - **Validates: Requirements 12.1**

- [ ] 8. Checkpoint - Ensure mock tests and progress tracking work
  - Ensure all tests pass, ask the user if questions arise.

- [x] 9. Implement AI tutor module
  - [x] 9.1 Create AI tutor service and models
    - Implement TutorResponse, StudyRecommendation models
    - Create AITutorService with Gemini 3.1 Pro API integration
    - Implement conversation context management (last 10 exchanges)
    - Add language detection logic
    - _Requirements: 3.1, 3.2_
  
  - [x] 9.2 Implement response caching for offline access
    - Create AI response cache in SQLite
    - Implement common question identification and caching
    - Add cache lookup for offline queries
    - _Requirements: 6.5, 6.6_
  
  - [x] 9.3 Write property test for language consistency
    - **Property 5: AI Tutor Language Consistency**
    - **Validates: Requirements 3.2, 9.5**
  
  - [x] 9.4 Write property test for cached responses
    - **Property 20: Cached Questions Accessible Offline**
    - **Validates: Requirements 6.5**
  
  - [ ] 9.5 Implement personalized recommendations
    - Create recommendation engine based on weak areas
    - Integrate with test performance analytics
    - _Requirements: 3.7_
  
  - [ ] 9.6 Write property test for personalized recommendations
    - **Property 7: Personalized Recommendations Based on Weak Areas**
    - **Validates: Requirements 3.7**
  
  - [ ] 9.7 Write property test for road sign visual references
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
  
  - [ ] 10.2 Write property test for school linking
    - **Property 1: School Linking Establishes Bidirectional Relationship**
    - **Validates: Requirements 2.1**
  
  - [ ] 10.3 Write property test for module unlocking
    - **Property 2: Module Unlocking Respects Schedule**
    - **Validates: Requirements 2.2**
  
  - [ ] 10.4 Write property test for progress sharing
    - **Property 4: Progress Sharing Preserves Data Integrity**
    - **Validates: Requirements 2.6**
  
  - [ ] 10.5 Implement leaderboard functionality
    - Create leaderboard ranking calculation
    - Add opt-in/opt-out preference
    - Implement anonymized school average comparison
    - _Requirements: 7.5, 12.6_
  
  - [ ] 10.6 Write property test for leaderboard ranking
    - **Property 26: Leaderboard Ranking Accuracy**
    - **Validates: Requirements 7.5**
  
  - [ ] 10.7 Write property test for school average comparison
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
  
  - [ ] 11.2 Write property test for payment processing
    - **Property 29: Payment Processing and Feature Unlocking**
    - **Validates: Requirements 8.2, 8.3**
  
  - [ ] 11.3 Implement promo code and referral system
    - Create promo code validation and discount application
    - Implement referral code tracking and discount processing
    - Add school commission calculation (20% revenue share)
    - _Requirements: 8.5, 8.6, 8.7_
  
  - [ ] 11.4 Write property test for school commission
    - **Property 30: School Commission Calculation**
    - **Validates: Requirements 8.5**
  
  - [ ] 11.5 Write property test for promo codes
    - **Property 31: Promo Code Discount Application**
    - **Validates: Requirements 8.6**
  
  - [ ] 11.6 Write property test for referral discounts
    - **Property 32: Referral Discount Processing**
    - **Validates: Requirements 8.7**
  
  - [ ] 11.7 Implement payment error handling
    - Add error message parsing from M-Pesa responses
    - Implement retry logic for failed payments
    - Create payment history tracking
    - _Requirements: 8.8_
  
  - [ ] 11.8 Write property test for payment failures
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
  
  - [ ] 13.2 Write property test for Model Town feedback
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
  
  - [ ] 14.2 Write property test for notification scheduling
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
  
  - [ ] 15.2 Write property test for UI language completeness
    - **Property 34: UI Language Completeness**
    - **Validates: Requirements 9.1**
  
  - [ ] 15.3 Write property test for language selection
    - **Property 35: Language Selection Affects All Content**
    - **Validates: Requirements 9.2**
  
  - [ ] 15.4 Add bilingual content support
    - Ensure all theory content has English and Swahili versions
    - Implement language-aware test question generation
    - _Requirements: 9.3, 9.4_
  
  - [ ] 15.5 Write property test for bilingual content
    - **Property 36: Theory Content Bilingual Availability**
    - **Validates: Requirements 9.3**
  
  - [ ] 15.6 Write property test for test language
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
  
  - [ ] 17.2 Write property test for profile updates
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
  
  - [ ] 18.2 Write property test for study recommendations
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
  
  - [ ] 20.2 Write integration tests
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

- [ ] 22. Implement social learning features
  - [ ] 22.1 Create study groups module
    - Implement StudyGroup, GroupMember, GroupActivity models
    - Create group creation and invitation system
    - Add group chat functionality
    - Implement shared progress tracking within groups
    - _Requirements: Social engagement, peer learning_
  
  - [ ] 22.2 Implement peer challenge system
    - Create challenge creation and acceptance flow
    - Implement head-to-head test competitions
    - Add real-time scoring and leaderboards
    - Create challenge history and statistics
    - _Requirements: Gamification, engagement_
  
  - [ ] 22.3 Add discussion forums
    - Create topic-based discussion threads
    - Implement question posting and answering
    - Add upvoting and best answer selection
    - Integrate AI tutor suggestions in forums
    - _Requirements: Community learning, knowledge sharing_
  
  - [ ] 22.4 Create social UI components
    - Build study group list and detail screens
    - Create challenge invitation and progress screens
    - Implement forum browsing and posting UI
    - Add social activity feed
    - _Requirements: User engagement_

- [ ] 23. Implement gamification and rewards
  - [ ] 23.1 Create comprehensive badge system
    - Implement 50+ achievement badges
    - Add rarity tiers (Bronze, Silver, Gold, Platinum)
    - Create badge showcase and collection UI
    - Implement badge sharing to social media
    - _Requirements: User motivation, retention_
  
  - [ ] 23.2 Implement points and levels system
    - Create XP (experience points) earning mechanics
    - Implement level progression (1-100)
    - Add daily/weekly/monthly challenges
    - Create rewards for level milestones
    - _Requirements: Engagement, progression_
  
  - [ ] 23.3 Add virtual currency system
    - Implement "Dereva Coins" earning and spending
    - Create coin shop for premium features
    - Add coin rewards for achievements
    - Implement daily login bonuses
    - _Requirements: Monetization, engagement_
  
  - [ ] 23.4 Create leaderboards and rankings
    - Implement global, regional, and school leaderboards
    - Add weekly/monthly/all-time rankings
    - Create category-specific leaderboards
    - Implement privacy controls for leaderboard participation
    - _Requirements: Competition, motivation_

- [ ] 24. Implement advanced AI features
  - [ ] 24.1 Create personalized learning paths
    - Implement AI-driven curriculum adaptation
    - Create difficulty adjustment based on performance
    - Add personalized study schedules
    - Implement smart content recommendations
    - _Requirements: Personalization, AI integration_
  
  - [ ] 24.2 Add voice interaction
    - Implement voice-to-text for AI tutor queries
    - Add text-to-speech for lesson content
    - Create voice-guided practice sessions
    - Implement multilingual voice support (English/Swahili)
    - _Requirements: Accessibility, convenience_
  
  - [ ] 24.3 Implement image recognition
    - Add road sign recognition from photos
    - Create AR (Augmented Reality) road sign learning
    - Implement vehicle part identification
    - Add real-world scenario analysis from images
    - _Requirements: Visual learning, practical application_
  
  - [ ] 24.4 Create predictive analytics
    - Implement test success probability prediction
    - Add optimal study time recommendations
    - Create weak area prediction before tests
    - Implement personalized revision schedules
    - _Requirements: AI-driven insights_

- [ ] 25. Implement offline-first enhancements
  - [ ] 25.1 Create advanced sync strategies
    - Implement differential sync for bandwidth efficiency
    - Add conflict resolution UI for user decisions
    - Create sync priority management
    - Implement background sync scheduling
    - _Requirements: Offline reliability, data efficiency_
  
  - [ ] 25.2 Add offline content management
    - Implement selective content download
    - Create storage management UI
    - Add automatic cleanup of old content
    - Implement content expiry and refresh
    - _Requirements: Storage optimization_
  
  - [ ] 25.3 Create offline analytics
    - Implement local analytics collection
    - Add batch analytics upload
    - Create offline event queuing
    - Implement analytics data compression
    - _Requirements: Data tracking, insights_

- [ ] 26. Implement accessibility features
  - [ ] 26.1 Add screen reader support
    - Implement comprehensive content descriptions
    - Add semantic markup for navigation
    - Create audio descriptions for images
    - Test with TalkBack (Android) and VoiceOver (iOS)
    - _Requirements: WCAG 2.1 AA compliance_
  
  - [ ] 26.2 Implement visual accessibility
    - Add high contrast mode
    - Implement adjustable font sizes
    - Create colorblind-friendly themes
    - Add dyslexia-friendly font option
    - _Requirements: Visual impairment support_
  
  - [ ] 26.3 Add motor accessibility
    - Implement larger touch targets
    - Add gesture alternatives
    - Create voice navigation option
    - Implement switch control support
    - _Requirements: Motor impairment support_
  
  - [ ] 26.4 Create cognitive accessibility
    - Add simplified UI mode
    - Implement reading assistance
    - Create focus mode (distraction-free)
    - Add progress indicators for all actions
    - _Requirements: Cognitive support_

- [ ] 27. Implement parent/guardian features
  - [ ] 27.1 Create parent dashboard
    - Implement child account linking
    - Add progress monitoring dashboard
    - Create study time reports
    - Implement test results notifications
    - _Requirements: Parental involvement_
  
  - [ ] 27.2 Add parental controls
    - Implement screen time limits
    - Create content filtering options
    - Add spending controls for in-app purchases
    - Implement activity reports
    - _Requirements: Child safety, oversight_
  
  - [ ] 27.3 Create family plans
    - Implement multi-user subscriptions
    - Add family discount pricing
    - Create shared payment methods
    - Implement family leaderboards
    - _Requirements: Family engagement_

- [ ] 28. Implement instructor/school admin features
  - [ ] 28.1 Create instructor dashboard
    - Implement student management interface
    - Add class creation and organization
    - Create assignment and homework system
    - Implement grade book functionality
    - _Requirements: School integration_
  
  - [ ] 28.2 Add custom content creation
    - Implement custom lesson creation
    - Add custom test generation
    - Create custom curriculum paths
    - Implement content sharing between instructors
    - _Requirements: Customization, flexibility_
  
  - [ ] 28.3 Create analytics for instructors
    - Implement class performance analytics
    - Add individual student progress tracking
    - Create intervention recommendations
    - Implement comparative analytics
    - _Requirements: Teaching effectiveness_
  
  - [ ] 28.4 Add communication tools
    - Implement announcements system
    - Create direct messaging with students
    - Add bulk notifications
    - Implement feedback collection
    - _Requirements: Communication, engagement_

- [ ] 29. Implement advanced testing features
  - [ ] 29.1 Create adaptive testing
    - Implement difficulty-adaptive question selection
    - Add CAT (Computer Adaptive Testing) algorithm
    - Create personalized test difficulty
    - Implement smart question ordering
    - _Requirements: Efficient assessment_
  
  - [ ] 29.2 Add timed practice modes
    - Implement speed drill mode
    - Create time-pressure training
    - Add reaction time tracking
    - Implement time management coaching
    - _Requirements: Test preparation_
  
  - [ ] 29.3 Create test simulation modes
    - Implement full NTSA test simulation
    - Add test center environment simulation
    - Create stress management exercises
    - Implement test-day preparation checklist
    - _Requirements: Realistic preparation_
  
  - [ ] 29.4 Add video explanations
    - Create video explanations for all questions
    - Implement animated road scenarios
    - Add instructor-led explanation videos
    - Create visual learning aids
    - _Requirements: Enhanced learning_

- [ ] 30. Implement content expansion
  - [ ] 30.1 Add practical driving lessons
    - Create video tutorials for driving maneuvers
    - Implement parking technique guides
    - Add emergency procedure videos
    - Create vehicle maintenance tutorials
    - _Requirements: Comprehensive learning_
  
  - [ ] 30.2 Create road safety campaigns
    - Implement seasonal safety tips
    - Add accident prevention content
    - Create defensive driving modules
    - Implement eco-driving education
    - _Requirements: Safety awareness_
  
  - [ ] 30.3 Add legal and regulatory content
    - Implement traffic law updates
    - Create insurance education module
    - Add vehicle registration guides
    - Implement penalty and fine information
    - _Requirements: Legal compliance_
  
  - [ ] 30.4 Create career guidance
    - Add professional driving career paths
    - Implement commercial license information
    - Create driving instructor certification guide
    - Add transport industry insights
    - _Requirements: Career development_

- [ ] 31. Implement marketing and growth features
  - [ ] 31.1 Create referral program
    - Implement referral code generation
    - Add referral tracking and rewards
    - Create social sharing integration
    - Implement referral leaderboards
    - _Requirements: User acquisition_
  
  - [ ] 31.2 Add promotional campaigns
    - Implement seasonal promotions
    - Create limited-time offers
    - Add flash sales functionality
    - Implement promotional notifications
    - _Requirements: Revenue growth_
  
  - [ ] 31.3 Create affiliate program
    - Implement driving school partnerships
    - Add affiliate tracking system
    - Create commission management
    - Implement affiliate dashboard
    - _Requirements: B2B growth_
  
  - [ ] 31.4 Add viral features
    - Implement achievement sharing
    - Create social media integration
    - Add success story showcases
    - Implement user testimonials
    - _Requirements: Organic growth_

- [ ] 32. Implement customer support features
  - [ ] 32.1 Create in-app support
    - Implement live chat support
    - Add ticket system for issues
    - Create FAQ and help center
    - Implement video tutorials library
    - _Requirements: User support_
  
  - [ ] 32.2 Add feedback system
    - Implement in-app feedback forms
    - Create feature request voting
    - Add bug reporting with screenshots
    - Implement satisfaction surveys
    - _Requirements: Product improvement_
  
  - [ ] 32.3 Create community moderation
    - Implement content reporting system
    - Add user blocking functionality
    - Create moderation dashboard
    - Implement automated content filtering
    - _Requirements: Community safety_

- [ ] 33. Implement business intelligence
  - [ ] 33.1 Create admin analytics dashboard
    - Implement user acquisition metrics
    - Add retention and churn analysis
    - Create revenue analytics
    - Implement cohort analysis
    - _Requirements: Business insights_
  
  - [ ] 33.2 Add A/B testing framework
    - Implement feature flag system
    - Create experiment tracking
    - Add statistical significance testing
    - Implement gradual rollout system
    - _Requirements: Data-driven decisions_
  
  - [ ] 33.3 Create reporting system
    - Implement automated report generation
    - Add custom report builder
    - Create scheduled report delivery
    - Implement data export functionality
    - _Requirements: Business reporting_

- [ ] 34. Implement security enhancements
  - [ ] 34.1 Add advanced authentication
    - Implement biometric authentication
    - Add two-factor authentication (2FA)
    - Create session management
    - Implement device trust system
    - _Requirements: Security, fraud prevention_
  
  - [ ] 34.2 Create fraud detection
    - Implement suspicious activity monitoring
    - Add payment fraud detection
    - Create account takeover prevention
    - Implement rate limiting
    - _Requirements: Platform security_
  
  - [ ] 34.3 Add data privacy features
    - Implement GDPR compliance tools
    - Create data export functionality
    - Add data deletion requests
    - Implement privacy dashboard
    - _Requirements: Legal compliance_

- [ ] 35. Implement performance monitoring
  - [ ] 35.1 Add crash reporting
    - Implement Firebase Crashlytics
    - Create error tracking dashboard
    - Add crash-free user metrics
    - Implement automated alerts
    - _Requirements: App stability_
  
  - [ ] 35.2 Create performance monitoring
    - Implement app performance metrics
    - Add network performance tracking
    - Create battery usage monitoring
    - Implement memory leak detection
    - _Requirements: App optimization_
  
  - [ ] 35.3 Add user behavior analytics
    - Implement screen flow tracking
    - Create user journey mapping
    - Add feature usage analytics
    - Implement drop-off analysis
    - _Requirements: UX optimization_

- [ ] 36. Final polish and launch preparation
  - [ ] 36.1 Create onboarding experience
    - Implement interactive tutorial
    - Add feature highlights
    - Create personalization wizard
    - Implement skip and progress indicators
    - _Requirements: User activation_
  
  - [ ] 36.2 Add app store optimization
    - Create compelling app descriptions
    - Design promotional graphics
    - Implement app preview videos
    - Add localized store listings
    - _Requirements: App discovery_
  
  - [ ] 36.3 Create launch materials
    - Implement press kit
    - Create demo videos
    - Add user documentation
    - Implement changelog system
    - _Requirements: Marketing, support_
  
  - [ ] 36.4 Final quality assurance
    - Conduct comprehensive testing
    - Perform security audit
    - Execute performance testing
    - Implement beta testing program
    - _Requirements: Launch readiness_

## Notes

- Tasks marked with `*` are optional property-based tests that can be skipped for faster MVP
- Each property test should run minimum 100 iterations
- Property tests should include comment tags: `// Feature: b2c-learner-app, Property {N}: {property_text}`
- Checkpoints ensure incremental validation and provide opportunities for user feedback
- All tasks reference specific requirements for traceability
- The implementation follows offline-first architecture throughout
- Security requirements (TLS 1.3, bcrypt) must be verified during implementation
- New features (Tasks 22-36) are advanced enhancements for post-MVP releases
- Prioritize features based on user feedback and business metrics
- Each new feature should have corresponding analytics tracking
- Maintain backward compatibility when adding new features