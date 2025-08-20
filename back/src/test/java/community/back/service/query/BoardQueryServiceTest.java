package community.back.service.query;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.BoardRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Image;
import community.back.repository.entity.User;
import community.back.service.ImageService;
import community.back.service.dto.response.GetBoardListResponseDto;
import community.back.service.dto.response.GetBoardResponseDto;
import community.back.service.util.UserUtil;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BoardQueryServiceTest {
    @InjectMocks
    private BoardQueryService boardQueryService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private ImageService imageService;
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

    @Test
    @DisplayName("아이디로 게시글 조회")
    void getBoard() {
        Board board = board();
        List<Image> imageList = List.of();
        doReturn(Optional.of(board)).when(boardRepository).findByIdWithWriter(board.getId());
        doReturn(imageList).when(imageService).findByBoardId(board().getId());
        GetBoardResponseDto expectedDto = new GetBoardResponseDto(board, imageList);

        GetBoardResponseDto responseDto = boardQueryService.getBoard(board.getId());

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("아이디로 게시글 조회 예외처리")
    void getBoardException() {
        Board board = board();
        doReturn(Optional.empty()).when(boardRepository).findByIdWithWriter(board.getId());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> boardQueryService.getBoard(board.getId()));

        assertThat(exception).usingRecursiveComparison()
                .isEqualTo(new BusinessException(ResponseCode.NOT_EXISTED_BOARD));
    }

    @Test
    @DisplayName("유저 게시글 리스트 조회")
    void getUserBoardList() {
        User user = user();
        Page<Board> boardPage = Page.empty();
        Pageable pageable = PageRequest.of(0, 10);
        GetBoardListResponseDto expectedDto = new GetBoardListResponseDto(boardPage, user);
        doReturn(user).when(userUtil).findByEmail(user.getEmail());
        doReturn(boardPage).when(boardRepository).findByWriterEmail(user.getEmail(), pageable);

        GetBoardListResponseDto responseDto = boardQueryService.getUserBoardList(user.getEmail(), pageable);

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("검색어로 게시글 리스트 조회")
    void getSearchBoardList() {
        String searchWord = "test";
        Page<Board> boardPage = Page.empty();
        Pageable pageable = PageRequest.of(0, 10);
        GetBoardListResponseDto expectedDto = new GetBoardListResponseDto(boardPage);
        doReturn(boardPage).when(boardRepository).findBySearchWord(searchWord, searchWord, pageable);

        GetBoardListResponseDto responseDto = boardQueryService.getSearchBoardList(searchWord, pageable);

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("최신 게시글 리스트 조회")
    void getLatestBoardList() {
        Page<Board> boardPage = Page.empty();
        Pageable pageable = PageRequest.of(0, 10);
        GetBoardListResponseDto expectedDto = new GetBoardListResponseDto(boardPage);
        doReturn(boardPage).when(boardRepository).findLatestBoards(pageable);

        GetBoardListResponseDto responseDto = boardQueryService.getLatestBoardList(pageable);

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }
}
