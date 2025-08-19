package community.back.service;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.UserRepository;
import community.back.repository.entity.User;
import community.back.service.dto.request.PatchNicknameRequestDto;
import community.back.service.dto.request.PatchProfileImageRequestDto;
import community.back.service.dto.request.SignUpRequestDto;
import community.back.service.dto.response.GetUserResponseDto;
import community.back.service.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final PasswordEncoder passwordEncoder;

    public void validateNickname(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BusinessException(ResponseCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ResponseCode.DUPLICATE_EMAIL);
        }
    }

    public GetUserResponseDto getUser(String email) {
        User user = userUtil.findByEmail(email);
        return new GetUserResponseDto(user);
    }

    @Transactional
    public void signUp(SignUpRequestDto dto) {
        validateEmail(dto.getEmail());
        validateNickname(dto.getNickname());

        String password = dto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        dto.setPassword(encodedPassword);

        User user = new User(dto);
        userRepository.save(user);
    }

    @Transactional
    public void patchNickname(String email, PatchNicknameRequestDto dto) {
        User user = userUtil.findByEmail(email);
        validateNickname(dto.getNickname());
        user.setNickname(dto.getNickname());
    }

    @Transactional
    public void patchProfileImage(String email, PatchProfileImageRequestDto dto) {
        User user = userUtil.findByEmail(email);
        user.setProfileImage(dto.getProfileImage());
    }
}
