package community.back.service.util;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.UserRepository;
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
public class UserUtilTest {
    @InjectMocks
    private UserUtil userUtil;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 유저 조회 예외처리")
    void findByEmail() {
        String email = "test";
        doReturn(Optional.empty()).when(userRepository).findByEmail(email);

        BusinessException exception = assertThrows(BusinessException.class, () -> userUtil.findByEmail(email));

        assertThat(exception).usingRecursiveComparison()
                .isEqualTo(new BusinessException(ResponseCode.NOT_EXISTED_USER));
    }
}
