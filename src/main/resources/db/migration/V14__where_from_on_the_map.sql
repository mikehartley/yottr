-- Where the place a listing sails from was found to be, for the map (PlaceLocator). Null when it wasn't, and the
-- map uses the middle of the listing's region instead.
alter table boats
    add column from_latitude double precision,
    add column from_longitude double precision;
