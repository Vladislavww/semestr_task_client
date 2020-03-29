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
	private static final String FRAME_TITLE = "������ ���������� ���������";
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
		// �������� ����� ���� "����" 
		JMenu fileMenu = new JMenu("����");
		menuBar.add(fileMenu); 
		// ������� �������� �� �������� �����
		Action takePhotoAction = new AbstractAction("������� ����������"){
			public void actionPerformed(ActionEvent event){ 
				if (fileChooser==null){ 
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showOpenDialog(MenuFrame.this) == JFileChooser.APPROVE_OPTION){
					openFigure(fileChooser.getSelectedFile());
				}
			}};
		// �������� ��������������� ������� ���� 
		fileMenu.add(takePhotoAction);
		Action savePhotoAction = new AbstractAction("��������� ��������� ����������"){
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
		Action importToServerAction = new AbstractAction("��������� ��������� ���������� �� ������"){
			public void actionPerformed(ActionEvent event){ 
				sendToServer();
			}};
		fileMenu.add(importToServerAction);
		figure = new JLabel();
		Action deleteFromServerAction = new AbstractAction("������� ��������� ���������� � �������"){
			public void actionPerformed(ActionEvent event){ 
				deleteFromServer();
			}};
		fileMenu.add(deleteFromServerAction);
		Box hboxfigure = Box.createHorizontalBox();
		hboxfigure.add(Box.createHorizontalGlue());
		hboxfigure.add(figure);
		hboxfigure.add(Box.createHorizontalGlue());
		
		Box buttons = Box.createHorizontalBox();
		buttons.add(Box.createHorizontalGlue());
		JButton buttonPrevisious = new JButton("����������");
		buttonPrevisious.addActionListener(new ActionListener() {    
			public void actionPerformed(ActionEvent ev) {   
				takePrevPhoto();
			}
		});
		JButton buttonNext = new JButton("���������");
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
				//������
			}
			public void messageReceived(LinkedList<String> message){
				//writeOnlineUsers(message);
			}
			public void messageReceived(String name, String message) {
				//������
			}
			public void messageReceived(byte[] bytes, int bytesSize) {
				receiveFigure(bytes, bytesSize);
			}
		});
	}
	
	private void openFigure(File selectedFile){
		try { 
			// ��� 1 - ������� ����� ������ ������, ��������� � ������� �������� ������� 
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
			// � ������ �������������� �������� ���� "���� �� ������" �������� ��������� �� ������
			JOptionPane.showMessageDialog(MenuFrame.this, "��������� ���� �� ������", "������ �������� ������", JOptionPane.WARNING_MESSAGE);
		} 
		catch (IOException ex){ 
			// � ������ ������ ����� �� ��������� ������ �������� ��������� �� ������ 
			JOptionPane.showMessageDialog(MenuFrame.this, "������ ������ ����������", "������ �������� ������", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void saveFigure(File selectedFile){
		try { 
			// ������� ����� �������� ����� ������, ������������ � ��������� ���� 
			DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
			for(int i=0; i<bytesFigureSize; i++){
				out.writeByte(bytesFigure[i]);
			}
			out.close();
		} 
		catch (Exception e) {
			JOptionPane.showMessageDialog(MenuFrame.this, "������ ������ ����������", "������ ���������� ������", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void receiveFigure(byte[] bytes, int bytesSize){
		bytesFigure = bytes;
		bytesFigureSize = bytesSize;
		figure.setIcon(new ImageIcon(bytesFigure));
	}
	private void sendToServer(){
		NetManager.send("IMPORT_PHOTO", UserName, bytesFigure, bytesFigureSize);
	}
	
	private void takeNextPhoto(){
		NetManager.send("NEXT_PHOTO", UserName);
	}
	
	private void takePrevPhoto(){
		NetManager.send("PREV_PHOTO", UserName);
	}
	
	private void deleteFromServer(){
		NetManager.send("DELETE_PHOTO", UserName);
	}

}

