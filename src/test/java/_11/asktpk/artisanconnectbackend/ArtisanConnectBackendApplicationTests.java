package _11.asktpk.artisanconnectbackend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArtisanConnectBackendApplicationTests {
    private static final Logger logger = LogManager.getLogger(ArtisanConnectBackendApplicationTests.class);

//    @Test
//    void testPostgresDatabase() {
//        postgresDatabase.add(new Notice("Test Notice", "Username", "Test Description"));
//        Boolean isRecordAvailable = postgresDatabase.get().size() > 0;
//        if(isRecordAvailable) {
//            logger.info("The record is available in the database");
//        } else {
//            logger.error("The record is not available in the database");
//        }
//        assert isRecordAvailable;
//    }
//
//    @Test
//    void getAllNotices() throws IOException {
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        Request request = new Request.Builder()
//                .url("http://localhost:8080/api/v1/notices/all")
//                .build();
//        Response response = client.newCall(request).execute();
//    }
}
