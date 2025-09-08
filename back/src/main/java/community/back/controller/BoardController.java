package community.back.controller;

import community.back.common.ResponseDto;
import community.back.service.ViewCountService;
import community.back.service.command.BoardCommandService;
import community.back.service.dto.request.PatchBoardRequestDto;
import community.back.service.dto.request.PostBoardRequestDto;
import community.back.service.dto.response.GetBoardListResponseDto;
import community.back.service.dto.response.GetBoardResponseDto;
import community.back.service.query.BoardQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardQueryService boardQueryService;
    private final BoardCommandService boardCommandService;
    private final ViewCountService viewCountService;

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto> getBoard(
            @PathVariable("boardId") Long boardId,
            HttpServletRequest request
    ) {
        GetBoardResponseDto responseDto = boardQueryService.getBoard(boardId);
        String userIdentifier = generateUserIdentifier(request);
        viewCountService.incrementViewCount(boardId, userIdentifier);
        return ResponseEntity.ok().body(ResponseDto.success(responseDto));
    }

    @GetMapping("/latest-list")
    public ResponseEntity<ResponseDto> getLatestBoardList(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        GetBoardListResponseDto response = boardQueryService.getLatestBoardList(pageable);
        return ResponseEntity.ok().body(ResponseDto.success(response));
    }

    @GetMapping("/search-list/{searchWord}")
    public ResponseEntity<ResponseDto> getSearchBoardList(
            @PathVariable("searchWord") String searchWord,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        GetBoardListResponseDto response = boardQueryService.getSearchBoardList(searchWord, pageable);
        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @GetMapping("/user-board-list")
    public ResponseEntity<ResponseDto> getUserBoardList(
            @AuthenticationPrincipal(expression = "username") String email,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        GetBoardListResponseDto response = boardQueryService.getUserBoardList(email, pageable);
        return ResponseEntity.ok().body(ResponseDto.success(response));
    }

    @PostMapping("")
    public ResponseEntity<ResponseDto> postBoard(
            @RequestBody @Valid PostBoardRequestDto requestDto,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        boardCommandService.postBoard(requestDto, email);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<ResponseDto> patchBoard(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal(expression = "username") String email,
            @RequestBody @Valid PatchBoardRequestDto requestBody
    ) {
        boardCommandService.patchBoard(requestBody, boardId, email);
        return ResponseEntity.ok().body(ResponseDto.success());
    }


    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResponseDto> deleteBoard(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        boardCommandService.deleteBoard(boardId, email);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    private String generateUserIdentifier(HttpServletRequest request) {
        String ip = getIpAddress(request);
        return DigestUtils.md5DigestAsHex(ip.getBytes());
    }

    private String getIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
                "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
