package community.back.service.dto.item;

import community.back.repository.entity.Comment;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class CommentListItem {
    private final Long commentId;
    private final String nickname;
    private final String profileImage;
    private final LocalDateTime writeDatetime;
    private final String content;

    private CommentListItem(Comment comment) {
        this.commentId = comment.getId();
        this.nickname = comment.getWriter().getNickname();
        this.profileImage = comment.getWriter().getProfileImage();
        this.writeDatetime = comment.getCreatedAt();
        this.content = comment.getContent();
    }

    public static Page<CommentListItem> getList(Page<Comment> commentList) {
        return commentList.map(CommentListItem::new);
    }
}
