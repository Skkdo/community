package community.back.repository;

import community.back.repository.entity.Favorite;
import community.back.repository.entity.FavoriteId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    void deleteByBoardId(Long boardId);

    Optional<Favorite> findByBoardIdAndUserEmail(Long boardId, String email);
}
