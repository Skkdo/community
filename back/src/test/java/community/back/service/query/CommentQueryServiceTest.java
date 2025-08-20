package community.back.service.query;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.CommentRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Comment;
import community.back.service.dto.response.GetCommentListResponseDto;
import community.back.service.util.BoardUtil;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CommentQueryServiceTest {
    @InjectMocks
    private CommentQueryService commentQueryService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BoardUtil boardUtil;

    private Board board() {
        return Board.builder()
                .id(1L)
                .title("test")
                .content("test")
                .favoriteCount(0)
                .viewCount(0)
                .commentCount(0)
                .build();
    }

    private Comment comment() {
        return Comment.builder()
                .id(1L)
                .content("test")
                .build();
    }

    @Test
    @DisplayName("아이디로 댓글 조회 예외처리")
    void findById() {
        Long commentId = 1L;
        doReturn(Optional.empty()).when(commentRepository).findById(commentId);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentQueryService.findById(commentId));

        assertThat(exception).usingRecursiveComparison()
                .isEqualTo(new BusinessException(ResponseCode.NOT_EXISTED_COMMENT));
    }

    @Test
    @DisplayName("게시글 아이디로 댓글 삭제")
    void deleteByBoardId() {
        Long boardId = 1L;
        doNothing().when(commentRepository).deleteByBoardId(boardId);

        commentQueryService.deleteByBoardId(boardId);

        verify(commentRepository, times(1)).deleteByBoardId(boardId);
    }

    @Test
    @DisplayName("댓글 리스트 조회")
    void getCommentList() {
        Board board = board();
        Page<Comment> commentPage = Page.empty();
        Pageable pageable = PageRequest.of(0, 10);
        GetCommentListResponseDto expectedDto = new GetCommentListResponseDto(commentPage);
        doReturn(board).when(boardUtil).findById(board.getId());
        doReturn(commentPage).when(commentRepository).findByBoardIdAndParentIsNull(board.getId(), pageable);

        GetCommentListResponseDto responseDto = commentQueryService.getCommentList(board().getId(), pageable);

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("대댓글 리스트 조회")
    void getChildCommentList() {
        Comment comment = comment();
        Long parentCommentId = 1L;
        Page<Comment> commentPage = Page.empty();
        Pageable pageable = PageRequest.of(0, 10);
        GetCommentListResponseDto expectedDto = new GetCommentListResponseDto(commentPage);
        doReturn(Optional.of(comment)).when(commentRepository).findById(parentCommentId);
        doReturn(commentPage).when(commentRepository).findByParentId(parentCommentId, pageable);

        GetCommentListResponseDto responseDto = commentQueryService.getChildCommentList(parentCommentId, pageable);

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }
}
