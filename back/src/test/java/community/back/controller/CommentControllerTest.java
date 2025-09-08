package community.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.back.common.ResponseDto;
import community.back.exception.GlobalExceptionHandler;
import community.back.service.command.CommentCommandService;
import community.back.service.dto.request.PatchCommentRequestDto;
import community.back.service.dto.request.PostCommentRequestDto;
import community.back.service.dto.response.GetCommentListResponseDto;
import community.back.service.query.CommentQueryService;
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
public class CommentControllerTest {
    private final String commonUrl = "/api/comment";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private CommentController commentController;
    @Mock
    private CommentCommandService commentCommandService;
    @Mock
    private CommentQueryService commentQueryService;
    private MockMvc mockMvc;

    @BeforeEach
    void build() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(commentController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .setValidator(new LocalValidatorFactoryBean())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("댓글 리스트 조회")
    void getCommentList() throws Exception {
        String url = commonUrl + "/{boardId}/comment-list";
        Long boardId = 1L;

        GetCommentListResponseDto mock = mock(GetCommentListResponseDto.class);
        ResponseDto responseDto = ResponseDto.success(mock);
        doReturn(mock).when(commentQueryService)
                .getCommentList(boardId, PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt")));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, boardId)
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "createdAt,DESC")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("댓글 생성")
    void postComment() throws Exception {
        String url = commonUrl + "/{boardId}/comment";
        Long boardId = 1L;
        PostCommentRequestDto requestDto = new PostCommentRequestDto("test", 0L);
        ResponseDto responseDto = ResponseDto.success();

        doNothing().when(commentCommandService).postComment(eq(boardId), eq(null), any(PostCommentRequestDto.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(url, boardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("댓글 수정")
    void patchComment() throws Exception {
        String url = commonUrl + "/{boardId}/{commentId}";
        Long boardId = 1L;
        Long commentId = 1L;
        PatchCommentRequestDto requestDto = new PatchCommentRequestDto("test");

        ResponseDto responseDto = ResponseDto.success();

        doNothing().when(commentCommandService)
                .patchComment(eq(boardId), eq(commentId), eq(null), any(PatchCommentRequestDto.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(url, boardId, commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        String url = commonUrl + "/{boardId}/{commentId}";
        Long boardId = 1L;
        Long commentId = 1L;
        ResponseDto responseDto = ResponseDto.success();

        doNothing().when(commentCommandService).deleteComment(eq(boardId), eq(null), eq(commentId));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, boardId, commentId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseDto.getMessage()));
    }
}
