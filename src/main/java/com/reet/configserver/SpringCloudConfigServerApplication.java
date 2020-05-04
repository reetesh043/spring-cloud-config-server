package com.reet.configserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@EnableConfigServer
@SpringBootApplication
public class SpringCloudConfigServerApplication {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServerApplication.class, args);
    }

}


@Component
class RefreshConfig {

    private static final Logger logger = LoggerFactory.getLogger(RefreshConfig.class);


    @Autowired
    private RestTemplate restTemplate;

    @Value("${application.url}")
    private String refreshUrl;

    @Scheduled(cron = "${cron.expression}") // (*/10 * * * * *) run every 10 seconds
    public void refresh() {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);

        try {
            restTemplate.exchange(refreshUrl, HttpMethod.POST, requestEntity, Void.class);
        } catch (HttpClientErrorException ex) {
            logger.error("Error occurred while refreshing config server", ex.getLocalizedMessage());
        }
    }
}