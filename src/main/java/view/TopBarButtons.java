package view;

import lombok.Getter;
import javax.swing.*;

@Getter
public class TopBarButtons {
	private final JButton nextButton, previousButton;
	private final JPanel panel;
	private final EmailsTableView emailsTableView;
	private final ProgressBarDialog progressBar;
	private final JTextField textField;

	public TopBarButtons(EmailsTableView emailsTableView, ProgressBarDialog progressBar) {
		this.emailsTableView = emailsTableView;
		this.progressBar = progressBar;
		textField = new JTextField(30);
		nextButton = new JButton("NEXT");
		previousButton = new JButton("PREVIOUS");
		panel = new JPanel();
		panel.add(textField);
		panel.add(previousButton);
		panel.add(nextButton);
		nextButton.addActionListener(e -> {
			nextButton.setEnabled(false);
			Task task = new Task(progressBar, nextButton, previousButton, emailsTableView, textField);
			task.setNextPage(true);
			task.execute();
		});

		previousButton.addActionListener(e -> {
			previousButton.setEnabled(false);
			Task task = new Task(progressBar, nextButton, previousButton, emailsTableView, textField);
			task.setNextPage(false);
			task.execute();
		});
	}
}
