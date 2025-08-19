package community.back.config.securityUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import community.back.common.ResponseCode;
import community.back.common.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto responseDto = ResponseDto.fail(ResponseCode.AUTHORIZATION_FAIL);
        String jsonResponse = objectMapper.writeValueAsString(responseDto);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
