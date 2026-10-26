-- A listing's description was limited to the default 255 characters, and nothing on the form said so,
-- so a longer one failed when saved. Boat and ListingForm now both allow 2000.

alter table boats alter column description type varchar(2000);
