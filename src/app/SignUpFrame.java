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
		super("회원가입", 400, 300);
		
		setBorderLayout();
		
		var lbList = "이름,아이디,비밀번호,비밀번호 확인,생년월일".split(",");
		JTextField[] tfList = {
				new JTextField(20), new JTextField(20), new JPasswordField(20), new JPasswordField(20)
		};
		JComboBox[] cbList = { new JComboBox(), new JComboBox(), new JComboBox() };
		LocalDate now = LocalDate.now();
		
		for (int y = 1900; y <= now.getYear(); y++) {
			cbList[0].addItem(y);
		}
		
		cbList[0].addActionListener(e -> {
			// year 변경
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
			// month 변경
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
				var lb = "년,월,일".split(",");
				
				for (int j = 0; j < lb.length; j++) {
					center.add(cbList[j]);
					center.add(new JLabel(lb[j]));
				}
			}
			
		}
		
		center.add(addButtonAction(new JButton("회원가입"), () -> {
			if(Arrays.stream(tfList)
				.anyMatch(tf -> tf.getText().length() == 0)) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			// 아이디 4~8 또는 중복 여부
			try (var rs = getPreparedResultSet("SELECT * FROM user WHERE u_id = ?", tfList[1].getText())) {
				if (tfList[1].getText().length() < 4 || tfList[1].getText().length() > 8 || rs.next()) {
					eMsg("사용할 수 없는 아이디입니다.");
					return;
				}
			}
			
			// 비밀번호 4글자 이상 연속으로 같을 경우
			// 각 아이디 4글자씩 반복하여 비밀번호 포함 여부 검사 e.g. aroom aroo room
			for (int i = 0; i <= tfList[1].getText().length() - 4; i++) {
				String subStr = tfList[1].getText().substring(i, i + 4);
				
				if (tfList[2].getText().contains(subStr)) {
					eMsg("비밀번호는 아이디와 4글자 이상 연속으로 겹쳐질 수 없습니다.");
					return;
				}				
			}
			
			// 비밀번호 확인
			if (tfList[2].getText().equals(tfList[3].getText()) == false) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}
			
			// 가입처리
			executeSQL("INSERT INTO user VALUES(0, ?, ?, ?, ?)", 
					tfList[1].getText(),
					tfList[2].getText(),
					tfList[0].getText(),
					String.format("%d-%02d-%02d", cbList[0].getSelectedItem(),
							cbList[1].getSelectedItem(),
							cbList[2].getSelectedItem()));
			
			iMsg(tfList[0].getText() + "님 가입을 환영합니다.");
			
			dispose();
			
		}));
		
		
	}
	
	
	public static void main(String[] args) {
		new SignUpFrame().setVisible(true);
	}

}
