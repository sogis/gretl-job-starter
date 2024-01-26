package ch.so.agi.gretl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MainControllerTest {

    @LocalServerPort
    private int port;
    
    private MockWebServer mockWebServer;

    @BeforeEach
    public void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start(9999);
    }
    
    // Dünkt mich noch nicht endzeitgeil.
    // Auch sollte man TestRestTemplate verwenden.
    @Test
    void testControllerWithHttpRequest() throws Exception {
        
        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) {
                connection.setInstanceFollowRedirects(false);
            }
        });

        MockResponse mockResponse = new MockResponse()
                .addHeader("Location", "http://"+mockWebServer.getHostName()+":"+mockWebServer.getPort()+"/queue/item/123/")
                .setResponseCode(201);

        mockWebServer.enqueue(mockResponse);
        mockWebServer.url("/job/agi_dummy/build");

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/start?job=agi_dummy&user=agiuser1&token=xxx111yyy222zzz333",
                HttpMethod.GET,
                null,
                String.class
        );

        assertTrue(responseEntity.getStatusCode().is3xxRedirection());        
    }    
}
