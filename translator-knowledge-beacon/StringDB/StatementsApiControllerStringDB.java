package bio.knowledge.server.api;

import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.StatementsObject;
import bio.knowledge.server.model.StatementsPredicate;
import bio.knowledge.server.model.StatementsSubject;
import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-05-05T11:45:59.374-07:00")

@Controller
public class StatementsApiController implements StatementsApi {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
		client.setReadTimeout(30, TimeUnit.SECONDS);  
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	//	String getStringConceptName(String conceptId){
	//		String name = conceptId;
	//		//Its too slow... 		
	//		//		String concept_req = "http://string-db.org/api/tsv-no-header/resolve?identifier="+conceptId+"&species=9606";
	//		String concept_req = "http://mygene.info/v3/query?q="+conceptId;
	//		//System.out.println(concept_req);
	//		try {
	//			String concept_response = run(concept_req);
	//			ObjectMapper mapper = new ObjectMapper();
	//			JsonNode readTree = mapper.readTree(concept_response);
	//			for (JsonNode node : readTree.findPath("hits")) {
	//				//			    System.out.println(node.findPath("name").asText());
	//				//			    System.out.println(node.findPath("symbol").asText());
	//				name = node.findPath("symbol").asText();
	//			}
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		return name;
	//	}


	public ResponseEntity<List<InlineResponse2003>> getStatements(@ApiParam(value = "set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exactly matching concepts to be used in a search for associated concept-relation statements ", required = true) @RequestParam(value = "c", required = true) List<String> c



			,
			@ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber



			,
			@ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize



			,
			@ApiParam(value = "a (url-encoded, space-delimited) string of keywords or substrings against which to match the subject, predicate or object names of the set of concept-relations matched by any of the input exact matching concepts ") @RequestParam(value = "keywords", required = false) String keywords



			,
			@ApiParam(value = "a (url-encoded, space-delimited) string of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain the subject or object concepts associated with the query seed concept (see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ") @RequestParam(value = "semgroups", required = false) String semgroups



			) {
		Set<InlineResponse2003> elements = new HashSet<InlineResponse2003>();
		// do some magic!
		for(String conceptId : c){


			//handle a curie patterned input
			//ensembl:ENSP00000367407
			try {
				conceptId = URLDecoder.decode(conceptId,"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String input_id = conceptId;
			//https://api.monarchinitiative.org/api/bioentity/disease/OMIM%3A605543/genes/?rows=20&fetch_objects=true
			// OMIM:605543

			//check to see if it looks like a valid request.
			String monarch_req1 = "http://api.monarchinitiative.org/api/bioentity/disease/"+input_id+"/genes/?rows=20&fetch_objects=true";
			System.out.println(monarch_req1);
			String monarch_req2 = "https://api.monarchinitiative.org/api/bioentity/gene/"+input_id+"/diseases/?rows=20&fetch_objects=true";
			String monarch_req ="";
			for(int i=1;i<3;i++){
				if(i==1){
					monarch_req = monarch_req1;
				}else{
					monarch_req = monarch_req2;
				}
				try {
					String monarch_response = run(monarch_req);
					System.out.println(monarch_response);

					ObjectMapper mapper = new ObjectMapper();
					JsonNode readTree = mapper.readTree(monarch_response);
					String object_id = ""; String object_label = "";
					String subject_id = ""; String subject_label = "";
					String statement_id = "";
					String relation = "associated_with";
					for (JsonNode node : readTree.get("associations")) { //associations 
						statement_id = node.get("id").asText();
						InlineResponse2003 element = new InlineResponse2003();
						element.setId(statement_id);
						//parse the data
						subject_id = node.findPath("subject").get("id").asText();
						subject_label = node.findPath("subject").get("label").asText();
						object_id = node.findPath("object").get("id").asText();
						object_label = node.findPath("object").get("label").asText();
						System.out.println(statement_id+"\n"+subject_id+" "+subject_label+" "+relation+" "+object_id+" "+object_label);
						//set up response object
						//subject
						StatementsSubject subject = new StatementsSubject();
						subject.setId(subject_id);
						subject.setName(subject_label);
						element.setSubject(subject);
						//object
						StatementsObject object = new StatementsObject();
						object.setId(object_id);
						object.setName(object_label);
						element.setObject(object);
						//predicate
						StatementsPredicate predicate = new StatementsPredicate();
						String predicate_id = "http://www.w3.org/2002/07/owl#topObjectProperty";
						predicate.setId(predicate_id);
						String predicate_name = "associated with";
						predicate.setName(predicate_name);
						element.setPredicate(predicate);
						elements.add(element);					
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				elements.addAll(getSciGraphNeighbors(input_id));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok(new ArrayList<InlineResponse2003>(elements));
	}
	
	public Set<InlineResponse2003> getSciGraphNeighbors(String curie) throws IOException{		
		//statements service begin
		ObjectMapper mapper = new ObjectMapper();
		Set<InlineResponse2003> elements = new HashSet<InlineResponse2003>();
		String neighbor_query = "https://scigraph-data.monarchinitiative.org/scigraph/graph/neighbors/"+curie+"?depth=1&blankNodes=false&direction=BOTH&entail=false&project=*";
		String neighbors = run(neighbor_query);
		System.out.println("neighbors\n"+neighbor_query+" \n "+neighbors);
		JsonNode readNeighbors = mapper.readTree(neighbors);
		for (JsonNode node : readNeighbors.get("edges")) {
			String relation_id = node.get("pred").asText();
			String object_id = node.get("obj").asText();
			String subject_id = node.get("sub").asText(); 
								
			if(relation_id.startsWith("OBAN")||subject_id.startsWith("MONARCH")||
					relation_id.equals("IAO:0000142")||relation_id.equals("isDefinedBy")
					||relation_id.equals("OIO:hasDbXref")||relation_id.equals("IAO:0000136")
					||relation_id.equals("RO:0002434")){
				continue; //skip these 
			}
			String object_label = getConceptLabel(object_id);
			String subject_label = getConceptLabel(subject_id);	
			
			String statement_id = subject_id+"|"+relation_id+"|"+object_id;
			InlineResponse2003 rel = new InlineResponse2003();
			rel.setId(statement_id);
			StatementsSubject subject = new StatementsSubject();
			subject.setId(subject_id);
			subject.setName(subject_label);
			rel.setSubject(subject);
			//object
			StatementsObject object = new StatementsObject();
			object.setId(object_id);
			object.setName(object_label);
			rel.setObject(object);
			//predicate
			StatementsPredicate predicate = new StatementsPredicate();
			JsonNode meta = node.get("meta");
			for(JsonNode r_label : meta.get("lbl")){
				String relation_label = r_label.asText();
				predicate.setName(relation_label);
				break;
			}
			predicate.setId(relation_id);			
			rel.setPredicate(predicate);
			
			System.out.println(predicate.getName()+" "+subject_label+" "+object_label+" "+statement_id);
			elements.add(rel);		
		}
		return elements;
	}
	
	public String getConceptLabel(String curie) throws IOException{
		String label = "";
		String concept_details = "https://scigraph-data.monarchinitiative.org/scigraph/vocabulary/id/"+curie;
		String details_response = run(concept_details);
		if(details_response.startsWith("<")){
			return "";
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode readDetails = mapper.readTree(details_response);
		
		for (JsonNode labelnode : readDetails.get("labels")) {
			label = labelnode.asText();
			break;
		}
		return label;
	}

}
