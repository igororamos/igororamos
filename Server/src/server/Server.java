/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileReader;
import java.io.File;
/**
 *
 * @author Igor
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //while(true)
        try{
            ServerSocket server;
            server = new ServerSocket(80);
            System.out.println("Servidor escutando na porta 80");
            
            Communication comm = new Communication(server);
            
            Thread threadComm = new Thread(comm);
            threadComm.start();

        }catch(IOException e){
            System.out.println(e.getMessage());
            //System.exit(-1);
        }        
    }
}

class Communication  implements Runnable {
    
    private ServerSocket server;   
   Communication(ServerSocket server){
       this.server = server;
   }
   
    @Override
    public void run(){        
        try{
            FileReader file;
            byte []b = new byte[1];
            
            String request="";
            Socket client = server.accept();
            System.out.println("Nova conexão com o cliente " +     
                    client.getInetAddress().getHostAddress());
            Thread.sleep(200);
            new Thread(new Communication(server)).start();

            OutputStream os = client.getOutputStream();

            InputStream is = client.getInputStream();
            
            do{
                is.read(b);              
                request+=(char)b[0];
                System.out.print((char)b[0]);                
            }while(b[0]!=10);            
            
            String[]part = request.split(" ");
            String response;
            
            //String[]path = part[1].split("/");
            
            if(part[0].equals("GET")){               
                String serverPath = new File("").getAbsolutePath();
                serverPath+="/www";
                
                if(part[1].compareTo("/")==0)
                    serverPath+="/index.html";
                else
                    serverPath+=part[1];
                File path = new File(serverPath);
                
                if(path.isDirectory()){
                    response = "HTTP/1.1 200 OK\n" +
                                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                                "Server: Apache/2.2.14 (Win32)\n" +
                                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                                "Content-Length: 88\n" +
                                "Content-Type: text/html\n" +
                                "Connection: Closed\n\n";
                    
                    os.write(response.getBytes());
                    os.flush();
                }
                else if(path.isFile()){
                    StringBuilder sb= new StringBuilder("");                    
                    
                    file = new FileReader(path.toString());

                    char[]c=new char[1];
                    int len=0;
                    int count;
                    do{
                        count = file.read(c);
                        len+=count;
                        sb.append(c[0]);
                    }while(count>0);
                    
                     response = "HTTP/1.1 200 OK\n" +
                                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                                "Server: Apache/2.2.14 (Win32)\n" +
                                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                                "Content-Length: "+len+"\n" +
                                "Content-Type: text/html\n" +
                                "Connection: Closed\n\n";                    
                    os.write(response.getBytes());
                    os.write(sb.toString().getBytes());
                    os.flush();
                }
                else{
                    serverPath = new File("").getAbsolutePath();
                    serverPath+="/www/error404.html";
                    file = new FileReader(serverPath);
                    
                    StringBuilder sb= new StringBuilder(""); 
                    char[]c=new char[1];
                    int len=0;
                    int count;
                    do{
                        count = file.read(c);
                        len += count;
                        sb.append(c[0]);
                    }while(len>0);
                   
                    response = "HTTP/1.1 404 Not Found\n" +
                                "Date: Sun, 18 Oct 2012 10:36:20 GMT\n" +
                                "Server: Apache/2.2.14 (Win32)\n" +
                                "Content-Length: "+len+"\n" +
                                "Connection: Closed\n" +
                                "Content-Type: text/html; charset=iso-8859-1\n\n";

                    os.write(response.getBytes());
                    os.write(sb.toString().getBytes());
                    
                    os.flush();
                }
                        
            }
            server.close();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            //System.exit(-1);
        } catch (InterruptedException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
