import com.example.trylmaproject.client.BoardGuiFrame;
import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardGuiFrameTest {

    @Test
    public void testGUI(){
        Board board = null;
        try {
            board = new Board(2);
        } catch (IllegalNumberOfPlayers e) {
            e.printStackTrace();
        }
        Player[] playerList= new Player[6];
        playerList[0] = new Player(1, "Xd");
        playerList[1] = new Player(2, "Dx");
        BoardGuiFrame boardGuiFrame = new BoardGuiFrame(1, board.getBoard(), playerList);
        boardGuiFrame.setTurn(true);
        var line = boardGuiFrame.getMessage();
        System.out.println(line);
        try {
            var commandArray = line.split(" ");
            board.getBoard()[Integer.parseInt(commandArray[2])][Integer.parseInt(commandArray[1])].setPlayerNumber(1);
            board.doMove(1,commandArray);
        } catch (IllegalMoveException e) {
            e.printStackTrace();
            Assertions.assertTrue(false);
        }
        boardGuiFrame.boardRepaint(board.getBoard());

        boardGuiFrame.whoWinner("Xd");
        assertEquals("Xd",boardGuiFrame.getPlace(1));

//        boardGuiFrame.endGame("Dx");
//        assertEquals("Dx",boardGuiFrame.getPlace(2));
    }
}
