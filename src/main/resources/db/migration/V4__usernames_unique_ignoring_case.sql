-- Usernames are unique ignoring case (UsernameAvailableValidator checks the same on sign-up).
-- This fails if two existing usernames differ only in case; rename one of them first.
create unique index users_username_lower_key on users (lower(username));
