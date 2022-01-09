import com.example.trylmaproject.client.BoardGuiFrame;
import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

//public class BoardGuiFrameTest {
//
//    @Test
//    public void testGUI(){
//        Board board = null;
//        try {
//            board = new Board(6);
//        } catch (IllegalNumberOfPlayers e) {
//            e.printStackTrace();
//        }
//        BoardGuiFrame boardGuiFrame = new BoardGuiFrame(2, board.getBoard());
//        boardGuiFrame.setTurn(true);
//        var line = boardGuiFrame.getMessage();
//        System.out.println(line);
//        try {
//            var commandArray = line.split(" ");
//            board.doMove(2,commandArray);
//        } catch (IllegalMoveException e) {
//            e.printStackTrace();
//            Assertions.assertTrue(false);
//        }
//        boardGuiFrame.boardRepaint(board.getBoard());
//    }
//}
