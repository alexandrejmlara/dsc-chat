package com.chat.server;

import java.io.*;
import java.net.Socket;

/**
 * Estende a classe {@link Thread} e é responsável por manter
 * a conexão do cliente com o servidor.
 *
 * Realiza o envio e recebimentos de mensagens entre os clientes
 * e o servidor.
 *
 */
public class ClientConnection implements Runnable {
    /**
     * Socket de conexão com o servidor
     */
    private Socket conn;
    /**
     * Stream de entrada
     */
    private InputStream in;
    /**
     * Leitor da stream de entrada
     */
    private InputStreamReader isr;
    /**
     * Armazena em buffer os dados
     */
    private BufferedReader bfr;
    /**
     * Escreve os dados a serem enviados
     */
    private BufferedWriter bfw;
    /**
     * Nome do cliente
     */
    private String name;

    /**
     * Construtor inicializa com o socket de conexão do cliente
     * e é responsável também pela inicialização dos buffers e
     * readers da conexão com o cliente.
     *
     * @param conn Socket de conexão do cliente com o servidor
     */
    public ClientConnection( Socket conn ){
        this.conn = conn;

        try{
            in=conn.getInputStream();
            isr =new InputStreamReader(in);
            bfr=new BufferedReader(isr);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Realiza a leitura de mensagens e o envio em broadcast
     * a todos os usuários conectados com o servidor.
     *
     */
    public void run(){

        try{

            String msg;
            OutputStream out =  this.conn.getOutputStream();
            Writer outWriter = new OutputStreamWriter(out);
            bfw = new BufferedWriter(outWriter);
            Server.clients.add(this);
            name = msg = bfr.readLine();

            while(!"Sair".equalsIgnoreCase(msg) && msg != null)
            {
                msg = bfr.readLine();
                Server.sendToAll(bfw, getName(), msg);
                System.out.println(this.getName() + ": " + msg);
            }

        }catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Recupera o nome do cliente
     *
     * @return Nome do cliente
     */
    public String getName() {
        return name;
    }

    /**
     * Recupera o escritor de buffer do cliente
     *
     * @return Objecto BufferedWriter do cliente
     */
    public BufferedWriter getBfw() {
        return bfw;
    }
}
