package community.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.back.common.ResponseDto;
import community.back.exception.GlobalExceptionHandler;
import community.back.service.UserService;
import community.back.service.dto.request.PatchNicknameRequestDto;
import community.back.service.dto.request.PatchProfileImageRequestDto;
import community.back.service.dto.request.SignInRequestDto;
import community.back.service.dto.request.SignUpRequestDto;
import community.back.service.dto.response.GetUserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private final String commonUrl = "/api/user";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    private MockMvc mockMvc;

    @BeforeEach
    void build() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .setValidator(new LocalValidatorFactoryBean())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("회원가입")
    void signUp() throws Exception {
        String url = commonUrl + "/auth/sign-up";
        SignUpRequestDto requestDto = new SignUpRequestDto("test@test.com", "test", "test");
        ResponseDto responseDto = ResponseDto.success();
        doNothing().when(userService).signUp(any(SignUpRequestDto.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("로그인")
    void signIn() throws Exception {
        String url = commonUrl + "/auth/sign-in";
        SignInRequestDto requestDto = new SignInRequestDto("test@test.com", "test");
        ResponseDto responseDto = ResponseDto.success();

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("유저 정보 조회")
    void getUser() throws Exception {
        String url = commonUrl + "/";
        GetUserResponseDto getUserResponseDto = mock(GetUserResponseDto.class);
        ResponseDto responseDto = ResponseDto.success(getUserResponseDto);
        doReturn(getUserResponseDto).when(userService).getUser(eq(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("닉네임 수정")
    void patchNickname() throws Exception {
        String url = commonUrl + "/nickname";
        PatchNicknameRequestDto requestDto = new PatchNicknameRequestDto("test");
        ResponseDto responseDto = ResponseDto.success();
        doNothing().when(userService).patchNickname(eq(null), any(PatchNicknameRequestDto.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("프로필 이미지 수정")
    void patchProfileImage() throws Exception {
        String url = commonUrl + "/profile-image";
        PatchProfileImageRequestDto requestDto = new PatchProfileImageRequestDto("test");
        ResponseDto responseDto = ResponseDto.success();
        doNothing().when(userService).patchProfileImage(eq(null), any(PatchProfileImageRequestDto.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }
}
