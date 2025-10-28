package org.example.app;

import jakarta.servlet.http.HttpServletRequest;
import org.example.model.Outcome;
import org.example.services.FileParserService;
import org.example.model.Entry;
import org.example.services.IpValidationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/entries")
public class FileProcessorController {
    private final FileParserService fileParserService;
    private final IpValidationService ipValidationService;

    public FileProcessorController(FileParserService fileParserService,  IpValidationService ipValidationService) {
        this.fileParserService = fileParserService;
        this.ipValidationService = ipValidationService;
    }

    @PostMapping(
            path = "/parse",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> processFile(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) throws IOException {
        String ipAddress = ipValidationService.extractClientIp(httpServletRequest);
        try {
            ipValidationService.validateIp(ipAddress);
        } catch(SecurityException securityException) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"" + securityException.getMessage() + "\"}");
        }
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        List<Entry> entries = fileParserService.parse(content);
            List<Outcome> outcomes = entries
                    .stream()
                    .map( entry -> new Outcome(entry.getName(),entry.getTransport(),entry.getTopSpeed()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "Name: OutcomeFile.json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(outcomes);
    }
}
