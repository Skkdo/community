package community.back.service.command;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.BoardRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.User;
import community.back.service.FavoriteService;
import community.back.service.ImageService;
import community.back.service.dto.request.PatchBoardRequestDto;
import community.back.service.dto.request.PostBoardRequestDto;
import community.back.service.query.CommentQueryService;
import community.back.service.util.BoardUtil;
import community.back.service.util.UserUtil;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardCommandServiceTest {
    @InjectMocks
    private BoardCommandService boardCommandService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardUtil boardUtil;
    @Mock
    private ImageService imageService;
    @Mock
    private UserUtil userUtil;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private CommentQueryService commentService;

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

    @Test
    @DisplayName("게시글 작성")
    void postBoard() {
        User user = user();
        List<String> imageList = List.of();
        PostBoardRequestDto requestDto = new PostBoardRequestDto("test", "test", imageList);
        doReturn(user).when(userUtil).findByEmail(user.getEmail());

        boardCommandService.postBoard(requestDto, user().getEmail());

        verify(boardRepository, times(1)).save(any(Board.class));
        verify(imageService, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("게시글 수정")
    void patchBoard() {
        Board board = board();
        User user = user();
        List<String> imageList = List.of();
        PatchBoardRequestDto requestDto = new PatchBoardRequestDto("test", "test", imageList);
        doReturn(user).when(userUtil).findByEmail(user.getEmail());
        doReturn(board).when(boardUtil).findById(board().getId());

        boardCommandService.patchBoard(requestDto, board().getId(), user.getEmail());

        verify(boardRepository, times(1)).save(any(Board.class));
        verify(imageService, times(1)).deleteByBoardId(board.getId());
        verify(imageService, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("게시글 삭제")
    void deleteBoard() {
        Board board = board();
        User user = user();
        doReturn(user).when(userUtil).findByEmail(user.getEmail());
        doReturn(board).when(boardUtil).findById(board().getId());

        boardCommandService.deleteBoard(board().getId(), user.getEmail());

        verify(imageService, times(1)).deleteByBoardId(board.getId());
        verify(favoriteService, times(1)).deleteByBoardId(board().getId());
        verify(commentService, times(1)).deleteByBoardId(board().getId());
        verify(boardRepository, times(1)).delete(any(Board.class));
    }

    @Test
    @DisplayName("작성자 확인")
    void isWriter() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> boardCommandService.isWriter("other", "writer"));

        assertThat(exception).usingRecursiveComparison().isEqualTo(new BusinessException(ResponseCode.NO_PERMISSION));
    }
}
