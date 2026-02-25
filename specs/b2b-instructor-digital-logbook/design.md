# Design Document: B2B Instructor Digital Logbook

## Overview

The B2B Instructor Digital Logbook is a Flutter-based mobile application that provides driving school instructors with a tamper-proof system for logging student practical training hours. The system uses GPS verification, QR code authentication, and cryptographic signatures to create an immutable audit trail that satisfies NTSA compliance requirements.

The architecture follows a mobile-first, offline-capable design pattern where the mobile app serves as the primary data collection point with local persistence and background synchronization to a cloud backend (Firebase or Supabase). The design prioritizes data integrity, regulatory compliance, and usability for users with varying technical literacy.

## Architecture

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                    Instructor Mobile App                     │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              Presentation Layer (Flutter)              │ │
│  │  - Session Management UI                               │ │
│  │  - Student List UI                                     │ │
│  │  - QR Scanner UI                                       │ │
│  │  - Report Generation UI                                │ │
│  └────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                  Business Logic Layer                  │ │
│  │  - Session Controller                                  │ │
│  │  - GPS Tracker                                         │ │
│  │  - QR Validator                                        │ │
│  │  - Sync Manager                                        │ │
│  │  - Crypto Service                                      │ │
│  └────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                   Data Layer                           │ │
│  │  - Local Database (SQLite/Hive)                        │ │
│  │  - Secure Storage (FlutterSecureStorage)               │ │
│  │  - Sync Queue                                          │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ HTTPS/TLS 1.3
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Backend Service                           │
│                  (Firebase/Supabase)                         │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  - Authentication Service                              │ │
│  │  - Session Storage (Firestore/PostgreSQL)              │ │
│  │  - Student Management                                  │ │
│  │  - Report Generator                                    │ │
│  │  - Audit Log                                           │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow

1. **Session Start Flow:**
   - Instructor authenticates → Scans student QR → Scans vehicle QR → Captures GPS stamp → Creates session record → Stores locally

2. **Session Active Flow:**
   - GPS tracker polls location every 10s → Appends GPS stamps to session → Persists state every 30s → Updates UI timer

3. **Session End Flow:**
   - Instructor ends session → Captures final GPS stamp → Calculates duration → Instructor adds notes/details → Generates cryptographic hash → Stores complete session → Queues for sync

4. **Sync Flow:**
   - Network detector monitors connectivity → When online, sync manager uploads queued sessions → Backend validates and stores → Confirms receipt → App marks as synced → Optionally purges old synced data

## Components and Interfaces

### Session Controller

The Session Controller manages the lifecycle of training sessions and coordinates between GPS tracking, QR validation, and data persistence.

```dart
class SessionController {
  // Start a new training session
  Future<Result<SessionId>> startSession({
    required StudentId studentId,
    required VehicleId vehicleId,
    required SessionType sessionType,
  });
  
  // End the current active session
  Future<Result<SessionRecord>> endSession({
    required SessionId sessionId,
    required SessionDetails details,
  });
  
  // Get the currently active session, if any
  Option<ActiveSession> getActiveSession();
  
  // Resume an incomplete session after app restart
  Future<Result<ActiveSession>> resumeSession(SessionId sessionId);
  
  // Discard an incomplete session
  Future<Result<void>> discardSession(SessionId sessionId);
}

enum SessionType { yardPractice, roadPractice }

class SessionDetails {
  List<Skill> skillsPracticed;
  String instructorNotes;
  WeatherCondition weather;
  VehicleCondition vehicleCondition;
}

class SessionRecord {
  SessionId id;
  StudentId studentId;
  VehicleId vehicleId;
  InstructorId instructorId;
  SessionType sessionType;
  GPSStamp startStamp;
  GPSStamp endStamp;
  List<GPSStamp> locationTrail;
  Duration duration;
  SessionDetails details;
  String cryptographicHash;
  DateTime createdAt;
  bool synced;
}
```

### GPS Tracker

The GPS Tracker handles location services, accuracy validation, and GPS stamp creation.

