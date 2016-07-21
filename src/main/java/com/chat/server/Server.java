package com.chat.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe Servidor que é responsável por realizar e manter conexões com os clientes,
 * além de retransmitir as mensagens a todos.
 *
 */
public class Server {
    /**
     * Array que mantém as conexões dos clientes conectados ao servidor
     */
    protected static ArrayList<ClientConnection> clients;
    /**
     * Socket do servidor
     */
    private static ServerSocket server;
    /**
     * Porta onde o servidor irá responder as requisições
     */
    public static int port = 12345;

    /***
     *
     * Inicializa o servidor e aguarda por novas conexões dos clientes.
     *
     * Cada cliente é tratado por uma thread específica em uma pool de threads.
     *
     */
    public static void main(String []args) {

        try{
            //Cria os objetos necessário para instânciar o servidor
            server = new ServerSocket(port);
            clients = new ArrayList<ClientConnection>();
            System.out.println("Servidor ativo na porta: " + port);
            ExecutorService e = Executors.newCachedThreadPool();

            while(true){
                System.out.println("Servidor aguardando conexão...");
                Socket conn = server.accept();
                System.out.println("Client conectado...");
                e.execute(new ClientConnection(conn));
            }

        }catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Envia mensagem a todos os clientes conectados no servidor.
     *
     * @param bwSaida BufferedWriter para enviar as mensagens a seus respectivos destinatários
     * @param msg Mensagem a ser enviada aos destinatários
     * @throws IOException
     */
    public static void sendToAll(BufferedWriter bwSaida, String username, String msg) throws  IOException
    {
        ReentrantLock lock = new ReentrantLock();
        BufferedWriter bfw;
        lock.lock();
        try {
            for (ClientConnection c : clients) {
                bfw = (BufferedWriter) c.getBfw();
                if (!(bwSaida == bfw)) {
                    c.getBfw().write(username + ": " + msg + "\r\n");
                    c.getBfw().flush();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
