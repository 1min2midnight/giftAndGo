package org.example.app;

import jakarta.servlet.http.HttpServletRequest;
import org.example.model.Outcome;
import org.example.services.FileParserService;
import org.example.model.Entry;
import org.example.services.IpValidationService;
import org.example.services.RequestLogService;
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
    private final RequestLogService requestLogService;

    public FileProcessorController(FileParserService fileParserService, IpValidationService ipValidationService, RequestLogService requestLogService) {
        this.fileParserService = fileParserService;
        this.ipValidationService = ipValidationService;
        this.requestLogService = requestLogService;
    }

    @PostMapping(
            path = "/parse",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> processFile(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        String ipAddress = ipValidationService.extractClientIp(httpServletRequest);
        String countryCode = "N/A";
        String isp = "N/A";
        int statusCode = 200;
        try {
            var ipInfo = ipValidationService.validateIp(ipAddress);
            countryCode = ipInfo.getCountryCode();
            isp = ipInfo.getIsp();

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

        } catch(SecurityException securityException) {
            statusCode = 403;
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"" + securityException.getMessage() + "\"}");
        } catch(Exception exception){
            statusCode = 500;
            return ResponseEntity
                    .status(statusCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"" + exception.getMessage() + "\"}");
        } finally{
            long endTime = System.currentTimeMillis() - startTime;
            requestLogService.saveLog(httpServletRequest.getRequestURI(), ipAddress, countryCode, isp, statusCode, endTime);
        }
    }
}
