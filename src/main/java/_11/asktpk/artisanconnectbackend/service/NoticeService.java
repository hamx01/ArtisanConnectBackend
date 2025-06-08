package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.dto.AttributeDto;
import _11.asktpk.artisanconnectbackend.dto.NoticeRequestDTO;
import _11.asktpk.artisanconnectbackend.entities.*;
import _11.asktpk.artisanconnectbackend.repository.*;
import _11.asktpk.artisanconnectbackend.dto.NoticeResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeService {
    private static final Logger logger = LogManager.getLogger(NoticeService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final NoticeRepository noticeRepository;
    private final ClientRepository clientRepository;
    private final ImageService imageService;
    private final AttributesRepository attributesRepository;
    private final AttributeValuesRepository attributeValuesRepository;
    private final AttributesNoticeRepository attributesNoticeRepository;

    public NoticeService(NoticeRepository noticeRepository,
                         ClientRepository clientRepository,
                         ImageService imageService,
                         AttributesRepository attributesRepository,
                         AttributeValuesRepository attributeValuesRepository,
                         AttributesNoticeRepository attributesNoticeRepository) {
        this.noticeRepository = noticeRepository;
        this.clientRepository = clientRepository;
        this.imageService = imageService;
        this.attributesRepository = attributesRepository;
        this.attributeValuesRepository = attributeValuesRepository;
        this.attributesNoticeRepository = attributesNoticeRepository;
    }

    public Notice fromDTO(NoticeRequestDTO dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setDescription(dto.getDescription());
        notice.setPrice(dto.getPrice());
        notice.setCategory(dto.getCategory());
        notice.setStatus(dto.getStatus());

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o ID: " + dto.getClientId()));
        notice.setClient(client);

        return notice;
    }

    private NoticeResponseDTO toDTO(Notice notice) {
        NoticeResponseDTO dto = new NoticeResponseDTO();
        dto.setNoticeId(notice.getIdNotice());
        dto.setTitle(notice.getTitle());
        dto.setClientId(notice.getClient().getId());
        dto.setDescription(notice.getDescription());
        dto.setPrice(notice.getPrice());
        dto.setCategory(notice.getCategory());
        dto.setStatus(notice.getStatus());
        dto.setPublishDate(notice.getPublishDate());

        List<AttributeDto> attributes = new ArrayList<>();
        if (notice.getAttributesNotices() != null) {
            for (AttributesNotice an : notice.getAttributesNotices()) {
                AttributeDto attr = new AttributeDto();
                attr.setName(an.getAttributeValue().getAttribute().getName());
                attr.setValue(an.getAttributeValue().getValue());
                attributes.add(attr);
            }
        }
        dto.setAttributes(attributes);

        return dto;
    }

    public List<NoticeResponseDTO> getAllNotices() {
        List<NoticeResponseDTO> result = new ArrayList<>();
        for (Notice notice : noticeRepository.findAll()) {
            result.add(toDTO(notice));
        }
        return result;
    }

    public NoticeResponseDTO getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id));
        return toDTO(notice);
    }

    public Notice getNoticeByIdEntity(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id));
    }

    public Long addNotice(NoticeRequestDTO dto) {
        Notice notice = fromDTO(dto);
        notice.setPublishDate(LocalDateTime.now());
        Notice savedNotice = noticeRepository.save(notice);

        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            saveAttributes(savedNotice.getIdNotice(), dto.getAttributes());
        }

        return savedNotice.getIdNotice();
    }

    private void saveAttributes(Long noticeId, List<AttributeDto> attributeDtos) {
        for (AttributeDto attributeDto : attributeDtos) {
            Attributes attribute = attributesRepository.findByName(attributeDto.getName())
                    .orElseGet(() -> {
                        Attributes newAttribute = new Attributes();
                        newAttribute.setName(attributeDto.getName());
                        return attributesRepository.save(newAttribute);
                    });

            AttributeValues attributeValue = attributeValuesRepository
                    .findByAttributeAndValue(attribute, attributeDto.getValue())
                    .orElseGet(() -> {
                        AttributeValues newValue = new AttributeValues();
                        newValue.setAttribute(attribute);
                        newValue.setValue(attributeDto.getValue());
                        return attributeValuesRepository.save(newValue);
                    });

            AttributesNotice attributesNotice = new AttributesNotice();
            attributesNotice.setNotice_id(noticeId);
            attributesNotice.setAttributeValue(attributeValue);
            attributesNoticeRepository.save(attributesNotice);
        }
    }

    public boolean noticeExists(Long id) {
        return noticeRepository.existsById(id);
    }

    public NoticeResponseDTO updateNotice(Long id, NoticeRequestDTO dto) {
        Notice existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id));

        existingNotice.setTitle(dto.getTitle());
        existingNotice.setDescription(dto.getDescription());
        existingNotice.setPrice(dto.getPrice());
        existingNotice.setCategory(dto.getCategory());
        existingNotice.setStatus(dto.getStatus());

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

            List<String> imagesList = new ArrayList<>();

            try {
                imagesList = imageService.getImagesList(id);
            } catch (Exception e) {
                logger.info("There weren't any images for notice with ID: " + id + ". Skipping deletion of images. Message: " + e.getMessage());
            }

            try {
                for (String imageName : imagesList) {
                    imageService.deleteImage(uploadDir, imageName);
                }
            } catch (Exception e) {
                logger.info("There were some issues while deleting images for notice with ID: " + id + ". Message: " + e.getMessage());
            }
        } else {
            throw new EntityNotFoundException("Nie znaleziono ogłoszenia o ID: " + id);
        }
    }

    public boolean isNoticeOwnedByClient(long noticeId, long clientId) {
        return noticeRepository.existsByIdNoticeAndClientId(noticeId, clientId);
    }

    public void boostNotice(long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException("Ogłoszenie o ID " + noticeId + " nie istnieje."));

        notice.setPublishDate(LocalDateTime.now());

        noticeRepository.save(notice);

    }


}
