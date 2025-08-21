package community.back.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBoardRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    private List<String> boardImageList;
}
