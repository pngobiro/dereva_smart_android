# Requirements Document: B2B Instructor Digital Logbook

## Introduction

The B2B Instructor Digital Logbook is a mobile-first system designed to digitize and verify practical driver training hours in Kenya's driving school ecosystem. The system addresses the critical problem of forged training logbooks by providing GPS-verified, tamper-proof digital records that meet NTSA (National Transport and Safety Authority) audit requirements.

The system consists of three main components:
1. A mobile application for driving instructors to log practical lessons in real-time
2. An administrative dashboard for driving school management
3. Automated reporting for NTSA compliance

## Glossary

- **Instructor_App**: The mobile application used by driving instructors to log practical training sessions
- **Admin_Dashboard**: The web-based management interface for driving school administrators
- **Training_Session**: A single practical driving lesson with a defined start time, end time, and GPS track
- **Student**: A learner enrolled in a driving school pursuing a specific license category
- **License_Category**: The classification of driving licenses (A1-A3, B1-B3, C, D, E, F, G) as defined by NTSA
- **Yard_Hours**: Practical training hours conducted in a controlled training yard environment
- **Road_Hours**: Practical training hours conducted on public roads
- **QR_Check_In**: The process of scanning QR codes to verify vehicle and student identity at session start
- **GPS_Track**: The recorded geographical path of a training session with timestamps
- **NTSA_Report**: A standardized progress report format required for NTSA audits
- **Sync_Engine**: The component responsible for synchronizing offline data when connectivity is restored
- **Digital_Logbook**: The tamper-proof electronic record of all training sessions for a student
- **Driving_School**: An organization that employs instructors and enrolls students for driver training

## Requirements

### Requirement 1: Student Management

**User Story:** As a driving school administrator, I want to manage student enrollments and assignments, so that instructors can access their assigned students and track training progress.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL allow administrators to create student profiles with name, contact information, and license category
2. WHEN an administrator assigns a student to an instructor, THE System SHALL make that student visible in the instructor's student list
3. THE Admin_Dashboard SHALL support bulk import of students via CSV file upload
4. WHEN importing students via CSV, THE System SHALL validate required fields (name, license category) and report any errors
5. THE System SHALL organize students by license category (A1-A3, B1-B3, C, D, E, F, G)
6. THE Admin_Dashboard SHALL display each student's total yard hours and road hours completed
7. WHEN a student completes all required hours for their license category, THE System SHALL mark the student as training-complete

### Requirement 2: Instructor Authentication and Profile Management

**User Story:** As a driving school administrator, I want to manage instructor accounts, so that only authorized instructors can log training sessions.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL allow administrators to create instructor accounts with email and initial password
2. WHEN an instructor first logs into the Instructor_App, THE System SHALL require a password change
3. THE Instructor_App SHALL authenticate instructors using email and password
4. WHEN authentication fails, THE Instructor_App SHALL display an error message and prevent access
5. THE System SHALL maintain instructor profiles including name, phone number, and assigned students
6. THE Instructor_App SHALL allow instructors to view their profile information
7. THE System SHALL support password reset via email verification

### Requirement 3: Training Session Initiation with QR Code Verification

**User Story:** As an instructor, I want to start a training session by scanning QR codes, so that the vehicle and student are verified before the lesson begins.

#### Acceptance Criteria

1. WHEN an instructor initiates a new training session, THE Instructor_App SHALL prompt for QR code scanning
2. THE Instructor_App SHALL scan the vehicle QR code and verify it belongs to the driving school
3. THE Instructor_App SHALL scan the student QR code and verify the student is assigned to the instructor
4. WHEN an invalid QR code is scanned, THE Instructor_App SHALL display an error message and prevent session start
5. WHEN both QR codes are verified, THE Instructor_App SHALL prompt the instructor to select session type (yard or road)
6. THE Instructor_App SHALL record the session start timestamp when verification is complete
7. THE Instructor_App SHALL capture the GPS location at session start

### Requirement 4: GPS Tracking During Training Sessions

**User Story:** As an instructor, I want the app to automatically track GPS location during training sessions, so that the training route is recorded for verification.

#### Acceptance Criteria

