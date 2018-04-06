package com.socket.recieve;

import java.io.InputStream;

import com.socket.Util;
import com.socket.cipher.AESCipher;
import com.socket.vo.BaseInfo;

public class RecieveMsg implements RecieveInterface {

	@Override
	public BaseInfo Start(BaseInfo recieveInfo, InputStream reciever) {
    	
//		InputStream reciever = recieveInfo.getReciever(); 
		byte[] data = new byte[1024];
    	//서버로부터 데이터 받기
        int n = 0;
		try {
			n = reciever.read(data);
	        //수신메시지 출력
	        String serverMsg = new String(data, 0, n);
	        recieveInfo.setMessage(AESCipher.Decode(serverMsg));
	        
	        Util.DEBUG(Util.DEBUG_2, String.format("Recieve message : %s", recieveInfo.getMessage()));
	        
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return recieveInfo;

	}

}
