package view;

import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import gmail.GmailAPIService;
import lombok.Getter;
import lombok.SneakyThrows;
import model.MessageModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

@Getter
public class TopBarButtons {
	private final JButton nextButton, previousButton;
	private final JPanel panel;
	private final EmailsTableView emailsTableView;
	private final ProgressBarDialog progressBar;

	public TopBarButtons(EmailsTableView emailsTableView, ProgressBarDialog progressBar) {
		this.emailsTableView = emailsTableView;
		this.progressBar = progressBar;
		nextButton = new JButton("NEXT");
		previousButton = new JButton("PREVIOUS");
		panel = new JPanel();
		panel.add(previousButton);
		panel.add(nextButton);
		nextButton.addActionListener(e -> {
			nextButton.setEnabled(false);
			Task task = new Task(progressBar, nextButton, previousButton, emailsTableView);
			task.setNextPage(true);
			task.execute();
		});

		previousButton.addActionListener(e -> {
			previousButton.setEnabled(false);
			Task task = new Task(progressBar, nextButton, previousButton, emailsTableView);
			task.setNextPage(false);
			task.execute();
		});
	}
}
