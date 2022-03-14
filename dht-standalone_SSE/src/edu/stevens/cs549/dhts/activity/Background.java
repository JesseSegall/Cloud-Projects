package edu.stevens.cs549.dhts.activity;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stevens.cs549.dhts.main.Log;
import edu.stevens.cs549.dhts.main.Main;

/*
 * Background thread performs periodic stabilization (on successor)
 * and fixes finger pointers.
 */
public class Background implements Runnable {
	
	private static final String TAG = Background.class.getCanonicalName();
	
	private static final Logger logger = Logger.getLogger(TAG);

	protected long interval;
	protected int ntimes;
	protected Main main;
	protected IDHTBackground dht;

	public Background(long msecs, int n, Main m, IDHTBackground d) {
		interval = msecs;
		ntimes = n;
		main = m;
		dht = d;
	}

	public void run() {
		try {
			while (!main.isTerminated()) {
				try {
					Thread.sleep(interval);
					Log.background(TAG, "Performing background stabilization.");
					dht.checkPredecessor();
					dht.stabilize();
					dht.fixFingers(ntimes);
				} catch (DHTBase.Failed e) {
					logger.log(Level.WARNING, "Remote failure during background processing: ", e);
				}
			}
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Exiting background thread: ", e);
		} catch (DHTBase.Error e) {
			logger.log(Level.SEVERE, "Internal error during background processing: ", e);
		}
	}

}
