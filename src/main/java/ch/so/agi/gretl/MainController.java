package ch.so.agi.gretl;

import java.net.URI;
import java.net.http.HttpClient;
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

    @Autowired
    HttpClient httpClient;

    @Autowired
    AppConfig appConfig;

    @Autowired
    JenkinsService jenkinsService;

    @GetMapping("/ping")
    public ResponseEntity<String>  ping() {
        logger.info("ping");
        return new ResponseEntity<String>("gretl-job-starter", HttpStatus.OK);
    }

    @GetMapping("/start")
    public ResponseEntity<?> startGretlJob(@RequestParam("user") String userName, @RequestParam("token") String token, @RequestParam("job") String jobName) {
        // Wir verwenden momentan immer Prod-GRETL-Jenkins.
        String gretlUrl = appConfig.getJenkinsUrl().stream()
            .filter(g -> g.get("env").equalsIgnoreCase("prod"))
            .findAny()
            .map(g -> g.get("url"))
            .map(g -> g.replace("${jobName}", jobName))
            .orElseThrow();
        logger.debug("GRETL Jenkins url: {}", gretlUrl);

        String encodedUserToken = Base64.getEncoder().encodeToString((userName+":"+token).getBytes());
        logger.debug("Encoded name and token: {}", encodedUserToken);

        JenkinsRequestResult result = jenkinsService.makeHttpRequest(gretlUrl, encodedUserToken);
        logger.debug("Status code: {}", result.statusCode());

        if (result.statusCode() != 201) {
            String errorMessage;
            switch (result.statusCode()) {
                case 401:   // Unauthorized
                    errorMessage = "FEHLER: Keine Berechtigung! Bitte überprüfen Sie Benutzernamen und API-Token. \n\nBei Fragen wenden Sie sich bitte ans AGI.";
                    break;
                case 404:   // Not Found
                    errorMessage = "FEHLER: Jobname unbekannt! Bitte überprüfen Sie den Jobnamen. \n\nBei Fragen wenden Sie sich bitte ans AGI.";
                    break;
                default:
                    errorMessage = "FEHLER: Job konnte nicht gestartet werden. \n\nBitte wenden Sie sich ans AGI.";
                    break;
            }

            logger.error(errorMessage + "\nStatus code: " + String.valueOf(result.statusCode()) + ". Request: " + result.requestUri());
            return ResponseEntity.ok(errorMessage);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(result.locationUri()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }
}
