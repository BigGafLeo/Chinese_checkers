import com.example.trylmaproject.client.BoardGuiFrame;
import com.example.trylmaproject.exceptions.IllegalMoveException;
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
        boardGuiFrame.setTurn(true);
        System.out.println(boardGuiFrame.getMessage());
        try {
            board.doMove(2,15,3,16,4);
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
        boardGuiFrame.boardRepaint(board.getBoard());
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
