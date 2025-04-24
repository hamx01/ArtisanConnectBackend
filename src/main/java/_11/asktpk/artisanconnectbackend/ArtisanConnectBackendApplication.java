package _11.asktpk.artisanconnectbackend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ArtisanConnectBackendApplication {

    private final Environment environment;

    public ArtisanConnectBackendApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(ArtisanConnectBackendApplication.class, args);
    }

    @PostConstruct
    public void logDataSourceUrl() {
        System.out.println("Datasource URL: " + environment.getProperty("spring.datasource.url"));
    }
}


