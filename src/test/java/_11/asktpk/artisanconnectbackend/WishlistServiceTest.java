package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.dto.NoticeResponseDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.entities.Wishlist;
import _11.asktpk.artisanconnectbackend.repository.WishlistRepository;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private NoticeService noticeService;

    @InjectMocks
    private WishlistService wishlistService;

    private Client testClient;
    private Notice testNotice;
    private Wishlist testWishlist;

    @BeforeEach
    void setUp() {
        System.out.println("Przygotowanie danych testowych...");

        testClient = new Client();
        testClient.setId(1L);
        testClient.setEmail("test@example.com");

        testNotice = new Notice();
        testNotice.setIdNotice(1L);
        testNotice.setTitle("Test Notice");

        testWishlist = new Wishlist();
        testWishlist.setId(1L);
        testWishlist.setClient(testClient);
        testWishlist.setNotice(testNotice);

        System.out.println("[Konfiguracja] Dane testowe gotowe");
    }

    @Test
    @DisplayName("Przełączanie wishlisty - powinno dodać gdy wpis nie istnieje")
    void toggleWishlist_shouldAddWhenNotExists() {
        System.out.println("Rozpoczęcie testu toggleWishlist_shouldAddWhenNotExists");

        when(wishlistRepository.findByClientAndNotice(testClient, testNotice)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(testWishlist);

        boolean result = wishlistService.toggleWishlist(testClient, testNotice);

        assertTrue(result);
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));

        System.out.println("Test zakończony powodzeniem: Poprawnie dodano do wishlisty");
    }

    @Test
    @DisplayName("Przełączanie wishlisty - powinno usunąć gdy wpis istnieje")
    void toggleWishlist_shouldRemoveWhenExists() {
        System.out.println("Rozpoczęcie testu toggleWishlist_shouldRemoveWhenExists");

        when(wishlistRepository.findByClientAndNotice(testClient, testNotice)).thenReturn(Optional.of(testWishlist));

        boolean result = wishlistService.toggleWishlist(testClient, testNotice);

        assertFalse(result);
        verify(wishlistRepository, times(1)).delete(testWishlist);

        System.out.println("Test zakończony powodzeniem: Poprawnie usunięto z wishlisty");
    }

    @Test
    @DisplayName("Pobieranie ogłoszeń z wishlisty - powinno zwrócić listę ogłoszeń")
    void getNoticesInWishlist_shouldReturnNoticeList() {
        System.out.println("Rozpoczęcie testu getNoticesInWishlist_shouldReturnNoticeList");

        List<Wishlist> wishlistEntries = new ArrayList<>();
        wishlistEntries.add(testWishlist);

        when(wishlistRepository.findAllByClientId(1L)).thenReturn(wishlistEntries);
        when(noticeService.getNoticeById(1L)).thenReturn(new NoticeResponseDTO());

        List<NoticeResponseDTO> result = wishlistService.getNoticesInWishlist(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        System.out.println(" Test zakończony powodzeniem: Poprawnie zwrócono listę ogłoszeń");
    }

    @Test
    @DisplayName("Pobieranie ogłoszeń z wishlisty - powinno zwrócić pustą listę gdy brak wpisów")
    void getNoticesInWishlist_shouldReturnEmptyListWhenNoEntries() {
        System.out.println("Rozpoczęcie testu getNoticesInWishlist_shouldReturnEmptyListWhenNoEntries");

        when(wishlistRepository.findAllByClientId(1L)).thenReturn(new ArrayList<>());

        List<NoticeResponseDTO> result = wishlistService.getNoticesInWishlist(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        System.out.println("Test zakończony powodzeniem: Poprawnie zwrócono pustą listę");
    }
}