```dart
class GPSTracker {
  // Start tracking GPS for a session
  Future<Result<void>> startTracking(SessionId sessionId);
  
  // Stop tracking GPS
  Future<Result<void>> stopTracking();
  
  // Get current GPS stamp
  Future<Result<GPSStamp>> getCurrentStamp();
  
  // Check if GPS accuracy is acceptable
  bool isAccuracyAcceptable(double accuracyMeters);
  
  // Stream of GPS stamps during active tracking
  Stream<GPSStamp> get locationStream;
}

class GPSStamp {
  double latitude;
  double longitude;
  DateTime timestamp;
  double accuracyMeters;
  String encryptedData; // Encrypted lat/lon/timestamp
}
```

### QR Validator

The QR Validator handles QR code scanning, decoding, and validation.

```dart
class QRValidator {
  // Scan and validate a student QR code
  Future<Result<StudentId>> validateStudentQR(String qrData);
  
  // Scan and validate a vehicle QR code
  Future<Result<VehicleId>> validateVehicleQR(String qrData);
  
  // Generate check-in record with digital signature
  CheckInRecord createCheckIn({
    required String qrData,
    required CheckInType type,
  });
}

enum CheckInType { student, vehicle }

class CheckInRecord {
  String qrData;
  CheckInType type;
  DateTime timestamp;
  String digitalSignature; // Cryptographic hash
}
```

### Crypto Service

The Crypto Service provides encryption, hashing, and digital signature generation.

```dart
class CryptoService {
  // Encrypt sensitive data (GPS coordinates, timestamps)
  String encrypt(String plaintext);
  
  // Decrypt sensitive data
  String decrypt(String ciphertext);
  
  // Generate cryptographic hash for session record
  String generateSessionHash(SessionRecord session);
  
  // Verify session integrity
  bool verifySessionIntegrity(SessionRecord session);
  
  // Generate digital signature for check-in
  String generateCheckInSignature(CheckInRecord checkIn);
}
```

### Sync Manager

The Sync Manager handles offline queueing and background synchronization with the backend.

```dart
class SyncManager {
  // Queue a session for upload
  Future<Result<void>> queueSession(SessionRecord session);
  
  // Attempt to sync all queued sessions
  Future<Result<SyncResult>> syncAll();
  
  // Get sync status
  SyncStatus getSyncStatus();
  
  // Stream of sync events
  Stream<SyncEvent> get syncEvents;
}

class SyncStatus {
  int pendingCount;
  DateTime? lastSuccessfulSync;
  bool isOnline;
  bool isSyncing;
}

class SyncResult {
  int successCount;
  int failureCount;
  List<SyncError> errors;
}
```

### Student Repository

The Student Repository manages student data with local caching and backend synchronization.

```dart
class StudentRepository {
  // Get all students assigned to the instructor
  Future<Result<List<Student>>> getAssignedStudents();
  
  // Get students filtered by license category
  Future<Result<List<Student>>> getStudentsByCategory(LicenseCategory category);
  
  // Get detailed student information including progress
  Future<Result<StudentDetail>> getStudentDetail(StudentId studentId);
  
  // Sync student data from backend
  Future<Result<void>> syncStudents();
  
  // Get cached student data (for offline use)
  List<Student> getCachedStudents();
}

class Student {
  StudentId id;
  String name;
  LicenseCategory category;
  double completedHours;
  double requiredHours;
}

class StudentDetail extends Student {
  List<SessionRecord> trainingHistory;
  double yardHours;
  double roadHours;
  double remainingHours;
}

enum LicenseCategory { A1, A2, A3, B1, B2, B3, C, D, E, F, G }
```

### Report Generator

The Report Generator creates NTSA-compliant reports with digital signatures and verification data.

```dart
class ReportGenerator {
  // Generate NTSA-compliant report for a student
  Future<Result<Report>> generateStudentReport(StudentId studentId);
  
  // Export report to PDF
  Future<Result<File>> exportToPDF(Report report);
  
  // Get audit trail for report generation
  List<AuditEvent> getAuditTrail(StudentId studentId);
}

class Report {
  StudentId studentId;
  String studentName;
  LicenseCategory category;
  List<SessionRecord> sessions;
  double totalYardHours;
  double totalRoadHours;
  double totalHours;
  List<String> digitalSignatures;
  DateTime generatedAt;
  String reportHash;
}

class AuditEvent {
  String eventType;
  DateTime timestamp;
  String userId;
  Map<String, dynamic> metadata;
}
```

