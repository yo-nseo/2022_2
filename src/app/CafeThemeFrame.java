package app;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CafeThemeFrame extends BaseFrame {

	JLabel themeImg = createComp(new JLabel(), 5, 35, 300, 350);
	JTextArea explan = createComp(new JTextArea(""), 305, 70, 270, 200);
	JLabel[] lbList = {
		createComp(createLabel("", JLabel.LEFT, 18), 305, 35, 250, 30),
		createComp(createLabel("", JLabel.LEFT, 14), 305, 280, 250, 25),
		createComp(createLabel("", JLabel.LEFT, 14), 305, 305, 250, 25),
		createComp(createLabel("", JLabel.LEFT, 14), 305, 330, 250, 25),
		createComp(createLabel("", JLabel.LEFT, 14), 305, 355, 250, 25),
	};
	JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
	String[] themeArray;
	HashMap<String, String> map = new HashMap<>();
	
	public CafeThemeFrame(String cafeNo) {
		super("지점소개", 600, 500);
		
		setBorderLayout();
		
		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		buttonPanel.add(createButton("예약하기", e -> {
			setVisible(false);
			new ReservationFrame()
				.addPrevForm(() -> setVisible(true))
				.setVisible(true);
		}));
		
		north.setLayout(new BorderLayout());
		north.add(createComp(new JScrollPane(themePanel), 142, 70), "West");
		north.add(buttonPanel, "East");
		
		// 여백 제거용
		this.remove(east);
		this.remove(west);
		this.remove(south);
		
		center.setBackground(Color.BLACK);
		center.setLayout(null);
		
		// 미리 themeNo 맵 생성
		runResultSetblock(getPreparedResultSet("SELECT * FROM theme"), rs -> {
			while (rs.next())
				map.put(rs.getString("t_no"), rs.getString("t_name"));
		});
		
		runResultSetblock(getPreparedResultSet("SELECT * FROM cafe WHERE c_no = ?", cafeNo), rs -> {
			// 레코드 없으면 return
			if (!rs.next()) return;
			
			String cafeName = rs.getString("c_name");
			
			themeArray = rs.getString("t_no").split(",");
			
			var lbTitle = createLabel(cafeName, JLabel.LEFT, 24);
			
			lbTitle.setForeground(Color.ORANGE);
			
			center.add(createComp(lbTitle, 5, 5, 300, 30));
			center.add(themeImg);
			
			for (var lb : lbList) {
				lb.setForeground(Color.WHITE);
				
				center.add(lb);
			}
						
			explan.setForeground(Color.WHITE);
			center.add(explan);
			explan.setFont(new Font("HY헤드라인M", 0, 16));
			explan.setForeground(Color.WHITE);
			explan.setOpaque(false);
			explan.setLineWrap(true);
			explan.setEditable(false);
			
			lbList[4].setText("가격 : " + String.format("%,d", rs.getInt("c_price")) + "원");
			
			updateTheme(themeArray[0]);
		});
		
	}
	
	void updateTheme(String themeNo) throws Exception {
		themePanel.removeAll();
		
		for (String tNo : themeArray) {
			var lbTheme = new JLabel();
			var img = ImageIO.read(new File("./Datafiles/테마/" + tNo + ".jpg"));
			var buffered = new BufferedImage(img.getWidth(), img.getHeight(),
					themeNo.equals(tNo) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_BYTE_GRAY);
			
			buffered.getGraphics().drawImage(img, 0, 0, null);
			
			lbTheme.setIcon(getResizedIcon(buffered, 40, 40));
			// 마우스 올리면 툴팁 텍스트
			lbTheme.setToolTipText(map.get(themeNo));
			
			lbTheme.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					try {
						updateTheme(tNo);						
					} catch(Exception e2) {
						e2.printStackTrace();
					}
				}
			});
			
			themePanel.add(lbTheme);
		}
		
		themePanel.revalidate();
		themePanel.repaint();
		
		runResultSetblock(getPreparedResultSet("SELECT t.*, g_name FROM theme t INNER JOIN genre g ON t.g_no = g.g_no WHERE t_no = " + themeNo), rs -> {
			
			if (!rs.next()) return;
			
			themeImg.setIcon(getResizedIcon(ImageIO.read(new File("./Datafiles/테마/" + themeNo + ".jpg")),
				300, 350));
			lbList[0].setText(rs.getString("t_name"));
			explan.setText(rs.getString("t_explan"));
			lbList[1].setText("장르 : " + rs.getString("g_name"));
			lbList[2].setText("최대 인원 : " + rs.getString("t_personnel") + "명");
			lbList[3].setText("시간 : " + rs.getString("t_time") + "분");
			
		});
		
	}
	
	
	public static void main(String[] args) {
		new CafeThemeFrame("A-001").setVisible(true);
	}

}
