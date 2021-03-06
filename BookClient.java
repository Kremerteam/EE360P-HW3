import java.net.InetAddress;
import java.util.Scanner;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.*;

public class BookClient {
	public static void main(String[] args) throws IOException {
		String hostAddress;
		int tcpPort;
		int udpPort;
		int clientId;

		if (args.length != 2) {
			System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
			System.out.println("\t(1) <command-file>: file with commands to the server");
			System.out.println("\t(2) client id: an integer between 1..9");
			System.exit(-1);
		}

		String commandFile = args[0];
		clientId = Integer.parseInt(args[1]);
		hostAddress = "localhost";
		tcpPort = 7000;// hardcoded -- must match the server's tcp port
		udpPort = 8000;// hardcoded -- must match the server's udp port
		boolean Tmode = false;
		Socket socket = null;
		PrintStream out = null;
		BufferedReader in = null;
		FileWriter output = new FileWriter("out_"+clientId+".txt");

		int recieveSize = 4096;
		
		try {
			InetAddress ia = InetAddress.getByName("localhost");
			Scanner sc = new Scanner(new FileReader(commandFile));
			byte[] buf = new byte[2048];
			DatagramSocket UDPSocket = new DatagramSocket();
			DatagramPacket dataPacket = new DatagramPacket(buf, buf.length);
			DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);
			DatagramPacket recievePacket = new DatagramPacket(buf, buf.length);

			while (sc.hasNextLine()) {
				String cmd = sc.nextLine();
				String[] tokens = cmd.split(" ");

				if (tokens[0].equals("setmode")) {
					String command = tokens[0] + '$' + tokens[1];
					// TODO: set the mode of communication for sending commands to the server
					if (tokens[1].equals("T")) {
						if (Tmode == true) { //TCP but in TCP mode
							//TODO
							out.println(command);
							out.flush();
							String reply = in.readLine();
							System.out.println(reply);
							output.write(reply+"\n");
						} else {
							try { //TCP but in UDP mode
								Tmode = true;
								buf = command.getBytes();
								sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
								UDPSocket.send(sendPacket);
								buf = new byte[recieveSize];
								recievePacket = new DatagramPacket(buf, buf.length);
								UDPSocket.receive(recievePacket);
								String response = new String(recievePacket.getData(), 0, recievePacket.getLength());
								System.out.println(response);
								output.write(response+"\n");
								//UDPSocket.close();
								socket = new Socket(hostAddress, tcpPort);
								out = new PrintStream(socket.getOutputStream());
								in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							//	System.out.println(command);
							//	String reply = "";
							//	reply = in.readLine();
							//	System.out.println(reply);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} else if (Tmode == true) { //UDP but in TCP mode
						try {
							Tmode=false;
							out.println(command);
							out.flush();
							String reply = in.readLine();
							System.out.println(reply);
							output.write(reply+"\n");
							socket.close();
							out.close();
							in.close();
						//	buf = new byte[1024];
						//	UDPSocket = new DatagramSocket();
							// send message
					/*	buf = command.getBytes();
							sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
							UDPSocket.send(sendPacket);
							buf = new byte[recieveSize];
							recievePacket = new DatagramPacket(buf, buf.length);
							UDPSocket.receive(recievePacket);
							String response = new String(recievePacket.getData(), 0, recievePacket.getLength());
							System.out.println(response);
							output.write(response+"\n");*/
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else { //UDP but in UDP
						Tmode=false;
						buf = command.getBytes();
						sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
						UDPSocket.send(sendPacket);
						buf = new byte[recieveSize];
						recievePacket = new DatagramPacket(buf, buf.length);
						UDPSocket.receive(recievePacket);
						String response = new String(recievePacket.getData(), 0, recievePacket.getLength());
						System.out.println(response);
						output.write(response+"\n");
					}

				} else if (tokens[0].equals("borrow")) {
					// TODO: send appropriate command to the server and display the
					// appropriate responses form the server
					String command = "";
					for (int i = 0; i < tokens.length; i++) {
						if (i < 2)
							command += tokens[i] + '$';
						else if (i != tokens.length - 1)
							command += tokens[i] + " ";
						else
							command += tokens[i];
					}

					if (Tmode) {
					//	System.out.println(command);
						 out.println(command);
						 out.flush();
						String reply = "";
						try {
							reply = in.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(reply);
						output.write(reply+"\n");
					} else {
						buf = command.getBytes();
						sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
						UDPSocket.send(sendPacket);
						buf = new byte[recieveSize];
						recievePacket = new DatagramPacket(buf, buf.length);
						UDPSocket.receive(recievePacket);
						String response = new String(recievePacket.getData(), 0, recievePacket.getLength());
						System.out.println(response);
						output.write(response+"\n");
					}

				} else if (tokens[0].equals("return")) {
					// TODO: send appropriate command to the server and display the
					// appropriate responses form the server
					String command = tokens[0] + '$' + tokens[1];
					if (Tmode) // TCP
					{
						out.println(command);
						out.flush();
						String reply = "";
						try {
							reply = in.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(reply);
						output.write(reply+"\n");
					} else { // UDP
						buf = command.getBytes();
						sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
						UDPSocket.send(sendPacket);
						buf = new byte[recieveSize];
						recievePacket = new DatagramPacket(buf, buf.length);
						UDPSocket.receive(recievePacket);
						String response = new String(recievePacket.getData(), 0, recievePacket.getLength());
						System.out.println(response);
						output.write(response+"\n");

					}

				} else if (tokens[0].equals("inventory")) {
					// TODO: send appropriate command to the server and display the
					// appropriate responses form the server
					String command = tokens[0] + "$";
					if (Tmode) {
						out.println(command);
						out.flush();
						String reply = "";
						try {
							char[] cbuf = new char[2040];
							in.read(cbuf);
							reply = String.valueOf(cbuf);
						    
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(reply);
						output.write(reply+"\n");
					} else {
						buf = new byte[buf.length];
						buf = command.getBytes();
						sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
						UDPSocket.send(sendPacket);
						buf = new byte[recieveSize];
						recievePacket = new DatagramPacket(buf, buf.length);
						UDPSocket.receive(recievePacket);
						String response = new String(recievePacket.getData(), 0, recievePacket.getLength());
						System.out.println(response);
						output.write(response+"\n");
					}

				} else if (tokens[0].equals("list")) {
					// TODO: send appropriate command to the server and display the
					// appropriate responses form the server
					String command = tokens[0] + '$' + tokens[1];
					if (Tmode) {
						out.println(command);
						out.flush();
						String reply = "";
						try {
							reply = in.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(reply);
						output.write(reply+"\n");
					} else {
						buf = new byte[buf.length];
						buf = command.getBytes();
						sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
						UDPSocket.send(sendPacket);
						buf = new byte[recieveSize];
						recievePacket = new DatagramPacket(buf, buf.length);
						UDPSocket.receive(recievePacket);
						String response = new String(recievePacket.getData(), 0, recievePacket.getLength());
						System.out.println(response);
						output.write(response+"\n");
					}

				} else if (tokens[0].equals("exit")) {
					// DONE!
					String command = tokens[0] + "$";
					if (Tmode) {
						out.println(command);
						out.flush();	
						out.close();
						in.close();
						socket.close();
					} else {
						buf = new byte[buf.length];
						buf = command.getBytes();
						sendPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
						UDPSocket.send(sendPacket);
						UDPSocket.close();
					}

				} else {
					System.out.println("ERROR: No such command");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		output.close();
	}
}
