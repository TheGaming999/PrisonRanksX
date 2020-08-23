package me.prisonranksx.utils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MySqlStreamer {

    private DataSource dataSource;
    private Connection globalConnection;
    
    public MySqlStreamer(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public MySqlStreamer(Connection connection) {
    	this.globalConnection = connection;
    }

    public Stream<Map> streamQuery(String sql) throws SQLException {
        return new MysqlStreamQuery().stream(sql);
    }

    class MysqlStreamQuery implements Closeable {

        private Connection connection;
        private Statement statement;

        public Stream<Map> stream(String sql) throws SQLException {
        	if(dataSource != null) {
            connection = dataSource.getConnection();
        	} else {
        		connection = globalConnection;
        	}
            /*
             * MySQL ResultSets are completely retrieved and stored in memory (com.mysql.jdbc.RowDataStatic). Or
             * - Set useCursorFetch=true&defaultFetchSize=nnn in connection string (com.mysql.jdbc.RowDataCursor).
             * - Set resultSetType/resultSetConcurrency and fetchSize (Integer.MIN_VALUE) when creating statements (com.mysql.jdbc.RowDataDynamic).
             * See: https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-implementation-notes.html
             */
            /*
             * MySQL documents say nothing about cursor holdability, so not use it explicitly.
             */
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
            /* begin query */
            ResultSet rs = statement.executeQuery(sql);
            int columns = rs.getMetaData().getColumnCount();
            Map resultMap = new HashMap(columns);
            /* NOTE: Manually invoking of Stream.close() is required to close the MySQL statement and connection. */
            Stream<Map> resultStream = StreamSupport.stream(new Spliterators.AbstractSpliterator<Map>(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE) {
                @Override
                public boolean tryAdvance(Consumer<? super Map> action) {
                    try {
                        if (!rs.next()) {
                            return false;
                        }
                        resultMap.clear();
                        for (int i = 1; i <= columns; i++) {
                            resultMap.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                        }
                        action.accept(resultMap);
                        return true;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, false).onClose(() -> close());
            return resultStream;
        }

        @Override
        public void close() {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
                statement = null;
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
                connection = null;
            }
        }
    }

}
