-- What a business offers, on a listing where crew pay for a charter, trip or course. ListingForm asks for it then,
-- and only then.
alter table boats add column commercial_offering varchar(255)
    check (commercial_offering in ('BAREBOAT_CHARTER', 'SKIPPERED_CHARTER', 'SKIPPERED_TRIP', 'TUITION'));
