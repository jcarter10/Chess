/*
 * By: Jordan Carter
 * Description: Main Class of entire project, can run all 3 types of games from here. It also acts as the server for
 * 				the online game for a TCP socket.
 * 
 */

import java.awt.Color;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MainPlayer {

	//globals
	public static int curRow, curCol, gotoRow, gotoCol;
	public static boolean myColor;
	public static boolean myTurn = false;
	public static boolean gameOver = true;
	public static JFrame hp;
	public static JFrame mf;
	public static String opponentMove = null;
	public static ObjectInputStream in;
	public static ObjectOutputStream out;
	public static Socket socket;
	public static FileWriter fw;
	public static FileReader fr;
	
	public static ArrayList<Integer> moveData = new ArrayList<>(4);
	
	public static int PORT = 6666;
	public static final String FILENAME = "GameHistory";
	

	//running homepage and waiting for user's decision on playing either a custom, bot, or online game.
	public static void main(String[] args) {
		hp = new HomePage();
	}
	
	//starts the server for Main Player where the Guest Player will connect.
	public static void startServer() throws IOException {
		ServerSocket ss = new ServerSocket(PORT);
		
		//creates a new thread to run the server on
		new Thread(() -> {
			try {
				//first try: waits for client to test connection, second try: waiting until client actually connects
				for (int i = 0; i < 2; i++) 
					socket = ss.accept();
				
				System.out.println("Successful Socket Connection for Server-Side");
				
				//disabling Nagles algorithm so that packets are not delayed while being sent
				socket.setTcpNoDelay(true);
				
				//function that saves IP information about the client-server 
				logServerClientInfo();
				
				//creating i/o streams, *have to create the 'out' before the 'in' or it will break program.
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				
				//sending opponent my color, so they can choose theirs correctly.
				System.out.println("Sending color");
				sendColor();
				
				//getting whos turn it is based on color
				if (myColor == false)
					myTurn = true;
				else 
					myTurn = false;
				
				//while connected, read for move data
				while (true) {
					//grab move data
					readMove();
					
					//grab turn data
					readTurn();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}).start();
	}

	
	//sending the other player their color
	public static void sendColor() throws IOException {
		System.out.println("myc: " + myColor);
		out.writeBoolean(myColor);
		out.flush();
	}

	//reading data to know if it's our turn yet.
	public static void readTurn() throws IOException, ClassNotFoundException {
		myTurn = in.readBoolean();
		System.out.println("My Turn: " + myTurn);
	}

	//sending data to other player to let them know it's their turn.
	public static void sendTurn() throws IOException {
		//telling other player it's their turn.
		out.writeBoolean(true);
		out.flush();
		
		//not your turn anymore so false.
		if (myTurn == true)
			myTurn = false;
	}
		
	
	//send move to other player
	public static void sendMove(ArrayList<Integer> ar) {
		try {
			//reset highlight of opponents move because our move was made.
			ChessBoard.grid[curRow][curCol].setBorder(null);
			ChessBoard.grid[gotoRow][gotoCol].setBorder(null);
			
			//saving the data, for border highlight purposes.
			curRow =  ar.get(0);
			curCol = ar.get(1);
			gotoRow = ar.get(2);
			gotoCol = ar.get(3);	
			
			//writing move data to other player.
			out.writeObject(ar);
			out.flush();

			//showing user which colors turn it is.
			Header.setCurrentTurnText(ChessBoard.currentTurn);

			//showing the move/turn counter for users.
			Header.setTurnCounter(ChessBoard.currentTurn, ChessBoard.valid);

			//setting each players timer.
			Header.setTimers(ChessBoard.currentTurn);

			//checking for a stalemate at the end of each turn.
			ChessBoard.stalemateCheck();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//recieve move from other player and call appropiate functions to place on chessboard
	public static void readMove(){
		try {
			//reset highlight of opponents move because our move was made.
			ChessBoard.grid[curRow][curCol].setBorder(null);
			ChessBoard.grid[gotoRow][gotoCol].setBorder(null);
			
			//reading data from other player
			System.out.println("Reading data...");

			/*since the other player has made a move and notified us that it's our turn, 
			  we can read the move data and replicate it on our board before performing our own move. */
			Object ob = in.readObject();
			moveData = (ArrayList<Integer>) ob;

			//must get the piece position from our main POV view, in order to update data for this POV.
			curRow = moveData.get(0);
			curCol = moveData.get(1);
			gotoRow = moveData.get(2);
			gotoCol = moveData.get(3);

			//setting the data of opponents move into our main running class ChessBoard so it can perform appropiate checks.
			ChessBoard.jb_currentRow = curRow;
			ChessBoard.jb_currentCol = curCol;
			ChessBoard.jb_gotoRow = gotoRow;
			ChessBoard.jb_gotoCol = gotoCol;

			//calling neccessary functions from chessboard.java to place move on board.
			//grabbing which pieces the move points too.
			int piecePos = ChessBoard.getChessPiece(curRow, curCol);
			int movePos = ChessBoard.getChessPiece(gotoRow, gotoCol);

			//checking if move is valid.
			ChessBoard.checkPieceValidity(piecePos, movePos);	
			System.out.println(ChessBoard.valid);

			//if move was valid, setting border highlights for second clicked button and disabling icon border.
			if (ChessBoard.valid == true) {
				//setting border highlights for cells and disabling icon border.
				if (ChessBoard.castled == false) {
					ChessBoard.grid[gotoRow][gotoCol].setFocusPainted(false);
					ChessBoard.grid[curRow][curCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
					ChessBoard.grid[gotoRow][gotoCol].setFocusPainted(false);
					ChessBoard.grid[gotoRow][gotoCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
				}
			}

			//showing user which colors turn it is.
			Header.setCurrentTurnText(ChessBoard.currentTurn);

			//showing the move/turn counter for users.
			Header.setTurnCounter(ChessBoard.currentTurn, ChessBoard.valid);

			//setting each players timer.
			Header.setTimers(ChessBoard.currentTurn);

			//checking for a stalemate at the end of each turn.
			ChessBoard.stalemateCheck();

			//resetting data for next operation.
			ChessBoard.resetVariables();

		} catch (NullPointerException e) {
			System.out.println("Nothing to read yet...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//saving server and client information to the logger file
	public static void logServerClientInfo() throws IOException {
		//checking game history to get game counter.
		BufferedReader br = new BufferedReader(new FileReader(FILENAME));
		String line = br.readLine();
		String newLine = line.replaceAll("\\D+", "");
		int gameCount = Integer.parseInt(newLine);
		br.close();
		
		//creating writer object to write server and client info into it, as well as get game ID.
		BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME, true));
		gameCount++;
		bw.write("\n\nGame " + gameCount + ":");
		
		//Server Information//
		//getting server ip
		String serverInfo = socket.getLocalSocketAddress().toString();
		String serverIP = serverInfo.replace("/", "");
		
		//getting client name if accessible
		String serverName = serverInfo.substring(0, serverInfo.indexOf("/"));
		if (serverName.equals("")) {
			serverName = "localhost";
		}	

		//getting date & time of access of clients connection
		System.currentTimeMillis();
		SimpleDateFormat formatter= new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String serverDate = formatter.format(date);

		//saving results in text file
		bw.write("\nMain-Player (Server): " + serverIP + " " + serverName + " - [" + serverDate + "]\n");


		//Client Information//
		//getting client ip
		String clientInfo = socket.getRemoteSocketAddress().toString();
		String clientIP = clientInfo.substring(clientInfo.indexOf("/") + 1, clientInfo.length());

		//getting client name if accessible
		String clientName = clientInfo.substring(0, clientInfo.indexOf("/"));
		if (clientName.isEmpty())
			clientName = "unknown";

		//getting date & time of access of clients connection
		System.currentTimeMillis();
		formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		date = new Date(System.currentTimeMillis());
		String clientDate = formatter.format(date);

		//saving results in text file
		bw.write("Guest-Player (Client): " + clientIP + " " + clientName + " - [" + clientDate + "]\n");
		bw.close();
		
		editFirstLineOfFile(line, gameCount);
	}

	//helper function for logServerClientInfo() to edit the first line game count after the game details are entered.
	public static void editFirstLineOfFile(String line, int gameCount) throws IOException {
		//scanner and stringbuffer to grab all the contents of the file
		Scanner scan = new Scanner(new File(FILENAME));
		StringBuffer buffer = new StringBuffer();
		while (scan.hasNextLine()) {
			buffer.append(scan.nextLine() + System.lineSeparator());
		}
		String content = buffer.toString();
		scan.close();

		//replace first line in file with appropiate game count value.
		content = content.replaceAll(line, "No. of Games Played: " + gameCount);
		
		//write the contents of the file back into it.
		FileWriter writer = new FileWriter(FILENAME);
		writer.append(content);
		writer.flush();
		writer.close();
	}
}
