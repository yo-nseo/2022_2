package app;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class BaseFrame extends JFrame {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	static Connection con;
	static Statement stmt;
	static DefaultTableCellRenderer centerCellRenderer;
	
	static class ComboItem {
		int id;
		String text;
		
		public ComboItem(int id, String text) {
			this.id = id;
			this.text = text;
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
	
	static interface ButtonAction {
		void action() throws Exception;
	}
	
	static interface ResultSetAction {
		void execute(ResultSet rs) throws Exception;
	}
	
	JPanel north = new JPanel();
	JPanel south = new JPanel();
	JPanel center = new JPanel();
	JPanel east = new JPanel();
	JPanel west = new JPanel();
	Thread thread;
	WindowListener prevListener;
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/2022지방_1?serverTimezone=UTC", "user", "1234");
			stmt = con.createStatement();
			sdf.setLenient(false);
			sdf2.setLenient(false);
			
			centerCellRenderer = new DefaultTableCellRenderer();
			centerCellRenderer.setHorizontalAlignment(0);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public BaseFrame(String title, int width, int height) {
		setTitle(title);
		setSize(width, height);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				destroyThread();
			}
		});
	}
	
	/*
	 * Button Action 내 익셉션 선언 회피
	 */
	JButton addButtonAction(JButton btn, ButtonAction action) {
		
		btn.addActionListener(e -> {
			try {
				action.action();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		return btn;
	}
	
	BaseFrame setPrev(WindowListener listener) {
		addWindowListener(listener);
		
		return this;
	}
	
	BaseFrame addPrevForm(Runnable r) {
		prevListener = new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				r.run();
			}
		};
		
		addWindowListener(prevListener);
		
		return this;
	}
	
	void disposWithoutPrevListener() {
		removeWindowListener(prevListener);
		dispose();
	}
	
	protected void destroyThread() {
		if (thread != null && thread.isAlive())
			thread.interrupt();
	}
	
	public static int executeSQL(String sql, Object ...objects) {
		
		try {
			var pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			for (int i = 0; i < objects.length; i++) {
				pstmt.setObject(i + 1, objects[i]);
			}
			
			pstmt.executeUpdate();
			
			var rs = pstmt.getGeneratedKeys();
			
			if (rs.next()) {
				return rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static ResultSet getPreparedResultSet(String sql, Object ...objects) {	
		try {
			var pstmt = con.prepareStatement(sql);
			
			for (int i = 0; i < objects.length; i++) {
				pstmt.setObject(i + 1, objects[i]);
			}
			
			return pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void runResultSetblock(ResultSet rs, ResultSetAction action) {
		try (var resultSet = rs) {
			action.execute(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setBorderLayout()
	{
		setLayout(new BorderLayout());
		add(north, BorderLayout.NORTH);
		add(south, BorderLayout.SOUTH);
		add(west, BorderLayout.WEST);
		add(east, BorderLayout.EAST);
		add(center);
	}
	
	public static JLabel createLabel(JLabel lb, Font font) {
		lb.setFont(font);
		
		return lb;
	}
	
	public static JLabel createLabel(String text, int align, int size) {
		var lb = new JLabel(text, align);
		
		lb.setFont(new Font("HY헤드라인M", 0, size));
		
		return lb;
	}
	
	public static JLabel createLabel(BufferedImage img, int w, int h) {
		var lb = new JLabel();
		
		lb.setIcon(getResizedIcon(img, w, h));
		lb.setBorder(new LineBorder(Color.BLACK));
		
		return lb;
	}
	
	public static ImageIcon getResizedIcon(BufferedImage img, int w, int h) {
		return new ImageIcon(img.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH));
	}
	
	public static <T extends JComponent> T createComp(T comp, int x, int y, int w, int h) {
		comp.setPreferredSize(new Dimension(w, h));
		comp.setBounds(x, y, w, h);
		
		return comp;
	}
	
	public static <T extends JComponent> T createComp(T comp, int w, int h) {
		return createComp(comp, 0, 0, w, h);
	}
	
	public static JButton createButton(String text, ActionListener action) {
		var btn = new JButton(text);

		btn.addActionListener(action);
		
		return btn;
	}
	
	public static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE | JOptionPane.OK_OPTION);
	}
	
	public static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE | JOptionPane.OK_OPTION);
	}

}
