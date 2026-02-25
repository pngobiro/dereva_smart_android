# Implementation Plan: B2B Instructor Digital Logbook

## Overview

This implementation plan breaks down the B2B Instructor Digital Logbook into discrete coding tasks. The approach follows a layered architecture: data layer first (models, database, storage), then business logic (controllers, services), and finally UI integration. Each task builds incrementally, with property-based tests integrated close to implementation to catch errors early.

The implementation uses Flutter/Dart for the mobile app with local SQLite storage and Firebase/Supabase for the backend service.

## Tasks

- [ ] 1. Set up project structure and dependencies
  - Create Flutter project with proper directory structure (lib/models, lib/services, lib/controllers, lib/ui, lib/utils)
  - Add dependencies: sqflite, flutter_secure_storage, geolocator, qr_code_scanner, crypto, pdf, fast_check (or equivalent Dart property testing library)
  - Configure platform-specific permissions (location, camera, storage) in AndroidManifest.xml and Info.plist
  - Set up testing framework and property-based testing library
  - _Requirements: All_

- [ ] 2. Implement core data models and types
  - [ ] 2.1 Create type definitions and enums
    - Define SessionId, StudentId, VehicleId, InstructorId as type aliases
    - Implement SessionType, Skill, WeatherCondition, VehicleCondition, LicenseCategory enums
    - Implement Result<T> and Option<T> types for error handling
    - _Requirements: 1.6, 4.1, 4.2, 4.4, 4.5, 3.1_
  
  - [ ] 2.2 Implement GPSStamp model
    - Create GPSStamp class with latitude, longitude, timestamp, accuracy, and encryptedData fields
    - Add JSON serialization/deserialization methods
    - _Requirements: 1.1, 1.2, 1.7_
  
  - [ ] 2.3 Implement SessionRecord model
    - Create SessionRecord class with all required fields (id, studentId, vehicleId, instructorId, sessionType, startStamp, endStamp, locationTrail, duration, details, cryptographicHash, createdAt, synced)
    - Add JSON serialization/deserialization methods
    - Implement SessionDetails nested class
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.6, 4.6_
  
  - [ ] 2.4 Implement Student and CheckInRecord models
    - Create Student, StudentDetail, CheckInRecord classes
    - Add JSON serialization/deserialization methods
    - _Requirements: 3.1, 3.2, 3.3, 2.5, 2.6_
  
  - [ ]* 2.5 Write property tests for data model serialization
    - **Property: Serialization round trip** - For any valid data model instance, serializing to JSON then deserializing should produce an equivalent object
    - **Validates: Requirements 1.1, 1.2, 2.5, 3.1**

- [ ] 3. Implement local database layer
  - [ ] 3.1 Create database schema and migrations
    - Implement SQLite schema for sessions, gps_trail, students, check_ins, sync_queue, audit_log tables
    - Create database migration logic
    - _Requirements: 1.1, 1.2, 1.3, 2.6, 3.6, 6.2_
  
  - [ ] 3.2 Implement database access layer
    - Create DatabaseHelper class with CRUD operations for all tables
    - Implement query methods for sessions by student, sync queue operations, audit log insertion
    - Add transaction support for atomic operations
    - _Requirements: 1.1, 1.2, 1.3, 3.3, 5.6, 6.2_
  
  - [ ]* 3.3 Write unit tests for database operations
    - Test CRUD operations for each table
    - Test transaction rollback on errors
    - Test query methods with various filters
    - _Requirements: 1.1, 1.2, 3.3, 6.2_

- [ ] 4. Implement CryptoService
  - [ ] 4.1 Create encryption and hashing methods
    - Implement AES-256 encryption/decryption using dart:crypto
    - Implement SHA-256 hashing for session records and check-ins
    - Implement session hash generation that includes all immutable fields
    - Implement hash verification method
    - _Requirements: 1.7, 2.5, 8.1, 8.3, 8.4_
  
  - [ ]* 4.2 Write property test for encryption round-trip
    - **Property 6: Data Encryption** - For any plaintext string, encrypting then decrypting should return the original string
    - **Validates: Requirements 1.7, 8.1**
  
  - [ ]* 4.3 Write property test for tamper detection
    - **Property 28: Cryptographic Hash Generation** - For any session record, modifying any field should result in a different hash
    - **Property 29: Tamper Detection and Rejection** - For any session with mismatched hash, verification should fail
    - **Validates: Requirements 8.3, 8.4**