1. WHEN a training session is active, THE Instructor_App SHALL continuously record GPS coordinates at 30-second intervals
2. THE Instructor_App SHALL store GPS coordinates with timestamps in the GPS_Track
3. WHEN GPS signal is unavailable, THE Instructor_App SHALL log the gap in tracking and continue when signal returns
4. THE Instructor_App SHALL calculate total distance traveled based on GPS_Track
5. THE Instructor_App SHALL display current session duration in real-time
6. WHEN a training session is active, THE Instructor_App SHALL prevent the device from sleeping
7. THE Instructor_App SHALL store GPS_Track data locally until the session is completed

### Requirement 5: Training Session Completion and Hour Logging

**User Story:** As an instructor, I want to end a training session and log the hours, so that the student's progress is recorded accurately.

#### Acceptance Criteria

1. WHEN an instructor ends a training session, THE Instructor_App SHALL record the session end timestamp
2. THE Instructor_App SHALL calculate session duration in hours and minutes
3. THE Instructor_App SHALL prompt the instructor to confirm the session type (yard or road)
4. WHEN the instructor confirms, THE System SHALL add the session hours to the student's total (yard or road)
5. THE Instructor_App SHALL save the complete Training_Session record including GPS_Track, timestamps, and duration
6. THE Instructor_App SHALL display a summary of the completed session
7. THE System SHALL mark the Training_Session as immutable after completion

### Requirement 6: Offline Capability and Data Synchronization

**User Story:** As an instructor, I want to log training sessions without internet connectivity, so that I can work in areas with poor network coverage.

#### Acceptance Criteria

1. WHEN the Instructor_App has no internet connectivity, THE System SHALL store all Training_Session data locally
2. THE Instructor_App SHALL display the current connectivity status (online/offline)
3. WHEN internet connectivity is restored, THE Sync_Engine SHALL automatically upload pending Training_Session records
4. THE Sync_Engine SHALL synchronize data in chronological order based on session start time
5. WHEN synchronization fails, THE Sync_Engine SHALL retry with exponential backoff
6. THE Instructor_App SHALL display sync status for each pending Training_Session
7. WHEN all data is synchronized, THE Instructor_App SHALL display a confirmation message

### Requirement 7: Student Progress Tracking

**User Story:** As an instructor, I want to view my students' training progress, so that I can plan upcoming lessons and track completion.

#### Acceptance Criteria

1. THE Instructor_App SHALL display a list of all students assigned to the instructor
2. WHEN an instructor selects a student, THE Instructor_App SHALL display total yard hours and road hours completed
3. THE Instructor_App SHALL display the required hours for the student's license category
4. THE Instructor_App SHALL calculate and display remaining hours needed (yard and road)
5. THE Instructor_App SHALL display a list of all completed Training_Sessions for the selected student
6. WHEN viewing a Training_Session, THE Instructor_App SHALL display session date, duration, type, and distance traveled
7. THE Instructor_App SHALL highlight students who have completed all required hours

### Requirement 8: NTSA-Compliant Progress Reports

**User Story:** As a driving school administrator, I want to generate NTSA-compliant progress reports, so that I can provide verified documentation during audits.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL generate NTSA_Reports for individual students
2. THE NTSA_Report SHALL include student name, license category, total yard hours, and total road hours
3. THE NTSA_Report SHALL include a chronological list of all Training_Sessions with dates, durations, and instructor names
4. THE NTSA_Report SHALL include GPS verification status for each Training_Session
5. THE Admin_Dashboard SHALL export NTSA_Reports as PDF files
6. THE NTSA_Report SHALL include a digital signature or verification code to prevent tampering
7. THE Admin_Dashboard SHALL allow filtering reports by date range, instructor, or license category

### Requirement 9: Administrative Dashboard for Driving School Management

**User Story:** As a driving school administrator, I want a comprehensive dashboard to manage instructors, students, and training activities, so that I can oversee operations efficiently.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL display total number of active students, instructors, and completed training sessions
2. THE Admin_Dashboard SHALL display a list of all instructors with their assigned student counts
3. THE Admin_Dashboard SHALL display a list of all students with their training progress
4. THE Admin_Dashboard SHALL allow administrators to assign or reassign students to instructors
5. THE Admin_Dashboard SHALL display recent Training_Sessions across all instructors
6. THE Admin_Dashboard SHALL allow administrators to search for students by name or license category
7. THE Admin_Dashboard SHALL display alerts for students approaching completion or overdue for training

