package fr.rsommerard.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ext.RuntimeDelegate;

import fr.rsommerard.rs.FTPRestService;
import fr.rsommerard.rs.StatusRestService;
import fr.rsommerard.services.FTPService;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import fr.rsommerard.rs.JaxRsApiApplication;

/**
 * Classe de configuration de la passerelle FTP.
 * Permet d'inserer de nouveaux elements au lancement du serveur.
 */
@Configuration
public class AppConfig {
    @Bean( destroyMethod = "shutdown" )
    public SpringBus cxf() {
        return new SpringBus();
    }

    @Bean @DependsOn( "cxf" )
    public Server jaxRsServer() {
        JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance().createEndpoint( jaxRsApiApplication(), JAXRSServerFactoryBean.class );

        List<Object> serviceBeans = new ArrayList<Object>();
        serviceBeans.add(statusRestService());
        serviceBeans.add(ftpRestService());

        factory.setServiceBeans(serviceBeans);
        factory.setAddress( "/" + factory.getAddress() );
        factory.setProviders( Arrays.< Object >asList( jsonProvider() ) );
        return factory.create();
    }

    @Bean
    public JaxRsApiApplication jaxRsApiApplication() {
        return new JaxRsApiApplication();
    }

    @Bean
    public StatusRestService statusRestService() {
        return new StatusRestService();
    }

    @Bean
    public FTPRestService ftpRestService() {
        return new FTPRestService();
    }

    @Bean
    public FTPService ftpService() {
        return new FTPService();
    }

    @Bean
    public JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider();
    }
}
