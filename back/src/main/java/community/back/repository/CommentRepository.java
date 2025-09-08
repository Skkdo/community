package community.back.repository;

import community.back.repository.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByBoardId(Long boardId);

    Page<Comment> findByBoardIdAndParentIsNull(Long boardId, Pageable pageable);

    Page<Comment> findByParentId(Long parentCommentId, Pageable pageable);
}
