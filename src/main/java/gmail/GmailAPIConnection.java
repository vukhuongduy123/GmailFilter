package gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;


public class GmailAPIConnection {
	private static final String APPLICATION_NAME = "GmailFilter";
	private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final File CREDENTIALS_FILE = new File(System.getProperty("user.dir") +
														  "\\src\\main\\resources\\credentials" +
														  ".json");
	private static final String OATH_URL = "https://accounts.google.com/o/oauth2/token";
	private static final String REFRESH_TOKEN = "1//0eUfJTbTbrnnICgYIARAAGA4SNwF-L9IrB8hmC4YHkK_ZKXYJXbcF6783siISFa0CQDi9LY5icZcX0xuPVWbwXiGPzBC410qZ9Hg";

	private GmailAPIConnection() {
	}


	@CheckReturnValue
	public static Gmail getGmailService() throws
										  IOException,
										  GeneralSecurityException {
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
																	 new InputStreamReader(
																			 new FileInputStream(
																					 CREDENTIALS_FILE)));

		Map<String, Object> accessParams = new LinkedHashMap<>();
		accessParams.put("grant_type", "refresh_token");
		accessParams.put("client_id", clientSecrets.getDetails().getClientId());
		accessParams.put("client_secret", clientSecrets.getDetails().getClientSecret());
		accessParams.put("refresh_token", REFRESH_TOKEN);

		Credential authorize = new GoogleCredential.Builder().setTransport(
				GoogleNetHttpTransport.newTrustedTransport())
															 .setJsonFactory(JSON_FACTORY)
															 .setClientSecrets(
																	 clientSecrets.getDetails()
																				  .getClientId(),
																	 clientSecrets.getDetails()
																				  .getClientSecret())
															 .build()
															 .setAccessToken(
																	 Objects.requireNonNull(
																			 getOathResponse(
																					 accessParams))
																			.getString(
																					"access_token"))
															 .setRefreshToken(
																	 "refreshToken");
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		return new Gmail.Builder(HTTP_TRANSPORT,
								 JSON_FACTORY,
								 authorize)
				.setApplicationName(APPLICATION_NAME).build();
	}

	private static JSONObject getOathResponse(Map<String, Object> params) {

		try {
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0) {
					postData.append('&');
				}
				postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()),
												  StandardCharsets.UTF_8));
			}
			byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

			URL url = new URL(OATH_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.getOutputStream().write(postDataBytes);

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder buffer = new StringBuilder();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
			}
			return new JSONObject(buffer.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
