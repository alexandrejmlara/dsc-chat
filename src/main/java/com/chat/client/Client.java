package com.chat.client;

import com.chat.server.Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Estende de cliente e é reponsável por realizar e manter
 * a conexão do cliente com o servidor.
 *
 * Também realiza operações de leitura e escrita, enviando e recebendo
 * mensagens do/para o servidor.
 */
public class Client extends Thread {
    /**
     * Socket de conexão do cliente com o servidor
     */
    private Socket socket;

    /**
     * Stream de saída do cliente
     */
    private OutputStream out;

    /**
     * Responsável pela escrita no buffer de saída
     */
    private Writer outWriter;

    /**
     * Buffer de saída do cliente
     */
    private BufferedWriter bfw;

    /**
     * Nome do cliente
     */
    private String name;

    public Client(String name) {
        this.name = name;
    }

    /**
     * Método main responsável pelo início da conexão e processo de
     * leitura de mensagens enviadas por outros usuários e redirecionadas
     * pelo servidor.
     *
     * @param args Array de strings contendo parâmetros que podem ser passados por linha de comando
     * @throws IOException Exceção quando há falha de leitura ou escrita
     */
    public static void main(String []args) throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.print("Por favor, digite seu nome: ");
        String name = sc.nextLine();
        System.out.println("--- Chat Iniciado ---");

        Client app = new Client(name);
        app.connect();
        app.start();
        app.listen();
    }


    /**
     * Executado concorrentemente e responsável por ler a entrada do usuário.
     */
    public void run(){
        while(true){
            Scanner sc = new Scanner(System.in);
            String msg = sc.nextLine();
            try {
                sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Método usado para connect no server socket, retorna IO Exception caso dê algum erro.
     * @throws IOException Lançada caso haja erro de leitura ou escrita
     */
    public void connect( ) throws IOException {

        socket = new Socket("localhost", Server.port);
        out = socket.getOutputStream();
        outWriter = new OutputStreamWriter(out);
        bfw = new BufferedWriter(outWriter);
        bfw.write(name+"\r\n");
        bfw.flush();
    }

    /***
     *  Envia mensagem para o servidor
     *
     * @param msg mensagem a ser enviada
     * @throws IOException Lançada caso haja erro de leitura ou escrita
     */
    public void sendMessage(String msg) throws IOException{

        if(msg.equals("Sair")){
            bfw.write("Desconectado \r\n");
            System.out.println("Desconectado \r\n");
        }else{
            bfw.write(msg+"\r\n");
            System.out.println(name + ": " + msg);
        }
        bfw.flush();
    }

    /**
     * Recebe mensagem do servidor
     * @throws IOException Lançada caso haja erro de leitura ou escrita
     */
    public void listen() throws IOException{

        InputStream in = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(isr);
        String msg = "";

        while(!"Sair".equalsIgnoreCase(msg))

            if(bfr.ready()){
                msg = bfr.readLine();
                if(msg.equals("Sair"))
                    System.out.println("Servidor caiu! \r\n");
                else
                    System.out.println(msg+"\r\n");
            }
    }

    /***
     * Método usado quando o usuário deseja exit
     *
     * @throws IOException Lançada caso haja erro de leitura ou escrita
     */
    public void exit() throws IOException{

        sendMessage("Sair");
        bfw.close();
        outWriter.close();
        out.close();
        socket.close();
    }



}
