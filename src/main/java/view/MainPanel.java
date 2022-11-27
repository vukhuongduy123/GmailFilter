package view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class MainPanel {
	private final JPanel panel;
	//private final EmailListView emailListView;
	private final ProgressBarDialog progressBar;
	private final EmailsTableView emailsTableView;

	public MainPanel() {
		panel = new JPanel(new BorderLayout());
		//emailListView = new EmailListView();
		progressBar = new ProgressBarDialog();
		emailsTableView = new EmailsTableView();
		panel.add(emailsTableView.getScrollPane(), BorderLayout.CENTER);
	}
}
