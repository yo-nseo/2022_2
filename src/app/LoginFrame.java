package app;

import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends BaseFrame {

	
	public LoginFrame() {
		super("�α���", 260, 200);
		
		setBorderLayout();
		north.add(createLabel("��Ż��", 0, 24));
		
		var lbList = "���̵�,��й�ȣ".split(",");
		JTextField[] tfList = {
			new JTextField(12),
			new JPasswordField(12)
		}; 
		JButton[] btnList = {
				new JButton("�α���"), new JButton("ȸ������")
		};
		
		for (int i = 0; i < lbList.length; i++) {
			center.add(createComp(new JLabel(lbList[i]), 50, 30));
			center.add(tfList[i]);
		}
		
		for (int i = 0; i < btnList.length; i++) {
			center.add(createComp(btnList[i], 90, 30));
		}
		
		addButtonAction(btnList[0], () -> {
			
			if (tfList[0].getText().length() == 0 || tfList[1].getText().length() == 0) {
				eMsg("��ĭ�� �ֽ��ϴ�.");
				return;
			}
			
			var rs = getPreparedResultSet("SELECT * FROM user WHERE binary u_id = ? AND binary u_pw = ?", 
					tfList[0].getText(), tfList[1].getText());
			
			if (rs.next()) {
				disposWithoutPrevListener();
				iMsg(rs.getString("u_name") + "�� ȯ���մϴ�.");
				
				new MainFrame().setVisible(true);
			} else {
				eMsg("ȸ�� ������ ��ġ���� �ʽ��ϴ�.");
			}
			
		});
		
		btnList[1].addActionListener(e -> {
			disposWithoutPrevListener();
			
			new SignUpFrame()
				.addPrevForm(() -> new LoginFrame().setPrev(prevListener).setVisible(true))
				.setVisible(true);
			
		});
		
	}
	
	
	public static void main(String[] args) {
		new LoginFrame().setVisible(true);
	}

}
