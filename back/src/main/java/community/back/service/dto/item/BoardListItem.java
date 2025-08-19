package community.back.service.dto.item;

import community.back.repository.entity.Board;
import community.back.repository.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListItem {
    private Long boardId;
    private String title;
    private String content;
    private String boardTitleImage;
    private int favoriteCount;
    private int commentCount;
    private int viewCount;
    private LocalDateTime writeDatetime;
    private String writerNickname;
    private String writerProfileImage;


    public static BoardListItem from(Board board, User user) {
        return BoardListItem.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .boardTitleImage(board.getTitleImage())
                .favoriteCount(board.getFavoriteCount())
                .commentCount(board.getCommentCount())
                .viewCount(board.getViewCount())
                .writeDatetime(board.getCreatedAt())
                .writerNickname(user.getNickname())
                .writerProfileImage(user.getProfileImage())
                .build();
    }

    public static BoardListItem from(Board board) {
        return BoardListItem.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .boardTitleImage(board.getTitleImage())
                .favoriteCount(board.getFavoriteCount())
                .commentCount(board.getCommentCount())
                .viewCount(board.getViewCount())
                .writeDatetime(board.getCreatedAt())
                .writerNickname(board.getWriter().getNickname())
                .writerProfileImage(board.getWriter().getProfileImage())
                .build();
    }

    public static Page<BoardListItem> getList(Page<Board> boardList) {
        return boardList.map(BoardListItem::from);
    }

    public static Page<BoardListItem> getList(Page<Board> boardList, User user) {
        return boardList.map(board -> BoardListItem.from(board, user));
    }
}
