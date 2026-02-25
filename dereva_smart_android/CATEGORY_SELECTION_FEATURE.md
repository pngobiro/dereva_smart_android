# Category Selection Feature

## Overview
Guest users now select their license category when first opening the app. This ensures they see content relevant to their target license.

## User Experience

### First Launch
1. App opens to category selection screen
2. User sees a grid of all license categories (A1-G)
3. Each category shows:
   - Category code (e.g., B1, A2)
   - Description (e.g., "Light Vehicle", "Motorcycle <125cc")
4. User taps to select their category
5. Selected card highlights with a checkmark
6. "Continue" button becomes enabled
7. User taps "Continue" to proceed to home screen

### Subsequent Launches
- App remembers the selected category
- Goes directly to home screen
- Category is displayed in the guest mode banner

### Changing Category
From the home screen, guest users can:
1. See their current category in the banner
2. Tap "Change Category" button
3. Return to category selection screen
4. Select a new category
5. Content updates to match new category

## Technical Implementation

### Data Persistence
- Category stored in DataStore with key `guest_category`
- Persists across app restarts
- Cleared on login or logout

### Navigation Flow
```
CategorySelection → Home → Content
       ↑              ↓
       └──────────────┘
     (Change Category)
```

### Category Options
| Code | Description | Vehicle Type |
|------|-------------|--------------|
| A1 | Motorcycle <125cc | Small motorcycle |
| A2 | Motorcycle >125cc | Medium motorcycle |
| A3 | Motorcycle >400cc | Large motorcycle |
| B1 | Light Vehicle | Cars, small vans |
| B2 | Light Vehicle + Trailer | Cars with trailers |
| B3 | Tractor | Agricultural vehicles |
| C | Medium Truck | Commercial trucks |
| D | Bus/Matatu | Passenger transport |
| E | Heavy Truck | Large commercial vehicles |
| F | Articulated Vehicle | Truck with trailer |
| G | Road Roller | Construction equipment |

## Benefits

### For Users
- Personalized content from the start
- No irrelevant content
- Easy to change if needed
- Clear understanding of what they're studying for

### For Business
- Better user engagement
- Accurate analytics per category
- Targeted content delivery
- Improved conversion rates

## Future Enhancements
- Show popular categories
- Add category recommendations based on user profile
- Display category-specific statistics
- Show number of users per category
- Add category-specific onboarding tips
