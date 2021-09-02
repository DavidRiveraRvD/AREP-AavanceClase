package edu.escuelaing.arep.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SquareServer {
	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(35000);
		} catch (IOException e) {
			System.err.println(e);
			System.err.println("Could not listen on port: 35000.");
			System.exit(1);
		}
		boolean running = true;
		while (running) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}

			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String inputLine, outputLine;
			double num = 0;
			while ((inputLine = in.readLine()) != null) {
				num = Math.pow((Integer.parseInt(inputLine)), 2);
				System.out.println("Mensaje: " + num);
				outputLine = "Respuesta: " + num;
				out.println(outputLine);
				if (outputLine.equals("Respuesta del servidor: NONE"))
					break;
			}
			out.close();
			in.close();
			clientSocket.close();
		}
		serverSocket.close();
	}
}
