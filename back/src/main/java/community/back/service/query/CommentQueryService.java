package community.back.service.query;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.CommentRepository;
import community.back.repository.entity.Comment;
import community.back.service.dto.response.GetCommentListResponseDto;
import community.back.service.util.BoardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final BoardUtil boardUtil;

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ResponseCode.NOT_EXISTED_COMMENT));
    }

    public void deleteByBoardId(Long boardId) {
        commentRepository.deleteByBoardId(boardId);
    }

    public GetCommentListResponseDto getCommentList(Long boardId, Pageable pageable) {
        boardUtil.findById(boardId);
        Page<Comment> commentList = commentRepository.findByBoardIdAndParentIsNull(boardId, pageable);

        return new GetCommentListResponseDto(commentList);
    }

    public GetCommentListResponseDto getChildCommentList(Long parentCommentId, Pageable pageable) {
        findById(parentCommentId);
        Page<Comment> commentList = commentRepository.findByParentId(parentCommentId, pageable);

        return new GetCommentListResponseDto(commentList);
    }


}
