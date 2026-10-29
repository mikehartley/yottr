-- Whether a member is on Yottr as crew, as an owner, or both (at least one; PersonalDetailsForm checks). The
-- site shows each member only what's relevant to them. Members who joined before this keep everything.
alter table users
    add column crew boolean not null default true,
    add column owner boolean not null default true;
