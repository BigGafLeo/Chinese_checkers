package com.example.trylmaprojectdatabase.database;

import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import javax.swing.*;
import java.beans.JavaBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SpringJdbcConfig {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;


    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/lab5tp");
        dataSource.setUsername("lab5");
        dataSource.setPassword("mateusz726");
        return dataSource;
    }

    public DataSource getDataSource(){
        return dataSource;
    }

    public SpringJdbcConfig(){
        dataSource = mysqlDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        //new JdbcTemplate(DriverManager.getConnection("jdbc:mysql://localhost:3306/lab5tp", "lab5@localhost", "mateusz726"));
    }

    public int createGame(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("gra").usingGeneratedKeyColumns("id_gry").usingColumns("data");
        Map<String, Object> parameters = new HashMap<String, Object>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        parameters.put("liczba_ruchów", 0);
//        parameters.put("is_started", false);
//        parameters.put("liczba_graczy", 0);
        parameters.put("data", formatter.format(new Date()));

        return jdbcInsert.executeAndReturnKey(parameters).intValue();
    }

    public void addPlayer(int id_gry, int playerNumber, String name){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("gracze");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id_gry", id_gry);
        parameters.put("id_gracza", playerNumber);
        parameters.put("nazwa", name);
        jdbcInsert.execute(parameters);
    }

    public void startGame(int id_gry){
        jdbcTemplate.update("UPDATE gra SET is_started = TRUE WHERE id_gry = ?", id_gry);
    }

    public void addMove(int id_gry, int id_gracza, String[] commandArray){
        addMove(id_gry, id_gracza, Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), Integer.parseInt(commandArray[4]));
    }

    public void addMove(int id_gry, int id_gracza, int start_x, int start_y, int end_x, int end_y){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("ruch").usingGeneratedKeyColumns("id_ruchu");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id_gry", id_gry);
        parameters.put("id_gracza", id_gracza);
        parameters.put("start_x", start_x);
        parameters.put("start_y", start_y);
        parameters.put("end_x", end_x);
        parameters.put("end_y", end_y);
        jdbcInsert.execute(parameters);
    }

    public Board getSavedBoard(int id_gry) throws IllegalNumberOfPlayers {
        int numberOfMoves = jdbcTemplate.queryForObject("SELECT liczba_ruchów FROM gra WHERE id_gry = " + id_gry, Integer.class);
        return getSavedBoard(id_gry, numberOfMoves);
    }

    public Board getSavedBoard(int id_gry, int ruch) throws IllegalNumberOfPlayers {
        int numberOfPlayers = jdbcTemplate.queryForObject("SELECT liczba_graczy FROM gra WHERE id_gry = " + id_gry, Integer.class);
        var query = "SELECT id_gracza, start_x, start_y, end_x, end_y FROM ruch WHERE id_gry = ? ORDER BY id_ruchu ASC LIMIT ?";
        return jdbcTemplate.queryForObject(query, new Object[]{id_gry, ruch}, new BoardRowMapper(numberOfPlayers));
    }
}

