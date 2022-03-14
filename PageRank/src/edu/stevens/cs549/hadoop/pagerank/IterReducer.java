package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IterReducer extends Reducer<Text, Text, Text, Text> {
	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double d = PageRankDriver.DECAY; // Decay factor
		/* 
		 * TODO: emit key:node+rank, value: adjacency list
		 * Use PageRank algorithm to compute rank from weights contributed by incoming edges.
		 * Remember that one of the values will be marked as the adjacency list for the node.
		 */
		Iterator<Text> iterator = values.iterator();
		double currentRank = 0; 
		String ajacentlist = "";
		while(iterator.hasNext()) {
			String line = iterator.next().toString();
			if(!line.startsWith(PageRankDriver.ADJ_MARKER)) {
				currentRank += Double.valueOf(line);
			} else {
				ajacentlist = line.replaceAll(PageRankDriver.ADJ_MARKER, "");
			}
		}
		
		currentRank = 1 - d + currentRank * d;
		context.write(new Text(key + "+" + currentRank), new Text(ajacentlist));
	}
	
	
}
