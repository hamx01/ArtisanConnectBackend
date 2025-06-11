package _11.asktpk.artisanconnectbackend;



import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.service.*;
import _11.asktpk.artisanconnectbackend.utils.Enums;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy dla funkcjonalności klienta w backendzie.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtisanConnectBackendApplicationTests {

    private static final Logger logger = LogManager.getLogger(ArtisanConnectBackendApplicationTests.class);

    @Nested
    @DisplayName("Testy integracyjne ImageService")
    class ImageServiceTest {

        private final Logger logger = LogManager.getLogger(ImageServiceTest.class);
        private final ImageService imageService;
        private final ImageRepository imageRepository;
        private final Path testDirectory;

        ImageServiceTest() throws Exception {
            logger.info("Inicjalizacja testów ImageService");
            this.imageRepository = mock(ImageRepository.class);
            this.testDirectory = Files.createTempDirectory("test-images");
            logger.info("Utworzono katalog testowy: {}", testDirectory);

            Constructor<ImageService> constructor = ImageService.class.getDeclaredConstructor(ImageRepository.class);
            constructor.setAccessible(true);
            this.imageService = constructor.newInstance(imageRepository);
        }

        @AfterEach
        void cleanup() throws IOException {
            logger.info("Sprzątanie po teście - usuwanie katalogu testowego: {}", testDirectory);
            Files.walk(testDirectory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            logger.debug("Usunięto plik: {}", path);
                        } catch (IOException e) {
                            logger.warn("Nie można usunąć pliku: {}", path, e);
                        }
                    });
        }

        @Test
        @DisplayName("Powinien poprawnie zapisać obraz w magazynie plików")
        void shouldSaveImageToStorage() throws IOException {
            logger.info("Test zapisu obrazu - rozpoczęcie");

            final String testFileName = "test.jpg";
            final Path testFilePath = testDirectory.resolve(testFileName);
            Files.createFile(testFilePath);
            Files.write(testFilePath, "test content".getBytes());
            logger.debug("Utworzono testowy plik: {}", testFilePath);

            final MultipartFile file = mock(MultipartFile.class);
            when(file.getOriginalFilename()).thenReturn(testFileName);
            when(file.getInputStream()).thenReturn(Files.newInputStream(testFilePath));

            final String savedFileName = imageService.saveImageToStorage(testDirectory.toString(), file);
            logger.info("Zapisano plik pod nazwą: {}", savedFileName);

            assertTrue(savedFileName.endsWith(".jpg"));
            assertTrue(Files.exists(testDirectory.resolve(savedFileName)));
            logger.info("Test zapisu obrazu - zakończony pomyślnie");
        }

        @Test
        @DisplayName("Powinien poprawnie pobrać obraz")
        void shouldGetImage() throws IOException {
            logger.info("Test pobierania obrazu - rozpoczęcie");

            final String testFileName = "test.jpg";
            Files.createFile(testDirectory.resolve(testFileName));
            logger.debug("Utworzono testowy plik: {}", testFileName);

            final Resource resource = imageService.getImage(testDirectory.toString(), testFileName);
            logger.info("Pobrano zasób: {}", resource.getFilename());

            assertNotNull(resource);
            assertTrue(resource.exists());
            assertInstanceOf(UrlResource.class, resource);
            logger.info("Test pobierania obrazu - zakończony pomyślnie");
        }

        @Test
        @DisplayName("Powinien zgłosić błąd, gdy obraz nie zostanie znaleziony")
        void shouldThrowExceptionWhenImageNotFound() {
            logger.info("Test obsługi błędu - rozpoczęcie");

            final Exception exception = assertThrows(IOException.class, () ->
                    imageService.getImage(testDirectory.toString(), "missing.jpg")
            );
            logger.info("Złapano wyjątek: {}", exception.getMessage());

            assertThat(exception).hasMessageContaining("File not found");
            logger.info("Test obsługi błędu - zakończony pomyślnie");
        }

        @Test
        @DisplayName("Powinien poprawnie usuwać obraz z magazynu plików")
        void shouldDeleteImage() throws IOException {
            logger.info("Test usuwania obrazu - rozpoczęcie");

            final String testFileName = "test-delete.jpg";
            final Path testFilePath = testDirectory.resolve(testFileName);
            Files.createFile(testFilePath);
            logger.debug("Utworzono testowy plik: {}", testFilePath);

            when(imageRepository.existsImageByImageNameEqualsIgnoreCase(testFileName)).thenReturn(true);

            imageService.deleteImage(testDirectory.toString(), testFileName);
            logger.info("Usunięto plik: {}", testFileName);

            assertFalse(Files.exists(testFilePath));
            verify(imageRepository).deleteByImageNameEquals(testFileName);
            logger.info("Test usuwania obrazu - zakończony pomyślnie");
        }

        @Test
        @DisplayName("Powinien poprawnie zwrócić listę nazw obrazów")
        void shouldGetImagesListForNotice() throws Exception {
            logger.info("Test pobierania listy obrazów - rozpoczęcie");

            final Long noticeId = 1L;
            final List<String> expectedNames = List.of("image1.jpg", "image2.jpg");
            final List<Image> mockImages = expectedNames.stream()
                    .map(name -> {
                        Image img = new Image();
                        img.setImageName(name);
                        return img;
                    })
                    .collect(Collectors.toList());

            when(imageRepository.findByNoticeId(noticeId)).thenReturn(mockImages);
            logger.debug("Skonfigurowano mock repository dla noticeId: {}", noticeId);

            final List<String> imageNames = imageService.getImagesList(noticeId);
            logger.info("Pobrano listę {} obrazów", imageNames.size());

            assertThat(imageNames).containsExactlyElementsOf(expectedNames);
            logger.info("Test pobierania listy obrazów - zakończony pomyślnie");
        }
    }


    @Nested
    @DisplayName("Testy dla VariablesController")
    @Transactional
    class VariablesControllerTest {

        private final int port;
        private final TestRestTemplate restTemplate;

        @Autowired
        public VariablesControllerTest(@LocalServerPort int port, TestRestTemplate restTemplate) {
            this.port = port;
            this.restTemplate = restTemplate;
            logger.info("Inicjalizacja testów VariablesController");
        }

        private String registerAndGetJwtToken(String emailPrefix) {
            logger.info("Rozpoczęcie procesu rejestracji dla prefiksu email: {}", emailPrefix);
            String email = emailPrefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
            logger.debug("Wygenerowany email: {}", email);

            ClientRegistrationDTO registrationDTO = new ClientRegistrationDTO();
            registrationDTO.setEmail(email);
            registrationDTO.setFirstName("Test");
            registrationDTO.setLastName("User");
            registrationDTO.setPassword("password123");

            ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(
                    createURLWithPort("/api/v1/auth/register"),
                    registrationDTO,
                    AuthResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.CONFLICT) {
                logger.warn("Użytkownik już istnieje, próba logowania");
                AuthRequestDTO loginRequest = new AuthRequestDTO();
                loginRequest.setEmail(email);
                loginRequest.setPassword("password123");

                response = restTemplate.postForEntity(
                        createURLWithPort("/api/v1/auth/login"),
                        loginRequest,
                        AuthResponseDTO.class
                );
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            } else {
                logger.info("Pomyślnie zarejestrowano nowego użytkownika");
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            }

            assertThat(response.getBody()).isNotNull();
            logger.debug("Otrzymano token JWT");
            return response.getBody().getToken();
        }

        private HttpEntity<Void> createRequestWithToken(String token) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            return new HttpEntity<>(headers);
        }

        @Test
        @DisplayName("Powinien zwrócić kategorie")
        void shouldGetCategories() {
            logger.info("Test pobierania kategorii - rozpoczęcie");
            String token = registerAndGetJwtToken(
                    "categories"
            );
            logger.debug("Otrzymano token autoryzacyjny");

            String url = createURLWithPort("/api/v1/vars/categories");
            logger.debug("Utworzono URL endpointu: {}", url);

            HttpEntity<Void> request = createRequestWithToken(token);
            ResponseEntity<CategoriesDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CategoriesDTO[].class
            );
            logger.info("Wykonano zapytanie o kategorie");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull().isNotEmpty();
            logger.info("Test pobierania kategorii - zakończony pomyślnie");
        }

        @Test
        @DisplayName("Powinien zwrócić statusy")
        void shouldGetStatuses() {
            String token = registerAndGetJwtToken(
                    "statuses"
            );

            String url = createURLWithPort("/api/v1/vars/statuses");

            HttpEntity<Void> request = createRequestWithToken(token);
            ResponseEntity<Enums.Status[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Enums.Status[].class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull().isNotEmpty();
        }


        private String createURLWithPort(String uri) {
            return "http://localhost:" + port + uri;
        }
    }



    @Nested
    @DisplayName("Testy integracyjne AuthController")
    @Transactional
    class AuthControllerTest {

        private final int port;
        private final TestRestTemplate restTemplate;
        private final ClientRepository clientRepository;
        private final NoticeRepository noticeRepository;
        private final Logger logger = LogManager.getLogger(AuthControllerTest.class);

        @Autowired
        public AuthControllerTest(
                @LocalServerPort int port,
                TestRestTemplate restTemplate,
                ClientRepository clientRepository,
                NoticeRepository noticeRepository) {
            this.port = port;
            this.restTemplate = restTemplate;
            this.clientRepository = clientRepository;
            this.noticeRepository = noticeRepository;
        }

        @BeforeEach
        void cleanDatabase() {
            noticeRepository.deleteAll();
            clientRepository.deleteAll();
        }

        private String createURLWithPort(String uri) {
            return "http://localhost:" + port + uri;
        }


        @Test
        @DisplayName("Powinien zwrócić błąd przy rejestracji z istniejącym emailem")
        void shouldFailRegisterWithExistingEmail() {
            String email = "user_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
            ClientRegistrationDTO registrationDTO = new ClientRegistrationDTO();
            registrationDTO.setEmail(email);
            registrationDTO.setFirstName("Jan");
            registrationDTO.setLastName("Kowalski");
            registrationDTO.setPassword("password123");

            ResponseEntity<AuthResponseDTO> firstResponse = restTemplate.postForEntity(
                    createURLWithPort("/api/v1/auth/register"),
                    registrationDTO,
                    AuthResponseDTO.class
            );
            assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            ResponseEntity<AuthResponseDTO> secondResponse = restTemplate.postForEntity(
                    createURLWithPort("/api/v1/auth/register"),
                    registrationDTO,
                    AuthResponseDTO.class
            );
            logger.info("Wysłano żądanie rejestracji");

            assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("Powinien poprawnie zalogować istniejącego użytkownika")
        void shouldLoginExistingUser() {
            logger.info("Test logowania użytkownika - rozpoczęcie");
            String email = "user_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
            String password = "password123";

            ClientRegistrationDTO registrationDTO = new ClientRegistrationDTO();
            registrationDTO.setEmail(email);
            registrationDTO.setFirstName("Jan");
            registrationDTO.setLastName("Kowalski");
            registrationDTO.setPassword(password);
            restTemplate.postForEntity(
                    createURLWithPort("/api/v1/auth/register"),
                    registrationDTO,
                    AuthResponseDTO.class
            );
            logger.debug("Zarejestrowano testowego użytkownika: {}", email);

            AuthRequestDTO loginRequest = new AuthRequestDTO();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(
                    createURLWithPort("/api/v1/auth/login"),
                    loginRequest,
                    AuthResponseDTO.class
            );
            logger.info("Wykonano próbę logowania");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getToken()).isNotBlank();
            logger.info("Test logowania - zakończony pomyślnie");
        }

        @Test
        @DisplayName("Powinien zwrócić błąd przy logowaniu z nieprawidłowym hasłem")
        void shouldFailLoginWithIncorrectPassword() {
            logger.info("Test obsługi błędnych danych logowania - rozpoczęcie");
            String email = "user_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
            String password = "password123";

            ClientRegistrationDTO registrationDTO = new ClientRegistrationDTO();
            registrationDTO.setEmail(email);
            registrationDTO.setFirstName("Jan");
            registrationDTO.setLastName("Kowalski");
            registrationDTO.setPassword(password);
            restTemplate.postForEntity(
                    createURLWithPort("/api/v1/auth/register"),
                    registrationDTO,
                    AuthResponseDTO.class
            );

            AuthRequestDTO loginRequest = new AuthRequestDTO();
            loginRequest.setEmail(email);
            loginRequest.setPassword("wrongPassword");
            logger.debug("Przygotowano nieprawidłowe dane logowania");

            ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(
                    createURLWithPort("/api/v1/auth/login"),
                    loginRequest,
                    AuthResponseDTO.class
            );
            logger.info("Wykonano próbę logowania z błędnymi danymi");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            logger.info("Test obsługi błędnych danych - zakończony pomyślnie");
        }

    }





}