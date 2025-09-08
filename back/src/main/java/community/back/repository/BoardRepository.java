package community.back.repository;

import community.back.repository.entity.Board;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.writer " +
            "WHERE b.id = :id")
    Optional<Board> findByIdWithWriter(@Param("id") Long id);

    Page<Board> findByWriterEmail(String email, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.writer " +
            "WHERE (b.title LIKE %:title% OR b.content LIKE %:content%)")
    Page<Board> findBySearchWord(@Param("title") String title, @Param("content") String content, Pageable pageable);

    @Query("SELECT b FROM Board b JOIN FETCH b.writer")
    Page<Board> findLatestBoards(Pageable pageable);

    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + :incrementCount WHERE b.id = :boardId")
    void updateViewCount(@Param("boardId") Long boardId, @Param("incrementCount") Long incrementCount);
}
