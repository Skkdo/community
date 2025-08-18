package community.back.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchBoardRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    private List<String> boardImageList;
}
