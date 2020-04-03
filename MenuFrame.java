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
import java.util.LinkedList;
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



public class MenuFrame extends JFrame {
	private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	private NetClass NetManager;
	private JFileChooser fileChooser = null; 
	private String UserName;
	private JLabel figure;
	private CryptClass crypter;
	//данные для фотографии, отображаемой на экране
	byte[] bytesFigure;
	int bytesFigureSize;
	
	public MenuFrame(String name, NetClass NetManager){
		super(FRAME_TITLE);
		UserName = name;
		this.NetManager = NetManager;
		final Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
		setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		crypter = new CryptClass();
		JMenuBar menuBar = new JMenuBar(); 
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("Файл");
		menuBar.add(fileMenu); 
		Action takePhotoAction = new AbstractAction("Выбрать фотографию"){
			public void actionPerformed(ActionEvent event){ 
				if (fileChooser==null){ 
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showOpenDialog(MenuFrame.this) == JFileChooser.APPROVE_OPTION){
					openFigure(fileChooser.getSelectedFile());
				}
		}};
		fileMenu.add(takePhotoAction);
		Action savePhotoAction = new AbstractAction("Сохранить выбранную фотографию"){
			public void actionPerformed(ActionEvent event){ 
				if (fileChooser==null){ 
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showSaveDialog(MenuFrame.this) == JFileChooser.APPROVE_OPTION){
					saveFigure(fileChooser.getSelectedFile());
				}
		}};
		fileMenu.add(savePhotoAction);
		Action importToServerAction = new AbstractAction("Загрузить выбранную фотографию на сервер"){
			public void actionPerformed(ActionEvent event){ 
				sendToServer();
		}};
		fileMenu.add(importToServerAction);
		figure = new JLabel();
		Action deleteFromServerAction = new AbstractAction("Удалить выбранную фотографию с сервера"){
			public void actionPerformed(ActionEvent event){ 
				deleteFromServer();
		}};
		fileMenu.add(deleteFromServerAction);
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
		JButton buttonPrevisious = new JButton("Предыдущее");
		buttonPrevisious.addActionListener(new ActionListener() {    
			public void actionPerformed(ActionEvent ev) {   
				takePrevPhoto();
			}
		});
		JButton buttonNext = new JButton("Следующее");
		buttonNext.addActionListener(new ActionListener() {    
			public void actionPerformed(ActionEvent ev) {   
				takeNextPhoto();
			}
		});
		buttons.add(buttonPrevisious);
		buttons.add(buttonNext);
		buttons.add(Box.createHorizontalGlue());
		
		Box contentBox = Box.createVerticalBox();
		contentBox.add(hboxfigure);
		contentBox.add(buttons);
		getContentPane().add(contentBox, BorderLayout.CENTER);
		
		NetManager.addMessageListener(new MessageListener(){
			public void messageReceived(String message) {
				//ничего
			}
			public void messageReceived(String name, String message) {
				//ничего
			}
			public void messageReceived(byte[] bytes, int bytesSize) {
				receiveFigure(bytes, bytesSize);
			}
		});
	}
	
	//Прочитать фотографию с памяти ПК
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
		} 
		catch (FileNotFoundException ex){ 
			JOptionPane.showMessageDialog(MenuFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
		} 
		catch (IOException ex){ 
			JOptionPane.showMessageDialog(MenuFrame.this, "Ошибка чтения фотографии", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	//Сохранить фотографию в ПК
	private void saveFigure(File selectedFile){
		try { 
			DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
			for(int i=0; i<bytesFigureSize; i++){
				out.writeByte(bytesFigure[i]);
			}
			out.close();
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(MenuFrame.this, "Ошибка записи фотографии", "Ошибка сохранения данных", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	//Получение фотографии с сервера
	private void receiveFigure(byte[] bytes, int bytesSize){
		bytesFigure = crypter.encryptFile(bytes, bytesSize);
		bytesFigureSize = bytesSize;
		figure.setIcon(new ImageIcon(bytesFigure));
	}
	private void sendToServer(){
		int result = NetManager.send("IMPORT_PHOTO", UserName, crypter.cryptFile(bytesFigure, bytesFigureSize), bytesFigureSize);
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		}
		else if(result==2){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void takeNextPhoto(){
		int result = NetManager.send("NEXT_PHOTO", UserName);
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		}
		else if(result==2){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void takePrevPhoto(){
		int result = NetManager.send("PREV_PHOTO", UserName);
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		}
		else if(result==2){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void deleteFromServer(){
		int result = NetManager.send("DELETE_PHOTO", UserName);
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		}
		else if(result==2){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void closeServer(){
		int result = NetManager.send("CLOSE_SERVER", UserName);
		if(result==1){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
		}
		else if(result==2){
			JOptionPane.showMessageDialog(MenuFrame.this,"Не удалось отправить запрос", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}

}

