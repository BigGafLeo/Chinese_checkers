import com.example.trylmaproject.client.Client;
import com.example.trylmaproject.server.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerMockTest {

    @Test
    public void testWinnerCommunicat(){
        ExecutorService threads = Executors.newFixedThreadPool(3);
        try {
            threads.execute(new ServerMock(59092));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Client client1, client2;
        threads.execute((client1 = new Client("localhost", 59092)));
        client2 = new Client("localhost", 59092);
        client2.run();
    }
}
