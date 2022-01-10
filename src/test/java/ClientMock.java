import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMock implements Runnable{

    Socket socket;
    PrintWriter out;
    ObjectInputStream ois;
    String name;
    public int numer;
    public Field[][] board;
    public boolean moveAccepted = false;
    int port;
    public boolean hasEnded = false;
    private int counter = 0;

    ClientMock(String name, int port){
        this.name = name;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            //dodaj dwóch graczy
            socket = new Socket("localhost", port);
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
                }
                break;
            }

            System.out.println("tak");


            for(int i = 0; i < 2; i++)
            {
                board = (Field[][])ois.readObject();
                Player[] player = (Player[])ois.readObject();
                line = (String)ois.readObject();
                if(line.startsWith("KONIEC_GRY: ")){
                    break;
                }
                line = (String)ois.readObject();
                if(line.startsWith("ZWYCIEZCA: ")){
                }
                line = (String)ois.readObject();
                System.out.println(line + " " + numer);
                if(line.equals("KOLEJKA: TAK")){
                    //Jeśli kolejkę ma gracz numer jeden, wykonaj poprawny ruch dla gracza numer jeden
                    if(numer == 1){
                        out.println("RUCH: 15 13 16 12");
                        System.out.println("RUCH: 15 13 16 12 - GRACZ" + numer);
                        line = (String)ois.readObject();
                        System.out.println(line + " " + numer);
                        moveAccepted = line.equals("AKCEPTACJA");
                        ois.readObject();
                        System.out.println(moveAccepted + " " + numer);
                        out.println("POMIN");
                        System.out.println("POMIN - " + numer);
                    }
                    //Jeśli kolejkę ma gracz numer dwa, wykonaj poprawny ruch dla gracza numer dwa
                    if(numer == 2){
                        out.println("RUCH: 15 3 16 4");
                        System.out.println("RUCH: 15 3 16 4 - GRACZ" + numer);
                        line = (String)ois.readObject();
                        System.out.println(line + " " + numer);
                        moveAccepted = line.equals("AKCEPTACJA");
                        ois.readObject();
                        System.out.println(moveAccepted + " " + numer);
                        out.println("POMIN");
                        System.out.println("POMIN - " + numer);
                    }
                }
                System.out.println("KOLEJKA: NIE " + numer);
                System.out.println(i);
            }
            hasEnded = true;
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
