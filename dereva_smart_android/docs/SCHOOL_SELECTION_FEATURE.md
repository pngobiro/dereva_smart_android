# School Selection Feature

## Overview
This feature allows users to optionally link their account to a verified driving school during registration. The school selection is completely optional and can be skipped.

## Implementation

### Android App Changes

1. **AuthViewModel Updates**
   - Added `schools` and `isLoadingSchools` to `AuthUiState`
   - Added `SchoolRepository` dependency injection
   - Added `loadSchools()` function to fetch verified schools from API
   - Schools are loaded when RegisterScreen is displayed

2. **RegisterScreen Updates**
   - Calls `viewModel.loadSchools()` on screen load
   - Uses schools from `uiState.schools` instead of local state
   - School selection dialog shows verified schools from API

3. **SchoolRepositoryImpl Updates**
   - Updated `getVerifiedSchools()` to properly map API response
   - Uses `verified` field from API response

4. **DerevaApiService Updates**
   - Added `verified` query parameter (defaults to `true`)
   - Returns `List<SchoolResponse>` directly (not wrapped in object)

5. **Dependency Injection**
   - Updated `AppModule.kt` to inject `SchoolRepository` into `AuthViewModel`

### Backend API Changes

1. **Schools Endpoint (`/api/schools`)**
   - Supports `?verified=true` query parameter to filter verified schools
   - Returns array directly: `[{...}, {...}]` instead of `{schools: [...]}`
   - Response format matches Android app's `SchoolResponse` DTO:
     ```json
     [
       {
         "id": "sch-001",
         "name": "AA Kenya Driving School",
         "location": "Nairobi, Nairobi",
         "phone": "254712345678",
         "email": "info@aakenya.co.ke",
         "rating": 4.5,
         "license_types": ["B1", "B2", "C1"],
         "price_range": "KES 15,000 - 25,000",
         "verified": true
       }
     ]
     ```

## API Endpoints

### Get Schools
```
GET /api/schools?verified=true
```

Returns list of verified driving schools.

## User Flow

1. User opens registration screen
2. App automatically loads verified schools from API
3. User can optionally select a school (or skip)
4. If school is selected, `drivingSchoolId` is sent with registration
5. User's account is linked to the school in the database

## Database Schema

The `users` table has a nullable `driving_school_id` field that links users to schools:

```sql
CREATE TABLE users (
  id TEXT PRIMARY KEY,
  phone_number TEXT UNIQUE NOT NULL,
  full_name TEXT NOT NULL,
  target_category TEXT NOT NULL,
  driving_school_id TEXT,
  -- other fields...
  FOREIGN KEY (driving_school_id) REFERENCES schools(id)
);
```

## Testing

1. Backend API test:
   ```bash
   curl "https://dereva-smart-backend.pngobiro.workers.dev/api/schools?verified=true"
   ```

2. Android app test:
   - Open registration screen
   - Verify schools load in the selection dialog
   - Select a school and complete registration
   - Verify user is linked to school in database
