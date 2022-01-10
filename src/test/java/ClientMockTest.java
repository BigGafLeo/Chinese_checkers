import com.example.trylmaproject.client.Client;
import com.example.trylmaproject.server.Game;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ClientMockTest{

    @Test
    public void testAddName(){
        ExecutorService threads = Executors.newFixedThreadPool(2);
        ClientMock mock = new ClientMock("mateusz", 59091);
        Game game = null;
        try {
            game = new Game(59091);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert game != null;
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
        ClientMock mock1 = new ClientMock("Alice", 59093);
        ClientMock mock2 = new ClientMock("Bob", 59093);
        Game game = null;
        try {
            game = new Game(59093);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert game != null;
        threads.execute(game);
        threads.execute(mock1);
        synchronized (this){
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threads.execute(mock2);

        while (true){
            synchronized (this){
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(mock1.hasEnded && mock2.hasEnded) break;
        }

        //ClientMock mock3 = new ClientMock("xd", 59090);

        assertEquals("Alice", game.getName(0));
        assertEquals("Bob", game.getName(1));
        assertTrue(mock1.moveAccepted);
        assertTrue(mock2.moveAccepted);
//        assertFalse(game.);
    }



}
