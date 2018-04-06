import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Predicate;

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

public class ServerMain {
	
	static List<BaseInfo> datas = new ArrayList<BaseInfo>();
	static long cntOfConnect = 0;

	public static void main(String[] args) {
		
		setPropertiesLoad();
		
		//자동 close
        try(ServerSocket server = new ServerSocket()){
            // 서버 초기화
            InetSocketAddress ipep = new InetSocketAddress(HostInfo.Port);
            server.bind(ipep);
             
	        Util.DEBUG(Util.DEBUG_ALL, String.format("Sever started. [port:%d]", HostInfo.Port));
            while (true) { 
	            //LISTEN 대기
	            Socket client = server.accept();
		        Util.DEBUG(Util.DEBUG_2, String.format("Connection address = [%s]", client.getLocalAddress()));
	            
	            Thread process = new ProcessThread(client);
	            process.start();
            }
            
        }catch(Throwable e){
            e.printStackTrace();
        }

	}

	private static void setPropertiesLoad() {
		// 프로퍼티 파일 위치
//        String propFile = "D:\\work\\soket\\config.properties";
        String propFile = "./config.properties";
         
        // 프로퍼티 객체 생성
        Properties props = new Properties();
         
        // 프로퍼티 파일 스트림에 담기
        FileInputStream fis;
		try {
			fis = new FileInputStream(propFile);
	        // 프로퍼티 파일 로딩
	        props.load(new java.io.BufferedInputStream(fis));
	        // 항목 읽기
	        String Level = props.getProperty("DEBUG_LEVEL").trim();
	        Util.debug_level = Integer.parseInt(Level);
	        System.out.println("config info : " + propFile);
	        System.out.println("");
	        System.out.println("DEBUG_LEVEL = " + Util.debug_level);
	        System.out.println("");
	         
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        		
	}

	private static class ProcessThread extends Thread {
		Socket client;
		
		ProcessThread(Socket client) {
			this.client = client;
		}
		
		
		public void run() {
			
            //send, reciever 스트림 받아오기
            //자동 close
           try(OutputStream sender = client.getOutputStream();
                InputStream reciever = client.getInputStream();) {
            	
            	long sTime = System.currentTimeMillis();
    			cntOfConnect++;

    			BaseInfo recieveinfo = new BaseInfo();
    			recieveinfo.setConnectedIndex(cntOfConnect);

            	SocketCommon socketCommon = new SocketCommon();

            	// Client에서 보낸 기본데이터 세팅
            	recieveinfo = socketCommon.RecieveDataInfo(recieveinfo, reciever);
    			recieveinfo.setSendTimeMillis(System.currentTimeMillis()); //Client에서 받은 시간
            	String id = recieveinfo.getId();
            	
            	Predicate<BaseInfo> filterId = e -> (!e.isTransfer()) && e.getTargetId().equals(id);
    			Optional<BaseInfo> baseinfo = datas.stream().filter(filterId).findFirst();
    			
    			BaseInfo sendinfo = baseinfo.isPresent() ? baseinfo.get() : new BaseInfo();
    			if (baseinfo.isPresent()) {
    				
    				sendinfo.setTransfer(true);
	            	Util.DEBUG(Util.DEBUG_2, String.format("find[%d]", sendinfo.getConnectedIndex()));
	            	sendinfo.println();
//	            	String uuid = sendinfo.getUuid();
//	    			datas = datas.stream().filter(e -> !e.getUuid().equals(uuid)).collect(Collectors.toList());
	    			
    			} else {
    				
	    			sendinfo.setKind("B");
	    			sendinfo.setDatalen(0);
	    			sendinfo.setMessage("connected ok[" + client.getLocalAddress() + "]!");
            	}
            	// Client에 Data기본정보 보내기
    			sendinfo.setRecieveTimeMillis(System.currentTimeMillis()); //Client에 보낸 시간
            	socketCommon.sendDataInfo(sendinfo, sender);
    			
            	//Client에 연결되었다고 메세지 보내기.
            	sendinfo = ClientSendMsg(sender, sendinfo);

            	// Client에서 데이터 받기.
            	if (!"P".equals(recieveinfo.getKind())) { //P. Server 처리 로직 업음.
            		
	            	recieveinfo.setUuid(UUID.randomUUID().toString());
	            	
	            	recieveinfo = ClientRecieve(reciever, recieveinfo);
	
	            	// client에서 보내준 메세지를 쌓아 놓는다.
            	    datas.add(recieveinfo);
	            	Util.DEBUG(Util.DEBUG_2, String.format("add[%d], count[%d]", recieveinfo.getConnectedIndex(), datas.size()));
	            	recieveinfo.println();
            	}
            	
            	long eTime = System.currentTimeMillis();
		        Util.DEBUG(Util.DEBUG_2, String.format("time : %,d", eTime - sTime));
 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		/**
		 * Client 메세지 보내기
		 * @param sender
		 */
		private BaseInfo ClientSendMsg(OutputStream sender, BaseInfo sendinfo) {
			
            if ("M".equals(sendinfo.getKind())) {
            	// Client에 메세지 보내기
    			//Send(new SendMsg(), sendinfo, sender);
    		}
            // 파일 보내기
    		else if ("F".equals(sendinfo.getKind())) {
    			sendinfo.setFilepath(sendinfo.getSavepath());  // ./upload/[id]
    			sendinfo.setFilename(sendinfo.getSavename());  // [uuid]
    			Send(new SendFile(), sendinfo, sender);
    		}
            // 암호화로 파일 보내기
    		else if ("C".equals(sendinfo.getKind())) {
    			sendinfo.setFilepath(sendinfo.getSavepath());  // ./upload/[id]
    			sendinfo.setFilename(sendinfo.getSavename());  // [uuid]
    			Send(new SendCipherFile(), sendinfo, sender);
    		}
			
			return sendinfo;
			
		}

		/**
		 * Client에서 데이터 받기.
		 * @param reciever
		 */
		private BaseInfo ClientRecieve(InputStream reciever, BaseInfo recieveInfo) {

        	if ("M".equals(recieveInfo.getKind())) {
                
                //클라이언트로부터 메시지 받기
        		//Recieve(new RecieveMsg(), recieveInfo, reciever);
        		
        	} else if ("F".equals(recieveInfo.getKind())) {
        		
        		// 파일 전송
        		recieveInfo.setSavepath("./upload/" + recieveInfo.getTargetId());
        		recieveInfo.setSavename(recieveInfo.getUuid());
        		Recieve(new RecieveFile(), recieveInfo, reciever);
        		
	       	} else if ("C".equals(recieveInfo.getKind())) {
	        	// 파일 전송
        		recieveInfo.setSavepath("./upload/" + recieveInfo.getTargetId());
        		recieveInfo.setSavename(recieveInfo.getUuid());
	       		Recieve(new RecieveCipherFile(), recieveInfo, reciever);
	       	}
        	
        	return recieveInfo;
		}

		private static void Send(SendInterface send, BaseInfo sendInfo, OutputStream sender) {
			send.Start(sendInfo, sender);
		}
		
		
		private static void Recieve(RecieveInterface recieve, BaseInfo recieveInfo, InputStream reciever) {
			recieve.Start(recieveInfo, reciever);
		}
		
		
	}
	
	
}