- [ ] 5. Implement GPSTracker service
  - [ ] 5.1 Create GPS tracking functionality
    - Implement startTracking, stopTracking, getCurrentStamp methods using geolocator package
    - Implement location stream that emits GPS stamps every 10 seconds
    - Implement accuracy checking (isAccuracyAcceptable method)
    - Add GPS stamp encryption using CryptoService
    - Handle location permissions and errors
    - _Requirements: 1.1, 1.2, 1.3, 1.5, 1.7, 10.1_
  
  - [ ]* 5.2 Write property test for GPS stamp creation
    - **Property 1: GPS Stamp Creation at Session Boundaries** - For any tracking start/stop event, a valid GPS stamp with all required fields must be created
    - **Validates: Requirements 1.1, 1.2**
  
  - [ ]* 5.3 Write property test for low accuracy handling
    - **Property 4: Low Accuracy GPS Handling** - For any GPS stamp with accuracy below threshold, a low accuracy flag must be set
    - **Validates: Requirements 1.5**
  
  - [ ]* 5.4 Write unit tests for GPS error handling
    - Test behavior when location services are disabled
    - Test behavior when GPS signal is lost
    - Test permission denied scenarios
    - _Requirements: 1.1, 1.2, 1.5_

- [ ] 6. Implement QRValidator service
  - [ ] 6.1 Create QR code validation functionality
    - Implement validateStudentQR and validateVehicleQR methods
    - Implement QR code parsing and format validation
    - Implement createCheckIn method with digital signature generation
    - Handle invalid QR codes with proper error messages
    - _Requirements: 2.1, 2.2, 2.3, 2.5, 2.6_
  
  - [ ]* 6.2 Write property test for QR check-in processing
    - **Property 7: QR Check-In Processing** - For any valid QR code, scanning must decode the identifier and create a check-in record with timestamp and signature
    - **Validates: Requirements 2.1, 2.2, 2.5, 2.6**
  
  - [ ]* 6.3 Write property test for invalid QR rejection
    - **Property 8: Invalid QR Code Rejection** - For any invalid QR code data, the system must return an error
    - **Validates: Requirements 2.3**

- [ ] 7. Implement SessionController
  - [ ] 7.1 Create session lifecycle management
    - Implement startSession method with dual check-in validation
    - Implement endSession method with duration calculation
    - Implement getActiveSession, resumeSession, discardSession methods
    - Integrate GPSTracker for automatic GPS stamp capture
    - Implement session state persistence every 30 seconds
    - Store sessions in local database with encryption
    - _Requirements: 1.1, 1.2, 1.4, 2.4, 4.6, 9.1, 9.2, 9.5_
  
  - [ ]* 7.2 Write property test for duration calculation
    - **Property 3: Duration Calculation Integrity** - For any session, duration must equal the difference between end and start timestamps
    - **Validates: Requirements 1.4**
  
  - [ ]* 7.3 Write property test for dual check-in requirement
    - **Property 9: Dual Check-In Requirement** - For any session start attempt, the session must only be created if both student and vehicle check-ins are completed
    - **Validates: Requirements 2.4**
  
  - [ ]* 7.4 Write property test for single active session constraint
    - **Property 32: Single Active Session Constraint** - For any session start attempt, if another session is already active, the new session must be rejected
    - **Validates: Requirements 9.5**
  
  - [ ]* 7.5 Write property test for session state persistence
    - **Property 30: Session State Persistence** - For any active session, after simulated crash and restart, the session state must be fully restored
    - **Validates: Requirements 9.1, 9.2**
  
  - [ ]* 7.6 Write property test for session type preservation
    - **Property 5: Session Type Preservation** - For any session, the session type must remain immutable after creation
    - **Validates: Requirements 1.6**
  
  - [ ]* 7.7 Write property test for session details completeness
    - **Property 16: Session Details Completeness** - For any completed session, all required details must be present in the session record
    - **Validates: Requirements 4.1, 4.2, 4.3, 4.4, 4.5, 4.6**

- [ ] 8. Checkpoint - Core session management complete
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 9. Implement StudentRepository
  - [ ] 9.1 Create student data management
    - Implement getAssignedStudents, getStudentsByCategory, getStudentDetail methods
    - Implement local caching with 100 student limit
    - Implement cache eviction strategy (LRU)
    - Query training history from sessions table
    - Calculate completed hours, remaining hours
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.6, 10.6_
  
  - [ ]* 9.2 Write property test for student grouping
    - **Property 10: Student Grouping by Category** - For any list of students, when grouped by category, all students in each group must share the same category
    - **Validates: Requirements 3.1**
  
  - [ ]* 9.3 Write property test for remaining hours calculation
    - **Property 13: Remaining Hours Calculation** - For any student, remaining hours must equal category requirement minus completed hours
    - **Validates: Requirements 3.4**
  
  - [ ]* 9.4 Write property test for training history completeness
    - **Property 12: Training History Completeness** - For any student, all past sessions must be included in the training history
    - **Validates: Requirements 3.3**
  
  - [ ]* 9.5 Write property test for cache size limit
    - **Property 34: Student Cache Size Limit** - For any instructor, cached student data must never exceed 100 students
    - **Validates: Requirements 10.6**
  
  - [ ]* 9.6 Write property test for offline data access
    - **Property 15: Offline Student Data Access** - For any synced student, data must be accessible from cache when offline
    - **Validates: Requirements 3.6**

