package community.back.service.query;

import community.back.repository.ImageRepository;
import community.back.repository.entity.Image;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public List<Image> findByBoardId(Long boardId) {
        return imageRepository.findByBoardId(boardId);
    }

    public void saveAll(List<Image> imageList) {
        imageRepository.saveAll(imageList);
    }

    public void deleteByBoardId(Long boardId) {
        imageRepository.deleteByBoardId(boardId);
    }
}
