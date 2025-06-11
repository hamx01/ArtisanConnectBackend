package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.controller.NoticeController;
import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private NoticeService noticeService;

    @Mock
    private ClientService clientService;

    @Mock
    private Tools tools;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private NoticeController noticeController;

    private NoticeResponseDTO sampleNotice;
    private NoticeRequestDTO sampleNoticeRequest;

    @BeforeEach
    void setUp() {
        System.out.println("Inicjalizacja danych testowych...");

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
    @DisplayName("Pobranie wszystkich ogłoszeń")
    void getAllNotices_ShouldReturnListOfNotices() {
        when(noticeService.getAllNotices()).thenReturn(List.of(sampleNotice));

        List<NoticeResponseDTO> result = noticeController.getAllNotices();

        assertNotNull(result);
        assertEquals(1, result.size());
        System.out.println("Test GET /notices zakończony sukcesem");
    }

    @Test
    @DisplayName("Pobranie istniejącego ogłoszenia")
    void getNoticeById_WhenNoticeExists_ShouldReturnNotice() {
        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.getNoticeById(1L)).thenReturn(sampleNotice);

        ResponseEntity<?> response = noticeController.getNoticeById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println("Test GET /notices/{id} (istniejące) zakończony sukcesem");
    }

    @Test
    @DisplayName("Pobranie nieistniejącego ogłoszenia")
    void getNoticeById_WhenNoticeNotExists_ShouldReturnNotFound() {
        when(noticeService.noticeExists(1L)).thenReturn(false);

        ResponseEntity<?> response = noticeController.getNoticeById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("Test GET /notices/{id} (nieistniejące) zakończony sukcesem");
    }

    @Test
    @DisplayName("Dodanie poprawnego ogłoszenia")
    void addNotice_WithValidData_ShouldCreateNotice() {
        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(clientService.clientExists(1L)).thenReturn(true);
        when(noticeService.addNotice(any(NoticeRequestDTO.class))).thenReturn(1L);

        ResponseEntity<NoticeAdditionDTO> response = noticeController.addNotice(sampleNoticeRequest, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("Test POST /notices (poprawne dane) zakończony sukcesem");
    }

    @Test
    @DisplayName("Dodanie ogłoszenia z błędną kategorią")
    void addNotice_WithInvalidCategory_ShouldReturnBadRequest() {
        sampleNoticeRequest.setCategory(null);

        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(clientService.clientExists(1L)).thenReturn(true);

        ResponseEntity<NoticeAdditionDTO> response = noticeController.addNotice(sampleNoticeRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println("Test POST /notices (błędna kategoria) zakończony sukcesem");
    }

    @Test
    @DisplayName("Dodanie ogłoszenia przez nieistniejącego klienta")
    void addNotice_WhenClientNotExists_ShouldReturnBadRequest() {
        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(clientService.clientExists(1L)).thenReturn(false);

        ResponseEntity<NoticeAdditionDTO> response = noticeController.addNotice(sampleNoticeRequest, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println("Test POST /notices (nieistniejący klient) zakończony sukcesem");
    }

    @Test
    @DisplayName("Aktualizacja własnego ogłoszenia")
    void editNotice_WhenNoticeExistsAndOwnedByClient_ShouldUpdateNotice() {
        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.isNoticeOwnedByClient(1L, 1L)).thenReturn(true);
        when(noticeService.updateNotice(anyLong(), any(NoticeRequestDTO.class))).thenReturn(sampleNotice);

        ResponseEntity<Object> response = noticeController.editNotice(1L, sampleNoticeRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println("Test PUT /notices/{id} (własne ogłoszenie) zakończony sukcesem");
    }

    @Test
    @DisplayName("Próba aktualizacji cudzego ogłoszenia")
    void editNotice_WhenNoticeNotOwnedByClient_ShouldReturnForbidden() {
        when(tools.getClientIdFromRequest(request)).thenReturn(2L);
        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.isNoticeOwnedByClient(1L, 2L)).thenReturn(false);

        ResponseEntity<Object> response = noticeController.editNotice(1L, sampleNoticeRequest, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        System.out.println("Test PUT /notices/{id} (cudze ogłoszenie) zakończony sukcesem");
    }

    @Test
    @DisplayName("Usunięcie własnego ogłoszenia")
    void deleteNotice_WhenNoticeExistsAndOwnedByClient_ShouldDeleteNotice() {
        when(tools.getClientIdFromRequest(request)).thenReturn(1L);
        when(noticeService.noticeExists(1L)).thenReturn(true);
        when(noticeService.isNoticeOwnedByClient(1L, 1L)).thenReturn(true);

        ResponseEntity<RequestResponseDTO> response = noticeController.deleteNotice(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(noticeService, times(1)).deleteNotice(1L);
        System.out.println("Test DELETE /notices/{id} (własne ogłoszenie) zakończony sukcesem");
    }
}