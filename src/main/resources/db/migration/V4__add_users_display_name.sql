-- "내 잔액" 표시를 위해 사용자 표시 이름(닉네임) 추가.
-- 기존 행은 이메일 local-part 로 백필.
alter table evenly.users add column display_name varchar(50);
update evenly.users set display_name = split_part(email, '@', 1) where display_name is null;
alter table evenly.users alter column display_name set not null;
