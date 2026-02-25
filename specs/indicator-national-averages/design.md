# Design Document: National Averages Feature

## Overview

The National Averages feature extends the Laravel performance management system to store, manage, and display benchmark values for master indicators. This enables units to compare their performance against national standards across different financial years and rank levels.

The feature integrates seamlessly with existing reporting infrastructure (Composite Score Reports and PMMU Reports) while providing a dedicated administrative interface for benchmark management. It supports multiple data entry methods including manual entry, auto-calculation from existing unit data, and bulk Excel import/export.

### Key Design Decisions

1. **Separate Model Approach**: Create a dedicated `NationalAverage` model rather than adding fields to `MasterIndicator` to maintain clean separation of concerns and support historical tracking
2. **Composite Key Strategy**: Use unique combination of (master_indicator_id, financial_year_id, unit_rank_id) to ensure one average per context
3. **Audit Trail via History Table**: Implement a separate `national_average_histories` table for complete change tracking
4. **Non-Destructive Deletes**: Soft delete national averages to preserve historical data for reports
5. **Lazy Loading in Reports**: Fetch national averages only when needed to avoid performance impact on existing queries
6. **PhpSpreadsheet Integration**: Leverage existing Excel library for import/export consistency

## Architecture

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
├─────────────────────────────────────────────────────────────┤
│  Admin UI          │  Reports UI       │  Data Entry UI     │
│  - List/Filter     │  - Composite      │  - Indicator       │
│  - Create/Edit     │  - PMMU           │    Entry Forms     │
│  - Import/Export   │  - Variance       │                    │
│  - Auto-Calculate  │    Display        │                    │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                     Controller Layer                         │
├─────────────────────────────────────────────────────────────┤
│  NationalAverageController  │  ReportsController (Extended) │
│  - CRUD Operations          │  - Composite Score Report     │
│  - Validation               │  - PMMU Report                │
│  - Import/Export            │  - Variance Calculation       │
│  - Auto-Calculate           │                               │
└─────────────────────────────────────────────────────────────┘
