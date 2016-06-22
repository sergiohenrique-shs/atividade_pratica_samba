package br.com.shs.atividadepratica.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import br.com.shs.atividadepratica.dto.ZencoderInput;

public class ZenCoderService {

	public String converter(ZencoderInput input){
		StringBuilder sbZenInput = new StringBuilder();
		InputStream is = getClass().getClassLoader().getResourceAsStream("template_html5_profiles_json_zencoder.tmpl");
		
		try {
			byte[] buffer = new byte[1024];
			
			int qtd = 0;
			
			while((qtd = is.read(buffer)) > 0){
				sbZenInput.append(new String(buffer, 0, qtd));
			}
			
			URL urlInput = new URL("https://app.zencoder.com/api/v2/jobs");
			HttpsURLConnection conn = (HttpsURLConnection)urlInput.openConnection();
			
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host", "app.zencoder.com");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Zencoder-Api-Key", "5bc1f5db0148e2171ab59f916a131308");
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			String jSonTemplate = sbZenInput.toString();
			
			jSonTemplate = jSonTemplate.replace("##BUCKET_NAME##", input.getBucketName());
			jSonTemplate = jSonTemplate.replace("##FILE_INPUT_NAME##", input.getFileName().replaceFirst("[.][^.]+$", ""));
			jSonTemplate = jSonTemplate.replace("##FILE_INPUT_EXTENSION##", input.getFileName().replaceFirst("^.+[.]([^.]+)$", "$1"));
			
			// Send post request
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(jSonTemplate);
			wr.flush();
			wr.close();
			
			int responseCode = conn.getResponseCode();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			return response.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {is.close();} catch (IOException e) {}
		}
		
		return "termino_inesperado";
	}
}
