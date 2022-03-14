package edu.stevens.cs549.dhts.resource;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.sse.SseFeature;

import edu.stevens.cs549.dhts.activity.DHTBase.Failed;

@Path("/dht")
public class NodeResource {

	/*
	 * Web service API.
	 * 
	 *
	 */

	Logger log = Logger.getLogger(NodeResource.class.getCanonicalName());

	@Context
	UriInfo uriInfo;

	@Context
	HttpHeaders headers;

	@GET
	@Produces("application/json")
	public Response getKeyBinding(@QueryParam("key") String key) {
		return createNodeService().getKeyBinding(key);
	}

	@PUT
	@Produces("application/json")
	public Response addKeyBinding(@QueryParam("key") String key, @QueryParam("val") String val) {
		return createNodeService().addKeyBinding(key, val);
	}

	@DELETE
	@Produces("application/json")
	public Response deleteKeyBinding(@QueryParam("key") String key, @QueryParam("val") String val) {
		return createNodeService().deleteKeyBinding(key, val);
	}

	@GET
	@Path("info")
	@Produces("application/json")
	public Response getNodeInfo() {
		return createNodeService().getNodeInfo();
	}

	@GET
	@Path("pred")
	@Produces("application/json")
	public Response getPred() {
		return createNodeService().getPred();
	}

	@GET
	@Path("succ")
	@Produces("application/json")
	public Response getSucc() {
		return createNodeService().getSucc();
	}

	@GET
	@Path("finger")
	@Produces("application/json")
	public Response getFinger(@QueryParam("id") String id) {
		int id2 = Integer.parseInt(id);
		return createNodeService().closestPrecedingFinger(id2);
	}

	@PUT
	@Path("notify")
	@Consumes("application/json")
	@Produces("application/json")
	/*
	 * Actually returns a TableRep
	 */
	public Response putNotify(TableRep predDb) {
		/*
		 * See the comment for WebClient::notify (the client side of this
		 * logic).
		 */
		return createNodeService().notify(predDb);
	}

	@GET
	@Path("find")
	@Produces("application/json")
	public Response findSuccessor(@QueryParam("id") String index) {
		int id = Integer.parseInt(index);
		return createNodeService().findSuccessor(id);
	}

	@GET
	@Path("listen")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public Response listenForBindings(@QueryParam("id") int id, @QueryParam("key") String key) {
		return createNodeService().listenForBindings(id, key);
	}

	private NodeService createNodeService() {
		return new NodeService(headers, uriInfo);
	}

	@DELETE
	public Response listenOff(@QueryParam("id") int id, @QueryParam("key") String key) throws Failed {

		NodeService nodeService = createNodeService();
		nodeService.stopListening(id, key);
		return nodeService.listenOff(key);
	}
}
