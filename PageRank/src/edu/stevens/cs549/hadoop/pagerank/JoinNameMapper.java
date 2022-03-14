package edu.stevens.cs549.hadoop.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class JoinNameMapper extends Mapper<LongWritable, Text, TextPair, Text> {

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		String line = value.toString(); // Converts Line to a String
		String[] sections = line.split(": "); // Splits it into two parts. Part 1: node | Part 2: name

		context.write(new TextPair(sections[0], "0"), new Text(sections[1]));

	}
}
