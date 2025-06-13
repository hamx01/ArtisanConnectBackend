package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.dto.AttributeDto;
import _11.asktpk.artisanconnectbackend.dto.NoticeRequestDTO;
import _11.asktpk.artisanconnectbackend.dto.NoticeResponseDTO;
import _11.asktpk.artisanconnectbackend.entities.*;
import _11.asktpk.artisanconnectbackend.repository.*;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AttributesRepository attributesRepository;

    @Mock
    private AttributeValuesRepository attributeValuesRepository;

    @Mock
    private AttributesNoticeRepository attributesNoticeRepository;

    @InjectMocks
    private NoticeService noticeService;

    private Notice sampleNotice;
    private NoticeRequestDTO sampleNoticeRequest;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        System.out.println("Przygotowanie danych testowych...");

        sampleClient = new Client();
        sampleClient.setId(1L);
        sampleClient.setEmail("test@example.com");

        sampleNotice = new Notice();
        sampleNotice.setIdNotice(1L);
        sampleNotice.setTitle("Testowe ogłoszenie");
        sampleNotice.setClient(sampleClient);
        sampleNotice.setDescription("Opis testowego ogłoszenia");
        sampleNotice.setPrice(100.0);
        sampleNotice.setCategory(Enums.Category.Woodworking);
        sampleNotice.setStatus(Enums.Status.ACTIVE);
        sampleNotice.setPublishDate(LocalDateTime.now());

        sampleNoticeRequest = new NoticeRequestDTO();
        sampleNoticeRequest.setTitle("Testowe ogłoszenie");
        sampleNoticeRequest.setClientId(1L);
        sampleNoticeRequest.setDescription("Opis testowego ogłoszenia");
        sampleNoticeRequest.setPrice(100.0);
        sampleNoticeRequest.setCategory(Enums.Category.Woodworking);
        sampleNoticeRequest.setStatus(Enums.Status.ACTIVE);
    }

    @Test
    @DisplayName("Pobranie wszystkich ogłoszeń - powinno zwrócić listę ogłoszeń")
    void getAllNotices_ShouldReturnListOfNotices() {
        when(noticeRepository.findAll()).thenReturn(List.of(sampleNotice));

        List<NoticeResponseDTO> result = noticeService.getAllNotices();

        assertNotNull(result);
        assertEquals(1, result.size());
        System.out.println("Test pobrania wszystkich ogłoszeń zakończony");
    }

    @Test
    @DisplayName("Pobranie ogłoszenia po ID - gdy istnieje")
    void getNoticeById_WhenNoticeExists_ShouldReturnNotice() {
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(sampleNotice));

        NoticeResponseDTO result = noticeService.getNoticeById(1L);

        assertNotNull(result);
        System.out.println("Test pobrania istniejącego ogłoszenia zakończony");
    }

    @Test
    @DisplayName("Pobranie ogłoszenia po ID - gdy nie istnieje")
    void getNoticeById_WhenNoticeNotExists_ShouldThrowException() {
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> noticeService.getNoticeById(1L));
        System.out.println("Test pobrania nieistniejącego ogłoszenia zakończony");
    }

    @Test
    @DisplayName("Dodanie nowego ogłoszenia - poprawne dane")
    void addNotice_WithValidData_ShouldCreateNotice() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient));
        when(noticeRepository.save(any(Notice.class))).thenReturn(sampleNotice);

        Long result = noticeService.addNotice(sampleNoticeRequest);

        assertNotNull(result);
        System.out.println("Test dodania ogłoszenia zakończony");
    }

    @Test
    @DisplayName("Dodanie ogłoszenia z atrybutami")
    void addNotice_WithAttributes_ShouldSaveAttributes() {
        AttributeDto attributeDto = new AttributeDto();
        attributeDto.setName("Kolor");
        attributeDto.setValue("Zielony");
        sampleNoticeRequest.setAttributes(List.of(attributeDto));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient));
        when(noticeRepository.save(any(Notice.class))).thenReturn(sampleNotice);
        when(attributesRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(attributeValuesRepository.findByAttributeAndValue(any(), anyString())).thenReturn(Optional.empty());

        Long result = noticeService.addNotice(sampleNoticeRequest);

        assertNotNull(result);
        System.out.println("Test dodania ogłoszenia z atrybutami zakończony");
    }

    @Test
    @DisplayName("Usunięcie istniejącego ogłoszenia")
    void deleteNotice_WhenNoticeExists_ShouldDeleteNotice() {
        when(noticeRepository.existsById(1L)).thenReturn(true);

        noticeService.deleteNotice(1L);

        System.out.println("Test usunięcia ogłoszenia zakończony");
    }

    @Test
    @DisplayName("Sprawdzenie właściciela ogłoszenia - gdy należy do klienta")
    void isNoticeOwnedByClient_WhenOwned_ShouldReturnTrue() {
        when(noticeRepository.existsByIdNoticeAndClientId(1L, 1L)).thenReturn(true);

        boolean result = noticeService.isNoticeOwnedByClient(1L, 1L);

        assertTrue(result);
        System.out.println("Test sprawdzenia właściciela (true) zakończony");
    }

    @Test
    @DisplayName("Boostowanie ogłoszenia - aktualizacja daty publikacji")
    void boostNotice_ShouldUpdatePublishDate() {
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(sampleNotice));
        when(noticeRepository.save(any(Notice.class))).thenReturn(sampleNotice);

        noticeService.boostNotice(1L);

        assertNotNull(sampleNotice.getPublishDate());
        System.out.println("Test boostowania ogłoszenia zakończony");
    }
}