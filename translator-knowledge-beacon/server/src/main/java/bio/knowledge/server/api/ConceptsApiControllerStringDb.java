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
public class ConceptsApiControllerStringDb implements ConceptsApi {

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
		String input_id = conceptId;

		if(conceptId.startsWith("ensembl:")){
			conceptId = "9606."+conceptId.substring(8); 
		}

		if(conceptId.startsWith("ENS")){
			conceptId = "9606."+conceptId;
		}

		List<InlineResponse2001> responses = new ArrayList<InlineResponse2001>();
		//check to see if it looks like a valid request.
		if(conceptId.startsWith("9606")){
			//conceptDetails/
			//http://string-db.org/api/tsv/resolve?identifier=9606.ENSP00000367407&species=9606
			String string_req = "http://string-db.org/api/tsv-no-header/resolve?identifier="+conceptId+"&species=9606";
			System.out.println(string_req);
			try {
				String string_response = run(string_req);
				if(string_response!=null){
					BufferedReader rdr = new BufferedReader(new StringReader(string_response));

					for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
						InlineResponse2001 element = new InlineResponse2001();
						String[] items = line.split("	");
						element.setId(input_id); 
						element.setName(items[3]);
						element.setDefinition("human protein. "+items[4]);
						element.setSemanticGroup("GENE");
						responses.add(element);				
					}
					rdr.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// do some magic!
		return ResponseEntity.ok(responses);
		//return new ResponseEntity<List<InlineResponse2001>>(HttpStatus.OK);
	}

	public ResponseEntity<List<InlineResponse200>> getConcepts(@ApiParam(value = "a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms", required = true) @RequestParam(value = "keywords", required = true) String keywords



			,
			@ApiParam(value = "a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ") @RequestParam(value = "semgroups", required = false) String semgroups



			,
			@ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber



			,
			@ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize



			) {
		// do some magic!
		//wrapping String API
		//call http://string-db.org/api/tsv/resolve?identifier=NMT2&species=9606
		//returns 
		//stringId	ncbiTaxonId	taxonName	preferredName	annotation
		//9606.ENSP00000367407	9606	Homo sapiens	NMT2	N-myristoyltransferase 2; Adds a myristoyl group to the N-terminal glycine residue of certain cellular proteins (By similarity)

		String string_req = "http://string-db.org/api/tsv-no-header/resolve?identifier="+keywords+"&species=9606";
		//ResponseEntity<List<InlineResponse200>> response = new ResponseEntity<List<InlineResponse200>>(HttpStatus.OK);
		List<InlineResponse200> elements = new ArrayList<InlineResponse200>();
		try {
			String string_response = run(string_req);
			if(string_response!=null&&string_response.startsWith("9606")){
				BufferedReader rdr = new BufferedReader(new StringReader(string_response));
				for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
					InlineResponse200 element = new InlineResponse200();
					String[] items = line.split("	");
					String curie = "ensembl:"+items[0].substring(5);
					element.setId(curie); //map to CURIE ?
					element.setName(items[3]);
					element.setDefinition("human protein. "+items[4]);
					element.setSemanticGroup("GENE");
					elements.add(element);				
				}
				rdr.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return response;
		return ResponseEntity.ok(elements);
	}

}
