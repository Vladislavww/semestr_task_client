package bsu.rfe_g6k2.Yackou.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

/**
 * This is a class-frame. Here is a user manages his photos via simple GUI
 * 
 * @version No recording 28.05.2020
 * @author Vlad Yatskou
 */
@SuppressWarnings("serial")
public class MenuFrame extends JFrame {
	private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	private NetClass NetManager;
	private JFileChooser fileChooser = null; 
	private String UserName;
	private String UserPassword;
	private JLabel figure;
	private CryptClass crypter;
	byte[] bytesFigure;
	int bytesFigureSize;
	
	public MenuFrame(String name, String password, NetClass NetManager){
		super(FRAME_TITLE);
		UserName = name;
		UserPassword = password;
		this.NetManager = NetManager;
		final Toolkit kit = Toolkit.getDefaultToolkit();
		
		setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
		setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		crypter = new CryptClass();
		JMenuBar menuBar = new JMenuBar(); 
		
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("Файл");
		
		menuBar.add(fileMenu);
		
		/** Option to load photo into the program from the local PC */
		Action takePhotoAction = new AbstractAction("Выбрать фотографию"){
			public void actionPerformed(ActionEvent event){ 
				if (fileChooser==null) { 
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showOpenDialog(MenuFrame.this) == JFileChooser.APPROVE_OPTION) {
					openFigure(fileChooser.getSelectedFile());
				}
		}};
		
		fileMenu.add(takePhotoAction);
		
		/** Option to save photo from the program into the local PC */
		Action savePhotoAction = new AbstractAction("Сохранить выбранную фотографию"){
			public void actionPerformed(ActionEvent event){ 
				if (fileChooser==null) { 
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showSaveDialog(MenuFrame.this) == JFileChooser.APPROVE_OPTION) {
					saveFigure(fileChooser.getSelectedFile());
				}
		}};
		
		fileMenu.add(savePhotoAction);
		
		/** Option to load photo to the server from the program */
		Action importToServerAction = new AbstractAction("Загрузить выбранную фотографию на сервер"){
			public void actionPerformed(ActionEvent event) { 
				sendToServer();
		}};
		fileMenu.add(importToServerAction);
		
		figure = new JLabel();
		
		/** Option to delete photo from the server */
		Action deleteFromServerAction = new AbstractAction("Удалить выбранную фотографию с сервера"){
			public void actionPerformed(ActionEvent event) { 
				deleteFromServer();
		}};
		
		fileMenu.add(deleteFromServerAction);
		
		/** Additional option to administrator */
		if(UserName.equals("Admin")){
			JMenu adminMenu = new JMenu("Администратор");
			
			menuBar.add(adminMenu); 
			Action closeServerAction = new AbstractAction("Выключить сервер"){
				public void actionPerformed(ActionEvent event){ 
					closeServer();
			}};
			adminMenu.add(closeServerAction);
		}
		Box hboxfigure = Box.createHorizontalBox();
		
		hboxfigure.add(Box.createHorizontalGlue());
		hboxfigure.add(figure);
		hboxfigure.add(Box.createHorizontalGlue());
		
		Box buttons = Box.createHorizontalBox();
		
		buttons.add(Box.createHorizontalGlue());
		
		/** Option to load previous photo from the server */
		JButton buttonPrevious = new JButton("Предыдущее");
		buttonPrevious.addActionListener(new ActionListener() {    
			public void actionPerformed(ActionEvent ev) {   
				takePrevPhoto();
			}
		});
		
		/** Option to load next photo from the server */
		JButton buttonNext = new JButton("Следующее");
		buttonNext.addActionListener(new ActionListener() {    
			public void actionPerformed(ActionEvent ev) {   
				takeNextPhoto();
			}
		});
		
		buttons.add(buttonPrevious);
		buttons.add(buttonNext);
		buttons.add(Box.createHorizontalGlue());
		Box contentBox = Box.createVerticalBox();
		
		contentBox.add(hboxfigure);
		contentBox.add(buttons);
		
		getContentPane().add(contentBox, BorderLayout.CENTER);
		
		NetManager.addMessageListener(new MessageListener(){
			public void messageReceived(String message) {
				/** Don't use here */
			}
			public void messageReceived(String name, String message) {
				/** Don't use here */
			}
			public void messageReceived(byte[] bytes, int bytesSize) {
				receiveFigure(bytes, bytesSize);
			}
		});
	}
	
	/** Reading photo from the local memory */
	private void openFigure(File selectedFile){
		try { 
			DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
			
			bytesFigure = new byte[in.available()];
			bytesFigureSize = in.available();
			int i=0;
			
			while (in.available()>0){
				bytesFigure[i] = in.readByte();
				i += 1;
			}
			in.close(); 
			figure.setIcon(new ImageIcon(bytesFigure));
		} catch (FileNotFoundException ex) { 
			JOptionPane.showMessageDialog(MenuFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
		} catch (IOException ex) { 
			JOptionPane.showMessageDialog(MenuFrame.this, "Ошибка чтения фотографии", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/** Saving photo to the local memory */
	private void saveFigure(File selectedFile){
		try { 
			DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
			
			for(int i=0; i<bytesFigureSize; i++){
				out.writeByte(bytesFigure[i]);
			}
			out.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MenuFrame.this, "Ошибка записи фотографии", "Ошибка сохранения данных", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/** Receiving photo from the server */
	private void receiveFigure(byte[] bytes, int bytesSize){
		bytesFigure = crypter.encryptFile(bytes, bytesSize);
		bytesFigureSize = bytesSize;
		figure.setIcon(new ImageIcon(bytesFigure));
	}
	
	/** Sending photo to the server */
	private void sendToServer(){
		int result = NetManager.send("IMPORT_PHOTO", UserName, crypter.cryptFile(UserName, UserPassword), crypter.cryptFile(bytesFigure, bytesFigureSize), bytesFigureSize);
		
		if (result==1) {
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		} else if(result==2) {
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Sending request to load next photo to the server */
	private void takeNextPhoto(){
		
		int result = NetManager.send("NEXT_PHOTO", UserName, crypter.cryptFile(UserName, UserPassword));
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		} else if(result==2) {
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Sending request to load previous photo to the server */
	private void takePrevPhoto(){
		int result = NetManager.send("PREV_PHOTO", UserName, crypter.cryptFile(UserName, UserPassword));
		
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		} else if(result==2) {
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Sending request to delete current photo from the server */
	private void deleteFromServer(){
		int result = NetManager.send("DELETE_PHOTO", UserName, crypter.cryptFile(UserName, UserPassword));
		
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		} else if(result==2) {
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** Sending request to close (turn off) the server */
	private void closeServer(){
		int result = NetManager.send("CLOSE_SERVER", UserName, crypter.cryptFile(UserName, UserPassword));
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		} else if(result==2) {
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
}

