-- The financial arrangements, worded as what happens to the crew, so they read the same to an owner and to crew.
update financial_arrangement set display_name = 'No cost to crew' where name = 'NO_CONTRIBUTION';
update financial_arrangement set display_name = 'Crew share food only' where name = 'FOOD_ONLY';
update financial_arrangement set display_name = 'Crew pay a fixed share' where name = 'SHARED_FIXED';
update financial_arrangement set display_name = 'Crew share running costs' where name = 'SHARED_VARIABLE';
update financial_arrangement set display_name = 'Crew pay (charter, trip or course)' where name = 'COMMERCIAL';
update financial_arrangement set display_name = 'Crew are paid' where name = 'PAID';
