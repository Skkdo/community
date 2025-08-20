package community.back.scheduler;

import community.back.service.ViewCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewCountScheduler {
    private final ViewCountService viewCountService;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void syncViewCountScheduled() {
        try {
            log.info("조회수 동기화 시작");
            viewCountService.syncAllViewCountToDatabase();
            log.info("조회수 동기화 끝");
        } catch (Exception e) {
            log.error("조회수 동기화 실패" ,e);
        }
    }
}
