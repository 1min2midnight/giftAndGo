package org.example;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = org.example.FileProcessingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WireMockTest
public class FileProcessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_allowedIpReturnsOutcomeJson() throws Exception {
        stubFor(get(urlEqualTo("/json/24.48.0.1"))
                .withQueryParam("fields", equalTo("query,status,isp,country,message")).willReturn(
                        okJson("""
                                            {
                                              "query": "24.48.0.1",
                                              "status": "success",
                                              "country": "Canada",
                                              "isp": "Le Groupe Videotron Ltee"
                                            }
                                """)
                ));

        var file = new MockMultipartFile(
                "file",
                "EntryFile.txt",
                "text/plain",
                """
                        18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                        3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
                        """.getBytes()
        );

        mockMvc.perform(multipart("/v1/entries/parse")
                        .file(file)
                        .header("X-Forwarded-For", "24.48.0.1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[1].transport").value("Drives an SUV"));

    }

    @Test
    public void test_blockedCountryReturns403() throws Exception {
        stubFor(post(urlEqualTo("/json/8.8.8.8"))
                .withQueryParam("fields", equalTo("query,status,isp,country")).willReturn(
                        okJson("""
                                {
                                  "query": "8.8.8.8",
                                  "status": "success",
                                  "country": "United States",
                                  "isp": "Google LLC"
                                }
                                """)
                ));
        var file = new MockMultipartFile(
                "file", "EntryFile.txt", "text/plain", "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1".getBytes());
        mockMvc.perform(multipart("/v1/entries/parse")
                        .file(file)
                        .header("X-Forwarded-For", "8.8.8.8"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Blocked IP")));
    }

}
