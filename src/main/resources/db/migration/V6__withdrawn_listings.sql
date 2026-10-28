-- A listing an administrator has taken down. Unlike suspended, its owner can't undo it.
alter table boats add column withdrawn boolean not null default false;
