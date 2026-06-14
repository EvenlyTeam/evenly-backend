-- 정산 완료 상태(사용자가 실제 송금 완료를 선언). null=진행중, 값=완료 시각.
alter table evenly.groups add column settled_at timestamptz;
