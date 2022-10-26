package gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;


public class GmailAPIConnection {
	private static final String APPLICATION_NAME = "GmailFilter";
	private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final File CREDENTIALS_FILE = new File(System.getProperty("user.dir") +
														  "\\src\\main\\resources\\credentials" +
														  ".json");
	private static final String OATH_URL = "https://accounts.google.com/o/oauth2/token";
	private static final String CODE_URL = "https://accounts.google" +
										   ".com/o/oauth2/v2/auth?scope=https://mail.google.com&access_type=offline&redirect_uri=http://localhost&response_type=code&client_id=599356532863-f1p2f218clkgbt7m365l2vfdieg8nf4i.apps.googleusercontent.com";

	private GmailAPIConnection() {
	}

	public static String getRefreshToken() throws URISyntaxException, IOException {
		if (Desktop.isDesktopSupported())
			Desktop.getDesktop().browse(new URI(CODE_URL));
		else
			return null;

		System.out.println("Enter your code: ");
		Scanner scanner = new Scanner(System.in);
		String code = scanner.nextLine();
		code = code.substring(code.indexOf("="));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
																	 new InputStreamReader(
																			 new FileInputStream(
																					 CREDENTIALS_FILE)));
		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("code", code);
		bodyMap.put("client_id", clientSecrets.getDetails().getClientId());
		bodyMap.put("client_secret", clientSecrets.getDetails().getClientSecret());
		bodyMap.put("redirect_uri", "http://localhost");
		bodyMap.put("grant_type", "authorization_code");

		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(
				RequestConfig.custom().build()).build();
		HttpPost httpPost = new HttpPost();

		return code;
	}

	@CheckReturnValue
	public static Gmail getGmailService(String refreshToken) throws
															 IOException,
															 GeneralSecurityException {
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
																	 new InputStreamReader(
																			 new FileInputStream(
																					 CREDENTIALS_FILE)));
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
																	 getAccessToken(clientSecrets,
																					refreshToken))
															 .setRefreshToken(
																	 "refreshToken");
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		return new Gmail.Builder(HTTP_TRANSPORT,
								 JSON_FACTORY,
								 authorize)
				.setApplicationName(APPLICATION_NAME).build();
	}

	private static String getAccessToken(GoogleClientSecrets googleClientSecrets,
										 String refreshToken) {

		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("grant_type", "refresh_token");
			params.put("client_id", googleClientSecrets.getDetails().getClientId());
			params.put("client_secret", googleClientSecrets.getDetails().getClientSecret());
			params.put("refresh_token", refreshToken);

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

			JSONObject json = new JSONObject(buffer.toString());
			return json.getString("access_token");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
