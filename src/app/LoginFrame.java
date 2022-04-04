package app;

import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends BaseFrame {

	
	public LoginFrame() {
		super("로그인", 260, 200);
		
		setBorderLayout();
		north.add(createLabel("방탈출", 0, 24));
		
		var lbList = "아이디,비밀번호".split(",");
		JTextField[] tfList = {
			new JTextField(12),
			new JPasswordField(12)
		}; 
		JButton[] btnList = {
				new JButton("로그인"), new JButton("회원가입")
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
				eMsg("빈칸이 있습니다.");
				return;
			}
			
			var rs = getPreparedResultSet("SELECT * FROM user WHERE binary u_id = ? AND binary u_pw = ?", 
					tfList[0].getText(), tfList[1].getText());
			
			if (rs.next()) {
				disposWithoutPrevListener();
				iMsg(rs.getString("u_name") + "님 환영합니다.");
				
				new MainFrame().setVisible(true);
			} else {
				eMsg("회원 정보가 일치하지 않습니다.");
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
