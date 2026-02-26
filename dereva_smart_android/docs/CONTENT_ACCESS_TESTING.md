# Content Access Control Testing Guide

## Overview
This guide helps you verify that content access control is working correctly for all user types.

## Test Scenarios

### Scenario 1: Guest User ✅
**User Type:** Guest (no account)  
**Expected Behavior:** Can access free content only

**Test Steps:**
1. Open app
2. Select any license category (e.g., B1)
3. Navigate to "Learning Modules"
4. **Verify in UI:**
   - Top bar shows: "Browse all categories"
   - See list of modules with some having lock icons 🔒
5. **Try to open FREE module** (no lock icon)
   - Should open successfully ✅
6. **Try to open PREMIUM module** (has lock icon)
   - Should show dialog: "Account Required"
   - Message: "This content requires a registered account with premium subscription. Create an account to access all features!"
   - Options: Register | Login | Cancel

**Expected Modules for B1:**
- ✅ "Introduction to Driving" (FREE)
- ✅ "Vehicle Basics & Town Driving" (FREE)
- 🔒 "Road Signs & Markings" (PREMIUM)

---

### Scenario 2: Registered User WITHOUT Subscription
**User Type:** Registered account with FREE status  
**Expected Behavior:** Same as guest but with account benefits

**Test Steps:**
1. Register new account OR login with existing FREE account
2. Navigate to "Learning Modules"
3. **Verify in UI:**
   - Top bar shows: "Category: B1"
   - Debug line shows: "Status: FREE | Active: false"
   - Home screen shows "FREE" badge (not "PREMIUM")
4. **Try to open FREE module**
   - Should open successfully ✅
5. **Try to open PREMIUM module**
   - Should show dialog: "Premium Content"
   - Message: "This content requires a premium subscription to access. Upgrade now to unlock all learning materials!"
   - Options: Subscribe Now | Cancel

**Differences from Guest:**
- Progress is saved to server
- Can link to driving school
- Can purchase subscription
- Shows "Upgrade to Premium" banner on home screen

---

### Scenario 3: Registered User WITH EXPIRED Subscription
**User Type:** Had premium, subscription expired  
**Expected Behavior:** Same access as FREE user

**Test Steps:**
1. Login with account that had premium subscription
2. Navigate to "Learning Modules"
3. **Verify in UI:**
   - Top bar shows: "Category: B1"
   - Debug line shows: "Status: PREMIUM_MONTHLY | Active: false"
   - Home screen shows "FREE" badge (subscription expired)
4. **Try to open FREE module**
   - Should open successfully ✅
5. **Try to open PREMIUM module**
   - Should show dialog: "Premium Content"
   - Message: "This content requires a premium subscription to access. Upgrade now to unlock all learning materials!"
   - Options: Subscribe Now | Cancel

**How to Create Test User with Expired Subscription:**
Option A - Via Database:
```sql
UPDATE users 
SET subscription_status = 'PREMIUM_MONTHLY',
    subscription_expiry_date = strftime('%s', 'now', '-1 day') * 1000
WHERE phone_number = '+254712345678';
```

Option B - Via App (after payment):
- Wait for subscription to expire naturally
- OR manually set expiry date in past via admin panel

---

### Scenario 4: Registered User WITH ACTIVE Subscription
**User Type:** Premium subscriber  
**Expected Behavior:** Can access ALL content

**Test Steps:**
1. Login with premium account OR complete payment flow
2. Navigate to "Learning Modules"
3. **Verify in UI:**
   - Top bar shows: "Category: B1"
   - Debug line shows: "Status: PREMIUM_MONTHLY | Active: true"
   - Home screen shows "PREMIUM" badge
   - NO lock icons on any modules
4. **Try to open ANY module**
   - All modules should open successfully ✅
   - No subscription dialogs

**How to Create Test User with Active Subscription:**
Option A - Via Database:
```sql
UPDATE users 
SET subscription_status = 'PREMIUM_MONTHLY',
    subscription_expiry_date = strftime('%s', 'now', '+30 days') * 1000
WHERE phone_number = '+254712345678';
```

Option B - Via App:
- Complete M-Pesa payment flow
- Subscription automatically activated

---

## Quick Verification Checklist

