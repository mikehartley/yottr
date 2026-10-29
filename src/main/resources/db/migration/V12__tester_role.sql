-- TESTER marks an account made for trying the site out, from test-users.properties (see TestUsers). It grants nothing.
alter table user_roles drop constraint if exists user_roles_role_check;
alter table user_roles add constraint user_roles_role_check check (role in ('FREE', 'CREW', 'ADMIN', 'TESTER'));
