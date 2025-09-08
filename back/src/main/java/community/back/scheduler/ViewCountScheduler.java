package community.back.scheduler;

import community.back.service.ViewCountService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewCountScheduler {
    private final ViewCountService viewCountService;
    private final RedissonClient redissonClient;

    @Scheduled(cron = "0 0/5 * * * *", zone = "Asia/Seoul")
    public void syncViewCountScheduled() {
        RLock lock = redissonClient.getLock("viewCountSyncLock");
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(0, 240, TimeUnit.SECONDS);

            if(isLocked) {
                log.info("조회수 동기화 시작");
                viewCountService.syncAllViewCountToDatabase();
                log.info("조회수 동기화 끝");
            } else {
                log.info("다른 서버에서 동기화 중");
            }
        } catch (InterruptedException  e) {
            Thread.currentThread().interrupt();
            log.error("동기화 작업 인터럽트 됨" ,e);
        } catch (Exception e) {
            log.error("동기화 작업 중 예외 발생", e);
        }finally {
            if(isLocked) {
                lock.unlock();
                log.info("락 해제 왼료");
            }
        }
    }
}
