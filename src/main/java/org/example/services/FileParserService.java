package org.example.services;

import org.example.config.ValidationProperties;
import org.example.model.Entry;
import org.example.validator.EntryValidator;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileParserService {

    private static final int EXPECTED_COLUMNS = 7;
    private final EntryValidator entryValidator;
    private final ValidationProperties validationProperties;

    public FileParserService(EntryValidator entryValidator, ValidationProperties validationProperties) {
        this.entryValidator = entryValidator;
        this.validationProperties = validationProperties;
    }
    public List<Entry> parse(String content){
        List<Entry> entries = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");

        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            line = line.trim();

            if(line.isBlank()) continue;

            String [] parts = line.split("\\|");
            if (parts.length != EXPECTED_COLUMNS){
               throw new IllegalArgumentException("Line " + lineNumber + " does not match expected number of columns");
            }

            try{
                Entry entry = new Entry();
                entry.setUuid(UUID.fromString(parts[0].trim()));
                entry.setId(parts[1].trim());
                entry.setName(parts[2].trim());
                entry.setLikes(parts[3].trim());
                entry.setTransport(parts[4].trim());
                entry.setAverageSpeed(new BigDecimal(parts[5].trim()));
                entry.setTopSpeed(new BigDecimal(parts[6].trim()));

                if(this.validationProperties.isEnabled()) {
                    BindingResult errors = new BeanPropertyBindingResult(entry, "entry");
                    entryValidator.validate(entry, errors);

                    if (errors.hasErrors()) {
                        var message = errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
                        throw new IllegalArgumentException(" Validation failed at" + lineNumber + " reason: " + message);
                    }
                }
                entries.add(entry);
            } catch (Exception e){
                throw new IllegalArgumentException("Line " + lineNumber + "failed to parse" + e.getMessage());
            }
        }
        if(entries.isEmpty()){
            throw new IllegalArgumentException("No valid entries were found");
        }
        return entries;
    }
}
