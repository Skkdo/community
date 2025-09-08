package community.back.service.dto.response;

import community.back.repository.entity.Comment;
import community.back.service.dto.item.CommentListItem;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class GetCommentListResponseDto {
    private final Page<CommentListItem> commentList;

    public GetCommentListResponseDto(Page<Comment> commentList) {
        this.commentList = CommentListItem.getList(commentList);
    }
}
