-- users 테이블을 BaseJpaEntity(created_at + updated_at) 와 호환되도록 updated_at 추가.
-- User JpaEntity 는 auth(회원가입) 유스케이스와 함께 추가될 예정.
alter table evenly.users add column updated_at timestamptz;
update evenly.users set updated_at = created_at where updated_at is null;
alter table evenly.users alter column updated_at set not null;
