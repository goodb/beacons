
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import bio.knowledge.client.model.InlineResponse2003;
import bio.knowledge.client.model.StatementsObject;
import bio.knowledge.client.model.StatementsPredicate;
import bio.knowledge.client.model.StatementsSubject;
import bio.knowledge.client.model.InlineResponse200;
import bio.knowledge.client.model.InlineResponse2001;

/**
 * @author bgood
 *
 */
public class TestProxyClients {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		TestProxyClients example = new TestProxyClients();
		//example.testStringdb();
		//example.testBioLink();
		//example.testSciGraphNeighbors();
		example.testSciGraphMatches();
	}

	private void testSciGraphMatches() throws IOException {
		String curie = "OMIM%3A605543";
		String matches_q = "https://scigraph-data.monarchinitiative.org/scigraph/graph/neighbors/"+
	curie+
	"?depth=1&blankNodes=false&relationshipType=equivalentClass&direction=BOTH&entail=false&project=*";
		Set<String> matches = new HashSet<String>();
		String neighbors = run(matches_q);
		System.out.println("neighbors\n"+matches_q+" \n "+neighbors);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode readNeighbors = mapper.readTree(neighbors);
		for (JsonNode node : readNeighbors.get("edges")) {
			String relation_id = node.get("pred").asText();
			String object_id = node.get("obj").asText();
			String subject_id = node.get("sub").asText(); 
			matches.add(object_id); matches.add(subject_id);					
		}
		for(String match : matches){
			System.out.println(match);
		}
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
	
	public void testSciGraphNeighbors() throws IOException{
		String query = "Parkinson's Disease";
		String input_id = "OMIM:605543";
		ObjectMapper mapper = new ObjectMapper();
		
		//concept query service begin
		String concept_query = "https://scigraph-data.monarchinitiative.org/scigraph/refine/reconcile?query="+query;
		String concept_response = run(concept_query);
		System.out.println(concept_response);
		JsonNode readTree = mapper.readTree(concept_response);
		List<InlineResponse200> concepts = new ArrayList<InlineResponse200>();
		for (JsonNode node : readTree.get("result")) {
			String concept_id = node.get("id").asText();
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
		
		//concept details service begin
		String concept_details = "https://scigraph-data.monarchinitiative.org/scigraph/vocabulary/id/"+input_id;
		String details_response = run(concept_details);
		System.out.println("details \n"+concept_details+"\n"+details_response);
		List<InlineResponse2001> conceptDetails = new ArrayList<InlineResponse2001>();
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
		for (JsonNode labelnode : readDetails.get("labels")) {
			String label = labelnode.asText();
			conceptD.addSynonymsItem(label);
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
		
		//statements service begin
		Set<InlineResponse2003> elements = new HashSet<InlineResponse2003>();
		String neighbor_query = "https://scigraph-data.monarchinitiative.org/scigraph/graph/neighbors/OMIM%3A605543?depth=1&blankNodes=false&direction=BOTH&entail=false&project=*";
		String neighbors = run(neighbor_query);
		System.out.println("neighbors\n"+neighbor_query+" \n "+neighbors);
		JsonNode readNeighbors = mapper.readTree(neighbors);
		for (JsonNode node : readNeighbors.get("edges")) {
			String relation_id = node.get("pred").asText();
			String object_id = node.get("obj").asText();
			String subject_id = node.get("sub").asText(); 
								
			if(relation_id.startsWith("OBAN")||subject_id.startsWith("MONARCH")||
					relation_id.equals("IAO:0000142")||relation_id.equals("isDefinedBy")
					||relation_id.equals("OIO:hasDbXref")){
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
		//now add the biolink stuff
		Set<InlineResponse2003> biolinks = testBioLink();
		elements.addAll(biolinks);
		System.out.println("All together now!");
		for(InlineResponse2003 biolink : elements){
			System.out.println(biolink.getSubject().getName()+" "+biolink.getPredicate().getName()+" "+biolink.getObject().getName());
		}
	}
	
	public Set<InlineResponse2003> testBioLink(){
		Set<InlineResponse2003> elements = new HashSet<InlineResponse2003>();
		String input_id = "OMIM:605543";
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
					//parse the data
					subject_id = node.findPath("subject").get("id").asText();
					subject_label = node.findPath("subject").get("label").asText();
					object_id = node.findPath("object").get("id").asText();
					object_label = node.findPath("object").get("label").asText();
					System.out.println(statement_id+"\n"+subject_id+" "+subject_label+" "+relation+" "+object_id+" "+object_label);
					//set up response object
					statement_id = node.get("id").asText();
					InlineResponse2003 element = new InlineResponse2003();
					element.setId(statement_id);
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
		return elements;
	}
	
	
	public void testStringdb() throws IOException{
		String response = run("http://string-db.org/api/tsv-no-header/interactors?identifier=9606.ENSP00000367407");
		System.out.println(response);
		BufferedReader rdr = new BufferedReader(new StringReader(response));
		List<String> lines = new ArrayList<String>();
		for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
		    lines.add(line);
		}
		rdr.close();
		System.out.println(lines);
	}
	
	/*
	 String
concepts/

http://string-db.org/api/tsv/resolve?identifier=NMT2&species=9606

stringId	ncbiTaxonId	taxonName	preferredName	annotation
9606.ENSP00000367407	9606	Homo sapiens	NMT2	N-myristoyltransferase 2; Adds a myristoyl group to the N-terminal glycine residue of certain cellular proteins (By similarity)
... (list)

conceptDetails/
http://string-db.org/api/tsv/resolve?identifier=9606.ENSP00000367407&species=9606

statements/
http://string-db.org/api/tsv/interactors?identifier=9606.ENSP00000367407

evidence/
"String said so"

exactmatches/
[]
	 */
	
	
	
}
