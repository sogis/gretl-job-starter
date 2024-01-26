package ch.so.agi.gretl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JenkinsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpClient httpClient;

    public JenkinsService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    public JenkinsRequestResult makeHttpRequest(String url, String encodedUserToken) {
        URI requestUri = URI.create(url);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.POST(HttpRequest.BodyPublishers.noBody()).uri(requestUri);
        HttpRequest request = requestBuilder
                .timeout(Duration.ofSeconds(30L))
                .setHeader("Authorization", "Basic " + encodedUserToken)
                .build();
       
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
        int statusCode = response.statusCode();
        
        String location = response.headers().firstValue("location").get();
        logger.debug("Location header: {}", location);
        
        String locationUri = requestUri.toString().substring(0, requestUri.toString().lastIndexOf("/"));
        logger.debug("Location url: {}", locationUri);

        return new JenkinsRequestResult(url, statusCode, locationUri);
    }
}
