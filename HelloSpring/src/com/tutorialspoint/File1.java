package com.tutorialspoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;


public class File1 {
	public static void main(String[] args) {
		try {
			byte[] b = File1.encode1();
			System.out.println(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static byte[] encode1() throws Exception{
		String filePath = "G:/��ά��/qrCode.png";
        File file = new File(filePath);
        //���ָ���ļ���byte[]
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1000];
        //public void write(byte []b, int of, int len) ��ָ���ֽ������д�ƫ���� off ��ʼ�� len ���ֽ�д����ֽ������������
        int n;
        while((n=in.read(b))!=-1){
        	int len = in.read(b);
        	System.out.println(len);
        	out.write(b, 0, n);
        }
        System.out.println(out);
        in.close();
        out.close();
        
        return out.toByteArray();
    }
}
