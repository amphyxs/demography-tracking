package com.assistant.centralservicespring.endpoint;

import com.assistant.centralservicespring.service.PersonService;
import com.assistant.centralservicespring.soap.generated.CountByHairColorRequest;
import com.assistant.centralservicespring.soap.generated.CountByHairColorResponse;
import com.assistant.centralservicespring.soap.generated.GetPercentageByNationalityAndEyeColorRequest;
import com.assistant.centralservicespring.soap.generated.GetPercentageByNationalityAndEyeColorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
public class DemographyEndpoint {

    private static final String NAMESPACE_URI = "http://centralservicespring.assistant.com/persons";
    private final PersonService personService;

    public DemographyEndpoint(PersonService personService) {
        this.personService = personService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CountByHairColorRequest")
    @ResponsePayload
    public CountByHairColorResponse countByHairColor(@RequestPayload CountByHairColorRequest request) {
        try {
            log.info("Getting count by hair color: {}", request.getHairColor());

            long count = personService.countByHairColor(request.getHairColor());

            CountByHairColorResponse response = new CountByHairColorResponse();
            response.setCount(count);

            return response;
        } catch (Exception e) {
            log.error("Error getting count by hair color: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing countByHairColor request", e);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPercentageByNationalityAndEyeColorRequest")
    @ResponsePayload
    public GetPercentageByNationalityAndEyeColorResponse getPercentageByNationalityAndEyeColor(
            @RequestPayload GetPercentageByNationalityAndEyeColorRequest request) {
        try {
            log.info("Getting percentage by nationality: {}, eye color: {}",
                    request.getNationality(), request.getEyeColor());

            double percentage = personService.getPercentageByNationalityAndEyeColor(
                    request.getNationality(),
                    request.getEyeColor()
            );

            GetPercentageByNationalityAndEyeColorResponse response = 
                    new GetPercentageByNationalityAndEyeColorResponse();
            response.setPercentage(percentage);

            return response;
        } catch (Exception e) {
            log.error("Error getting percentage: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing getPercentageByNationalityAndEyeColor request", e);
        }
    }
}
