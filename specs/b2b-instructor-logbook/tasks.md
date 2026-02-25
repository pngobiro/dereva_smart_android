# Implementation Plan: B2B Instructor Digital Logbook

## Overview

This implementation plan breaks down the B2B Instructor Digital Logbook system into incremental, testable steps. The system will be built using Flutter for cross-platform mobile and web support, with Firebase as the backend. Each task builds on previous work, with property-based tests integrated throughout to validate correctness early.

The implementation follows a bottom-up approach: core data models and services first, then business logic, followed by UI components, and finally integration and synchronization features.

## Tasks

- [ ] 1. Project setup and core infrastructure
  - Initialize Flutter project with proper folder structure (lib/models, lib/services, lib/repositories, lib/ui)
  - Configure Firebase project (Authentication, Firestore, Storage)
  - Set up dependency injection (Provider or Riverpod)
  - Configure testing framework and add faker package for test data generation
  - Create environment configuration files (dev, staging, prod)
  - _Requirements: All_

- [ ] 2. Implement core data models
  - [ ] 2.1 Create data model classes
    - Implement User model with role enum (instructor, admin)
    - Implement Student model with LicenseCategory enum
    - Implement Vehicle model with VehicleType enum
    - Implement TrainingSession model with SessionType and SessionStatus enums
    - Implement GPSPoint model
    - Implement LicenseRequirements model
    - Add JSON serialization/deserialization for all models
    - _Requirements: 1.1, 2.5, 3.6, 4.2, 11.1, 12.1, 15.1_
  
  - [ ]* 2.2 Write property test for model serialization
    - **Property: Serialization round trip**
    - **Validates: Requirements 2.5, 5.5**
    - For any valid model instance, serializing to JSON then deserializing should produce an equivalent object


- [ ] 3. Implement authentication service
  - [ ] 3.1 Create AuthService interface and Firebase implementation
    - Implement signIn, signOut, getCurrentUser, resetPassword, changePassword methods
    - Implement authStateChanges stream
    - Add error handling for network and authentication errors
    - _Requirements: 2.3, 2.4, 2.7_
  
  - [ ]* 3.2 Write property test for authentication
    - **Property 4: Authentication with Valid Credentials**
    - **Validates: Requirements 2.3**
  
  - [ ]* 3.3 Write property test for authentication failure
    - **Property 5: Authentication Failure Handling**
    - **Validates: Requirements 2.4**
  
  - [ ]* 3.4 Write unit tests for authentication edge cases
    - Test password reset flow
    - Test first-time password change requirement
    - Test session expiration
    - _Requirements: 2.2, 2.7_

- [ ] 4. Implement local storage layer
  - [ ] 4.1 Set up SQLite database for offline storage
    - Create database schema for students, vehicles, sessions, sync queue
    - Implement database migration system
    - Create database helper class with CRUD operations
    - _Requirements: 6.1_
  
  - [ ] 4.2 Implement Hive storage for key-value data
    - Set up Hive for user preferences (language, settings)
    - Create type adapters for custom models
    - _Requirements: 17.4_

- [ ] 5. Implement student repository
  - [ ] 5.1 Create StudentRepository interface and implementation
    - Implement getStudentsByInstructor with local caching
    - Implement getStudentById with cache-first strategy
    - Implement createStudent, updateStudent methods
    - Implement assignStudentToInstructor method
    - Implement watchStudentsByInstructor stream
    - _Requirements: 1.1, 1.2, 1.6_
  
  - [ ]* 5.2 Write property test for student assignment visibility
    - **Property 1: Student Assignment Visibility and Access Control**
    - **Validates: Requirements 1.2, 18.4**
  
  - [ ]* 5.3 Write property test for student completion status
    - **Property 2: Student Completion Status**
    - **Validates: Requirements 1.7**
  
  - [ ] 5.4 Implement CSV import functionality
    - Parse CSV file and validate required fields
    - Create students in batch with error reporting
    - _Requirements: 1.3, 1.4_
  
  - [ ]* 5.5 Write property test for CSV validation
    - **Property 3: CSV Import Validation**
    - **Validates: Requirements 1.4**


- [ ] 6. Implement vehicle repository
  - [ ] 6.1 Create VehicleRepository interface and implementation
    - Implement getVehiclesBySchool, getVehicleById methods
    - Implement createVehicle, updateVehicle, deactivateVehicle methods
    - Implement getVehicleStats method
    - _Requirements: 11.1, 11.4, 11.5, 11.6_
  
  - [ ]* 6.2 Write property test for unique vehicle QR generation
    - **Property 33: Unique Vehicle QR Generation**
    - **Validates: Requirements 11.2**
  
  - [ ]* 6.3 Write property test for vehicle usage statistics
    - **Property 35: Vehicle Usage Statistics**
    - **Validates: Requirements 11.5**
  
  - [ ]* 6.4 Write property test for deactivated vehicle rejection
    - **Property 36: Deactivated Vehicle Rejection**
    - **Validates: Requirements 11.7**

- [ ] 7. Implement QR code service
  - [ ] 7.1 Create QRCodeService interface and implementation
    - Implement generateStudentQR and generateVehicleQR methods
    - Implement scanQR method using mobile_scanner package
    - Implement verifyStudentQR and verifyVehicleQR methods
    - Add QR code format validation
    - _Requirements: 3.2, 3.3, 3.4, 11.2, 12.1_
  
  - [ ]* 7.2 Write property test for student QR uniqueness
    - **Property 37: Unique Student QR Generation**
    - **Validates: Requirements 12.1**
  
  - [ ]* 7.3 Write property test for QR content encoding
    - **Property 38: Student QR Content**
    - **Validates: Requirements 12.4**
  
  - [ ]* 7.4 Write property test for invalid QR rejection
    - **Property 9: Invalid QR Rejection**
    - **Validates: Requirements 3.4**
  
  - [ ]* 7.5 Write property test for QR invalidation on regeneration
    - **Property 39: QR Code Invalidation on Regeneration**
    - **Validates: Requirements 12.7**

- [ ] 8. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 9. Implement GPS tracking service
  - [ ] 9.1 Create GPSTrackingService interface and implementation
    - Implement startTracking and stopTracking methods
    - Implement locationStream with 30-second intervals using geolocator
    - Implement permission checking and requesting
    - Handle GPS signal loss gracefully
    - _Requirements: 4.1, 4.3, 4.7_
  
  - [ ]* 9.2 Write property test for GPS tracking interval
    - **Property 12: GPS Tracking Interval**
    - **Validates: Requirements 4.1**
  
  - [ ]* 9.3 Write property test for GPS point structure
    - **Property 13: GPS Point Structure**
    - **Validates: Requirements 4.2, 4.7**
  
  - [ ]* 9.4 Write unit tests for GPS error handling
    - Test permission denied scenario
    - Test GPS disabled scenario
    - Test GPS signal loss and recovery
    - _Requirements: 4.3, 20.3_

