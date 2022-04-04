package app;


public class BoardFrame extends BaseFrame {

	
	public BoardFrame() {
		super("", 500, 400);
		
		setBorderLayout();
		
	}
	
	
	public static void main(String[] args) {
		new BoardFrame().setVisible(true);
	}

}
