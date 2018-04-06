package com.socket.recieve;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import com.socket.Util;
import com.socket.cipher.AESCipher;
import com.socket.vo.BaseInfo;

public class RecieveCipherFile implements RecieveInterface {

	@Override
	public BaseInfo Start(BaseInfo recieveInfo, InputStream reciever) {

//		InputStream reciever = recieveInfo.getReciever(); 

		try {
			int datalen = recieveInfo.getDatalen();
			String filename = recieveInfo.getSavename();

			File dir = new File(recieveInfo.getSavepath());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(recieveInfo.getSavepath() + "/" + filename);
			FileOutputStream fout = new FileOutputStream(file);
	        Util.DEBUG(Util.DEBUG_2, String.format("[%s] File 암호화 수신 중... %s (%,d KB)", recieveInfo.getId(), filename, datalen));
			
			int k = datalen / 30;
			if (k == 0) {
				k = datalen;
			}
			
		    CipherInputStream cin = new CipherInputStream(reciever, AESCipher.getCipher(Cipher.DECRYPT_MODE));
		    
		    int process = 0;
			int n = 0;
			byte[] data = new byte[1024];
			while ((n = cin.read(data)) != -1) {
				if (process++ % k == 0) {
	    			System.out.print(".");
				}
				fout.write(data, 0, n);
			}
			System.out.println("");
			fout.close();
	        //수신 메시지 출력
	        Util.DEBUG(Util.DEBUG_2, String.format("[%s] File 암호화 수신완료.  %s (%,d KB)", recieveInfo.getId(), filename, process));
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recieveInfo;

	}

}
