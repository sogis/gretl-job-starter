package ch.so.agi.gretl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

// Vorbereitung, falls mit mehreren GRETL-Jenkins-Umgebungen gearbeitet werden muss.
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private List<Map<String,String>> jenkinsUrl;

    public List<Map<String,String>> getJenkinsUrl() {
        return jenkinsUrl;
    }

    public void setJenkinsUrl(List<Map<String,String>> jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
    }
}
