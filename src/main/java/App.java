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
				Gmail gmail = GmailAPIConnection.getGmailService();
				GmailAPIService.getInstance().setGmailService(gmail);
				MainPanel panel = new MainPanel();
				MainFrame.getInstance().add(panel.getPanel());
				panel.getTopBarButtons().getNextButton().doClick();
				MainFrame.getInstance().pack();
				MainFrame.getInstance().setVisible(true);
				MainFrame.getInstance().setLocationRelativeTo(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}