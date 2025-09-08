package community.back.service;

import community.back.repository.FavoriteRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Favorite;
import community.back.repository.entity.User;
import community.back.service.dto.response.GetFavoriteResponseDto;
import community.back.service.util.BoardUtil;
import community.back.service.util.UserUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final BoardUtil boardUtil;
    private final UserUtil userUtil;

    @Transactional
    public void deleteByBoardId(Long boardId) {
        favoriteRepository.deleteByBoardId(boardId);
    }

    @Transactional
    public void putFavorite(String email, Long boardId) {

        Board board = boardUtil.findById(boardId);
        User user = userUtil.findByEmail(email);

        Optional<Favorite> optional = favoriteRepository.findByBoardIdAndUserEmail(boardId, email);
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

    public GetFavoriteResponseDto getFavorite(String email, Long boardId) {
        Optional<Favorite> optional = favoriteRepository.findByBoardIdAndUserEmail(boardId, email);
        return new GetFavoriteResponseDto(optional.isPresent());
    }
}