- [ ] 10. Implement SyncManager
  - [ ] 10.1 Create offline queue and sync functionality
    - Implement queueSession method to add sessions to sync queue
    - Implement syncAll method with network detection
    - Implement exponential backoff retry logic
    - Implement sync status tracking (pending count, last sync time, online status)
    - Implement automatic sync on network reconnection
    - Compress session data before queueing
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 10.2_
  
  - [ ]* 10.2 Write property test for offline session creation
    - **Property 20: Offline Session Creation** - For any session created offline, it must be stored in the sync queue with full functionality
    - **Validates: Requirements 6.1, 6.2**
  
  - [ ]* 10.3 Write property test for offline data round-trip integrity
    - **Property 22: Offline Data Round-Trip Integrity** - For any session created offline, after syncing, GPS stamps and timestamps must be identical to original values
    - **Validates: Requirements 6.4**
  
  - [ ]* 10.4 Write property test for sync retry backoff
    - **Property 23: Sync Retry with Exponential Backoff** - For any failed sync, retry attempts must follow exponential backoff timing
    - **Validates: Requirements 6.5**
  
  - [ ]* 10.5 Write property test for sync status accuracy
    - **Property 24: Sync Status Accuracy** - For any point in time, sync status must accurately reflect pending uploads, last sync time, and online status
    - **Validates: Requirements 6.6**
  
  - [ ]* 10.6 Write property test for session compression
    - **Property 33: Session Record Compression** - For any session stored locally, the data must be compressed
    - **Validates: Requirements 10.2**

- [ ] 11. Implement ReportGenerator
  - [ ] 11.1 Create NTSA-compliant report generation
    - Implement generateStudentReport method that compiles all sessions for a student
    - Calculate total yard hours and road hours separately
    - Include all GPS stamps, timestamps, durations, instructor details, digital signatures, and check-in data
    - Implement exportToPDF method using pdf package
    - Implement audit trail logging for report generation
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7_
  
  - [ ]* 11.2 Write property test for report data completeness
    - **Property 17: Report Data Completeness** - For any generated report, it must include all session records, GPS stamps, timestamps, durations, signatures, and check-in data
    - **Validates: Requirements 5.1, 5.2, 5.4, 5.7**
  
  - [ ]* 11.3 Write property test for practice hours segregation
    - **Property 18: Practice Hours Segregation** - For any report, yard and road hours must be calculated separately and correctly sum all sessions of each type
    - **Validates: Requirements 5.3**
  
  - [ ]* 11.4 Write property test for audit trail logging
    - **Property 19: Audit Trail Logging** - For any report generation, an audit log entry must be created
    - **Validates: Requirements 5.6**
  
  - [ ]* 11.5 Write unit test for PDF export
    - Test that PDF export produces a valid PDF file
    - _Requirements: 5.5_

- [ ] 12. Checkpoint - Backend integration and reporting complete
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 13. Implement backend API client
  - [ ] 13.1 Create API service for backend communication
    - Implement session upload endpoint (POST /api/v1/sessions)
    - Implement student sync endpoint (GET /api/v1/instructors/{id}/students)
    - Implement report generation endpoint (POST /api/v1/reports/student/{id})
    - Configure TLS 1.3 for all requests
    - Implement authentication token management with secure storage
    - Handle network errors and timeouts
    - _Requirements: 3.5, 6.3, 8.2, 8.7_
  
  - [ ]* 13.2 Write property test for automatic sync on reconnection
    - **Property 21: Automatic Sync on Reconnection** - For any queued session, when network is restored, the session must be automatically uploaded
    - **Validates: Requirements 6.3**
  
  - [ ]* 13.3 Write property test for student data synchronization
    - **Property 14: Student Data Synchronization** - For any student added on backend, the student must appear in the app after next sync
    - **Validates: Requirements 3.5**
  
  - [ ]* 13.4 Write unit tests for network error handling
    - Test connection timeout handling
    - Test server unavailable scenarios
    - Test authentication failure handling
    - _Requirements: 6.3, 6.5_

