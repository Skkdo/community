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

        Comment comment = new Comment(board, user, dto);
        commentRepository.save(comment);

        board.increaseCommentCount();
    }

    public void patchComment(Long boardId, Long commentId, String email, PatchCommentRequestDto dto) {
        userUtil.findByEmail(email);
        boardUtil.findById(boardId);

        Comment comment = commentQueryService.findById(commentId);
        String commentWriterEmail = comment.getWriter().getEmail();
        boolean isCommentWriter = commentWriterEmail.equals(email);
        if (!isCommentWriter) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        comment.patchComment(dto);
    }

    public void deleteComment(Long boardId, String email, Long commentId) {

        Board board = boardUtil.findById(boardId);
        userUtil.findByEmail(email);
        Comment comment = commentQueryService.findById(commentId);

        String writerEmail = board.getWriter().getEmail();
        String commentWriterEmail = comment.getWriter().getEmail();

        boolean isWriter = writerEmail.equals(email);
        boolean isCommentWriter = commentWriterEmail.equals(email);
        if (!isWriter && !isCommentWriter) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        commentRepository.delete(comment);
        board.decreaseCommentCount();
    }
}
