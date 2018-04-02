/*
 * InsertRow.java
 *
 * DBMS Implementation
 */

import java.io.*;
import com.sleepycat.db.*;
import com.sleepycat.bind.*;
import com.sleepycat.bind.tuple.*;

/**
 * A class that represents a row that will be inserted in a table in a
 * relational database.
 *
 * This class contains the code used to marshall the values of the
 * individual columns to a single key-data pair in the underlying
 * BDB database.
 */
public class InsertRow {
    private Table table;         // the table in which the row will be inserted
    private Object[] values;     // the individual values to be inserted
    private DatabaseEntry key;   // the key portion of the marshalled row
    private DatabaseEntry data;  // the data portion of the marshalled row

    /**
     * Constructs an InsertRow object for a row containing the specified
     * values that is to be inserted in the specified table.
     *
     * @param  t  the table
     * @param  values  the values in the row to be inserted
     */
    public InsertRow(Table table, Object[] values) {
        this.table = table;
        this.values = values;

        // These objects will be created by the marshall() method.
        this.key = null;
        this.data = null;
    }

    /**
     * Takes the collection of values for this InsertRow
     * and marshalls them into a key/data pair.
     */
    public void marshall() {
        table = this.table;
        values = this.values;
        /* Prepare TupleOutputs for the data. */
        TupleOutput offset_buffer = new TupleOutput();
        TupleOutput key_buffer = new TupleOutput();
        TupleOutput tuple_buffer = new TupleOutput();

        int int_size = Integer.SIZE/8;
        int vals_len = values.length;

        /* Start with length of values array. */
        // tuple.writeByte(values.length);

        /* Then write the metadata for each item in values. */
        for (int i = 0; i < vals_len; i++) {
            Column col = table.getColumn(i);
            // marshall according to type
            try {
                switch (col.getType()){
                    case (Column.INTEGER):
                        // key is first item
                        if (i == 0) key_buffer.writeInt((int) values[i]);
                        // the rest are data
                        else {
                            offset_buffer.writeByte(Integer.SIZE/8 + vals_len + 1);
                            tuple_buffer.writeInt((int) values[i]);
                        }
                        break;
                    case (Column.REAL):
                        // key is first item
                        if (i == 0) key_buffer.writeDouble((double) values[i]);
                        // the rest are data
                        else {
                            offset_buffer.writeByte(Double.SIZE/8 + vals_len + 1);
                            tuple_buffer.writeDouble((double) values[i]);
                        }
                        break;
                    case (Column.CHAR):
                        int char_str_len = col.getLength();
                        String char_str = (String)values[i];
                        for (int x = 0; x < char_str_len; x++) {
                            // key is first item
                            if (i == 0) key_buffer.writeChar(char_str.charAt(x));
                            // the rest are data
                            else {
                                offset_buffer.writeByte(Character.SIZE/8 + vals_len + 1);
                                tuple_buffer.writeChar(char_str.charAt(x));
                            }
                        }
                        break;
                    case (Column.VARCHAR):
                        String vchar_str = (String)values[i];
                        char[] chars = vchar_str.toCharArray();
                        // key is first item
                        if (i == 0) key_buffer.writeBytes(chars);
                        // the rest are data
                        else {
                            offset_buffer.writeByte((chars.length * Character.SIZE/8)
                                + vals_len + 1);
                            tuple_buffer.writeBytes(chars);
                        }
                        break;
                    default:
                        throw new Exception("Invalid column type");
                    }
                } catch (Exception e) {
                    System.err.println(e);
                    return;
                }
        }

        // add end-of-tuple offset
        offset_buffer.writeByte(offset_buffer.getBufferLength()
            + tuple_buffer.getBufferLength());

        // add the tuple buffer to the offset buffer to get one buffer with
        // an offsets header + data
        TupleOutput data_buffer = offset_buffer;
        for (int j = 0; j < tuple_buffer.getBufferLength(); j++) {
            data_buffer.writeByte(tuple_buffer.getBufferBytes()[j]);
        }

        // make database entries
        DatabaseEntry key = null, data = null;
        key = new DatabaseEntry(key_buffer.getBufferBytes(), 0,
                                key_buffer.getBufferLength());
        data = new DatabaseEntry(data_buffer.getBufferBytes(), 0,
                                 data_buffer.getBufferLength());

        this.key = key;
        this.data = data;
    }

    /**
     * Returns the DatabaseEntry for the key in the key/data pair for this row.
     *
     * @return  the key DatabaseEntry
     */
    public DatabaseEntry getKey() {
        return this.key;
    }

    /**
     * Returns the DatabaseEntry for the data item in the key/data pair
     * for this row.
     *
     * @return  the data DatabaseEntry
     */
    public DatabaseEntry getData() {
        return this.data;
    }
}
