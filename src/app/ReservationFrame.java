package app;


public class ReservationFrame extends BaseFrame {

	
	public ReservationFrame() {
		super("����", 500, 400);
		
		setBorderLayout();
		
	}
	
	
	public static void main(String[] args) {
		new ReservationFrame().setVisible(true);
	}

}
