package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DiffRed1 extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double[] ranks = new double[2];
		/* 
		 * TODO: The list of values should contain two ranks.  Compute and output their difference.
		 */
		Iterator<Text> valueIterator = values.iterator();
		if(valueIterator.hasNext()) {
			ranks[0] = Double.valueOf(valueIterator.next().toString());
		}
		if(valueIterator.hasNext()) {
			ranks[1] = Double.valueOf(valueIterator.next().toString());
		}
		context.write(new Text (key) , new Text (String.valueOf(Math.abs(ranks[0]-ranks[1]))));
	}
}
