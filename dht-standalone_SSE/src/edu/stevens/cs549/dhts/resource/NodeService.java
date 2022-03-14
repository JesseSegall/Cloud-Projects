package edu.stevens.cs549.dhts.resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.sse.EventOutput;

import edu.stevens.cs549.dhts.activity.DHT;
import edu.stevens.cs549.dhts.activity.DHTBase;
import edu.stevens.cs549.dhts.activity.DHTBase.Failed;
import edu.stevens.cs549.dhts.activity.DHTBase.Invalid;
import edu.stevens.cs549.dhts.activity.IDHTResource;
import edu.stevens.cs549.dhts.activity.NodeInfo;
import edu.stevens.cs549.dhts.main.Log;

/*
 * Additional resource logic.  The Web resource operations call
 * into wrapper operations here.  The main thing these operations do
 * is to call into the DHT service object, and wrap internal exceptions
 * as HTTP response codes (throwing WebApplicationException where necessary).
 * 
 * This should be merged into NodeResource, then that would be the only
 * place in the app where server-side is dependent on JAX-RS.
 * Client dependencies are in WebClient.
 * 
 * The activity (business) logic is in the dht object, which exposes
 * the IDHTResource interface to the Web service.
 */

public class NodeService {

	private static final String TAG = NodeService.class.getCanonicalName();

	private static Logger logger = Logger.getLogger(TAG);

	// add the missing operations

	HttpHeaders headers;

	IDHTResource dht;

	private void error(String mesg, Exception e) {
		logger.log(Level.SEVERE, mesg, e);
	}

	public NodeService(HttpHeaders headers, UriInfo uri) {
		this.headers = headers;
		this.dht = new DHT(uri);
	}

	private Response response(NodeInfo n) {
		return Response.ok(n).build();
	}

	private Response response(TableRep t) {
		return Response.ok(t).build();
	}

	private Response response(TableRow r) {
		return Response.ok(r).build();
	}

	private Response responseNull() {
		return Response.notModified().build();
	}

	private Response response(EventOutput e) {
		return Response.ok(e).build();
	}

	private Response response() {
		return Response.ok().build();
	}

	public Response getNodeInfo() {
		Log.weblog(TAG, "getNodeInfo()");
		return response(dht.getNodeInfo());
	}

	public Response getPred() {
		Log.weblog(TAG, "getPred()");
		return response(dht.getPred());
	}

	public Response notify(TableRep predDb) {
		Log.weblog(TAG, "notify()");
		TableRep db = dht.notify(predDb);
		if (db == null) {
			return responseNull();
		} else {
			return response(db);
		}
	}

	public Response findSuccessor(int id) {
		try {
			Log.weblog(TAG, "findSuccessor()");
			return response(dht.findSuccessor(id));
		} catch (Failed e) {
			error("findSuccessor", e);
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}
	}

	public Response getKeyBinding(String key) {
		try {
			Log.weblog(TAG, "getKeyBinding()");
			return response(new TableRow(key, dht.get(key)));
		} catch (DHTBase.Invalid e) {
			error("getKeyInfo", e);
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);

		}

	}

	public Response addKeyBinding(String key, String val) {
		try {
			Log.weblog(TAG, "addKeyBinding()");
			dht.addNet(key, val);
			return response();
		} catch (Failed e) {
			error("addKeyBinding", e);
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}

	}

	public Response deleteKeyBinding(String key, String val) {
		try {
			Log.weblog(TAG, "deleteKeyBinding()");
			dht.deleteNet(key, val);
			return response();
		} catch (Failed e) {
			error("deleteKeyBinding", e);
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}

	}

	public Response getSucc() {
		Log.weblog(TAG, "getSucc()");
		return response(dht.getSucc());

	}

	public Response closestPrecedingFinger(int id) {
		Log.weblog(TAG, "getFinger()");
		return response(dht.closestPrecedingFinger(id));

	}

	public Response listenForBindings(int id, String key) {
		Log.weblog(TAG, "listen");
		return response(dht.listenForBindings(id, key));
	}

	public Response listenOff(String key) throws Failed {
		Log.weblog(TAG, "listenOff");
		dht.listenOff(key);
		return response();
	}

	public Response stopListening(int id, String key) {
		Log.weblog(TAG, "stopListening");
		dht.stopListening(id, key);
		return response();
	}

}