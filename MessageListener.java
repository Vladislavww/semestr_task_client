package bsu.rfe_g6k2.Yackou.client;

import java.util.LinkedList;
//���������, ����� ������� ��������� ��������� ���������
public interface MessageListener{
	void messageReceived(String message);
	void messageReceived(LinkedList<String> message);
	void messageReceived(String name, String message);
}
