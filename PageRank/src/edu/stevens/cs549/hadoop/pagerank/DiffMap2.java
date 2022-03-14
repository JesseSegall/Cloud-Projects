package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DiffMap2 extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,
			IllegalArgumentException {
		String s = value.toString(); // Converts Line to a String

		/* 
		 * TODO: emit: key:"Difference" value:difference calculated in DiffRed1
		 */
		String[] diffTokens = s.split("\t+");
		double difference = Double.valueOf(diffTokens[1]);
		context.write(new Text ("Difference"), new Text (String.valueOf(difference)));
	}

}
