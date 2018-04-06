package com.socket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
	
	public static final int DEBUG_ALL = 0;
	public static final int DEBUG_1 = 1;
	public static final int DEBUG_2 = 2;
	public static final int DEBUG_OFF = 100;
	
	public static int debug_level = DEBUG_OFF; // 100
	
	public static void DEBUG(int level, String str) {
		
		if (debug_level <= level) {
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA );
			System.out.println(String.format("[%s] : %s", formatter.format(new Date()), str));
		}
		
	}
	
	
}
