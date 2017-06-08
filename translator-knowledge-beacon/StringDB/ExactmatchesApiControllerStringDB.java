package bio.knowledge.server.api;


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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-05-05T11:45:59.374-07:00")

@Controller
public class ExactmatchesApiController implements ExactmatchesApi {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public ResponseEntity<List<String>> getExactMatchesToConcept(
			@ApiParam(value = "(url-encoded) CURIE identifier of the concept to be matched",required=true ) @PathVariable("conceptId") String conceptId) {
		List<String> matches = new ArrayList<String>();
		try {
			conceptId = URLDecoder.decode(conceptId,"UTF-8");
			matches.addAll(getMatches(conceptId));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok(matches);
	}


	public ResponseEntity<List<String>> getExactMatchesToConceptList(@ApiParam(value = "set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). ", required = true) @RequestParam(value = "c", required = true) List<String> c) {
		List<String> matches = new ArrayList<String>();
		for(String conceptId : c){
			try {
				conceptId = URLDecoder.decode(conceptId,"UTF-8");
				matches.addAll(getMatches(conceptId));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//return new ResponseEntity<List<String>>(HttpStatus.OK);
		return ResponseEntity.ok(matches);
	}

	private List<String> getMatches(String curie) throws IOException{
		curie = curie.replace("ensembl","ENSEMBL");//for string-db..
		String matches_q = "https://scigraph-data.monarchinitiative.org/scigraph/graph/neighbors/"+
				curie+
				"?depth=1&blankNodes=false&relationshipType=equivalentClass&direction=BOTH&entail=false&project=*";
		Set<String> matches = new HashSet<String>();
		String neighbors = run(matches_q);
		System.out.println("neighbors\n"+matches_q+" \n "+neighbors);
		if(neighbors.startsWith("{")){
			ObjectMapper mapper = new ObjectMapper();
			JsonNode readNeighbors = mapper.readTree(neighbors);
			for (JsonNode node : readNeighbors.get("edges")) {
				//String relation_id = node.get("pred").asText();
				String object_id = node.get("obj").asText();
				String subject_id = node.get("sub").asText(); 
				matches.add(object_id); matches.add(subject_id);					
			}
		}
		return new ArrayList<String>(matches);
	}

}
