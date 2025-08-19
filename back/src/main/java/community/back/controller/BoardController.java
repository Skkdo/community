package community.back.controller;

import community.back.common.ResponseDto;
import community.back.service.ViewCountService;
import community.back.service.command.BoardCommandService;
import community.back.service.dto.response.GetBoardResponseDto;
import community.back.service.query.BoardQueryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardQueryService boardQueryService;
    private final BoardCommandService boardCommandService;
    private final ViewCountService viewCountService;

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto> getBoard(
            @RequestParam("boardId") Long boardId,
            HttpServletRequest request
    ) {
        String userIdentifier = generateUserIdentifier(request);
        viewCountService.incrementViewCount(boardId, userIdentifier);

        GetBoardResponseDto responseDto = boardQueryService.getBoard(boardId);
        return ResponseEntity.ok().body(ResponseDto.success(responseDto));
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
