package community.back.controller;

import community.back.common.ResponseDto;
import community.back.service.dto.request.SignInRequestDto;
import community.back.service.dto.request.SignUpRequestDto;
import community.back.service.query.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth/sign-up")
    public ResponseEntity<ResponseDto> signUp(
            @RequestBody @Valid SignUpRequestDto requestBody
    ) {
        userService.signUp(requestBody);
        return ResponseEntity.ok().body(ResponseDto.success());
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<ResponseDto> signIn(
            @RequestBody @Valid SignInRequestDto requestBody,
            HttpServletRequest servletRequest
    ) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestBody.getEmail(), requestBody.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpSession session = servletRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        return ResponseEntity.ok().body(ResponseDto.success());
    }
}
