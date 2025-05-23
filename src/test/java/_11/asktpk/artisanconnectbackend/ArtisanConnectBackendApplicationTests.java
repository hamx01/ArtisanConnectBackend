package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.dto.CategoriesDTO;
import _11.asktpk.artisanconnectbackend.dto.ClientDTO;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import _11.asktpk.artisanconnectbackend.dto.WishlistDTO;
import _11.asktpk.artisanconnectbackend.entities.Client;
import _11.asktpk.artisanconnectbackend.entities.Notice;
import _11.asktpk.artisanconnectbackend.entities.Wishlist;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.repository.WishlistRepository;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.ImageService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.service.WishlistService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import _11.asktpk.artisanconnectbackend.entities.Image;
import _11.asktpk.artisanconnectbackend.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static _11.asktpk.artisanconnectbackend.utils.Enums.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy dla funkcjonalności klienta w backendzie.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtisanConnectBackendApplicationTests {

    private static final Logger logger = LogManager.getLogger(ArtisanConnectBackendApplicationTests.class);

    @LocalServerPort
    private int port;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        logger.info("Inicjalizacja komponentów testowych na porcie: {}", port);
    }

    @Nested
    @DisplayName("Testy jednostkowe ClientService")
    class ClientServiceTest {

        private ClientRepository clientRepository;
        private ClientService clientService;

        @BeforeEach
        void setUp() {
            logger.info("Inicjalizacja mocków dla ClientService");
            clientRepository = mock(ClientRepository.class);
            clientService = new ClientService(clientRepository);
        }

        @Test
        @DisplayName("Powinien poprawnie mapować klientów na ClientDTO")
        void testClientMappingToDTO() {
            logger.info("Tworzenie danych klientów...");
            Client client = createTestClient("Jan", "Kowalski");
            when(clientRepository.findAll()).thenReturn(List.of(client));

            logger.info("Wywołanie metody getAllClients...");
            List<ClientDTO> clientDTOList = clientService.getAllClients();

            assertThat(clientDTOList).hasSize(1);
            assertThat(clientDTOList.get(0).getFirstName()).isEqualTo("Jan");
            verify(clientRepository, times(1)).findAll();
            logger.info("Test zakończony poprawnie");
        }

        private Client createTestClient(String firstName, String lastName) {
            Client client = new Client();
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
            client.setRole(USER);
            return client;
        }
    }

    @Nested
    @DisplayName("Testy integracyjne ClientController")
    class ClientControllerTest {

        @BeforeEach
        void cleanDatabase() {
            clientRepository.deleteAll();
        }

        @Test
        @DisplayName("Powinien poprawnie dodawać klienta")
        void shouldAddClientSuccessfully() {
            ClientDTO newClient = createTestDTO("new.email@example.com", "John", "Smith");

            ResponseEntity<ClientDTO> response = restTemplate.postForEntity(
                    createURLWithPort("/api/v1/clients/add"),
                    newClient,
                    ClientDTO.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getEmail()).isEqualTo("new.email@example.com");

            assertThat(clientRepository.existsById(response.getBody().getId()));
            logger.info("Pomyślnie dodano klienta");
        }

        @Test
        @DisplayName("Powinien zwracać wszystkich klientów")
        void shouldReturnAllClients() {
            logger.info("Dodawanie przykładowych klientów do testu...");
            clientRepository.save(createTestClient("client1@example.com", "Anna", "Nowak"));
            clientRepository.save(createTestClient("client2@example.com", "Adam", "Kowalski"));

            ResponseEntity<ClientDTO[]> response = restTemplate.getForEntity(
                    createURLWithPort("/api/v1/clients/get/all"),
                    ClientDTO[].class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).hasSize(2);
            logger.info("Wszyscy klienci poprawnie pobrani");
        }

        private Client createTestClient(String email, String firstName, String lastName) {
            Client client = new Client();
            client.setEmail(email);
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setRole(USER);
            return client;
        }

        private ClientDTO createTestDTO(String email, String firstName, String lastName) {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setEmail(email);
            clientDTO.setFirstName(firstName);
            clientDTO.setLastName(lastName);
            clientDTO.setRole(USER);
            return clientDTO;
        }

        private String createURLWithPort(String uri) {
            return "http://localhost:" + port + uri;
        }
    }

    @Nested
    @DisplayName("Testy jednostkowe NoticeService")
    class NoticeServiceUnitTest {

        private NoticeRepository noticeRepository;
        private ClientRepository clientRepository;
        private NoticeService noticeService;

        @BeforeEach
        void setUp() {
            noticeRepository = mock(NoticeRepository.class);
            clientRepository = mock(ClientRepository.class);
            noticeService = new NoticeService(noticeRepository, clientRepository, null, null);
        }

        @Test
        @DisplayName("Powinien poprawnie dodać ogłoszenie")
        void shouldAddNoticeSuccessfully() {
            Client client = createTestClient("test@example.com", "Anna", "Kowalska");
            when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(client));

            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setClientId(1L);
            noticeDTO.setTitle("Test Notice");
            noticeDTO.setDescription("Opis ogłoszenia");
            noticeDTO.setPrice(100.0);

            Notice notice = new Notice();
            notice.setIdNotice(1L);

            when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

            Long savedNoticeId = noticeService.addNotice(noticeDTO);

            assertThat(savedNoticeId).isEqualTo(1L);
            verify(noticeRepository, times(1)).save(any(Notice.class));
        }

        @Test
        @DisplayName("Powinien zwrócić wyjątek, gdy klient dla ogłoszenia nie istnieje")
        void shouldThrowExceptionWhenClientNotFound() {
            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setClientId(1L);

            when(clientRepository.findById(1L)).thenReturn(java.util.Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> noticeService.addNotice(noticeDTO));
        }

        private Client createTestClient(String email, String firstName, String lastName) {
            Client client = new Client();
            client.setId(1L);
            client.setEmail(email);
            client.setFirstName(firstName);
            client.setLastName(lastName);
            return client;
        }
    }

    @Nested
    @DisplayName("Testy integracyjne ImageService")
    class ImageServiceTest {

        private ImageRepository imageRepository;
        private ImageService imageService;

        @BeforeEach
        void setUp() {
            imageRepository = mock(ImageRepository.class);
            imageService = new ImageService(imageRepository);
        }

        @Test
        @DisplayName("Powinien poprawnie zapisać obraz w magazynie plików")
        void shouldSaveImageToStorage() throws IOException {
            MultipartFile file = mock(MultipartFile.class);
            when(file.getOriginalFilename()).thenReturn("test.jpg");
            when(file.getInputStream()).thenReturn(Files.newInputStream(Path.of("src/test/resources/test.jpg")));

            String uploadDirectory = "upload_dir";
            Path uploadPath = Path.of(uploadDirectory);
            Files.createDirectories(uploadPath);

            String savedFileName = imageService.saveImageToStorage(uploadDirectory, file);

            assertTrue(savedFileName.contains(".jpg"));
            assertTrue(Files.exists(uploadPath.resolve(savedFileName)));

            Files.deleteIfExists(uploadPath.resolve(savedFileName));
        }

        @Test
        @DisplayName("Powinien poprawnie zapisać nazwę obrazu do bazy danych")
        void shouldAddImageNameToDB() {
            String filename = UUID.randomUUID() + "test.jpg";
            Long noticeId = 1L;

            imageService.addImageNameToDB(filename, noticeId);

            verify(imageRepository, times(1)).save(Mockito.any(Image.class));
        }

        @Test
        @DisplayName("Powinien poprawnie pobrać obraz")
        void shouldGetImage() throws IOException {
            Path imagePath = Path.of("src/test/resources/test.jpg");
            Resource resource = imageService.getImage("src/test/resources", "test.jpg");

            assertNotNull(resource);
            assertTrue(resource instanceof UrlResource);
            assertTrue(Files.exists(imagePath));
        }

        @Test
        @DisplayName("Powinien zgłosić błąd, gdy obraz nie zostanie znaleziony")
        void shouldThrowExceptionWhenImageNotFound() {
            Exception exception = assertThrows(IOException.class, () -> {
                imageService.getImage("invalid/path", "missing.jpg");
            });

            assertThat(exception).hasMessageContaining("File not found");
        }

        @Test
        @DisplayName("Powinien poprawnie usuwać obraz z magazynu plików i bazy danych")
        void shouldDeleteImage() throws IOException {
            Path imagePath = Files.createTempFile("temp-dir", "temp-image.jpg");
            String imageName = imagePath.getFileName().toString();
            String imageDirectory = imagePath.getParent().toString();

            Image image = new Image();
            image.setImageName(imageName);
            when(imageRepository.existsImageByImageNameEqualsIgnoreCase(imageName)).thenReturn(true);

            imageService.deleteImage(imageDirectory, imageName);

            assertFalse(Files.exists(imagePath));
            verify(imageRepository, times(1)).deleteByImageNameEquals(imageName);
        }

        @Test
        @DisplayName("Powinien poprawnie zwrócić listę nazw obrazów dla podanego ogłoszenia")
        void shouldGetImagesListForNotice() throws Exception {
            Long noticeId = 1L;
            List<Image> images = List.of(
                    createTestImage(1L, noticeId, "image1.jpg"),
                    createTestImage(2L, noticeId, "image2.jpg")
            );
            when(imageRepository.findByNoticeId(noticeId)).thenReturn(images);

            List<String> imageNames = imageService.getImagesList(noticeId);

            assertThat(imageNames).hasSize(2);
            assertThat(imageNames).containsExactly("image1.jpg", "image2.jpg");
        }

        private Image createTestImage(Long id, Long noticeId, String imageName) {
            Image image = new Image();
            image.setId(id);
            image.setNoticeId(noticeId);
            image.setImageName(imageName);
            return image;
        }
    }

    @Nested
    @DisplayName("Testy integracyjne WishlistService")
    class WishlistServiceTest {

        private WishlistRepository wishlistRepository;
        private WishlistService wishlistService;
        private NoticeService noticeService;

        @BeforeEach
        void setUp() {
            wishlistRepository = mock(WishlistRepository.class);
            noticeService = mock(NoticeService.class);
            wishlistService = new WishlistService(wishlistRepository, noticeService);
        }

        @Test
        @DisplayName("Powinien poprawnie zwrócić wishlist dla klienta")
        void shouldGetWishlistForClient() {
            Long clientId = 1L;
            Wishlist wishlist1 = createTestWishlist(1L, clientId, 10L);
            Wishlist wishlist2 = createTestWishlist(2L, clientId, 20L);

            when(wishlistRepository.findAllByClientId(clientId)).thenReturn(List.of(wishlist1, wishlist2));

            List<WishlistDTO> result = wishlistService.getWishlistForClientId(clientId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getNoticeId()).isEqualTo(10L);
            verify(wishlistRepository, times(1)).findAllByClientId(clientId);
        }

        @Test
        @DisplayName("Powinien poprawnie dodać lub usunąć element z wishlist")
        void shouldToggleWishlist() {
            Client client = createTestClient(1L, "test@example.com");
            Notice notice = createTestNotice(10L);

            // Scenariusz 1: Element istnieje i powinien zostać usunięty
            when(wishlistRepository.findByClientAndNotice(client, notice)).thenReturn(Optional.of(new Wishlist()));

            boolean removed = wishlistService.toggleWishlist(client, notice);

            assertThat(removed).isFalse();
            verify(wishlistRepository, times(1)).delete(any(Wishlist.class));

            // Scenariusz 2: Element nie istnieje i powinien zostać dodany
            when(wishlistRepository.findByClientAndNotice(client, notice)).thenReturn(Optional.empty());

            boolean added = wishlistService.toggleWishlist(client, notice);

            assertThat(added).isTrue();
            verify(wishlistRepository, times(1)).save(any(Wishlist.class));
        }

        @Test
        @DisplayName("Powinien zwrócić listę ogłoszeń w wishlist klienta")
        void shouldGetNoticesInWishlist() {
            Long clientId = 1L;
            Wishlist wishlist1 = createTestWishlist(1L, clientId, 10L);
            Wishlist wishlist2 = createTestWishlist(2L, clientId, 20L);

            when(wishlistRepository.findAllByClientId(clientId)).thenReturn(List.of(wishlist1, wishlist2));
            when(noticeService.getNoticeById(10L)).thenReturn(createNoticeDTO(10L, "Ogłoszenie 1"));
            when(noticeService.getNoticeById(20L)).thenReturn(createNoticeDTO(20L, "Ogłoszenie 2"));

            List<NoticeDTO> result = wishlistService.getNoticesInWishlist(clientId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getNoticeId()).isEqualTo(10L);
            assertThat(result.get(1).getNoticeId()).isEqualTo(20L);
        }

        private Wishlist createTestWishlist(Long id, Long clientId, Long noticeId) {
            Wishlist wishlist = new Wishlist();
            wishlist.setId(id);

            Client client = new Client();
            client.setId(clientId);
            wishlist.setClient(client);

            Notice notice = new Notice();
            notice.setIdNotice(noticeId);
            wishlist.setNotice(notice);

            return wishlist;
        }

        private Client createTestClient(Long id, String email) {
            Client client = new Client();
            client.setId(id);
            client.setEmail(email);
            return client;
        }

        private Notice createTestNotice(Long noticeId) {
            Notice notice = new Notice();
            notice.setIdNotice(noticeId);
            return notice;
        }

        private NoticeDTO createNoticeDTO(Long noticeId, String title) {
            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setNoticeId(noticeId);
            noticeDTO.setTitle(title);
            return noticeDTO;
        }
    }

    @Nested
    @DisplayName("Testy dla VariablesController")
    class VariablesControllerTest {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        @DisplayName("Powinien zwrócić kategorie")
        void shouldGetCategories() {
            String url = createURLWithPort("/api/v1/vars/categories");

            ResponseEntity<CategoriesDTO[]> response = restTemplate.getForEntity(url, CategoriesDTO[].class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("Powinien zwrócić statusy")
        void shouldGetStatuses() {
            String url = createURLWithPort("/api/v1/vars/statuses");

            ResponseEntity<Enums.Status[]> response = restTemplate.getForEntity(url, Enums.Status[].class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("Powinien zwrócić role")
        void shouldGetRoles() {
            String url = createURLWithPort("/api/v1/vars/roles");

            ResponseEntity<Enums.Role[]> response = restTemplate.getForEntity(url, Enums.Role[].class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull().isNotEmpty();
        }

        private String createURLWithPort(String uri) {
            return "http://localhost:" + port + uri;
        }
    }


}