package password;

import org.springframework.beans.factory.annotation.Autowired;

import utils.Encode;
import utils.Disgests;

public class TestPassword {
	@Autowired
	private	static Encrypts encrypts;
	
	private static String plain = "123123";
	
	
	public static void main(String[] args) {
		//У�� �������� �õ����� ���������������У��
		String[] passwords = hashPassword(plain,"8f9b85935ff71a85");
		System.out.println(passwords[0]);
		System.out.println(passwords[1]);
		
		//�������  �������� ��������
		
	}
	
	public static String[] hashPassword(String plain,String salt) {
		byte[] salts = Encode.decodeHex(salt);
		byte[] hash = Disgests.sha1(plain.getBytes(), salts, 1024);
		String hexHash = Encode.encodeHex(hash);
		
		return new String[]{hexHash, salt};
	}
	
	public static String hashPlain(String passWord,String salt){
		
		return "";
	}
	
}
