package services;

import jakarta.ejb.Remote;

@Remote
public interface DemographyProxyService {

    Long getCountByHairColor(String hairColor);

    Double getPercentageByNationalityAndEyeColor(String nationality, String eyeColor);

}
