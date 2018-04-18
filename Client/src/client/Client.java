/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Igor
 */
public class Client {

    /**
     * @param arg the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] arg) throws IOException {
        
        String []args = new String[1];
        args[0] = "127.0.0.1/images/chart.png";
        
	String addr = args[0].split("/")[0];
        byte[]b= new byte[1];
        int port=0;
        int begin,end,len;
        String path;
        begin = args[0].indexOf("/");
        end = args[0].lastIndexOf("/");
        path = args[0].substring(begin, end+1);
        
        if(args.length==1)port=80;
        else Integer.parseInt(args[1]);

        try (Socket client = new Socket(addr, port)) {
            System.out.println("O client se conectou ao servidor!");
            System.out.println(client.toString());
            
            InputStream is = client.getInputStream();
            OutputStream os = client.getOutputStream();
            
            String request = "GET / HTTP/1.1\n" +
                    "Host: "+client.getInetAddress().getHostName()+"\n" +
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                    "Accept-Language: pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3\n" +
                    "Accept-Encoding: gzip, deflate\n" +
                    "Connection: keep-alive\n" +
                    "Upgrade-Insecure-Requests: 1\n" +
                    "If-Modified-Since: Wed, 22 Jul 2009 19:15:56 GMT\n\n";
            
            os.write(request.getBytes());
            os.flush();
            String response ="";
            while(true)
            {
                is.read(b);
                response+=(char)b[0];
                if(response.contains("\n\n") || response.contains("\r\n\r\n"))break;
            }
            
            System.out.println(response.split("\n")[0]);
            
            begin = response.indexOf("Content-");
            
            if(response.indexOf("\r", begin)>0)
                end = response.indexOf("\r", begin);
            else
                end = response.indexOf("\n", begin);
            
            len = Integer.parseInt(response.substring(begin+16,end));
            byte[]buffer = new byte[len];
            is.read(buffer);
            
            new File(path).mkdirs();
            File file = new File(path+"/index.html");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(buffer);
                fos.flush();
                fos.close();
            }
        }
        catch(UnknownHostException e){
            System.out.println("Erro: não foi possivel conectar no endereco\n"+e.getMessage());
        }
    }
    
}
