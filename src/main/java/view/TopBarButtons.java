package view;

import lombok.Getter;
import javax.swing.*;

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
