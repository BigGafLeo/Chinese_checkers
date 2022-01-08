import com.example.trylmaproject.server.Field;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class ClientMock implements Runnable{

    private ExecutorService threads;
    Scanner scanner;
    Socket socket;
    Scanner in;
    PrintWriter out, reader;
    ObjectInputStream ois;
    String name, test;
    public int numer;
    public Field[][] board;
    public boolean moveAccepted = false;

    ClientMock(String name){
        this.name = name;
    }

    @Override
    public void run() {
        try {
            //dodaj dwóch graczy
            socket = new Socket("localhost", 59090);
            out = new PrintWriter(socket.getOutputStream(),true);
            ois = new ObjectInputStream(socket.getInputStream());
            var line = (String)ois.readObject();
            if(line.startsWith("NUMER: "))
                numer = Integer.parseInt(line.substring(7));
            do{}
            while(!((String)ois.readObject()).startsWith("IMIE:"));
            out.println(name);

            while(true){
                if(numer == 1){
                    synchronized (this){
                        wait(1000);
                    }
                    out.println("START");
                    if (ois.readObject().equals("JESZCZE_RAZ")) {}
                    else break;
                }
            }

            System.out.println("tak");


            while (true)
            {
                board = (Field[][])ois.readObject();
                line = (String)ois.readObject();
                if(line.startsWith("KONIEC_GRY: ")){
                    break;
                }
                line = (String)ois.readObject();
                if(line.startsWith("ZWYCIEZCA: ")){
                }
                line = (String)ois.readObject();
                if(line.equals("KOLEJKA: TAK")){
                    if(numer == 1){
                        out.println("MOVE: 15 13 16 12");
                        System.out.println("MOVE: 15 13 16 12");
                        if(((String)ois.readObject()).equals("AKCEPTACJA")){
                            moveAccepted = true;
                            break;
                        }
                    }
                    if(numer == 2) out.println("POMIN");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
