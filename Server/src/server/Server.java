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
import java.io.FileInputStream;
import java.io.File;
import java.nio.file.Files;

/**
 *
 * @author Igor
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String s;
        if(args.length>0)
            s= args[0];
        else s="www";
        
        int port;
        if(args.length<2)
            port = 80;
        else port = Integer.parseInt(args[1]);
        try{
            ServerSocket server;
            server = new ServerSocket(port);
            System.out.println("Servidor escutando na porta "+port);
            
            Communication comm = new Communication(server,s);
            
            Thread threadComm = new Thread(comm);
            threadComm.start();

        }catch(IOException e){
            System.out.println(e.getMessage());
            
        }        
    }
}

class Communication  implements Runnable {
    
    private final ServerSocket server;   
    private final String s;   
   Communication(ServerSocket server,String s){
       this.server = server;
       this.s = s;
   }
   
    @Override
    public void run(){        
        try{
            FileInputStream file;
            
            byte []b = new byte[110];
            
            String request="";
            Socket client = server.accept();
            System.out.println("Nova conexão com o cliente " +     
                    client.getInetAddress().getHostAddress());
            Thread.sleep(200);
            new Thread(new Communication(server,s)).start();

            OutputStream os = client.getOutputStream();

            InputStream is = client.getInputStream();
                        
            is.read(b);
            for(int i=0;i<110;++i)
                request += (char)(b[i]);
            System.out.print(request);
            
            String[]part = request.split(" ");
            String response;
           
            if(part[0].equals("GET")){               
                String serverPath = new File("").getAbsolutePath();
                serverPath+="/www";
                String folder;
                
                if(part[1].compareTo("/")==0){
                    folder=part[1];
                }
                else{
                    serverPath+=part[1];
                    folder=part[1]+"/";
                }
                File path = new File(serverPath);
                File[] listOfFiles = path.listFiles();
                
                if(path.isDirectory()){
                    String sb;
                    sb = "<html>\n" +
                            "<head>\n" +
                            "   <title></title>\n" +
                            "</head>\n" +
                            "<body>\n";

                            for (int i = 0; i < listOfFiles.length; i++) {
                                if (listOfFiles[i].isDirectory()) {
                                    sb+="<p><a href='"+folder+""+listOfFiles[i].getName()+"'>"+
                                    listOfFiles[i].getName()+"</a></p>";
                                }
                            }   
                            for (int i = 0; i < listOfFiles.length; i++){
                                if (listOfFiles[i].isFile()) {
                                    sb+="<p><a href='"+folder+""+listOfFiles[i].getName()+"'>"+
                                    listOfFiles[i].getName()+"</a></p>";
                                }
                            }
                           
                        sb+="</body>\n" +
                        "</html>";
                         
                    response = "HTTP/1.1 200 OK\n" +
                                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                                "Server: Apache/2.2.14 (Win32)\n" +
                                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                                "Content-Length: "+sb.length()+"\n" +
                                "Content-Type: text/html\n" +
                                "Connection: Closed\n\n";
                    response+=sb;
                    os.write(response.getBytes());
                    os.flush();
                }
                else if(path.isFile()){
                                      
                    file = new FileInputStream(path.toString());
                    String cType = Files.probeContentType(path.toPath());
                    long len = Files.size(path.toPath());
                    byte []buffer = new byte[(int)len];
                    file.read(buffer);                    
                    
                    response = "HTTP/1.1 200 OK\n" +
                                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                                "Server: Apache/2.2.14 (Win32)\n" +
                                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                                "Content-Length: "+len+"\n" +
                                "Content-Type: "+cType+"\n" +
                                "Connection: Closed\n\n";
                    
                    os.write(response.getBytes());                    
                    os.write(buffer);
                    os.flush();
                }
                else{
                    serverPath = new File("").getAbsolutePath();
                    serverPath+="/www/error404.html";
                    file = new FileInputStream(serverPath);
                    long len = Files.size(new File(serverPath).toPath());
                    byte []buffer = new byte[(int)len];
                    file.read(buffer);
                    
                    response = "HTTP/1.1 404 Not Found\n" +
                                "Date: Sun, 18 Oct 2012 10:36:20 GMT\n" +
                                "Server: Apache/2.2.14 (Win32)\n" +
                                "Content-Length: "+len+"\n" +
                                "Connection: Closed\n" +
                                "Content-Type: text/html; charset=iso-8859-1\n\n";

                    os.write(response.getBytes());
                    os.write(buffer);                    
                    os.flush();
                }                        
            }
            os.close();
            is.close();
            client.close();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
