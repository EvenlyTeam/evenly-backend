package com.evenly.user.application.port.in;

import java.util.UUID;

public interface DeleteUserUseCase {

    /** 회원 탈퇴. 본인이 소유한 모임은 함께 삭제된다(DB cascade). */
    void deleteUser(UUID userId);
}
