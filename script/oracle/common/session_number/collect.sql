select username, count(*) count from v$session where username is not null group by username
