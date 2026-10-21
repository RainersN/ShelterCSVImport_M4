package lv.bootcamp.shelter;

import lv.bootcamp.shelter.service.CsvImportService;
import lv.bootcamp.shelter.service.JsonExportService;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.ReportExportService;
import lv.bootcamp.shelter.service.ShelterAnalyticsService;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShelterCsvApplication {

    public static void main(String[] args) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path inputPath = Path.of("src", "main", "resources", "data", "intake.csv");
        Path outputPath = Path.of("output", "upload-report-" + timestamp + ".txt");
        Path jsonPath = Path.of("output", "upload-summary-" + timestamp + ".json");

        CsvImportService importService = new CsvImportService();
        ShelterAnalyticsService analyticsService = new ShelterAnalyticsService();
        ReportExportService reportExportService = new ReportExportService();
        JsonExportService jsonExportService = new JsonExportService();

        ImportResult importResult = importService.importAnimals(inputPath);
        ShelterReportData reportData = analyticsService.buildReportData(importResult);
        reportExportService.writeReport(outputPath, reportData);
        jsonExportService.writeJson(jsonPath, reportData);
    }
}