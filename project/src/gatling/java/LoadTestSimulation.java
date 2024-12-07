import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpDsl;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoadTestSimulation extends Simulation {


    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json");


    ScenarioBuilder sendMessageScenario = scenario("Send Message Test")
            .exec(
                    http("Send Message")
                            .post("/chat.sendMsg")
                            .body(StringBody("{\"message\": \"Hello, world!\", \"sender\": \"user1\"}"))
                            .asJson()
                            .check(status().is(200))
            )
            .pause(1);


    ScenarioBuilder addUserScenario = scenario("Add User Test")
            .exec(
                    http("Add User")
                            .post("/chat.addUser")
                            .body(StringBody("{\"username\": \"user1\"}"))
                            .asJson()
                            .check(status().is(200))
            )
            .pause(1);


    ScenarioBuilder publicTopicScenario = scenario("Public Topic Test")
            .exec(
                    http("Access Public Topic")
                            .get("/topic/public")
                            .check(status().is(200))
            )
            .pause(1);

    {
        setUp(
                sendMessageScenario.injectOpen(
                        rampUsers(500).during(30)
                ),
                addUserScenario.injectOpen(
                        rampUsers(300).during(30)
                ),
                publicTopicScenario.injectOpen(
                        rampUsers(200).during(30)
                )
        ).protocols(httpProtocol);
    }
}
