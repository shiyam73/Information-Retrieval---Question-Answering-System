package com.ir.qa.parser.templateParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MediaWikiTemplateParser implements TemplateParser {
	
	private static Pattern flagPattern = Pattern.compile("[\\{]{2}flag[^\\}]+[\\}]{2}|[\\{]{2}[a-zA-Z]{1,4}[\\}]{2}");

	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		
		try {
			Matcher m = flagPattern.matcher(value);
			if(m.find()){
				if(value.contains("flagicon|")){
					value.replace("flagicon|", "flag|");
				}
			}
			String urlParameters = "action=expandtemplates&text=" + value
					+ "&format=xml";
			String request = "http://en.wikipedia.org/w/api.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// process response - need to get xml response back.
			InputStream stream = connection.getInputStream();

			InputStreamReader isReader = new InputStreamReader(stream);

			// put output stream into a string
			BufferedReader br = new BufferedReader(isReader);
			// put output stream into a string

			String result = "";
			String line;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				result+= line;
			}

			br.close();
			connection.disconnect();

			Pattern p1 = Pattern.compile("[\\[]{2}[a-zA-Z\\-\\:\\| ]+[\\]]{2}");
			Matcher m1 = p1.matcher(result);
			int inside=0;
			while (m1.find()) {
				String split[];
				String flag = m1.group().toString();
				if(!flag.contains("Category") && !flag.contains("Template"))
				{
				split = flag.split("\\|");
				
				result = split[1].replaceAll("[\\]]","");
				}
				else
				{
					result = flag.replaceAll("[\\]]|[\\[]","");
					result = result.replaceAll("Template","");
					result = result.replaceAll(":","");
				}
				inside=1;

			}
			if(result.contains("[[File:") && inside == 0 )
			{
				int index=0;
				String spli[] = result.split("\\|");
				while(index < spli.length)
				{
					if(spli[index].contains("link="))
					{
						String temp[] = spli[index].split("\\=");
						result = temp[1].replaceAll("\\][^\\>]*\\>\\<\\/api\\>","");
					}
					index++;
				}
			}
			//System.out.println("Flag::"+result);
			return result;

			// System.out.println(result);

		} catch (Exception e) {
			//System.out.println("Mediawiki Value::"+value);
			e.printStackTrace();
		}
		return null;
	}
}
