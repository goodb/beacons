/**
 * 
 */

/**
 * @author bgood
 *
 */

import bio.knowledge.client.*;
import bio.knowledge.client.auth.*;
import bio.knowledge.client.model.*;
import bio.knowledge.client.api.ConceptsApi;
import bio.knowledge.client.api.EvidenceApi;
import bio.knowledge.client.api.ExactmatchesApi;
import bio.knowledge.client.api.StatementsApi;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestABeacon {

	public static void main(String[] args) {

		//concepts API
		ConceptsApi apiInstance = new ConceptsApi();
		//String basePath = "http://beacon.medgeninformatics.net/api";
		String basePath = "http://localhost:8090/api";
		//String basePath = "http://52.14.14.106:8090/api/";
		
		//String basePath = "http://garbanzo.sulab.org";
			apiInstance.getApiClient().setBasePath(basePath);		
		apiInstance.getApiClient().setConnectTimeout(1000000);
		apiInstance.getApiClient().getHttpClient().setReadTimeout(0, TimeUnit.SECONDS);
		String conceptId = "OMIM:605543";//"NCBIGene:6622";//"OMIM:605543";//"ensembl:ENSP00000367407"; // String | (url-encoded) CURIE identifier of concept of interest
		String keyword = "breast cancer";//"NMT2";//"NMT2";
				try {
					conceptId = URLEncoder.encode(conceptId, "UTF-8");
					keyword = URLEncoder.encode(keyword, "UTF-8");
//					List<InlineResponse2001> result1 = apiInstance.getConceptDetails(conceptId);
//					System.out.println("concept details:"+ result1);
					List<InlineResponse200> result2 = apiInstance.getConcepts(keyword, "", 0, 10);
					System.out.println("concepts "+result2);
//					ExactmatchesApi m = new ExactmatchesApi();
//					List<String> matches = m.getExactMatchesToConcept(conceptId);
//					System.out.println(matches);
					
				} catch (ApiException e) {
					System.err.println("Exception when calling ConceptsApi#getConceptDetails");
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		//statements API
//				StatementsApi statementsApi = new StatementsApi();
//				statementsApi.getApiClient().setBasePath(basePath);
//				List<String> c = new ArrayList<String>(); 
//				c.add(conceptId);
//				try {
//					List<InlineResponse2003> result3 = statementsApi.getStatements(c, 0, 10, "", "");
//					System.out.println(result3);
//				} catch (ApiException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

		//evidence API
		//		String sid = "kbs:Q594013_P361_Q3201466";
//		String sid = "0f03224e-7385-49bc-b301-0da1d5fb5e0d";//"ensembl:ENSP00000367407|http://purl.obolibrary.org/obo/RO_0002434|ensembl:ENSP00000309576";
//		try {
//			sid = URLEncoder.encode(sid, "UTF-8");
//			EvidenceApi evidenceApi = new EvidenceApi();
//			evidenceApi.getApiClient().setBasePath(basePath);
//
//			List<InlineResponse2002> result4 = evidenceApi.getEvidence(sid, "", 0, 10);
//			System.out.println(result4);
//		} catch (ApiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}

