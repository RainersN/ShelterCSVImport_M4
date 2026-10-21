package lv.bootcamp.shelter.service.data;

import lv.bootcamp.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record ShelterReportData(ImportResult importResult, Set<String> uniqueSpecies, Map<String, List<Animal>> animalsBySpecies, List<String> animalsNeedingVetInput, Map<String, Map<Boolean, Long>> vaccinationCountsBySpecies, Map<String, Optional<Animal>> oldestAnimalBySpecies) {

}