## Data Models

### Core Data Structures

```dart
// Unique identifiers
typedef SessionId = String;
typedef StudentId = String;
typedef VehicleId = String;
typedef InstructorId = String;

// Session state for active sessions
class ActiveSession {
  SessionId id;
  StudentId studentId;
  VehicleId vehicleId;
  SessionType sessionType;
  GPSStamp startStamp;
  List<GPSStamp> locationTrail;
  DateTime startTime;
  Duration elapsed;
}

// Skills that can be practiced during a session
enum Skill {
  steering,
  braking,
  acceleration,
  parking,
  reversing,
  hillStart,
  emergencyStop,
  laneChanging,
  roundabouts,
  trafficSigns,
  defensiveDriving,
}

// Weather conditions
enum WeatherCondition {
  clear,
  cloudy,
  rainy,
  foggy,
}

// Vehicle condition
enum VehicleCondition {
  excellent,
  good,
  fair,
  needsMaintenance,
}

// Result type for error handling
sealed class Result<T> {
  const Result();
}

class Success<T> extends Result<T> {
  final T value;
  const Success(this.value);
}

class Failure<T> extends Result<T> {
  final String error;
  final Exception? exception;
  const Failure(this.error, [this.exception]);
}

// Option type for nullable values
sealed class Option<T> {
  const Option();
}

class Some<T> extends Option<T> {
  final T value;
  const Some(this.value);
}

class None<T> extends Option<T> {
  const None();
}
```

### Local Database Schema

```sql
-- Sessions table
CREATE TABLE sessions (
  id TEXT PRIMARY KEY,
  student_id TEXT NOT NULL,
  vehicle_id TEXT NOT NULL,
  instructor_id TEXT NOT NULL,
  session_type TEXT NOT NULL,
  start_latitude REAL NOT NULL,
  start_longitude REAL NOT NULL,
  start_timestamp INTEGER NOT NULL,
  start_accuracy REAL NOT NULL,
  start_encrypted TEXT NOT NULL,
  end_latitude REAL,
  end_longitude REAL,
  end_timestamp INTEGER,
  end_accuracy REAL,
  end_encrypted TEXT,
  duration_seconds INTEGER,
  skills_practiced TEXT, -- JSON array
  instructor_notes TEXT,
  weather TEXT,
  vehicle_condition TEXT,
  cryptographic_hash TEXT NOT NULL,
  created_at INTEGER NOT NULL,
  synced INTEGER DEFAULT 0,
  FOREIGN KEY (student_id) REFERENCES students(id)
);

-- GPS trail table
CREATE TABLE gps_trail (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  session_id TEXT NOT NULL,
  latitude REAL NOT NULL,
  longitude REAL NOT NULL,
  timestamp INTEGER NOT NULL,
  accuracy REAL NOT NULL,
  encrypted_data TEXT NOT NULL,
  FOREIGN KEY (session_id) REFERENCES sessions(id)
);

-- Students table (cached)
CREATE TABLE students (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  license_category TEXT NOT NULL,
  completed_hours REAL DEFAULT 0,
  required_hours REAL NOT NULL,
  last_synced INTEGER NOT NULL
);

-- Check-ins table
CREATE TABLE check_ins (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  session_id TEXT NOT NULL,
  qr_data TEXT NOT NULL,
  check_in_type TEXT NOT NULL,
  timestamp INTEGER NOT NULL,
  digital_signature TEXT NOT NULL,
  FOREIGN KEY (session_id) REFERENCES sessions(id)
);

-- Sync queue table
CREATE TABLE sync_queue (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  session_id TEXT NOT NULL,
  retry_count INTEGER DEFAULT 0,
  last_attempt INTEGER,
  error_message TEXT,
  FOREIGN KEY (session_id) REFERENCES sessions(id)
);

-- Audit log table
CREATE TABLE audit_log (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  event_type TEXT NOT NULL,
  timestamp INTEGER NOT NULL,
  user_id TEXT NOT NULL,
  metadata TEXT -- JSON
);
```

