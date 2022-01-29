package com.example.trylmaprojectdatabase.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerDB {
    public static void main(String[] args){
        try{
            ExecutorService threads = Executors.newFixedThreadPool(1);
            threads.execute(new GameDatabase(58090));
        }
        catch(IOException exception){exception.printStackTrace();}
    }
}
