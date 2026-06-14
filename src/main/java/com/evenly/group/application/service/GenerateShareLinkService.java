package com.evenly.group.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.ShareLinkInfo;
import com.evenly.group.application.port.in.GenerateShareLinkUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class GenerateShareLinkService implements GenerateShareLinkUseCase {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 24; // base64url 32자

    private final LoadGroupPort loadGroupPort;
    private final SaveGroupPort saveGroupPort;

    GenerateShareLinkService(LoadGroupPort loadGroupPort, SaveGroupPort saveGroupPort) {
        this.loadGroupPort = loadGroupPort;
        this.saveGroupPort = saveGroupPort;
    }

    @Override
    public ShareLinkInfo generateShareLink(UUID groupId) {
        Group group = loadGroupPort.findById(groupId).orElseThrow(() -> new NotFoundException("Group", groupId));
        if (group.hasShareToken()) {
            return new ShareLinkInfo(group.getShareToken()); // 멱등: 이미 발급된 토큰 재사용
        }
        Group withToken = saveGroupPort.save(group.withShareToken(generateToken()));
        return new ShareLinkInfo(withToken.getShareToken());
    }

    private static String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
