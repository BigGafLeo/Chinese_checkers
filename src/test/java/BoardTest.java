import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    @Test
    public void testMove(){
        try {
            Board board = new Board(2);
            board.doMove(1,15,13,16,12);
            assertEquals(1,board.getBoard()[12][16].getPlayerNumber());
            assertEquals(0,board.getBoard()[13][15].getPlayerNumber());
            board.resetMoveablePawn();
            board.doMove(1,14,14,15,13);
            board.resetMoveablePawn();
            assertEquals(1,board.getBoard()[13][15].getPlayerNumber());
            board.doMove(1,15,13,17,11);
            assertEquals(1,board.getBoard()[11][17].getPlayerNumber());
            board.resetMoveablePawn();
            board.doMove(1,13,13,14,12);
            board.resetMoveablePawn();
            board.doMove(1,16,12,12,12);
        } catch (IllegalNumberOfPlayers | IllegalMoveException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIllegalMove1(){
        try {
            //Test move to null
            Board board = new Board(2);
            assertThrows(IllegalMoveException.class,
                    () -> board.doMove(1,15,13,14,13)
            );
            board.resetMoveablePawn();
            assertThrows(IllegalMoveException.class,
                    () -> board.doMove(1,15,13,13,13)
            );
            board.resetMoveablePawn();
            assertThrows(IllegalMoveException.class,
                    () -> board.doMove(1,15,13,17,11)
            );
            board.resetMoveablePawn();
            assertThrows(IllegalMoveException.class,
                    () -> board.doMove(1,15,13,15,11)
            );
            board.doMove(1,15,13,16,12);
            assertThrows(IllegalMoveException.class,
                    () -> board.doMove(1,14,14,15,13)
            );

        } catch (IllegalNumberOfPlayers | IllegalMoveException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIllegalNumberOfPlayers(){
        assertThrows(IllegalNumberOfPlayers.class,
                () ->
                new Board(1)
                );
    }

    @Test
    public void testCreateBoard(){
        try {
            Board board = new Board(3);
            Assertions.assertTrue(board.getBoard()[13][9].getPlayerNumber() > 0);
            board = new Board(4);
            Assertions.assertTrue(board.getBoard()[9][3].getPlayerNumber() > 0);
            board = new Board(6);
            Assertions.assertTrue(board.getBoard()[9][3].getPlayerNumber() > 0);
        } catch (IllegalNumberOfPlayers e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testWinner(){
        try {
            Board board = new Board(2);
            Field[][] fieldList = board.getBoard();
            for(Field[] field1 : fieldList){
                for(Field field2 : field1){
                    if(field2 != null && field2.getPlayerNumber() == 2) field2.setPlayerNumber(1);
                }
            }
            assertTrue(board.isWinner(1));
        } catch (IllegalNumberOfPlayers e) {
            e.printStackTrace();
        }
    }


}
