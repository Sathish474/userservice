package com.sathish.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sathish.userservice.dtos.SendEmailMessageDto;
import com.sathish.userservice.exceptions.IncorrectPasswordException;
import com.sathish.userservice.exceptions.UserAlreadyExistsException;
import com.sathish.userservice.exceptions.UserDoesnotExistException;
import com.sathish.userservice.models.Session;
import com.sathish.userservice.models.SessionStatus;
import com.sathish.userservice.models.User;
import com.sathish.userservice.repositories.SessionRepository;
import com.sathish.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

import static java.lang.System.currentTimeMillis;

@Service
public class AuthService {

    @Autowired
    private KafkaTemplate<String, SendEmailMessageDto> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionRepository sessionRepository;
    private SecretKey key = Jwts.SIG.HS256.key().build();

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }

    public Boolean singUp(String email, String password) throws UserAlreadyExistsException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        SendEmailMessageDto sendEmailMessageDto = new SendEmailMessageDto();
        sendEmailMessageDto.setTo("sathishchowdary474@gmail.com");
        sendEmailMessageDto.setSubject("User has signed up");
        sendEmailMessageDto.setBody("User with email " + savedUser.getEmail() + " has signed up.");
        kafkaTemplate.send("TopicEmail", sendEmailMessageDto);
        /*try {
            kafkaTemplate.send("TopicEmail", sendEmailMessageDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
        return true;
    }

    public String login(String email, String password) throws UserDoesnotExistException, IncorrectPasswordException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserDoesnotExistException("User does not exist");
        }
        boolean isPasswordMatched = bCryptPasswordEncoder.matches(password, optionalUser.get().getPassword());
        if (!isPasswordMatched) {
            throw new IncorrectPasswordException("Incorrect password Exception.");
        }
        String jwtToken = createJwtToken(optionalUser.get().getId(), new ArrayList<>(), optionalUser.get().getEmail());
        Session session = new Session();
        session.setToken(jwtToken);
        session.setUser(optionalUser.get());
        session.setExpiringAt(new Date(currentTimeMillis() + 1000 * 60 * 60));
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);
        return jwtToken;
    }

    private String createJwtToken(Long userId, List<String> roles, String email) {
        Map<String, Object> dataInJWT = new HashMap<>();
        dataInJWT.put("userId", userId);
        dataInJWT.put("roles", roles);
        dataInJWT.put("email", email);

        String token = Jwts.builder().
                claims(dataInJWT)
                .expiration(new Date(currentTimeMillis() + 1000 * 60 * 60 * 24))
                .issuedAt(new Date(currentTimeMillis()))
                .signWith(key)
                .compact();
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJwts = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
