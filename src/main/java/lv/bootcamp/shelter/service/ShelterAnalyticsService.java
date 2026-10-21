package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.*;
import java.util.stream.Collectors;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();
        //task 2
        Set<String> uniqueSpecies = allAnimals.stream().map(Animal::getSpecies).collect(Collectors.toCollection(TreeSet::new));
        Map<String, List<Animal>> animalsBySpecies = allAnimals.stream().collect(Collectors.groupingBy(Animal::getSpecies));
        List<String> animalsNeedingVetInput = allAnimals.stream().filter(animal -> animal.getAge() == null).map(animal -> animal.getName() + "(" + animal.getSpecies() + ")" ).toList();
        //task 3
        Map<String, Map<Boolean, Long>> vaccinationCountsBySpecies = allAnimals.stream().collect(Collectors.groupingBy(Animal::getSpecies, Collectors.partitioningBy(Animal::isVaccinated, Collectors.counting())));
        Map<String, Optional<Animal>> oldestAnimalBySpecies = allAnimals.stream().filter(animal -> animal.getAge() != null).collect(Collectors.groupingBy(Animal::getSpecies, Collectors.maxBy(Comparator.comparing(Animal::getAge))));

        return new ShelterReportData(importResult, uniqueSpecies, animalsBySpecies, animalsNeedingVetInput, vaccinationCountsBySpecies, oldestAnimalBySpecies);
    }
}
