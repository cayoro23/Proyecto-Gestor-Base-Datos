package Cliente;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class cliente {

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Direccion del servidor: ");
            String serverAddress = sc.nextLine();
            System.out.print("Puerto: ");
            int port = sc.nextInt();

            Socket socket = new Socket(serverAddress, port);
            System.out.println("Conectado al servidor: " + socket.getRemoteSocketAddress());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                System.out.print("ingresar comando sql: ");
                String message = sc.nextLine().trim();

                out.writeUTF(message);
                out.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
