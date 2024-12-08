package com.sathish.userservice.controllers;

import com.sathish.userservice.dtos.*;
import com.sathish.userservice.exceptions.UserAlreadyExistsException;
import com.sathish.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) throws UserAlreadyExistsException {
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();
        try {
            if (authService.singUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword())) {
                signUpResponseDto.setStatus(RequestStatus.SUCCESS);
            } else {
                signUpResponseDto.setStatus(RequestStatus.FAILURE);
            }
        } catch (Exception e){
            signUpResponseDto.setStatus(RequestStatus.FAILURE);
            return new ResponseEntity<>(signUpResponseDto, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(signUpResponseDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        String token = null;
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        try{
            token = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            loginResponseDto.setStatus(token != null ? RequestStatus.SUCCESS : RequestStatus.FAILURE);
            headers.add("AUTH_TOKEN", token);
        } catch (Exception e){
            loginResponseDto.setMessage(e.getMessage());
            loginResponseDto.setStatus(RequestStatus.FAILURE);
            return new ResponseEntity<>(loginResponseDto, HttpStatus.UNAUTHORIZED);
        }


        return new ResponseEntity<>(loginResponseDto, headers ,token != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("token") String token){
        return authService.validateToken(token);
    }
}
