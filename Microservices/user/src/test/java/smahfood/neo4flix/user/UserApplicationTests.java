package smahfood.neo4flix.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = UserApplication.class,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.service-registry.auto-registration.enabled=false",
                "spring.autoconfigure.exclude=" +
                        "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration," +
                        "org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration," +
                        "org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration," +
                        "org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration," +
                        "org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryClientAutoConfiguration"
        }
)
class UserApplicationTests {

    @Test
    void contextLoads() {
    }
}