### Backend API Contracts

```typescript
// Session upload endpoint
POST /api/v1/sessions
Request: {
  sessionId: string;
  studentId: string;
  vehicleId: string;
  instructorId: string;
  sessionType: "yard" | "road";
  startStamp: GPSStampDTO;
  endStamp: GPSStampDTO;
  locationTrail: GPSStampDTO[];
  duration: number; // seconds
  details: SessionDetailsDTO;
  cryptographicHash: string;
  checkIns: CheckInDTO[];
  createdAt: string; // ISO 8601
}
Response: {
  success: boolean;
  sessionId: string;
  serverTimestamp: string;
}

// Student sync endpoint
GET /api/v1/instructors/{instructorId}/students
Response: {
  students: StudentDTO[];
  lastModified: string;
}

// Report generation endpoint
POST /api/v1/reports/student/{studentId}
Response: {
  reportId: string;
  reportUrl: string;
  expiresAt: string;
}
```


## Correctness Properties

A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.

### Property 1: GPS Stamp Creation at Session Boundaries

*For any* training session, when the session starts or ends, a GPS stamp must be created containing valid latitude, longitude, timestamp, accuracy metadata, and encrypted data.

**Validates: Requirements 1.1, 1.2**

### Property 2: GPS Trail Accumulation

*For any* active training session, the location trail must grow monotonically over time, with GPS stamps recorded at regular intervals (approximately every 10 seconds).

**Validates: Requirements 1.3, 10.1**

### Property 3: Duration Calculation Integrity

*For any* completed training session, the duration must equal the difference between the end timestamp and start timestamp, with no manual time entry allowed.

**Validates: Requirements 1.4**

### Property 4: Low Accuracy GPS Handling

*For any* GPS stamp with accuracy below the acceptable threshold (e.g., > 50 meters), the system must flag the session with a low accuracy warning.

**Validates: Requirements 1.5**

### Property 5: Session Type Preservation

*For any* training session, the session type (yard practice or road practice) must be stored with the session record and remain immutable after creation.

**Validates: Requirements 1.6**

### Property 6: Data Encryption

*For any* session record stored locally, all sensitive data (GPS coordinates, timestamps) must be encrypted using AES-256, and the encrypted data field must be non-empty.

**Validates: Requirements 1.7, 8.1**

### Property 7: QR Check-In Processing

*For any* valid QR code (student or vehicle), scanning must correctly decode the identifier, create a check-in record with timestamp and digital signature, and associate it with the current session.

**Validates: Requirements 2.1, 2.2, 2.5, 2.6**

### Property 8: Invalid QR Code Rejection

*For any* invalid or malformed QR code data, the system must return an error and prevent session start.

**Validates: Requirements 2.3**

### Property 9: Dual Check-In Requirement

*For any* session start attempt, the session must only be created if both student and vehicle check-ins have been completed successfully.

**Validates: Requirements 2.4**

### Property 10: Student Grouping by Category

*For any* list of students, when displayed, students must be correctly grouped by their license category with all students in each group sharing the same category.

**Validates: Requirements 3.1**

### Property 11: Student Display Completeness

*For any* student displayed in the UI, the rendered output must contain the student's name, license category, completed hours, and remaining required hours.

**Validates: Requirements 3.2**

### Property 12: Training History Completeness

*For any* student detail view, all past training sessions for that student must be included in the displayed history.

**Validates: Requirements 3.3**

### Property 13: Remaining Hours Calculation

*For any* student, the remaining required hours must equal the category-specific requirement minus the completed hours.

**Validates: Requirements 3.4**

### Property 14: Student Data Synchronization

*For any* student added to an instructor's roster on the backend, that student must appear in the instructor's app after the next successful sync operation.

**Validates: Requirements 3.5**

### Property 15: Offline Student Data Access

*For any* student that has been synced, the student data must be accessible from local cache even when the device is offline.

**Validates: Requirements 3.6**

