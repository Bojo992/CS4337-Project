import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpDsl;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoadTestSimulation extends Simulation {

    // Define HTTP protocol configuration
    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl("http://localhost:8080");
            //.acceptHeader("application/json");

    // Define the scenario
    ScenarioBuilder scn = scenario("Basic Load Test")
            .exec(
                    http("Get Messages")
                            .get("")
                            .check(status().is(200))
            )
            .pause(1);

    {
        setUp(
                scn.injectOpen(
                        rampUsers(1000).during(60)
                )
        ).protocols(httpProtocol);
    }
}
