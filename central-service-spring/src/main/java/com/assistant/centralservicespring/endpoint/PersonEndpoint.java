package com.assistant.centralservicespring.endpoint;

import com.assistant.centralservicespring.service.PersonService;
import com.assistant.centralservicespring.soap.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Endpoint
public class PersonEndpoint {

    private static final String NAMESPACE_URI = "http://centralservicespring.assistant.com/persons";
    private final PersonService personService;

    public PersonEndpoint(PersonService personService) {
        this.personService = personService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPersonsRequest")
    @ResponsePayload
    public GetPersonsResponse getPersons(@RequestPayload GetPersonsRequest request) {
        try {
            Map<String, Object> filters = personService.createFilters(
                    request.getId() != null && request.getId() != 0 ? request.getId() : null,
                    request.getIdLt() != null && request.getIdLt() != 0 ? request.getIdLt() : null,
                    request.getIdGt() != null && request.getIdGt() != 0 ? request.getIdGt() : null,
                    request.getName(),
                    request.getHeight() != null && request.getHeight() != 0.0 ? request.getHeight() : null,
                    request.getHeightLt() != null && request.getHeightLt() != 0.0 ? request.getHeightLt() : null,
                    request.getHeightGt() != null && request.getHeightGt() != 0.0 ? request.getHeightGt() : null,
                    request.getWeight() != null && request.getWeight() != 0L ? request.getWeight() : null,
                    request.getWeightLt() != null && request.getWeightLt() != 0L ? request.getWeightLt() : null,
                    request.getWeightGt() != null && request.getWeightGt() != 0L ? request.getWeightGt() : null,
                    request.getNationality(),
                    request.getBirthday(),
                    request.getBirthdayLt(),
                    request.getBirthdayGt()
            );

            List<String> sortParams = request.getSort();
            int page = request.getPage();
            int size = request.getSize();

            List<com.assistant.centralservicespring.model.Person> persons = 
                    personService.findFiltered(filters, sortParams, page, size);
            long totalCount = personService.countFiltered(filters);

            GetPersonsResponse response = new GetPersonsResponse();
            response.getPersons().addAll(persons.stream()
                    .map(this::convertToSoapPerson)
                    .collect(Collectors.toList()));
            response.setTotalCount((int) totalCount);

            return response;
        } catch (Exception e) {
            log.error("Error in getPersons: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing getPersons request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreatePersonRequest")
    @ResponsePayload
    public CreatePersonResponse createPerson(@RequestPayload CreatePersonRequest request) {
        try {
            com.assistant.centralservicespring.model.Person person = 
                    convertFromSoapPerson(request.getPerson());
            com.assistant.centralservicespring.model.Person created = 
                    personService.create(person);

            CreatePersonResponse response = new CreatePersonResponse();
            response.setPerson(convertToSoapPerson(created));

            return response;
        } catch (Exception e) {
            log.error("Error in createPerson: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing createPerson request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPersonByIdRequest")
    @ResponsePayload
    public GetPersonByIdResponse getPersonById(@RequestPayload GetPersonByIdRequest request) {
        try {
            Optional<com.assistant.centralservicespring.model.Person> person = 
                    personService.findById(request.getId());

            if (person.isEmpty()) {
                throw new RuntimeException("Person not found with id: " + request.getId());
            }

            GetPersonByIdResponse response = new GetPersonByIdResponse();
            response.setPerson(convertToSoapPerson(person.get()));

            return response;
        } catch (Exception e) {
            log.error("Error in getPersonById: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing getPersonById request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdatePersonRequest")
    @ResponsePayload
    public UpdatePersonResponse updatePerson(@RequestPayload UpdatePersonRequest request) {
        try {
            com.assistant.centralservicespring.model.Person person = 
                    convertFromSoapPerson(request.getPerson());
            com.assistant.centralservicespring.model.Person updated = 
                    personService.update(request.getId(), person);

            if (updated == null) {
                throw new RuntimeException("Person not found with id: " + request.getId());
            }

            UpdatePersonResponse response = new UpdatePersonResponse();
            response.setPerson(convertToSoapPerson(updated));

            return response;
        } catch (Exception e) {
            log.error("Error in updatePerson: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing updatePerson request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeletePersonRequest")
    @ResponsePayload
    public DeletePersonResponse deletePerson(@RequestPayload DeletePersonRequest request) {
        try {
            boolean deleted = personService.delete(request.getId());

            DeletePersonResponse response = new DeletePersonResponse();
            response.setSuccess(deleted);

            return response;
        } catch (Exception e) {
            log.error("Error in deletePerson: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing deletePerson request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetAverageWeightRequest")
    @ResponsePayload
    public GetAverageWeightResponse getAverageWeight(@RequestPayload GetAverageWeightRequest request) {
        try {
            Double average = personService.getAverageWeight();

            GetAverageWeightResponse response = new GetAverageWeightResponse();
            response.setAverageWeight(average != null ? average : 0.0);

            return response;
        } catch (Exception e) {
            log.error("Error in getAverageWeight: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing getAverageWeight request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CountByLocationRequest")
    @ResponsePayload
    public CountByLocationResponse countByLocation(@RequestPayload CountByLocationRequest request) {
        try {
            long count = personService.countByLocation(
                    request.getX() != null ? request.getX() : null,
                    request.getY() != null ? request.getY() : null,
                    request.getName()
            );

            CountByLocationResponse response = new CountByLocationResponse();
            response.setCount((int) count);

            return response;
        } catch (Exception e) {
            log.error("Error in countByLocation: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing countByLocation request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPersonsByHeightRequest")
    @ResponsePayload
    public GetPersonsByHeightResponse getPersonsByHeight(@RequestPayload GetPersonsByHeightRequest request) {
        try {
            List<com.assistant.centralservicespring.model.Person> persons = 
                    personService.findByHeightGreaterThan(request.getMinHeight());

            GetPersonsByHeightResponse response = new GetPersonsByHeightResponse();
            response.getPersons().addAll(persons.stream()
                    .map(this::convertToSoapPerson)
                    .collect(Collectors.toList()));

            return response;
        } catch (Exception e) {
            log.error("Error in getPersonsByHeight: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing getPersonsByHeight request", e);
        }
    }

    private com.assistant.centralservicespring.soap.generated.Person convertToSoapPerson(
            com.assistant.centralservicespring.model.Person person) {
        com.assistant.centralservicespring.soap.generated.Person soapPerson = 
                new com.assistant.centralservicespring.soap.generated.Person();

        soapPerson.setId(person.getId());
        soapPerson.setName(person.getName());
        soapPerson.setHeight(person.getHeight());
        soapPerson.setBirthday(convertToXMLGregorianCalendar(person.getBirthday()));
        soapPerson.setWeight(person.getWeight());

        if (person.getCreationDate() != null) {
            soapPerson.setCreationDate(convertToXMLGregorianCalendar(person.getCreationDate()));
        }

        com.assistant.centralservicespring.soap.generated.Coordinates coords = 
                new com.assistant.centralservicespring.soap.generated.Coordinates();
        coords.setX(person.getCoordinates().getX());
        coords.setY((int) person.getCoordinates().getY());
        soapPerson.setCoordinates(coords);

        com.assistant.centralservicespring.soap.generated.Location location = 
                new com.assistant.centralservicespring.soap.generated.Location();
        location.setX(person.getLocation().getX());
        location.setY(person.getLocation().getY());
        location.setName(person.getLocation().getName());
        soapPerson.setLocation(location);

        soapPerson.setNationality(
                com.assistant.centralservicespring.soap.generated.Country.valueOf(person.getNationality().name()));

        if (person.getHairColor() != null) {
            soapPerson.setHairColor(
                    com.assistant.centralservicespring.soap.generated.HairColor.valueOf(person.getHairColor().name()));
        }

        if (person.getEyeColor() != null) {
            soapPerson.setEyeColor(
                    com.assistant.centralservicespring.soap.generated.EyeColor.valueOf(person.getEyeColor().name()));
        }

        return soapPerson;
    }

    private com.assistant.centralservicespring.model.Person convertFromSoapPerson(
            com.assistant.centralservicespring.soap.generated.Person soapPerson) {
        com.assistant.centralservicespring.model.Person person = 
                new com.assistant.centralservicespring.model.Person();

        if (soapPerson.getId() != null) {
            person.setId(soapPerson.getId());
        }
        person.setName(soapPerson.getName());
        person.setHeight(soapPerson.getHeight());
        person.setBirthday(convertFromXMLGregorianCalendar(soapPerson.getBirthday()));
        person.setWeight(soapPerson.getWeight());

        com.assistant.centralservicespring.model.Coordinates coords = 
                new com.assistant.centralservicespring.model.Coordinates();
        coords.setX(soapPerson.getCoordinates().getX());
        coords.setY(soapPerson.getCoordinates().getY());
        person.setCoordinates(coords);

        com.assistant.centralservicespring.model.Location location = 
                new com.assistant.centralservicespring.model.Location();
        location.setX(soapPerson.getLocation().getX());
        location.setY(soapPerson.getLocation().getY());
        location.setName(soapPerson.getLocation().getName());
        person.setLocation(location);

        person.setNationality(
                com.assistant.centralservicespring.model.Country.valueOf(soapPerson.getNationality().name()));

        if (soapPerson.getHairColor() != null) {
            person.setHairColor(
                    com.assistant.centralservicespring.model.HairColor.valueOf(soapPerson.getHairColor().name()));
        }

        if (soapPerson.getEyeColor() != null) {
            person.setEyeColor(
                    com.assistant.centralservicespring.model.EyeColor.valueOf(soapPerson.getEyeColor().name()));
        }

        return person;
    }

    private XMLGregorianCalendar convertToXMLGregorianCalendar(LocalDate date) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error converting LocalDate to XMLGregorianCalendar", e);
        }
    }

    private XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error converting Date to XMLGregorianCalendar", e);
        }
    }

    private LocalDate convertFromXMLGregorianCalendar(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }
}
