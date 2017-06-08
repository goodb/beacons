package bio.knowledge.server.api;

import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse200;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-05-05T11:45:59.374-07:00")

@Controller
public class ConceptsApiController implements ConceptsApi {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(
			@ApiParam(value = "(url-encoded) CURIE identifier of concept of interest",required=true ) @PathVariable("conceptId") String conceptId


			) {
		//handle a curie patterned input
		//ensembl:ENSP00000367407
		try {
			conceptId = URLDecoder.decode(conceptId,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<InlineResponse2001> conceptDetails = new ArrayList<InlineResponse2001>();
		ObjectMapper mapper = new ObjectMapper();
		String input_id = conceptId;
		String concept_details = "https://scigraph-data.monarchinitiative.org/scigraph/vocabulary/id/"+input_id;
		String details_response;
		try {
			details_response = run(concept_details);
			System.out.println("details \n"+concept_details+"\n"+details_response);
			InlineResponse2001 conceptD = new InlineResponse2001();
			JsonNode readDetails = mapper.readTree(details_response);
			String iri = readDetails.get("iri").asText();
			String curie = readDetails.get("curie").asText();
			conceptD.setId(curie);

			String type ="";
			for(JsonNode catnode : readDetails.get("categories")) {
				type = catnode.asText();
				break;
			}
			if(type.equals("disease")){
				type = "DISO";
			}else if(type.equals("sequence feature")){
				type = "GENE";
			}
			conceptD.setSemanticGroup(type);
			String label1 =  ""; 
			for (JsonNode labelnode : readDetails.get("labels")) {
				String label = labelnode.asText();
				conceptD.addSynonymsItem(label);
				if(label1.equals("")){
					label1 = label;
					conceptD.setName(label);
				}
			}
			for (JsonNode synnode : readDetails.get("synonyms")) {
				String synonym = synnode.asText();
				conceptD.addSynonymsItem(synonym);
			}
			String definition = "";
			for (JsonNode defnode : readDetails.get("definitions")) {
				definition = defnode.asText();
				conceptD.setDefinition(definition);
				break;
			}
			conceptDetails.add(conceptD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok(conceptDetails);
	}

	public ResponseEntity<List<InlineResponse200>> getConcepts(@ApiParam(value = "a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms", required = true) @RequestParam(value = "keywords", required = true) String keywords



			,
			@ApiParam(value = "a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ") @RequestParam(value = "semgroups", required = false) String semgroups



			,
			@ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber



			,
			@ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		List<InlineResponse200> concepts = new ArrayList<InlineResponse200>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			keywords = URLDecoder.decode(keywords,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//concept query service begin
		String concept_query = "https://scigraph-data.monarchinitiative.org/scigraph/refine/reconcile?query="+keywords;
		String concept_response;
		try {
			concept_response = run(concept_query);
			System.out.println(concept_response);
			JsonNode readTree = mapper.readTree(concept_response);

			for (JsonNode node : readTree.get("result")) {
				String concept_uri_id = node.get("id").asText();
				concept_uri_id = URLEncoder.encode(concept_uri_id,"UTF-8");
				//concept_id comes back as text
				String concept_details = "https://scigraph-data.monarchinitiative.org/scigraph/vocabulary/id/"+concept_uri_id;
				String details_response = run(concept_details);
				JsonNode readDetails = mapper.readTree(details_response);
				String concept_id = readDetails.get("curie").asText(); //curie now
				
				String name = node.get("name").asText();
				int score = node.get("score").asInt();
				boolean match = node.get("match").asBoolean();
				JsonNode types = node.get("type");
				String type = "";
				if(types.elements().hasNext()){
					type = types.elements().next().asText();
				}
				System.out.println(concept_id+" "+name+" "+type+" "+score+" "+match);
				InlineResponse200 element = new InlineResponse200();
				element.setId(concept_id);
				element.setName(name);
				if(type.equals("disease")){
					type = "DISO";
				}else if(type.equals("sequence feature")){
					type = "GENE";
				}
				element.setSemanticGroup(type);
				concepts.add(element);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok(concepts);
	}

}
