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
import org.springframework.dao.DataIntegrityViolationException;
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
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
    private final int port;

    private final ClientService clientService;
    private final TestRestTemplate restTemplate;

    @Autowired
    public ArtisanConnectBackendApplicationTests(ClientService clientService, @LocalServerPort int port) {
        this.clientService = clientService;
        this.port = port;
        this.restTemplate = new TestRestTemplate();
    }


    @Nested
    @DisplayName("Testy jednostkowe ClientService")
    class ClientServiceTest {

        private final ClientRepository clientRepository;
        private final ClientService clientService;

        ClientServiceTest(ClientRepository clientRepository, ClientService clientService) {
            logger.info("Inicjalizacja mocków dla ClientService");
            this.clientRepository = clientRepository;
            this.clientService = clientService;
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
            client.setRole(clientService.getUserRole());
            return client;
        }
    }

    @Nested
    @DisplayName("Testy integracyjne ClientController")
    class ClientControllerTest {

        private final int port;
        private final TestRestTemplate restTemplate;
        private final ClientService clientService;
        private final NoticeService noticeService;
        private final NoticeRepository noticeRepository;
        private final Logger logger = LogManager.getLogger(ClientControllerTest.class);

        @Autowired
        public ClientControllerTest(
                @LocalServerPort int port,
                TestRestTemplate restTemplate,
                ClientService clientService,
                NoticeService noticeService,
                NoticeRepository noticeRepository) {
            this.port = port;
            this.restTemplate = restTemplate;
            this.clientService = clientService;
            this.noticeService = noticeService;
            this.noticeRepository = noticeRepository;
        }

        @BeforeEach
        void cleanDatabase() {

            noticeRepository.deleteAll();

            clientService.getAllClients().forEach(client -> {
                try {
                    clientService.deleteClient(client.getId());
                } catch (Exception e) {
                    logger.error("Błąd podczas usuwania klienta: {}", e.getMessage());
                }
            });
        }

        private boolean hasNotices(Long clientId) {
            return noticeService.getAllNotices().stream()
                    .anyMatch(notice -> notice.getClientId().equals(clientId));
        }

        @Test
        @DisplayName("Powinien poprawnie usunąć klienta z powiązanymi ogłoszeniami")
        void shouldDeleteClientWithNotices() {
            ClientDTO client = clientService.addClient(createTestDTO("client@example.com", "Jan", "Kowalski"));

            NoticeDTO notice = new NoticeDTO();
            notice.setClientId(client.getId());
            notice.setTitle("Test Notice");
            Long noticeId = noticeService.addNotice(notice);

            ResponseEntity<Void> deleteNoticeResponse = restTemplate.exchange(
                    createURLWithPort("/api/v1/notices/delete/" + noticeId),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            assertThat(deleteNoticeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            ResponseEntity<Void> deleteClientResponse = restTemplate.exchange(
                    createURLWithPort("/api/v1/clients/delete/" + client.getId()),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            assertThat(deleteClientResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(clientService.clientExists(client.getId())).isFalse();
            assertThat(noticeService.noticeExists(noticeId)).isFalse();
        }
        @Autowired
        private ClientRepository clientRepository;


        @Test
        @DisplayName("Powinien zwracać wszystkich klientów")
        void shouldReturnAllClients() {
            ClientDTO client1 = clientService.addClient(createTestDTO("client1@example.com", "Anna", "Nowak"));
            ClientDTO client2 = clientService.addClient(createTestDTO("client2@example.com", "Adam", "Kowalski"));

            ResponseEntity<ClientDTO[]> response = restTemplate.getForEntity(
                    createURLWithPort("/api/v1/clients/get/all"),
                    ClientDTO[].class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).hasSize(2);
        }



        @Test
        @DisplayName("Powinien zwrócić błąd przy próbie usunięcia klienta z powiązanymi ogłoszeniami bez kaskady")
        void shouldFailWhenDeletingClientWithNoticesWithoutCascade() {
            noticeService.getAllNotices().forEach(n -> noticeService.deleteNotice(n.getNoticeId()));
            clientService.getAllClients().forEach(c -> clientService.deleteClient(c.getId()));

            ClientDTO client = clientService.addClient(createTestDTO("client@example.com", "Jan", "Kowalski"));

            NoticeDTO notice = new NoticeDTO();
            notice.setClientId(client.getId());
            notice.setTitle("Test Notice");
            noticeService.addNotice(notice);

            try {
                clientService.deleteClient(client.getId());
                fail("Powinien zostać rzucony wyjątek DataIntegrityViolationException");
            } catch (DataIntegrityViolationException e) {
                // Oczekiwany wyjątek
                assertThat(e.getMessage()).contains("could not execute statement");
            }
        }

        @Test
        @DisplayName("Powinien poprawnie usunąć klienta bez powiązanych ogłoszeń")
        void shouldDeleteClientWithoutNotices() {
            ClientDTO client = clientService.addClient(createTestDTO("client@example.com", "Jan", "Kowalski"));

            ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                    createURLWithPort("/api/v1/clients/delete/" + client.getId()),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(clientService.clientExists(client.getId())).isFalse();
        }

        private ClientDTO createTestDTO(String email, String firstName, String lastName) {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setEmail(email);
            clientDTO.setFirstName(firstName);
            clientDTO.setLastName(lastName);
            clientDTO.setRole("USER");
            return clientDTO;
        }

        private String createURLWithPort(String uri) {
            return "http://localhost:" + port + uri;
        }
    }

    @Nested
    @DisplayName("Testy jednostkowe NoticeService")
    class NoticeServiceUnitTest {

        private final NoticeRepository noticeRepository;
        private final ClientRepository clientRepository;
        private final NoticeService noticeService;

        NoticeServiceUnitTest() {
            this.noticeRepository = mock(NoticeRepository.class);
            this.clientRepository = mock(ClientRepository.class);
            this.noticeService = new NoticeService(
                    noticeRepository,
                    clientRepository,
                    null,
                    null
            );
        }

        @Test
        @DisplayName("Powinien poprawnie dodać ogłoszenie")
        void shouldAddNoticeSuccessfully() {
            Client client = createTestClient("test@example.com", "Anna", "Kowalska");
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

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

            when(clientRepository.findById(1L)).thenReturn(Optional.empty());

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

        private final ImageRepository imageRepository;
        private final ImageService imageService;

        ImageServiceTest() throws Exception {
            this.imageRepository = mock(ImageRepository.class);
            Constructor<ImageService> constructor = ImageService.class.getDeclaredConstructor(ImageRepository.class);
            constructor.setAccessible(true);
            this.imageService = constructor.newInstance(imageRepository);
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

        private final WishlistRepository wishlistRepository;
        private final NoticeService noticeService;
        private final WishlistService wishlistService;

        WishlistServiceTest() {
            this.wishlistRepository = mock(WishlistRepository.class);
            this.noticeService = mock(NoticeService.class);
            this.wishlistService = new WishlistService(wishlistRepository, noticeService);
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

        private final int port;
        private final TestRestTemplate restTemplate;

        @Autowired
        public VariablesControllerTest(@LocalServerPort int port, TestRestTemplate restTemplate) {
            this.port = port;
            this.restTemplate = restTemplate;
        }

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