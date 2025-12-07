package services.impl;

import exceptions.ProxyServiceException;
import lombok.extern.java.Log;
import org.jboss.ejb3.annotation.Pool;
import services.DemographyProxyService;

import jakarta.ejb.Stateless;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Level;

@Stateless
@Pool("slsb-strict-max-pool")
@Log
public class DemographyProxyServiceImpl implements DemographyProxyService {

    private static final String CENTRAL_SERVICE_URL = "http://localhost:18085/central-service/api";
    private static final String TRUSTSTORE_PATH = System.getProperty("jboss.server.config.dir") + "/proxy-truststore.p12";
    private static final String TRUSTSTORE_PASSWORD = "changeit";

    private Client createSecureClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(TRUSTSTORE_PATH)) {
                trustStore.load(fis, TRUSTSTORE_PASSWORD.toCharArray());
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            HostnameVerifier allHostsValid = (hostname, session) -> true;

            return ClientBuilder.newBuilder()
                    .sslContext(sslContext)
                    .hostnameVerifier(allHostsValid)
                    .build();

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error creating secure client", e);
            throw new ProxyServiceException("Failed to create secure client", e);
        }
    }

    @Override
    public Long getCountByHairColor(String hairColor) {
        log.info("Proxy request: Getting count by hair color: " + hairColor);

        Client client = createSecureClient();
        try {
            Response response = client
                    .target(CENTRAL_SERVICE_URL)
                    .path("/demography/hair-color/{hairColor}")
                    .resolveTemplate("hairColor", hairColor)
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                Long result = response.readEntity(Long.class);
                log.info("Proxy response: Count for hair color " + hairColor + " is " + result);
                return result;
            } else {
                log.warning("Error response status: " + response.getStatus());
                throw new ProxyServiceException("Error calling central service: " + response.getStatus());
            }
        } catch (ProxyServiceException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in proxy request for hair color " + hairColor, e);
            throw new ProxyServiceException("Error in proxy request", e);
        } finally {
            client.close();
        }
    }

    @Override
    public Double getPercentageByNationalityAndEyeColor(String nationality, String eyeColor) {
        log.info("Proxy request: Getting percentage by nationality: " + nationality + " and eye color: " + eyeColor);

        Client client = createSecureClient();
        try {
            Response response = client
                    .target(CENTRAL_SERVICE_URL)
                    .path("/demography/nationality/{nationality}/eye-color/{eyeColor}/percentage")
                    .resolveTemplate("nationality", nationality)
                    .resolveTemplate("eyeColor", eyeColor)
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                Double result = response.readEntity(Double.class);
                log.info("Proxy response: Percentage for nationality " + nationality + 
                        " and eye color " + eyeColor + " is " + result + "%");
                return result;
            } else {
                log.warning("Error response status: " + response.getStatus());
                throw new ProxyServiceException("Error calling central service: " + response.getStatus());
            }
        } catch (ProxyServiceException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in proxy request for nationality " + nationality + 
                    " and eye color " + eyeColor, e);
            throw new ProxyServiceException("Error in proxy request", e);
        } finally {
            client.close();
        }
    }
}