### Requirement 10: Data Integrity and Tamper Prevention

**User Story:** As a driving school administrator, I want training records to be tamper-proof, so that NTSA auditors can trust the authenticity of our logbooks.

#### Acceptance Criteria

1. WHEN a Training_Session is completed, THE System SHALL generate a cryptographic hash of the session data
2. THE System SHALL store the hash with the Training_Session record
3. WHEN displaying a Training_Session, THE System SHALL verify the hash matches the session data
4. IF a hash verification fails, THE System SHALL flag the Training_Session as potentially tampered
5. THE System SHALL prevent modification of completed Training_Session records
6. THE System SHALL log all administrative actions (student creation, instructor assignment) with timestamps and user IDs
7. THE Admin_Dashboard SHALL display an audit trail of all system modifications

### Requirement 11: Vehicle Management

**User Story:** As a driving school administrator, I want to manage training vehicles and their QR codes, so that instructors can verify vehicles during session check-in.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL allow administrators to register training vehicles with registration number and vehicle type
2. WHEN a vehicle is registered, THE System SHALL generate a unique QR code for that vehicle
3. THE Admin_Dashboard SHALL display and allow printing of vehicle QR codes
4. THE System SHALL associate each Training_Session with the vehicle used
5. THE Admin_Dashboard SHALL display vehicle usage statistics (total sessions, total hours)
6. THE Admin_Dashboard SHALL allow administrators to deactivate vehicles
7. WHEN a deactivated vehicle QR code is scanned, THE Instructor_App SHALL reject the check-in

### Requirement 12: Student QR Code Generation

**User Story:** As a driving school administrator, I want to generate QR codes for students, so that instructors can verify student identity during session check-in.

#### Acceptance Criteria

1. WHEN a student is created, THE System SHALL generate a unique QR code for that student
2. THE Admin_Dashboard SHALL display the student QR code on the student profile page
3. THE Admin_Dashboard SHALL allow printing or downloading of student QR codes
4. THE Student QR code SHALL encode the student ID and license category
5. WHEN a student QR code is scanned, THE Instructor_App SHALL display the student name and license category for verification
6. THE System SHALL support regenerating QR codes if a student's code is lost or compromised
7. WHEN a QR code is regenerated, THE System SHALL invalidate the previous QR code

### Requirement 13: Session Validation Rules

**User Story:** As a system administrator, I want the system to enforce training session validation rules, so that only legitimate training sessions are recorded.

#### Acceptance Criteria

1. THE System SHALL reject Training_Sessions with duration less than 15 minutes
2. THE System SHALL reject Training_Sessions with GPS_Track containing fewer than 10 coordinate points
3. WHEN a Training_Session has GPS gaps exceeding 5 minutes, THE System SHALL flag it for review
4. THE System SHALL prevent an instructor from starting multiple concurrent Training_Sessions
5. THE System SHALL prevent logging more than 8 hours of training per student per day
6. WHEN validation rules are violated, THE System SHALL display a specific error message explaining the violation
7. THE Admin_Dashboard SHALL allow administrators to review and approve flagged Training_Sessions

### Requirement 14: Notification System

**User Story:** As an instructor, I want to receive notifications about student assignments and system updates, so that I stay informed about my responsibilities.

#### Acceptance Criteria

1. WHEN a student is assigned to an instructor, THE System SHALL send a push notification to the Instructor_App
2. WHEN a student completes all required hours, THE System SHALL notify the assigned instructor
3. WHEN the Sync_Engine completes synchronization, THE Instructor_App SHALL display a notification
4. THE Instructor_App SHALL allow instructors to enable or disable push notifications
5. THE Admin_Dashboard SHALL allow administrators to send broadcast messages to all instructors
6. WHEN a system update is available, THE Instructor_App SHALL notify the instructor
7. THE Instructor_App SHALL display a notification history

### Requirement 15: License Category Hour Requirements

**User Story:** As a system administrator, I want to configure required training hours for each license category, so that the system enforces NTSA standards.

#### Acceptance Criteria

