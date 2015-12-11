/**
 * Copyright (C) 2015 3pc, Art+Com, Condat, Deutsches Forschungszentrum 
 * für Künstliche Intelligenz, Kreuzwerke (http://)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.freme.broker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.freme.broker.edocumentstorage.api.EDocumentStorageService;
import eu.freme.broker.esesame.api.ESesameService;


//@SpringBootApplication
//@ComponentScan("de.dkt.eservices.eopennlp.api")
@Configuration
public class DKTBrokerConfig {
	
	@Value("${documentstorage.storage}")
	String documentStorageLocation;
	
	@Value("${sesame.storage}")
	String sesameStorageLocation;
	
	@Bean
	public ESesameService getSesameApi(){
		return new ESesameService(sesameStorageLocation);
	}
	
	@Bean
	public EDocumentStorageService getDocumentStorageApi(){
		return new EDocumentStorageService();
	}
	
}