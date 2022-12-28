import com.google.api.services.gmail.Gmail;
import gmail.GmailAPIConnection;
import gmail.GmailAPIService;
import view.MainFrame;
import view.MainPanel;

import javax.swing.*;
import java.util.Objects;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				String path = Objects.requireNonNull(App.class
															 .getClassLoader()
															 .getResource("logging.properties"))
									 .getFile();
				System.setProperty("java.util.logging.config.file", path);
				String code = JOptionPane.showInputDialog("Enter your code");
				Gmail gmail = GmailAPIConnection.getGmailService(code);
				GmailAPIService.getInstance().setGmailService(gmail);
				MainPanel panel = new MainPanel();
				MainFrame.getInstance().add(panel.getPanel());
				panel.getTopBarButtons().getNextButton().doClick();
				MainFrame.getInstance().pack();
				MainFrame.getInstance().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
