-- What crew pay, or are paid, in whole pounds, and what it's for. ListingForm asks for both unless there's no cost to
-- crew. Listings from before have neither.
alter table boats add column money_amount integer check (money_amount > 0);
alter table boats add column money_period varchar(255) check (money_period in ('DAY', 'WEEK', 'MONTH', 'YEAR', 'TRIP', 'COURSE'));
