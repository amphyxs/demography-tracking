package com.assistant.centralservicespring.service;

import com.assistant.centralservicespring.model.*;
import com.assistant.centralservicespring.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person create(Person person) {
        person.setCreationDate(new Date());
        return personRepository.save(person);
    }

    public Optional<Person> findById(Integer id) {
        return personRepository.findById(id);
    }

    public Person update(Integer id, Person updatedPerson) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isEmpty()) {
            return null;
        }
        Person person = optionalPerson.get();
        person.setName(updatedPerson.getName());
        person.setCoordinates(updatedPerson.getCoordinates());
        person.setHeight(updatedPerson.getHeight());
        person.setBirthday(updatedPerson.getBirthday());
        person.setWeight(updatedPerson.getWeight());
        person.setNationality(updatedPerson.getNationality());
        person.setLocation(updatedPerson.getLocation());
        person.setHairColor(updatedPerson.getHairColor());
        person.setEyeColor(updatedPerson.getEyeColor());
        return personRepository.save(person);
    }

    public boolean delete(Integer id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public List<Person> findFiltered(Map<String, Object> filters,
                                     List<String> sortParams,
                                     int page,
                                     int size) {
        return personRepository.findWithFiltersAndSort(filters, sortParams, page, size);
    }

    public long countFiltered(Map<String, Object> filters) {
        return personRepository.countWithFilters(filters);
    }

    public Double getAverageWeight() {
        Double average = personRepository.getAverageWeight();
        return average != null ? average : 0.0;
    }

    public long countByLocation(Float x, Integer y, String name) {
        return personRepository.countByLocation(x, y, name);
    }

    public List<Person> findByHeightGreaterThan(double minHeight) {
        return personRepository.findByHeightGreaterThan(minHeight);
    }

    public long getTotalCount() {
        return personRepository.count();
    }

    public long countByHairColor(String hairColorString) {
        try {
            HairColor hairColor = HairColor.valueOf(hairColorString.toUpperCase());
            return personRepository.countByHairColor(hairColor);
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    public double getPercentageByNationalityAndEyeColor(String nationalityString, String eyeColorString) {
        return personRepository.getPercentageByNationalityAndEyeColor(
                nationalityString.toUpperCase(),
                eyeColorString.toUpperCase()
        );
    }


    public Map<String, Object> createFilters(Integer id, Integer idLt, Integer idGt,
                                              String name, Double height, Double heightLt, Double heightGt,
                                              Long weight, Long weightLt, Long weightGt,
                                              String nationality, String birthday, String birthdayLt, String birthdayGt) {
        Map<String, Object> filters = new HashMap<>();

        if (id != null) filters.put("id", id);
        if (idLt != null) filters.put("id[lt]", idLt);
        if (idGt != null) filters.put("id[gt]", idGt);
        if (name != null) filters.put("name", name);
        if (height != null) filters.put("height", height);
        if (heightLt != null) filters.put("height[lt]", heightLt);
        if (heightGt != null) filters.put("height[gt]", heightGt);
        if (weight != null) filters.put("weight", weight);
        if (weightLt != null) filters.put("weight[lt]", weightLt);
        if (weightGt != null) filters.put("weight[gt]", weightGt);
        if (nationality != null) filters.put("nationality", nationality);

        if (birthday != null) filters.put("birthday", LocalDate.parse(birthday));
        if (birthdayLt != null) filters.put("birthday[lt]", LocalDate.parse(birthdayLt));
        if (birthdayGt != null) filters.put("birthday[gt]", LocalDate.parse(birthdayGt));

        return filters;
    }
}