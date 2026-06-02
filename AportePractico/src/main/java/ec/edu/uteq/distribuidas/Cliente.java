package ec.edu.uteq.distribuidas;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Cliente {

    public static void main(String[] args) {

        try (
                Socket socket = new Socket("localhost", 9001);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner sc = new Scanner(System.in)
        ) {

            // AUTH
            out.println("TOKEN:123");
            System.out.println(in.readLine());

            while (true) {

                System.out.print("> ");
                String msg = sc.nextLine();

                out.println(msg);

                System.out.println(in.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
