package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DiffRed2 extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		double diff_max = 0.0; // sets diff_max to a default value
		/* 
		 * TODO: Compute and emit the maximum of the differences
		 */
		
		Iterator<Text> valueIterator = values.iterator();
		
		while(valueIterator.hasNext()) {
			double currentValue = Double.valueOf(String.valueOf(valueIterator.next()));
			if(currentValue>diff_max) {
				diff_max = currentValue;
			}
		}
		
		context.write(new Text (""), new Text (String.valueOf((diff_max))));
	}
}
