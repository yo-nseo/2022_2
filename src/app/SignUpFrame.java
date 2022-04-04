package app;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SignUpFrame extends BaseFrame {

	
	public SignUpFrame() {
		super("ȸ������", 400, 300);
		
		setBorderLayout();
		
		var lbList = "�̸�,���̵�,��й�ȣ,��й�ȣ Ȯ��,�������".split(",");
		JTextField[] tfList = {
				new JTextField(20), new JTextField(20), new JPasswordField(20), new JPasswordField(20)
		};
		JComboBox[] cbList = { new JComboBox(), new JComboBox(), new JComboBox() };
		LocalDate now = LocalDate.now();
		
		for (int y = 1900; y <= now.getYear(); y++) {
			cbList[0].addItem(y);
		}
		
		cbList[0].addActionListener(e -> {
			// year ����
			cbList[1].removeAllItems();
			cbList[2].removeAllItems();
			
			int months = (Integer) cbList[0].getSelectedItem() < now.getYear() ? 12 : now.getMonthValue();
			
			for (int m = 1; m <= months; m++) {
				cbList[1].addItem(m);
			}
			
			cbList[1].setSelectedItem(1);
			cbList[2].setSelectedItem(1);
		});
		
		cbList[1].addActionListener(e -> {
			// month ����
			cbList[2].removeAllItems();
			
			if (cbList[1].getSelectedItem() == null) return;
			
			var tmp = LocalDate.of((Integer) cbList[0].getSelectedItem(), (Integer) cbList[1].getSelectedItem(), 1);
			int days = (Integer) cbList[0].getSelectedItem() == now.getYear() &&
					(Integer) cbList[1].getSelectedItem() == now.getMonthValue() ? now.getDayOfMonth() : tmp.lengthOfMonth();
			
			for (int d = 1; d <= days; d++) {
				cbList[2].addItem(d);
			}
			
			cbList[2].setSelectedItem(1);
		});
		
		cbList[0].setSelectedItem(now.getYear());
		cbList[1].setSelectedItem(now.getMonthValue());
		cbList[2].setSelectedItem(now.getDayOfMonth());
		
		for (int i = 0; i < lbList.length; i++) {
			
			center.add(createComp(new JLabel(lbList[i]), 100, 30));
			
			if (i < 4) {
				center.add(tfList[i]);
			} else {
				var lb = "��,��,��".split(",");
				
				for (int j = 0; j < lb.length; j++) {
					center.add(cbList[j]);
					center.add(new JLabel(lb[j]));
				}
			}
			
		}
		
		center.add(addButtonAction(new JButton("ȸ������"), () -> {
			if(Arrays.stream(tfList)
				.anyMatch(tf -> tf.getText().length() == 0)) {
				eMsg("��ĭ�� �ֽ��ϴ�.");
				return;
			}

			// ���̵� 4~8 �Ǵ� �ߺ� ����
			try (var rs = getPreparedResultSet("SELECT * FROM user WHERE u_id = ?", tfList[1].getText())) {
				if (tfList[1].getText().length() < 4 || tfList[1].getText().length() > 8 || rs.next()) {
					eMsg("����� �� ���� ���̵��Դϴ�.");
					return;
				}
			}
			
			// ��й�ȣ 4���� �̻� �������� ���� ���
			// �� ���̵� 4���ھ� �ݺ��Ͽ� ��й�ȣ ���� ���� �˻� e.g. aroom aroo room
			for (int i = 0; i <= tfList[1].getText().length() - 4; i++) {
				String subStr = tfList[1].getText().substring(i, i + 4);
				
				if (tfList[2].getText().contains(subStr)) {
					eMsg("��й�ȣ�� ���̵�� 4���� �̻� �������� ������ �� �����ϴ�.");
					return;
				}				
			}
			
			// ��й�ȣ Ȯ��
			if (tfList[2].getText().equals(tfList[3].getText()) == false) {
				eMsg("��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
				return;
			}
			
			// ����ó��
			executeSQL("INSERT INTO user VALUES(0, ?, ?, ?, ?)", 
					tfList[1].getText(),
					tfList[2].getText(),
					tfList[0].getText(),
					String.format("%d-%02d-%02d", cbList[0].getSelectedItem(),
							cbList[1].getSelectedItem(),
							cbList[2].getSelectedItem()));
			
			iMsg(tfList[0].getText() + "�� ������ ȯ���մϴ�.");
			
			dispose();
			
		}));
		
		
	}
	
	
	public static void main(String[] args) {
		new SignUpFrame().setVisible(true);
	}

}
