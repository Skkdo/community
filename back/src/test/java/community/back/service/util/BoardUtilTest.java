package community.back.service.util;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.BoardRepository;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardUtilTest {
    @InjectMocks
    private BoardUtil boardUtil;
    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("아이디로 게시글 조회 예외처리")
    void findByEmail() {
        Long id = 1L;
        doReturn(Optional.empty()).when(boardRepository).findById(id);

        BusinessException exception = assertThrows(BusinessException.class, () -> boardUtil.findById(id));

        assertThat(exception).usingRecursiveComparison()
                .isEqualTo(new BusinessException(ResponseCode.NOT_EXISTED_BOARD));
    }
}
