package view;

import lombok.Getter;
import model.MessageModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

@Getter
public class EmailsTableView {
	private final JTable table;
	private final JScrollPane scrollPane;
	//private final List<MessageModel> messageModel = new ArrayList<>();

	public EmailsTableView() {
		table = new JTable(new DefaultTableModel(new String[][]{}, new String[]{"FROM", "SUBJECT",
				"LABELS", "CLASSIFY"}));
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
	}

	public void addEmail(List<MessageModel> model) {
		for (MessageModel messageModel : model) {
			String[] rowValues = new String[4];
			rowValues[0] = messageModel.getSender();
			rowValues[1] = messageModel.getSubject();
			rowValues[2] = messageModel.getLabels();
			rowValues[3] = messageModel.getClassification();
			((DefaultTableModel) table.getModel()).addRow(rowValues);
		}
	}

	public void removeAll() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rows = model.getRowCount();
		for (int i = rows - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
}
