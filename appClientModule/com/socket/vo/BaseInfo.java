package com.socket.vo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BaseInfo {

	//서버에 연결된 순서
	private long connectedIndex = 0;
	// 메세지 고유번호
	private String uuid = "";
	// 접속 IP
	private String ip = "";
	// 접속 ID
	private String id = "";
	// Target ID
	private String targetId = "";
	// 서버에 보내는 값의 종류
	private String kind = "P"; // 받기 모드
	// 서버에 보내는 message
	private String message = "";
	// 서버에 보내는 File의 Path
	private String filepath = "";
	// 서버에 보내는 File
	private String filename = "";
	// 파일저장Path.
	private String savepath = "";
	// 파일저장명.
	private String savename = "";
	// file 크기(KB)
	private int datalen = 0;
	// 메세지 전달유무
	private boolean isTransfer = false;
	// 받은시간(Server 기준)
	private long recieveTimeMillis;
	// 보낸시간(Server 기준)
	private long sendTimeMillis;
	
	
	/**
	 * @return the connectedIndex
	 */
	public long getConnectedIndex() {
		return connectedIndex;
	}
	/**
	 * @param connectedIndex the connectedIndex to set
	 */
	public void setConnectedIndex(long connectedIndex) {
		this.connectedIndex = connectedIndex;
}
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	/**
	 * @return the targetId
	 */
	public String getTargetId() {
		return targetId;
	}
	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the filepath
	 */
	public String getFilepath() {
		return filepath;
	}
	/**
	 * @param filepath the filepath to set
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * @return the savepath
	 */
	public String getSavepath() {
		return savepath;
	}
	/**
	 * @param savepath the savepath to set
	 */
	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}
	/**
	 * @return the savename
	 */
	public String getSavename() {
		return savename;
	}
	/**
	 * @param savename the savename to set
	 */
	public void setSavename(String savename) {
		this.savename = savename;
	}
	/**
	 * @return the datalen
	 */
	public int getDatalen() {
		return datalen;
	}
	/**
	 * @param datalen the datalen to set
	 */
	public void setDatalen(int datalen) {
		this.datalen = datalen;
	}
		
	/**
	 * @return the isTransfer
	 */
	public boolean isTransfer() {
		return isTransfer;
	}
	/**
	 * @param isTransfer the isTransfer to set
	 */
	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}
	
	
	/**
	 * @return the recieveTimeMillis
	 */
	public long getRecieveTimeMillis() {
		return recieveTimeMillis;
	}
	/**
	 * @param recieveTimeMillis the recieveTimeMillis to set
	 */
	public void setRecieveTimeMillis(long recieveTimeMillis) {
		this.recieveTimeMillis = recieveTimeMillis;
	}
	/**
	 * @return the sendTimeMillis
	 */
	public long getSendTimeMillis() {
		return sendTimeMillis;
	}
	/**
	 * @param sendTimeMillis the sendTimeMillis to set
	 */
	public void setSendTimeMillis(long sendTimeMillis) {
		this.sendTimeMillis = sendTimeMillis;
	}
	/**
	 * File 유효 체크.
	 * @param filename
	 * @return
	 */
	public int fileCheck() {
		
		int n = 0;
		int datalen = 0;
    	File file = new File(filename);
		byte[] data = new byte[1024];
    	// 파일이 존재하는지 체크한다.
    	if (!file.exists()) {
    		System.out.println("File not found : " + filename);
    		return -1;
    	}
    	this.filepath = file.getParent().toString();
    	this.filename = file.getName(); // extract file name.
    	// 파일 size, 파일명 보내기.
    	try {
    		FileInputStream fin = new FileInputStream(file);
        	try {
				while ((n = fin.read(data)) != -1) {
					datalen++;
				}
	        	fin.close();
			} catch (IOException e) {
			e.printStackTrace();
				return -1;
			}
        	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
		return datalen;
	}
	public String println() {
		return "{{\"kind\",\"" + getKind() + "\"}{\"id\",\"" + getId() + "\"},{\"targetId\",\"" + getId() + "\"},{\"message\",\"" + getMessage() + "\"}}";
	}
	
}
