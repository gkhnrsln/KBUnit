package prlab.kbunit.business.connector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import org.jdom2.JDOMException;
import prlab.kbunit.config.Configuration;
import prlab.kbunit.test.TestParameterInfo;
import prlab.kbunit.test.TestResultInfo;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** 
 * Klasse stellt Methoden zur Verfuegung, die Zugriffe auf den Server
 * ermoeglichen.
 * <br>
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */ 
public class WebServiceConnector {

	// Objekt mit Konfiguration aus WebserviceHome.xml
	private Configuration configuration;

	// Client - Objekt fuer den Web Service
	private Client client;

	// Pfad des Web-Services
	private String pathWebResource;


	/**
	 * Konstruktor fuer das Data Access Objekt
	 * @throws JDOMException
	 * @throws IOException
	 */
	public WebServiceConnector() throws JDOMException, IOException {
		this.configuration = new Configuration();
		this.client = Client.create();
		this.pathWebResource = configuration.getPathWebResource();
	}

	/**
	 * loescht uebergebende ID aus der DB
	 * @param id ID wird uebergeben
	 */
	public void deleteTestcase(int id) throws Exception{
		WebResource resource = client.resource(this.pathWebResource 
			+ "/deleteTestcase/" + id);
		Builder builder = resource.accept(MediaType.TEXT_PLAIN);
		String result = builder.get(String.class);
		if(!"".equalsIgnoreCase(result)){	
		     throw new Exception(result);
		}
	}

	/**
	 * Schreibt Testfall in die Datenbank
	 * @param tri TestResultInfo, die in die DB geschrieben wird
	 * @return von der DB zugeordnete ID
	 */
	public int writeTestResultInfo(TestResultInfo tri) throws Exception {
		int newId = 0;
		String resultmsg = "";
		try
		{
			ObjectMapper mapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String s = mapper.writeValueAsString(tri);
			WebResource resource = client.resource(this.pathWebResource 
				+ "/insertTestcase");
			ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, s);
			resultmsg = response.getEntity(String.class);
			newId = Integer.parseInt(resultmsg);
		} 
		catch(Exception exc)
		{
			exc.printStackTrace();
			if("".equalsIgnoreCase(resultmsg))
			{	
				throw new Exception(exc.getClass().getSimpleName() + ": " 
			        + exc.getMessage());
			}
			else
			{
				throw new Exception(resultmsg);
			}
		}
		return newId;
	}
	
	/**
	 * Liesst alle Testfaelle aus der DB
	 * @param sourceClassName JUnit Test-Source
	 * @throws Exception, wenn gelesene Testfaelle nicht aus dem JSON String 
	 *                    extrahiert werden koennen
	 */
	public ArrayList<TestResultInfo> readTestResultInfo(String sourceClassName) {
		ArrayList<TestResultInfo> tests = new ArrayList<TestResultInfo>();
		TestResultInfo[] testsRead = new TestResultInfo[0];
		WebResource resource = client.resource(pathWebResource + "/readTestcases/" 
		    + sourceClassName + ".");
		com.sun.jersey.api.client.WebResource.Builder builder 
		    = resource.accept("application/json");
		String s = builder.get(String.class);
		try{
			// Sort
			ObjectMapper mapper = new ObjectMapper()
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Class<?> clazz = Class.forName(sourceClassName);
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object parameterssort = constructor.newInstance();
			//Object parameterssort = Class.forName(sourceClassName).newInstance();
			Field[] fields = parameterssort.getClass().getDeclaredFields();
			List<String> parameternames = new ArrayList<>();
			for (Field field : fields) 
				parameternames.add(field.getName());
			if(!"".equals(s)){
				testsRead = mapper.readValue(s, TestResultInfo[].class);
				for(int i = 0; i < testsRead.length; i++){
					ArrayList<TestParameterInfo> newparameters 
					    = new ArrayList<TestParameterInfo>();
					//-------------------------SORT PARAMETERS ----------------------
					for(int j = 0;j<parameternames.size();j++) {
						for(int p = 0; p<testsRead[i].getParameters().size();p++) {
								if(parameternames.get(j).equals(testsRead[i]
									.getParameters().get(p).getName())) {
									newparameters.add(new TestParameterInfo(testsRead[i]
										.getParameters().get(p).getName(), testsRead[i]
										.getParameters().get(p).getValue()));
								}
						}
							
					}
					testsRead[i].setParameters(newparameters);
					tests.add(testsRead[i]);

					Method[] methods = clazz.getDeclaredMethods();
					ArrayList<String> argumentTypes = new ArrayList<>();
					for(Method m : methods) {
						if (m.getName().equals(testsRead[i].getIdentifierName().substring(
								testsRead[i].getIdentifierName().lastIndexOf('.') + 1))) {
							Class<?>[] methodArgumentsTypes = m.getParameterTypes();
							for (Class<?> paramClazz : methodArgumentsTypes) {
								argumentTypes.add(paramClazz.getTypeName());
							}
						}
					}
					testsRead[i].setArgumentsTypes(argumentTypes);

				}
			}    
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Fehler beim Lesen der Testfälle!");
			System.err.println(e);
		}
		return tests;
	}

}
