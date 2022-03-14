package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



public class InitMapper extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,
			IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		
		String[] tokens = line.split(":"); //adj list
		if(tokens != null && tokens.length == 2 ) {
			String node = tokens[0].trim();
			String adjList = tokens[1].trim();
			context.write(new Text(node), new Text(adjList));
		}
		
		


	}

}