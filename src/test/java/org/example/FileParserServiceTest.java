package org.example;

import org.assertj.core.api.Assertions;
import org.example.config.ValidationProperties;
import org.example.model.Entry;
import org.example.services.FileParserService;
import org.example.services.RequestLogService;
import org.example.validator.EntryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(classes = FileParserService.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FileParserServiceTest {
    @Autowired
    private FileParserService fileParserService;
    @Test
    public void testFileParserService() {
        String content = """
                   18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                   3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
                """;
        List<Entry> entries = fileParserService.parse(content);
        Assertions.assertThat(entries).hasSize(2);
        Entry firstEntry = entries.get(0);
        Assertions.assertThat(firstEntry.getUuid().toString()).isEqualTo("18148426-89e1-11ee-b9d1-0242ac120002");
        Assertions.assertThat(firstEntry.getId()).isEqualTo("1X1D14");
        Assertions.assertThat(firstEntry.getName()).isEqualTo("John Smith");
        Assertions.assertThat(firstEntry.getLikes()).isEqualTo("Likes Apricots");
        Assertions.assertThat(firstEntry.getTransport()).isEqualTo("Rides A Bike");
        Assertions.assertThat(firstEntry.getAverageSpeed()).isEqualTo(new BigDecimal("6.2"));
        Assertions.assertThat(firstEntry.getTopSpeed()).isEqualTo(new BigDecimal("12.1"));

        Entry secondEntry = entries.get(1);
        Assertions.assertThat(secondEntry.getUuid().toString()).isEqualTo("3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7");
        Assertions.assertThat(secondEntry.getId()).isEqualTo("2X2D24");
        Assertions.assertThat(secondEntry.getName()).isEqualTo("Mike Smith");
        Assertions.assertThat(secondEntry.getLikes()).isEqualTo("Likes Grape");
        Assertions.assertThat(secondEntry.getTransport()).isEqualTo("Drives an SUV");
        Assertions.assertThat(secondEntry.getAverageSpeed()).isEqualTo(new BigDecimal("35.0"));
        Assertions.assertThat(secondEntry.getTopSpeed()).isEqualTo(new BigDecimal("95.5"));
    }
    @Test
    public void testFileParserServiceWithInvalidContent() {
        String content = "nonnummericalString|1X1D14|John Smith|Likes Apricots|Rides A Bike|--|blah";
        Assertions.assertThatThrownBy(() -> fileParserService.parse(content)).isInstanceOf(IllegalArgumentException.class);
    }
}
