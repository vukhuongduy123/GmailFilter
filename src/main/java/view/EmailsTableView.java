package view;

import lombok.Getter;
import model.MessageModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Getter
public class EmailsTableView {
	private final JTable table;
	private final JScrollPane scrollPane;

	private static class ColumnCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected,
													   boolean hasFocus, int row, int col) {
			// Cells are by default rendered as a JLabel.
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, value,
																	isSelected,
																	hasFocus, row, col);

			//Get the status for the current row.
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			if (((String) tableModel.getValueAt(row, 3)).trim().equals("spam")) {
				l.setBackground(Color.RED);
			} else {
				l.setBackground(Color.GREEN);
			}
			//Return the JLabel which renders the cell.
			return l;
		}
	}

	public EmailsTableView() {
		table = new JTable(new DefaultTableModel(new String[][]{}, new String[]{"FROM", "SUBJECT",
				"LABELS", "CLASSIFY"}));
		table.setDefaultRenderer(Object.class, new ColumnCellRenderer());
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
