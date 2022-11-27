import cc.mallet.util.MalletLogger;
import classify.TextImport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import gmail.GmailAPIConnection;
import gmail.GmailAPIService;
import model.MessageModel;
import view.EmailsTableView;
import view.MainFrame;
import view.MainPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
				ListMessagesResponse messages = GmailAPIService.getInstance().getMessageList(gmail,
																							 "me",
																							 "is:unread");
				MainPanel panel = new MainPanel();
				MainFrame.getInstance().add(panel.getPanel());
				for (Message message : messages.getMessages()) {
					System.out.println("Running");
					Message msg = GmailAPIService.getInstance().getMessage(gmail,
																		   GmailAPIService.DEFAULT_USER,
																		   message.getId(), "full");
					panel.getEmailsTableView()
						 .addEmail(new MessageModel(Collections.emptyList(), msg));
				}
				new TextImport(
						"E:\\Self Study Course\\java\\GmailFilter\\src\\main\\resources\\emails_data");
				MainFrame.getInstance().pack();
				MainFrame.getInstance().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
