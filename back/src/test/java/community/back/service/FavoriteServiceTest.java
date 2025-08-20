package community.back.service;

import community.back.repository.FavoriteRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Favorite;
import community.back.repository.entity.User;
import community.back.service.util.BoardUtil;
import community.back.service.util.UserUtil;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {
    @InjectMocks
    private FavoriteService favoriteService;
    @Mock
    private FavoriteRepository favoriteRepository;
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

    @Test
    @DisplayName("게시글 아이디로 좋아요 삭제")
    void deleteByBoardId() {
        Board board = board();
        doNothing().when(favoriteRepository).deleteByBoardId(board().getId());

        favoriteService.deleteByBoardId(board().getId());

        verify(favoriteRepository, times(1)).deleteByBoardId(board.getId());
    }

    @Test
    @DisplayName("좋아요")
    void saveFavorite() {
        Board board = board();
        int favoriteCount = board.getFavoriteCount();
        User user = user();
        Favorite favorite = new Favorite(board, user);
        doReturn(board).when(boardUtil).findById(board().getId());
        doReturn(user).when(userUtil).findByEmail(user().getEmail());
        doReturn(Optional.empty()).when(favoriteRepository).findByBoardIdAndUserEmail(board().getId(), user.getEmail());
        doReturn(favorite).when(favoriteRepository).save(any(Favorite.class));

        favoriteService.putFavorite(user.getEmail(), board().getId());

        verify(favoriteRepository, times(1)).save(any(Favorite.class));
        Assertions.assertThat(board.getFavoriteCount()).isEqualTo(favoriteCount + 1);
    }

    @Test
    @DisplayName("좋아요 취소")
    void deleteFavorite() {
        Board board = board();
        int favoriteCount = board.getFavoriteCount();
        User user = user();
        Favorite favorite = new Favorite(board, user);
        doReturn(board).when(boardUtil).findById(board().getId());
        doReturn(user).when(userUtil).findByEmail(user().getEmail());
        doReturn(Optional.of(favorite)).when(favoriteRepository)
                .findByBoardIdAndUserEmail(board().getId(), user.getEmail());

        favoriteService.putFavorite(user.getEmail(), board().getId());

        verify(favoriteRepository, times(1)).delete(any(Favorite.class));
        Assertions.assertThat(board.getFavoriteCount()).isEqualTo(favoriteCount - 1);
    }
}