1. THE System SHALL store required yard hours and road hours for each license category
2. THE Admin_Dashboard SHALL allow administrators to view and update hour requirements
3. WHEN calculating remaining hours for a student, THE System SHALL use the requirements for the student's license category
4. THE System SHALL validate that yard hours and road hours are non-negative integers
5. THE System SHALL apply updated hour requirements to all students in that license category
6. THE Admin_Dashboard SHALL display a table of all license categories with their hour requirements
7. THE System SHALL initialize with default NTSA-compliant hour requirements for all categories

### Requirement 16: Session History and Analytics

**User Story:** As a driving school administrator, I want to view analytics on training activities, so that I can identify trends and optimize operations.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL display total training hours logged per day, week, and month
2. THE Admin_Dashboard SHALL display average session duration by session type (yard vs road)
3. THE Admin_Dashboard SHALL display instructor performance metrics (total sessions, total hours logged)
4. THE Admin_Dashboard SHALL display student completion rates by license category
5. THE Admin_Dashboard SHALL display a chart of training activity over time
6. THE Admin_Dashboard SHALL allow filtering analytics by date range, instructor, or license category
7. THE Admin_Dashboard SHALL export analytics data as CSV files

### Requirement 17: Multi-Language Support

**User Story:** As an instructor with limited English proficiency, I want to use the app in my preferred language, so that I can operate it effectively.

#### Acceptance Criteria

1. THE Instructor_App SHALL support English and Swahili languages
2. THE Instructor_App SHALL allow instructors to select their preferred language in settings
3. WHEN the language is changed, THE Instructor_App SHALL update all UI text immediately
4. THE System SHALL store language preference per instructor
5. WHEN an instructor logs in, THE Instructor_App SHALL display in their preferred language
6. THE Admin_Dashboard SHALL support English and Swahili languages
7. THE NTSA_Report SHALL be generated in English (NTSA standard)

### Requirement 18: Data Privacy and Security

**User Story:** As a driving school administrator, I want student and instructor data to be secure, so that we comply with data protection regulations.

#### Acceptance Criteria

1. THE System SHALL encrypt all data in transit using TLS 1.3 or higher
2. THE System SHALL encrypt sensitive data at rest (passwords, personal information)
3. THE System SHALL implement role-based access control (instructor vs administrator)
4. WHEN an instructor logs in, THE System SHALL only allow access to their assigned students
5. THE System SHALL automatically log out inactive users after 30 minutes
6. THE System SHALL hash passwords using bcrypt or equivalent secure algorithm
7. THE Admin_Dashboard SHALL allow administrators to export or delete student data upon request

### Requirement 19: System Performance and Scalability

**User Story:** As a driving school administrator, I want the system to handle multiple concurrent users, so that all instructors can work simultaneously without performance issues.

#### Acceptance Criteria

1. THE Instructor_App SHALL load the student list within 2 seconds on a 4G connection
2. THE Instructor_App SHALL start GPS tracking within 5 seconds of session initiation
3. THE Admin_Dashboard SHALL load the main dashboard within 3 seconds
4. THE System SHALL support at least 100 concurrent active Training_Sessions
5. THE Sync_Engine SHALL upload a Training_Session record within 10 seconds on a 4G connection
6. THE Admin_Dashboard SHALL generate an NTSA_Report within 5 seconds for students with up to 100 sessions
7. THE System SHALL maintain performance standards with up to 1000 students and 50 instructors per driving school

### Requirement 20: Error Handling and User Feedback

**User Story:** As an instructor, I want clear error messages when something goes wrong, so that I can understand and resolve issues quickly.

#### Acceptance Criteria

1. WHEN an error occurs, THE Instructor_App SHALL display a user-friendly error message in the selected language
2. THE Instructor_App SHALL log detailed error information for debugging purposes
3. WHEN GPS tracking fails, THE Instructor_App SHALL notify the instructor and suggest troubleshooting steps
4. WHEN QR code scanning fails, THE Instructor_App SHALL display the reason (invalid code, unassigned student, etc.)
5. WHEN synchronization fails, THE Instructor_App SHALL display the number of pending sessions and retry option
6. THE Instructor_App SHALL provide a "Report Problem" feature to send error logs to support
7. THE Admin_Dashboard SHALL display system health status and recent errors
