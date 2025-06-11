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
        when(tools.getClientIdFromRequest(request)).thenReturn(testClientId);
    }

    @Test
    void toggleWishlist_shouldReturnSuccessWhenNoticeExists() {
        NoticeResponseDTO noticeResponse = new NoticeResponseDTO();
        noticeResponse.setNoticeId(testNoticeId);

        when(noticeService.getNoticeById(testNoticeId)).thenReturn(noticeResponse);
        when(clientService.getClientById(testClientId)).thenReturn(new Client());
        when(noticeService.getNoticeByIdEntity(testNoticeId)).thenReturn(new Notice());
        when(wishlistService.toggleWishlist(any(), any())).thenReturn(true);

        ResponseEntity<RequestResponseDTO> response = wishlistController.toggleWishlist(testNoticeId, request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Wishlist entry added", response.getBody().getMessage());
    }

    @Test
    void toggleWishlist_shouldReturnBadRequestWhenNoticeNotFound() {
        when(noticeService.getNoticeById(testNoticeId)).thenReturn(null);

        ResponseEntity<RequestResponseDTO> response = wishlistController.toggleWishlist(testNoticeId, request);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Notice not found", response.getBody().getMessage());
    }

    @Test
    void getWishlistForClient_shouldReturnNoticeList() {
        NoticeResponseDTO noticeResponse = new NoticeResponseDTO();
        noticeResponse.setNoticeId(testNoticeId);

        when(wishlistService.getNoticesInWishlist(testClientId)).thenReturn(Collections.singletonList(noticeResponse));

        List<NoticeResponseDTO> result = wishlistController.getWishlistForClient(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNoticeId, result.getFirst().getNoticeId());
    }

    @Test
    void getWishlistForClient_shouldReturnEmptyListWhenNoEntries() {
        when(wishlistService.getNoticesInWishlist(testClientId)).thenReturn(Collections.emptyList());

        List<NoticeResponseDTO> result = wishlistController.getWishlistForClient(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}