package bsu.rfe_g6k2.Yackou.client;

import java.util.Random;

/**
 * This class crypts and encrypts photos and passwords
 * 
 * @version No recording 28.05.2020
 * @author Vlad Yatskou
 */
public class CryptClass {
	public CryptClass(){}
	
	/** Function to crypt photo */
	public byte[] cryptFile(byte[] bytes, int bytesSize){
		int num = bytesSize/10;
		
		/** random is a variable for getting pseudorandom number */
		Random random = new Random(bytes[num]);
		
		for (int i=0; i<num; i++) {
			bytes[i] += random.nextInt()%256;
		}
		for (int i=num+1; i<bytesSize; i++) {
			bytes[i] += random.nextInt()%256;
		}
		return bytes;
	}
	
	/** Function that crypts password (toCrypt) with help of user's login (key) */
	public String cryptFile(String key, String toCrypt){
		int sum = 0;
		
		for (int i=0; i<key.length(); i++) {
			sum += key.charAt(i);
		}
		Random random = new Random(sum);
		int num;
		char[] toreturn = new char[toCrypt.length()];
		
		for (int i=0; i<toCrypt.length(); i++) {
			num = toCrypt.charAt(i);
			num += random.nextInt()%256;
			toreturn[i] = (char)num;
		}
		return new String(toreturn);
	}
	
	/** Function to encrypt photo */
	public byte[] encryptFile(byte[] bytes, int bytesSize){
		int num = bytesSize/10;
		
		Random random = new Random(bytes[num]);
		
		for (int i=0; i<num; i++) {
			bytes[i] -= random.nextInt()%256;
		}
		for (int i=num+1; i<bytesSize; i++) {
			bytes[i] -= random.nextInt()%256;
		}
		return bytes;
	}
}
