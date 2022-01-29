package com.example.trylmaprojectdatabase.database;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class TableMapper implements RowMapper<int[][]> {
    @Override
    public int[][] mapRow(ResultSet rs, int k) throws SQLException {
        int[][] table;
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        ArrayList<int[]> array = new ArrayList<>();
        int i = 0;
        while(rs.next()){
            i++;
            int[] temp = new int[cols];
            array.add(temp);
            for(int j = 1; j<=cols; j++){
                array.get(i-1)[j-1] = rs.getInt(j);
            }
        }
        table = new int[i][cols];
        array.toArray(table);
        return table;
    }
}
