import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import com.socket.SocketCommon;
import com.socket.Util;
import com.socket.recieve.RecieveCipherFile;
import com.socket.recieve.RecieveFile;
import com.socket.recieve.RecieveInterface;
import com.socket.recieve.RecieveMsg;
import com.socket.send.SendCipherFile;
import com.socket.send.SendFile;
import com.socket.send.SendInterface;
import com.socket.send.SendMsg;
import com.socket.vo.BaseInfo;
import com.socket.vo.HostInfo;

public class ClientMain {

	public static int endProcess = 0;
	public static String recieveId = "";
	
	public static void main(String[] args) {
		// args[0] : id
		// args[1] : 구분('F', 'M') F : 파일전송, M : Message, P : 서버에서 값 가져오기.
		// args[2] : target id
		// args[3] : 파일명, 메세지
		if (args.length < 2) {
			return;
		}
    	BaseInfo sendInfo = new BaseInfo();
    	BaseInfo recieveInfo = new BaseInfo();
		sendInfo.setId(args[0]);
		String kind = args[1];
		
		//------------------------------------------------------
		// 정기적으로 받는 메세지 체크 
		ClientThread receiveMsg = new ClientThread(sendInfo.getId());
		receiveMsg.start();
		//------------------------------------------------------
		
		sendInfo.setKind(kind);
		sendInfo.setTargetId(args[2]);
		String message = "";
		if (!"P".equals(sendInfo.getKind()) && !"A".equals(sendInfo.getKind())) {
			message = args[3];
	        // 구분에 따라서 입력값을 체크한다.
	        if ("F".equals(sendInfo.getKind()) || "C".equals(sendInfo.getKind())) {
	        	sendInfo.setFilename(message);
	        } else if ("M".equals(sendInfo.getKind())) {
	        	sendInfo.setMessage(message);
	        }
	
		}
		Util.debug_level = Util.DEBUG_2;
		Scanner scan = new Scanner(System.in);

		while (true) {
			if ("A".equals(kind)) {
				System.out.print("[" + sendInfo.getId() + "]:");
				message = scan.nextLine();
				if ("e".equals(message)) {
					endProcess = 1;
					break; //종료
				}
				sendInfo.setMessage(message);
				sendInfo.setKind("M");
				recieveInfo = new BaseInfo();
			}
	        if ("F".equals(sendInfo.getKind()) || "C".equals(sendInfo.getKind())) {
	        	int datalen = sendInfo.fileCheck();
	        	sendInfo.setDatalen(datalen);
	        }
	        // socket 통신
			while (!"B".equals(recieveInfo.getKind())) {
				recieveInfo = Communication(sendInfo);
				Util.DEBUG(Util.DEBUG_ALL, recieveInfo.println());
			}
			if (!"A".equals(kind)) {
				break;
			}
		}
        
	}
	
	public static class ClientThread extends Thread {
		BaseInfo sendInfo = new BaseInfo();
		
		public ClientThread(String id) {
			this.sendInfo.setId(id);
		}
		
		public void run() {
			this.sendInfo.setKind("P");
			while (true) {
				BaseInfo recieveInfo = new BaseInfo();
		        // socket 통신
				while (!"B".equals(recieveInfo.getKind())) {
					recieveInfo = Communication(this.sendInfo);
					Util.DEBUG(Util.DEBUG_ALL, recieveInfo.println());
				}
				try {
					Thread.sleep(1000); // 1초 마다
					if (endProcess == 1) break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
	}

	/**
	 * 1. socket 연결
	 * 2. 서버 메세지 확인.
	 * 3. 서버 메세지 출력
	 * 4. 서버에 보내기(메세지, 파일)
	 * @param sendInfo
	 */
	private static BaseInfo Communication(BaseInfo sendInfo) {
		
		BaseInfo recieveInfo = new BaseInfo();
		//1. socket 연결
        try(Socket client = new Socket()){
            //클라이언트 초기화
            InetSocketAddress ipep = new InetSocketAddress(HostInfo.Host, HostInfo.Port);
            //접속
            client.connect(ipep);
            sendInfo.setIp(client.getLocalSocketAddress().toString());
        	
            try(OutputStream sender = client.getOutputStream();
                    InputStream reciever = client.getInputStream();){
	        
            	SocketCommon sendcommon = new SocketCommon();
            	//1. 서버에 Data 보내기.
            	sendcommon.sendDataInfo(sendInfo, sender);

            	//2. 서버 메세지 확인.
            	recieveInfo = sendcommon.RecieveDataInfo(recieveInfo, reciever);

            	// 3. 서버 메세지 출력
            	if ("M".equals(recieveInfo.getKind())) {
            		//recieveInfo = Recieve(new RecieveMsg(), recieveInfo, reciever);
//        	        Util.DEBUG(Util.DEBUG_ALL, String.format(" [메세지 송신 ]"));
        	        if (!recieveId.equals(recieveInfo.getId())) {
        	        	recieveId = recieveInfo.getId(); 
        	        	System.out.println(String.format("[%s]", recieveInfo.getId()));
        	        }
        	        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.KOREA );
        	        System.out.println(String.format("%s [%s]", recieveInfo.getMessage(), formatter.format(new Date(recieveInfo.getSendTimeMillis()))));
            		
            	} else if ("F".equals(recieveInfo.getKind())) {
            		
            		// 파일 전송
            		recieveInfo.setSavepath("./download");
            		recieveInfo.setSavename(recieveInfo.getFilename());
            		Recieve(new RecieveFile(), recieveInfo, reciever);
            		
    	       	} else if ("C".equals(recieveInfo.getKind())) {
    	        	// 파일 전송
            		recieveInfo.setSavepath("./download");
            		recieveInfo.setSavename(recieveInfo.getFilename());
    	       		Recieve(new RecieveCipherFile(), recieveInfo, reciever);
        		}
        		else
        		{
        	        Util.DEBUG(Util.DEBUG_ALL, String.format("Server Message 없음."));
        			return recieveInfo;
        		}
	        	
	        	if (sendInfo.getDatalen() != -1) { // 보낼파일이 있는 경우.
	                // 파일 보내기
	        		if ("F".equals(sendInfo.getKind())) {
	        			Send(new SendFile(), sendInfo, sender);
	        		}
	                // 암호화로 파일 보내기
	        		else if ("C".equals(sendInfo.getKind())) {
	        			sendInfo.setSavepath("./download");
	        			Send(new SendCipherFile(), sendInfo, sender);
	        		}
	        	}
	        	
            }
            
        }catch(Throwable e){
            e.printStackTrace();
        }
        
        return recieveInfo;
	}
	
	private static BaseInfo Recieve(RecieveInterface recieve, BaseInfo recieveInfo, InputStream reciever) {
		return recieve.Start(recieveInfo, reciever);
		
	}


	private static BaseInfo Send(SendInterface send, BaseInfo clientInfo, OutputStream sender) {
		return send.Start(clientInfo, sender);
	}
	
	
}
