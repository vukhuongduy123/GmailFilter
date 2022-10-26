package gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class GmailAPIService {
	public static final String DEFAULT_USER = "me";

	private enum LabelProperties {
		FILTER_HARM("filter: harm(duyvk)", "#434343", "#666666", "1"),
		FILTER_SPAM("filter: spam(duyvk)", "#434343", "#999999", "2"),
		FILTER_UNKNOWN("filter: unknow(duyvk)", "#434343", "#cccccc", "3"),
		FILTER_OK("filter: ok(duyvk)", "#434343", "#efefef", "4");
		private final String text;
		private final LabelColor labelColor;
		private final String id;

		LabelProperties(String text, String textColor, String backgroundColor, String id) {
			this.text = text;
			labelColor = new LabelColor();
			labelColor.setTextColor(textColor);
			labelColor.setBackgroundColor(backgroundColor);
			this.id = id;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private GmailAPIService() {
	}

	public static final GmailAPIService gmailAPIService = new GmailAPIService();

	public static GmailAPIService getInstance() {
		return gmailAPIService;
	}

	public ListMessagesResponse getMessageList(Gmail gmailService, String userId,
											   String query) throws
															IOException {
		Gmail.Users.Messages.List request =
				gmailService.users().messages().list(userId).setQ(query);
		ListMessagesResponse messagesResponse = request.execute();
		request.setPageToken(messagesResponse.getNextPageToken());
		return messagesResponse;
	}

	public Message getMessage(Gmail gmailService, String userId, String messageId) throws
																						  IOException {

		return gmailService.users().messages().get(userId, messageId).execute();
	}

	public void createLabels(Gmail gmailService, String userId) throws IOException {
		Label label;
		for (LabelProperties labelProperties : LabelProperties.values()) {
			label = new Label();
			label.setName(labelProperties.toString());
			label.setColor(labelProperties.labelColor);
			label.setId(labelProperties.id);
			gmailService.users().labels().create(userId, label).execute();
		}
	}

	public Message setMessageLabels(Gmail gmailService, String userId,
										   String messageId, String labelName) throws IOException {
		LabelProperties labelProperties =
				Stream.of(LabelProperties.values())
					  .filter(e -> e.toString().equalsIgnoreCase(labelName))
					  .findFirst()
					  .orElseThrow();
		Label label = new Label();
		label.setName(labelProperties.toString());
		label.setColor(labelProperties.labelColor);
		label.setId(labelProperties.id);
		ModifyMessageRequest modifyMessageRequest = new ModifyMessageRequest();
		modifyMessageRequest.setAddLabelIds(List.of(label.getId()));
		modifyMessageRequest.setRemoveLabelIds(
				getMessage(gmailService, userId, messageId).getLabelIds());
		return gmailService.users().messages().modify(userId, messageId, modifyMessageRequest).execute();
	}
}
