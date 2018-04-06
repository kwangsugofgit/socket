package com.socket.send;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import com.socket.Util;
import com.socket.vo.BaseInfo;

public class SendFile implements SendInterface {

	@Override
	public BaseInfo Start(BaseInfo sendInfo, OutputStream sender) {
//        OutputStream sender = sendInfo.getSender();

    	String filename = sendInfo.getFilepath() + "/" + sendInfo.getFilename();
    	int datalen = sendInfo.getDatalen();
    	FileInputStream fin = null;
		byte[] data = new byte[1024];
		int n = 0;
    	
    	try {
	        Util.DEBUG(Util.DEBUG_ALL, String.format("send file : %s[%,d KB] ... 전송중", sendInfo.getFilename(), datalen));
	    	
	    	// 파일보내기
			int k = datalen / 30;
			if (k == 0) {
				k = datalen;
			}
			int process = 0;
	    	File file = new File(filename);
	    	fin = new FileInputStream(file);
	    	while ((n = fin.read(data)) != -1) {
				if (process++ % k == 0) {
	    			System.out.print(".");
				}
	    		sender.write(data, 0, n);
	    	}
	    	System.out.println("");
	    	sender.flush();
	        Util.DEBUG(Util.DEBUG_ALL, String.format("전송완료~~"));

		} catch (Throwable e) {
			e.printStackTrace();
		}
    	
    	return sendInfo;
        
	}

}
