import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import gmail.GmailAPIConnection;
import gmail.GmailAPIService;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class App {
	public static void main(String[] args) throws GeneralSecurityException, IOException {
		Gmail gmail = GmailAPIConnection.getGmailService(
				"1//0ePghvfIyCf-XCgYIARAAGA4SNwF-L9IrJC2RTTSq1bSN-QWPudTjHtyoHltMhqhSLgkxs-yecIpfJVURCRPf681J0Xu1l42n_a4");
		ListMessagesResponse messages = GmailAPIService.getInstance().getMessageList(gmail,
																					 "me",
																					 "is:unread");
		System.out.println(messages.getMessages().get(0).getId());
		Message message = GmailAPIService.getInstance().getMessage(gmail,
																   GmailAPIService.DEFAULT_USER,
																   messages.getMessages()
																		   .get(0)
																		   .getId());
		String emailBody = StringUtils
				.newStringUtf8(Base64.decodeBase64(
						message.getPayload().getParts().get(0).getBody().getData()));
		System.out.println(emailBody);
	}
}
