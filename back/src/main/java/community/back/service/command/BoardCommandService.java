package community.back.service.command;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import community.back.repository.BoardRepository;
import community.back.repository.entity.Board;
import community.back.repository.entity.Image;
import community.back.repository.entity.User;
import community.back.service.dto.request.PatchBoardRequestDto;
import community.back.service.dto.request.PostBoardRequestDto;
import community.back.service.query.CommentQueryService;
import community.back.service.FavoriteService;
import community.back.service.ImageService;
import community.back.service.util.BoardUtil;
import community.back.service.util.UserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardCommandService {
    private final BoardRepository boardRepository;
    private final BoardUtil boardUtil;
    private final ImageService imageService;
    private final UserUtil userUtil;
    private final FavoriteService favoriteService;
    private final CommentQueryService commentService;

    public void postBoard(PostBoardRequestDto dto, String email) {
        User user = userUtil.findByEmail(email);
        Board board = Board.from(dto, user);
        boardRepository.save(board);

        List<Image> images = dto.getBoardImageList().stream()
                .map(image -> Image.from(board, image))
                .toList();
        imageService.saveAll(images);
    }

    public void patchBoard(PatchBoardRequestDto dto, Long id, String email) {
        Board board = boardUtil.findById(id);
        userUtil.findByEmail(email);

        isWriter(email, board.getWriter().getEmail());

        board.patchBoard(dto);
        boardRepository.save(board);
        imageService.deleteByBoardId(id);
        List<Image> images = dto.getBoardImageList().stream()
                .map(image -> Image.from(board, image))
                .toList();
        imageService.saveAll(images);
    }

    public void deleteBoard(Long id, String email) {
        Board board = boardUtil.findById(id);
        userUtil.findByEmail(email);

        isWriter(email, board.getWriter().getEmail());

        imageService.deleteByBoardId(id);
        favoriteService.deleteByBoardId(id);
        commentService.deleteByBoardId(id);
        boardRepository.delete(board);
    }

    public void isWriter(String email, String writerEmail) {
        if (!writerEmail.equals(email)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }
    }
}
