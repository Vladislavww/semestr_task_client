package bsu.rfe_g6k2.Yackou.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This class sends and receives from server data.
 * 
 * @version No recording 28.05.2020
 * @author Vlad Yatskou
 */
public class NetClass {
	
	/** Current client's port */
	private static final int CLIENT_PORT = 1560;
	
	/** Current servers's port */
	private static final int SERVER_PORT = 4512;
	
	/** Current servers's IP */
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private ArrayList<MessageListener> listeners = new ArrayList<MessageListener>(5);
	
	public NetClass(){
		
		/** Thread for checking and receiving information from the server */
		new Thread(new Runnable() {
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(CLIENT_PORT);
					
					while (!Thread.interrupted()){
						Socket socket = serverSocket.accept();
						DataInputStream in = new DataInputStream(socket.getInputStream());
						String work_type = in.readUTF();
						
						/** Result of authorization */
						if (work_type.equals("CHECK_IN")) {
							String answer = in.readUTF();
							
							notifyListeners(answer);
						}
						
						/** Result of creating new user */
						else if (work_type.equals("NEW_USER")) {
							String answer = in.readUTF();
							
							notifyListeners(answer);
						}
						
						/** Receiving photo from the server */
						else if(work_type.equals("NEXT_PHOTO") || work_type.equals("PREV_PHOTO")) {
							int bytesSize = in.readInt();
							byte[] bytes = new byte[bytesSize];
							
							for (int i=0; i<bytesSize; i++) {
								bytes[i] = in.readByte();
							}
							notifyListeners(bytes, bytesSize);
						}
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/** Sending command, login and password to the server */
	public int send(String worktype, String login, String password){
		try {
			final Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			
			final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			out.writeUTF(worktype);
			out.writeUTF(login);
			out.writeUTF(password);
			Integer port = CLIENT_PORT;
			out.writeUTF(port.toString());
			socket.close();
			return 0;
		} catch (UnknownHostException e) {
			return 1;
		} catch (IOException e) {
			return 2;
		}
	}
	
	/** Sending command, login, password and photo to the server */
	public int send(String worktype, String name, String password, byte[] bytesFigure, int bytesFigureSize){
		try {
			final Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			
			final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			out.writeUTF(worktype);
			out.writeUTF(name);
			out.writeUTF(password);
			out.writeInt(bytesFigureSize);
			for(int i=0; i<bytesFigureSize; i++){
				out.writeByte(bytesFigure[i]);
			}
			socket.close();
			return 0;
		} catch (UnknownHostException e) {
			return 1;
		} catch (IOException e){
			return 2;
		}
	}
	
	/** 4 functions for using interface "MessageListener" */
	public void addMessageListener(MessageListener listener) {
		synchronized (listeners){
			listeners.add(listener);
		}
	}
	
	public void removeMessageListener(MessageListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private void notifyListeners(String message) {
		synchronized (listeners){
			int size = listeners.size();
			for(int i=0; i<size; i++){
				listeners.get(i).messageReceived(message);
			}
		}
	}
	
	private void notifyListeners(byte[] bytes, int bytesSize) {
		synchronized (listeners){
			int size = listeners.size();
			for(int i=0; i<size; i++){
				listeners.get(i).messageReceived(bytes, bytesSize);
			}
		}
	}
}

	