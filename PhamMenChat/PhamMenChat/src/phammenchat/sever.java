/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phammenchat;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author 217 Be
 */
public class sever {
   
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello. This is server");
        int port = 1512;
       
  
        try{
          
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket currentSocket = serverSocket.accept();
                System.out.println("connected");
                
                InputStream is = currentSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                String s = (String)ois.readObject();
                System.out.println("receive from client: " + s);
                ObjectMapper mapper = new ObjectMapper();
                MyMessage mr = mapper.readValue(s, MyMessage.class);
                
                MyMessage m = new MyMessage();
                m.sender = "server";
                m.receiver = mr.sender;
                m.type = "login";
                
                if (mr.type.equalsIgnoreCase("login")){
                    Connection con = null;
                    Statement sttm = null;
                    Class.forName("org.sqlite.JDBC");
                    con = DriverManager.getConnection("jdbc:sqlite:SQlite15I2.db");

                    String sql = "SELECT id, username, password FROM TblUser WHERE (username = ?) AND (password = ?)";
                    PreparedStatement pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, mr.sender);
                    pstmt.setString(2, mr.content);
                    ResultSet rs    = pstmt.executeQuery();
                    
                    if (rs.next()){
                        m.content = "ok";
                    }
                    // loop through the result set
                    else{
                        m.content = "fail";
                    }
                }
                
               if (mr.type.equalsIgnoreCase("login")){
                   if (mr.sender.equalsIgnoreCase("anhvan")
                          && mr.content.equalsIgnoreCase("210797")){
                        m.content = "ok";
                    }
                    else{
                        m.content = "fail";
                    }
                }
                OutputStream os = currentSocket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                String s1 = mapper.writeValueAsString(m);
                System.out.println("send to client: " + s1);
                oos.writeObject(s1);
                oos.flush();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
