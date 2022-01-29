package com.example.trylmaprojectdatabase.database;

import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BoardRowMapper implements RowMapper<Board> {
    private int numberOfPlayers;
    @Override
    public Board mapRow(ResultSet rs, int i) throws SQLException {
        try {
            Board board = new Board(numberOfPlayers);
            while(rs.next()){
                board.doMove(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5));
            }
            return board;
        } catch (IllegalNumberOfPlayers | IllegalMoveException e) {
            e.printStackTrace();
            return null;
        }

    }

    public BoardRowMapper(int numberOfPlayers){
        this.numberOfPlayers = numberOfPlayers;
    }
}
