import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.Map;
//javac -cp sqlite-jdbc-3.23.1.jar; server.java
//path %path%;C:\Program Files\Java\jdk1.8.0_131\bin

public class server{
public static void main(String[] args) throws IOException {
    int port = 8500;
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    Database db = new Database ("jdbc:sqlite:ES.db");

    String query = "SELECT * From characters";
    //SELECT name From Characters where class like '1%'
    //SELECT name From Characters where class like '2%'
    //SELECT name From Characters where class like '3%'
	String ug="SELECT*FROM discography";
	//String ug = "SELECT Characters.name From Characters INNER JOIN discography ON characters.unit=discography.unit;";
    //String html = Input.readFile("");

    server.createContext("/characters", new RouteHandler(db,query));
	server.createContext("/disco", new RouteHandler(db,ug));
	server.createContext("/tracksbyartist", new HttpHandler(){
            public void handle(HttpExchange exchange) throws IOException {
					Map<String, Object> parameters = RouteHandler.parseParameters("get",exchange);
                   
					String id = parameters.get("id").toString();
                    String query = "SELECT * FROM albums " + 
                                   "INNER JOIN tracks ON characters.unit=discography.unit" +
                                   "WHERE artistId = " + id;
                    String response = db.selectData(query);
					
                    RouteHandler.send(response,exchange);
            }
        });
	
    server.start();
    System.out.println("Server is listening on port " + port );

}
}