package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class InitReducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
		Iterator<Text> valueIterator = values.iterator();
		while(valueIterator.hasNext()) {
			context.write(new Text(key+"+"+1),valueIterator.next());
		}
	}
}
