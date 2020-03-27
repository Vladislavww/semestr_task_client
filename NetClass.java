package bsu.rfe_g6k2.Yackou.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;


public class NetClass{
	private static final int CLIENT_PORT = 1560;
	private static final int SERVER_PORT = 4512;
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private ArrayList<MessageListener> listeners = new ArrayList<MessageListener>(5);
	public NetClass(){
		new Thread(new Runnable() {
			public void run(){
				try{
					final ServerSocket serverSocket = new ServerSocket(CLIENT_PORT);
					while (!Thread.interrupted()){
						final Socket socket = serverSocket.accept();
						final DataInputStream in = new DataInputStream(socket.getInputStream());
						String work_type = in.readUTF();
						if(work_type.equals("CHECK_IN")){
							String answer = in.readUTF();
							notifyListeners(answer);
						}
						else if(work_type.equals("NEW_USER")){
							String answer = in.readUTF();
							notifyListeners(answer);
						}
						else if(work_type.equals("TAKE_USERS_ONLINE")){
							LinkedList<String> usersOnline = new LinkedList<String>();
							int size = Integer.parseInt(in.readUTF());
							for(int i=0; i<size; i++){
								usersOnline.add(in.readUTF());
							}
							if(size > 0){
								notifyListeners(usersOnline);
							}
						}
						else if(work_type.equals("DIALOG")){
							final String name = in.readUTF();
							final String message = in.readUTF();
							notifyListeners(name, message);
						}
						socket.close();
					}
					
				} 
				catch (IOException e) {
				}
			}
		}).start();
	}
	
	
	//функция для отправки логина и пароля
	public int send(String worktype, String login, String password){
		try{
			final Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(worktype);
			out.writeUTF(login);
			out.writeUTF(password);
			Integer port = CLIENT_PORT;
			out.writeUTF(port.toString());
			socket.close();
			return 0;
		}
		catch (UnknownHostException e){
			e.printStackTrace();
			return 1;
		} 
		catch (IOException e){
			e.printStackTrace();
			return 2;
		}
	}
	
	
	public void send(String worktype, String name, byte[] bytesFigure, int bytesFigureSize){
		try{
			final Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(worktype);
			out.writeUTF(name);
			for(int i=0; i<bytesFigureSize; i++){
				out.writeByte(bytesFigure[i]);
			}
			socket.close();
		}
		catch (UnknownHostException e){//реагировать не нужно
		} 
		catch (IOException e){
		}
	}
	
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
	private void notifyListeners(LinkedList<String> message) {
		synchronized (listeners){
			int size = listeners.size();
			for(int i=0; i<size; i++){
				listeners.get(i).messageReceived(message);
			}
		}
	}
	
	private void notifyListeners(String name, String message) {
		synchronized (listeners){
			int size = listeners.size();
			for(int i=0; i<size; i++){
				listeners.get(i).messageReceived(name, message);
			}
		}
	}
}
