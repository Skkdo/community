package community.back.service.dto.response;

import community.back.repository.entity.Board;
import community.back.repository.entity.Image;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class GetBoardResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final List<String> imageList;
    private final LocalDateTime writeDatetime;
    private final String writerEmail;
    private final String writerNickname;
    private final String writerProfileImage;

    public GetBoardResponseDto(Board board, List<Image> list) {

        List<String> imageList = new ArrayList<>();
        for (Image image : list) {
            String boardImage = image.getImage();
            imageList.add(boardImage);
        }

        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.imageList = imageList;
        this.writeDatetime = board.getCreatedAt();
        this.writerEmail = board.getWriter().getEmail();
        this.writerNickname = board.getWriter().getNickname();
        this.writerProfileImage = board.getWriter().getProfileImage();
    }
}
