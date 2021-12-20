import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import javax.swing.*;

public class ClientMock implements Runnable{

    private ExecutorService threads;
    Scanner scanner;
    Socket socket;
    Scanner in;
    PrintWriter out, reader;
    ObjectInputStream ois;
    String name, test;
    public int numer;

    ClientMock(String name){
        this.name = name;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost",59090);
            scanner = new Scanner(System.in);
            reader = new PrintWriter(System.out);
            in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            out = new PrintWriter(socket.getOutputStream(), true);
            //ois = new ObjectInputStream(socket.getInputStream());
            while(true){
                synchronized (this){
                    try {
                        this.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                test = in.next();
                test = in.next();
                if(test.equals("IMIE:")) {
                    do{
                        out.println(name);
                    }
                    while(!in.nextLine().equals("IMIĘ_ZAAKCEPTOWANE"));
                }
                while(true){
                    test=in.nextLine();
                    String[] arraytest = test.split(" ");
                    if(arraytest[0].equals("NUMER:")){
                        numer = Integer.parseInt(arraytest[1]);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
