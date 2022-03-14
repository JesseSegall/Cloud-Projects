package edu.stevens.cs549.dhts.main;

public class Log {
	
	// private static Logger logger = Logger.getLogger(Log.class.getCanonicalName());
	
	private static synchronized void say(String tag, String mesg) {
		System.out.println(tag);
		System.out.println("** " + mesg);
	}
	
	
	/*
	 * Allow logging of Web service calls (server and client side)
	 */
	private static boolean logging = false;
	
	public static void setLogging(boolean l) {
		logging = l;
	}
	
	public static void setLogging() {
		logging = !logging;
	}
	
	public static void weblog(String tag, String mesg) {
		if (logging) {
			say(tag, mesg);
		}
	}
	
	
	/*
	 * Allow logging of background processing to be turned on and off.
	 */
	private static boolean background = false;
	
	public static void setBackground() {
		background = !background;
	}

	public static void background(String tag, String mesg) {
		if (background)
			say(tag, mesg);
	}
	
	
	/*
	 * Debugging of DHT protocols.
	 */
	private static boolean debug = false;
	
	public static void setDebug() {
		debug = !debug;
	}

	public static void debug(String tag, String mesg) {
		if (debug)
			say(tag, mesg);
	}
	
}
