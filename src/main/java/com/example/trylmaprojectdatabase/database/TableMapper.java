package com.example.trylmaprojectdatabase.database;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TableMapper implements RowMapper<int[][]> {
    @Override
    public int[][] mapRow(ResultSet rs, int k) throws SQLException {
        int[][] table;
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        rs.last();
        int rows = rs.getRow();
        rs.beforeFirst();
        table = new int[rows][cols];
        for(int i = 1; i<= rows; i++){
            rs.next();
            for(int j = 1; j<=cols; j++){
                table[i-1][j-1] = rs.getInt(j);
            }
        }
        return table;
    }
}
