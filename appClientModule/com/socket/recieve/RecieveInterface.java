package com.socket.recieve;

import java.io.InputStream;

import com.socket.vo.BaseInfo;

public interface RecieveInterface {
	public BaseInfo Start(BaseInfo recieveInfo, InputStream reciever);
}

