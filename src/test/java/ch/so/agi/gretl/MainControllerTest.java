package ch.so.agi.gretl;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JenkinsService jenkinsService;
    
    
    // TODO
    // Ich brauche noch sowas: https://github.com/edigonzales/oereb-cts/blob/main/lib/src/test/java/ch/so/agi/oereb/cts/GetCapabilitiesProbeTest.java
    // Man müsste/könnte es wahrscheinlich eleganter lösen. Aber wie? Ich möchte einfach einen fake return value von der Service-Klasse.
//    @Test
//    void testControllerWithHttpRequest() throws Exception {
//        // Mock the behavior of the service
//        JenkinsRequestResult mockedResponse = new JenkinsRequestResult("arequest", 201, "redirect-url");
//        when(jenkinsService.makeHttpRequest("http://google.com", "my-token")).thenReturn(mockedResponse);
//
//        // Perform the request and assert the response
//        mockMvc.perform(get("/ping"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("gretl-job-starter"));
//    }
}
