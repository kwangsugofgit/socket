package com.socket.send;

import java.io.OutputStream;

import com.socket.vo.BaseInfo;

public interface SendInterface {
	
	public BaseInfo Start(BaseInfo sendInfo, OutputStream sender);
	
}
