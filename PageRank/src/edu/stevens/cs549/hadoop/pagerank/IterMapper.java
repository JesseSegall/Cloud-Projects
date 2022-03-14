package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



public class IterMapper extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,
			IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		String[] sections = line.split("\t"); // Splits it into two parts. Part 1: node+rank | Part 2: adj list

		if (sections.length > 2) // Checks if the data is in the incorrect format
		{
			throw new IOException("Incorrect data format");
		}
		if (sections.length != 2) {
			return;
		}
		
		
		String[] sectionsTokens = sections[0].split("\\+");
		String node = sectionsTokens[0];
		double rank = Double.valueOf(sectionsTokens[1]);
		String[] adjList = sections[1].toString().trim().split(" ");
		double computedWeight = 1.0*rank/adjList.length;
		for(int i = 0; i < adjList.length; i++ ) {
			context.write(new Text (adjList[i]), new Text (String.valueOf(computedWeight)));
		}
		context.write(new Text (node), new Text (PageRankDriver.ADJ_MARKER + sections[1]));// added to differentiate line 33 context which is node and rank and 35 which is node and list
	}

}
