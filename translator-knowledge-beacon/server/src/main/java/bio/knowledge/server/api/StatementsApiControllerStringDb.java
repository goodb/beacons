package bio.knowledge.server.api;

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
import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-05-05T11:45:59.374-07:00")

@Controller
public class StatementsApiControllerStringDb implements StatementsApi {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	String getStringConceptName(String conceptId){
		String name = conceptId;
		//Its too slow... 		
		//		String concept_req = "http://string-db.org/api/tsv-no-header/resolve?identifier="+conceptId+"&species=9606";
		String concept_req = "http://mygene.info/v3/query?q="+conceptId;
		//System.out.println(concept_req);
		try {
			String concept_response = run(concept_req);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode readTree = mapper.readTree(concept_response);
			for (JsonNode node : readTree.findPath("hits")) {
				//			    System.out.println(node.findPath("name").asText());
				//			    System.out.println(node.findPath("symbol").asText());
				name = node.findPath("symbol").asText();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}


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
		List<InlineResponse2003> elements = new ArrayList<InlineResponse2003>();
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

			if(conceptId.startsWith("ensembl:")){
				conceptId = "9606."+conceptId.substring(8); 
			}

			if(conceptId.startsWith("ENS")){
				conceptId = "9606."+conceptId;
			}	
			//check to see if it looks like a valid response.
			if(conceptId.startsWith("9606")){
				String string_req = "http://string-db.org/api/tsv-no-header/interactors?identifier="+conceptId; //9606.ENSP00000367407
				System.out.println(string_req);
				try {
					String string_response = run(string_req);
					if(string_response!=null){
						BufferedReader rdr = new BufferedReader(new StringReader(string_response));				
						for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
							InlineResponse2003 element = new InlineResponse2003();
							StatementsSubject subject = new StatementsSubject();
							String subject_id = input_id;
							String subject_name = getStringConceptName(conceptId.substring(5));
							subject.setId(subject_id);
							subject.setName(subject_name);
							element.setSubject(subject);
							StatementsObject object = new StatementsObject();
							//9606.ENSP00000367407
							String object_ensembl = line.substring(5);
							String curie = "ensembl:"+object_ensembl;						
							String object_id = curie;
							String object_name = getStringConceptName(object_ensembl);
							object.setId(object_id);
							object.setName(object_name);
							element.setObject(object);

							StatementsPredicate predicate = new StatementsPredicate();
							String predicate_id = "http://purl.obolibrary.org/obo/RO_0002434";
							predicate.setId(predicate_id);
							String predicate_name = "interacts with";
							predicate.setName(predicate_name);
							element.setPredicate(predicate);
							element.setId(subject_id+"|"+predicate_id+"|"+object_id);//scheme for statement identifier ?
							elements.add(element);			
						}
						rdr.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		return ResponseEntity.ok(elements);
		// return new ResponseEntity<List<InlineResponse2003>>(HttpStatus.OK);
	}

}
