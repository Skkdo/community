package community.back.config.securityUtil;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.UserRepository;
import community.back.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws BusinessException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.SIGN_IN_FAIL));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
