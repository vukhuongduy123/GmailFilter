package view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class ProgressBarDialog {
	private final JProgressBar progressBar;
	private final JDialog dialog;

	public ProgressBarDialog() {
		dialog = new JDialog((Dialog) null, "Progress Dialog", false);

		progressBar = new JProgressBar();
		progressBar.setSize(0, 500);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		dialog.add(BorderLayout.CENTER, progressBar);
		dialog.add(BorderLayout.NORTH, new JLabel("Progress..."));
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setSize(300, 75);
	}

	public void setRange(int start, int end) {
		progressBar.setMinimum(start);
		progressBar.setMaximum(end);
		progressBar.setValue(0);
	}

	public void iterator() {
		progressBar.setValue(progressBar.getValue() + 1);
	}
}
