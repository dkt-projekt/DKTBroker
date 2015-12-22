package eu.freme.broker.eservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.freme.broker.edocumentstorage.api.EDocumentStorageService;
import eu.freme.broker.edocumentstorage.exceptions.BadRequestException;
import eu.freme.broker.edocumentstorage.exceptions.ExternalServiceFailedException;
import eu.freme.broker.elucene.api.ELuceneService;
import eu.freme.broker.eopennlp.api.EOpenNLPService;
import eu.freme.broker.esesame.api.ESesameService;
import eu.freme.broker.eweka.api.EWekaService;
import eu.freme.broker.filemanagement.FileFactory;
import eu.freme.broker.niftools.NIFReader;
import eu.freme.broker.niftools.NIFWriter;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;

@RestController
public class DocumentAnalyzer extends BaseRestController{

	@Value("${documentstorage.storage}")
	String documentStorageLocation;
	
	@Value("${sesame.storage}")
	String sesameStorageLocation;
	
	@Autowired
	EDocumentStorageService docStorageService;
	
	@Autowired
	ESesameService sesameService;
	
	@Autowired
	ELuceneService luceneService;
	
	@Autowired
	EOpenNLPService openNLPService;
	
	@Autowired
	EWekaService wekaService;
	
//	@Autowired
//	ESMTService smtService;
	
	@Autowired
	RDFConversionService rdfConversion;
	
	public DocumentAnalyzer() {
	}
	
