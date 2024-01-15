package ch.so.agi.gretl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PARAM_USER = "user";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_JOB_NAME = "job";

    @Autowired
    HttpClient httpClient;
    
    @Autowired
    AppConfig appConfig;

    @GetMapping("/ping")
    public ResponseEntity<String>  ping() {
        logger.info("ping");
        return new ResponseEntity<String>("gretl-job-starter", HttpStatus.OK);
    }
    
    @GetMapping("/start")    
    public ResponseEntity<?> startGretlJob(@RequestParam("user") String userName, @RequestParam("token") String token, @RequestParam("job") String jobName) {        
        // Wir verwenden momentan immer nur Prod-GRETL-Jenkins.
        String gretlUrl = appConfig.getJenkinsUrl().stream()
            .filter(g -> g.get("env").equalsIgnoreCase("prod"))
            .findAny()
            .map(g -> g.get("url"))
            .map(g -> g.replace("${jobName}", jobName))
            .orElseThrow();
        logger.debug("GRETL Jenkins url: {}", gretlUrl);
                
        String encodedUserToken = Base64.getEncoder().encodeToString((userName+":"+token).getBytes());
        logger.debug("Encoded user token: {}", encodedUserToken);
        
        URI requestUri = URI.create(gretlUrl);
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
        logger.debug("Status code: {}", statusCode);
        
        if (statusCode != 201) {
            String errorMessage = "Job not started. Status code: " + String.valueOf(statusCode) + ". Request: " + requestUri.toString();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);            
        }
        
        String location = response.headers().firstValue("location").get();
        logger.debug("Location header: {}", location);
        
        String locationUri = requestUri.toString().substring(0, requestUri.toString().lastIndexOf("/"));
        logger.debug("Location url: {}", locationUri);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(locationUri));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);        
    }
}
