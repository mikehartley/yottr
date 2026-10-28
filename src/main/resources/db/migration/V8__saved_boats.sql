-- The listings each member has saved to come back to. Deleting either side removes the row: Hibernate maps it from
-- the member's side only, so it wouldn't clear the rows for a deleted listing itself.
create table saved_boats (
    user_id bigint not null references users on delete cascade,
    boat_id bigint not null references boats on delete cascade,
    primary key (user_id, boat_id)
);
