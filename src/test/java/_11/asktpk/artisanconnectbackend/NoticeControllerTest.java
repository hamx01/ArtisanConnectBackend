package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.controller.NoticeController;
import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeControllerTest {

    @Mock
    private final NoticeService noticeService = mock(NoticeService.class);

    @Mock
    private final ClientService clientService = mock(ClientService.class);

    @Mock
    private final Tools tools = mock(Tools.class);

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private NoticeController noticeController;

    private NoticeResponseDTO sampleNotice;
    private NoticeRequestDTO sampleNoticeRequest;

    @BeforeEach
    void setUp() {
        System.out.println("Inicjalizacja danych testowych przed każdym testem");

        sampleNotice = new NoticeResponseDTO();
        sampleNotice.setNoticeId(1L);
        sampleNotice.setTitle("Testowe ogłoszenie");
        sampleNotice.setClientId(1L);
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

        when(noticeService.getAllNotices()).thenReturn(List.of(sampleNotice));

        List<NoticeResponseDTO> result = noticeController.getAllNotices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleNotice.getNoticeId(), result.getFirst().getNoticeId());

        System.out.println("Pomyślnie zwrócono listę ogłoszeń");
    }

    @Test
    void getNoticeById_WhenNoticeExists_ShouldReturnNotice() {
        System.out.println("Test: getNoticeById_WhenNoticeExists_ShouldReturnNotice - powinien zwrócić ogłoszenie gdy istnieje");

        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.getNoticeById(1L)).thenReturn(sampleNotice);

        ResponseEntity<?> response = noticeController.getNoticeById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleNotice, response.getBody());

        System.out.println("Pomyślnie zwrócono istniejące ogłoszenie");
    }

    @Test
    void getNoticeById_WhenNoticeNotExists_ShouldReturnNotFound() {
        System.out.println("Test: getNoticeById_WhenNoticeNotExists_ShouldReturnNotFound - powinien zwrócić 404 gdy ogłoszenie nie istnieje");

        when(noticeService.noticeExists(1L)).thenReturn(false);

        ResponseEntity<?> response = noticeController.getNoticeById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        System.out.println("Pomyślnie zwrócono status 404 dla nieistniejącego ogłoszenia");
    }

    @Test
    void addNotice_WithValidData_ShouldCreateNotice() {
        System.out.println("Test: addNotice_WithValidData_ShouldCreateNotice - powinien utworzyć nowe ogłoszenie przy poprawnych danych");

        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(clientService.clientExists(1L)).thenReturn(true);
        when(noticeService.addNotice(any(NoticeRequestDTO.class))).thenReturn(1L);

        ResponseEntity<NoticeAdditionDTO> response = noticeController.addNotice(sampleNoticeRequest, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getNoticeId());
        assertEquals("Dodano ogłoszenie.", response.getBody().getMessage());

        System.out.println("Pomyślnie utworzono nowe ogłoszenie");
    }

    @Test
    void addNotice_WithInvalidCategory_ShouldReturnBadRequest() {
        System.out.println("Test: addNotice_WithInvalidCategory_ShouldReturnBadRequest - powinien zwrócić błąd dla nieprawidłowej kategorii");

        sampleNoticeRequest.setCategory(null);

        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(clientService.clientExists(1L)).thenReturn(true);

        ResponseEntity<NoticeAdditionDTO> response = noticeController.addNotice(sampleNoticeRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nie ma takiej kategorii", response.getBody().getMessage());

        System.out.println("Pomyślnie zwrócono błąd dla nieprawidłowej kategorii");
    }

    @Test
    void addNotice_WhenClientNotExists_ShouldReturnBadRequest() {
        System.out.println("Test: addNotice_WhenClientNotExists_ShouldReturnBadRequest - powinien zwrócić błąd gdy klient nie istnieje");

        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(clientService.clientExists(1L)).thenReturn(false);

        ResponseEntity<NoticeAdditionDTO> response = noticeController.addNotice(sampleNoticeRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Nie znaleziono klienta o ID:"));

        System.out.println("Pomyślnie zwrócono błąd dla nieistniejącego klienta");
    }

    @Test
    void editNotice_WhenNoticeExistsAndOwnedByClient_ShouldUpdateNotice() {
        System.out.println("Test: editNotice_WhenNoticeExistsAndOwnedByClient_ShouldUpdateNotice - powinien zaktualizować ogłoszenie gdy istnieje i należy do klienta");

        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.isNoticeOwnedByClient(1L, 1L)).thenReturn(true);
        when(noticeService.updateNotice(anyLong(), any(NoticeRequestDTO.class))).thenReturn(sampleNotice);

        ResponseEntity<Object> response = noticeController.editNotice(1L, sampleNoticeRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleNotice, response.getBody());

        System.out.println("Pomyślnie zaktualizowano ogłoszenie należące do klienta");
    }

    @Test
    void editNotice_WhenNoticeNotOwnedByClient_ShouldReturnForbidden() {
        System.out.println("Test: editNotice_WhenNoticeNotOwnedByClient_ShouldReturnForbidden - powinien zwrócić błąd 403 gdy ogłoszenie nie należy do klienta");

        when(tools.getClientIdFromRequest(request)).thenReturn(2L);
        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.isNoticeOwnedByClient(1L, 2L)).thenReturn(false);

        ResponseEntity<Object> response = noticeController.editNotice(1L, sampleNoticeRequest, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((RequestResponseDTO) response.getBody()).getMessage().contains("Nie masz uprawnień"));

        System.out.println("Pomyślnie zwrócono błąd 403 dla próby edycji nie swojego ogłoszenia");
    }

    @Test
    void deleteNotice_WhenNoticeExistsAndOwnedByClient_ShouldDeleteNotice() {
        System.out.println("Test: deleteNotice_WhenNoticeExistsAndOwnedByClient_ShouldDeleteNotice - powinien usunąć ogłoszenie gdy istnieje i należy do klienta");

        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.isNoticeOwnedByClient(1L, 1L)).thenReturn(true);

        ResponseEntity<RequestResponseDTO> response = noticeController.deleteNotice(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Pomyślnie usunięto"));

        verify(noticeService, times(1)).deleteNotice(1L);

        System.out.println("Pomyślnie usunięto ogłoszenie należące do klienta");
    }

}