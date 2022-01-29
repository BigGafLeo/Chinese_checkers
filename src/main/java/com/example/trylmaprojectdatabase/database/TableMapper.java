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
        do{
            int[] temp = new int[cols];
            array.add(temp);
            for(int j = 0; j<cols; j++){
                array.get(i)[j] = rs.getInt(j+1);
            }
            i++;
        } while(rs.next());
        table = new int[i][cols];
        array.toArray(table);
        return table;
    }
}
