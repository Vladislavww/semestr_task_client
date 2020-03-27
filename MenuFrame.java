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
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.GroupLayout.Alignment;



public class MenuFrame extends JFrame {
	private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
	private static final int FRAME_MINIMUM_WIDTH = 500;
	private static final int FRAME_MINIMUM_HEIGHT = 500;
	private NetClass NetManager;
	private JFileChooser fileChooser = null; 
	private String UserName;
	private JLabel figure;
	byte[] bytesFigure;
	int bytesFigureSize;
	
	public MenuFrame(String name, NetClass NetManager){
		super(FRAME_TITLE);
		UserName = name;
		this.NetManager = NetManager;
		final Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
		setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
		JMenuBar menuBar = new JMenuBar(); 
		setJMenuBar(menuBar);
		// Добавить пункт меню "Файл" 
		JMenu fileMenu = new JMenu("Файл");
		menuBar.add(fileMenu); 
		// Создать действие по открытию файла
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
		// Добавить соответствующий элемент меню 
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
		/*Action exportFromServerAction = new AbstractAction("Загрузить выбранную фотографию на компьютер с сервера"){
			public void actionPerformed(ActionEvent event){ 
				
			}};
		fileMenu.add(exportFromServerAction);*/
		figure = new JLabel();
		Box hboxfigure = Box.createHorizontalBox();
		hboxfigure.add(Box.createHorizontalGlue());
		hboxfigure.add(figure);
		hboxfigure.add(Box.createHorizontalGlue());
		
		Box buttons = Box.createHorizontalBox();
		buttons.add(Box.createHorizontalGlue());
		JButton buttonPrevisious = new JButton("Предыдущее");
		buttonPrevisious.addActionListener(new ActionListener() {    
			public void actionPerformed(ActionEvent ev) {   
				
			}
		});
		JButton buttonNext = new JButton("Следующее");
		buttonNext.addActionListener(new ActionListener() {    
			public void actionPerformed(ActionEvent ev) {   
				
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
			public void messageReceived(LinkedList<String> message){
				//writeOnlineUsers(message);
			}
			public void messageReceived(String name, String message) {
				//ничего
			}
		});
	}
	
	private void openFigure(File selectedFile){
		try { 
			// Шаг 1 - Открыть поток чтения данных, связанный с входным файловым потоком 
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
			// В случае исключительной ситуации типа "Файл не найден" показать сообщение об ошибке
			JOptionPane.showMessageDialog(MenuFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
		} 
		catch (IOException ex){ 
			// В случае ошибки ввода из файлового потока показать сообщение об ошибке 
			JOptionPane.showMessageDialog(MenuFrame.this, "Ошибка чтения фотографии", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void saveFigure(File selectedFile){
		try { 
			// Создать новый байтовый поток вывода, направленный в указанный файл 
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
	
	private void sendToServer(){
		NetManager.send("IMPORT_PHOTO", UserName, bytesFigure, bytesFigureSize);
	}

}

