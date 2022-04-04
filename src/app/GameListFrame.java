package app;


public class GameListFrame extends BaseFrame {

	
	public GameListFrame() {
		super("", 500, 400);
		
		setBorderLayout();
		
	}
	
	
	public static void main(String[] args) {
		new GameListFrame().setVisible(true);
	}

}
