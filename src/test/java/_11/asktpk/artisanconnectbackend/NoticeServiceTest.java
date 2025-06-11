package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.dto.AttributeDto;
import _11.asktpk.artisanconnectbackend.dto.NoticeRequestDTO;
import _11.asktpk.artisanconnectbackend.dto.NoticeResponseDTO;
import _11.asktpk.artisanconnectbackend.entities.*;
import _11.asktpk.artisanconnectbackend.repository.*;
import _11.asktpk.artisanconnectbackend.service.ImageService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
    private final NoticeRepository noticeRepository = mock(NoticeRepository.class);

    @Mock
    private final ClientRepository clientRepository = mock(ClientRepository.class);


    @Mock
    private final AttributesRepository attributesRepository = mock(AttributesRepository.class);

    @Mock
    private final AttributeValuesRepository attributeValuesRepository = mock(AttributeValuesRepository.class);

    @Mock
    private final AttributesNoticeRepository attributesNoticeRepository = mock(AttributesNoticeRepository.class);

    @InjectMocks
    private NoticeService noticeService;

    private Notice sampleNotice;
    private NoticeRequestDTO sampleNoticeRequest;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        System.out.println("Inicjalizacja danych testowych przed każdym testem");

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
    void getAllNotices_ShouldReturnListOfNotices() {
        System.out.println("Test: getAllNotices_ShouldReturnListOfNotices - powinien zwrócić listę ogłoszeń");

        when(noticeRepository.findAll()).thenReturn(List.of(sampleNotice));

        List<NoticeResponseDTO> result = noticeService.getAllNotices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleNotice.getIdNotice(), result.getFirst().getNoticeId());

        System.out.println("Pomyślnie zwrócono listę ogłoszeń");
    }

    @Test
    void getNoticeById_WhenNoticeExists_ShouldReturnNotice() {
        System.out.println("Test: getNoticeById_WhenNoticeExists_ShouldReturnNotice - powinien zwrócić ogłoszenie gdy istnieje");

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(sampleNotice));

        NoticeResponseDTO result = noticeService.getNoticeById(1L);

        assertNotNull(result);
        assertEquals(sampleNotice.getIdNotice(), result.getNoticeId());

        System.out.println("Pomyślnie zwrócono istniejące ogłoszenie");
    }

    @Test
    void getNoticeById_WhenNoticeNotExists_ShouldThrowException() {
        System.out.println("Test: getNoticeById_WhenNoticeNotExists_ShouldThrowException - powinien rzucić wyjątek gdy ogłoszenie nie istnieje");

        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> noticeService.getNoticeById(1L));

        System.out.println("Pomyślnie rzucono wyjątek dla nieistniejącego ogłoszenia");
    }

    @Test
    void addNotice_WithValidData_ShouldCreateNotice() {
        System.out.println("Test: addNotice_WithValidData_ShouldCreateNotice - powinien utworzyć nowe ogłoszenie przy poprawnych danych");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient));
        when(noticeRepository.save(any(Notice.class))).thenReturn(sampleNotice);

        Long result = noticeService.addNotice(sampleNoticeRequest);

        assertNotNull(result);
        assertEquals(1L, result);

        verify(noticeRepository, times(1)).save(any(Notice.class));

        System.out.println("Pomyślnie utworzono nowe ogłoszenie");
    }

    @Test
    void addNotice_WithAttributes_ShouldSaveAttributes() {
        System.out.println("Test: addNotice_WithAttributes_ShouldSaveAttributes - powinien zapisać atrybuty ogłoszenia");

        AttributeDto attributeDto = new AttributeDto();
        attributeDto.setName("Materiał");
        attributeDto.setValue("Drewno");
        sampleNoticeRequest.setAttributes(List.of(attributeDto));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(sampleClient));
        when(noticeRepository.save(any(Notice.class))).thenReturn(sampleNotice);
        when(attributesRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(attributeValuesRepository.findByAttributeAndValue(any(), anyString())).thenReturn(Optional.empty());

        Long result = noticeService.addNotice(sampleNoticeRequest);

        assertNotNull(result);
        verify(attributesNoticeRepository, times(1)).save(any(AttributesNotice.class));

        System.out.println("Pomyślnie zapisano atrybuty ogłoszenia");
    }


    @Test
    void deleteNotice_WhenNoticeExists_ShouldDeleteNotice() {
        System.out.println("Test: deleteNotice_WhenNoticeExists_ShouldDeleteNotice - powinien usunąć istniejące ogłoszenie");

        when(noticeRepository.existsById(1L)).thenReturn(true);

        noticeService.deleteNotice(1L);

        verify(noticeRepository, times(1)).deleteById(1L);

        System.out.println("Pomyślnie usunięto istniejące ogłoszenie");
    }

    @Test
    void isNoticeOwnedByClient_WhenOwned_ShouldReturnTrue() {
        System.out.println("Test: isNoticeOwnedByClient_WhenOwned_ShouldReturnTrue - powinien zwrócić true gdy ogłoszenie należy do klienta");

        when(noticeRepository.existsByIdNoticeAndClientId(1L, 1L)).thenReturn(true);

        boolean result = noticeService.isNoticeOwnedByClient(1L, 1L);

        assertTrue(result);

        System.out.println("Pomyślnie zwrócono true dla ogłoszenia należącego do klienta");
    }

    @Test
    void boostNotice_ShouldUpdatePublishDate() {
        System.out.println("Test: boostNotice_ShouldUpdatePublishDate - powinien zaktualizować datę publikacji");

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(sampleNotice));
        when(noticeRepository.save(any(Notice.class))).thenReturn(sampleNotice);

        noticeService.boostNotice(1L);

        assertNotNull(sampleNotice.getPublishDate());
        verify(noticeRepository, times(1)).save(sampleNotice);

        System.out.println("Pomyślnie zaktualizowano datę publikacji ogłoszenia");
    }
}