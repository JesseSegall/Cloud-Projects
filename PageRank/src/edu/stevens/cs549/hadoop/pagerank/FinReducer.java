package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FinReducer extends Reducer<DoubleWritable, Text, Text, Text> {

//	@Override
//	public void setup(Context context) {
//		try {
//			super.setup(context);
//			URI[] files = context.getCacheFiles();
//		} catch (IOException | InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public void reduce(DoubleWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		/*
		 * TODO: For each value, emit: key:value, value:-rank
		 */
		Iterator<Text> valueIterator = values.iterator();
		while (valueIterator.hasNext()) {
			context.write(new Text(valueIterator.next()), new Text(String.valueOf((-1) * key.get())));
		}
	}
}
