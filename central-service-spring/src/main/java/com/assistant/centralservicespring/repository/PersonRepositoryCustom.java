package com.assistant.centralservicespring.repository;

import com.assistant.centralservicespring.model.Person;

import java.util.List;
import java.util.Map;

public interface PersonRepositoryCustom {

    List<Person> findWithFiltersAndSort(Map<String, Object> filters,
                                        List<String> sortParams,
                                        int page,
                                        int size);

    long countWithFilters(Map<String, Object> filters);

    long countByLocation(Float x, Integer y, String name);

    double getPercentageByNationalityAndEyeColor(String nationality, String eyeColor);
}