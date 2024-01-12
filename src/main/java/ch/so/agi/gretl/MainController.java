package ch.so.agi.gretl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

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
    public ResponseEntity<?> startGretlJob(@RequestParam Map<String, String> queryParameters) {
        String userName = queryParameters.get(PARAM_USER);
        String token = queryParameters.get(PARAM_TOKEN);
        String jobName = queryParameters.get(PARAM_JOB_NAME);

        logger.info(appConfig.getJenkinsUrl().toString());
        
        
        String encodedUserToken = Base64.getEncoder().encodeToString("xxxx:yyyyy".getBytes());
        logger.info(encodedUserToken);
        
        URI requestUri = URI.create("https://xxxxxx/job/agi_dummy/build");

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
        logger.info("{}", statusCode);
        
        if (statusCode != 201) {
            String errorMessage = "Job not started. Status code: " + String.valueOf(statusCode) + ". Request: " + requestUri.toString();
            throw new IllegalStateException(errorMessage);            
        }
        
        String location = response.headers().firstValue("location").get();
        logger.info("{}", location);

        
        
        
        
        String body = response.body();
        logger.info(body);
        
        //String contentType = response.headers().firstValue("content-type").orElse("text/plain");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("https://xxxxx/job/agi_dummy"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);        
    }

    
    

}
