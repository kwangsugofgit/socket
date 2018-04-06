
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import com.socket.cipher.AESCipher;
import com.socket.send.SendInterface;
import com.socket.vo.BaseInfo;
import com.socket.vo.HostInfo;

public class Client {
	
	public static void main(String[] args) {
		// args[0] : 구분('F', 'M') F : 파일전송, M : Message
		// args[1] : 파일명, 메세지
		if (args.length < 2) {
			return;
		}
		String kind = args[0];
		String message = args[1];

    	BaseInfo clientInfo = new BaseInfo();

        clientInfo.setId("kwangsug");
        clientInfo.setKind(kind);
        
        // 구분에 따라서 입력값을 체크한다.
        if ("F".equals(kind) || "C".equals(kind)) {
			clientInfo.setFilename(message);
        	int datalen = clientInfo.fileCheck();
        	clientInfo.setDatalen(datalen);
        	if (datalen < 0) return;
        } else {
			clientInfo.setMessage(message);
        }
        
        if ("M".equals(kind)) {
			sendMsg(clientInfo);
		}
		else if ("F".equals(kind)) {
			sendFile(clientInfo);
		}
		else if ("C".equals(kind)) {
			sendCipherFile(clientInfo);
		}
        
	}

	
	/** 
	 * 암호화 File 전송 
	 * */
	private static void sendCipherFile(BaseInfo clientInfo) {
		
		// 서버에 보낼 파일
		String filename = clientInfo.getFilename();
		byte[] data = new byte[1024];
    	int n = 0;
    	
    	File file = new File(filename);
    	FileInputStream fin = null;
    	int datalen = clientInfo.getDatalen();
    	
		//자동 close
        try(Socket client = new Socket()){
        	
            //클라이언트 초기화
            InetSocketAddress ipep = new InetSocketAddress(HostInfo.Host, HostInfo.Port);
            //접속
            client.connect(ipep);
            clientInfo.setIp(client.getLocalSocketAddress().toString());
            
            try(OutputStream sender = client.getOutputStream();
                InputStream reciever = client.getInputStream();){

            	DataOutputStream outData = new DataOutputStream(sender);

            	//서버로부터 데이터 받기
                n = reciever.read(data);
                 
                //수신메시지 출력
                String serverMsg = new String(data, 0, n);
                String out = String.format("Server MSG : %s", AESCipher.Decode(serverMsg));
                System.out.println(out);
                 
            	outData.writeUTF("C");

            	filename = file.getName();
            	outData.writeInt(datalen);
            	outData.writeUTF(AESCipher.Encode(filename));
            	outData.flush();
            	System.out.println(String.format("send file : %s[%,d KB] ... 암호화 전송중", filename, datalen));
            	
            	// 파일보내기
        		int k = datalen / 30;
        		if (k == 0) {
        			k = datalen;
        		}
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
            	System.out.println(String.format("암호화 전송완료~~ %,d Bytes", datalen));
               	
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    	
		
	}

	/**
	 * 파일 전송
	 * @param filename
	 */
	private static void sendFile(BaseInfo clientInfo) {

		// 서버에 보낼 파일
		String filename = clientInfo.getFilename();
		byte[] data = new byte[1024];
    	FileInputStream fin = null;
    	int n = 0;
    	int datalen = clientInfo.getDatalen();
    	
		//자동 close
        try(Socket client = new Socket()){
        	
            //클라이언트 초기화
            InetSocketAddress ipep = new InetSocketAddress(HostInfo.Host, HostInfo.Port);
            //접속
            client.connect(ipep);
            clientInfo.setIp(client.getLocalSocketAddress().toString());
            
            try(OutputStream sender = client.getOutputStream();
                InputStream reciever = client.getInputStream();){

            	DataOutputStream outData = new DataOutputStream(sender);
            	//서버로부터 데이터 받기
                n = reciever.read(data);
                 
                //수신메시지 출력
                String serverMsg = new String(data, 0, n);
                String out = String.format("Server MSG : %s", AESCipher.Decode(serverMsg));
                System.out.println(out);
                 
            	outData.writeUTF("F");

            	File file = new File(filename);
            	filename = file.getName();
            	outData.writeInt(datalen);
            	outData.writeUTF(AESCipher.Encode(filename));
            	outData.flush();
            	System.out.println(String.format("send file : %s[%,d KB] ... 전송중", filename, datalen));
            	
            	// 파일보내기
        		int k = datalen / 30;
        		if (k == 0) {
        			k = datalen;
        		}
        		int process = 0;
            	fin = new FileInputStream(file);
            	while ((n = fin.read(data)) != -1) {
        			if (process++ % k == 0) {
            			System.out.print(".");
        			}
            		sender.write(data, 0, n);
            	}
            	System.out.println("");
            	sender.flush();
            	System.out.println("전송완료~~");
            
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    	
    	
		
	}

	/**
	 * 메세지 보내기
	 * @param message
	 */
	private static void sendMsg(BaseInfo clientInfo) {
        
		String message = clientInfo.getMessage();
		byte[] data = new byte[1024];
		//자동 close
        try(Socket client = new Socket()){
        	
            //클라이언트 초기화
            InetSocketAddress ipep = new InetSocketAddress(HostInfo.Host, HostInfo.Port);
            //접속
            client.connect(ipep);
            clientInfo.setIp(client.getLocalSocketAddress().toString());
            
            try(OutputStream sender = client.getOutputStream();
                InputStream reciever = client.getInputStream();){

            	DataOutputStream outData = new DataOutputStream(sender);
            	//서버로부터 데이터 받기
                int n = reciever.read(data);
                 
                //수신메시지 출력
                String serverMsg = new String(data, 0, n);
                String out = String.format("Server MSG : %s", AESCipher.Decode(serverMsg));
                System.out.println(out);
                 
            	outData.writeUTF("M");
                //서버로 데이터 보내기
            	outData.flush();
                data = AESCipher.Encode(message).getBytes();
                sender.write(data, 0, data.length);
                sender.flush();
            
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
		
	}

}
