-- Where a listing sails from and to: a region from SailingRegion, and the place as the owner writes it, such as
-- Hamble. ListingForm needs where from, and where to for a trip or a delivery. Listings posted before this have
-- neither until they are next edited.
alter table boats
    add column from_region varchar(255)
        check (from_region in ('EAST_COAST', 'SOUTH_COAST', 'WEST_COUNTRY', 'CHANNEL_ISLANDS', 'WALES_AND_IRISH_SEA',
                               'WEST_SCOTLAND', 'EAST_SCOTLAND_AND_NORTH_EAST', 'IRELAND', 'NORTHERN_EUROPE',
                               'BISCAY_AND_IBERIA', 'MEDITERRANEAN', 'ATLANTIC_ISLANDS', 'CARIBBEAN', 'NORTH_AMERICA',
                               'CENTRAL_AND_SOUTH_AMERICA', 'AFRICA_AND_INDIAN_OCEAN', 'ASIA', 'PACIFIC',
                               'AUSTRALIA_AND_NEW_ZEALAND')),
    add column from_place varchar(60),
    add column to_region varchar(255)
        check (to_region in ('EAST_COAST', 'SOUTH_COAST', 'WEST_COUNTRY', 'CHANNEL_ISLANDS', 'WALES_AND_IRISH_SEA',
                             'WEST_SCOTLAND', 'EAST_SCOTLAND_AND_NORTH_EAST', 'IRELAND', 'NORTHERN_EUROPE',
                             'BISCAY_AND_IBERIA', 'MEDITERRANEAN', 'ATLANTIC_ISLANDS', 'CARIBBEAN', 'NORTH_AMERICA',
                             'CENTRAL_AND_SOUTH_AMERICA', 'AFRICA_AND_INDIAN_OCEAN', 'ASIA', 'PACIFIC',
                             'AUSTRALIA_AND_NEW_ZEALAND')),
    add column to_place varchar(60);

create index boats_from_region on boats (from_region);
create index boats_to_region on boats (to_region);
