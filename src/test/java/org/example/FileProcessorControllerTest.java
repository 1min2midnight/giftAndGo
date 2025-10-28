package org.example;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.services.IpValidationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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

    private static int wireMockPort;

    @BeforeAll
    static void beforeAll(WireMockRuntimeInfo wireMockRuntimeInfo) {
        wireMockPort = wireMockRuntimeInfo.getHttpPort();
    }

    @BeforeEach
    void beforeEach() {
        reset();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("ip.api.url", () -> "http://localhost:" + wireMockPort + "/json/");
    }

    @Test
    public void test_allowedIpReturnsOutcomeJson() throws Exception {
        stubFor(get(urlPathEqualTo("/json/24.48.0.1"))
                .withQueryParam("fields", equalTo("query,status,isp,country,countryCode,message"))
                .willReturn(
                        okJson("""
                                             {
                                                 "query": "24.48.0.1",
                                                 "status": "success",
                                                 "country": "Canada",
                                                 "countryCode": "CA",
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
        stubFor(post(urlPathEqualTo("/json/8.8.8.8"))
                .withQueryParam("fields", equalTo(IpValidationService.FIELDS))
                .willReturn(
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

    @Test
    public void test_blockedIspReturns403() throws Exception {
        stubFor(get(urlPathEqualTo("/json/5.6.7.8"))
                .withQueryParam("fields", equalTo(IpValidationService.FIELDS))
                .willReturn(okJson("""
                            {
                              "query": "5.6.7.8",
                              "status": "success",
                              "country": "Germany",
                              "isp": "Amazon AWS"
                            }
                        """)));

        var file = new MockMultipartFile(
                "file", "EntryFile.txt", "text/plain",
                "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1".getBytes());

        mockMvc.perform(multipart("/v1/entries/parse")
                        .file(file)
                        .header("X-Forwarded-For", "5.6.7.8"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Blocked IP from restricted ISP")));
    }

}
