package com.assistant.proxyservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.DemographyProxyService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

@Configuration
@Slf4j
public class RemoteEjbConfig {

    @Value("${ejb.remote.host:localhost}")
    private String ejbHost;

    @Value("${ejb.remote.port:29443}")
    private int ejbPort;

    @Value("${ejb.remote.app-name:ProxyEJB}")
    private String appName;

    private Context createInitialContext() throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://" + ejbHost + ":" + ejbPort);
        props.put("jboss.naming.client.ejb.context", true);
        
        // Это чтобы отключить JBOSS-LOCAL-USER для работы через SSH туннель, будем работать под нашим пользователем
        props.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
        props.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

        log.info("Creating JNDI context for Remote EJB: {}:{}", ejbHost, ejbPort);
        
        return new InitialContext(props);
    }

    @Bean
    public DemographyProxyService demographyProxyService() throws NamingException {
        Context context = createInitialContext();
        String jndiName = "ejb:/" + appName + "/DemographyProxyServiceImpl!services.DemographyProxyService";
        log.info("Looking up Remote EJB with JNDI name: {}", jndiName);
        DemographyProxyService service = (DemographyProxyService) context.lookup(jndiName);
        log.info("Successfully obtained Remote EJB proxy: {}", service.getClass().getName());
        
        return service;
    }
}
