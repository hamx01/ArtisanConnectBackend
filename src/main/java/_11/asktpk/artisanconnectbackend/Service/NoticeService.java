package _11.asktpk.artisanconnectbackend.Service;

import _11.asktpk.artisanconnectbackend.Entities.Client;
import _11.asktpk.artisanconnectbackend.Entities.Notice;
import _11.asktpk.artisanconnectbackend.Repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.Repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.DTO.NoticeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private ClientRepository clientRepository;

    public Notice createFromDTO(NoticeDTO dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setDescription(dto.getDescription());
        notice.setPrice(dto.getPrice());
        notice.setCategory(dto.getCategory());
        notice.setImages(dto.getImages());
        notice.setStatus(dto.getStatus());
        notice.setPublishDate(dto.getPublishDate());
        notice.setAttributesNotices(dto.getAttributesNotices());
        notice.setOrders(dto.getOrders());
        notice.setPayments(dto.getPayments());

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o ID: " + dto.getClientId()));
        notice.setClient(client);

        return notice;
    }

    // Metoda do konwersji Notice na DTO
    public NoticeDTO toDTO(Notice notice) {
        return new NoticeDTO(
                notice.getTitle(),
                notice.getClient().getId(),
                notice.getDescription(),
                notice.getPrice(),
                notice.getCategory(),
                notice.getImages(),
                notice.getStatus(),
                notice.getPublishDate(),
                notice.getAttributesNotices(),
                notice.getOrders(),
                notice.getPayments()
        );
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public void addNotice(Notice notice) {
        noticeRepository.save(notice);
    }
}