### Visual Indicators
- [ ] Guest: "Browse all categories" in header
- [ ] Registered: "Category: [name]" in header
- [ ] Debug line shows subscription status (FREE/PREMIUM_MONTHLY)
- [ ] Debug line shows active status (true/false)
- [ ] Lock icons 🔒 appear on premium modules
- [ ] Home screen badge shows correct status (FREE/PREMIUM)

### Access Control
- [ ] Guest can open free modules
- [ ] Guest CANNOT open premium modules
- [ ] FREE user can open free modules
- [ ] FREE user CANNOT open premium modules
- [ ] EXPIRED user can open free modules
- [ ] EXPIRED user CANNOT open premium modules
- [ ] PREMIUM user can open ALL modules

### Dialog Messages
- [ ] Guest sees "Account Required" dialog
- [ ] FREE user sees "Premium Content" dialog
- [ ] EXPIRED user sees "Premium Content" dialog
- [ ] PREMIUM user sees NO dialogs

---

## Logcat Verification

Check Android Studio Logcat for these logs:

```
ContentViewModel: setCurrentUser called with user: [name], category: B1, isGuest: [true/false]
ContentViewModel: Loading modules for category: B1, isGuest: [true/false]
ContentViewModel: Received 3 modules from API
ContentViewModel: Module requires subscription, isGuest: [true/false]
```

When clicking premium module:
```
ContentViewModel: checkSubscriptionAccess - module: [module_name], requiresSubscription: true
ContentViewModel: checkSubscriptionAccess - isGuestMode: [true/false]
ContentViewModel: checkSubscriptionAccess - isSubscriptionActive: [true/false]
```

---

## Common Issues

### Issue: All modules show lock icons
**Cause:** API returning all modules as premium  
**Fix:** Check database - ensure `requires_subscription = 0` for free modules

### Issue: Premium user still sees locks
**Cause:** Subscription expiry date in past  
**Fix:** Update expiry date to future date

### Issue: Guest can access premium content
**Cause:** Bug in access control logic  
**Fix:** Check `checkSubscriptionAccess()` function

---

## Database Queries for Testing

### Check module subscription status:
```sql
SELECT id, title, requires_subscription 
FROM modules 
WHERE license_category = 'B1';
```

### Check user subscription status:
```sql
SELECT phone_number, subscription_status, 
       datetime(subscription_expiry_date/1000, 'unixepoch') as expiry
FROM users 
WHERE phone_number = '+254712345678';
```

### Create test users:
```sql
-- FREE user
INSERT INTO users (id, phone_number, password_hash, name, target_category, 
                   subscription_status, is_phone_verified, created_at, last_active_at)
VALUES ('test-free', '+254700000001', 'hash', 'Test Free User', 'B1', 
        'FREE', 1, strftime('%s', 'now') * 1000, strftime('%s', 'now') * 1000);

-- EXPIRED user
INSERT INTO users (id, phone_number, password_hash, name, target_category, 
                   subscription_status, subscription_expiry_date, is_phone_verified, 
                   created_at, last_active_at)
VALUES ('test-expired', '+254700000002', 'hash', 'Test Expired User', 'B1', 
        'PREMIUM_MONTHLY', strftime('%s', 'now', '-1 day') * 1000, 1,
        strftime('%s', 'now') * 1000, strftime('%s', 'now') * 1000);

-- ACTIVE PREMIUM user
INSERT INTO users (id, phone_number, password_hash, name, target_category, 
                   subscription_status, subscription_expiry_date, is_phone_verified, 
                   created_at, last_active_at)
VALUES ('test-premium', '+254700000003', 'hash', 'Test Premium User', 'B1', 
        'PREMIUM_MONTHLY', strftime('%s', 'now', '+30 days') * 1000, 1,
        strftime('%s', 'now') * 1000, strftime('%s', 'now') * 1000);
```

---

## Summary

The access control system uses this logic:

```kotlin
fun checkSubscriptionAccess(module: Module): Boolean {
    // Free content is always accessible
    if (!module.requiresSubscription) return true
    
    // Guests cannot access premium content
    if (currentUser?.isGuestMode == true) return false
    
    // Check if subscription is active
    return currentUser?.isSubscriptionActive == true
}
```

Where `isSubscriptionActive` checks:
1. Subscription status is PREMIUM_MONTHLY
2. Expiry date is in the future (or null)

This ensures:
- ✅ Everyone can access free content
- ✅ Only active premium subscribers can access premium content
- ✅ Expired subscriptions are treated as FREE
