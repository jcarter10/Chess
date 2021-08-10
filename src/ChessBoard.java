/*
 * By: Jordan Carter
 * Description: Most important class of project, contains all of the functions neccesary to run the chess game and holds all of the board data.
 * 
 */


import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class ChessBoard implements ActionListener{

	//Globals
	public static int N = 8;
	public static int currentTurn = 1;	//white starts always
	public static int whitePiecesTaken = 0;
	public static int blackPiecesTaken = 0;
	public static int moveRule50 = 0;
	
	public static boolean valid = false;
	public static boolean inCheck = false;
	public static boolean castled;
	public static boolean botColor;
	public static boolean opponentColor;
	public static boolean checkmate = false;
	
	public static String castleName = "";
	public static String pawnPromotion = "";
	public static String pov = "white";
	
	public static JButton[][] grid = new JButton[8][8];
	
	//arraylists for holding all the pieces
	public static ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();
	public static ArrayList<ChessPiece> whitePieces = new ArrayList<ChessPiece>();
	public static ArrayList<ChessPiece> blackPieces = new ArrayList<ChessPiece>();
	//Pieces to be added to the pieces ArrayList.
	ChessPiece kb, qb, rb1, rb2, nb1, nb2, bb1, bb2, pb1, pb2, pb3, pb4, pb5, pb6, pb7, pb8,
		       kw, qw, rw1, rw2, nw1, nw2, bw1, bw2, pw1, pw2, pw3, pw4, pw5, pw6, pw7, pw8;
	
	//arrayLists for pieces taken.
	public static ArrayList<ChessPiece> whiteTaken = new ArrayList<ChessPiece>();
	public static ArrayList<ChessPiece> blackTaken = new ArrayList<ChessPiece>();
	
	//arraylists to hold bot move/piece information
	public static ArrayList<ChessPiece> possibleAIChessPiece = new ArrayList<ChessPiece>();
	public static ArrayList<Integer> possibleAIMovementsX = new ArrayList<Integer>();
	public static ArrayList<Integer> possibleAIMovementsY = new ArrayList<Integer>();
	public static ArrayList<Integer> possibleMoveValue = new ArrayList<Integer>();
	
	//board colors
	public static Color darkC = new Color(181, 136, 99);
	public static Color lightC = new Color(240, 217, 181);

	//important variables that store data for the current move
	static JButton jb_current = null;
	static JButton jb_goto = null;
	public static int jb_currentRow = -1; //location of current in 2d array.
	public static int jb_currentCol = -1;
	public static int jb_gotoRow = -1; //location of goto in 2d array.
	public static int jb_gotoCol = -1;
	
	public static int curPiecePos;	//position of current piece in arraylist.
	public static int gotoPiecePos;	//position of goto piece in arraylist.

	//Random piece instead of using null for each value.
	ChessPiece nullFill = new Pawn(100, 100, ChessPiece.pType.PAWN, true, new ImageIcon("res\\Chess_pdt60.png"), 0);

	//Constructor
	public ChessBoard() {
		//timers are paused until white makes the first move.
		Header.pauseTimers();
		
		//creates the empty board disregarding pieces.
		createData();

		//creates the pieces and their respective cells.
		createStartPieces();
		
		//sets the color for the MainPlayer first
		if (GuestPlayer.guest == false) {
			//Creates the starting chess board onto the Main Frame (whites pov).
			createBoardWhiteside(MainFrame.chessboard);
			
			//Pop-up to ask user if they want black or white.
			String[] options = {"White", "Black"};
			int side = (JOptionPane.showOptionDialog(null, "Which side do you want?.", "Pick your side.",
					JOptionPane.YES_NO_OPTION, 0, new ImageIcon("res\\board.png"), options, options[0]));

			//creates board as blacks pov if wanted.
			if (side == 1) {
				//clears board of whites pov.
				clearBoard(MainFrame.chessboard);
				//sets new black pov.
				createBoardBlackside(MainFrame.chessboard);
				//set position units around cehssboard.
				MainFrame.resetValuesForBlackPOV(MainFrame.numValues, MainFrame.alphValues);
				
				//main color = black, so bot color = white.
				if (HomePage.botGame == true)
					botColor = false;
				//main wants black.
				if (HomePage.onlineGame == true) {
					MainPlayer.myColor = true;
					MainPlayer.myTurn = true;
				}
			}
			else {
				if (HomePage.botGame == true)  {
					botColor = true;
				}
			}
		}
		//if it turns out to be an online game, there is a guest player, so set its board accordingly.
		else {
			if (GuestPlayer.myColor == true) {
				createBoardBlackside(MainFrame.chessboard);
			}
			else {
				createBoardWhiteside(MainFrame.chessboard);
			}
		}
	}


	//Creates the buttons representing the board data.
	public void createData() {
		int i;
		boolean lastColor = true;
		int rowCount = 2;
		int colCount = 0;

		//loops through 64 times to set each cell color.
		for (i = 17; i < (N * 6) + 1; i++) {
			grid[rowCount][colCount] = new JButton("");

			//the first cell of each row must be the same color as the last cell of the previous row. 
			if (i == 9 || i == 17 || i == 25 || i == 33 || i == 41 || i == 49 || i == 57) {
				if (lastColor == true) {
					grid[rowCount][colCount].setBackground(lightC);

				}
				else {
					grid[rowCount][colCount].setBackground(darkC);
				}
			}
			//if the last cell was light grey, new cell is green.
			else if (lastColor == false) {
				grid[rowCount][colCount].setBackground(lightC);
				lastColor = true;
			}
			//if the last cell was green, new cell is light grey.
			else {
				grid[rowCount][colCount].setBackground(darkC);
				lastColor = false;
			}

			colCount++;
			//resetting the column count for the next row, and setting row count to next row.
			if (colCount == 8) {
				colCount = 0;
				rowCount++;
			}
		}
	}


	//Sets chess pieces into their respective starting positions as well as create their ChessPiece object.
	public void createStartPieces() {
		Icon icon;

		//Setting images to respective cells (1-16 and 49-64)
		//Black Pieces (1-16)
		//Rook, Knight, Bishop, Queen, King
		icon = new ImageIcon("res\\Chess_rdt60.png");
		grid[0][0] = new JButton(icon);
		grid[0][0].setBackground(lightC);
		grid[0][0].setFocusPainted(false);
		rb1 = new Rook(0, 0, ChessPiece.pType.ROOK, true, icon, 0);
		pieces.add(rb1);

		icon = new ImageIcon("res\\Chess_ndt60.png");
		grid[0][1] = new JButton(icon);
		grid[0][1].setBackground(darkC);
		grid[0][1].setFocusPainted(false);
		nb1 = new Knight(0, 1, ChessPiece.pType.KNIGHT, true, icon, 0);
		pieces.add(nb1);

		icon = new ImageIcon("res\\Chess_bdt60.png");
		grid[0][2] = new JButton(icon);
		grid[0][2].setBackground(lightC);
		grid[0][2].setFocusPainted(false);
		bb1 = new Bishop(0, 2, ChessPiece.pType.BISHOP, true, icon, 0);
		pieces.add(bb1);

		icon = new ImageIcon("res\\Chess_qdt60.png");
		grid[0][3] = new JButton(icon);
		grid[0][3].setBackground(darkC);
		grid[0][3].setFocusPainted(false);
		qb = new Queen(0, 3, ChessPiece.pType.QUEEN, true, icon, 0);
		pieces.add(qb);

		icon = new ImageIcon("res\\Chess_kdt60.png");
		grid[0][4] = new JButton(icon);
		grid[0][4].setBackground(lightC);
		grid[0][4].setFocusPainted(false);
		kb = new King(0, 4, ChessPiece.pType.KING, true, icon, 0);
		pieces.add(kb);

		icon = new ImageIcon("res\\Chess_bdt60.png");
		grid[0][5] = new JButton(icon);
		grid[0][5].setBackground(darkC);
		grid[0][5].setFocusPainted(false);
		bb2 = new Bishop(0, 5, ChessPiece.pType.BISHOP, true, icon, 0);
		pieces.add(bb2);

		icon = new ImageIcon("res\\Chess_ndt60.png");
		grid[0][6] = new JButton(icon);
		grid[0][6].setBackground(lightC);
		grid[0][6].setFocusPainted(false);
		nb2 = new Knight(0, 6, ChessPiece.pType.KNIGHT, true, icon, 0);
		pieces.add(nb2);

		icon = new ImageIcon("res\\Chess_rdt60.png");
		grid[0][7] = new JButton(icon);
		grid[0][7].setBackground(darkC);
		grid[0][7].setFocusPainted(false);
		rb2 = new Rook(0, 7, ChessPiece.pType.ROOK, true, icon, 0);
		pieces.add(rb2);

		//Pawns
		icon = new ImageIcon("res\\Chess_pdt60.png");
		for (int i = 0; i < 8; i++) {
			grid[1][i] = new JButton(icon);
			grid[1][i].setFocusPainted(false);
			if (i % 2 == 0) {
				grid[1][i].setBackground(darkC);
			}
			else {
				grid[1][i].setBackground(lightC);
			}
		}
		pb1 = new Pawn(1, 0, ChessPiece.pType.PAWN, true, icon, 0); pb2 = new Pawn(1, 1, ChessPiece.pType.PAWN, true, icon, 0); pb3 = new Pawn(1, 2, ChessPiece.pType.PAWN, true, icon, 0); pb4 = new Pawn(1, 3, ChessPiece.pType.PAWN, true, icon, 0); 
		pb5 = new Pawn(1, 4, ChessPiece.pType.PAWN, true, icon, 0); pb6 = new Pawn(1, 5, ChessPiece.pType.PAWN, true, icon, 0); pb7 = new Pawn(1, 6, ChessPiece.pType.PAWN, true, icon, 0); pb8 = new Pawn(1, 7, ChessPiece.pType.PAWN, true, icon, 0);
		pieces.add(pb1); pieces.add(pb2); pieces.add(pb3); pieces.add(pb4); pieces.add(pb5); pieces.add(pb6); pieces.add(pb7); pieces.add(pb8);



		//White Pieces (49-64)
		//Pawns
		icon = new ImageIcon("res\\Chess_plt60.png");
		for (int i = 0; i < 8; i++) {
			grid[6][i] = new JButton(icon);
			grid[6][i].setFocusPainted(false);
			if (i % 2 == 0) {
				grid[6][i].setBackground(lightC);
			}
			else {
				grid[6][i].setBackground(darkC);
			}
		}
		pw1 = new Pawn(6, 0, ChessPiece.pType.PAWN, false, icon, 0); pw2 = new Pawn(6, 1, ChessPiece.pType.PAWN, false, icon, 0); pw3 = new Pawn(6, 2, ChessPiece.pType.PAWN, false, icon, 0); pw4 = new Pawn(6, 3, ChessPiece.pType.PAWN, false, icon, 0); 
		pw5 = new Pawn(6, 4, ChessPiece.pType.PAWN, false, icon, 0); pw6 = new Pawn(6, 5, ChessPiece.pType.PAWN, false, icon, 0); pw7 = new Pawn(6, 6, ChessPiece.pType.PAWN, false, icon, 0); pw8 = new Pawn(6, 7, ChessPiece.pType.PAWN, false, icon, 0);
		pieces.add(pw1); pieces.add(pw2); pieces.add(pw3); pieces.add(pw4); pieces.add(pw5); pieces.add(pw6); pieces.add(pw7); pieces.add(pw8);

		//Rook, Knight, Bishop, Queen, King
		icon = new ImageIcon("res\\Chess_rlt60.png");
		grid[7][0] = new JButton(icon);
		grid[7][0].setBackground(darkC);
		grid[7][0].setFocusPainted(false);
		rw1 = new Rook(7, 0, ChessPiece.pType.ROOK, false, icon, 0);
		pieces.add(rw1);

		icon = new ImageIcon("res\\Chess_nlt60.png");
		grid[7][1] = new JButton(icon);
		grid[7][1].setBackground(lightC);
		grid[7][1].setFocusPainted(false);
		nw1 = new Knight(7, 1, ChessPiece.pType.KNIGHT, false, icon, 0);
		pieces.add(nw1);

		icon = new ImageIcon("res\\Chess_blt60.png");
		grid[7][2] = new JButton(icon);
		grid[7][2].setBackground(darkC);
		grid[7][2].setFocusPainted(false);
		bw1 = new Bishop(7, 2, ChessPiece.pType.BISHOP, false, icon, 0);
		pieces.add(bw1);

		icon = new ImageIcon("res\\Chess_qlt60.png");
		grid[7][3] = new JButton(icon);
		grid[7][3].setBackground(lightC);
		grid[7][3].setFocusPainted(false);
		qw = new Queen(7, 3, ChessPiece.pType.QUEEN, false, icon, 0);
		pieces.add(qw);

		icon = new ImageIcon("res\\Chess_klt60.png");
		grid[7][4] = new JButton(icon);
		grid[7][4].setBackground(darkC);
		grid[7][4].setFocusPainted(false);
		kw = new King(7, 4, ChessPiece.pType.KING, false, icon, 0);
		pieces.add(kw);

		icon = new ImageIcon("res\\Chess_blt60.png");
		grid[7][5] = new JButton(icon);
		grid[7][5].setBackground(lightC);
		grid[7][5].setFocusPainted(false);
		bw2 = new Bishop(7, 5, ChessPiece.pType.BISHOP, false, icon, 0);
		pieces.add(bw2);

		icon = new ImageIcon("res\\Chess_nlt60.png");
		grid[7][6] = new JButton(icon);
		grid[7][6].setBackground(darkC);
		grid[7][6].setFocusPainted(false);
		nw2 = new Knight(7, 6, ChessPiece.pType.KNIGHT, false, icon, 0);
		pieces.add(nw2);

		icon = new ImageIcon("res\\Chess_rlt60.png");
		grid[7][7] = new JButton(icon);
		grid[7][7].setBackground(lightC);
		grid[7][7].setFocusPainted(false);
		rw2 = new Rook(7, 7, ChessPiece.pType.ROOK, false, icon, 0);
		pieces.add(rw2);
	}

	//Updates the board from the 2-d array representing our board data, and setting the action listeners as WHITES POV.
	public void createBoardWhiteside(JPanel jp) {
		jp.setVisible(false);
		//sets the data to the board (white POV).
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				grid[i][j].addActionListener(this);
				jp.add(grid[i][j]);
			}
		}
		jp.setVisible(true);
	}

	//Updates the board from the 2-d array representing our board data, and setting the action listeners as BLACKS POV.
	public void createBoardBlackside(JPanel jp) {
		jp.setVisible(false);
		//sets the data to the board as blacks POV.
		for (int i = N - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				grid[i][j].addActionListener(this);
				jp.add(grid[i][j]);
			}
		}
		jp.setVisible(true);
	}


	//Clears the board when needed, called before we switch from white to black pov or vice versa.
	public void clearBoard(JPanel jp) {
		//clears/unsets board for new pov.
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				grid[i][j].addActionListener(this);
				jp.remove(grid[i][j]);
			}
		}
	}


	//Action performed for any button on the chessboard being pressed.
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("\nMove Counter: " + currentTurn);

		//showing user which colors turn it is.
		Header.setCurrentTurnText(currentTurn);

		//getting the current sides turn.
		boolean cur = (currentTurn % 2 == 0) ? true : false;

		//only running this section of the code when the user is making the moves and not the bot/ai.
		if (HomePage.customGame == true || (cur != botColor && HomePage.botGame == true) || ((MainPlayer.myTurn == true || GuestPlayer.myTurn == true) && HomePage.onlineGame == true)) {
			//button data and borders get reset once a full move has been calculated. 
			if (jb_currentRow != -1 && jb_currentCol != -1 && jb_gotoRow != -1 && jb_gotoCol != -1) {
				//before the data reset, unset the highlights on the cells for the previous move.
				grid[jb_currentRow][jb_currentCol].setBorder(null); 
				grid[jb_gotoRow][jb_gotoCol].setBorder(null);

				//resets data for next move/actionPerformed.
				resetVariables();
			}

			//action performed for first button.
			if (jb_current == null) {
				System.out.println("First Cell Clicked!");
				//Checking to see which button was clicked.
				jb_current = findMatch(e);

				//the button is clicked, and does not contain a piece, nothing happens and the user can try again.
				if (jb_current.getIcon() == null) {
					System.out.println("Empty Selection."); 
					resetVariables();
				}
				//when a piece is found, find its location in the pieces list.
				else {
					//finding which chess piece was clicked from Pieces (arraylist).
					curPiecePos = getChessPiece(jb_currentRow, jb_currentCol);

					//setting border highlights for first clicked button and disabling icon border.
					grid[jb_currentRow][jb_currentCol].setFocusPainted(false);
					grid[jb_currentRow][jb_currentCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 

					//checking to see if the pieces color matches the turn color.
					Boolean color = (currentTurn % 2 == 0) ? true : false;
					//it doesn't match the color so reset for next move.
					if (color != pieces.get(curPiecePos).getColor()) {
						grid[jb_currentRow][jb_currentCol].setBorder(null); 
						resetVariables();
					}
					//colors match, valid selection.
					else {
						//showing all potential moves for the selected piece.
						addPotentialMoves(pieces.get(curPiecePos));

						//showing user the selected piece if the selected piece is a valid selection.
						Header.setSelectedPieceText(pieces.get(curPiecePos).getPiece().name(), 1, pieces.get(curPiecePos).getRow(), pieces.get(curPiecePos).getCol());
					}
				}
			}
			//action performed for second button, only goes to this when the first button was valid. 
			else {
				System.out.println("Second Cell Clicked!");
				jb_goto = findMatch(e);
				Header.setSelectedPieceText("", 0, 0, 0);
				gotoPiecePos = getChessPiece(jb_gotoRow, jb_gotoCol);
				//System.out.println("gotoPiecePos: " + gotoPiecePos);

				//if the chess piece is clicked again, it resets.
				if (gotoPiecePos != -1 && (pieces.get(gotoPiecePos) == pieces.get(curPiecePos))) {
					System.out.println("Same cell was clicked... Try again");
					//removing all potential move highlights for the selected piece since clicked again.
					removePotentialMoves(pieces.get(curPiecePos));

					grid[jb_currentRow][jb_currentCol].setBorder(null); 
					resetVariables();
				}
				//determine if the piece can move to cell.
				else {
					//removing all potential move highlights for the selected piece.
					removePotentialMoves(pieces.get(curPiecePos));

					//if other button contains a piece.
					checkPieceValidity(curPiecePos, gotoPiecePos);	

					//if move was valid, setting border highlights for second clicked button and disabling icon border.
					if (valid == true) {

						//setting border highlights for second clicked button and disabling icon border.
						if (castled == false) {
							grid[jb_gotoRow][jb_gotoCol].setFocusPainted(false);
							grid[jb_gotoRow][jb_gotoCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
						}
					}
					//move was invalid, resetting clicked button.
					else {
						grid[jb_currentRow][jb_currentCol].setBorder(null); 
					}

					//showing the move/turn counter for users.
					Header.setTurnCounter(currentTurn, valid);

					//setting each players timer.
					Header.setTimers(currentTurn);

					//checking for a stalemate at the end of each turn.
					stalemateCheck();
				}
				
				//if it's an online game send your move to .
				if (valid == true && HomePage.onlineGame == true && jb_goto != null && jb_current != null) {
					//grabbing the data for the move that was just processed.
					ArrayList<Integer> ar = new ArrayList<>(Arrays.asList(jb_currentRow, jb_currentCol, jb_gotoRow, jb_gotoCol));

					//sending the move data to opponent.
					if (MainPlayer.myTurn == true) {
						try {
							//notify guest that it's their turn.
							MainPlayer.sendTurn();
							
							//send move data to guest.
							MainPlayer.sendMove(ar);
							
							//highlighting second button click.
							grid[jb_gotoRow][jb_gotoCol].setFocusPainted(false);
							grid[jb_gotoRow][jb_gotoCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					else {
						try {
							//notify main player that it's their turn.
							GuestPlayer.sendTurn();
							
							//send move data to main.
							GuestPlayer.sendMove(ar);
							
							//highlighting second button click.
							grid[jb_gotoRow][jb_gotoCol].setFocusPainted(false);
							grid[jb_gotoRow][jb_gotoCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
				}
			}

			//getting who's current turn it is for bot game.
			cur = (currentTurn % 2 == 0) ? true : false;
			System.out.println("Current Turn Color: " + cur);

			//if its a bot game, and when its the bots turn it calculates all it's potential moves and makes a move.
			if (botColor == cur && HomePage.botGame == true && jb_goto != null && jb_current != null) {
				System.out.println("\nEntering AI part.");

				//clears other colors move before it makes its own.
				grid[jb_currentRow][jb_currentCol].setBorder(null); 
				grid[jb_gotoRow][jb_gotoCol].setBorder(null);

				calculateAllPossibleMoves();

				System.out.println("\nEnding AI part.");

				//showing user which colors turn it is.
				Header.setCurrentTurnText(currentTurn);

				//showing the move/turn counter for users.
				Header.setTurnCounter(currentTurn, valid);

				//setting each players timer.
				Header.setTimers(currentTurn);
				
				stalemateCheck();
			}
		}
	}


	//resets important variables for next move.
	public static void resetVariables() {
		jb_current = null;
		jb_goto = null;
		jb_currentRow = -1;	
		jb_currentCol = -1;
		jb_gotoRow = -1;		
		jb_gotoCol = -1;
		curPiecePos = 0;	
		gotoPiecePos = 0;
		King.potentialMovesBlack = 0;
		King.potentialMovesWhite = 0;
	}


	//Finds a match between the button clicked and the buttons in our grid, returns the button data.
	public JButton findMatch(ActionEvent e) {
		JButton jb = null;

		//searches through each element of array.
		for (int i = 0; i < N; i++) {
			//when a match is found, force exit loop.
			if (jb != null) {
				break;
			}
			for (int j = 0; j < N; j++) {
				//when a match is found.
				if (grid[i][j] == e.getSource()) {
					//System.out.println("Selection matches a cell on grid");
					jb = grid[i][j];
					
					//if the current (1st) button is not set, set it.
					if (jb_currentRow == -1 && jb_currentCol == -1) {
						jb_currentRow = i;
						jb_currentCol = j;
						//System.out.println("current: " + i + " and " + j);
						break;
					}
					//when the first button is set, second button gets set instead.
					else {
						jb_gotoRow = i;
						jb_gotoCol = j;
						//System.out.println("goto: " + i + " and " + j);
						break;
					}
				}
			}
		}
//		System.out.println(e.getSource());
		return jb;
	}


	//Matches the piece on the button clicked, that corresponds with the piece in the ChessPiece arraylist.
	public static int getChessPiece(int row, int col) {
		int i;
		//searches list for match
		for (i = 0; i < pieces.size(); i++) 
		{ 		      
			//System.out.println(pieces.get(i).toString());
			if (pieces.get(i).getRow() == row) {
				if (pieces.get(i).getCol() == col) {
					break;
				}
			}		
		}
		//no match found.
		if (i == pieces.size()) {
			//System.out.println("No matching piece! It is empty.");
			return -1;
		}
		//match found.
		else {
			//System.out.println("Piece is at position: " +i+ " on array");
			return i;
		}
	}


	//Prints out info of all the current pieces
	public static void printPieces() {
		for (int i = 0; i < pieces.size(); i++) 
		{ 		      
			System.out.println(pieces.get(i).toString()); 		
		}   
	}


	//Checks to see which piece was chosen, calls its respective fucntion to check the move validity, if allowed the piece moves. 
	public static boolean checkPieceValidity(int curPos, int gotoPos) {
		castled = false;
		ChessPiece king;
		ChessPiece.pType pieceType;
		ChessPiece tempGotoChessPiece = null;
		ChessPiece tempCurChessPiece = null;
		//backup values, incase of checks and I have to move pieces back to their original locations.
		int tempCurPos = 0;
		int tempGotoPos = 0;
		int tempCurRow = 0;
		int tempCurCol = 0;
		int tempGotoRow = 0;
		int tempGotoCol = 0;
		int tempGotoIndex = 0;

//		System.out.println(curPos + "    " + gotoPos); 
		pieceType = pieces.get(curPos).getPiece();

		//getting current pieces type.
		switch (pieceType) {
		case PAWN:
			//incase the current piece is trying to take another, we send the piece info to check for a valid move.
			if (gotoPos == -1) {
				//not the best-solution but sending pieces.get(curPos) again, instead of null, so I don't have to try-catch the null.
				valid = Pawn.checkValidity(pieces.get(curPos), pieces.get(curPos), jb_gotoRow, jb_gotoCol);	
		
				if (Pawn.enPassant == true) {
					//calling this for Header.setList later on.
					tempCurChessPiece = pieces.get(curPos);
					tempCurRow = jb_currentRow;
					tempCurCol = jb_currentCol;
					tempGotoRow = jb_gotoRow;
					tempGotoCol = jb_gotoCol;	

					//resetting variable for next instance of enpassant.
					Pawn.nextMovePotentialEnpassant = false;
				}
			}
			else {
				valid = Pawn.checkValidity(pieces.get(curPos), pieces.get(gotoPos), jb_gotoRow, jb_gotoCol);
			}

			break;
		case ROOK:
			//incase the current piece is trying to take another, we send the piece info to check for a valid move.
			if (gotoPos == -1) {
				valid = Rook.checkValidity(pieces.get(curPos), pieces.get(curPos), jb_gotoRow, jb_gotoCol);			
			}
			else {
				valid = Rook.checkValidity(pieces.get(curPos), pieces.get(gotoPos), jb_gotoRow, jb_gotoCol);
			}

			break;
		case KNIGHT:
			//incase the current piece is trying to take another, we send the piece info to check for a valid move.
			if (gotoPos == -1) {
				valid = Knight.checkValidity(pieces.get(curPos), pieces.get(curPos), jb_gotoRow, jb_gotoCol);			
			}
			else {
				valid = Knight.checkValidity(pieces.get(curPos), pieces.get(gotoPos), jb_gotoRow, jb_gotoCol);
			}

			break;
		case BISHOP:
			//incase the current piece is trying to take another, we send the piece info to check for a valid move.
			if (gotoPos == -1) {
				valid = Bishop.checkValidity(pieces.get(curPos), pieces.get(curPos), jb_gotoRow, jb_gotoCol);			
			}
			else {
				valid = Bishop.checkValidity(pieces.get(curPos), pieces.get(gotoPos), jb_gotoRow, jb_gotoCol);
			}

			break;
		case QUEEN:
			//incase the current piece is trying to take another, we send the piece info to check for a valid move.
			if (gotoPos == -1) {
				valid = Queen.checkValidity(pieces.get(curPos), pieces.get(curPos), jb_gotoRow, jb_gotoCol);			
			}
			else {
				valid = Queen.checkValidity(pieces.get(curPos), pieces.get(gotoPos), jb_gotoRow, jb_gotoCol);
			}

			break;
		case KING:
			//incase the current piece is trying to take another, we send the piece info to check for a valid move.
			if (gotoPos == -1) {
				valid = King.checkValidity(pieces.get(curPos), pieces.get(curPos), jb_gotoRow, jb_gotoCol);			
			}
			else {
				valid = King.checkValidity(pieces.get(curPos), pieces.get(gotoPos), jb_gotoRow, jb_gotoCol);

				//checking for a castle.
				if (pieces.get(curPos).getPiece().name() == "KING" && pieces.get(gotoPos).getPiece().name() == "ROOK") {
					//user is trying to castle.
					if (valid == true && inCheck != true) {
						//calling this for Header.setList later on.
						tempCurChessPiece = pieces.get(curPos);

						castled = castleCheck(curPos, gotoPos);
					}
					//user tried to castle but is in check so cannot.
					else if (valid == true && inCheck == true) {
						System.out.println("You can't castle when in check!");
						//resetting move counters, for next attempt of castling.
						pieces.get(curPos).decrementMoveCount();
						pieces.get(gotoPos).decrementMoveCount();
						return false;
					}
				}
			}

			break;
		default:
			System.out.println("No piece was found that matches the description...");

		}

		//if the move is valid, the piece moves to desired location/takes piece at location.
		if (valid == true && castled != true && Pawn.enPassant != true) {
			if (gotoPos != -1) {
				//saving backup variables incase of checks, used to move pieces back to original locations.
				tempCurPos = curPos;
				tempGotoPos = gotoPos;
				tempCurRow = jb_currentRow;
				tempCurCol = jb_currentCol;
				tempGotoRow = jb_gotoRow;
				tempGotoCol = jb_gotoCol;
				tempGotoIndex = tempGotoPos;
				tempCurChessPiece = pieces.get(tempCurPos);
				tempGotoChessPiece = pieces.get(tempGotoIndex);

				moveToCell(pieces.get(curPos), pieces.get(gotoPos), jb_currentRow, jb_currentCol, jb_gotoRow, jb_gotoCol);
				//currentTurn++;
			}
			else {
				//saving backup variables incase of checks, used to move pieces back to original locations.
				tempCurPos = curPos;
				tempCurRow = jb_currentRow;
				tempCurCol = jb_currentCol;
				tempGotoRow = jb_gotoRow;
				tempGotoCol = jb_gotoCol;
				tempCurChessPiece = pieces.get(tempCurPos);
				moveToEmptyCell(pieces.get(curPos), jb_currentRow, jb_currentCol, jb_gotoRow, jb_gotoCol);
				//currentTurn++;
			}
		}

		//looking for checks on current king.
		king = (currentTurn % 2 == 0) ? getKing(true) : getKing(false);

		//checking to see if its in check/checkmate.
		inCheck = King.checksOnKing(king);

		//if its in check, puts the piece and/or pieces back to original locations using backup variables for the pieces (ignores if the move is valid or not).
		if (inCheck == true) {
			//case for no taken piece.
			if (gotoPos == -1) {
				//invalid move due to check so moving piece back.
				moveToEmptyCell(pieces.get(tempCurPos), tempGotoRow, tempGotoCol, tempCurRow, tempCurCol);
				pieces.get(tempCurPos).decrementMoveCount();
				grid[jb_gotoRow][jb_gotoCol].setBorder(null); 
				valid = false;
			}
			//case for a taken piece (case is only possible when the move is valid and the move went through already).
			else if (valid == true) {
				//adding removed piece back onto list, and taking piece off of the piece taken list.
				pieces.add(tempGotoIndex, tempGotoChessPiece); 

				//black pieces
				if (pieces.get(tempGotoIndex).getColor() == true) {
					blackTaken.remove(pieces.get(tempGotoIndex));
					//blackPiecesTaken--;
				}
				//white pieces
				else {
					whiteTaken.remove(pieces.get(tempGotoIndex));
					//whitePiecesTaken--;
				}

				//invalid move due to check so moving pieces back.
				movePiecesBack(pieces.get(tempCurPos), pieces.get(tempGotoPos), tempCurRow, tempCurCol, tempGotoRow, tempGotoCol);
				pieces.get(tempCurPos).decrementMoveCount();
				valid = false;
			}
			System.out.println("\nYou can't move there, you are putting yourself in check!");
		}
		//no checks on itself after move, so checking other king for checks.
		else {
			//looking for checks on enemy king, if the current king didn't put itself in check.
			king = (currentTurn % 2 != 0) ? getKing(true) : getKing(false);

			//checking to see if its in check/checkmate.
			inCheck = King.checksOnKing(king);

			if (inCheck == true) {
				currentTurn++;
			}
			//no checks on itself after move, so other players turn.
			else {
				//move is valid, its the other colors turn (else: it stays the same turn).
				if (valid == true) {
					currentTurn++;
				}
			}

			if ((inCheck == true && valid == true) || valid == true) {
				//showing the user all the moves, if the move was correct/allowed.
				if (tempGotoChessPiece != null) {
					Header.setMoveHistoryList(tempCurChessPiece, true, tempCurRow, tempCurCol, tempGotoRow, tempGotoCol, inCheck, castleName, pawnPromotion);
				}
				else {
					Header.setMoveHistoryList(tempCurChessPiece, false, tempCurRow, tempCurCol, tempGotoRow, tempGotoCol, inCheck, castleName, pawnPromotion);
				}
				castleName = "";
			}

		}

		System.out.println("WhitePieces taken:");
		for (int i = 0; i < whiteTaken.size(); i++) 
		{ 		      
			System.out.println(whiteTaken.get(i).toString()); 		
		}   
		System.out.println("BlackPieces taken:");
		for (int i = 0; i < blackTaken.size(); i++) 
		{ 		      
			System.out.println(blackTaken.get(i).toString()); 		
		}   


		//when move is valid, set information in header.
		if (valid == true) {
			//setting arrayLists for each color.
			setPieceArray(whitePieces, false);
			setPieceArray(blackPieces, true);

			//showing the taken pieces for users.
			Header.setTakenPieces();

			//incrementing counter because no pawn move was made.
			if (pieceType != ChessPiece.pType.PAWN)
				moveRule50++;
			//resetting the 50 move counter because a pawn was moved.
			else 
				moveRule50 = 0;

			//need to implement something to check if a piece was taken or not, if a piece was captured then the count resets.

		}

		//resetting values.
		Pawn.enPassant = false;

		return valid;
	}


	//Moving the piece to its wanted/selected location that contains another piece.
	public static void moveToCell(ChessPiece cur, ChessPiece newP, int oldRow, int oldCol, int newRow, int newCol) {

		//Wanted cell contains a piece, so the taken pieces goes into their respective arraylists.
		//black piece
		if (newP.getColor() == true) {
			blackTaken.add(newP);
			//blackPiecesTaken++;
		}
		//white pieces
		else {
			whiteTaken.add(newP);
			//whitePiecesTaken++;
		}

		//setting current piece to new cell for next update.
		grid[newRow][newCol].setIcon(grid[oldRow][oldCol].getIcon());

		//updating the objects position.
		cur.setRow(newRow);
		cur.setCol(newCol);

		//clear icon of old cell.
		grid[oldRow][oldCol].setIcon(null);

		//removing taken piece from list.
		pieces.remove(pieces.indexOf(newP));

		//resetting the 50 move counter because a piece was taken.
		moveRule50 = 0;

	}


	//empty cell, piece just moves to open cell.
	public static void moveToEmptyCell(ChessPiece cur, int oldRow, int oldCol, int newRow, int newCol) {
		cur.setRow(newRow);
		cur.setCol(newCol);

		//setting current piece to new cell for next update.
		System.out.println("old: " + oldRow + "   " + oldCol);
		System.out.println("new: " + newRow + "   " + newCol);
		grid[newRow][newCol].setIcon(grid[oldRow][oldCol].getIcon());

		//clear icon of old cell.
		grid[oldRow][oldCol].setIcon(null);
	}


	//used to erase a move and set the pieces back to its original position.
	public static void movePiecesBack(ChessPiece cur, ChessPiece newP, int oldCurRow, int oldCurCol, int oldNewRow, int oldNewCol) {
		//setting back the main piece.
		cur.setRow(oldCurRow);
		cur.setCol(oldCurCol);
		grid[oldCurRow][oldCurCol].setIcon(cur.getIcon());

		//setting back the taken piece.
		newP.setRow(oldNewRow);
		newP.setCol(oldNewCol);
		grid[oldNewRow][oldNewCol].setIcon(newP.getIcon());

	}


	//checking for a caslting movement, only allowed once for each side.
	public static boolean castleCheck(int curPos, int gotoPos) {
		boolean castled = false;

		//castling black side.
		if (King.castlingBlack == true) {
			System.out.println("Trying to castle black side!");
			//long castle
			if (pieces.get(gotoPos).getRow() == 0 && pieces.get(gotoPos).getCol() == 0) {
				//moves the king 2 cells to the left and moves the rook 3 to the right.
				castling(pieces.get(curPos), pieces.get(gotoPos), 0, 2, 0, 3);
				castleName = "Long";

			}
			//short castle
			else if (pieces.get(gotoPos).getRow() == 0 && pieces.get(gotoPos).getCol() == 7) {
				//moves the king 2 cells to the right and moves the rook 2 to the left.
				castling(pieces.get(curPos), pieces.get(gotoPos), 0, 6, 0, 5);
				castleName = "Short";
			}

			//setting it back to false so it never castles again.
			King.castlingBlack = false;
			//used to tell the program that we castled meaning the king is not taking the rook.
			castled = true;
		}
		//castling white side.
		else if (King.castlingWhite == true) {
			System.out.println("Trying to castle white side!");
			//long castle
			if (pieces.get(gotoPos).getRow() == 7 && pieces.get(gotoPos).getCol() == 0) {
				//moves the king 2 cells to the left and moves the rook 3 to the right.
				castling(pieces.get(curPos), pieces.get(gotoPos), 7, 2, 7, 3);
				castleName = "Long";
			}
			//short castle
			else if (pieces.get(gotoPos).getRow() == 7 && pieces.get(gotoPos).getCol() == 7) {
				//moves the king 2 cells to the right and moves the rook 2 to the left.
				castling(pieces.get(curPos), pieces.get(gotoPos), 7, 6, 7, 5);
				castleName = "Short";
			}

			//setting it back to false so it never castles again.
			King.castlingWhite = false;
			//used to tell the program that we castled meaning the king is not taking the rook.
			castled = true;
		}

		return castled;
	}


	//moving the pieces into the correct castling position.
	public static void castling(ChessPiece king, ChessPiece rook, int kingRow, int kingCol, int rookRow, int rookCol) {

		//saving kings old cell for icon update.
		int oldRow = king.getRow();
		int oldCol = king.getCol();

		//setting kings new position
		king.setRow(kingRow);
		king.setCol(kingCol);

		//setting current piece to new cell for next update.
		grid[kingRow][kingCol].setIcon(king.getIcon());
		//clear icon of old cell.
		grid[oldRow][oldCol].setIcon(null);

		//saving rooks old cell for icon update.
		oldRow = rook.getRow();
		oldCol = rook.getCol();

		//setting rooks new position
		rook.setRow(rookRow);
		rook.setCol(rookCol);

		//setting current piece to new cell for next update.
		grid[rookRow][rookCol].setIcon(rook.getIcon());
		//clear icon of old cell.
		grid[oldRow][oldCol].setIcon(null);

		//saving king and rooks location in order to unset the border highlights next turn.
		grid[jb_currentRow][jb_currentCol].setBorder(null);
		jb_currentRow = kingRow; 
		jb_currentCol = kingCol; 
		jb_gotoRow = rookRow; 
		jb_gotoCol = rookCol;

		//setting highlights for the castle.
		grid[rookRow][rookCol].setFocusPainted(false);
		grid[rookRow][rookCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
		grid[kingRow][kingCol].setFocusPainted(false);
		grid[kingRow][kingCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
	}


	//finding king based on color 
	public static ChessPiece getKing(boolean color) {
		int i;
		ChessPiece cp = null;

		//searches list for match
		for (i = 0; i < pieces.size(); i++) 
		{ 		      
			if (pieces.get(i).getColor() == color && pieces.get(i).getPiece() == ChessPiece.pType.KING) {
				cp = pieces.get(i);
				return cp;
			}
		}		
		return cp;
	}


	//function used to set each of the colors array from the main pieces array.
	private static void setPieceArray(ArrayList<ChessPiece> arr, boolean color) {
		int i = 0;

		//emptying array for new one.
		arr.clear();

		while (i < pieces.size()) {
			if (pieces.get(i).getColor() == color) {
				arr.add(pieces.get(i));
			}
			i++;
		}
	}


	//function to highlight cells with potential moves for that piece.
	private void addPotentialMoves(ChessPiece cp) {
		ChessPiece.pType pieceType = cp.getPiece();

		//getting current pieces type.
		switch (pieceType) {
		case PAWN:
			Pawn.showAvailableMoves(true, cp);
			break;
		case ROOK:
			Rook.showAvailableMoves(true, cp);
			break;
		case KNIGHT:
			Knight.showAvailableMoves(true, cp);
			break;
		case BISHOP:
			Bishop.showAvailableMoves(true, cp);
			break;
		case QUEEN:
			Queen.showAvailableMoves(true, cp);
			break;
		case KING:
			King.showAvailableMoves(true, cp);
			break;
		}
	}


	//function to delete the highlighted cells with potential moves for that piece.
	private void removePotentialMoves(ChessPiece cp) {
		ChessPiece.pType pieceType = cp.getPiece();

		//getting current pieces type.
		switch (pieceType) {
		case PAWN:
			Pawn.showAvailableMoves(false, cp);
			break;
		case ROOK:
			Rook.showAvailableMoves(false, cp);
			break;
		case KNIGHT:
			Knight.showAvailableMoves(false, cp);
			break;
		case BISHOP:
			Bishop.showAvailableMoves(false, cp);
			break;
		case QUEEN:
			Queen.showAvailableMoves(false, cp);
			break;
		case KING:
			King.showAvailableMoves(false, cp);
			break;
		}
	}


	//Checks to see if a stalemate has occured.
	public static void stalemateCheck() {
		//Stalemate (king is not in check, but has nowhere to move)
		//I have to get the available moves for each piece on the board, and if any of them can move it is not a stalemate, else it is a stalemate.

		//50-move rule (no pawns have moved and no captures have been made in the last 50 turns (aka 100 moves).
		if (moveRule50 == 100) {
			stalematePopup();
		}

		//Impossibility of checkmate.
		//only kings left on the board.
		if (whitePieces.size() == 1 && blackPieces.size() == 1) {
			stalematePopup();
		}
		//white King vs black King & (Bishop or Knight)
		else if (whitePieces.size() == 1 && blackPieces.size() == 2) {
			int i = 0;
			while (i < 2) {
				if (blackPieces.get(i).getPiece() == ChessPiece.pType.BISHOP || blackPieces.get(i).getPiece() == ChessPiece.pType.KNIGHT) {
					stalematePopup();
				}
				i++;
			}
		}
		//black King vs white King & (Bishop or Knight)
		else if (blackPieces.size() == 1 && whitePieces.size() == 2) {
			int i = 0;
			while (i < 2) {
				if (whitePieces.get(i).getPiece() == ChessPiece.pType.BISHOP || whitePieces.get(i).getPiece() == ChessPiece.pType.KNIGHT) {
					stalematePopup();
				}
				i++;
			}
		}
		//black King & Bishop vs white King & Bishop (with the bishops on the same color)
		else if (blackPieces.size() == 2 && whitePieces.size() == 2) {
			boolean b1DarkCell = false, b1LightCell = false;
			boolean b2DarkCell = false, b2LightCell = false;

			//finding white bishops cell color.
			int i = 0;
			while (i < 2) {
				ChessPiece cp = whitePieces.get(i);
				if (cp.getPiece() == ChessPiece.pType.BISHOP) {
					//black cell if the row and col are even.
					if (cp.getRow() % 2 == 0 && cp.getCol() % 2 == 0) {
						b1DarkCell = true;
					}
					//white cell if the row and col are odd.
					else {
						b1LightCell = true;
					}
				}
				i++;
			}

			//finding black bishops cell color.
			i = 0;
			while (i < 2) {
				ChessPiece cp = blackPieces.get(i);
				if (cp.getPiece() == ChessPiece.pType.BISHOP) {
					//black cell if the row and col are even.
					if (cp.getRow() % 2 == 0 && cp.getCol() % 2 == 0) {
						b2DarkCell = true;
					}
					//white cell if the row and col are odd.
					else {
						b2LightCell = true;
					}
				}
				i++;
			}

			//bishops on the same colored cell means its a stalemate.
			if (b1DarkCell == true && b2DarkCell == true)
				stalematePopup();
			else if (b1LightCell == true && b2LightCell == true)
				stalematePopup();
		}

	}


	//Function that provides the stalemate pop-up screen.
	public static void stalematePopup() {
		//stopping timers
		MainFrame.timerWhite.stop();
		MainFrame.timerBlack.stop();

		//re-scaling draw icon.
		ImageIcon ic = new ImageIcon("res\\draw.png");
		Image img = ic.getImage();
		Image newimg = img.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH ) ;  
		ic = new ImageIcon(newimg);

		//sends draw popup.
		JOptionPane.showOptionDialog(null, "Congratulations you tied! \nA stalemate occured, meaning its a draw.", "Stalemate!", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);

		//disables board because game is over.
		ChessBoard.disableBoard(MainFrame.chessboard);

		//stops the bot, if its a bot game.
		if (HomePage.botGame == true)
			HomePage.botGame = false;
	}


	//Function to help disable the board when the game is finished.
	public static void disableBoard(JPanel jp) {
		//clears/unsets board for new pov.
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				grid[i][j].setEnabled(false);  
				grid[i][j].setDisabledIcon(grid[i][j].getIcon()); 
				grid[i][j].setIcon(grid[i][j].getIcon());
			}
		}
		//also diasbles the relative buttons to the game ending.
		MainFrame.resignButton.setEnabled(false); 
		MainFrame.drawButton.setEnabled(false); 
	}


	// Functions for the bot //
	//Function to get all of the possible piece movements for the ai.
	public static void calculateAllPossibleMoves() {
		possibleAIChessPiece.clear();
		possibleAIMovementsX.clear();
		possibleAIMovementsY.clear();
		possibleMoveValue.clear();


		System.out.println("AI is processing available moves...");

		//loops through all the pieces.
		int i = 0;
		while (i < pieces.size()) {
			//only if the piece is the same color as the bot.
			if (pieces.get(i).getColor() == botColor) {
				//gets current pieces available moves.
				switch (pieces.get(i).getPiece().name()) {
				case "PAWN":
					Pawn.showAvailableMoves(true, pieces.get(i));
					break;
				case "ROOK":
					Rook.showAvailableMoves(true, pieces.get(i));
					break;
				case "KNIGHT":
					Knight.showAvailableMoves(true, pieces.get(i));
					break;
				case "BISHOP":
					Bishop.showAvailableMoves(true, pieces.get(i));
					break;
				case "QUEEN":
					Queen.showAvailableMoves(true, pieces.get(i));
					break;
				case "KING":
					King.showAvailableMoves(true, pieces.get(i));
					break;
				}
			}
			i++;
		}

		//when the king has not moved yet, allow the bot the option of castling.
		ChessPiece king = getKing(botColor);
		if (king.getMoveCount() == 0) {
			//manually adding the castle option to the bots side depending on color.
			//black
			if (botColor == true) {
				//long
				possibleAIChessPiece.add(king);
				possibleAIMovementsX.add(0);
				possibleAIMovementsY.add(0);
				//short
				possibleAIChessPiece.add(king);
				possibleAIMovementsX.add(0);
				possibleAIMovementsY.add(7);

				//move priority is similar to taking a bishop/knight.
				if (HomePage.mediumBot == true) {
					possibleMoveValue.add(3);
					possibleMoveValue.add(3);
				}
			}
			//white
			else {
				//long
				possibleAIChessPiece.add(king);
				possibleAIMovementsX.add(7);
				possibleAIMovementsY.add(0);
				//short
				possibleAIChessPiece.add(king);
				possibleAIMovementsX.add(7);
				possibleAIMovementsY.add(7);

				//move priority is similar to taking a bishop/knight.
				if (HomePage.mediumBot == true) {
					possibleMoveValue.add(3);
					possibleMoveValue.add(3);
				}
			}
		}


//		//printing potential moves.
//		i = 0;
//		while (i < possibleAIMovementsX.size()) {
//			System.out.println("row: " + possibleAIMovementsX.get(i) + "col: " + possibleAIMovementsY.get(i));
//			if (possibleMoveValue.isEmpty() == false)
//				System.out.println("Move Value: " + possibleMoveValue.get(i));
//			i++;
//		}
//		System.out.println("Piece List:");
//		i = 0;
//		while (i < possibleAIChessPiece.size()) {
//			System.out.println(possibleAIChessPiece.get(i));
//			i++;
//		}

		//getting a random value based on the size of our potential move list.
		int min = 0;
		int max = 0;
		if (HomePage.mediumBot == true) {
			//skips the castling, because we will check for it later.
			max = possibleAIMovementsX.size() - 3;
		}
		else {
			max = possibleAIMovementsX.size() - 1;
		}
		int selectedMovePos = (int)(Math.random() * (max - min + 1) + min);
		System.out.println("selected move: " + selectedMovePos);

		//when its a medium level bot, it must check the best move possible in its position.
		//checks to see if all the moves have the same point value, if so it makes a random move.
		if (HomePage.mediumBot == true) {
			int value = 0;
			int maxValue = 0;
			int maxValueLocation = 0;
			boolean pieceBlocking;
			ArrayList<Integer> possibleMovesThisTurn = new ArrayList<Integer>();

			//checks to see if theres a good move (exempting castling value).
			i = 0;
			while (i < possibleMoveValue.size() - 2) {

				//saves the highest value location
				if (possibleMoveValue.get(i) > maxValue) {
					maxValue = possibleMoveValue.get(i);
					maxValueLocation = i;
					possibleMovesThisTurn.clear();
				}
				//if its the same value, it gets put onto a list to randomly be selected later.
				else if (possibleMoveValue.get(i) == maxValue) {
					if (possibleMovesThisTurn.size() == 0) {
						possibleMovesThisTurn.add(maxValueLocation);
					}
					possibleMovesThisTurn.add(i);
				}

				//counting to see if the point value is greater than 0.
				value += possibleMoveValue.get(i);
				i++;
			}
			//it chooses the best move when possible, if all the moves are the same value it keeps the random selection.
			if (value != 0) {
				//gets a random move out of all the new potential ones.
				if (possibleMovesThisTurn.isEmpty() == false) {
					int rd = (int)(Math.random() * ((possibleMovesThisTurn.size() - 1) - 0 + 1) + 0);
					selectedMovePos = possibleMovesThisTurn.get(rd);
				}
				else {
					selectedMovePos = maxValueLocation;
				}
			}

			//checks for castling
			if (king.getMoveCount() == 0) {
				if (value < 3) {
					//checking for pieces looking at the castle path.
					int theX = possibleAIMovementsX.get(possibleAIChessPiece.size() - 1);
					int theY =  possibleAIMovementsY.get(possibleAIChessPiece.size() - 1);
					boolean pathBlocked = King.checkSpecificCastlePath(king, "Short", king.getColor());

					//making sure rook hasn't moved either.
					int fp = getChessPiece(theX, theY);
					if (fp != -1) {
						ChessPiece p = pieces.get(fp);
						int mc = p.getMoveCount();
						//checking for pieces blocking the castle.
						if (mc == 0)
							pieceBlocking = King.checkCastleCollisions(king, pieces.get(getChessPiece(theX, theY)), theX, theY, "Short", king.getColor());
						else 
							pieceBlocking = true;

						//castles short.
						if (pathBlocked == false && pieceBlocking == false) {
							selectedMovePos = possibleAIChessPiece.size() - 1;
						}
						else {
							//checking for pieces looking at the castle path.
							theX = possibleAIMovementsX.get(possibleAIChessPiece.size() - 2);
							theY =  possibleAIMovementsY.get(possibleAIChessPiece.size() - 2);
							pathBlocked = King.checkSpecificCastlePath(king, "Long", king.getColor());


							//making sure rook hasn't moved either.
							fp = getChessPiece(theX, theY);
							if (fp != -1) {
								p = pieces.get(fp);
								mc = p.getMoveCount();

								//checking for pieces blocking the castle.
								if (mc == 0)
									pieceBlocking = King.checkCastleCollisions(king, pieces.get(getChessPiece(theX, theY)), theX, theY, "Long", king.getColor());
								else 
									pieceBlocking = true;

								//castles long.
								if (pathBlocked == false && pieceBlocking == false) {
									selectedMovePos = possibleAIChessPiece.size() - 2;
								}
							}
						}
					}
				}
			}
		}

		System.out.println("best move possible loc: " + selectedMovePos);

		//king has no moves and is not in check so stalemate
		if (possibleAIChessPiece.isEmpty() == true) {
			stalematePopup();
		}
		
		//processing the random move.
		int piecePos = getChessPiece(possibleAIChessPiece.get(selectedMovePos).getRow(), possibleAIChessPiece.get(selectedMovePos).getCol());
		int movePos = getChessPiece(possibleAIMovementsX.get(selectedMovePos), possibleAIMovementsY.get(selectedMovePos));
		System.out.println("Piece Pos: " + piecePos + "and move Pos: " + movePos);

		//setting some important variables
		jb_currentRow = possibleAIChessPiece.get(selectedMovePos).getRow();
		jb_currentCol = possibleAIChessPiece.get(selectedMovePos).getCol();
		jb_gotoRow = possibleAIMovementsX.get(selectedMovePos);
		jb_gotoCol = possibleAIMovementsY.get(selectedMovePos);

		//checking if move is valid.
		checkPieceValidity(piecePos, movePos);	

		//if move was valid, setting border highlights for second clicked button and disabling icon border.
		if (valid == true) {

			//setting border highlights for cells and disabling icon border.
			if (castled == false) {
				grid[jb_currentRow][jb_currentCol].setFocusPainted(false);
				grid[jb_currentRow][jb_currentCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
				grid[jb_gotoRow][jb_gotoCol].setFocusPainted(false);
				grid[jb_gotoRow][jb_gotoCol].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); 
			}
		}
		//move was invalid, resetting clicked button.
		else {
			grid[jb_currentRow][jb_currentCol].setBorder(null);
			System.out.println("\nBAD MOVE TRY AGAIN");
			calculateAllPossibleMoves();
		}
	}


	//calculates the value of a piece.
	public static int getValue(ChessPiece cp) {
		int value = 0;

		//getting pieces point value.
		switch (cp.getPiece().name()) {
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

		return value;
	}



}
