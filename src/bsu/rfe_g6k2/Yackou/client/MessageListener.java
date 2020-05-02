package bsu.rfe_g6k2.Yackou.client;

//интерфейс, чтобы сделать слушатель получения сообщений
public interface MessageListener{
	void messageReceived(String message);
	void messageReceived(String name, String message);
	void messageReceived(byte[] bytes, int bytesSize);
}
