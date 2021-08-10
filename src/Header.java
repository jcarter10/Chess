/*
 * By: Jordan Carter
 * Description: Takes care of all the information around the ChessBoard
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

//a class full of functions to set the information around the chessboard
public class Header extends MainFrame{

	//Globals
	public static int turnCounter = 1;
	public static ArrayList<String> totalMoves = new ArrayList<String>();
	private static int n = 0;
	public static String finalString = "";
	public static int whiteVal;
	public static int blackVal;
	public static String pos;
	public static String move;
	
	//Shows the currently selected piece by user (actionPerformed in ChessBoard calls this everytime a button is clicked)
	public static void setSelectedPieceText(String pName, int loc, int row, int col) {
		String str = getChessNotation(row, col);
		
		if (loc == 1) {
			pName = pName.toLowerCase();
			pName = pName.substring(0,1).toUpperCase() + pName.substring(1).toLowerCase();
			currentPieceText.setText("Selected: " + pName + " at " + str);
		}
		else {
			currentPieceText.setText("Nothing Selected...");
		}
	}
	
	
	//Shows the user whose turn it is.
	public static void setCurrentTurnText(int currentTurn) {
		String color = (currentTurn % 2 == 0) ? "Black" : "White";
		currentTurnText.setText("(" +color + " to move).");
	}
	
	
	//Shows the user all the taken pieces from each side.
	public static void setTakenPieces() {
		int i = 0;
		int j = 0;
		JButton jb;
		ImageIcon ic;
		Image img;
		Image newimg;
		
		//sorting the black taken list by the chess piece values.
		Collections.sort(ChessBoard.blackTaken, new Comparator<ChessPiece>() {
			@Override
			public int compare(ChessPiece cp1, ChessPiece cp2) {
				if (cp1.getPiece() == cp2.getPiece()) {
					return cp1.getPiece().name().compareTo(cp2.getPiece().name());
				} 
				else {
					return cp1.getPiece().compareTo(cp2.getPiece());
				}
			}
		});
		
		//sorting the white taken list by the chess piece values.
		Collections.sort(ChessBoard.whiteTaken, new Comparator<ChessPiece>() {
			@Override
			public int compare(ChessPiece cp1, ChessPiece cp2) {
				if (cp1.getPiece() == cp2.getPiece()) {
					return cp1.getPiece().name().compareTo(cp2.getPiece().name());
				} 
				else {
					return cp1.getPiece().compareTo(cp2.getPiece());
				}
			}
		});
		
		//resetting gridLayout values for new array list.
		blackPieces.removeAll();
		whitePieces.removeAll();
		
		//Black pieces.
		while (i < 16) {
			//initilizing button specifics
			jb = new JButton();
			jb.setPreferredSize(new Dimension(5, 5));
			jb.setBackground(new Color(238, 238, 238));
			jb.setEnabled(false);
			jb.setBorderPainted(false);
			
			//displays a removed piece.
			if (j < ChessBoard.blackTaken.size()) {
				//getting icon and scaling to size of the button.
				ic = (ImageIcon) ChessBoard.blackTaken.get(i).getIcon();  
				img = ic.getImage();
				newimg = img.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH ) ;  
				ic = new ImageIcon(newimg);
				
				//adding scaled icon to button.
				jb.setDisabledIcon(ic); 
				jb.setIcon(ic);
				blackPieces.add(jb);
				
				j++;
			}
			//displays empty cell.
			else {
				blackPieces.add(jb);
			}
			
			i++;
		}
		
		//White pieces.
		i = 0;
		j = 0;
		while (i < 16) {
			//initilizing button specifics
			jb = new JButton();
			jb.setPreferredSize(new Dimension(5, 5));
			jb.setBackground(new Color(238, 238, 238));
			jb.setEnabled(false);
			jb.setBorderPainted(false);
			
			//displays a removed piece.
			if (j < ChessBoard.whiteTaken.size()) {
				//getting icon and scaling to size of the button.
				ic = (ImageIcon) ChessBoard.whiteTaken.get(i).getIcon();  
				img = ic.getImage();
				newimg = img.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH ) ;  
				ic = new ImageIcon(newimg);
				
				//adding scaled icon to button.
				jb.setDisabledIcon(ic); 
				jb.setIcon(ic);
				whitePieces.add(jb);
				
				j++;
			}
			//displays empty cell.
			else {
				whitePieces.add(jb);
			}
			
			i++;
		}
		
		//sets the piece value for whoever is ahead.
		setChessValues(ChessBoard.blackTaken, ChessBoard.whiteTaken);
	}
	
	
	//calculates the chess piece values and shows the user the value based on who is ahead.
	private static void setChessValues(ArrayList<ChessPiece> blackTaken, ArrayList<ChessPiece> whiteTaken) {
		int result = 0;
		String str = "";
		
		//getting the piece values for each color.
		blackVal = getPieceValue(ChessBoard.whitePieces);
		whiteVal = getPieceValue(ChessBoard.blackPieces);
		
		//calculating difference in value.
		result = Math.abs(blackVal - whiteVal);
		
		blackPointsText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		whitePointsText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		//piece value is tied
		if (result == 0) {
			blackPointsText.setText("+0");
			whitePointsText.setText("+0");
		}
		//shows each players +/- value.
		else {
			str = (blackVal > whiteVal) ? ("+" + result) : ("-");
			whitePointsText.setText(str);
			
			str = (whiteVal > blackVal) ? ("+" + result) : ("-");
			blackPointsText.setText(str);
		}
	}
	
	// calculating the piece value for all active pieces
	private static int getPieceValue(ArrayList<ChessPiece> array) {
		int i = 0;
		int value = 0;
		
		//getting all pieces value.
		while (i < array.size()) {
			switch (array.get(i).getPiece().name()) {
			case "PAWN": 
				value += 1;
				break;
			case ("BISHOP"): 
				value += 3;
			break;
			case ("KNIGHT"):
				value += 3;
			break;
			case ("ROOK"):
				value += 5;
			break;
			case ("QUEEN"):
				value += 9;
			break;
			}
			i++;
		}
		
		return value;
	}
	
	//User wishes to resign the game, sending pop-ups for whichever option clicked.
	public static void resign(int currentTurn) {
		String color;
		int res;
		
		color = (currentTurn % 2 == 0) ? "Black" : "White";
		
		res = JOptionPane.showConfirmDialog(null, color + ", are you sure you wish to resign?", "Resignation", JOptionPane.YES_NO_OPTION);
		
		//user wishes to resign
		if (res == 0) {
			//stopping timers
			timerWhite.stop();
			timerBlack.stop();
			
			//re-scaling draw icon.
			ImageIcon ic = new ImageIcon("res\\checkmate.png");
			Image img = ic.getImage();
			Image newimg = img.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH ) ;  
			ic = new ImageIcon(newimg);
			
			//sends winner popup.
			color = (color.compareTo("Black") == 0) ? "White" : "Black";
			JOptionPane.showOptionDialog(null, "Congratulations " + color + "!\nYou won the game by resignation.", "VICTORY!", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);
		
			//disables board because game is over.
			ChessBoard.disableBoard(MainFrame.chessboard);
					
			//stops the bot, if its a bot game.
			if (HomePage.botGame == true)
				HomePage.botGame = false;
		}
	}
	
	
	//Asks the users if they wish to draw the match.
	public static void draw() {
		int res;
		
		//asks for a draw.
		res = JOptionPane.showConfirmDialog(null, "Are you sure you wish to call a draw?", "Draw?", JOptionPane.YES_NO_OPTION);
		//bot will accept a draw if and only if its down a minimum of 5 point-value.
		if (HomePage.botGame == true) {
			if (ChessBoard.botColor == true) {
				//when there are more black pieces taken then white. 
				int val = whiteVal - blackVal;
				if (val >= -5) {
					System.out.println("\n\n\n\b " + blackVal);
					System.out.println("w " + whiteVal);
					System.out.println("v " + val);
					res = 1;
				}
			}
			else {
				//when there are more white pieces taken then black. 
				int val = blackVal - whiteVal;
				if (val >= -5) {
					res = 1;
				}
			}
		}
		
		if (res == 0) {
			//stopping timers
			timerWhite.stop();
			timerBlack.stop();
			
			//re-scaling draw icon.
			ImageIcon ic = new ImageIcon("res\\draw.png");
			Image img = ic.getImage();
			Image newimg = img.getScaledInstance(125, 125,  java.awt.Image.SCALE_SMOOTH ) ;  
			ic = new ImageIcon(newimg);
			
			//sends draw popup.
			if (HomePage.botGame == true)
				JOptionPane.showOptionDialog(null, "Congratulations you tied! \nThe bot agreed to a draw.", "DRAW!", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);
			else 
				JOptionPane.showOptionDialog(null, "Congratulations you tied! \nYou agreed to a draw.", "DRAW!", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);
			
			//disables board because game is over.
			ChessBoard.disableBoard(MainFrame.chessboard);
			
			//stops the bot, if its a bot game.
			if (HomePage.botGame == true)
				HomePage.botGame = false;
		}
		else {
			if (HomePage.botGame == true) {
				JOptionPane.showOptionDialog(null, "Sorry, the bot declined!", "Play on!", JOptionPane.INFORMATION_MESSAGE, 0, new ImageIcon("res\\robot.png"), new Object[]{}, null);
			}
		}
	}
	
	
	//Showing which # move the players are on.
	public static void setTurnCounter(int currentTurn, boolean valid) {
		boolean current;
		
		//not the first turn.
		//if (currentTurn != 1 && valid == true) {
		if (valid == true) {
		//when the current turn is not divisible by 2 (after blacks turn), increment the turn counter.
			current = (currentTurn % 2 == 0) ? true : false;
			if (current == false) {
				turnCounter++;
				turnText.setText("Turn:" + turnCounter);
			}
		}
	}
	
	
	//Showing the list of moves after each move
	public static void setMoveHistoryList(ChessPiece cur, boolean goTo, int curRow, int curCol, int gotoRow, int gotoCol, boolean inCheck, String castleName, String pawnPromotion) {
		String pieceName = "";
		String result;
		char posChar;
		int j;

		//getting the piece name/notation to show.
		pieceName = cur.getPiece().name();
		switch (pieceName) {
		case "ROOK":
			pieceName = "R";
			break;
		case "KNIGHT":
			pieceName = "N";
			break;
		case "BISHOP":
			pieceName = "B";
			break;
		case "QUEEN":
			pieceName = "Q";
			break;
		case "KING":
			pieceName = "K";
			break;
		default:
			break;
		}

		//getting notations.
		pos = getChessNotation(curRow, curCol);
		move = getChessNotation(gotoRow, gotoCol);
		
		//if its an online game, send move to server
		if (HomePage.botGame == true) {
					
		}
		
		//incase of castle long/short.
		if (castleName == "Long" || castleName == "Short") {
			if (castleName == "Long") {
				result = "O-O-O";
			}
			else {
				result = "O-O";
			}
		}
		//when piece is a pawn, it just shows the move. Or if its promoting it has special notation.
		else if (pieceName == "PAWN" || pawnPromotion.isEmpty() != true) {
			//en-passant.
			if (Pawn.enPassant == true) {
				posChar = pos.charAt(0);
				result = posChar + "x" + move;
			}
			//other pawn movements.
			else {
				//piece taken
				if (goTo == true) {
					posChar = pos.charAt(0);
					result = posChar + "x" + move;
				}
				//no piece taken
				else {
					result = move;
				}
				
				//when the pawn promotes add special notation.
				if (pawnPromotion == "Queen" || pawnPromotion == "Rook" || pawnPromotion == "Knight" || pawnPromotion == "Bishop") {
					result += "=" + pawnPromotion.charAt(0);
					//resetting value.
					ChessBoard.pawnPromotion = "";
				}
			}
		}
		//any other piece.
		else {
			//when the goto cell contains another piece, add an "x" between the piece and move.
			if (goTo == true) {
				result = pieceName + "x" + move;
			}
			//just shows the move after the piece.
			else {
				result = pieceName + move;
			}
		}
		
		//adding the move string onto arraylist, adding check notation if so.
		if (inCheck == true) {
			if (King.itsCheckmate == true) {
				result += "#";
			}
			else {
				result += "+";
			}
		}
		//adding to move list
		totalMoves.add(result);
		
		
		//print 2 moves onto 1 line, until the total moves are done.
		j = 0;
		result = "";
		while (n < (totalMoves.size())) {
			j = 0;
			//result += turnCounter + ".  ";
			while (j < 2) {
				//both moves were set.
				if (totalMoves.size() % 2 == 0) {
					//first and second were set.
					if (j == 1) {
						result += "	   " + totalMoves.get(n-1 + j) + "\n";
						finalString += result;
						n++;
					}
					System.out.println(result);
					j++;
				}
				//first move was set.
				else {

					result += " " + turnCounter + ".  ";
					result += totalMoves.get(n);
					finalString += result;
					n++;
					break;
				}
			}
			break;
		}
		
		//System.out.println("Final String: " + finalString);
		
		//adding all the moves to the scroll pane.
		movesListText.setText(" Move History: \n" + finalString);
		

		//adding new list to content pane, changing it to a scroll bar also.
		JScrollPane jsp = new JScrollPane(movesListText);
		contentPane.add(jsp, BorderLayout.EAST);

	}

	
	//Setting the users timers, stopping them and starting them based off of whos turn it is.
	public static void setTimers(int currentTurn) {
		boolean cur;
		
		cur = (currentTurn % 2 == 0) ? true : false;
		//blacks turn
		if (cur == true) {
			timerWhite.stop();
			timerBlack.start();
		}
		//whites turn
		else {
			timerBlack.stop();
			timerWhite.start();
		}
	}
	
	
	//pausing timers until connection is established (for online game)
	public static void pauseTimers() {
		timerWhite.stop();
		timerBlack.stop();
	}
	
	
	//Changes the pieces x and y location to chess notation.
	public static String getChessNotation(int row, int col) {
		String str = "";
		String c = "";
		
		System.out.println("row: " + row);
		System.out.println("col: " + col);
		
		//Numerical value.
		switch (row) {
		case 0:
			row = 8;
			break;
		case 1:
			row = 7;
			break;
		case 2:
			row = 6;
			break;
		case 3:
			row = 5;
			break;
		case 4:
			row = 4;
			break;
		case 5:
			row = 3;
			break;
		case 6:
			row = 2;
			break;
		case 7: 
			row = 1;
			break;
		}
		
		//Alphabetical value	
		switch (col) {
		case 0:
			c = "a";
			break;
		case 1:
			c = "b";
			break;
		case 2:
			c = "c";
			break;
		case 3:
			c = "d";
			break;
		case 4:
			c = "e";
			break;
		case 5:
			c = "f";
			break;
		case 6:
			c = "g";
			break;
		case 7: 
			c = "h";
			break;
		}
		
		str = c + Integer.toString(row);
		return str;
	}
	
	
	
	
}
