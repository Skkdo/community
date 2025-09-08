package community.back.repository.entity;

import community.back.service.dto.request.PatchCommentRequestDto;
import community.back.service.dto.request.PostCommentRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE comment SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content" , nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_email" , nullable = false)
    private User writer;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id" , nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    public void patchComment(PatchCommentRequestDto dto){
        this.content = dto.getContent();
    }

    public Comment(Board board, User user, PostCommentRequestDto dto){
        this.content = dto.getContent();
        this.writer = user;
        this.board = board;
    }

    public Comment(Board board, User user, PostCommentRequestDto dto, Comment comment){
        this.content = dto.getContent();
        this.writer = user;
        this.board = board;
        this.parent = comment;
    }
}
