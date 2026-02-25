# Requirements Document

## Introduction

The National Averages feature enables the performance management system to store, manage, and display national benchmark values for master indicators. This allows organizational units to compare their performance against national standards, providing context for performance evaluation and identifying areas for improvement. The system supports both manual entry and automatic calculation of national averages, with comprehensive audit trails and integration into existing reporting infrastructure.

## Glossary

- **National_Average**: A benchmark value representing the typical or expected performance level for a specific master indicator across all units at a particular rank level during a financial year
- **Master_Indicator**: A standardized performance metric defined at the system level that units measure and report against
- **Financial_Year**: The fiscal period for which performance data is collected and evaluated
- **Unit_Rank**: The hierarchical classification level of organizational units (e.g., Rank 11, Rank 12)
- **Unit**: An organizational entity that reports performance data
- **Achievement**: The actual performance value recorded by a unit for a specific indicator
- **Composite_Score_Report**: A comprehensive report showing aggregated performance metrics across multiple indicators
- **PMMU_Report**: Performance Management and Monitoring Unit report showing detailed performance analytics
- **Special_Indicator**: Type 1 indicator with a maximum value constraint of 100
- **Normal_Indicator**: Type 2 indicator with flexible target ranges
- **Variance**: The percentage difference between a unit's achievement and the national average
- **Auto_Calculate**: System feature that computes the mean of all units' achievements for a given indicator, rank, and financial year
- **Audit_Trail**: Historical record of all changes made to national average values, including who made changes and when

## Requirements

### Requirement 1: Store National Average Data

**User Story:** As a system administrator, I want to store national average values for each master indicator by financial year and unit rank, so that the system maintains accurate benchmark data for performance comparison.

#### Acceptance Criteria

1. THE System SHALL store a national average value for each unique combination of Master_Indicator, Financial_Year, and Unit_Rank
2. WHEN a national average is created or updated, THE System SHALL record the user identifier who performed the action
3. WHEN a national average is created or updated, THE System SHALL record the timestamp of the action
4. THE System SHALL maintain a complete history of all changes to national average values
5. THE System SHALL support storing national average values as positive decimal numbers with up to 2 decimal places
6. WHEN a duplicate combination of Master_Indicator, Financial_Year, and Unit_Rank is submitted, THE System SHALL update the existing record rather than create a duplicate

### Requirement 2: Validate National Average Values

**User Story:** As a system administrator, I want the system to validate national average entries, so that only realistic and appropriate benchmark values are stored.

#### Acceptance Criteria

1. WHEN a national average value is submitted, THE System SHALL reject values that are not positive numbers
2. WHEN a national average is submitted for a Special_Indicator, THE System SHALL reject values exceeding 100
3. WHEN a national average is submitted for a Normal_Indicator, THE System SHALL reject values exceeding 200% of the typical target value
4. WHEN a manually entered national average differs by more than 30% from the auto-calculated average, THE System SHALL display a warning message to the user
5. THE System SHALL prevent saving a national average value of zero or negative numbers

### Requirement 3: Manage National Averages via Admin Interface

**User Story:** As a system administrator, I want a dedicated interface to manage national averages, so that I can efficiently maintain benchmark data.

#### Acceptance Criteria

1. WHEN a user with admin role accesses the national averages management page, THE System SHALL display the management interface
2. WHEN a user without admin role attempts to access the national averages management page, THE System SHALL deny access and display an authorization error
3. THE System SHALL display a list view showing all national averages with columns for Financial_Year, Unit_Rank, Master_Indicator name, average value, last updated date, and updated by user
4. WHEN an administrator applies filters for Financial_Year, Unit_Rank, or Master_Indicator, THE System SHALL display only matching national average records
5. WHEN an administrator clicks to create a new national average, THE System SHALL display a form with fields for Financial_Year, Unit_Rank, Master_Indicator, and average value
6. WHEN an administrator clicks to edit an existing national average, THE System SHALL display a pre-populated form with the current values
7. WHEN an administrator submits a valid national average form, THE System SHALL save the data and display a success message
8. WHEN an administrator submits an invalid national average form, THE System SHALL display validation error messages and prevent saving

### Requirement 4: Auto-Calculate National Averages

**User Story:** As a system administrator, I want to automatically calculate national averages from existing unit data, so that I can quickly establish benchmarks based on actual performance.

#### Acceptance Criteria

1. WHEN an administrator selects a Financial_Year and Unit_Rank and clicks auto-calculate, THE System SHALL compute the mean of all units' achievement values for each Master_Indicator
2. WHEN computing the mean, THE System SHALL exclude units with null or zero achievement values
3. WHEN computing the mean, THE System SHALL include only units matching the selected Unit_Rank
4. WHEN the auto-calculation is complete, THE System SHALL display the calculated averages for administrator review before saving
5. WHEN an administrator confirms the auto-calculated values, THE System SHALL save them as national averages with the administrator's user identifier
6. WHEN no achievement data exists for a Master_Indicator at the selected Financial_Year and Unit_Rank, THE System SHALL display a message indicating insufficient data

### Requirement 5: Bulk Import National Averages from Excel

**User Story:** As a system administrator, I want to import national averages from an Excel file, so that I can efficiently load benchmark data from external sources.

#### Acceptance Criteria

