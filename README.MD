# Chat for UL Students
A WhatsApp clone targeted towards UL Students, using Spring Boot and JWT.

[Project Tracking (Trello)](https://trello.com/b/2siErWSi/cs4337-group7)

## Services
The planned services are:
- DB Service
- Chat Service
- Auth/Authz Service


## Technologies
- Gradle
- JDK 23
- Docker for containing each service.
- Spring Boot for all services.
- JJWT for web token management.
- MySQL for databases.
- STOMP client and Sock.JS for websocket interactions in chat service.
- Spring Boot test containers for database tests.
- Lombok for reducing boilerplate (getters/setters)
- JUnit for unit tests.

## Building
### Chat Service
1. Build the Gradle project in the `project` directory with: 
```bash
./gradlew build
```
2. Build the chat service with Docker running.
```bash
docker build -t cs4337-chatservice .
```
3. Run the docker instance.
```bash
docker-compose up
```

The chat service should now be accessible via [localhost:8080](localhost:8080) on your browser.

### Security/Auth Service
1. Build the Gradle project in the `security` directory with: 
```bash
./gradlew build
```
2. Build the chat service with Docker running.
```bash
docker build -t security_image .
```
3. Run the docker instance.
```bash
docker-compose up
```

The security service should now be accessible via pinging [localhost:8081](localhost:8081) on postman or through the chat service.

## Running

Both services have to be running for the chat service to work, as it communicates with the security service's endpoints.

## Endpoints
### Chat Service (http://localhost:8080)
Currently uses websockets for displaying methods.

- `/ws`: The websocket endpoint Sock.js uses.
- `/topic/public`: The public messages channel for the websocket

- `/chat.sendMsg`: sends a ChatMessage to the public messages channel. Called when user clicks the `Send` button
- `/chat.addUser`: sends a `username joined!` message to the public messages channel. Called after a successful logon, either manually or automatically with a JWT.

### Security Service (http://localhost:8081)

#### POST:

- `/register`

example body:
```json
{
    "username": "cooluser123",
    "password": "totallysecure",
    "email": "12345678@studentmail.ul.ie"
}
```
Registers the user. Returns a success message. Called by chat service if you tick the register checkbox.

- `/login`

```json
{
    "username": "cooluser123",
    "password": "totallysecure",
}
```
Logs the user in, returning a JWT and refresh token. Called when logging in in chat service, and then stored in local storage. Returns 403 if invalid.
```json
{
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzMxNjc1OTEwLCJleHAiOjE3MzE2Nzc3MTB9.uNDPocwOZsj2xfNAI1RSILNWM7tVmwhHD6hqbqIRtvs",
    "Refresh": "3556498"
}
```

- `/refreshJwt`
```json
{
    "token": "3556498",
}
```
Using the refresh token from login, sends a new jwt. Returns 403 if invalid

- `/checkJwtOutside`
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNzMxNjc1OTEwLCJleHAiOjE3MzE2Nzc3MTB9.uNDPocwOZsj2xfNAI1RSILNWM7tVmwhHD6hqbqIRtvs",
}
```
Using the jwt, receives the validity of the token `isCorrect` and the ``username`` of the user the token is for. Used by chat service to save login.

#### DELETE:

- `/deleteUser`
```json
{
    "username": "cooluser123",
}
```
Requires a bearer token of the user's jwt. Deletes this user from the security database. Returns 403 if invalid.

## Breakdown
-  Feature 1: Chat Service, base controller and public messages
    - Assigned to: Roy
-  Feature 2: Chat Service, controller modifications, multiple-chat support.
    - Assigned to: Boris
-  Feature 3: Chat Service, endpoints with security service
    - Assigned to: Roy
-  Feature 4: Security Service, JWT management and generation
    - Assigned to Boris
-  Feature 5: Security service, database for authentication
    - Assigned to Boris
-  Feature 6: Database service
    - Assigned to Blake
-  Testing/Quality Assurance
    - Assigned to: Adam (Chat Service),Boris (Security Service)
-  Documentation
    - Assigned to: Roy
