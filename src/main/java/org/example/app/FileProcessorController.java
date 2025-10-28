package org.example.app;

import org.example.services.FileParserService;
import org.example.model.Entry;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/v1/entries")
public class FileProcessorController {
    private final FileParserService fileParserService;

    public FileProcessorController(FileParserService fileParserService) {
        this.fileParserService = fileParserService;
    }

    @PostMapping(
            path = "/parse",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> processFile(@RequestBody MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        List<Entry> entry = fileParserService.parse(content);
        return ResponseEntity.ok().build();
    }
}
