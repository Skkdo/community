package community.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.back.common.ResponseDto;
import community.back.exception.GlobalExceptionHandler;
import community.back.service.ViewCountService;
import community.back.service.command.BoardCommandService;
import community.back.service.dto.request.PatchBoardRequestDto;
import community.back.service.dto.request.PostBoardRequestDto;
import community.back.service.dto.response.GetBoardListResponseDto;
import community.back.service.dto.response.GetBoardResponseDto;
import community.back.service.query.BoardQueryService;
import java.util.List;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {
    private final String commonUrl = "/api/board";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private BoardController boardController;
    @Mock
    private BoardQueryService boardQueryService;
    @Mock
    private BoardCommandService boardCommandService;
    @Mock
    private ViewCountService viewCountService;
    private MockMvc mockMvc;

    @BeforeEach
    void build() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(boardController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .setValidator(new LocalValidatorFactoryBean())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("게시글 조회")
    void getBoard() throws Exception {
        String url = commonUrl + "/{boardId}";
        Long boardId = 1L;
        GetBoardResponseDto mock = mock(GetBoardResponseDto.class);
        ResponseDto responseDto = ResponseDto.success(mock);
        doReturn(mock).when(boardQueryService).getBoard(boardId);
        doNothing().when(viewCountService).incrementViewCount(eq(boardId), any(String.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, boardId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("최신 게시글 리스트 조회")
    void getLatestBoardList() throws Exception {
        String url = commonUrl + "/latest-list";

        GetBoardListResponseDto mock = mock(GetBoardListResponseDto.class);
        ResponseDto responseDto = ResponseDto.success(mock);
        doReturn(mock).when(boardQueryService)
                .getLatestBoardList(PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt")));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url)
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "createdAt,DESC")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("검색어로 게시글 리스트 조회")
    void getSearchBoardList() throws Exception {
        String url = commonUrl + "/search-list/{searchWord}";
        String searchWord = "searchWord";

        GetBoardListResponseDto mock = mock(GetBoardListResponseDto.class);
        ResponseDto responseDto = ResponseDto.success(mock);
        doReturn(mock).when(boardQueryService)
                .getSearchBoardList(searchWord, PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt")));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, searchWord)
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "createdAt,DESC")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("유저의 게시글 리스트 조회")
    void getUserBoardList() throws Exception {
        String url = commonUrl + "/user-board-list";

        GetBoardListResponseDto mock = mock(GetBoardListResponseDto.class);
        ResponseDto responseDto = ResponseDto.success(mock);
        doReturn(mock).when(boardQueryService)
                .getUserBoardList(eq(null), any(Pageable.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url)
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "createdAt,DESC")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("게시글 생성")
    void postBoard() throws Exception {
        PostBoardRequestDto requestDto = new PostBoardRequestDto("test", "test", List.of());
        ResponseDto responseDto = ResponseDto.success();
        doNothing().when(boardCommandService).postBoard(any(PostBoardRequestDto.class), eq(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(commonUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("게시글 수정")
    void patchBoard() throws Exception {
        String url = commonUrl + "/{boardId}";
        Long boardId = 1L;
        PatchBoardRequestDto requestDto = new PatchBoardRequestDto("test", "test", List.of());
        ResponseDto responseDto = ResponseDto.success();

        doNothing().when(boardCommandService).patchBoard(any(PatchBoardRequestDto.class), eq(boardId), eq(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(url, boardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("게시글 삭제")
    void deleteBoard() throws Exception {
        String url = commonUrl + "/{boardId}";
        Long boardId = 1L;
        ResponseDto responseDto = ResponseDto.success();
        doNothing().when(boardCommandService).deleteBoard(eq(boardId), eq(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, boardId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }
}
