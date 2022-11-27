package view;

import lombok.Getter;
import model.MessageModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EmailsTableView {
	private final JTable table;
	private final JScrollPane scrollPane;
	private final List<MessageModel> messageModel = new ArrayList<>();
	private final DefaultTableModel defaultTableModel;

	public EmailsTableView() {
		defaultTableModel = new DefaultTableModel(new String[][]{}, new String[]{"FROM", "SUBJECT",
				"LABELS", "CLASSIFY"});
		table = new JTable(defaultTableModel);

		//table.setDefaultRenderer(Object.class, new CellRender());
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
	}

	public void addEmail(MessageModel model) {
		String[] rowValues = new String[4];
		rowValues[0] = model.getSender();
		rowValues[1] = model.getSubject();
		rowValues[2] = model.getLabels();
		rowValues[3] = model.getClassification();
		((DefaultTableModel) table.getModel()).addRow(rowValues);
		messageModel.add(model);
	}
}
