package community.back.controller;

import community.back.common.ResponseDto;
import community.back.service.command.CommentCommandService;
import community.back.service.dto.request.PatchCommentRequestDto;
import community.back.service.dto.request.PostCommentRequestDto;
import community.back.service.dto.response.GetCommentListResponseDto;
import community.back.service.query.CommentQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @GetMapping("/{boardId}/comment-list")
    public ResponseEntity<ResponseDto> getCommentList(
            @PathVariable("boardId") Long boardId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        GetCommentListResponseDto response = commentQueryService.getCommentList(boardId, pageable);
        return ResponseEntity.ok().body(ResponseDto.success(response));
    }

    @GetMapping("/{commentId}/child-comment-list")
    public ResponseEntity<ResponseDto> getChildCommentList(
            @PathVariable("commentId") Long commentId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        GetCommentListResponseDto response = commentQueryService.getChildCommentList(commentId, pageable);
        return ResponseEntity.ok().body(ResponseDto.success(response));
    }

    @PostMapping("/{boardId}/comment")
    public ResponseEntity<ResponseDto> postComment(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal(expression = "username") String email,
            @RequestBody @Valid PostCommentRequestDto dto
    ) {
        commentCommandService.postComment(boardId, email, dto);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    @PatchMapping("{boardId}/{commentId}")
    public ResponseEntity<ResponseDto> patchComment(
            @AuthenticationPrincipal(expression = "username") String email,
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid PatchCommentRequestDto dto
    ) {
        commentCommandService.patchComment(boardId, commentId, email, dto);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    @DeleteMapping("/{boardId}/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(
            @AuthenticationPrincipal(expression = "username") String email,
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId
    ) {
        commentCommandService.deleteComment(boardId, email, commentId);
        return ResponseEntity.ok(ResponseDto.success());
    }
}
