package com.jangsacartel.biz.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jangsacartel.biz.board.mapper.BoardMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletionScheduler {

    private final BoardMapper boardMapper;

    @Scheduled(cron = "0 0 3 * * *") // 매 새벽 3시마다 실행
    public void deleteExpiredData() {
        
        try {
            int deletedPosts = boardMapper.deleteExpiredPosts();
           

            int deletedComments = boardMapper.deleteExpiredComments();
            
        } catch (Exception e) {
            log.error("Error during scheduled deletion", e);
        }
        
    }
}
