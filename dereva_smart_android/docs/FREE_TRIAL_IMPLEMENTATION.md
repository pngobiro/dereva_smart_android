# Free Trial Implementation Summary

## Overview
Implemented a free trial flow where users can access the app without logging in. Users first select their license category, then can browse content. Login is only required when accessing premium content that requires a subscription.

## Key Changes

### 1. User Model Updates
- Added `isGuestMode: Boolean` field to `User` model
- Added `User.createGuestUser()` companion function to create guest users
- Updated `UserEntity` to include `isGuestMode` field

### 2. Content Model Updates
- Added `requiresSubscription: Boolean` field to `Module` model
- Added `requiresSubscription: Boolean` field to `Lesson` model
- Updated `ModuleEntity` and `LessonEntity` to include subscription flags
- Updated database version from 6 to 7

### 3. Category Selection
- Created `CategorySelectionScreen` for guest users to choose their license category
- Shows all available license categories (A1-A3, B1-B3, C, D, E, F, G) with descriptions
- Allows users to change category anytime from home screen
- Category selection is persisted in DataStore

### 4. Authentication Repository
- Added `startGuestMode()` method to create guest user sessions
- Added `isGuestMode()` method to check if user is in guest mode
- Added `updateGuestCategory()` method to update guest user's selected category
- Updated `login()` to clear guest mode flag on successful login
- Updated `logout()` to clear guest mode flag and category
- Updated `getAuthStateFlow()` to handle guest mode state and restore category

### 5. Authentication ViewModel
- Added automatic guest mode initialization on app start
- Added `startGuestMode()` method
- Added `updateGuestCategory()` method to update category selection
- Added `checkAndStartGuestMode()` to auto-start guest mode if not authenticated

### 6. Content ViewModel
- Added `showSubscriptionRequired` and `subscriptionRequiredMessage` to UI state
- Added `setCurrentUser()` method to track current user
- Added `checkSubscriptionAccess()` methods for modules and lessons
- Updated `selectModule()` and `selectLesson()` to check subscription requirements
- Added `dismissSubscriptionDialog()` method

### 7. Navigation Updates
- Added `CategorySelection` screen route
- Changed start destination to show category selection for new guest users
- Added logic to skip category selection if already selected
- Added `LaunchedEffect` to pass current user to ContentViewModel in all content screens

### 8. UI Updates

#### CategorySelectionScreen (NEW)
- Grid layout showing all license categories
- Visual selection with highlighted cards
- Category descriptions (e.g., "Light Vehicle", "Motorcycle <125cc")
- Continue button enabled only when category is selected
- Note about changing category later

#### HomeScreen
- Added guest mode banner showing "Free Trial Mode"
- Displays selected category (e.g., "Category: B1")
- Added "Login" button in top app bar when in guest mode
- Added "Change Category" button in guest banner
- Shows prompt to login for full features

#### ModuleListScreen
- Added subscription required dialog
- Added premium indicator (lock icon) next to module titles that require subscription
- Dialog offers "Login / Subscribe" or "Cancel" options

#### LessonViewerScreen
- Added subscription required dialog
- Shows when attempting to access premium lessons
- Offers "Login / Subscribe" or "Go Back" options

## User Flow

### First Time User Flow
1. User opens app
2. Category selection screen appears
3. User selects their license category (e.g., B1 for Light Vehicle)
4. User clicks "Continue"
5. App navigates to home screen in guest mode
6. User can browse and access free content
7. When user tries to access premium content:
   - Dialog appears: "Premium Content - This content requires a subscription"
   - Options: "Login / Subscribe" or "Cancel/Go Back"
8. If user clicks "Login / Subscribe", navigates to login screen
9. After login, user gains access to premium content based on subscription status

### Returning Guest User Flow
1. User opens app
2. App remembers selected category
3. Goes directly to home screen
4. Can change category via "Change Category" button

### Login Flow
1. User can login anytime via:
   - "Login" button in home screen top bar (when in guest mode)
   - "Login / Subscribe" button in free trial banner
   - When attempting to access premium content
2. After successful login:
   - Guest mode is cleared
   - User's actual subscription status is used
   - Access to content is based on subscription

## Database Migration
- Database version updated from 6 to 7
- New fields added with default values for backward compatibility:
  - `users.isGuestMode` (default: false)
  - `modules.requiresSubscription` (default: false)
  - `lessons.requiresSubscription` (default: false)

## DataStore Keys
- `guest_mode`: Stores whether user is in guest mode
- `guest_category`: Stores selected license category for guest users
- Both cleared on login/logout

## Content Configuration
To mark content as premium:
1. Set `requiresSubscription = true` when creating Module or Lesson objects
2. Free content should have `requiresSubscription = false` (default)

## License Categories
- **A1**: Motorcycle <125cc
- **A2**: Motorcycle >125cc
- **A3**: Motorcycle >400cc
- **B1**: Light Vehicle (most common)
- **B2**: Light Vehicle + Trailer
- **B3**: Tractor
- **C**: Medium Truck
- **D**: Bus/Matatu
- **E**: Heavy Truck
- **F**: Articulated Vehicle
- **G**: Road Roller

## Testing Checklist
- [ ] App starts with category selection for new users
- [ ] Category selection shows all categories with descriptions
- [ ] Selected category is saved and persisted
- [ ] App skips category selection for returning users
- [ ] Home screen shows selected category
- [ ] "Change Category" button works
- [ ] Free content is accessible in guest mode
- [ ] Premium content shows subscription dialog
- [ ] Login button appears in guest mode
- [ ] Login clears guest mode and category
- [ ] Subscription status is checked correctly
- [ ] Database migration works without data loss
- [ ] Guest mode banner displays correctly
- [ ] Premium indicators show on locked content
- [ ] Category persists across app restarts
