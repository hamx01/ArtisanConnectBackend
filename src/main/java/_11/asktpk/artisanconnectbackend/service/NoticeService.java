package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ClientRepository clientRepository;

    public NoticeService(NoticeRepository noticeRepository, ClientRepository clientRepository) {
        this.noticeRepository = noticeRepository;
        this.clientRepository = clientRepository;
    }

    private Notice fromDTO(NoticeDTO dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setDescription(dto.getDescription());
        notice.setPrice(dto.getPrice());
        notice.setCategory(dto.getCategory());
        notice.setImages(dto.getImages());
        notice.setStatus(dto.getStatus());
        notice.setPublishDate(dto.getPublishDate());
        notice.setAttributesNotices(dto.getAttributesNotices());

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o ID: " + dto.getClientId()));
        notice.setClient(client);

        return notice;
    }

    private NoticeDTO toDTO(Notice notice) {
        NoticeDTO dto = new NoticeDTO();
        dto.setNoticeId(notice.getIdNotice());
        dto.setTitle(notice.getTitle());
        dto.setClientId(notice.getClient().getId());
        dto.setDescription(notice.getDescription());
        dto.setPrice(notice.getPrice());
        dto.setCategory(notice.getCategory());
        dto.setImages(notice.getImages());
        dto.setStatus(notice.getStatus());
        dto.setPublishDate(notice.getPublishDate());
        dto.setAttributesNotices(notice.getAttributesNotices());

        return dto;
    }

    public List<NoticeDTO> getAllNotices() {
        List<NoticeDTO> result = new ArrayList<>();
        for (Notice notice : noticeRepository.findAll()) {
            result.add(toDTO(notice));
        }
        return result;
    }

    public NoticeDTO getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id));
        return toDTO(notice);
    }

    public void addNotice(NoticeDTO dto) {
        noticeRepository.save(fromDTO(dto));
    }

    public boolean noticeExists(Long id) {
        return noticeRepository.existsById(id);
    }

    public NoticeDTO updateNotice(Long id, NoticeDTO dto) {
        Notice existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id));

        existingNotice.setTitle(dto.getTitle());
        existingNotice.setDescription(dto.getDescription());
        existingNotice.setPrice(dto.getPrice());
        existingNotice.setCategory(dto.getCategory());
        existingNotice.setImages(dto.getImages());
        existingNotice.setStatus(dto.getStatus());
        existingNotice.setAttributesNotices(dto.getAttributesNotices());

        if (dto.getClientId() != null && !dto.getClientId().equals(existingNotice.getClient().getId())) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o ID: " + dto.getClientId()));
            existingNotice.setClient(client);
        }

        return toDTO(noticeRepository.save(existingNotice));
    }

    public void deleteNotice(Long id) {
        if (noticeExists(id)) {
            noticeRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id);
        }
    }

    public String saveImage(Long noticeId, MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/images/notices/" + noticeId;
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // szukanie nazwy pliku
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

            List<Path> filesInDirectory = Files.list(uploadPath)
                    .filter(Files::isRegularFile)
                    .toList();

            int maxNumber = filesInDirectory.stream()
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.startsWith(baseName) && name.endsWith(extension))
                    .map(name -> name.substring(baseName.length(), name.length() - extension.length()))
                    .filter(number -> number.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);

            fileName = baseName + (maxNumber + 1) + extension;
        }
        //koniec szukania nazwy pliku

        if(fileName == null) {
            throw new IOException("Nie można znaleźć nazwy pliku");
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + noticeId));
        List<String> images = notice.getImages();
        images.add(filePath.toString());
        notice.setImages(images);
        noticeRepository.save(notice);

        return filePath.toString();
    }
}
