import java.net.*;
import java.io.*;
import java.util.Scanner;

public class DeltaClient {
	public static void main(String[] args) {
		try {
			final Scanner[] scanner = { new Scanner(System.in) };
			final Socket[] socket = { new Socket("localhost", 4999) };

			// Create a thread for receiving messages from the server
			Thread receiveThread = new Thread(() -> {
				try {
					InputStreamReader in = new InputStreamReader(socket[0].getInputStream());
					BufferedReader bf = new BufferedReader(in);

					while (true) {
						String str = bf.readLine();
						if (str == null || str.equals("exit")) {
							break;
						}
						System.out.println("Server: " + str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			receiveThread.start();

			// Create a thread for sending messages to the server
			Thread sendThread = new Thread(() -> {
				try {
					PrintWriter pr = new PrintWriter(socket[0].getOutputStream());

					// Take input from the user and send messages to the server
					while (true) {
						String message = scanner[0].nextLine();
						pr.println(message);
						pr.flush();

						// Exit the loop if the user enters "exit"
						if (message.equals("exit")) {
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (socket[0] != null && !socket[0].isClosed()) {
							socket[0].close();
							System.out.println("Socket closed");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			sendThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
