/**
 * WordCount.java
 *
 * a sample MapReduce program for counting how many times each word appears
 * in a text file
 */

import java.io.IOException;
import java.time.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

/* 
 * interfaces and classes for Hadoop data types, including 
 * some that are not needed for WordCount, but that you might
 * end up using in one of the PS 4 problems
 */
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WordCount {

    /*
     * This mapper maps each input line to a set of (word, 1) pairs, 
     * with one pair for each word in the line.
     */
    public static class MyMapper extends
        Mapper<Object, Text, Text, IntWritable> 
    {
        public void map(Object key, Text value, Context context) 
            throws IOException, InterruptedException 
        {
            // Convert the Text object for the value to a String.
            String line = value.toString();

            // Split the line on the spaces to get an array containing
            // the individual words.
            String[] words = line.split(" ");

            // Process the words one at a time, writing a key-value pair 
            // for each of them.
            for (String word : words) {
                context.write(new Text(word), new IntWritable(1));
            }
        }
    }

    /*
     * This reducer adds up the 1s for a given word and writes 
     * a (word, count) pair.
     */
    public static class MyReducer extends
        Reducer<Text, IntWritable, Text, LongWritable> 
    {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) 
             throws IOException, InterruptedException 
        {
            // Total the list of values associated with the word.
            long count = 0;
            for (IntWritable val : values) {
                count += val.get();
            }

            context.write(key, new LongWritable(count));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");

        // Specifies the name of the outer class.
        job.setJarByClass(WordCount.class);

        // Specifies the names of the mapper and reducer classes.
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

        // Sets the type for the keys output by the mapper and reducer.
        job.setOutputKeyClass(Text.class);

        // Sets the type for the values output by the mapper and reducer,
        // although we can--and do in this case--change the mapper's type below.
        job.setOutputValueClass(LongWritable.class);

        // Sets the type for the keys output by the mapper.
        // Not needed here because both the mapper and reducer's output keys 
        // have the same type, but you can uncomment it as needed
        // and pass in the appropriate type.
        //   job.setMapOutputKeyClass(Text.class);

        // Sets the type for the values output by the mapper.
        // This is needed because it is different than the type specified
        // by job.setOutputValueClass() above. 
        // If the mapper and reducer output values of the same type, 
        // you can comment out or remove this line.
        job.setMapOutputValueClass(IntWritable.class);


        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
