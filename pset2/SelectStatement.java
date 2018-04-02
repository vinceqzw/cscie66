/*
 * SelectStatement.java
 *
 * DBMS Implementation
 */

import java.util.*;
import java.io.*;
import com.sleepycat.db.*;

/**
 * A class that represents a SELECT statement.
 */
public class SelectStatement extends SQLStatement {
    /* Used in the selectList for SELECT * statements. */
    public static final String STAR = "*";

    private ArrayList<Object> selectList;
    private Limit limit;
    private boolean distinctSpecified;

    /**
     * Constructs a SelectStatement object involving the specified
     * columns and other objects from the SELECT clause, the specified
     * tables from the FROM clause, the specified conditional
     * expression from the WHERE clause (if any), the specified Limit
     * object summarizing the LIMIT clause (if any), and the specified
     * value indicating whether or not we should eliminate duplicates.
     *
     * @param  selectList  the columns and other objects from the SELECT clause
     * @param  fromList  the list of tables from the FROM clause
     * @param  where  the conditional expression from the WHERE clause (if any)
     * @param  limit  summarizes the info in the LIMIT clause (if any)
     * @param  distinctSpecified  should duplicates be eliminated?
     */
    public SelectStatement(ArrayList<Object> selectList,
                           ArrayList<Table> fromList, ConditionalExpression where,
                           Limit limit, Boolean distinctSpecified)
    {
        super(fromList, new ArrayList<Column>(), where);
        this.selectList = selectList;
        this.limit = limit;
        this.distinctSpecified = distinctSpecified.booleanValue();

        /* add the columns in the select list to the list of columns */
        for (int i = 0; i < selectList.size(); i++) {
            Object selectItem = selectList.get(i);
            if (selectItem instanceof Column)
                this.addColumn((Column)selectItem);
        }
    }

    /**
     * Returns a boolean value indicating whether duplicates should be
     * eliminated in the result of this statement -- i.e., whether the
     * user specified SELECT DISTINCT.
     */
    public boolean distinctSpecified() {
        return this.distinctSpecified;
    }

    public void execute() throws DatabaseException, DeadlockException {
        TableIterator table_iter = null;
        PrintStream print_stream = System.out;
        // open table
        try {
            if (this.selectList.size() > 1) {
                throw new Exception("Selection of more than 1 column is not "
                    + "supported.");
            }
            Table table = this.getTable(0);
            if (table.open() != OperationStatus.SUCCESS)
                throw new Exception();
            if (table.getName() != getTable(0).getName()) {
                throw new Exception("Requested table \"" + getTable(0)
                    + "\" does not exist.");
            }
        // } catch (NullPointerException e) {
        //     System.err.println(e);
        //     return;
        // }
        // try {
            boolean eval_where;
            if (this.getWhere() != null) eval_where = true;
            else eval_where = false;
            table_iter = new TableIterator(this, table, eval_where);

            // look for unsupported SELECT commands
            if (this.selectList.get(0) != STAR) {
                throw new Exception("Only \"SELECT *\" is supported");
            }
            if (numTables() > 1) {
                throw new Exception("Selection from more than 1 table is not "
                    + "supported.");
            }

            // printAll appears to just print nulls forever if there's no tuples
            // to print.
            if (table_iter.numTuples() == 0) {
                // Display column names -- and compute the length of the separator.
                int separatorLen = 0;

                print_stream.println();
                for (int i = 0; i < table_iter.numColumns(); i++) {
                    Column col = table_iter.getColumn(i);
                    print_stream.print(" | " + col.getName());

                    int colWidth = col.printWidth();
                    for (int j = col.getName().length(); j < colWidth; j++)
                        print_stream.print(" ");
                    separatorLen += (colWidth + 3);
                }
                print_stream.println(" | ");
                separatorLen += 3;

                // Display the separator.
                for (int i = 0; i < separatorLen; i++)
                    print_stream.print("-");
                print_stream.println("\n");
            }
            // if there are values, printAll
            else {
                /* Sorry, I cannot figure this part out.  It just loops nulls
                forever _inside of_ printAll. I added println flags inside of
                RelationIterator.java, and the while-loop `while (next()) {`
                never stops, even when there is nothing next.  I know this is
                already being submitted late, but I'm at a loss, because it
                seems like the problem is out of my hands, in a file/function
                I'm not supposed to modify.
                 */
                }
                table_iter.printAll(print_stream);
            }
            System.out.println("Selected " + table_iter.numTuples() + " tuples.");
        } catch (Exception e) {
            // System.err.println(e);
            return;
        }
        table_iter.close();
        return;
    }
}
