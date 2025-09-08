package community.back.service.dto.response;

import lombok.Getter;

@Getter
public class GetFavoriteResponseDto {
    private final boolean liked;

    public GetFavoriteResponseDto(boolean liked) {
        this.liked = liked;
    }
}
