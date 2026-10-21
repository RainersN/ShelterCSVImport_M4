package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) {
        log.info("Starting export to {}", outputPath);
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            writer.write("=== Shelter Intake Report ===");
            writer.newLine();
            writer.write("Date generated: " + currentDate);
            writer.newLine();
            writer.newLine();
            writer.write("Total imported: " + reportData.importResult().importedRows());
            writer.newLine();
            writer.write("Total skipped: " + reportData.importResult().skippedRows());
            writer.newLine();
            writer.newLine();

            writer.write("--- Unique species ---");
            writer.newLine();
            writer.write(String.join(", ", reportData.uniqueSpecies()));
            writer.newLine();
            writer.newLine();

            writer.write("--- Per-species breakdown ---");
            writer.newLine();
            for (String species : reportData.uniqueSpecies()) {
                List<Animal> animals = reportData.animalsBySpecies().getOrDefault(species, List.of());
                Map<Boolean, Long> vaccination = reportData.vaccinationCountsBySpecies().getOrDefault(species, Map.of());

                writer.write(String.format("%-9s", species + ":") + animals.size() + " total, " + vaccination.getOrDefault(true, 0L) + " vaccinated");
                writer.newLine();
            }
            writer.newLine();

            writer.write("--- Oldest per species ---");
            writer.newLine();
            writer.newLine();
            for (String species : reportData.uniqueSpecies()) {
                Optional<Animal> oldest =
                        reportData.oldestAnimalBySpecies().getOrDefault(species, Optional.empty());

                if (oldest.isPresent()) {
                    writer.write(String.format("%-9s", species + ":")
                            + oldest.get().getName() + "(age " + oldest.get().getAge() + ")");
                    writer.newLine();
                }
            }
            writer.newLine();
            writer.newLine();

            writer.write("--- Needs Vet input to determine age ---");
            writer.newLine();
            writer.write(String.join(", ", reportData.animalsNeedingVetInput()));

            log.info("File written successfully.");
        } catch (IOException e) {
            log.error("Error writing file: {}", e.getMessage());
        }
    }
}