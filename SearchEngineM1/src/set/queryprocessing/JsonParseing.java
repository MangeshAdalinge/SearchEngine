package set.queryprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonParseing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSONParser parser=new JSONParser();
		try {
			JSONObject obj= (JSONObject) parser.parse(new FileReader("C:/Users/mange/Desktop/all-nps-sites.json"));
			JSONArray ary=(JSONArray) obj.get("documents") ;
		//	System.out.println(ary);
			int i=1;
			for (Object object : ary) {
		     //   JSONObject aJson = (JSONObject) object;
//System.out.println(object);
		     /*   String body = (String) aJson.get("body");
		        String url = (String) aJson.get("url");
		        String title = (String) aJson.get("title");*/
		        File file = new File("C:/Users/mange/Desktop/Files 36K/article"+i+".json");

				
				file.createNewFile();
				

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				
				fw.write(object.toString());
				fw.flush();
			    fw.close();

		        i++;

		        

		    }
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
