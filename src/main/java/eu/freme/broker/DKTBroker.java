package eu.freme.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import eu.freme.broker.tools.StarterHelper;

@SpringBootApplication
@Import(DKTBrokerConfig.class)
public class DKTBroker {
    public static void main(String[] args) {
		String[] newArgs = StarterHelper.addProfile(args, "broker");

        SpringApplication.run(DKTBroker.class, newArgs);
    }
}