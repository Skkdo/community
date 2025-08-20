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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserUtil userUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User user() {
        return User.builder()
                .email("test@test.com")
                .password("test")
                .nickname("test")
                .build();
    }

    @Test
    @DisplayName("닉네임 중복 확인")
    void validateNickname() {
        User user = user();
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByNickname(user.getNickname());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.validateNickname(user.getNickname()));

        assertThat(exception).usingRecursiveComparison()
                .isEqualTo(new BusinessException(ResponseCode.DUPLICATE_NICKNAME));
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void validateEmail() {
        User user = user();
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.validateEmail(user.getEmail()));

        assertThat(exception).usingRecursiveComparison().isEqualTo(new BusinessException(ResponseCode.DUPLICATE_EMAIL));
    }

    @Test
    @DisplayName("유저 정보 조회")
    void getUser() {
        User user = user();
        GetUserResponseDto expectedDto = new GetUserResponseDto(user);
        Mockito.doReturn(user).when(userUtil).findByEmail(user.getEmail());

        GetUserResponseDto responseDto = userService.getUser(user.getEmail());

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("회원가입")
    void signUp() {
        User user = user();
        String password = user.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        SignUpRequestDto requestDto = new SignUpRequestDto(user.getEmail(), user.getPassword(),
                user.getNickname());
        Mockito.doReturn(Optional.empty()).when(userRepository).findByNickname(requestDto.getNickname());
        Mockito.doReturn(Optional.empty()).when(userRepository).findByEmail(requestDto.getEmail());
        Mockito.doReturn(encodedPassword).when(passwordEncoder).encode(password);

        userService.signUp(requestDto);

        assertThat(requestDto.getPassword()).isEqualTo(encodedPassword);
        Mockito.verify(userRepository, Mockito.times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("닉네임 변경")
    void patchNickname() {
        User user = user();
        PatchNicknameRequestDto requestDto = new PatchNicknameRequestDto("patch");
        Mockito.doReturn(user).when(userUtil).findByEmail(user.getEmail());
        Mockito.doReturn(Optional.empty()).when(userRepository).findByNickname(requestDto.getNickname());

        userService.patchNickname(user.getEmail(), requestDto);

        assertThat(user.getNickname()).isEqualTo(requestDto.getNickname());
    }

    @Test
    @DisplayName("프로필 이미지 변경")
    void patchProfileImage() {
        User user = user();
        PatchProfileImageRequestDto requestDto = new PatchProfileImageRequestDto("patch");
        Mockito.doReturn(user).when(userUtil).findByEmail(user.getEmail());

        userService.patchProfileImage(user.getEmail(), requestDto);

        assertThat(user.getProfileImage()).isEqualTo(requestDto.getProfileImage());
    }
}
