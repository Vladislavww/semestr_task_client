package bsu.rfe_g6k2.Yackou.client;

//���������, ����� ������� ��������� ��������� ���������
public interface MessageListener{
	void messageReceived(String message);
	void messageReceived(String name, String message);
	void messageReceived(byte[] bytes, int bytesSize);
}
