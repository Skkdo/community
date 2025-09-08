package community.back.service.command;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.CommentRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Comment;
import community.back.repository.entity.User;
import community.back.service.dto.request.PatchCommentRequestDto;
import community.back.service.dto.request.PostCommentRequestDto;
import community.back.service.query.CommentQueryService;
import community.back.service.util.BoardUtil;
import community.back.service.util.UserUtil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentCommandServiceTest {
    @InjectMocks
    private CommentCommandService commentCommandService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentQueryService commentQueryService;
    @Mock
    private BoardUtil boardUtil;
    @Mock
    private UserUtil userUtil;

    private User user() {
        return User.builder()
                .email("test@test.com")
                .password("test")
                .nickname("test")
                .build();
    }

    private Board board() {
        return Board.builder()
                .id(1L)
                .title("test")
                .content("test")
                .writer(user())
                .favoriteCount(0)
                .viewCount(0)
                .commentCount(0)
                .build();
    }

    private Comment comment() {
        return Comment.builder()
                .id(1L)
                .writer(user())
                .board(board())
                .content("test")
                .build();
    }

    @Test
    @DisplayName("댓글 작성")
    void postComment() {
        Board board = board();
        int commentCount = board.getCommentCount();
        User user = user();
        PostCommentRequestDto requestDto = new PostCommentRequestDto("test", 0L);
        doReturn(user).when(userUtil).findByEmail(user.getEmail());
        doReturn(board).when(boardUtil).findById(board().getId());

        commentCommandService.postComment(board.getId(), user.getEmail(), requestDto);

        assertThat(board.getCommentCount()).isEqualTo(commentCount + 1);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 수정")
    void patchComment() {
        Board board = board();
        User user = user();
        Comment comment = comment();
        String patchContent = "patch";
        PatchCommentRequestDto requestDto = new PatchCommentRequestDto(patchContent);
        doReturn(user).when(userUtil).findByEmail(user.getEmail());
        doReturn(board).when(boardUtil).findById(board().getId());
        doReturn(comment).when(commentQueryService).findById(comment.getId());

        commentCommandService.patchComment(board.getId(), comment.getId(), user.getEmail(), requestDto);

        assertThat(comment.getContent()).isEqualTo(patchContent);
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        Board board = board();
        User user = user();
        Comment comment = comment();
        int commentCount = board.getCommentCount();
        doReturn(user).when(userUtil).findByEmail(user.getEmail());
        doReturn(board).when(boardUtil).findById(board().getId());
        doReturn(comment).when(commentQueryService).findById(comment.getId());

        commentCommandService.deleteComment(board.getId(), user.getEmail(), comment.getId());

        assertThat(board.getCommentCount()).isEqualTo(commentCount - 1);
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    @Test
    @DisplayName("작성자 확인")
    void isWriter() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentCommandService.isWriter("other", "writer"));

        assertThat(exception).usingRecursiveComparison().isEqualTo(new BusinessException(ResponseCode.NO_PERMISSION));
    }
}
