package com.socket.send;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import com.socket.Util;
import com.socket.cipher.AESCipher;
import com.socket.vo.BaseInfo;

public class SendCipherFile implements SendInterface {

	@Override
	public BaseInfo Start(BaseInfo sendInfo, OutputStream sender) {
//        OutputStream sender = sendInfo.getSender();

    	String filename = sendInfo.getFilepath() + "/" + sendInfo.getFilename();
    	int datalen = sendInfo.getDatalen();
    	FileInputStream fin = null;
		byte[] data = new byte[1024];
		int n = 0;
    	
    	try {
	        Util.DEBUG(Util.DEBUG_ALL, String.format("send file : %s[%,d KB] ... 암호화 전송중", sendInfo.getFilename(), datalen));
        	
        	// 파일보내기
    		int k = datalen / 30;
    		if (k == 0) {
    			k = datalen;
    		}
	    	File file = new File(filename);
        	fin = new FileInputStream(file);

			CipherInputStream cout = new CipherInputStream(fin, AESCipher.getCipher(Cipher.ENCRYPT_MODE));
			int process = 0; 
        	while ((n = cout.read(data)) != -1) {
    			if (process++ % (k * 32) == 0) {
        			System.out.print(".");
    			}
    			sender.write(data, 0, n);
        	}
        	System.out.println("");
        	sender.flush();
	        Util.DEBUG(Util.DEBUG_ALL, String.format("암호화 전송완료~~ %,d Bytes", datalen));

		} catch (Throwable e) {
			e.printStackTrace();
		}
    	
    	return sendInfo;
		
	}

}
