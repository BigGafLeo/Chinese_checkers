package com.example.trylmaproject.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args){
        try{
            ExecutorService threads = Executors.newFixedThreadPool(1);
            threads.execute(new Game(59090));
        }
        catch(IOException exception){exception.printStackTrace();}
    }
}
