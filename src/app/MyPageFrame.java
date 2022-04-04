package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import session.Session;

public class MyPageFrame extends BaseFrame {
	DefaultTableModel dtm = new DefaultTableModel("no,��¥,�ð�,ī�� �̸�,�׸���,�ο���,����".split(","), 0);
	JComboBox cb = new JComboBox();
	JLabel lbAmount = new JLabel("�� �ݾ� :0");
	
	public MyPageFrame() {
		super("����������", 700, 400);
		
		setBorderLayout();
		
		north.setLayout(new BorderLayout());
		var left = new JPanel();
		var right = new JPanel();
		
		cb.addItem("��ü");
		for (int i = 1; i <= 12; i++) {
			cb.addItem(i + "��");
		}
		
		left.add(new JLabel("��¥ : "));
		left.add(cb);
		
		north.add(left, "West");
		north.add(right, "East");
		
		var table = new JTable(dtm);
		
		center.setLayout(new BorderLayout());
		center.add(new JScrollPane(table));
		
		table.removeColumn(table.getColumn("no"));
		// ���� �� ���� ����
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(centerCellRenderer);
		}
		
		table.getColumn("ī�� �̸�").setPreferredWidth(150);
		table.getColumn("�׸���").setPreferredWidth(150);
		
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		south.add(lbAmount);
		
		cb.addActionListener(e -> update());
		
		right.add(createButton("����", e -> {
			int row = table.getSelectedRow();
			
			if (row == -1) {
				eMsg("������ ���ڵ带 �����ϼ���.");
				return;
			}
			
			String date = dtm.getValueAt(row, 1) + " " + dtm.getValueAt(row, 2);
			
			// ���� ��¥���� ������ ���
			try {
				if (sdf2.parse(date).compareTo(new Date()) < 0) {
					eMsg("���� ������ ������ �� �����ϴ�.");
					return;
				}				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			executeSQL("DELETE FROM reservation WHERE r_no = " + dtm.getValueAt(row, 0));
			
			iMsg("������ �Ϸ�Ǿ����ϴ�.");
			update();
			
		}));

		update();
	}
	
	void update() {
		dtm.setRowCount(0);
		
		var rs = getPreparedResultSet("SELECT r_no, r_date, r_time, c_name, t.t_name, r_people, c_price\r\n"
				+ "FROM 2022����_1.reservation r\r\n"
				+ "INNER JOIN cafe c ON r.c_no = c.c_no\r\n"
				+ "INNER JOIN theme t ON r.t_no = t.t_no\r\n"
				+ "WHERE u_no = 1\r\n");
		
		try {
			int amount = 0;
			while (rs.next()) {
				
				if (cb.getSelectedIndex() > 0) {
					// ���� ���õ� ���
					// ���� �ٸ��� ���� ���ڵ�
					if (cb.getSelectedIndex() != rs.getDate(2).toLocalDate().getMonthValue())
						continue;
				}
				
				dtm.addRow(new Object[] {
						rs.getInt(1),
						rs.getString(2),
						rs.getString(3),
						rs.getString(4),
						rs.getString(5),
						rs.getInt(6),
						String.format("%,d", rs.getInt(6) * rs.getInt(7))
				});
				
				amount += rs.getInt(6) * rs.getInt(7);
			}
			
			lbAmount.setText("�� �ݾ� :" + String.format("%,d", amount));
			
			if (amount == 0 && cb.getSelectedIndex() > 0) {
				eMsg("������Ȳ�� �����ϴ�.");
				cb.setSelectedIndex(0);
				update();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		Session.userNo = 1;
		new MyPageFrame().setVisible(true);
	}

}
