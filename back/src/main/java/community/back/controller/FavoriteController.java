package community.back.controller;

import community.back.common.ResponseDto;
import community.back.service.FavoriteService;
import community.back.service.dto.response.GetFavoriteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PutMapping("/{boardId}")
    public ResponseEntity<ResponseDto> putFavorite(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        favoriteService.putFavorite(email, boardId);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto> getFavorite(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        GetFavoriteResponseDto response = favoriteService.getFavorite(email, boardId);
        return ResponseEntity.ok().body(ResponseDto.success(response));
    }
}
