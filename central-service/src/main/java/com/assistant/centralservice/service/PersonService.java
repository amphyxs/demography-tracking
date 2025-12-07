package com.assistant.centralservice.service;

import com.assistant.centralservice.dto.PersonCreateRequest;
import com.assistant.centralservice.model.Person;
import com.assistant.centralservice.model.HairColor;
import com.assistant.centralservice.model.EyeColor;
import com.assistant.centralservice.model.Country;
import com.assistant.centralservice.repository.PersonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.*;


@ApplicationScoped
@Transactional
public class PersonService {

    @Inject
    private PersonRepository personRepository;

    public Person create(PersonCreateRequest request) {
        Person person = new Person();
        person.setName(request.getName());
        person.setCoordinates(request.getCoordinates());
        person.setHeight(request.getHeight());
        person.setBirthday(request.getBirthday());
        person.setWeight(request.getWeight());
        person.setNationality(request.getNationality());
        person.setLocation(request.getLocation());
        person.setHairColor(request.getHairColor());
        person.setEyeColor(request.getEyeColor());
        person.setCreationDate(new Date());

        return personRepository.save(person);
    }

    public Optional<Person> findById(Integer id) {
        return personRepository.findById(id);
    }

    public Person update(Integer id, PersonCreateRequest request) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isEmpty()) {
            return null;
        }

        Person person = optionalPerson.get();
        person.setName(request.getName());
        person.setCoordinates(request.getCoordinates());
        person.setHeight(request.getHeight());
        person.setBirthday(request.getBirthday());
        person.setWeight(request.getWeight());
        person.setNationality(request.getNationality());
        person.setLocation(request.getLocation());
        person.setHairColor(request.getHairColor());
        person.setEyeColor(request.getEyeColor());

        return personRepository.save(person);
    }

    public boolean delete(Integer id) {
        return personRepository.deleteById(id);
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
        return personRepository.getTotalCount();
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
        try {
            Country nationality = Country.valueOf(nationalityString.toUpperCase());
            EyeColor eyeColor = EyeColor.valueOf(eyeColorString.toUpperCase());
            return personRepository.getPercentageByNationalityAndEyeColor(nationality, eyeColor);
        } catch (IllegalArgumentException e) {
            return 0.0;
        }
    }
}