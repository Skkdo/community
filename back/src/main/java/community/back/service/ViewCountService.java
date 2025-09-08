package community.back.service;

import community.back.repository.BoardRepository;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewCountService {
    private static final String VIEW_COUNT_KEY = "board:viewCount:";
    private static final String VIEW_LOG_KEY = "board:viewLog:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final BoardRepository boardRepository;

    public void incrementViewCount(Long boardId, String userIdentifier) {
        String viewCountKey = VIEW_COUNT_KEY + boardId;
        String viewLogKey = VIEW_LOG_KEY + boardId;

        if (isAlreadyViewed(viewLogKey, userIdentifier)) {
            return;
        }

        redisTemplate.opsForValue().increment(viewCountKey);

        redisTemplate.opsForSet().add(viewLogKey, userIdentifier);
        redisTemplate.expire(viewLogKey, Duration.ofHours(24));
    }

    private boolean isAlreadyViewed(String viewLogKey, String userIdentifier) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(viewLogKey, userIdentifier));
    }

    @Transactional
    public void syncAllViewCountToDatabase() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY + "*");
        for(String key : keys) {
            long boardId = Long.parseLong(key.substring(VIEW_COUNT_KEY.length()));
            syncViewCountToDatabase(boardId);
        }
    }

    @Transactional
    public void syncViewCountToDatabase(Long boardId) {
        Long redisViewCount = getViewCount(boardId);
        if(redisViewCount > 0) {
            boardRepository.updateViewCount(boardId, redisViewCount);
        }
    }

    public Long getViewCount(Long boardId) {
        String viewCountKey = VIEW_COUNT_KEY + boardId;
        Object count = redisTemplate.opsForValue().get(viewCountKey);
        return count == null ? 0L : Long.parseLong(count.toString());
    }
}
