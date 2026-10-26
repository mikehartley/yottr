-- The reference data the listing forms need. These match the enums in RyaSailCruisingLevel.Level,
-- FinancialArrangement.FinancialArrangementEnum and Frequency.FrequencyEnum; ReferenceDataMigrationIT checks they still do.
-- ON CONFLICT DO NOTHING because databases from before Flyway may already have some of these rows.

insert into rya_sail_cruising_level (rank, display_name) values
    (0, 'None'),
    (100, 'Start Yachting'),
    (200, 'Competent Crew'),
    (300, 'Day Skipper'),
    (400, 'Coastal Skipper'),
    (500, 'Yachtmaster Coastal'),
    (600, 'Yachtmaster Offshore'),
    (700, 'Yachtmaster Ocean')
on conflict do nothing;

insert into financial_arrangement (name, display_name) values
    ('NO_CONTRIBUTION', 'No Contribution Required'),
    ('FOOD_ONLY', 'Food Only (No Boat Costs)'),
    ('SHARED_FIXED', 'Shared Fixed'),
    ('SHARED_VARIABLE', 'Shared Variable'),
    ('COMMERCIAL', 'Commercial'),
    ('PAID', 'Paid Position')
on conflict do nothing;

insert into frequency (name, display_name) values
    ('WEEKDAYS', 'Weekdays'),
    ('WEEKENDS', 'Weekends'),
    ('HOLIDAYS', 'Holidays'),
    ('LONG_TERM', 'Long Term'),
    ('TRIP', 'Trip')
on conflict do nothing;
