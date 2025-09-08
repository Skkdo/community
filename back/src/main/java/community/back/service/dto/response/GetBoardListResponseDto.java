package community.back.service.dto.response;

import community.back.repository.entity.Board;
import community.back.repository.entity.User;
import community.back.service.dto.item.BoardListItem;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class GetBoardListResponseDto {
    private final Page<BoardListItem> boardList;

    public GetBoardListResponseDto(Page<Board> boardList, User user) {
        this.boardList = BoardListItem.getList(boardList, user);
    }

    public GetBoardListResponseDto(Page<Board> boardList) {
        this.boardList = BoardListItem.getList(boardList);
    }
}