	@RequestMapping(value = "/documentanalyzer/processDocument", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> storeData(
			@RequestParam(value = "storageFileName", required = false) String storageFileName,
			@RequestParam(value = "inputFilePath", required = false) String inputFilePath,
//			@RequestParam(value = "preffix", required = false) String preffix,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "openNLPAnalysis", required = false) String openNLPAnalysis,
			@RequestParam(value = "openNLPModels", required = false) String openNLPModels,
			@RequestParam(value = "sesameStorageName", required = false) String sesameStorageName,
			@RequestParam(value = "luceneIndexName", required = false) String luceneIndexName,
			@RequestParam(value = "luceneFields", required = false) String luceneFields,
			@RequestParam(value = "luceneAnalyzers", required = false) String luceneAnalyzers,
			@RequestParam(value = "luceneCreate", required = false) boolean luceneCreate,
            @RequestBody(required = false) String postBody) throws Exception {

		EDocumentStorageService.checkNotNullOrEmpty(inputFilePath, "input data type");
//		EDocumentStorageService.checkNotNullOrEmpty(storageFileName, "storage file name");
		EDocumentStorageService.checkNotNullOrEmpty(storageFileName, "language");
		EDocumentStorageService.checkNotNullOrEmpty(openNLPAnalysis, "open NLP analysis type");
		EDocumentStorageService.checkNotNullOrEmpty(sesameStorageName, "sesame storage");
		EDocumentStorageService.checkNotNullOrEmpty(luceneIndexName, "lucene index name");
		EDocumentStorageService.checkNotNullOrEmpty(luceneFields, "lucene fields");
		EDocumentStorageService.checkNotNullOrEmpty(luceneAnalyzers, "lucene analyzers");

		Date d1 = new Date();
		int fileCounter=1;
		
		File inputFile = FileFactory.generateFileInstance(inputFilePath);
		if(inputFile==null || !inputFile.exists()){
			throw new eu.freme.broker.exception.ExternalServiceFailedException("No input file provided.");
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
			String line = br.readLine();
			while(line!=null){
				if(!line.equalsIgnoreCase("") && line.split(" ").length>3){
					
					String contentString = line;
		        	ResponseEntity<String> response1 = docStorageService.storeFileByString("testFile"+fileCounter+"_"+d1.toGMTString().replace(' ', '_')+".txt", contentString, "http://jmschnei.dfki.de/test1/");
		        	
		        	String content2 = response1.getBody();
		        	
		        	System.out.println("INPUT FOR NLP"+content2);
		        	
		        	ResponseEntity<String> response2 = openNLPService.analyzeText(content2, language, openNLPAnalysis, openNLPModels);
		        	
		        	String content3 = response2.getBody();
		        	
		        	System.out.println("INPUT FOR SESAME"+content3);
		        	
		        	ResponseEntity<String> response3 = sesameService.storeEntitiesFromString(sesameStorageName, content3, "NIF");
		        	
		        	String content4 = response3.getBody();
		        	System.out.println("OUTPUT OF SESAME"+content4);
		        	
		        	System.out.println("INPUT FOR LUCENE"+content3);
		        	
		        	ResponseEntity<String> response4 = luceneService.callLuceneIndexing("string", content3, "NIF", language, luceneFields, luceneAnalyzers, luceneIndexName, luceneCreate);
		        	
		        	String content5 = response4.getBody();
		        	System.out.println("OUTPUT OF LUCENE"+content5);
					
//		        	ResponseEntity<String> response5 = wekaService.;
//		        	
//		        	String content6 = response5.getBody();
					
//		        	ResponseEntity<String> response6 = smtService.;
//		        	
//		        	String content7 = response6.getBody();
					
		        	fileCounter++;
				}
				line = br.readLine();
			}
			br.close();

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", "RDF/XML");
//			StringWriter writer = new StringWriter();
//			outModel.write(writer, "RDF/XML");
//			try {
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			String rdfString = writer.toString();

			String rdfString = "The document has been succesfully processed.";

			return new ResponseEntity<String>(rdfString, responseHeaders, HttpStatus.OK);
		} catch (BadRequestException e) {
            throw e;
        } catch (ExternalServiceFailedException e) {
            throw e;
        }
	}

	@RequestMapping(value = "/documentanalyzer/processQuery", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> processQuery(
			@RequestParam(value = "queryText", required = false) String queryText,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "output", required = false) String output,
			@RequestParam(value = "openNLPAnalysis", required = false) String openNLPAnalysis,
			@RequestParam(value = "openNLPModels", required = false) String openNLPModels,
			@RequestParam(value = "sesameStorageName", required = false) String sesameStorageName,
			@RequestParam(value = "luceneIndexName", required = false) String luceneIndexName,
			@RequestParam(value = "luceneFields", required = false) String luceneFields,
			@RequestParam(value = "luceneAnalyzers", required = false) String luceneAnalyzers,
			@RequestParam(value = "luceneCreate", required = false) boolean luceneCreate,
			@RequestBody(required = false) String postBody) throws Exception {

		EDocumentStorageService.checkNotNullOrEmpty(language, "language");
		EDocumentStorageService.checkNotNullOrEmpty(queryText, "query text");
		EDocumentStorageService.checkNotNullOrEmpty(openNLPAnalysis, "open NLP analysis type");
		EDocumentStorageService.checkNotNullOrEmpty(openNLPModels, "open NLP models");
		EDocumentStorageService.checkNotNullOrEmpty(sesameStorageName, "sesame storage");
		EDocumentStorageService.checkNotNullOrEmpty(luceneIndexName, "lucene index name");
		EDocumentStorageService.checkNotNullOrEmpty(luceneFields, "lucene fields");
		EDocumentStorageService.checkNotNullOrEmpty(luceneAnalyzers, "lucene analyzers");

		Date d1 = new Date();

		try {
			String contentString = queryText;
			
			Model model = ModelFactory.createDefaultModel(); 
			NIFWriter.addInitialString(model, contentString, "http://dkt.dfki.de/query");
			
			//ResponseEntity<String> response1 = docStorageService.storeFileByString("testFile"+fileCounter+"_"+d1.toGMTString()+".txt", contentString, "http://jmschnei.dfki.de/test1/");

			String content2 = NIFReader.model2String(model);
			
        	System.out.println("INPUT FOR NLP"+content2);

        	ResponseEntity<String> response2 = openNLPService.analyzeText(content2, language, openNLPAnalysis, openNLPModels);
        	
        	String content3 = response2.getBody();
        	System.out.println("OUTPUT OF NLP"+content3);
        	
//        	ResponseEntity<String> response3 = sesameService.storeEntitiesFromString(sesameStorageName, content3, "NIF");
//        	
//        	String content4 = response3.getBody();
        	
        	System.out.println("INPUT FOR LUCENE"+content2);

        	ResponseEntity<String> response4 = luceneService.callLuceneExtraction("nif", content3, language, luceneIndexName, luceneFields, luceneAnalyzers, 30);
        	
        	String content5 = response4.getBody();

        	//Merge results from document retrieval and NIF??? HOW???

        	
        	
			Model outModel = ModelFactory.createDefaultModel();
			
			//TODO Convert output to be seen as NIF or as Website.
//			
//			
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", RDFSerialization.JSON.getMimeType());
//			
//			StringWriter writer = new StringWriter();
//			outModel.write(writer, "RDF/XML");
//			try {
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			String rdfString = writer.toString();
//			return new ResponseEntity<String>(rdfString, responseHeaders, HttpStatus.OK);
			return new ResponseEntity<String>(content5, responseHeaders, HttpStatus.OK);
		} catch (BadRequestException e) {
            throw e;
		}
	}
}