### Property 16: Session Details Completeness

*For any* completed training session, the session record must contain all required details: lesson type, skills practiced, instructor notes, weather condition, vehicle condition, and immutable timestamps.

**Validates: Requirements 4.1, 4.2, 4.3, 4.4, 4.5, 4.6**

### Property 17: Report Data Completeness

*For any* generated student report, the report must include all session records for that student, all GPS stamps, timestamps, session durations, instructor details, digital signatures for each session, and QR check-in data.

**Validates: Requirements 5.1, 5.2, 5.4, 5.7**

### Property 18: Practice Hours Segregation

*For any* student report, the total yard practice hours and road practice hours must be calculated separately and correctly sum all sessions of each type.

**Validates: Requirements 5.3**

### Property 19: Audit Trail Logging

*For any* report generation event, an audit log entry must be created with event type, timestamp, user ID, and metadata.

**Validates: Requirements 5.6**

### Property 20: Offline Session Creation

*For any* session creation attempt when the device is offline, the session must be created successfully with full functionality and stored in the local sync queue.

**Validates: Requirements 6.1, 6.2**

### Property 21: Automatic Sync on Reconnection

*For any* queued session, when network connection is restored, the session must be automatically uploaded to the backend service.

**Validates: Requirements 6.3**

### Property 22: Offline Data Round-Trip Integrity

*For any* session created offline, after syncing to the backend, the GPS stamps and timestamps must be identical to the original values (no modification during sync).

**Validates: Requirements 6.4**

### Property 23: Sync Retry with Exponential Backoff

*For any* failed sync operation, retry attempts must follow exponential backoff timing (e.g., 1s, 2s, 4s, 8s, ...) until successful.

**Validates: Requirements 6.5**

### Property 24: Sync Status Accuracy

*For any* point in time, the displayed sync status must accurately reflect the number of pending uploads, last successful sync time, online status, and whether a sync is currently in progress.

**Validates: Requirements 6.6**

### Property 25: Cached Data Fallback

*For any* offline state, student data queries must return cached data from the last successful sync without errors.

**Validates: Requirements 6.7**

### Property 26: Active Session Timer Accuracy

*For any* active training session, the displayed elapsed time must match the actual time difference between the current time and the session start time.

**Validates: Requirements 7.4**

### Property 27: UI Response Time

*For any* user action in the app, visual feedback must be provided within 200 milliseconds.

**Validates: Requirements 7.7**

### Property 28: Cryptographic Hash Generation

*For any* session record, a cryptographic hash must be generated and stored, and any modification to the session data must result in a different hash (tamper detection).

**Validates: Requirements 8.3**

### Property 29: Tamper Detection and Rejection

*For any* session record, if the stored hash does not match the computed hash of the current data, the system must reject the record and log a security event.

**Validates: Requirements 8.4**

### Property 30: Session State Persistence

*For any* active training session, the session state must be persisted to local storage at regular intervals (every 30 seconds), and after an app crash, the session state must be fully restored on next launch.

**Validates: Requirements 9.1, 9.2**

### Property 31: Background GPS Tracking

*For any* active session, when the app is backgrounded, GPS stamps must continue to be recorded and added to the location trail.

**Validates: Requirements 9.4**

### Property 32: Single Active Session Constraint

*For any* session start attempt, if another session is already active, the new session start must be rejected.

**Validates: Requirements 9.5**

### Property 33: Session Record Compression

*For any* session record stored locally, the data must be compressed before storage to minimize storage usage.

**Validates: Requirements 10.2**

### Property 34: Student Cache Size Limit

*For any* instructor, the locally cached student data must never exceed 100 students, with oldest students being evicted when the limit is reached.

**Validates: Requirements 10.6**

## Error Handling

### Error Categories

1. **Network Errors**
   - Connection timeout
   - Server unavailable
   - Authentication failure
   - Sync conflicts

2. **GPS Errors**
   - Location services disabled
   - GPS signal lost
   - Low accuracy
   - Permission denied

3. **QR Code Errors**
   - Invalid QR format
   - Unrecognized student/vehicle ID
   - Camera permission denied
   - QR code expired

