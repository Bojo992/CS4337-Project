# Chat for UL Students
A WhatsApp clone targeted towards UL Students.

## Services
The planned services are:
- DB Service
- Chat Service
- Auth/Authz Service

Connected using Kafka.

Currently a barebones chat implementation with a public room topic is implemented using websockets, and will be connected to a DB and using Kafka queues 

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

The chat service should now be accessible via [localhost:8080](localhost:8080).
