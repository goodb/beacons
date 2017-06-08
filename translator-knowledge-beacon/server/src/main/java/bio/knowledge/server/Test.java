package bio.knowledge.server;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class Test {
	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

//	public static void main(String[] args) throws IOException {
//		Test t = new Test();
//		String input_id = "OMIM:605543";
//		String monarch_req = "http://api.monarchinitiative.org/api/bioentity/disease/"+input_id+"/genes/?rows=20&fetch_objects=true";
//		System.out.println(monarch_req);
//
//		String monarch_response = t.run(monarch_req);
//		System.out.println(monarch_response);
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode readTree = mapper.readTree(monarch_response);
//			String object_id = ""; String object_label = "";
//			String subject_id = ""; String subject_label = "";
//			String statement_id = "";
//			String relation = "associated_with";
//			for (JsonNode node : readTree.get("associations")) { //associations 
//				statement_id = node.get("id").asText();
//				subject_id = node.findPath("subject").get("id").asText();
//				subject_label = node.findPath("subject").get("label").asText();
//				object_id = node.findPath("object").get("id").asText();
//				object_label = node.findPath("object").get("label").asText();
//				System.out.println(statement_id+"\n"+subject_id+" "+subject_label+" "+relation+" "+object_id+" "+object_label);
//				//			    System.out.println(node.findPath("symbol").asText());
//				//name = node.findPath("symbol").asText();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

}