1. THE System SHALL provide a downloadable Excel template with columns for Financial_Year, Unit_Rank, Master_Indicator code, and Average value
2. WHEN an administrator uploads an Excel file, THE System SHALL validate that all required columns are present
3. WHEN processing an Excel import, THE System SHALL validate each row against the validation rules for national averages
4. WHEN an Excel row contains invalid data, THE System SHALL collect all validation errors and display them to the administrator
5. WHEN all Excel rows are valid, THE System SHALL import the national averages and display a success message with the count of imported records
6. WHEN an imported row matches an existing Master_Indicator, Financial_Year, and Unit_Rank combination, THE System SHALL update the existing record
7. WHEN an Excel import is completed, THE System SHALL record the administrator's user identifier and timestamp for all imported records

### Requirement 6: Bulk Export National Averages to Excel

**User Story:** As a system administrator, I want to export national averages to an Excel file, so that I can share benchmark data or perform external analysis.

#### Acceptance Criteria

1. WHEN an administrator clicks the export button, THE System SHALL generate an Excel file containing all national averages
2. THE System SHALL include columns for Financial_Year, Unit_Rank, Master_Indicator code, Master_Indicator name, Average value, Last updated date, and Updated by user
3. WHEN filters are applied to the list view, THE System SHALL export only the filtered records
4. WHEN the Excel file is generated, THE System SHALL prompt the administrator to download the file
5. THE System SHALL format the Excel file with headers in bold and appropriate column widths for readability

### Requirement 7: Display National Averages in Composite Score Report

**User Story:** As a report viewer, I want to see national averages alongside unit achievements in the composite score report, so that I can assess performance relative to national benchmarks.

#### Acceptance Criteria

1. WHEN the Composite_Score_Report is generated, THE System SHALL include a "National Avg" column for each Master_Indicator
2. WHEN a national average exists for a Master_Indicator, Financial_Year, and Unit_Rank combination, THE System SHALL display the average value in the "National Avg" column
3. WHEN no national average exists for a Master_Indicator, Financial_Year, and Unit_Rank combination, THE System SHALL display "N/A" in the "National Avg" column
4. THE System SHALL include a "Variance from Avg" column showing the percentage difference between the unit's achievement and the national average
5. WHEN calculating variance, THE System SHALL use the formula: ((Achievement - National_Avg) / National_Avg) × 100
6. WHEN a unit's achievement is above the national average, THE System SHALL display the variance value with green color coding
7. WHEN a unit's achievement equals the national average (within 1% tolerance), THE System SHALL display the variance value with yellow color coding
8. WHEN a unit's achievement is below the national average, THE System SHALL display the variance value with red color coding
9. WHEN no national average exists for an indicator, THE System SHALL not display a variance value

### Requirement 8: Display National Averages in PMMU Report

**User Story:** As a PMMU report viewer, I want to see national averages in the PMMU report, so that I can evaluate unit performance against national standards.

#### Acceptance Criteria

1. WHEN the PMMU_Report is generated, THE System SHALL include national average values for each Master_Indicator where available
2. WHEN displaying indicator data in the PMMU_Report, THE System SHALL show the national average as a reference line or value
3. THE System SHALL display the variance between unit achievement and national average in the PMMU_Report
4. WHEN no national average exists for an indicator, THE System SHALL indicate that no benchmark is available

### Requirement 9: Display National Averages in Indicator Entry Forms

**User Story:** As a data entry user, I want to see the national average when entering indicator values, so that I have context for the expected performance level.

#### Acceptance Criteria

1. WHEN a user opens an indicator entry form, THE System SHALL display the national average for that Master_Indicator, Financial_Year, and Unit_Rank if available
2. THE System SHALL display the national average as informational text near the achievement input field
3. WHEN no national average exists, THE System SHALL not display any benchmark information
4. THE System SHALL display the national average in a read-only format that cannot be edited by data entry users

### Requirement 10: Control Access to National Average Management

**User Story:** As a system administrator, I want to restrict national average management to authorized users only, so that benchmark data integrity is maintained.

#### Acceptance Criteria

1. WHEN a user attempts to access national average management pages, THE System SHALL verify the user has admin role
2. WHEN a user without admin role attempts to access management functions, THE System SHALL return an HTTP 403 Forbidden response
3. THE System SHALL allow all authenticated users to view national averages in reports
4. THE System SHALL allow all authenticated users to view national averages in indicator entry forms
5. THE System SHALL prevent non-admin users from creating, updating, or deleting national average records

### Requirement 11: Maintain Audit Trail for National Averages

**User Story:** As a system administrator, I want to track all changes to national averages, so that I can maintain accountability and review historical benchmark data.

#### Acceptance Criteria

1. WHEN a national average is created, THE System SHALL record the creating user's identifier and creation timestamp
2. WHEN a national average is updated, THE System SHALL record the updating user's identifier and update timestamp
3. THE System SHALL maintain a history table storing all previous values of national averages
4. WHEN viewing a national average's history, THE System SHALL display all previous values with timestamps and user identifiers
5. THE System SHALL record the source of each national average (manual entry, auto-calculation, or bulk import)
6. WHEN a national average is deleted, THE System SHALL retain the historical records for audit purposes

### Requirement 12: Handle Edge Cases and Data Integrity

**User Story:** As a system administrator, I want the system to handle edge cases gracefully, so that national average functionality remains reliable under all conditions.

#### Acceptance Criteria

1. WHEN a Master_Indicator is deleted, THE System SHALL retain associated national average records for historical reporting
2. WHEN a Financial_Year is deleted, THE System SHALL retain associated national average records for historical reporting
3. WHEN a Unit_Rank is deleted, THE System SHALL retain associated national average records for historical reporting
4. WHEN calculating auto-averages with fewer than 3 units reporting data, THE System SHALL display a warning about statistical reliability
5. WHEN a national average is requested for a future Financial_Year, THE System SHALL allow entry but display a warning
6. THE System SHALL handle concurrent updates to the same national average record by using database-level locking
