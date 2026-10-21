package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvImportService {
    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);
        List<Animal> allAnimals = new ArrayList<>();
        int skippedRows = 0;
        int importedRows = 0;
        try(BufferedReader reader = Files.newBufferedReader(inputPath,StandardCharsets.UTF_8)){
            String header = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null){
                String[] parts = line.split(",",-1);
                if(parts.length != 5){
                    log.warn("Skipping malformed row: {}",line);
                    skippedRows++;
                    continue;
                }
                String name = parts[0].trim();
                if(name.isEmpty()){
                    log.warn("Empty name! Skipping malformed row: {}",line);
                    skippedRows++;
                    continue;
                }
                String species = parts[1].trim();
                if(species.isEmpty()){
                    log.warn("Empty species! Skipping malformed row: {}",line);
                    skippedRows++;
                    continue;
                }
                Integer age;
                if(parts[2].trim().isEmpty()){
                    age = null;
                }
                else{
                    try {
                        age = Integer.parseInt(parts[2].trim());
                        if(age<0){
                            log.warn("Negative age! Skipping malformed row: {}",line);
                            skippedRows++;
                            continue;
                        }
                    }
                    catch (NumberFormatException e) {
                        log.warn("Not a valid number! Skipping malformed row: {}",line);
                        skippedRows++;
                        continue;
                    }
                }
                boolean vaccinated;
                try {
                    vaccinated = Boolean.parseBoolean(parts[3].trim());
                } catch (RuntimeException e) {
                    log.warn("Bad vaccination status! Skipping malformed row: {}",line);
                    skippedRows++;
                    continue;
                }
                LocalDate intakeDate;
                try {
                    intakeDate = LocalDate.parse(
                            parts[4].trim(),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    );
                } catch (RuntimeException e) {
                    log.warn("Bad date! Skipping malformed row: {}",line);
                    skippedRows++;
                    continue;
                }
                importedRows++;
                allAnimals.add(new Animal(name,species,age,vaccinated,intakeDate));
            }
        } catch (IOException e) {
            log.error("Error reading file.");
        }
        return new ImportResult(allAnimals, skippedRows,importedRows);
    }
}
