package com.tinqinacademy.authentication.core.services.cleaners;

import com.tinqinacademy.authentication.persistence.repositories.BlackListTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class BlacklistTokenCleaner {
    private final BlackListTokenRepository blackListTokenRepository;

    public BlacklistTokenCleaner(BlackListTokenRepository blackListTokenRepository) {
        this.blackListTokenRepository = blackListTokenRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void deleteAllBlacklistTokens() {
        log.info("Deleting all blacklisted expired tokens");
        LocalDateTime now = LocalDateTime.now();
        blackListTokenRepository.deleteAllByCreatedAtBefore(now.minusMinutes(5));
    }
}
