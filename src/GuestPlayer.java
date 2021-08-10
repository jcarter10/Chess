/*
 * By: Jordan Carter
 * Description: If an online game is wanted, this class is to be ran. It acts as the guest-side of the TCP connection.
 * 
 */

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;

public class GuestPlayer {

	//globals
	public static int opponentMove;
	public static int curRow, curCol, gotoRow, gotoCol;
	public static boolean myColor;
	public static boolean guest = false;
	public static boolean myTurn = false;
	public static Socket socket;
	public static ChessBoard cb;
	public static HomePage hp;
	public static ObjectInputStream in;
	public static ObjectOutputStream out;

	public static ArrayList<Integer> moveData = new ArrayList<>(4);

	public static final int PORT = 6666;
	public static final String IP = "127.0.0.1";

	public static void main(String[] args) {
		//getting server InetAddress to connect to.
		try {
			InetAddress ipAddress = InetAddress.getByName(IP);

			System.out.println("Test: attempting to reach server...");
			int attempt = 1;

			//testing the socket to see if it's even connectable, then closing the test socket.
			boolean connecting = true;
			while (connecting) {
				try { 
					//connect to socket, if connection works stop trying
					socket = new Socket(ipAddress, PORT); 
					connecting = false;
					
					System.out.println("Attempt #" + attempt + ": Connection Successful, client has connected.");
				
				} catch (ConnectException e) {
					//connection didn't work, so try again after 2 seconds.
					System.out.println("Attempt #" + attempt + ": Connection failed, trying to reconnect to server.");
					attempt++;
					try {
						//trys again after 2 seconds.
						Thread.sleep(2000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
			
			//closing test socket for actual connection
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//letting program know a guest is here
		guest = true;
		//we know connection is possible, so run the game
		HomePage.runOnlineGame();
	}

	//connects the client to the Main Player aka the server.
	public static void connect() {
		//creates a new thread for the client side connection
		new Thread(() -> {
			try {
				//officially connecting to socket because the test above was true.
				InetAddress ipAddress = InetAddress.getByName(IP);
				socket = new Socket(ipAddress, PORT); 

				//disabling Nagles algorithm so that packets are not delayed while being sent
				socket.setTcpNoDelay(true);

				//creating i/o streams
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				
				//getting current color to determine piece side before closing so we can set appropiate GUI.
				System.out.println("Recieving Color...");
				getColor();

				//getting whos turn it is based on color
				if (myColor == false)
					myTurn = true;
				else 
					myTurn = false;

				//while connected, read for move data
				while (true) {
					//grab turn data
					readTurn();

					//grab move data
					readMove();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}).start();
	}


	//recieving it's appropiate color
	public static void getColor() throws IOException, ClassNotFoundException {
		Boolean opponentColor = in.readBoolean();
		System.out.println("Opponents Color: " + opponentColor);

		myColor = (opponentColor == true) ? false : true;
		System.out.println("My Color: " + myColor);
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

			//setting each players timer.
			Header.setTimers(ChessBoard.currentTurn);

			//checking for a stalemate at the end of each turn.
			ChessBoard.stalemateCheck();



		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//recieve move from main player and call appropiate functions to place on chessboard
	public static void readMove() {
		try {
			//reset highlight of opponents move because our move was made.
			ChessBoard.grid[curRow][curCol].setBorder(null);
			ChessBoard.grid[gotoRow][gotoCol].setBorder(null);

			//reading data from server
			System.out.println("Reading data...");

			//since the other player has made a move and notified us that it's our turn, we can read the move data and replicate it on our board before performing our own move.
			if (myTurn == true) {
				Object ob = in.readObject();
				moveData = (ArrayList<Integer>) ob;
				for (int n : moveData) {
					System.out.println("Data sent: " + n);
				}

				//must get the piece position from our main POV view, in order to updatedata for this POV.
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
						ChessBoard.grid[curRow][curCol].setFocusPainted(false);
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
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Nothing to read yet...");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
