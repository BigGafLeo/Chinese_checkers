import com.example.trylmaproject.client.Client;
import com.example.trylmaproject.server.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        threads.execute(new Client("localhost", 59092));
        new Client("localhost", 59092).run();
    }
}
