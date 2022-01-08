import com.example.trylmaproject.client.BoardGuiFrame;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class BoardGuiFrameTest {

    @Test
    public void testGUI(){
        Board board = null;
        try {
            board = new Board(2);
        } catch (IllegalNumberOfPlayers e) {
            e.printStackTrace();
        }
        BoardGuiFrame boardGuiFrame = new BoardGuiFrame(2, board.getBoard());
        boardGuiFrame.setDefaultCloseOperation(3);
        boardGuiFrame.setTitle("Warcaby");
        boardGuiFrame.setVisible(true);
        boardGuiFrame.setLocationRelativeTo((Component) null);
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
