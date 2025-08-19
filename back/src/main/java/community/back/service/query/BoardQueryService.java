package community.back.service.query;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.BoardRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Image;
import community.back.repository.entity.User;
import community.back.service.ImageService;
import community.back.service.dto.response.GetBoardListResponseDto;
import community.back.service.dto.response.GetBoardResponseDto;
import community.back.service.util.UserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryService {
    private final BoardRepository boardRepository;
    private final ImageService imageService;
    private final UserUtil userUtil;

    public GetBoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findByIdWithWriter(id).orElseThrow(
                () -> new BusinessException(ResponseCode.NOT_EXISTED_BOARD));
        List<Image> imageList = imageService.findByBoardId(id);
        return new GetBoardResponseDto(board, imageList);
    }

    public GetBoardListResponseDto getUserBoardList(String email, Pageable pageable) {
        User user = userUtil.findByEmail(email);
        Page<Board> boardList = boardRepository.findByWriterEmail(email, pageable);
        return new GetBoardListResponseDto(boardList, user);
    }

    public GetBoardListResponseDto getSearchBoardList(String searchWord, Pageable pageable) {
        Page<Board> boardList = boardRepository.findBySearchWord(searchWord, searchWord, pageable);
        return new GetBoardListResponseDto(boardList);
    }

    public GetBoardListResponseDto getLatestBoardList(Pageable pageable) {
        Page<Board> latestBoardList = boardRepository.findLatestBoards(pageable);
        return new GetBoardListResponseDto(latestBoardList);
    }

}
