import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.socket.cipher.AESCipher;

public class Test {

	public static void main(String[] args) {
        Thread process = new ProcessThread("OK~~");
        process.start();
	}

	
	private static class ProcessThread extends Thread {
		
		String message = "";
		
		ProcessThread(String message) {
			this.message = message;
		}
		
		public void run() {
			
			try {
				String str = AESCipher.Encode(message);
				
				System.out.println(str);
				String str1 = AESCipher.Decode(str);
				int k = 0;
				if (k % 0 == 0) {
					System.out.println("Test....");
				}
				System.out.println(str1);
				
			} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}	
}
