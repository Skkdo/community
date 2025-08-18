package community.back.service.dto.response;

import community.back.repository.entity.User;
import lombok.Getter;

@Getter
public class GetUserResponseDto {
    private final String email;
    private final String nickname;
    private final String profileImage;

    public GetUserResponseDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
