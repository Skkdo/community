package community.back.service;

import community.back.repository.ImageRepository;
import community.back.repository.entity.Image;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;

    public List<Image> findByBoardId(Long boardId) {
        return imageRepository.findByBoardId(boardId);
    }

    @Transactional
    public void saveAll(List<Image> imageList) {
        imageRepository.saveAll(imageList);
    }

    @Transactional
    public void deleteByBoardId(Long boardId) {
        imageRepository.deleteByBoardId(boardId);
    }
}
