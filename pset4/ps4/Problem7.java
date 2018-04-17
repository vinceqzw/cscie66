/**
 * Problem7.java
 */

import java.io.IOException;
import java.time.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

/*
 * interfaces and classes for Hadoop data types that you may
 * need for some or all of the problems from PS 4
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


public class Problem7 {
    /*
     * One possible type for the values that the mapper should output
     */
    public static class IntArrayWritable extends ArrayWritable {
        public IntArrayWritable() {
            super(IntWritable.class);
        }

        public IntArrayWritable(IntWritable[] values) {
            super(IntWritable.class, values);
        }

        public int[] toIntArray() {
            Writable[] w = this.get();
            int[] a = new int[w.length];
            for (int i = 0; i < a.length; ++i) {
                a[i] = Integer.parseInt(w[i].toString());
            }
            return a;
        }
    }

    public static class MyMapper extends
        Mapper<Object, Text, Text, Text>
    {
        public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException
        {
            // Convert the Text object for the value to a String.
            String line = value.toString();

            int user_id = Integer.valueOf(line.split(",")[0]);

            // If there's a semicolon, then they have friend values
            int[] friends;
            String friends_str;
            int[] user_pair;
            String user_pair_str;
            if (hasFriends(line)) {
                friends = getFriends(line);

                // make friendship pair
                for (int friend : friends) {
                    if (user_id < friend) user_pair = new int[]{user_id, friend};
                    else user_pair = new int[]{friend, user_id};

                    user_pair_str = intArrToStr(user_pair);
                    friends_str = intArrToStr(friends);

                    // System.out.println("user_pair_str: " + user_pair_str);
                    // System.out.println("friends:       " + friends_str);

                    // write the pair and the main user's friends list
                    context.write(new Text(user_pair_str),
                        new Text(friends_str));
                }
            }
        }
    }


    public static class MyReducer extends
        Reducer<Text, Text, Text, Text>
    {
        public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException
        {
            String pair = key.toString();
            System.out.println("pair: " + pair);
            for (Text val : values) {
                String friends = val.toString();
                System.out.println("friends: " + friends);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "problem 7");
        job.setJarByClass(Problem7.class);


        /* CHANGE THE CLASS NAMES AS NEEDED IN THE METHOD CALLS BELOW */
        // See Problem 4.java for comments describing the calls

        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

    private static boolean hasFriends(String user_line) {
        // if the line has a semicolon, then they have friends
        if (user_line.contains(";")) return true;
        else return false;
    }

    private static int[] getFriends(String user_line) {
        // return the user's friends as an int[]

        // get friends as string array
        String[] friends_str = user_line.split(";")[1].split(",");
        // convert friends string to array of ints
        int[] friends_arr = new int[friends_str.length];
        for (int i = 0; i < friends_str.length; i++) {
            friends_arr[i] = Integer.valueOf(friends_str[i]);
        }

        return friends_arr;
    }

    private static String intArrToStr(int[] int_arr) {
        // convert [1, 2, 3] to 1,2,3
        String s = Arrays.toString(int_arr);
        s = s.replaceAll("[\\[\\] ]", "");
        return s;
    }
}
