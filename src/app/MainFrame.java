package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import session.Session;

public class MainFrame extends BaseFrame {

	JComboBox cb = new JComboBox("지점,테마".split(","));
	JPanel animationPanel = new JPanel();
	
	public MainFrame() {
		super("메인", 600, 450);
		
		setBorderLayout();
		
		north.setLayout(new FlowLayout(FlowLayout.LEFT));
		north.add(createLabel(new JLabel("예약 TOP5"), new Font("HY헤드라인M", 0, 14)));
		north.add(cb);
		
		center.setLayout(null);
		center.add(animationPanel);
		
		var btnLabel = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");
		JButton[] btnList = new JButton[btnLabel.length];
		
		for (int i = 0; i < btnList.length; i++) {
			south.add(btnList[i] = createButton(btnLabel[i], e -> {}));
		}
		
		// 로그인 상태 확인
		if (Session.userNo == 0) {
			for (int i = 1; i <= 4; i++) {
				btnList[i].setEnabled(false);
			}
			
			// 로그인
			btnList[0].addActionListener(e -> {
				dispose();
				new LoginFrame()
					.addPrevForm(() -> new MainFrame().setVisible(true))
					.setVisible(true);
			} );
			
			// 마이페이지
			btnList[1].addActionListener(e -> {
				dispose();
				new MyPageFrame()
					.addPrevForm(() -> new MainFrame().setVisible(true))
					.setVisible(true);
			});
			// 검색
			btnList[2].addActionListener(e -> {
			});
			// 게시판
			btnList[3].addActionListener(e -> {
				dispose();
				new BoardFrame()
					.addPrevForm(() -> new MainFrame().setVisible(true))
					.setVisible(true);
			});
			// 방탈출게임
			// 예약현황
			
		} else {
			btnList[0].setText("로그아웃");
			
			btnList[0].addActionListener(e -> {
				Session.userNo = 0;
				dispose();
				new MainFrame().setVisible(true);				
			});
			
		}
		
		cb.addActionListener(e -> updateAnimation());
		this.updateAnimation();
	}
	
	public void updateAnimation() {
		String sql;
		animationPanel.removeAll();
		
		if (cb.getSelectedIndex() == 0) {
			sql = "SELECT c.c_no, c.c_name, COUNT(1) AS cnt\r\n"
					+ "FROM cafe c\r\n"
					+ "INNER JOIN reservation r ON c.c_no = r.c_no\r\n"
					+ "GROUP BY c.c_no\r\n"
					+ "ORDER BY cnt DESC, c.c_name\r\n"
					+ "LIMIT 5;";
		} else {
			sql = "SELECT t.t_no, t.t_name, COUNT(1) cnt\r\n"
					+ "FROM theme t\r\n"
					+ "INNER JOIN reservation r ON t.t_no = r.t_no\r\n"
					+ "GROUP BY t.t_no\r\n"
					+ "ORDER BY cnt DESC\r\n"
					+ "LIMIT 5";
		}
		
		var rs = getPreparedResultSet(sql);
		
		destroyThread();
		
		try {
			Queue<JPanel> queue = new LinkedList<JPanel>();
			
			while (rs.next()) {
				var panel = new JPanel(new BorderLayout());
				String caption = rs.getString(2);
				String path = "";
				
				if (cb.getSelectedIndex() == 0) {
					// 지점 이미지 (지점명 파싱)
					path = "./Datafiles/지점/" + caption.substring(0, caption.indexOf(" ")) + ".jpg";
				} else {
					// 테마 (테마 번호)
					path = "./Datafiles/테마/" + rs.getInt(1) +".jpg";
				}
				panel.add(new JLabel(
						getResizedIcon(ImageIO.read(new FileInputStream(path)),
						570, 340)));
				
				
				var lb = createLabel(new JLabel(caption, 0), new Font("HY헤드라인M", 0, 20));
				
				lb.setBorder(new EmptyBorder(8, 0, 40, 0));
				
				panel.add(createComp(lb, 0, 60), "South");
				
				queue.add(panel);
				animationPanel.add(createComp(panel, 570, 400));
			}
			
			animationPanel.setBounds(0, 0, 570, 400 * queue.size());
			
			thread = new Thread(() -> {
				int id = 0;
				
				try {
					while(true) {
						
						// 애니메이션
						for (int i = 0; i <= 100; i++) {
							thread.sleep(10);
							animationPanel.setBounds(0, -(i * 4), 570, 400 * queue.size());
						}
						
						// 맨 앞 item 맨 뒤로 넣기
						var item = queue.poll();
						
						animationPanel.add(item);
						// 다시 큐에 추가
						queue.add(item);
						
						// 다시 그리기
						animationPanel.setBounds(0, 0, 570, 400 * queue.size());
						animationPanel.revalidate();

					}
				} catch (InterruptedException e) {
				}
			});
			
			thread.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 화면 다시 그리기
		animationPanel.revalidate();
		animationPanel.repaint();
	}
	
	public static void main(String[] args) {
		// Testing용 로그인
		Session.userNo = 1;
		new MainFrame().setVisible(true);
	}

}
