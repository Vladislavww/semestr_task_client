package bsu.rfe_g6k2.Yackou.client;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				final LoginFrame login_frame = new LoginFrame();
				login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				login_frame.setVisible(true);
			}
		});

	}

}
