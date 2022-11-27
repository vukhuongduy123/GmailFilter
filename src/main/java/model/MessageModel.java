package model;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageModel {
	private List<String> classification;
	private Message message;

	public String getSubject() {
		MessagePart messagePart = message.getPayload();
		if (messagePart == null) {
			return message.getSnippet();
		} else {
			Optional<MessagePartHeader> optional = messagePart.getHeaders()
															  .stream()
															  .filter(e -> e.getName()
																			.equalsIgnoreCase(
																					"Subject"))
															  .findAny();
			return optional.isPresent() ? optional.get().getValue() : "";
		}
	}

	public String getSender() {
		Optional<MessagePartHeader> optional =
				message.getPayload().getHeaders().stream().filter(e -> e.getName()
																		.equalsIgnoreCase(
																				"From"))
					   .findAny();
		return optional.isPresent() ? optional.get().getValue() : "";
	}

	public String getLabels() {
		List<String> labelIds = message.getLabelIds();
		StringBuilder labels = new StringBuilder();
		labelIds.forEach(e -> labels.append(" ").append(e));
		return labels.toString();
	}

	public String getClassification() {
		StringBuilder labels = new StringBuilder();
		classification.forEach(e -> labels.append(" ").append(e));
		return labels.toString();
	}
}