- [ ] 14. Implement UI components
  - [ ] 14.1 Create session management UI
    - Build session start screen with QR scanner integration
    - Build active session screen with timer display and GPS status
    - Build session end screen with details input form
    - Implement large buttons with icons and clear labels
    - Use consistent color scheme (green=success, red=error, yellow=warning)
    - Ensure visual feedback within 200ms for all actions
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.7_
  
  - [ ]* 14.2 Write property test for timer accuracy
    - **Property 26: Active Session Timer Accuracy** - For any active session, displayed elapsed time must match actual time difference
    - **Validates: Requirements 7.4**
  
  - [ ]* 14.3 Write property test for UI response time
    - **Property 27: UI Response Time** - For any user action, visual feedback must be provided within 200ms
    - **Validates: Requirements 7.7**
  
  - [ ] 14.4 Create student management UI
    - Build student list screen with category grouping
    - Build student detail screen with training history
    - Display student name, category, completed hours, remaining hours
    - Implement pull-to-refresh for student sync
    - _Requirements: 3.1, 3.2, 3.3, 3.5_
  
  - [ ]* 14.5 Write property test for student display completeness
    - **Property 11: Student Display Completeness** - For any student displayed, the UI must contain name, category, completed hours, and remaining hours
    - **Validates: Requirements 3.2**
  
  - [ ] 14.6 Create report generation UI
    - Build report selection screen
    - Build report preview screen
    - Implement PDF export and sharing
    - Display sync status indicator
    - _Requirements: 5.1, 5.5, 6.6_

- [ ] 15. Implement background services and lifecycle management
  - [ ] 15.1 Create background GPS tracking
    - Implement background location tracking when app is backgrounded
    - Implement session state auto-save every 30 seconds
    - Handle app crashes and restore incomplete sessions on launch
    - _Requirements: 9.1, 9.2, 9.4_
  
  - [ ]* 15.2 Write property test for background GPS tracking
    - **Property 31: Background GPS Tracking** - For any active session, when app is backgrounded, GPS stamps must continue to be recorded
    - **Validates: Requirements 9.4**
  
  - [ ] 15.3 Implement resource management
    - Implement low battery detection and warning
    - Implement storage monitoring and cleanup
    - Implement auto-lock after 5 minutes idle
    - _Requirements: 9.3, 10.3, 8.6_
  
  - [ ]* 15.4 Write unit tests for resource management
    - Test low battery warning display
    - Test storage cleanup when exceeding 80% capacity
    - Test auto-lock after idle timeout
    - _Requirements: 9.3, 10.3, 8.6_

- [ ] 16. Implement authentication and security
  - [ ] 16.1 Create authentication flow
    - Implement biometric/PIN authentication using local_auth package
    - Implement secure token storage using flutter_secure_storage
    - Implement auto-lock on idle
    - _Requirements: 8.5, 8.6, 8.7_
  
  - [ ]* 16.2 Write unit tests for authentication
    - Test biometric authentication flow
    - Test PIN authentication flow
    - Test auto-lock behavior
    - _Requirements: 8.5, 8.6_

- [ ] 17. Integration and end-to-end testing
  - [ ]* 17.1 Write integration tests for complete session flow
    - Test: Start session → QR check-ins → GPS tracking → End session → Sync
    - Test: Offline session creation → Network restoration → Automatic sync
    - Test: Session crash recovery → Resume session
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 6.1, 6.3, 9.2_
  
  - [ ]* 17.2 Write integration tests for student management flow
    - Test: Sync students → View list → View details → Generate report
    - Test: Offline student access → Online sync → Updated data
    - _Requirements: 3.1, 3.2, 3.3, 3.5, 3.6, 5.1_
  
  - [ ]* 17.3 Write integration tests for error scenarios
    - Test: Invalid QR codes → Error display → Retry
    - Test: GPS signal loss → Warning → Continue with degraded accuracy
    - Test: Network failure → Queue → Retry with backoff
    - _Requirements: 1.5, 2.3, 6.5_

- [ ] 18. Final checkpoint - Complete system integration
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 19. Performance optimization and polish
  - [ ] 19.1 Optimize app performance
    - Profile and optimize app launch time (target: <3 seconds)
    - Optimize GPS polling for battery efficiency
    - Optimize database queries with indexes
    - Implement lazy loading for student lists
    - _Requirements: 10.1, 10.4, 10.5_
  
  - [ ] 19.2 Add error telemetry and logging
    - Implement error reporting to backend
    - Add debug logging for troubleshooting
    - Implement crash reporting
    - _Requirements: All_
  
  - [ ] 19.3 Polish UI and UX
    - Add loading indicators for all async operations
    - Add empty states for lists
    - Add onboarding flow for first-time users
    - Ensure all error messages are user-friendly
    - _Requirements: 7.1, 7.2, 7.6_

- [ ] 20. Documentation and deployment preparation
  - Create user manual for instructors
  - Create API documentation for backend integration
  - Create deployment guide for app stores
  - Document testing procedures and property test results
  - _Requirements: All_

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at key milestones
- Property tests validate universal correctness properties across all inputs
- Unit tests validate specific examples, edge cases, and error conditions
- The implementation follows a bottom-up approach: data layer → business logic → UI
- Background services and lifecycle management are implemented after core functionality
- Integration tests verify end-to-end flows after all components are complete
