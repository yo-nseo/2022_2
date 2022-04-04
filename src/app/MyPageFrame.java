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
	DefaultTableModel dtm = new DefaultTableModel("no,날짜,시간,카페 이름,테마명,인원수,가격".split(","), 0);
	JComboBox cb = new JComboBox();
	JLabel lbAmount = new JLabel("총 금액 :0");
	
	public MyPageFrame() {
		super("마이페이지", 700, 400);
		
		setBorderLayout();
		
		north.setLayout(new BorderLayout());
		var left = new JPanel();
		var right = new JPanel();
		
		cb.addItem("전체");
		for (int i = 1; i <= 12; i++) {
			cb.addItem(i + "월");
		}
		
		left.add(new JLabel("날짜 : "));
		left.add(cb);
		
		north.add(left, "West");
		north.add(right, "East");
		
		var table = new JTable(dtm);
		
		center.setLayout(new BorderLayout());
		center.add(new JScrollPane(table));
		
		table.removeColumn(table.getColumn("no"));
		// 다중 행 선택 방지
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(centerCellRenderer);
		}
		
		table.getColumn("카페 이름").setPreferredWidth(150);
		table.getColumn("테마명").setPreferredWidth(150);
		
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		south.add(lbAmount);
		
		cb.addActionListener(e -> update());
		
		right.add(createButton("삭제", e -> {
			int row = table.getSelectedRow();
			
			if (row == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}
			
			String date = dtm.getValueAt(row, 1) + " " + dtm.getValueAt(row, 2);
			
			// 현재 날짜보다 과거인 경우
			try {
				if (sdf2.parse(date).compareTo(new Date()) < 0) {
					eMsg("지난 예약은 삭제할 수 없습니다.");
					return;
				}				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			executeSQL("DELETE FROM reservation WHERE r_no = " + dtm.getValueAt(row, 0));
			
			iMsg("삭제가 완료되었습니다.");
			update();
			
		}));

		update();
	}
	
	void update() {
		dtm.setRowCount(0);
		
		var rs = getPreparedResultSet("SELECT r_no, r_date, r_time, c_name, t.t_name, r_people, c_price\r\n"
				+ "FROM 2022지방_1.reservation r\r\n"
				+ "INNER JOIN cafe c ON r.c_no = c.c_no\r\n"
				+ "INNER JOIN theme t ON r.t_no = t.t_no\r\n"
				+ "WHERE u_no = 1\r\n");
		
		try {
			int amount = 0;
			while (rs.next()) {
				
				if (cb.getSelectedIndex() > 0) {
					// 월이 선택된 경우
					// 월이 다르면 다음 레코드
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
			
			lbAmount.setText("총 금액 :" + String.format("%,d", amount));
			
			if (amount == 0 && cb.getSelectedIndex() > 0) {
				eMsg("예약현황이 없습니다.");
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
