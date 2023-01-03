package view;

import cc.mallet.classify.Classification;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import classify.TextClassify;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import gmail.GmailAPIService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import model.MessageModel;
import org.jsoup.Jsoup;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class Task extends SwingWorker<Void, Void> implements PropertyChangeListener {
	private ProgressBarDialog progressBar;
	private JButton nextButton, previousButton;
	private JTextField textField;
	private EmailsTableView emailsTableView;
	private boolean nextPage;

	public Task(ProgressBarDialog progressBar, JButton nextButton, JButton previousButton,
				EmailsTableView emailsTableView, JTextField textField) {
		super();
		this.progressBar = progressBar;
		this.nextButton = nextButton;
		this.previousButton = previousButton;
		this.emailsTableView = emailsTableView;
		this.textField = textField;
		this.addPropertyChangeListener(this);
	}

	@Override
	@SneakyThrows
	public Void doInBackground() {
		progressBar.setRange(0, 25);
		progressBar.getDialog().setVisible(true);
		int progress = 0;
		setProgress(progress);

		ListMessagesResponse messages;
		if (isNextPage()) {
			messages = GmailAPIService.getInstance()
									  .next("me", "in:anywhere " + (textField.getText() == null ?
											  "" : textField.getText()));
		} else {
			messages = GmailAPIService.getInstance()
									  .previous("me",
												"in:anywhere " + (textField.getText() == null ?
														"" : textField.getText()));
		}
		emailsTableView.removeAll();
		for (Message message : messages.getMessages()) {
			Message msg = GmailAPIService.getInstance()
										 .getMessage("me", message.getId(), "full");

			MessagePart messagePart = msg.getPayload();
			StringBuilder content = new StringBuilder();
			try {
				if (messagePart == null) {
					content = new StringBuilder(
							StringUtils.newStringUtf8(Base64.decodeBase64(msg.getRaw())));
				} else {
					if (messagePart.getMimeType().equalsIgnoreCase("text/plain")) {
						content = new StringBuilder((StringUtils.newStringUtf8(Base64.decodeBase64(
								messagePart.getBody().getData()))));
					} else if (messagePart.getMimeType().equalsIgnoreCase("text/html")) {
						content = new StringBuilder(Jsoup.parse(StringUtils.newStringUtf8(
								Base64.decodeBase64((messagePart.getBody().getData()))))
														 .wholeText());
					} else if (messagePart.getMimeType().startsWith("multipart")) {
						for (MessagePart part : messagePart.getParts()) {
							if (part.getMimeType().equalsIgnoreCase("text/plain")) {
								content.append(StringUtils.newStringUtf8(
										Base64.decodeBase64(part.getBody().getData())));
							} else if (part.getMimeType().equalsIgnoreCase("text/html")) {
								content.append(Jsoup.parse(StringUtils.newStringUtf8(
										Base64.decodeBase64((part.getBody().getData()))))
													.wholeText());
							}
						}
					}
				}
				Instance instance = new Instance(content.toString(),
												 "", "", "");
				Pipe pipe = TextClassify.getInstance().getPipe();
				InstanceList instances = new InstanceList(pipe);
				instances.addThruPipe(instance);

				ArrayList<Classification> classifications = TextClassify.getInstance()
																		.getClassify(instances);
				List<String> bestLabel = classifications.stream()
														.map(e -> e.getLabeling()
																   .getBestLabel()
																   .toString())
														.collect(Collectors.toCollection(
																ArrayList::new));
				emailsTableView
						.addEmail(Collections.singletonList(
								new MessageModel(bestLabel, msg)));
				setProgress(progress++);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() {
		nextButton.setEnabled(true);
		previousButton.setEnabled(true);
		progressBar.getDialog().setVisible(false);
		Toolkit.getDefaultToolkit().beep();

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();
			progressBar.getProgressBar().setValue(progress);
		}
	}
}
