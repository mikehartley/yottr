-- The day a member signed up. Members who signed up before this was recorded have none.
alter table users add column joined date;
