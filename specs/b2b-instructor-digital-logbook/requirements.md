# Requirements Document: B2B Instructor Digital Logbook

## Introduction

The B2B Instructor Digital Logbook is a mobile application for driving school instructors in Kenya to log student practical training hours with GPS verification and QR code check-ins. This system addresses the national crisis of forged practical training hours by creating an audit-proof, NTSA-compliant digital logbook that eliminates manual logbook fraud while maintaining usability for instructors with varying levels of technical literacy.

## Glossary

- **Instructor_App**: The mobile application used by driving school instructors to log training sessions
- **Training_Session**: A discrete period of practical driving instruction with defined start and end times
- **GPS_Stamp**: A geographic coordinate and timestamp pair recorded at a specific moment
- **QR_Check_In**: A verification process where a student or vehicle QR code is scanned to confirm presence
- **Session_Record**: The complete data structure containing all information about a training session
- **Sync_Queue**: A local storage mechanism that holds session data when offline for later upload
- **NTSA**: National Transport and Safety Authority (Kenya's regulatory body)
- **License_Category**: The classification of driver's license (A1-A3, B1-B3, C, D, E, F, G)
- **Yard_Practice**: Training conducted in a controlled practice area
- **Road_Practice**: Training conducted on public roads
- **Audit_Trail**: An immutable chronological record of all session activities
- **Backend_Service**: The cloud-based system (Firebase/Supabase) that stores and processes session data
- **Digital_Signature**: A cryptographic verification mechanism for completed training hours

## Requirements

### Requirement 1: GPS-Verified Session Tracking

**User Story:** As an instructor, I want the system to automatically record GPS coordinates when I start and end lessons, so that training locations and times are verifiable and cannot be manually altered.

#### Acceptance Criteria

1. WHEN an instructor starts a training session, THE Instructor_App SHALL capture and store a GPS_Stamp with location coordinates, timestamp, and accuracy metadata
2. WHEN an instructor ends a training session, THE Instructor_App SHALL capture and store a GPS_Stamp with location coordinates, timestamp, and accuracy metadata
3. WHEN a Training_Session is active, THE Instructor_App SHALL record GPS_Stamps at regular intervals to create a location trail
4. THE Instructor_App SHALL calculate session duration from start and end GPS_Stamps without allowing manual time entry
5. WHEN GPS accuracy is below acceptable threshold, THE Instructor_App SHALL warn the instructor and mark the session with low accuracy flag
6. THE Instructor_App SHALL distinguish between Yard_Practice and Road_Practice session types and store this classification with each Session_Record
7. WHEN storing GPS_Stamps, THE Instructor_App SHALL encrypt the timestamp and coordinates to prevent tampering

### Requirement 2: QR Code Check-In System

**User Story:** As an instructor, I want to verify student and vehicle presence by scanning QR codes at lesson start, so that there is proof of who attended and which vehicle was used.

#### Acceptance Criteria

1. WHEN an instructor scans a student QR code, THE Instructor_App SHALL decode the student identifier and associate it with the current Training_Session
2. WHEN an instructor scans a vehicle QR code, THE Instructor_App SHALL decode the vehicle identifier and associate it with the current Training_Session
3. WHEN a QR code scan fails or produces invalid data, THE Instructor_App SHALL display an error message and prevent session start
4. THE Instructor_App SHALL require both student and vehicle QR_Check_In before allowing a Training_Session to start
5. WHEN QR_Check_In data is stored, THE Instructor_App SHALL include a cryptographic hash to create a Digital_Signature for tamper detection
6. THE Instructor_App SHALL store the QR_Check_In timestamp as part of the Session_Record

### Requirement 3: Student Management

**User Story:** As an instructor, I want to view my assigned students organized by license category and track their progress, so that I can see who needs training and how many hours they have remaining.

#### Acceptance Criteria

1. WHEN an instructor opens the student list, THE Instructor_App SHALL display all assigned students grouped by License_Category
2. WHEN displaying a student, THE Instructor_App SHALL show the student's name, License_Category, total completed hours, and remaining required hours
3. WHEN an instructor selects a student, THE Instructor_App SHALL display a detailed view with all past Training_Sessions for that student
4. THE Instructor_App SHALL calculate remaining required hours by subtracting completed hours from the category-specific requirement
5. WHEN the Backend_Service adds new students to an instructor's roster, THE Instructor_App SHALL sync and display these students on next connection
6. THE Instructor_App SHALL cache student data locally for offline access

### Requirement 4: Session Logging

**User Story:** As an instructor, I want to record detailed information about each training session including skills practiced and observations, so that there is a complete record of what was taught.

#### Acceptance Criteria

1. WHEN logging a Training_Session, THE Instructor_App SHALL allow the instructor to select lesson type (Yard_Practice or Road_Practice)
2. WHEN logging a Training_Session, THE Instructor_App SHALL provide a list of predefined skills for the instructor to mark as practiced
3. WHEN logging a Training_Session, THE Instructor_App SHALL allow the instructor to enter free-text notes and observations
4. WHEN logging a Training_Session, THE Instructor_App SHALL allow the instructor to record weather conditions from a predefined list
5. WHEN logging a Training_Session, THE Instructor_App SHALL allow the instructor to record vehicle condition from a predefined list
6. THE Instructor_App SHALL store all session details as part of the Session_Record with immutable timestamps
7. WHEN a Training_Session is completed, THE Instructor_App SHALL require the instructor to confirm all details before finalizing

### Requirement 5: NTSA Compliance Reporting

**User Story:** As a driving school administrator, I want to generate NTSA-compliant reports of student training hours with digital signatures, so that we can pass audits and prove training authenticity.

#### Acceptance Criteria

1. WHEN generating a report for a student, THE Instructor_App SHALL compile all Session_Records for that student into an NTSA-compliant format
2. WHEN generating a report, THE Instructor_App SHALL include all GPS_Stamps, timestamps, session durations, and instructor details
3. WHEN generating a report, THE Instructor_App SHALL calculate total Yard_Practice hours and Road_Practice hours separately
4. WHEN generating a report, THE Instructor_App SHALL include Digital_Signatures for each Training_Session to prove authenticity
5. THE Instructor_App SHALL export reports in PDF format with embedded verification data
6. THE Instructor_App SHALL maintain an Audit_Trail showing all report generation events with timestamps
7. WHEN exporting a report, THE Instructor_App SHALL include QR_Check_In data for each session as proof of attendance

### Requirement 6: Offline Capability

**User Story:** As an instructor working in low-connectivity areas, I want the app to function offline and automatically sync when connection is restored, so that I can log sessions regardless of network availability.

#### Acceptance Criteria

1. WHEN the Instructor_App detects no network connection, THE Instructor_App SHALL continue to allow session logging with full functionality
2. WHEN operating offline, THE Instructor_App SHALL store all Session_Records in a local Sync_Queue
3. WHEN network connection is restored, THE Instructor_App SHALL automatically upload all queued Session_Records to the Backend_Service
4. WHEN syncing queued sessions, THE Instructor_App SHALL preserve the original GPS_Stamps and timestamps without modification
5. WHEN a sync operation fails, THE Instructor_App SHALL retry with exponential backoff until successful
6. THE Instructor_App SHALL display sync status to the instructor showing pending uploads and last successful sync time
7. WHEN student data is unavailable due to offline status, THE Instructor_App SHALL use cached student information from the last successful sync

### Requirement 7: User Interface Simplicity

**User Story:** As an instructor with limited technical literacy, I want a simple and intuitive interface with clear visual cues, so that I can use the app without extensive training.

#### Acceptance Criteria

1. WHEN the Instructor_App displays any screen, THE Instructor_App SHALL use large, clearly labeled buttons with icons
2. WHEN the Instructor_App requires user input, THE Instructor_App SHALL provide clear instructions in simple language
3. THE Instructor_App SHALL use a consistent color scheme where green indicates success, red indicates errors, and yellow indicates warnings
4. WHEN a Training_Session is active, THE Instructor_App SHALL display a prominent timer showing elapsed time
5. THE Instructor_App SHALL limit each screen to a single primary action to avoid confusion
6. WHEN errors occur, THE Instructor_App SHALL display error messages in plain language without technical jargon
7. THE Instructor_App SHALL provide visual feedback for all user actions within 200 milliseconds

### Requirement 8: Data Security and Integrity

**User Story:** As a system administrator, I want all training data to be encrypted and tamper-proof, so that we can guarantee data integrity for regulatory compliance.

#### Acceptance Criteria

1. WHEN storing Session_Records locally, THE Instructor_App SHALL encrypt all data using AES-256 encryption
2. WHEN transmitting Session_Records to the Backend_Service, THE Instructor_App SHALL use TLS 1.3 or higher
3. THE Instructor_App SHALL generate a cryptographic hash for each Session_Record to detect any tampering
4. WHEN a Session_Record is modified after creation, THE Instructor_App SHALL reject the modification and log a security event
5. THE Instructor_App SHALL require biometric or PIN authentication before allowing access
6. WHEN the Instructor_App is idle for more than 5 minutes, THE Instructor_App SHALL automatically lock and require re-authentication
7. THE Instructor_App SHALL store authentication tokens securely using platform-specific secure storage mechanisms

### Requirement 9: Session State Management

**User Story:** As an instructor, I want the app to handle interruptions gracefully, so that I don't lose session data if the app crashes or my phone battery dies.

#### Acceptance Criteria

1. WHEN a Training_Session is in progress, THE Instructor_App SHALL persist session state to local storage every 30 seconds
2. WHEN the Instructor_App is force-closed or crashes during a session, THE Instructor_App SHALL restore the session state on next launch
3. WHEN the Instructor_App detects low battery during a session, THE Instructor_App SHALL warn the instructor and offer to end the session early
4. WHEN the Instructor_App is backgrounded during a session, THE Instructor_App SHALL continue recording GPS_Stamps
5. THE Instructor_App SHALL prevent starting a new Training_Session when another session is already active
6. WHEN an incomplete session is detected on app launch, THE Instructor_App SHALL prompt the instructor to either resume or discard the session

### Requirement 10: Performance and Resource Management

**User Story:** As an instructor using an older Android device, I want the app to run smoothly without draining my battery or consuming excessive storage, so that I can use it throughout the day.

#### Acceptance Criteria

1. THE Instructor_App SHALL limit GPS polling frequency to once every 10 seconds during active sessions to conserve battery
2. THE Instructor_App SHALL compress Session_Records before storing them locally to minimize storage usage
3. WHEN local storage exceeds 80% capacity, THE Instructor_App SHALL prioritize syncing and deleting successfully uploaded sessions
4. THE Instructor_App SHALL complete app launch and display the home screen within 3 seconds on devices with 2GB RAM or more
5. WHEN the Instructor_App is idle, THE Instructor_App SHALL reduce background activity to minimal levels
6. THE Instructor_App SHALL limit cached student data to the most recent 100 students per instructor
