package bsu.rfe_g6k2.Yackou.client;

/**
 * This is an interface to sending commands-messages from NetClass to frames
 * 
 * @version No recording 28.05.2020
 * @author Vlad Yatskou
 */
public interface MessageListener{
	void messageReceived(String message);
	void messageReceived(String name, String message);
	void messageReceived(byte[] bytes, int bytesSize);
}
