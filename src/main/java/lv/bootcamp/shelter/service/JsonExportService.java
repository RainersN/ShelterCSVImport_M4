package lv.bootcamp.shelter.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JsonExportService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void writeJson(Path outputPath, ShelterReportData reportData) {
        log.info("Starting JSON export to {}", outputPath);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("dateGenerated", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        report.put("totalImported", reportData.importResult().importedRows());
        report.put("totalSkipped", reportData.importResult().skippedRows());
        report.put("uniqueSpecies", new ArrayList<>(reportData.uniqueSpecies()));

        Map<String, Object> breakdown = new LinkedHashMap<>();
        for (String species : reportData.uniqueSpecies()) {
            List<Animal> animals = reportData.animalsBySpecies().getOrDefault(species, List.of());
            Map<Boolean, Long> vaccination = reportData.vaccinationCountsBySpecies().getOrDefault(species, Map.of());
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("total", animals.size());
            entry.put("vaccinated", vaccination.getOrDefault(true, 0L));
            breakdown.put(species, entry);
        }
        report.put("perSpeciesBreakdown", breakdown);

        Map<String, Object> oldest = new LinkedHashMap<>();
        for (String species : reportData.uniqueSpecies()) {
            Optional<Animal> animal = reportData.oldestAnimalBySpecies().getOrDefault(species, Optional.empty());
            animal.ifPresent(a -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("name", a.getName());
                entry.put("age", a.getAge());
                oldest.put(species, entry);
            });
        }
        report.put("oldestPerSpecies", oldest);

        report.put("needsVetInput", reportData.animalsNeedingVetInput());

        try {
            Files.writeString(outputPath, gson.toJson(report), StandardCharsets.UTF_8);
            log.info("JSON file written successfully.");
        } catch (IOException e) {
            log.error("Error writing JSON file: {}", e.getMessage());
        }
    }
}