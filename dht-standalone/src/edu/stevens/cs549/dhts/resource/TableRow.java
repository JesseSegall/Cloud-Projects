package edu.stevens.cs549.dhts.resource;

import java.io.Serializable;

public class TableRow implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public String key;
	
	public String[] vals;
	
	public TableRow (String k, String[] vs) {
		this.key = k;
		this.vals = vs;
	}
	
	public TableRow () {
	}
	
}
