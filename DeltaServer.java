import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DeltaServer {
	private static List<ClientHandler> clients = new ArrayList<>();
	private static ServerSocket serverSocket;

	public static void main(String[] args) {
		try {
			serverSocket = new ServerSocket(4999);
			System.out.println("Server is running....");

			while (true) {
				Socket s = serverSocket.accept();
				System.out.println("New client connected");

				// Create a new client handler for the connected client
				ClientHandler clientHandler = new ClientHandler(s);

				// Add the client handler to the list of clients
				clients.add(clientHandler);

				// Handle each client in a separate thread
				Thread thread = new Thread(clientHandler);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
					System.out.println("ServerSocket closed");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Broadcast the message to all connected clients
	public static void broadcast(String message, ClientHandler excludeClient) {
		for (ClientHandler client : clients) {
			if (client != excludeClient) {
				client.sendMessage(message);
			}
		}
	}

	// Inner class representing a client handler
	static class ClientHandler implements Runnable {
		private Socket socket;
		private BufferedReader bf;
		private PrintWriter pr;

		public ClientHandler(Socket socket) {
			try {
				this.socket = socket;
				this.bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.pr = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					String str = bf.readLine();
					if (str == null || str.equals("exit")) {
						break;
					}
					System.out.println("Client " + socket + ": " + str);

					// Broadcast the message to all clients
					DeltaServer.broadcast(str, this);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
					System.out.println("Client disconnected: " + socket);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Remove the client handler from the list when client disconnects
				clients.remove(this);
			}
		}

		// Send a message to this specific client
		public void sendMessage(String message) {
			pr.println(message);
			pr.flush();
		}
	}
}
