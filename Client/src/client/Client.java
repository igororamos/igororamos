package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Igor
 */
public class Client {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
       
	String addr = args[0].split("/")[0];
       
        int port=0;
        int begin,end,len;
        String path,folder;        
        String s;
        begin = args[0].indexOf("/");
        end = args[0].lastIndexOf("/");
        
        if(begin<0)s="/";
        else s = args[0].substring(begin,args[0].length());
        
        if(end <0)folder=args[0];
        else folder=args[0].substring(0, end+1);
        
        if(end ==args[0].length()|| end ==-1){
            path = args[0]+"/index.html";
        }
        else path = args[0];
        
        if(args.length==1)port=80;
        else Integer.parseInt(args[1]);

        try (Socket client = new Socket(addr, port)) {
            System.out.println("O client se conectou ao servidor!");
            System.out.println(client.toString());
            
            InputStream is = client.getInputStream();
            OutputStream os = client.getOutputStream();
            
            String request = "GET "+s+" HTTP/1.1\n" +
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
            while(true){                
                response+=(char)is.read();
                if(response.contains("\n\n") || response.contains("\r\n\r\n"))break;
            }
            
            System.out.println(response.split("\n")[0]);
            
            if(response.split(" ")[1].equals("200")){            
                begin = response.indexOf("Content-L");

                if(response.indexOf("\r", begin)>0)
                    end = response.indexOf("\r", begin);
                else
                    end = response.indexOf("\n", begin);

                len = Integer.parseInt(response.substring(begin+16,end));
                byte[]buffer = new byte[len];
                for(int i=0;i<len;++i)
                    buffer[i]=(byte)is.read();

                new File(folder).mkdirs();
                File file = new File(path);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(buffer);
                    fos.flush();
                    fos.close();
                }//end try;
                catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }//end if;
        }//end try socket;
        catch(UnknownHostException | SocketException e){
            System.out.println(e.getMessage());
        }
        //end catch;
    }//end main;
    
}//end class;
