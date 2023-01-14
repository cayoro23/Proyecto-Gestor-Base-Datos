/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 *
 * @author Carlos
 */
public class Servidor implements Runnable {

    private ServerSocket serverSocket;
    private boolean clientConectado = false;
    private final int puerto = Integer.parseInt("8000");
    private final JTextArea panel;
    private final Comandos bd = new Comandos();

    public Servidor(JTextArea panel) {
        this.panel = panel;
    }

    public void iniciarServidor() {
        int puerto = Integer.parseInt("8000");
        new Thread(this).start();
    }

    public void detenerServidor() {
        System.out.println("Servidor inactivo, cerrando...");
        try {
            if (clientConectado) {
                serverSocket.close();
            } else {
                System.out.println("No se ha establecido conexión con ningún cliente");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            panel.append("Servidor iniciado en: 127.0.0.1"
                    + " \npuerto: " + puerto + "\n");
            serverSocket = new ServerSocket(puerto);
            panel.append("Esperando clientes...\n");
            while (true) {
                try {
                    Socket server = serverSocket.accept();
                    clientConectado = true;
                    panel.append("Cliente conectado: " + server.getRemoteSocketAddress() + "\n");
                    DataInputStream in = new DataInputStream(server.getInputStream());
                    while (true) {
                        String command = in.readUTF();
                        bd.ejecutarComando(command);
                    }
                } catch (IOException e) {
                    panel.append("Error:" + e.getMessage() + "\n");
                    break;
                }
            }
        } catch (IOException e) {
            panel.append("Error:" + e.getMessage() + "\n");
        }
    }
}
