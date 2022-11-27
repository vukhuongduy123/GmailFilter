package view;

import javax.swing.*;

public class MainFrame {
	private static final JFrame frameInstance;

	static {
		frameInstance = new JFrame("GMAIL");
		frameInstance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameInstance.setLocationRelativeTo(null);
	}

	public static JFrame getInstance() {
		return frameInstance;
	}
}
