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

    @Scheduled(cron = "0 0 3 * * *") // 매 1분마다 실행
    //    cron = "0 0 3 * * *" 테스트 완료 후 새벽 3시마다 실행하는 것으로 변경 예정
    public void deleteExpiredData() {
        
        try {
            int deletedPosts = boardMapper.deleteExpiredPosts();
           

            int deletedComments = boardMapper.deleteExpiredComments();
            
        } catch (Exception e) {
            log.error("Error during scheduled deletion", e);
        }
        
    }
}
