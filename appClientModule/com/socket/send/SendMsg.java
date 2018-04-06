package com.socket.send;

import java.io.OutputStream;

import com.socket.cipher.AESCipher;
import com.socket.vo.BaseInfo;

public class SendMsg implements SendInterface {

	@Override
	public BaseInfo Start(BaseInfo sendInfo, OutputStream sender) {
            
//        OutputStream sender = sendInfo.getSender();
         
    	try {
			String message = sendInfo.getMessage();
			byte[] data = AESCipher.Encode(message).getBytes();
	        sender.write(data, 0, data.length);
	        sender.flush();
	        
		} catch (Throwable e) {
			e.printStackTrace();
		}
    	
    	return sendInfo;
        
	}

}
