package community.back.service.command;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.CommentRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Comment;
import community.back.repository.entity.User;
import community.back.service.dto.request.PatchCommentRequestDto;
import community.back.service.dto.request.PostCommentRequestDto;
import community.back.service.query.CommentQueryService;
import community.back.service.util.BoardUtil;
import community.back.service.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {
    private final CommentRepository commentRepository;
    private final CommentQueryService commentQueryService;
    private final BoardUtil boardUtil;
    private final UserUtil userUtil;

    public void postComment(Long boardId, String email, PostCommentRequestDto dto) {
        Board board = boardUtil.findById(boardId);
        User user = userUtil.findByEmail(email);

        if (dto.getParentCommentId() == null) {
            commentRepository.save(new Comment(board, user, dto));
        } else {
            Comment comment = commentQueryService.findById(dto.getParentCommentId());
            commentRepository.save(new Comment(board, user, dto, comment));
        }
        board.increaseCommentCount();
    }

    public void patchComment(Long boardId, Long commentId, String email, PatchCommentRequestDto dto) {
        userUtil.findByEmail(email);
        boardUtil.findById(boardId);

        Comment comment = commentQueryService.findById(commentId);
        isWriter(email, comment.getWriter().getEmail());

        comment.patchComment(dto);
    }

    public void deleteComment(Long boardId, String email, Long commentId) {
        Board board = boardUtil.findById(boardId);
        userUtil.findByEmail(email);
        Comment comment = commentQueryService.findById(commentId);

        isWriter(email, board.getWriter().getEmail());
        isWriter(email, comment.getWriter().getEmail());

        commentRepository.delete(comment);
        board.decreaseCommentCount();
    }

    public void isWriter(String email, String writerEmail) {
        if (!writerEmail.equals(email)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }
    }
}
