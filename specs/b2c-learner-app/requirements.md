# Requirements Document: B2C Learner App

## Introduction

The B2C Learner App is an AI-powered mobile application for learner drivers in Kenya preparing for their NTSA theory test. The app provides interactive study materials, an AI tutor powered by Gemini 3.1 Pro, mock tests aligned with NTSA standards, and offline-capable content. It syncs with registered driving schools to provide a coordinated learning experience that supplements practical training.

## Glossary

- **NTSA**: National Transport and Safety Authority (Kenya's transport regulatory body)
- **PDL**: Provisional Driving License (required before taking the NTSA test)
- **eCitizen**: Kenya's government digital services platform
- **Learner_App**: The B2C mobile application system
- **AI_Tutor**: The Gemini 3.1 Pro powered conversational assistant
- **Mock_Test_Engine**: The system component that generates and evaluates practice tests
- **School_Sync_Service**: The system component that synchronizes with driving school data
- **Content_Manager**: The system component that manages theory content and offline storage
- **Payment_Gateway**: The M-Pesa Daraja API integration component
- **Progress_Tracker**: The system component that tracks user learning progress
- **LN_28_2020**: Legal Notice 28 of 2020 (NTSA curriculum standard)
- **Model_Town**: Interactive 3D simulation environment for traffic scenarios

## Requirements

### Requirement 1: eCitizen Integration and PDL Application Support

**User Story:** As a learner driver, I want to access eCitizen PDL application services directly from the app, so that I can complete my license application process efficiently.

#### Acceptance Criteria

1. THE Learner_App SHALL provide a direct link to the eCitizen PDL application portal
2. WHEN a user accesses the PDL application guide, THE Learner_App SHALL display step-by-step instructions for completing the KSh 650 PDL application
3. THE Learner_App SHALL provide a PDL application status tracking interface
4. WHEN a user enters their eCitizen application reference number, THE Learner_App SHALL display the current application status
5. THE Learner_App SHALL display all required documents and prerequisites for PDL application

### Requirement 2: Driving School Synchronization

**User Story:** As a learner driver, I want my app content to sync with my driving school's teaching schedule, so that my theory learning aligns with my practical lessons.

#### Acceptance Criteria

1. WHEN a user registers with a driving school code, THE School_Sync_Service SHALL link the user account to the registered driving school
2. THE School_Sync_Service SHALL unlock theory modules based on the instructor's teaching schedule
3. WHEN an instructor updates the teaching schedule, THE School_Sync_Service SHALL synchronize module availability within 5 minutes
4. THE Learner_App SHALL support all NTSA license categories (A1, A2, A3, B1, B2, B3, C, D, E, F, G)
5. WHEN a user selects a license category, THE Content_Manager SHALL display category-specific modules and Common Core Units
6. THE School_Sync_Service SHALL track user progress and share completion data with the registered driving school

### Requirement 3: AI Tutor Conversational Interface

**User Story:** As a learner driver, I want to ask questions to an AI tutor in my preferred language, so that I can get immediate clarification on driving concepts.

#### Acceptance Criteria

1. THE AI_Tutor SHALL provide real-time conversational responses using Gemini 3.1 Pro API
2. THE AI_Tutor SHALL support both English and Swahili language inputs and outputs
3. WHEN a user asks a question about NTSA curriculum content, THE AI_Tutor SHALL provide accurate answers based on LN 28 of 2020 standards
4. WHEN a user asks about road signs, THE AI_Tutor SHALL provide explanations with visual references
5. THE AI_Tutor SHALL provide context-aware responses based on the user's current progress and license category
6. WHEN a user asks about driving techniques, THE AI_Tutor SHALL provide explanations aligned with NTSA best practices
7. THE AI_Tutor SHALL provide personalized study recommendations based on user performance data

### Requirement 4: Interactive Theory Content Delivery

**User Story:** As a learner driver, I want to access engaging interactive content aligned with NTSA curriculum, so that I can learn effectively through multiple formats.

#### Acceptance Criteria

1. THE Content_Manager SHALL provide theory content that is 100% aligned with LN 28 of 2020 NTSA curriculum
2. THE Learner_App SHALL display short instructional videos for each curriculum topic
3. THE Learner_App SHALL provide AR-based road sign recognition functionality
4. THE Learner_App SHALL provide an interactive drag-and-drop 3D Model Town simulation
5. WHEN a user interacts with the Model Town, THE Learner_App SHALL provide real-time feedback on traffic scenario decisions
6. THE Content_Manager SHALL organize content into Common Core Units and category-specific modules
7. THE Learner_App SHALL display visual learning aids including diagrams and illustrations for all theory topics

### Requirement 5: NTSA Mock Test Generation and Evaluation

**User Story:** As a learner driver, I want to take realistic mock tests that match NTSA format, so that I can assess my readiness for the actual test.

#### Acceptance Criteria

1. THE Mock_Test_Engine SHALL auto-generate 50-question tests that replicate NTSA test format
2. THE Mock_Test_Engine SHALL enforce the 80% pass mark requirement (40 out of 50 questions correct)
3. WHEN a user starts a mock test, THE Mock_Test_Engine SHALL apply time limits matching NTSA test duration
4. WHEN a user completes a mock test, THE Mock_Test_Engine SHALL provide detailed explanations for all answers
5. THE Mock_Test_Engine SHALL generate performance analytics identifying weak knowledge areas
6. WHEN a user achieves 80% or higher on three consecutive mock tests, THE Learner_App SHALL award a digital "Ready for NTSA" badge
7. THE Learner_App SHALL generate a shareable Certificate of Competence for users who pass mock tests consistently
8. THE Mock_Test_Engine SHALL draw questions from all relevant curriculum areas based on the user's license category

### Requirement 6: Offline Mode Functionality

**User Story:** As a learner driver in a rural area with limited connectivity, I want to access study materials offline, so that I can continue learning without internet access.

#### Acceptance Criteria

1. THE Content_Manager SHALL allow users to download complete theory modules for offline access
2. THE Mock_Test_Engine SHALL allow users to take mock tests without internet connectivity
3. WHEN internet connectivity is restored, THE Content_Manager SHALL synchronize user progress to the cloud
4. THE Learner_App SHALL provide a low-data mode that minimizes bandwidth usage
5. THE AI_Tutor SHALL cache responses to common questions for offline access
6. WHEN a user asks a cached question offline, THE AI_Tutor SHALL provide the cached response within 2 seconds
7. THE Learner_App SHALL clearly indicate which content is available offline and which requires connectivity

### Requirement 7: Progress Tracking and Gamification

**User Story:** As a learner driver, I want to track my learning progress and earn achievements, so that I stay motivated throughout my preparation.

#### Acceptance Criteria

1. THE Progress_Tracker SHALL record total study time for each user
2. THE Progress_Tracker SHALL calculate and display module completion percentage
3. THE Progress_Tracker SHALL track daily study streaks
4. WHEN a user achieves a milestone, THE Learner_App SHALL award achievement badges
5. WHERE a user opts into leaderboards, THE Learner_App SHALL display rankings within their driving school
6. THE Learner_App SHALL send study reminder notifications based on user preferences
7. THE Progress_Tracker SHALL display visual progress indicators for each curriculum module

### Requirement 8: Payment Processing and Subscription Management

**User Story:** As a learner driver, I want to purchase premium features using M-Pesa, so that I can access full app functionality.

#### Acceptance Criteria

1. THE Learner_App SHALL provide free access to basic theory notes and one mock test
2. THE Payment_Gateway SHALL process one-time payments of KSh 699 for lifetime premium access
3. THE Payment_Gateway SHALL process monthly subscription payments of KSh 299 via M-Pesa Daraja API
4. WHEN a payment is successful, THE Learner_App SHALL unlock premium features within 30 seconds
5. THE Payment_Gateway SHALL track driving school commission allocations (20% revenue share)
6. THE Payment_Gateway SHALL validate and apply promo codes for discounts
7. THE Payment_Gateway SHALL process referral discounts when users invite other learners
8. WHEN a payment fails, THE Payment_Gateway SHALL provide clear error messages and retry options

### Requirement 9: Multi-Language Support

**User Story:** As a learner driver, I want to use the app in my preferred language, so that I can learn in the language I'm most comfortable with.

#### Acceptance Criteria

1. THE Learner_App SHALL support English and Swahili languages for all user interface elements
2. WHEN a user selects a language preference, THE Learner_App SHALL display all content in the selected language
3. THE Content_Manager SHALL provide theory content in both English and Swahili
4. THE Mock_Test_Engine SHALL generate test questions in the user's selected language
5. THE AI_Tutor SHALL detect the input language and respond in the same language

### Requirement 10: Performance and Device Compatibility

**User Story:** As a learner driver with a low-end device, I want the app to run smoothly on my phone, so that I can access learning materials without technical issues.

#### Acceptance Criteria

1. THE Learner_App SHALL function on devices with minimum 2GB RAM
2. WHEN the app launches on a low-end device, THE Learner_App SHALL load the home screen within 5 seconds
3. THE Learner_App SHALL support Android 8.0 and above
4. THE Learner_App SHALL support iOS 12.0 and above
5. THE Content_Manager SHALL optimize video content for low-bandwidth connections
6. THE Learner_App SHALL limit background data usage to 10MB per day in low-data mode

### Requirement 11: User Authentication and Account Management

**User Story:** As a learner driver, I want to create and manage my account securely, so that my progress and payment information are protected.

#### Acceptance Criteria

1. THE Learner_App SHALL allow users to register using phone number and password
2. THE Learner_App SHALL send SMS verification codes for phone number validation
3. WHEN a user forgets their password, THE Learner_App SHALL provide a password reset flow via SMS
4. THE Learner_App SHALL encrypt all user data in transit using TLS 1.3
5. THE Learner_App SHALL store user passwords using bcrypt hashing with minimum 12 rounds
6. THE Learner_App SHALL allow users to update their profile information including name, license category, and driving school
7. WHEN a user deletes their account, THE Learner_App SHALL permanently remove all personal data within 30 days

### Requirement 12: Analytics and Reporting

**User Story:** As a learner driver, I want to see detailed analytics of my performance, so that I can identify areas needing improvement.

#### Acceptance Criteria

1. THE Progress_Tracker SHALL display performance trends over time with visual charts
2. THE Mock_Test_Engine SHALL identify and highlight the user's three weakest knowledge areas
3. THE Progress_Tracker SHALL calculate and display average test scores
4. THE Progress_Tracker SHALL show time spent on each curriculum module
5. WHEN a user views analytics, THE Learner_App SHALL provide actionable study recommendations
6. THE Progress_Tracker SHALL compare user performance against driving school averages (anonymized)
