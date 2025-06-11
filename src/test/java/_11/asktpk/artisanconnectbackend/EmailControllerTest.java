package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testSendEmailWithValidAuthToken() throws Exception {
        System.out.println("Starting testSendEmailWithValidAuthToken");
        String jsonPayload = """
                {
                    "to": "test@example.com",
                    "subject": "Test Subject",
                    "body": "Test Body"
                }
                """;
        System.out.println("Sending JSON payload: " + jsonPayload);

        String jwtToken = "Bearer " + jwtUtil.generateToken("test@example.com", "USER", 1L);

        MvcResult result = mockMvc.perform(post("/api/v1/email/send")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(content().string("Email wysłany pomyślnie"))
                .andReturn();

        System.out.println("Response status: " + result.getResponse().getStatus());
        System.out.println("Response content: " + result.getResponse().getContentAsString());
    }

    @Test
    void testSendEmailWithoutAuthToken() throws Exception {
        System.out.println("Starting testSendEmailWithoutAuthToken");
        String jsonPayload = """
                {
                    "to": "test@example.com",
                    "subject": "Test Subject",
                    "body": "Test Body"
                }
                """;
        System.out.println("Sending JSON payload: " + jsonPayload);

        MvcResult result = mockMvc.perform(post("/api/v1/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isForbidden())
                .andReturn();

        System.out.println("Response status: " + result.getResponse().getStatus());
        System.out.println("Response content: " + result.getResponse().getContentAsString());
    }
}