package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class SearchFrame extends BaseFrame {

	DefaultTableModel dtm = new DefaultTableModel("no,����".split(","), 0) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	
	JComboBox cbGenre = new JComboBox();
	JTextField tf = new JTextField(16);
	JPanel listPanel = new JPanel();
	JTable table = new JTable(dtm);
	
	public SearchFrame() {
		super("�˻�", 750, 500);
		
		setBorderLayout();
		
		var northTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
		var northCenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		northTop.add(createLabel("��Ż�� ī�� �˻�", JLabel.LEFT, 30));
		northCenter.add(new JLabel("�帣"));
		northCenter.add(cbGenre);
		northCenter.add(new JLabel("�׸�"));
		northCenter.add(tf);
		northCenter.add(createButton("�˻�", e -> updateList(true)));
		
		north.setLayout(new BorderLayout());
		north.add(northTop, "North");
		north.add(northCenter);
		
		west.setLayout(new BorderLayout());
		
		table.removeColumn(table.getColumn("no"));
		table.getColumn("����").setCellRenderer(centerCellRenderer);
		
		west.add(createComp(new JScrollPane(table), 100, 0));
		
		center.setLayout(new BorderLayout());
		center.add(new JScrollPane(listPanel));
		
		runResultSetblock(getPreparedResultSet("SELECT * FROM area"), rs -> {
			
			dtm.addRow(new Object[] { 0, "����" });
			
			while (rs.next())
				dtm.addRow(new Object[] { rs.getInt(1), rs.getString(2) });
		}	);
		
		runResultSetblock(getPreparedResultSet("SELECT * FROM genre"), rs -> {
			
			cbGenre.addItem(new ComboItem(0, "��ü"));
			
			while (rs.next())
				cbGenre.addItem(new ComboItem(rs.getInt(1), rs.getString(2)));
		});
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionInterval(0, 0);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				updateList(false);
			}
		});
		
		updateList(false);
	}
	
	void updateList(boolean checkGenre) {
		
		listPanel.removeAll();
		
		int genreNo = ((ComboItem) cbGenre.getSelectedItem()).id;
		int row = table.getSelectedRow();
		int areaNo = (Integer) dtm.getValueAt(row, 0);
		
		String sql = "SELECT c.*\r\n"
				+ "-- INNER JOIN�� �ƴ� CROSS JOIN���� ��� ���ڵ带 ����\r\n"
				+ "FROM 2022����_1.cafe c, theme t\r\n"
				+ "-- LIKE���� �̿��ؼ� t_no�� �� �� �ִ� ��� ��츦 �������� full searching\r\n"
				+ "-- 1. 1,�� ���� ��� (ù ����)\r\n"
				+ "-- 2. ,1�� ���� ��� (������)\r\n"
				+ "-- 3. ,1,�� ���� ��� (�߰��� ����)\r\n"
				+ "-- 4. 1 ���� ���� ���\r\n"
				+ "WHERE (c.t_no LIKE concat(t.t_no, \",%\") OR c.t_no LIKE concat(\"%,\", t.t_no) OR c.t_no LIKE concat(\"%,\", t.t_no ,\",%\") OR c.t_no LIKE t.t_no)\r\n";
		
		if (areaNo != 0)
			sql += "AND c.a_no = " + areaNo + " ";
		
		if (checkGenre && genreNo != 0)
			sql += "AND t.g_no = " + genreNo + " ";
		
		sql += "GROUP BY c.c_no\r\n"
				+ "-- TextField ������ t_name�� ���ԵǴ���\r\n"
				+ "HAVING COUNT(IF(t.t_name LIKE ?, 1, null)) > 0;";
		
		runResultSetblock(getPreparedResultSet(sql, "%" + tf.getText() + "%"), rs -> {
			
			int cnt = 0;
			
			while (rs.next()) {
				var panel = new JPanel(new BorderLayout());
				String name = rs.getString("c_name");
				String cafeNo = rs.getString("c_no");
				String imgName = name.split(" ")[0] + ".jpg";
				
				panel.setBorder(new LineBorder(Color.BLACK));
				panel.add(new JLabel(name, 0), "South");
				
				var lbImg = new JLabel("", 0);
				
				lbImg.setIcon(getResizedIcon(ImageIO.read(new File("./Datafiles/����/" + imgName)), 180, 150));
				lbImg.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						setVisible(false);
						new CafeThemeFrame(cafeNo)
							.addPrevForm(() -> setVisible(true))
							.setVisible(true);
					}
				});
				
				panel.add(lbImg);
				
				listPanel.add(panel);
				
				++cnt;
			}
			
			int rows = (int) Math.ceil(cnt / 3f);
			
			// ���� ��ĭ ä��� (3��)
			for (int i = 0; i < (rows * 3) - cnt; i++) {
				listPanel.add(new JPanel());
			}
					
			listPanel.setLayout(new GridLayout(rows, 3, 6, 6));
			listPanel.setPreferredSize(new Dimension(550, rows * 180));
			listPanel.revalidate();
			listPanel.repaint();
			
			if (cnt == 0) {
				eMsg("�˻� ����� �����ϴ�.");
				tf.setText("");
				cbGenre.setSelectedIndex(0);
				table.setRowSelectionInterval(0, 0);
				updateList(false);
			}
		});
		
	}
	
	
	public static void main(String[] args) {
		new SearchFrame().setVisible(true);
	}

}
