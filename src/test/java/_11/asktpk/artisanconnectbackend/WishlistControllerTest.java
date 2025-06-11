package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.controller.WishlistController;
import _11.asktpk.artisanconnectbackend.dto.NoticeResponseDTO;
import _11.asktpk.artisanconnectbackend.dto.RequestResponseDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.service.WishlistService;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @Mock
    private ClientService clientService;

    @Mock
    private NoticeService noticeService;

    @Mock
    private Tools tools;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WishlistController wishlistController;

    private final Long testClientId = 1L;
    private final Long testNoticeId = 1L;

    @BeforeEach
    void setUp() {
        System.out.println("[Konfiguracja] Przygotowanie środowiska testowego...");
        when(tools.getClientIdFromRequest(request)).thenReturn(testClientId);
    }

    @Test
    @DisplayName("Dodanie/Usunięcie z wishlisty - powinno zwrócić sukces gdy ogłoszenie istnieje")
    void toggleWishlist_shouldReturnSuccessWhenNoticeExists() {
        System.out.println("Rozpoczęcie testu toggleWishlist_shouldReturnSuccessWhenNoticeExists");

        NoticeResponseDTO noticeResponse = new NoticeResponseDTO();
        noticeResponse.setNoticeId(testNoticeId);

        when(noticeService.getNoticeById(testNoticeId)).thenReturn(noticeResponse);
        when(clientService.getClientById(testClientId)).thenReturn(new Client());
        when(noticeService.getNoticeByIdEntity(testNoticeId)).thenReturn(new Notice());
        when(wishlistService.toggleWishlist(any(), any())).thenReturn(true);

        ResponseEntity<RequestResponseDTO> response = wishlistController.toggleWishlist(testNoticeId, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Wishlist entry added", response.getBody().getMessage());

        System.out.println("Test zakończony powodzeniem: Poprawnie obsłużono dodanie/usunięcie z wishlisty");
    }

    @Test
    @DisplayName("Dodanie/Usunięcie z wishlisty - powinno zwrócić błąd gdy ogłoszenie nie istnieje")
    void toggleWishlist_shouldReturnBadRequestWhenNoticeNotFound() {
        System.out.println("Rozpoczęcie testu toggleWishlist_shouldReturnBadRequestWhenNoticeNotFound");

        when(noticeService.getNoticeById(testNoticeId)).thenReturn(null);

        ResponseEntity<RequestResponseDTO> response = wishlistController.toggleWishlist(testNoticeId, request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Notice not found", response.getBody().getMessage());

        System.out.println("Test zakończony powodzeniem: Poprawnie obsłużono brak ogłoszenia");
    }

    @Test
    @DisplayName("Pobieranie wishlisty - powinno zwrócić listę ogłoszeń")
    void getWishlistForClient_shouldReturnNoticeList() {
        System.out.println("Rozpoczęcie testu getWishlistForClient_shouldReturnNoticeList");

        NoticeResponseDTO noticeResponse = new NoticeResponseDTO();
        noticeResponse.setNoticeId(testNoticeId);

        when(wishlistService.getNoticesInWishlist(testClientId)).thenReturn(Collections.singletonList(noticeResponse));

        List<NoticeResponseDTO> result = wishlistController.getWishlistForClient(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNoticeId, result.getFirst().getNoticeId());

        System.out.println("Test zakończony powodzeniem: Poprawnie pobrano listę ogłoszeń");
    }

    @Test
    @DisplayName("Pobieranie wishlisty - powinno zwrócić pustą listę gdy brak wpisów")
    void getWishlistForClient_shouldReturnEmptyListWhenNoEntries() {
        System.out.println("Rozpoczęcie testu getWishlistForClient_shouldReturnEmptyListWhenNoEntries");

        when(wishlistService.getNoticesInWishlist(testClientId)).thenReturn(Collections.emptyList());

        List<NoticeResponseDTO> result = wishlistController.getWishlistForClient(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        System.out.println("Test zakończony powodzeniem: Poprawnie zwrócono pustą wishlistę");
    }
}