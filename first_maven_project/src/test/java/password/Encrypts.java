package password;



import password.Constants;
import utils.Disgests;
import utils.Encode;


public class Encrypts {
	
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_ITERATIONS = 1024;
	private static final int SALT_SIZE = 8;
	
	private Encrypts(){}
	
	

	/**
	 * ���ɰ�ȫ��ϣ��. 
	 * @param plain ��������
	 * @return string[0]:HASH��ȫ���룬string[1]:SALTֵ.
	 */
	public static String[] hashPassword(String plain) {
		byte[] salt = Disgests.generateSalt(SALT_SIZE);
		String hexSalt = Encode.encodeHex(salt);

		byte[] hash = Disgests.sha1(plain.getBytes(), salt, HASH_ITERATIONS);
		String hexHash = Encode.encodeHex(hash);
		
		return new String[]{hexHash, hexSalt};
	}
	
	/**
	 * 
	 * <���Ľ���><���ܾ���ʵ��>
	 *
	 * @create��2016��11��29�� ����10:10:51
	 * @author��zzq
	 * @param plain
	 * @return
	 */
	public static String encodePassword(String plain) {
		return hashPassword(plain, Constants.SALT);
	}
	
	/**
	 * 
	 * <���Ľ���><���ܾ���ʵ��>
	 *
	 * @create��2017��1��25�� ����4:52:33
	 * @author��zzq
	 * @param plain
	 * @param salt
	 * @return
	 */
	public static String hashPassword(String plain, String salt) {
		byte[] saltBytes = Encode.decodeHex(salt);
		byte[] hash = Disgests.sha1(plain.getBytes(), saltBytes, HASH_ITERATIONS);
		return Encode.encodeHex(hash);
	}
	
}
