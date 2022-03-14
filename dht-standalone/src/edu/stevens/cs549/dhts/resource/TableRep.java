package edu.stevens.cs549.dhts.resource;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.stevens.cs549.dhts.activity.NodeInfo;

/*
 * Model class for some of the bindings stored at a node, that may need to be transferred to
 * another node : an array of table rows
 */
public class TableRep implements Serializable {

	private static final long serialVersionUID = 1L;

	Logger log = Logger.getLogger(TableRep.class.getCanonicalName());

	public TableRow[] entry;

	/*
	 * Node id needed for transferring bindings to predecessor.
	 */
	public NodeInfo info;

	/*
	 * Successor (transmitted to pred for backup).
	 */
	public NodeInfo succ;

	public TableRep() {
		entry = new TableRow[0];
	}

	public TableRep(NodeInfo info, NodeInfo succ, List<TableRow> rows) {
		this(info, succ, rows.size());
		Iterator<TableRow> iter = rows.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			entry[i] = iter.next();
		}
	}

	public TableRep(NodeInfo info, NodeInfo succ, int nrecs) {
		entry = new TableRow[nrecs];
		this.info = info;
		this.succ = succ;
	}

	public NodeInfo getInfo() {
		return info;
	}

	public NodeInfo getSucc() {
		return succ;
	}

}
