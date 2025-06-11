package _11.asktpk.artisanconnectbackend;

import _11.asktpk.artisanconnectbackend.security.JwtUtil;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToolsTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private Tools tools;

    @Test
    void getClientIdFromRequest_shouldReturnClientIdWhenTokenValid() {
        String token = "valid.token.here";
        Long expectedClientId = 1L;

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(expectedClientId);

        Long result = tools.getClientIdFromRequest(request);

        assertEquals(expectedClientId, result);
    }

}