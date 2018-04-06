
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.socket.cipher.AESCipher;
import com.socket.vo.HostInfo;

public class Server {
	
	public static void main(String[] args) {
		
		//자동 close
        try(ServerSocket server = new ServerSocket()){
            // 서버 초기화
            InetSocketAddress ipep = new InetSocketAddress(HostInfo.Port);
            server.bind(ipep);
             
            System.out.println("Sever started. [port:" + HostInfo.Port + "]");
            while (true) { 
	            //LISTEN 대기
	            Socket client = server.accept();
	            System.out.println("Connection : " + client.getLocalAddress());
	            
	            Thread process = new ProcessThread(client);
	            process.start();
            }
        }catch(Throwable e){
            e.printStackTrace();
        }

	}
	
	
	private static class ProcessThread extends Thread {
		Socket client;
		
		ProcessThread(Socket client) {
			this.client = client;
		}
		
		
		public void run() {
            //send,reciever 스트림 받아오기
            //자동 close
            try(OutputStream sender = client.getOutputStream();
                InputStream reciever = client.getInputStream();) {
            	
            	long sTime = System.currentTimeMillis();

            	DataInputStream inData = new DataInputStream(reciever);

            	//Client에 연결되었다고 메세지 보내기.
                String message = AESCipher.Encode("connected ok[" + client.getLocalAddress() + "]!");
                byte[] okdata = message.getBytes();
                sender.write(okdata, 0, okdata.length);
                sender.flush();
        		String kind = inData.readUTF();
            	
            	if ("M".equals(kind)) {
	                
	                //클라이언트로부터 메시지 받기
	                byte rdata[] = new byte[4096];
	                int n = 0;
	                StringBuffer sb = new StringBuffer();
	                while ((n = reciever.read(rdata)) != -1) {
	                	sb.append(AESCipher.Decode(new String(rdata, 0, n)));
	                }
	                String out = String.format("Client[%s] : %s", client.getLocalAddress(), sb.toString());
	                //수신 메시지 출력
	                System.out.println(out);
	                //Thread.sleep(1000);
	                
            	} else if ("F".equals(kind)) {
            		// 파일 전송
            		int datalen = inData.readInt();
            		String filename = AESCipher.Decode(inData.readUTF());

            		File file = new File("./upload/" + filename);
            		FileOutputStream fout = new FileOutputStream(file);
	                System.out.println(String.format("Client[%s] File 수신 중...%s(%,d KB)", client.getLocalAddress(), filename, datalen));
            		
            		int k = datalen / 30;
            		if (k == 0) {
            			k = datalen;
            		}
            		int process = 0;
            		int n = 0;
            		byte[] data = new byte[1024];
            		while ((n = reciever.read(data)) != -1) {
            			if (process++ % k == 0) {
                			System.out.print(".");
            			}
            			fout.write(data, 0, n);
            		}
        			System.out.println("");
            		fout.close();
	                //수신 메시지 출력
	                System.out.println("Client[" + client.getLocalAddress() + "] File 수신완료~");
            		
            	} else if ("C".equals(kind)) {
            		// 파일 전송
            		int datalen = inData.readInt();
            		String filename = AESCipher.Decode(inData.readUTF());

            		File file = new File("./upload/" + filename);
            		FileOutputStream fout = new FileOutputStream(file);
	                System.out.println(String.format("Client[%s] File 암호화 수신 중...%s(%,d KB)", client.getLocalAddress(), filename, datalen));
            		
            		int k = datalen / 30;
            		if (k == 0) {
            			k = datalen;
            		}
            		
            	    CipherInputStream cin = new CipherInputStream(reciever, AESCipher.getCipher(Cipher.DECRYPT_MODE));
            	    
            		int n = 0;
            		byte[] data = new byte[1024];
            		while ((n = cin.read(data)) != -1) {
            			fout.write(data, 0, n);
            		}
            		fout.close();
	                //수신 메시지 출력
	                System.out.println("Client[" + client.getLocalAddress() + "] File 암호화 수신완료~");
            	}
            	
            	long eTime = System.currentTimeMillis();
                System.out.println("time : " + (eTime - sTime));
            } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Server() {
		super();
	}
	 	

}