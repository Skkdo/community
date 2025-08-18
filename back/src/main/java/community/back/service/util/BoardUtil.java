package community.back.service.util;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.BoardRepository;
import community.back.repository.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardUtil {
    private final BoardRepository boardRepository;

    public Board findById(Long id) {
        return boardRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.NOT_EXISTED_BOARD));
    }
}
