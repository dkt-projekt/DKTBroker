package eu.freme.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import eu.freme.broker.tools.StarterHelper;
import eu.freme.common.FREMECommonConfig;
import eu.freme.eservices.eentity.EEntityConfig;
import eu.freme.eservices.elink.ELinkConfig;
import eu.freme.eservices.epublishing.EPublishingConfig;
import eu.freme.eservices.pipelines.api.PipelineConfig;
import eu.freme.i18n.api.EInternationalizationConfig;

@SpringBootApplication
@Import({ DKTBrokerConfig.class, FremeCommonConfig.class, EEntityConfig.class, ELinkConfig.class,
	EPublishingConfig.class, FREMECommonConfig.class,
	PipelineConfig.class, EInternationalizationConfig.class,
	EWekaConfig.class, ELuceneConfig.class, EDocumentStorageConfig.class, EOpenNLPConfig.class })
@Profile("broker")
public class DKTBroker {
    public static void main(String[] args) {
		String[] newArgs = StarterHelper.addProfile(args, "broker");

        SpringApplication.run(DKTBroker.class, newArgs);
    }
}