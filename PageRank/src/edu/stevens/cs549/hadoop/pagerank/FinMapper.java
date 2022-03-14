package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



public class FinMapper extends Mapper<LongWritable, Text, DoubleWritable, Text> {

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException, IllegalArgumentException {
		String line = value.toString(); // Converts Line to a String
		/*
		 * TODO output key:-rank, value: node
		 * See IterMapper for hints on parsing the output of IterReducer.
		 */
		System.out.println("line =" + line + "++++++++++");
		String[] sectionsTokens = line.split("\t"); // node+rank - first token        adj list - second token
        String[] nodeRankTokens = sectionsTokens[0].split("\\+"); // node - first token      rank - second token
		String node = nodeRankTokens[0];
		double rank = Double.valueOf(nodeRankTokens[1]);
		context.write(new DoubleWritable ( (-1)*rank), new Text (node));
	}
}
