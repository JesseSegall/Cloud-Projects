package edu.stevens.cs549.dhts.main;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;

import edu.stevens.cs549.dhts.activity.DHTBase;
import edu.stevens.cs549.dhts.activity.NodeInfo;
import edu.stevens.cs549.dhts.resource.TableRep;
import edu.stevens.cs549.dhts.resource.TableRow;

public class WebClient {

	private static final String TAG = WebClient.class.getCanonicalName();

	private Logger logger = Logger.getLogger(TAG);

	private void error(String msg, Exception e) {
		logger.log(Level.SEVERE, msg, e);
	}

	/*
	 * Encapsulate Web client operations here.
	 * 
	 * Fill in missing operations.
	 */

	/*
	 * Creation of client instances is expensive, so just create one.
	 */
	protected Client client;

	protected Client listenClient;

	public WebClient() {
		client = ClientBuilder.newBuilder().register(ObjectMapperProvider.class).register(JacksonFeature.class).build();
		listenClient = ClientBuilder.newBuilder().register(ObjectMapperProvider.class).register(JacksonFeature.class)
				.register(SseFeature.class).build();
	}

	private void info(String mesg) {
		Log.weblog(TAG, mesg);
	}

	private Response getRequest(URI uri) {
		try {
			Response cr = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
			return cr;
		} catch (Exception e) {
			error("Exception during GET request", e);
			return null;
		}
	}

