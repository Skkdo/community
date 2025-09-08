package community.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.back.common.ResponseDto;
import community.back.exception.GlobalExceptionHandler;
import community.back.service.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
public class FavoriteControllerTest {
    private final String commonUrl = "/api/favorite";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private FavoriteController favoriteController;
    @Mock
    private FavoriteService favoriteService;
    private MockMvc mockMvc;

    @BeforeEach
    void build() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(favoriteController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    @Test
    @DisplayName("좋아요")
    void putFavorite() throws Exception {
        String url = commonUrl + "/{boardId}";
        Long boardId = 1L;
        ResponseDto responseDto = ResponseDto.success();
        doNothing().when(favoriteService).putFavorite(eq(null), eq(boardId));

        mockMvc.perform(
                        MockMvcRequestBuilders.put(url, boardId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }
}