4. **Data Integrity Errors**
   - Hash mismatch (tampering detected)
   - Corrupted local database
   - Missing required fields
   - Invalid session state

5. **Resource Errors**
   - Low battery
   - Low storage
   - Memory pressure
   - Camera unavailable

### Error Handling Strategies

**Network Errors:**
- Queue operations for retry when offline
- Display clear offline indicator
- Automatic retry with exponential backoff
- User notification when sync fails repeatedly

**GPS Errors:**
- Prompt user to enable location services
- Display accuracy warnings
- Continue session with degraded accuracy flag
- Fallback to last known location for non-critical operations

**QR Code Errors:**
- Display specific error message
- Offer manual ID entry as fallback
- Retry scanning with improved camera focus
- Log failed scan attempts for debugging

**Data Integrity Errors:**
- Reject tampered records immediately
- Log security events to audit trail
- Notify administrator of integrity violations
- Prevent sync of corrupted data

**Resource Errors:**
- Warn user of low battery during active session
- Offer to end session early
- Compress and cleanup old data when storage is low
- Reduce GPS polling frequency under memory pressure

### Error Recovery

All errors follow a consistent recovery pattern:

1. **Detect**: Identify the error condition
2. **Log**: Record error details to local log
3. **Notify**: Display user-friendly error message
4. **Recover**: Attempt automatic recovery where possible
5. **Fallback**: Provide manual recovery option
6. **Report**: Send error telemetry to backend (when online)

## Testing Strategy

### Dual Testing Approach

The testing strategy employs both unit testing and property-based testing to ensure comprehensive coverage:

- **Unit tests**: Verify specific examples, edge cases, and error conditions
- **Property tests**: Verify universal properties across all inputs
- Both approaches are complementary and necessary for complete validation

### Unit Testing

Unit tests focus on:
- Specific examples that demonstrate correct behavior (e.g., parsing a known QR code format)
- Edge cases (e.g., empty student lists, sessions with zero duration)
- Error conditions (e.g., invalid QR codes, network failures)
- Integration points between components (e.g., GPS tracker → Session controller)

Unit tests should be concise and targeted. Avoid writing excessive unit tests for scenarios that property-based tests already cover through randomization.

### Property-Based Testing

Property-based testing validates that universal properties hold across many randomly generated inputs. This approach is particularly valuable for:
- Data integrity properties (encryption, hashing, tamper detection)
- Calculation correctness (duration, remaining hours, totals)
- Round-trip properties (offline sync, serialization)
- Invariants (single active session, cache size limits)

**Configuration:**
- Use **fast_check** (for TypeScript/JavaScript) or **Hypothesis** (for Python) as the property-based testing library
- Each property test must run a minimum of 100 iterations
- Each test must include a comment tag referencing the design property
- Tag format: `// Feature: b2b-instructor-digital-logbook, Property {number}: {property_text}`

**Example Property Test Structure:**

```dart
// Feature: b2b-instructor-digital-logbook, Property 3: Duration Calculation Integrity
test('session duration equals timestamp difference', () {
  fc.assert(
    fc.property(
      fc.record({
        startTime: fc.date(),
        endTime: fc.date(),
      }),
      (data) {
        final session = createSession(
          startTime: data.startTime,
          endTime: data.endTime,
        );
        
        final expectedDuration = data.endTime.difference(data.startTime);
        expect(session.duration, equals(expectedDuration));
      }
    ),
    numRuns: 100,
  );
});
```

### Test Coverage Requirements

- All 34 correctness properties must have corresponding property-based tests
- Each component interface must have unit tests for error conditions
- Integration tests must verify end-to-end flows (session start → logging → sync)
- UI tests must verify accessibility and usability requirements
- Performance tests must validate resource management constraints

### Testing Priorities

1. **Critical Path**: Session creation, GPS tracking, QR check-in, offline sync
2. **Data Integrity**: Encryption, hashing, tamper detection
3. **Compliance**: NTSA report generation, audit trails
4. **Usability**: Error handling, offline capability, UI responsiveness
5. **Performance**: Battery usage, storage management, app launch time
