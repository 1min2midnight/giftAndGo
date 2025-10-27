package org.example.app;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/entries")
public class FileProcessorController {
    @PostMapping(
            path = "/parse",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> processFile(@RequestParam("file") MultipartFile file, @RequestBody(required = false) String body) {
        String response = String.format("File: %s , size: %s", file.getOriginalFilename(),  file.getSize());

        return ResponseEntity.ok(response);
    }
}