	private Response deleteRequest(URI uri) {
		try {
			Response cr = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).delete();
			return cr;
		} catch (Exception e) {
			error("Exception during DELETE request", e);
			return null;
		}

	}

	private Response putRequest(URI uri, TableRep tableRep) {
		try {
			Response cr = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE)
					.put(Entity.entity(tableRep, MediaType.APPLICATION_JSON_TYPE));
			return cr;
		} catch (Exception e) {
			error("Exception during put request", e);
			return null;
		}

	}

	private Response putRequest(URI uri) {
		try {
			Response cr = client.target(uri).request().put(Entity.text(""));
			return cr;
		} catch (Exception e) {
			error("Exception during PUT request", e);
			return null;
		}
	}

	/*
	 * Ping a remote site to see if it is still available.
	 */
	public boolean isFailed(URI base) {
		URI uri = UriBuilder.fromUri(base).path("info").build();
		Response c = getRequest(uri);
		return c.getStatus() >= 300;
	}

	/*
	 * Get the predecessor pointer at a node.
	 */
	public NodeInfo getPred(NodeInfo node) throws DHTBase.Failed {
		URI predPath = UriBuilder.fromUri(node.addr).path("pred").build();
		info("client getPred(" + predPath + ")");
		Response response = getRequest(predPath);
		if (response == null) {
			throw new DHTBase.Failed("GET /pred null response");
		} else if (response.getStatus() >= 300) {
			throw new DHTBase.Failed("GET /pred status:" + response.getStatus());
		} else {
			NodeInfo pred = response.readEntity(NodeInfo.class);
			return pred;
		}
	}

	/*
	 * Notify node that we (think we) are its predecessor.
	 */
	public TableRep notify(NodeInfo node, TableRep predDb) throws DHTBase.Failed {
		/*
		 * The protocol here is more complex than for other operations. We
		 * notify a new successor that we are its predecessor, and expect its
		 * bindings as a result. But if it fails to accept us as its predecessor
		 * (someone else has become intermediate predecessor since we found out
		 * this node is our successor i.e. race condition that we don't try to
		 * avoid because to do so is infeasible), it notifies us by returning
		 * null. This is represented in HTTP by RC=304 (Not Modified).
		 */
		NodeInfo thisNode = predDb.getInfo();
		UriBuilder ub = UriBuilder.fromUri(node.addr).path("notify");
		URI notifyPath = ub.queryParam("id", thisNode.id).build();
		info("client notify(" + notifyPath + ")");
		Response response = putRequest(notifyPath, predDb);
		if (response != null && response.getStatusInfo() == Response.Status.NOT_MODIFIED) {
			/*
			 * Do nothing, the successor did not accept us as its predecessor.
			 */
			return null;
		} else if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("PUT /notify?id=ID");
		} else {
			TableRep bindings = response.readEntity(TableRep.class);
			return bindings;
		}
	}

	public String[] get(NodeInfo node, String skey) throws DHTBase.Failed {
		UriBuilder ub = UriBuilder.fromUri(node.addr).path("");
		URI getBinding = ub.queryParam("key", skey).build();
		info("client getBinding(" + getBinding + ")");
		Response response = getRequest(getBinding);
		if (response != null && response.getStatusInfo() == Response.Status.NOT_MODIFIED) {

			return null;
		} else if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("GET /dht?key=" + skey);
		} else {
			String[] bindings = new String[1];
			bindings = response.readEntity(TableRow.class).vals;
			return bindings;
		}

	}

	public NodeInfo getInfo(NodeInfo node) throws DHTBase.Failed {
		URI infoPath = UriBuilder.fromUri(node.addr).path("info").build();
		info("client getInfo(" + infoPath + ")");
		Response response = getRequest(infoPath);
		if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("GET /info");
		} else {
			NodeInfo pred = response.readEntity(NodeInfo.class);
			return pred;
		}
	}

	public void add(NodeInfo node, String skey, String v) throws DHTBase.Failed {
		UriBuilder ub = UriBuilder.fromUri(node.addr).path("");
		URI putBinding = ub.queryParam("key", skey).queryParam("val", v).build();
		info("client notify(" + putBinding + ")");
		Response response = putRequest(putBinding);
		if (response != null && response.getStatusInfo() == Response.Status.NOT_MODIFIED) {
			/*
			 * Do nothing, the successor did not accept us as its predecessor.
			 */

		} else if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("PUT /dht?key=" + skey + "&val=" + v);

		}
	}

	public void delete(NodeInfo node, String skey, String v) throws DHTBase.Failed {
		UriBuilder ub = UriBuilder.fromUri(node.addr).path("");
		URI deleteBinding = ub.queryParam("key", skey).queryParam("val", v).build();
		info("client notify(" + deleteBinding + ")");
		Response response = deleteRequest(deleteBinding);
		if (response != null && response.getStatusInfo() == Response.Status.NOT_MODIFIED) {
			/*
			 * Do nothing, the successor did not accept us as its predecessor.
			 */

		} else if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("DELETE /dht?key=" + skey + "&val=" + v);

		}
	}

	public NodeInfo find(URI addr, int id) throws DHTBase.Failed {
		UriBuilder ub = UriBuilder.fromUri(addr).path("find");
		URI findURI = ub.queryParam("id", id).build();
		info("client findURI(" + findURI + ")");
		Response response = getRequest(findURI);
		if (response != null && response.getStatusInfo() == Response.Status.NOT_MODIFIED) {

			return null;
		} else if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("GET /dht/find?id=" + id);
		} else {
			NodeInfo bindings = response.readEntity(NodeInfo.class);
			return bindings;
		}

	}

	public NodeInfo getSucc(NodeInfo node) throws DHTBase.Failed {
		URI succPath = UriBuilder.fromUri(node.addr).path("succ").build();
		info("client getSucc(" + succPath + ")");
		Response response = getRequest(succPath);
		if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("GET /succ");
		} else {
			NodeInfo pred = response.readEntity(NodeInfo.class);
			return pred;
		}
	}

	public NodeInfo predFinger(NodeInfo node, int id) throws DHTBase.Failed {
		UriBuilder ub = UriBuilder.fromUri(node.addr).path("finger");
		URI fingerURI = ub.queryParam("id", id).build();
		info("client findURI(" + fingerURI + ")");
		Response response = getRequest(fingerURI);
		if (response != null && response.getStatusInfo() == Response.Status.NOT_MODIFIED) {

			return null;
		} else if (response == null || response.getStatus() >= 300) {
			throw new DHTBase.Failed("GET /dht/finger?id=" + id);
		} else {
			NodeInfo bindings = response.readEntity(NodeInfo.class);
			return bindings;
		}

	}

	/*
	 * Operations for listening for new bindings.
	 */
	public EventSource listenForBindings(NodeInfo node, int id, String skey) throws DHTBase.Failed {
		// listen for SSE subscription requests on
		// http://.../dht/listen?key=<key>
		// On the service side, don't expect LT request or response headers for
		// this request.
		// Note: "id" is client's id, to enable us to stop event generation at
		// the server.
		UriBuilder ub = UriBuilder.fromUri(node.addr).path("listen");
		URI listenURI = ub.queryParam("id", id).queryParam("key", skey).build();
		info("client listenURI(" + listenURI + ")");
		WebTarget target = listenClient.target(listenURI);

		return EventSource.target(target).build();
	}

	public void listenOff(NodeInfo node, int id, String skey) throws DHTBase.Failed {
		UriBuilder ub = UriBuilder.fromUri(node.addr).path("listen");
		URI listenOffURI = ub.queryParam("id", id).queryParam("skey", skey).build();
		info("client listenURI(" + listenOffURI + ")");
		deleteRequest(listenOffURI);

	}

}
