package community.back.repository;

import community.back.repository.entity.Like;
import community.back.repository.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {
}
