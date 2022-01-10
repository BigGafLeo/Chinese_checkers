import com.example.trylmaproject.client.QueFrame;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class QueFrameTest {

	@Test
	public void testQueGui()
	{
		QueFrame frame2 = new QueFrame(2);
		QueFrame frame1 = new QueFrame(1);
		frame1.setVisible(true);
		if(frame1.isNameReadyToSend())
			assertTrue(frame1.getNameToServer() != null);
		assertTrue(frame1.isReadyToPlay()==true);

	}
}