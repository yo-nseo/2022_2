package app;


public class EmptyFrame extends BaseFrame {

	
	public EmptyFrame() {
		super("", 500, 400);
		
		setBorderLayout();
		
	}
	
	
	public static void main(String[] args) {
		new EmptyFrame().setVisible(true);
	}

}
