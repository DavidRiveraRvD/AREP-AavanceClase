package edu.escuelaing.arep.http;

import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.*;

public class HttpServer {
	private static final HttpServer _instance = new HttpServer();
	
	public static HttpServer getInstance() {
		return _instance;
	}
	
	private HttpServer() {}
	
        /*Profe codifo
        
        private void loadComponents(String[] componentsList) {
            for (String component : componentsList) {
                try {
                    Class c = Class.forName(component);
                    for (Method m : c.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(GetMapping.class)) {
                            String uri = m.getAnnotation(GetMapping.class).value();
                            services.put(uri, m);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, "Component not found.", ex);
                }

            }
        }
        */
        /*Profe codifo
        
        public String getServiceResponse(URI serviceURI){​​​
            String response ="";
            // The path has the form: "/do/*"
            Method m = services.get(serviceURI.getPath().substring(3));
            try {​​​
                response = m.invoke(null).toString();
            }​​​ catch (IllegalAccessException ex) {​​​
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }​​​ catch (IllegalArgumentException ex) {​​​
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }​​​ catch (InvocationTargetException ex) {​​​
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }​​​
            response = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/html\r\n"
            + "\r\n" + response;
            return response;
            }​​​
        */
	public void start(String[] args) throws IOException, URISyntaxException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(35000);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 35000.");
			System.exit(1);
		}

		boolean running = true;
		while (running) {
			Socket clientSocket = null;
			try {
				System.out.println("Listo para recibir ...");
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			serveConnection(clientSocket);
		}
		serverSocket.close();
	}
	
	public void serveConnection(Socket clientSocket) throws IOException, URISyntaxException {
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String inputLine, outputLine;
		ArrayList<String> request = new ArrayList<String>();
		while ((inputLine = in.readLine()) != null) {
			System.out.println("Received: " + inputLine);
			request.add(inputLine);
			if (!in.ready()) {
				break;
			}
		}
		
		String uriStr = request.get(0).split(" ")[1];
		URI resourceURI = new URI(uriStr);
		outputLine = getResource(resourceURI);
		out.println(outputLine);
		
		out.close();
		in.close();
		clientSocket.close();
	}
	
	public String getResource(URI resourceURI) throws IOException {
		System.out.println("Received URI path: " + resourceURI.getPath());
		System.out.println("Received URI query: " + resourceURI.getQuery());
		System.out.println("Received URI: " + resourceURI);
		//return computeDefaultResponse();
		return  getRequestDisc(resourceURI);
	}
	
	//Hacer que el servidor ya reciba js, css, imagenes y no solo html.
	public String getRequestDisc(URI resourceURI) throws IOException{
		Path file = Paths.get("target/classes/public" + resourceURI.getPath());
		String output = null;
		
		try (BufferedReader in = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {

			String str, type = null;
			type = "text/css";
			output = "HTTP/1.1 200 OK\r\n" + "Content-Type: " + type + "\r\n"; // Define tipo de archivo por ahora solo css
			
			while ((str = in.readLine()) != null) {
				System.out.println(str);
				output+= str+"\n";
			}

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
		System.out.println(output);
		return output;
	}
	
	public String computeDefaultResponse() {
		String outputLine = 
				"HTTP/1.1 200 OK\r\n" 
				+ "Content-Type: text/html\r\n" 
				+ "\r\n" 
				+ "<!DOCTYPE html>\n"
				+ "  <html>\n"
				+ "    <head>\n" 
				+ "      <meta charset=\"UTF-8\">\n" 
				+ "      <title>Title of the document</title>\n" 
				+ "    </head>\n"
				+ "    <body>\n" 
				+ "      My Web Site\n" 
				+ "      <img src=\"https://ak.picdn.net/shutterstock/videos/9932489/thumb/3.jpg\"> "
                                + "    </body>\n" 
				+ "  </html>\n";
		return outputLine;
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		HttpServer.getInstance().start(args);
	}
}
