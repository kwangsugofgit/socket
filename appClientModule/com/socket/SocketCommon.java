package com.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.socket.cipher.AESCipher;
import com.socket.vo.BaseInfo;

public class SocketCommon {

	/**
	 * 서버에 정보보내기
	 * @param sendInfo
	 */
	public void sendDataInfo(BaseInfo sendInfo, OutputStream sender) {

    	DataOutputStream outData = new DataOutputStream(sender);
        
    	try {
        	outData.writeUTF(AESCipher.Encode(sendInfo.getId()));
        	outData.writeUTF(AESCipher.Encode(sendInfo.getTargetId()));
        	outData.writeUTF(sendInfo.getKind());    // 메세지 종류
        	outData.writeUTF(AESCipher.Encode(sendInfo.getMessage())); // 파일명
        	outData.writeInt(sendInfo.getDatalen()); // 파일 size
        	outData.writeUTF(AESCipher.Encode(sendInfo.getFilename())); // 파일명
        	outData.writeLong(sendInfo.getSendTimeMillis());
        	outData.writeLong(sendInfo.getRecieveTimeMillis());
	    	outData.flush();
	        
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Client에서 보낸 기본 Data를 세팅한다.
	 * @param recieveInfo
	 * @return
	 */
	public BaseInfo RecieveDataInfo(BaseInfo recieveInfo, InputStream reciever) {
		
    	DataInputStream inData = new DataInputStream(reciever);
    	try {
			recieveInfo.setId(AESCipher.Decode(inData.readUTF()));
			recieveInfo.setTargetId(AESCipher.Decode(inData.readUTF()));
			recieveInfo.setKind(inData.readUTF());
			recieveInfo.setMessage(AESCipher.Decode(inData.readUTF()));
        	recieveInfo.setDatalen(inData.readInt());
			recieveInfo.setFilename(AESCipher.Decode(inData.readUTF()));
			recieveInfo.setSendTimeMillis(inData.readLong());
			recieveInfo.setRecieveTimeMillis(inData.readLong());
			
    	} catch (Throwable e) {
			e.printStackTrace();
		}
		return recieveInfo;
	}

	
	
}
