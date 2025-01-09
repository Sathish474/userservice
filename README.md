# User Service Application

This is a Spring Boot application that provides user authentication and authorization services. It includes security configurations, Eureka client integration, and OAuth2 authorization server setup.

---

## Prerequisites

- **Java**: 11 or higher
- **Maven**: 3.6.3 or higher
- **Database**: MySQL
- **Service Discovery**: Eureka server

---

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/Sathish474/userservice.git
cd userservice
```

### Build the Project

```bash 
mvn clean install
```
### Configure the Application
```properties
spring.application.name=userservice
productService=fakeStoreProductService
spring.jpa.hibernate.ddl-auto=validate
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true

server.port=8080
spring.flyway.baseline-on-migrate=true

logging.level.org.springframework.security=trace

spring.security.user.name=user
spring.security.user.password=password

spring.security.oauth2.authorizationserver.client.oidc-client.registration.client-id=oidc-client
spring.security.oauth2.authorizationserver.client.oidc-client.registration.client-secret={noop}secret
spring.security.oauth2.authorizationserver.client.oidc-client.registration.client-authentication-methods=client_secret_basic
spring.security.oauth2.authorizationserver.client.oidc-client.registration.authorization-grant-types=authorization_code,refresh_token
spring.security.oauth2.authorizationserver.client.oidc-client.registration.redirect-uris=http://127.0.0.1:8080/login/oauth2/code/oidc-client
spring.security.oauth2.authorizationserver.client.oidc-client.registration.post-logout-redirect-uris=http://127.0.0.1:8080/
spring.security.oauth2.authorizationserver.client.oidc-client.registration.scopes=openid,profile
spring.security.oauth2.authorizationserver.client.oidc-client.require-authorization-consent=true

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```
### Run the Application
```bash 
mvn spring-boot:run
```

### Access the Application

#### Authentication Endpoints
	•	Sign Up: POST /signup
	•	Login: POST /login
	•	Validate Token: GET /validate

### Eureka Client
The application registers itself with the Eureka server at http://localhost:8761/eureka/.  

### Security Configuration
The application uses Spring Security to secure endpoints and manage user authentication. It includes an OAuth2 authorization server setup.  

#### Key Classes
	•	SpringSecurityConfig: Configures security settings, including authorization server and default security filter chains.
	•	AuthController: Handles authentication requests.

```bash 
This `README.md` file provides an overview of the project, setup instructions, and key information about the application's configuration and endpoints.
``` 