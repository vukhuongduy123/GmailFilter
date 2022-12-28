package view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class MainPanel {
	private final JPanel panel;
	private final ProgressBarDialog progressBar;
	private final EmailsTableView emailsTableView;
	private final TopBarButtons topBarButtons;

	public MainPanel() {
		panel = new JPanel(new BorderLayout());
		progressBar = new ProgressBarDialog();
		emailsTableView = new EmailsTableView();
		topBarButtons = new TopBarButtons(emailsTableView, progressBar);
		panel.add(emailsTableView.getScrollPane(), BorderLayout.CENTER);
		panel.add(topBarButtons.getPanel(), BorderLayout.NORTH);
	}
}
