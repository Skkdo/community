package community.back.service.query;

import community.back.repository.FavoriteRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Favorite;
import community.back.repository.entity.User;
import community.back.service.util.BoardUtil;
import community.back.service.util.UserUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final BoardUtil boardUtil;
    private final UserUtil userUtil;

    public void deleteByBoardId(Long boardId) {
        favoriteRepository.deleteByBoardId(boardId);
    }

    @Transactional
    public void putFavorite(String email, Long boardId) {

        Board board = boardUtil.findById(boardId);
        User user = userUtil.findByEmail(email);

        Optional<Favorite> optional = favoriteRepository.findByBoardIdAndUserEmail(boardId, email);
        // TODO 좋아요 증가, 감소 redis 로직 추가
        if (optional.isEmpty()) {
            Favorite favorite = new Favorite(board, user);
            favoriteRepository.save(favorite);
            board.increaseFavoriteCount();
        } else {
            Favorite favorite = optional.get();
            favoriteRepository.delete(favorite);
            board.decreaseFavoriteCount();
        }
    }
}
