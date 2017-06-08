package bio.knowledge.server.api;

import bio.knowledge.server.model.InlineResponse2002;

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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-05-05T11:45:59.374-07:00")

@Controller
public class EvidenceApiControllerStringDb implements EvidenceApi {

    public ResponseEntity<List<InlineResponse2002>> getEvidence(
@ApiParam(value = "(url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought ",required=true ) @PathVariable("statementId") String statementId


,
        @ApiParam(value = "(url-encoded, space delimited) keyword filter to apply against the label field of the annotation ") @RequestParam(value = "keywords", required = false) String keywords



,
        @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber



,
        @ApiParam(value = "number of cited references per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize



) {
        // do some magic!
    	// ids look like ensembl:ENSP00000367407|http://purl.obolibrary.org/obo/RO_0002434|ensembl:ENSP00000220913 
    	try {
			statementId = URLDecoder.decode(statementId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(statementId);
    	String[] triple = statementId.split("\\|");
    	String ens = triple[0].substring(8);
    	List<InlineResponse2002> elements = new ArrayList<InlineResponse2002>();
    	InlineResponse2002 element = new InlineResponse2002();
    	element.setDate("2017-05-10");
    	element.setId("http://string-db.org/newstring_cgi/show_network_section.pl?targetmode=proteins&caller_identity=Translator&network_flavor=evidence&identifier="+ens);
    	element.setLabel("string-db.org said so! (about "+ens+" )");
    	elements.add(element);
    	return ResponseEntity.ok(elements);
    	//return new ResponseEntity<List<InlineResponse2002>>(HttpStatus.OK);
    }

}
