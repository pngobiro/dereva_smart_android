# Cloudflare Backend Integration

## Overview
The Android app is now integrated with the Cloudflare Workers backend deployed at:
`https://dereva-smart-backend.pngobiro.workers.dev`

## Changes Made

### 1. API Service Layer
Created new files for backend communication:

#### `DerevaApiService.kt`
- Retrofit interface defining all API endpoints
- Endpoints: auth, users, content, questions, progress, payments, schools, tutor
- Uses suspend functions for coroutine support

#### `ApiModels.kt` (DTO package)
- Data Transfer Objects for API requests/responses
- Includes: AuthResponse, UserData, ModuleResponse, LessonResponse, etc.
- Uses @SerializedName for JSON mapping

#### `ApiClient.kt`
- Singleton Retrofit client configuration
- Base URL: `https://dereva-smart-backend.pngobiro.workers.dev`
- Includes logging interceptor for debugging
- 30-second timeouts for all requests

### 2. Repository Updates

#### `AuthRepositoryImpl.kt`
Updated authentication methods to use Cloudflare API:

**Register:**
- Calls `/api/auth/register` endpoint
- Sends phone, password, name, category
- Stores user locally and saves session token
- Handles API errors gracefully

**Login:**
- Calls `/api/auth/login` endpoint
- Validates credentials with backend
- Creates/updates local user from API response
- Maps subscription status from API
- Clears guest mode on successful login

### 3. Dependency Injection

#### `AppModule.kt`
- Added `ApiClient.apiService` to Koin module
- Available for injection in repositories

## API Endpoints Used

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/verify` - SMS verification
- `POST /api/auth/resend-code` - Resend verification code

### Content
- `GET /api/content/modules?category={category}` - Get learning modules
- `GET /api/content/lessons/{moduleId}` - Get lessons for module

### Questions
- `GET /api/questions?category={category}` - Get practice questions

### Progress
- `GET /api/progress/{userId}` - Get user progress
- `POST /api/progress` - Update progress

### Payments
- `POST /api/payments/initiate` - Initiate M-Pesa payment

### Schools
- `GET /api/schools` - Get driving schools list

### AI Tutor
- `POST /api/tutor/ask` - Ask AI tutor a question

## Authentication Flow

1. **Guest Mode** (No API calls)
   - User starts app → Category selection → Home screen
   - All data stored locally
   - No backend communication

2. **Registration**
   - User fills registration form
   - App calls `/api/auth/register`
   - Backend creates user, sends SMS verification
   - App stores user locally with token
   - User can verify phone later

3. **Login**
   - User enters phone + password
   - App calls `/api/auth/login`
   - Backend validates credentials
   - App receives user data + token
   - Token stored for authenticated requests

4. **Authenticated Requests**
   - Token sent in `Authorization` header
   - Format: `Bearer {token}`
   - Backend validates token for protected endpoints

## Data Synchronization

### Hybrid Approach
The app uses a hybrid local-first + cloud-sync approach:

**Local Storage:**
- User profile
- Downloaded content
- Progress tracking
- Mock test results
- Cached questions

**Cloud Storage (Cloudflare):**
- User authentication
- Subscription status
- Cross-device sync
- Payment records
- School data

### Sync Strategy
1. App works offline with local data
2. When online, syncs with backend
3. Backend is source of truth for:
   - Subscription status
   - Payment verification
   - User authentication
4. Local DB is source of truth for:
   - Downloaded content
   - Offline progress
   - Cached questions

## Error Handling

### Network Errors
```kotlin
try {
    val response = ApiClient.apiService.login(request)
    if (!response.isSuccessful) {
        throw Exception(response.body()?.message ?: "Login failed")
    }
} catch (e: Exception) {
    // Handle error, show user-friendly message
}
```

### API Response Format
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

## Testing

### Test Backend Connection
```kotlin
// In a ViewModel or Repository
viewModelScope.launch {
    try {
        val response = ApiClient.apiService.healthCheck()
        if (response.isSuccessful) {
            Log.d("API", "Backend connected: ${response.body()}")
        }
    } catch (e: Exception) {
        Log.e("API", "Backend error", e)
    }
}
```

### Test Registration
1. Open app
2. Click "Create Account"
3. Fill form with valid data
4. Submit → Should call backend
5. Check Logcat for API logs

### Test Login
1. Use registered phone number
2. Enter password
3. Submit → Should call backend
4. Should navigate to home on success

## Next Steps

### Immediate
- [ ] Test registration flow end-to-end
- [ ] Test login flow end-to-end
- [ ] Verify SMS sending works
- [ ] Test guest mode → login transition

### Content Integration
- [ ] Update ContentRepository to fetch from API
- [ ] Implement module/lesson sync
- [ ] Add offline content caching
- [ ] Sync progress to backend

### Payment Integration
- [ ] Connect M-Pesa API
- [ ] Test payment initiation
- [ ] Verify subscription updates

### Production Readiness
- [ ] Add proper error messages
- [ ] Implement retry logic
- [ ] Add loading states
- [ ] Handle token expiration
- [ ] Implement refresh token flow

## Configuration

### Change Backend URL
Edit `ApiClient.kt`:
```kotlin
private const val BASE_URL = "https://your-custom-domain.com"
```

### Enable/Disable Logging
Edit `ApiClient.kt`:
```kotlin
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.NONE // Disable in production
}
```

## Security Notes

1. **HTTPS Only**: All API calls use HTTPS
2. **Token Storage**: Tokens stored in DataStore (encrypted)
3. **Password Hashing**: BCrypt with 12 rounds
4. **No Hardcoded Secrets**: API keys in environment variables
5. **Request Timeout**: 30 seconds to prevent hanging

## Troubleshooting

### "Unable to resolve host"
- Check internet connection
- Verify backend URL is correct
- Check if backend is deployed

### "401 Unauthorized"
- Token expired or invalid
- User needs to login again
- Check Authorization header format

### "Network timeout"
- Backend might be slow
- Increase timeout in ApiClient
- Check backend logs

### "JSON parsing error"
- API response format changed
- Check DTO classes match API
- Verify @SerializedName annotations
