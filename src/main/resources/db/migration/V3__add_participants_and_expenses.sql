-- 참여자: 모임 애그리거트의 일부. 이름만으로 추가 가능(가입 회원일 필요 없음).
-- user_id 는 로그인 사용자와의 soft link("내 잔액" 계산용), 보통 null.
create table evenly.participants (
    id         uuid        primary key,
    group_id   uuid        not null references evenly.groups (id) on delete cascade,
    user_id    uuid        references evenly.users (id),
    name       varchar(50) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    unique (group_id, name)
);
create index idx_participants_group_id on evenly.participants (group_id);

-- 지출: 결제자 1명, 금액은 원 단위 정수(부동소수점 금지).
create table evenly.expenses (
    id          uuid         primary key,
    group_id    uuid         not null references evenly.groups (id) on delete cascade,
    payer_id    uuid         not null references evenly.participants (id),
    description varchar(100) not null,
    amount      bigint       not null check (amount > 0),
    created_at  timestamptz  not null,
    updated_at  timestamptz  not null
);
create index idx_expenses_group_id on evenly.expenses (group_id);

-- 지출 분담 대상(N명). share_order 는 균등분할 나머지 1~2원을 첫 분담자에게 배분하기 위한 순서.
create table evenly.expense_shares (
    expense_id     uuid not null references evenly.expenses (id) on delete cascade,
    participant_id uuid not null references evenly.participants (id),
    share_order    int  not null,
    primary key (expense_id, participant_id)
);
