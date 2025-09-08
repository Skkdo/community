package community.back.repository;

import community.back.repository.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByBoardId(Long boardId);

    void deleteByBoardId(Long boardId);
}
