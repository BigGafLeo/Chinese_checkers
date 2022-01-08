import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Game;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientMockTest{

    @Test
    public void testAddName(){
        ExecutorService threads = Executors.newFixedThreadPool(2);
        ClientMock mock = new ClientMock("mateusz");
        Game game = null;
        try {
            game = new Game(59090);
        } catch (IOException e) {
            e.printStackTrace();
        }
        threads.execute(game);
        threads.execute(mock);


        synchronized (this){
            try {
                this.wait(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals("mateusz", game.getName(0));
    }

    @Test
    public void testMultiplePlayersWithResponse(){
        ExecutorService threads = Executors.newFixedThreadPool(3);
        ClientMock mock1 = new ClientMock("Alice");
        ClientMock mock2 = new ClientMock("Bob");
        Game game = null;
        try {
            game = new Game(59090);
        } catch (IOException e) {
            e.printStackTrace();
        }
        threads.execute(game);
        threads.execute(mock1);
        synchronized (this){
            try {
                this.wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threads.execute(mock2);

        synchronized (this){
            try {
                this.wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEquals("Alice", game.getName(0));
        assertEquals("Bob", game.getName(1));
        assertTrue(mock1.moveAccepted);
    }



}
