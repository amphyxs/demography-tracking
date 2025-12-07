package com.assistant.centralservicespring.repository;

import com.assistant.centralservicespring.model.Country;
import com.assistant.centralservicespring.model.EyeColor;
import com.assistant.centralservicespring.model.HairColor;
import com.assistant.centralservicespring.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer>, PersonRepositoryCustom {

    @Query("SELECT AVG(p.weight) FROM Person p WHERE p.weight IS NOT NULL")
    Double getAverageWeight();

    @Query("SELECT p FROM Person p WHERE p.height > :minHeight")
    List<Person> findByHeightGreaterThan(@Param("minHeight") double minHeight);

    @Query("SELECT COUNT(p) FROM Person p WHERE p.hairColor = :hairColor")
    long countByHairColor(@Param("hairColor") HairColor hairColor);

    @Query("SELECT COUNT(p) FROM Person p WHERE p.nationality = :nationality")
    long countByNationality(@Param("nationality") Country nationality);

    @Query("SELECT COUNT(p) FROM Person p WHERE p.nationality = :nationality AND p.eyeColor = :eyeColor")
    long countByNationalityAndEyeColor(@Param("nationality") Country nationality,
                                       @Param("eyeColor") EyeColor eyeColor);
}