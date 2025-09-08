package community.back.service.util;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.UserRepository;
import community.back.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(ResponseCode.NOT_EXISTED_USER));
    }
}
