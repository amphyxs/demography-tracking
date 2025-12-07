package com.assistant.centralservice.dto;

import com.assistant.centralservice.model.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ со списком людей")
public class PersonListResponse {

    @Schema(description = "Список людей")
    private List<Person> persons;

    @Schema(description = "Общее количество людей, подходящих под фильтры (без учета пагинации)")
    private int size;

    public PersonListResponse(List<Person> persons) {
        this.persons = persons;
        this.size = persons != null ? persons.size() : 0;
    }
}