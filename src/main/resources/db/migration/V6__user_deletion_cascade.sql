-- 회원 탈퇴 시: 본인이 소유한 모임은 함께 삭제(cascade),
-- 타인 모임에 이름으로 참여 중이던 연결은 끊고 이름만 남긴다(set null).
alter table evenly.groups drop constraint groups_owner_id_fkey;
alter table evenly.groups
    add constraint groups_owner_id_fkey
    foreign key (owner_id) references evenly.users (id) on delete cascade;

alter table evenly.participants drop constraint participants_user_id_fkey;
alter table evenly.participants
    add constraint participants_user_id_fkey
    foreign key (user_id) references evenly.users (id) on delete set null;
