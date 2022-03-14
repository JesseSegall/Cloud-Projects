package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DiffMap1 extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,
			IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		String[] sections = line.split("\t"); // Splits each line
		if (sections.length > 2) // checks for incorrect data format
		{
			throw new IOException("Incorrect data format");
		}
		/**
		 *  TODO: read node-rank pair and emit: key:node, value:rank
		 */
		String[] sectionsTokens = sections[0].split("\\+");
		String node = sectionsTokens[0].trim();
		double rank = Double.valueOf(sectionsTokens[1]);
		context.write(new Text (node), new Text (String.valueOf(rank)));
	}

}
