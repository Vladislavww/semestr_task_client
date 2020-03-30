package bsu.rfe_g6k2.Yackou.client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout.Alignment;


public class LoginFrame extends JFrame{
	private static final String FRAME_TITLE = "Клиент мгновенных сообщений-авторизация";
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JCheckBox NewUserFlag;
	private JLabel ResultLabel;
	private static final int SMALL_GAP = 5;
	private static final int MEDIUM_GAP = 10;
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	private NetClass NetManager = new NetClass();
	
	public LoginFrame(){
		super(FRAME_TITLE);
		final Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
		setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		JLabel UsernameLabel = new JLabel("Login:");
		JLabel PasswordLabel = new JLabel("Password:");
		JLabel NewUserLabel = new JLabel("Если вы новый пользователь");
		ResultLabel = new JLabel();
		ResultLabel.setVisible(false);
		textFieldUsername = new JTextField("", 30);
		textFieldUsername.setMaximumSize(textFieldUsername.getPreferredSize());
		textFieldPassword = new JTextField("", 30);
		textFieldPassword.setMaximumSize(textFieldPassword.getPreferredSize());
		final JButton enterButton = new JButton("Войти");
		enterButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				enter();
			}
		});
		NewUserFlag = new JCheckBox();
		final JPanel MainPanel = new JPanel();
		final GroupLayout layout2 = new GroupLayout(MainPanel);
		MainPanel.setLayout(layout2);
		layout2.setHorizontalGroup(layout2.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout2.createParallelGroup(Alignment.LEADING)
				.addComponent(UsernameLabel)
				.addComponent(textFieldUsername)
				.addComponent(PasswordLabel)
				.addComponent(textFieldPassword)
				.addGroup(layout2.createSequentialGroup()
					.addComponent(NewUserFlag)
					.addGap(SMALL_GAP)
					.addComponent(NewUserLabel))
			.addComponent(ResultLabel)
			.addComponent(enterButton))
			.addContainerGap());
		layout2.setVerticalGroup(layout2.createSequentialGroup()
			.addContainerGap()
			.addComponent(UsernameLabel)
			.addGap(SMALL_GAP)
			.addComponent(textFieldUsername)
			.addGap(MEDIUM_GAP)
			.addComponent(PasswordLabel)
			.addGap(SMALL_GAP)
			.addComponent(textFieldPassword)
			.addGap(MEDIUM_GAP)
			.addGroup(layout2.createParallelGroup(Alignment.BASELINE)
				.addComponent(NewUserFlag)
				.addComponent(NewUserLabel))
			.addGap(MEDIUM_GAP)
			.addComponent(ResultLabel)
			.addGap(MEDIUM_GAP)
			.addComponent(enterButton)
			.addContainerGap());
		final GroupLayout layout1 = new GroupLayout(getContentPane());
		setLayout(layout1);
		layout1.setHorizontalGroup(layout1.createSequentialGroup()
			.addContainerGap()
			.addGroup(layout1.createParallelGroup()
				.addComponent(MainPanel))
			.addContainerGap());
		layout1.setVerticalGroup(layout1.createSequentialGroup()
			.addContainerGap()
			.addGap(MEDIUM_GAP)
			.addComponent(MainPanel)
			.addContainerGap());
		NetManager.addMessageListener(new MessageListener(){
			public void messageReceived(String message) {
				checkInResult(message);
			}
			public void messageReceived(LinkedList<String> message) {
				//ничего
			}
			public void messageReceived(String name, String message) {
				//ничего
			}
			public void messageReceived(byte[] bytes, int bytesSize) {
				//ничего
			}
		});
	}
	
	//попытка авторизации
	//и обработка ошибок
	private void enter(){
		String login = textFieldUsername.getText();
		String password = textFieldPassword.getText();
		if(NewUserFlag.isSelected() == false){
			int result = NetManager.send("CHECK_IN", login, password);
			if(result==1){
				JOptionPane.showMessageDialog(LoginFrame.this,"Не удалось отправить сообщение: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
			}
			else if(result==2){
				JOptionPane.showMessageDialog(LoginFrame.this,"Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
			}
		}
		else{
			int result = NetManager.send("NEW_USER", login, password);
			if(result==1){
				JOptionPane.showMessageDialog(LoginFrame.this,"Не удалось отправить сообщение: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
			}
			else if(result==2){
				JOptionPane.showMessageDialog(LoginFrame.this,"Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	//функция изменеия ResultLabel в зависимости от результата ввода логина и пароля(или регистрации)
	private void checkInResult(String text){ 
		ResultLabel.setVisible(true);
		if(text.equals("true")){
			ResultLabel.setText("Пароль правильный!");
			final MenuFrame menu_frame = new MenuFrame(textFieldUsername.getText(), NetManager);
			menu_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			menu_frame.setVisible(true);
			NetManager.removeMessageListener(new MessageListener(){ //убираю слушатель за ненадобностью
				public void messageReceived(String message) {
					checkInResult(message);
				}
				public void messageReceived(String name, String message) {
					//ничего
				}
				public void messageReceived(byte[] bytes, int bytesSize) {
					//ничего
				}
			});
			this.setVisible(false);
			this.setEnabled(false);
		}
		else if(text.equals("false")){
			ResultLabel.setText("Логин или пароль неправильный");
		}
		else if(text.equals("created")){
			ResultLabel.setText("Учетная запись создана");
		}
		else if(text.equals("not_created")){
			ResultLabel.setText("Такой логин уже есть. Придумайте другой.");
		}
	}

}
