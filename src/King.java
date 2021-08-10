/*
 * By: Jordan Carter
 * Description: Contains all of the chess rules for the King chess piece.
 * 
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class King extends ChessPiece {
	
	//globals
	public static int allChecks = 0;
	public static int potentialMovesWhite = 0;
	public static int potentialMovesBlack = 0;
	public static ArrayList<ChessPiece> piecesCheckingKing = new ArrayList<>();
	public static boolean castlingWhite = false;
	public static boolean castlingBlack = false;
	public static boolean shortCastle = false;
	public static boolean longCastle = false;
	private static String checkingDirection = "";
	private static ArrayList<Integer> lineRow = new ArrayList<>();
	private static ArrayList<Integer> lineCol = new ArrayList<>();
	public static boolean itsCheckmate = false;
	
	public King(int row, int col, pType piece, boolean color, Icon ic, int moveCount) {
		super(row, col, piece, color, ic, moveCount);
	}
	
	//checks if the move is valid for the king.
	public static boolean checkValidity(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol) {
		boolean validMove = false;
		boolean pieceBlocking = false;
		boolean pathBlocked = false;
		String direction = "";
		
		//checks that the king is not moving next to the other king.
		ChessPiece tempKing = new King(goRow, goCol, ChessPiece.pType.KING, cp1.getColor(), null, 0);
		boolean nextToOtherKing = checkForOtherKing(tempKing);
		if (nextToOtherKing == true) {
			System.out.println("You can't put your king next to the other king.");
			return false;
		}
		
		//all possible King movements.
		//King moves vertically.
		if (Math.abs(cp1.getRow() - goRow) == 1 && Math.abs(cp1.getCol() - goCol) == 0) {
			direction = "vertical";
			validMove = true;

			//checks for pieces blocking the path. 
			pieceBlocking = checkCollisions(cp1, cp2, goRow, goCol, direction);

		}
		//King moves horizontally.
		else if (Math.abs(cp1.getRow() - goRow) == 0 && Math.abs(cp1.getCol() - goCol) == 1) {
			direction = "horizontal";
			validMove = true;

			//checks for pieces blocking the path. 
			pieceBlocking = checkCollisions(cp1, cp2, goRow, goCol, direction);
		}
		//King moves diagonally.
		else if (Math.abs(cp1.getRow() - goRow) == 1 && Math.abs(cp1.getCol() - goCol) == 1) {
			direction = "diagonal";
			validMove = true;
			
			//checks for pieces blocking the path. 
			pieceBlocking = checkCollisions(cp1, cp2, goRow, goCol, direction);
			
		}
		//castling (short or long) is allowed for the king and rook if they both haven't moved yet.
		else if (cp1.getPiece() == ChessPiece.pType.KING && cp2.getPiece() == ChessPiece.pType.ROOK && validMove != true) {
			
			if ((cp1.getMoveCount() == 0 && cp2.getMoveCount() == 0) && cp1.getColor() == cp2.getColor()) {
				//black
				if (cp1.getColor() == true) {
					castlingBlack = true;
				}
				else {
					castlingWhite = true;
				}	
				validMove = true;
			}
			else {
				validMove = false;
			}
			
			//checks for pieces blocking the path. 
			pieceBlocking = checkCollisions(cp1, cp2, goRow, goCol, direction);
			
			//checks the cells that the king is crossing incase a piece has it in its line.
			pathBlocked = checkCastlePath(cp1);
			System.out.println("PATH BLOCKED for castle: " + pathBlocked);
			
			//castle is valid so rook count goes up.
			if (pathBlocked == false && pieceBlocking == false) {
				//moves rooks move count up 1.
				cp2.incrementMoveCount();
			}
			
			
		}
		//invalid move.
		else {
			validMove = false;
			System.out.println("Invalid Move!!! Play another...");
		}
		
		//Returning a boolean based on multiple conditions that were set.
		if (pieceBlocking == true || pathBlocked == true) {
			validMove = false;
			System.out.println("You can't castle because a piece is blocking your path, or the king is passing through an attacked cell.");
		}
		//move was valid
		else if (validMove == true) {
			cp1.incrementMoveCount();
			System.out.println("The move was valid.");
		}

		return validMove;
	}
	
	
	//Checking the kings collision course.
	private static boolean checkCollisions(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol, String direction) {
		boolean b = false;
		int foundPiece = -1;
		int col = 0;

		//checking if the piece its taking is the same color or not.
		foundPiece = ChessBoard.getChessPiece(goRow, goCol);
		if (foundPiece != -1) {
			//if trying to take same color piece and its not a rook (because selecting a rook could be the cause of a castle)
			if (ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.ROOK && cp1.getColor() == ChessBoard.pieces.get(foundPiece).getColor()) {
				System.out.println("You're trying to take your own piece.");
				return true;
			}
		}	
		
		
		if (foundPiece != -1) {
			if (cp1.getColor() == ChessBoard.pieces.get(foundPiece).getColor()) {
				b = true;
				//black side castle.
				if (cp1.getColor() == true && castlingBlack== true) {
					//checking for long.
					if (cp2.getCol() == 0) {
						for (col = cp1.getCol() - 1; col > 0; col--) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(0, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						longCastle = true;
						b = false;
					}
					//checking for short
					else {
						//columns right of king.
						for (col = cp1.getCol() + 1; col < 7; col++) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(0, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						shortCastle = true;
						b = false;
					}
				}
				//white side castle.
				else if (cp1.getColor() == false && castlingWhite== true) {
					//checking for long.
					if (cp2.getCol() == 0) {
						for (col = cp1.getCol() - 1; col > 0; col--) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(7, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						longCastle = true;
						b = false;
					}
					//checking for short
					else {
						//columns right of king.
						for (col = cp1.getCol() + 1; col < 7; col++) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(7, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						shortCastle = true;
						b = false;
					}
				}
			}
		}

		return b;
	}
	
	
	//Checks to see if there are any checks on the king or a checkmate.
	public static boolean checksOnKing(ChessPiece king) {
		boolean checkFound = false;
		int foundPiece = -1;
		int row;
		int col;
		int i;
		int check = 0;
		boolean result;
		allChecks = 0;
		
		//only running the checkmate function later on if its the real king, so we must get it incase its one of the temporary ones from the potential move function.
		ChessPiece realKing = ChessBoard.getKing(king.getColor());
		
		//clearing for next iteration.
		if (realKing == king) {
			piecesCheckingKing.clear();
		}
		
		if (king == null)
			return checkFound;
		
		//vertical check, for queen or rooks.
		//rows above king.
		for (row = king.getRow() - 1; row >= 0; row--) {
			//getting the piece the vertical line ran into.
			foundPiece = ChessBoard.getChessPiece(row, king.getCol());
			
			//if the king is a temporary king looking for checks in its new position and it runs into the real king there is no check.
			//if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) != realKing) {
			//	break;
			//}
			//the vertical line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or rook from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.ROOK && ChessBoard.pieces.get(foundPiece) != realKing) { //i think its cause its checking the temp king when the real king is on the board so we must check for that.
				break;
			}
			
			//sending info to function to check for the check.
			result = qrCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
		} 
		//rows under king.
		for (row = king.getRow() + 1; row <= 8; row++) {
			
			//getting the piece the vertical line ran into.
			foundPiece = ChessBoard.getChessPiece(row, king.getCol());

			//the vertical line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or rook from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.ROOK && ChessBoard.pieces.get(foundPiece) != realKing) {
				break;
			}
			
			//sending info to function to check for the check.
			result = qrCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
		}

		//horizontal check, for queen or rooks.
		//columns left of king.
		for (col = king.getCol() - 1; col >= 0; col--) {
			//getting the piece that the horizontal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow(), col);
			//the horizontal line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or rook from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.ROOK && ChessBoard.pieces.get(foundPiece) != realKing) {
				break;
			}
			
			//sending info to function to check for the check.
			result = qrCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
		}

		//columns right of king.
		for (col = king.getCol() + 1; col <= 8; col++) {
			//getting the piece that the horizontal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow(), col);
			//the horizontal line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or rook from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.ROOK && ChessBoard.pieces.get(foundPiece) != realKing) {
				break;
			}
			
			//sending info to function to check for the check.
			result = qrCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
		}

		//diagonal check.
		//north-east of King.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() - i, king.getCol() + i);
			
			//the diagonal line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or bishop from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.BISHOP && ChessBoard.pieces.get(foundPiece) != realKing) {
				break;
			}
			
			//sending info to function to check for a check.
			result = qbCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
			i++;
		}
		
		//north-west of King.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() - i, king.getCol() - i);
			//the diagonal line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or bishop from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.BISHOP && ChessBoard.pieces.get(foundPiece) != realKing) {
				break;
			}
			
			//sending info to function to check for a check.
			result = qbCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
			i++;

		}

		//south-west of King.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() + i, king.getCol() - i);
			//the diagonal line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or bishop from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.BISHOP && ChessBoard.pieces.get(foundPiece) != realKing) {
				break;
			}
			
			//sending info to function to check for a check.
			result = qbCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
			i++;
		}

		//south-east of King.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() + i, king.getCol() + i);
			//the diagonal line ran into a same color piece, so theres no check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() != pType.KING) {
				break;
			}
			//vertical line ran into a piece blocking the queen or bishop from making a check, so there is no check.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.QUEEN && 
					ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.BISHOP && ChessBoard.pieces.get(foundPiece) != realKing) {
				break;
			}
			//else if (foundPiece != -1 && )
			
			//sending info to function to check for a check.
			result = qbCheck(king, foundPiece, check);
			//piece found that could put king into check in that line.
			if (result == true)
				break;
			i++;
		}
		
		//checks for knights/horses.
		//checking for knights in all possible L directions (alot of repetitive code).
		hCheck(king, ChessBoard.getChessPiece(king.getRow() - 2, king.getCol() - 1), check);
		hCheck(king, ChessBoard.getChessPiece(king.getRow() - 2, king.getCol() + 1), check);
		hCheck(king, ChessBoard.getChessPiece(king.getRow() - 1, king.getCol() - 2), check);
		hCheck(king, ChessBoard.getChessPiece(king.getRow() - 1, king.getCol() + 2), check);
		hCheck(king, ChessBoard.getChessPiece(king.getRow() + 2, king.getCol() - 1), check);
		hCheck(king, ChessBoard.getChessPiece(king.getRow() + 2, king.getCol() + 1), check);
		hCheck(king, ChessBoard.getChessPiece(king.getRow() + 1, king.getCol() - 2), check);
		hCheck(king, ChessBoard.getChessPiece(king.getRow() + 1, king.getCol() + 2), check);
		
		//checks for pawns.
		//black.
		if (king.getColor() == true) {
			pCheck(king, ChessBoard.getChessPiece(king.getRow() + 1, king.getCol() - 1), check);
			pCheck(king, ChessBoard.getChessPiece(king.getRow() + 1, king.getCol() + 1), check);
		}
		//white.
		else {
			pCheck(king, ChessBoard.getChessPiece(king.getRow() - 1, king.getCol() - 1), check);
			pCheck(king, ChessBoard.getChessPiece(king.getRow() - 1, king.getCol() + 1), check);
		}
		
		//sending info.
		System.out.print("# of checks for (" + king.getColor() + "): " + allChecks + "\n");
		checkFound = (allChecks != 0) ? true : false;
		
		//whenever there is a check on the king, program checks for a checkmate.
		if (checkFound == true && realKing == king) {
			
			//running the checkmate function when the real king is in check. 
			boolean checkmate = checkMate(realKing);
			
			//when checkmate occurs, send a popup and end the game..
			if (checkmate == true) {
				System.out.println("\n\n\n CHECKMATE!!!\n\n\n");
				itsCheckmate = true;
				checkmatePopup(piecesCheckingKing.get(0).getColor());
			}
		}
		
		System.out.println("\nPieces checking king:");
		int k = 0;
		while (k < piecesCheckingKing.size()) {
			System.out.println(piecesCheckingKing.get(k).toString());
			k++;
		}
		System.out.println();
		
		return checkFound;
	}
	
	
	//Function to help with repetitive code for finding checks for the Queen or Rook.
	private static boolean qrCheck(ChessPiece king, int foundPiece, int check) {
		//when a queen or rook is found w/ nothing in the way, king is in check (as long as its a different color piece).
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
			//queen or rook found.
			check = (ChessBoard.pieces.get(foundPiece).getPiece() == ChessPiece.pType.QUEEN || 
					ChessBoard.pieces.get(foundPiece).getPiece() == ChessPiece.pType.ROOK) ? +1 : +0;
			
			allChecks += check;
			if (check != 0) {
				System.out.println("Queen or Rook has you in check!");
				//only adding the piece if its the real king and not a temp king.
				ChessPiece realKing = ChessBoard.getKing(king.getColor());
				if (realKing == king) {
					piecesCheckingKing.add(ChessBoard.pieces.get(foundPiece));
				}
				return true;
			}
			check = 0;
		}
		return false;
	}
	
	//Function to help with repetitive code for finding checks for the Queen or Bishop.
	private static boolean qbCheck(ChessPiece king, int foundPiece, int check) {
		//when a queen or bishop is found w/ nothing in the way, king is in check (as long as its a different color piece).
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
			//queen or bishop found.
			check = (ChessBoard.pieces.get(foundPiece).getPiece() == ChessPiece.pType.QUEEN || 
					ChessBoard.pieces.get(foundPiece).getPiece() == ChessPiece.pType.BISHOP) ? +1 : +0;
			
			allChecks += check;
			if (check != 0) {
				System.out.println("Queen or Bishop has you in check!");
				//only adding the piece if its the real king and not a temp king.
				ChessPiece realKing = ChessBoard.getKing(king.getColor());
				if (realKing == king) {
					piecesCheckingKing.add(ChessBoard.pieces.get(foundPiece));
				}
				return true;
			}
				check = 0;
		}
		return false;
	}
	
	//Function to help with repetitive code for finding checks for the knight/horse.
	private static void hCheck(ChessPiece king, int foundPiece, int check) {
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
			//knight found.
			check = (ChessBoard.pieces.get(foundPiece).getPiece() == ChessPiece.pType.KNIGHT) ? +1 : +0;

			allChecks += check;
			if (check != 0) {
				System.out.println("Horse has you in check!");
				//only adding the piece if its the real king and not a temp king.
				ChessPiece realKing = ChessBoard.getKing(king.getColor());
				if (realKing == king) {
					piecesCheckingKing.add(ChessBoard.pieces.get(foundPiece));
				}
			}
			check = 0;
		}
	}
	
	//Function to help with repetitive code for finding checks for the pawn.
	private static void pCheck(ChessPiece king, int foundPiece, int check) {
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
			//pawn found.
			check = (ChessBoard.pieces.get(foundPiece).getPiece() == ChessPiece.pType.PAWN) ? +1 : +0;
			
			allChecks += check;
			if (check != 0) {
				System.out.println("Pawn has you in check!");
				//only adding the piece if its the real king and not a temp king.
				ChessPiece realKing = ChessBoard.getKing(king.getColor());
				if (realKing == king) {
					piecesCheckingKing.add(ChessBoard.pieces.get(foundPiece));
				}
			}
			check = 0;
		}
	}
	
	
	//Shows all current available moves for the piece.
	public static void showAvailableMoves(boolean show, ChessPiece cp) {
		int row, row1, row2, row3, row4, row5, row6, row7;
		int col, col1, col2, col3, col4, col5, col6, col7;
		
		//setting all possible movements for king.
		row = cp.getRow() - 1;
		col = cp.getCol();
		row1 = cp.getRow() - 1;
		col1 = cp.getCol() + 1;
		row2 = cp.getRow() - 1;
		col2 = cp.getCol() - 1;
		row3 = cp.getRow();
		col3 = cp.getCol() - 1;
		row4 = cp.getRow();
		col4 = cp.getCol() + 1;
		row5 = cp.getRow() + 1;
		col5 = cp.getCol();
		row6 = cp.getRow() + 1;
		col6 = cp.getCol() - 1;
		row7 = cp.getRow() + 1;
		col7 = cp.getCol() + 1;
		
		//enabling/disabling highlights for potential moves.
		checkForCorrectMove(cp, row, col, show);
		checkForCorrectMove(cp, row1, col1, show);
		checkForCorrectMove(cp, row2, col2, show);
		checkForCorrectMove(cp, row3, col3, show);
		checkForCorrectMove(cp, row4, col4, show);
		checkForCorrectMove(cp, row5, col5, show);
		checkForCorrectMove(cp, row6, col6, show);
		checkForCorrectMove(cp, row7, col7, show);

	}
	
	
	//Helps the showAvailableMoves function with repetitive code.
	private static void checkForCorrectMove(ChessPiece cp, int row, int col, boolean show) {
		Icon icon2 = new ImageIcon("res\\circle90x90.png");
		
		//saving the actual # of checks before running the temporary kings checks.
		int backupChecks = allChecks;
		
		//making a second copy of the king to test for a check in its potential new position.
		ChessPiece tempKing = new King(row, col, ChessPiece.pType.KING, cp.getColor(), null, 0);
		boolean tempInCheck = checksOnKing(tempKing);
		//System.out.println("\nCheck: " + tempInCheck + "\nTemp King: " + tempKing.toString());
		
		//if the move puts the king in check, no highlight is shown.
		if (tempInCheck == false) {
			
			//makes sure that the king is not moving next to the other king.
			boolean nextToOtherKing = checkForOtherKing(tempKing);
			if (nextToOtherKing == false) {

				//enabling highlights.
				if (show == true) {

					//making sure its inside grid.
					if (row < 8 && row >= 0 && col < 8 && col >= 0) {
						int foundPiece = ChessBoard.getChessPiece(row, col);

						//empty space.
						if (foundPiece == -1) {
							//saving possible movement to list (for the computer).
							boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
							if (ChessBoard.botColor == cur && HomePage.botGame == true) {
								ChessBoard.possibleAIChessPiece.add(cp);
								ChessBoard.possibleAIMovementsX.add(row);
								ChessBoard.possibleAIMovementsY.add(col);
								
								//saving each moves potential point value.
								if (HomePage.mediumBot == true) {
									//regular move so no point value.
									ChessBoard.possibleMoveValue.add(0);
								}
							}
							//sets highlight.
							else {
								ChessBoard.grid[row][col].setIcon(icon2); 
							}
						}
						//piece thats on the opposite team.
						else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor()) {
							//saving possible movement to list (for the computer).
							boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
							if (ChessBoard.botColor == cur && HomePage.botGame == true) {
								ChessBoard.possibleAIChessPiece.add(cp);
								ChessBoard.possibleAIMovementsX.add(row);
								ChessBoard.possibleAIMovementsY.add(col);
								
								//saving each moves potential point value.
								if (HomePage.mediumBot == true) {
									//gets the value of the taken piece.
									int val = ChessBoard.getValue(ChessBoard.pieces.get(foundPiece));
									ChessBoard.possibleMoveValue.add(val);
								}
							}
							//sets highlight.
							else {
								Icon icon1 = ChessBoard.pieces.get(foundPiece).getIcon();

								//getting first image.
								BufferedImage bf1 = new BufferedImage(
										icon1.getIconWidth(),
										icon1.getIconHeight(),
										BufferedImage.TYPE_INT_ARGB);
								Graphics g1 = bf1.createGraphics();
								//paints the Icon to the BufferedImage.
								icon1.paintIcon(null, g1, 0, 0);
								g1.dispose();

								//setting the icon as buffered image to be used throughout the class.
								BufferedImage bf2 = new BufferedImage(
										icon2.getIconWidth(),
										icon2.getIconHeight(),
										BufferedImage.TYPE_INT_ARGB);
								Graphics g2 = bf2.createGraphics();
								//paints the Icon to the BufferedImage.
								icon2.paintIcon(null, g2, 0, 0);
								g2.dispose();

								//merging images.
								final BufferedImage combinedImage = new BufferedImage(
										bf2.getWidth(), 
										bf2.getHeight(), 
										BufferedImage.TYPE_INT_ARGB );
								Graphics2D g = combinedImage.createGraphics();

								//in order for the piece to be centered inside of the circle "highlight" we must set its new width and height.
								int newW = (bf2.getWidth() - bf1.getWidth()) / 2;
								int newH = (bf2.getHeight() - bf1.getHeight()) / 2;
								//drawing the 2 images to the BufferedImage w/ specific placement.
								g.drawImage(bf1, newW, newH, null);
								g.drawImage(bf2, 1, 1, null);
								g.dispose();

								ChessBoard.grid[row][col].setIcon(new ImageIcon(combinedImage)); 
							}
						}
					}
				}
				//disabling highlights
				else {
					if (row < 8 && row >= 0 && col < 8 && col >= 0) {
						int foundPiece = ChessBoard.getChessPiece(row, col);

						if (foundPiece == -1) {
							ChessBoard.grid[row][col].setIcon(null);
						}
						else {
							ChessBoard.grid[row][col].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
						}
					}
				}
			}
		}
		//resetting the temporary checks.
		allChecks = backupChecks;
	}
	
	
	
	
	//Everytime a check on the king is found, it checks for a checkmate.
	private static boolean checkMate(ChessPiece king) {
		//1) Is the kings cell in check? if so continue. (done, cause of our check function)
		//2) Check the kings potential moves to see if it can move out of check. (done)
		//3) For all the pieces that have the king in check, can another piece take that piece to stop the check? (done) (make an array that for each piece that has it in check.) 
		//4) For all the pieces that have the king in check (except knights), can we move a piece in front of the checking piece to block its path and prevent check? aka can the check be blocked? (done)
		//5) if its a double check, the only response is the king moving or taking a piece. (done).
		int row, col, i, foundPiece;
		int row1, row2, row3, row4, row5, row6, row7;
		int col1, col2, col3, col4, col5, col6, col7;
		ChessPiece checkingPiece = null;
		lineRow.clear();
		lineCol.clear();
		potentialMovesBlack = 0;
		potentialMovesWhite = 0;

		
		//setting all possible cell movements for king.
		row = king.getRow() - 1;
		col = king.getCol();
		row1 = king.getRow() - 1;
		col1 = king.getCol() + 1;
		row2 = king.getRow() - 1;
		col2 = king.getCol() - 1;
		row3 = king.getRow();
		col3 = king.getCol() - 1;
		row4 = king.getRow();
		col4 = king.getCol() + 1;
		row5 = king.getRow() + 1;
		col5 = king.getCol();
		row6 = king.getRow() + 1;
		col6 = king.getCol() - 1;
		row7 = king.getRow() + 1;
		col7 = king.getCol() + 1;

		//calculating all of the kings potential moves to get out of check.
		calculatingAvailableKingMoves(king, row, col);
		calculatingAvailableKingMoves(king, row1, col1);
		calculatingAvailableKingMoves(king, row2, col2);
		calculatingAvailableKingMoves(king, row3, col3);
		calculatingAvailableKingMoves(king, row4, col4);
		calculatingAvailableKingMoves(king, row5, col5);
		calculatingAvailableKingMoves(king, row6, col6);
		calculatingAvailableKingMoves(king, row7, col7);
		
		
		System.out.println("# of pieces checking the king: " + piecesCheckingKing.size());
		System.out.println("Potential Moves Black: " + potentialMovesBlack);
		System.out.println("Potential Moves White: " + potentialMovesWhite);
		
		
		//incase there is no piece putting the king in check.
		if (piecesCheckingKing.size() != 0) 
			checkingPiece = piecesCheckingKing.get(0);
		//no need to check for checkmate because there is no piece checking the king.
		else {
			return false;
		}
			
		//before anything else, if there is a double check, the only possible move is for the king to move or take a piece.
		//if thats not possible for the king to do, then it's a smothered mate.
		if (piecesCheckingKing.size() > 1) {
			//black
			if (king.getColor() == true) {
				if (potentialMovesBlack == 0) {
					System.out.println("\nDouble check!\n");
					return true;
				}
			}
			//white
			else {
				if (potentialMovesWhite == 0) {
					System.out.println("\nDouble check!\n");
					return true;
				}
			}
		}
		
		
		//checking to see if the king can move off of it's cell thats in check.
		//black
		if (king.getColor() == true) {
			if (potentialMovesBlack != 0) {
				return false;
			}
		}
		//white
		else {
			if (potentialMovesWhite != 0) {
				return false;
			}
		}
		
		
		//can another piece take the piece checking the king. 
		//checks the pieces lines and if we find a piece that can potenitally take that piece, then its not checkmate. 
		boolean move = checkPiecesLines(checkingPiece);
		
		//there is a move found on a line, so no checkmate.
		if (move == true) {
			return false;
		}
		
		
		//can we move a piece in front of the piece that is checking the king in order to block it (as long as the piece checking is not a knight or pawn (cause there is no room in between the pawn and king to block)).
		//piece is not a knight
		if (checkingPiece.getPiece() != pType.KNIGHT) {
			//maybe run a loop of pieces and if one of the pieces can go in front of the other pieces line on the king then its not check.
			//getting the direction so that we can see if any piece can block that line. (so if it was above at 3,3) then we would check 2,3 and 1,3.

			//getting direction/line of the check.
			checkingDirection = findCheckDirection(king, checkingPiece);
			
			//getting the values of the line/direction the king is in check by.
			switch (checkingDirection) {
			case "Above":
				row = king.getRow() - 1;
				col = king.getCol();
				while (row >= 0) {
					foundPiece = ChessBoard.getChessPiece(row, col);
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}
					
					row--;
				}
				break;
			case "Below":
				row = king.getRow() + 1;
				col = king.getCol();
				while (row < 8) {
					foundPiece = ChessBoard.getChessPiece(row, col);
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}
					
					row++;
				}
				break;
			case "Left":
				row = king.getRow();
				col = king.getCol() - 1;
				while (col >= 0) {
					foundPiece = ChessBoard.getChessPiece(row, col);
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}
					
					col--;
				}
				break;
			case "Right":
				row = king.getRow();
				col = king.getCol() + 1;
				while (col < 8) {
					foundPiece = ChessBoard.getChessPiece(row, col);
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}
					
					col++;
				}
				break;
			case "North-East":
				i = 1;
				while (i < 8) {
					row = king.getRow() - i;
					col = king.getCol() + i;
					foundPiece = ChessBoard.getChessPiece(row, col);
					
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}
					
					i++;
				}
				break;
			case "North-West":
				i = 1;
				while (i < 8) {
					row = king.getRow() - i;
					col = king.getCol() - i;
					foundPiece = ChessBoard.getChessPiece(row, col);
					
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}

					i++;
				}
				break;
			case "South-West":
				i = 1;
				while (i < 8) {
					row = king.getRow() + i;
					col = king.getCol() - i;
					foundPiece = ChessBoard.getChessPiece(row, col);
					
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}
					
					i++;
				}
				break;
			case "South-East":
				i = 1;
				while (i < 8) {
					row = king.getRow() + i;
					col = king.getCol() + i;
					foundPiece = ChessBoard.getChessPiece(row, col);
					
					//stops when it reaches the checking piece.
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == checkingPiece) {
						break;
					}
					//adds the line values to specific arraylists.
					else {
						lineRow.add(row);
						lineCol.add(col);
					}
					
					i++;
				}
				break;
				
			}
			
			
			System.out.println(checkingPiece.toString());
			System.out.println("\n\n\n" + checkingDirection);
			i = 0;
			while (i < lineRow.size()) {
				System.out.println(lineRow.get(i) + " and " + lineCol.get(i));
				i++;
			}
			
		
			//seeing if any piece is able to block the checking path/line.
			i = 0;
			while (i < lineRow.size()) {
				boolean b = checkForPieceToCell(king, lineRow.get(i), lineCol.get(i));
				//if a piece is able to block the check, then there is no checkmate.
				if (b == true) {
					return false;
				}
				i++;
			}
		
		}
		
		//there are no moves possible to stop the king from being in check, therefore it is checkmate and the game is over.
		return true;
	}
	
	
	//Helps the checkmate function, checks all of the pieces lines to see if another can take it.
	private static boolean checkPiecesLines(ChessPiece cp) {
		int row;
		int col;
		int foundPiece;
		int i;
		
		
		//vertical check, for queen or rooks.
		//rows above piece.
		for (row = cp.getRow() - 1; row >= 0; row--) {
			foundPiece = ChessBoard.getChessPiece(row, cp.getCol());
			//the vertical line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			
		} 
		//rows under piece.
		for (row = cp.getRow() + 1; row < 8; row++) {
			//getting the piece the vertical line ran into.
			foundPiece = ChessBoard.getChessPiece(row, cp.getCol());
			
			//the vertical line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			
		}

		//horizontal check, for queen or rooks.
		//columns left of piece.
		for (col = cp.getCol() - 1; col >= 0; col--) {
			//getting the piece that the horizontal line ran into.
			foundPiece = ChessBoard.getChessPiece(cp.getRow(), col);
			
			//the horizontal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

		}

		//columns right of piece.
		for (col = cp.getCol() + 1; col < 8; col++) {
			//getting the piece that the horizontal line ran into.
			foundPiece = ChessBoard.getChessPiece(cp.getRow(), col);
			
			//the horizontal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

		}

		//diagonal check.
		//north-east of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(cp.getRow() - i, cp.getCol() + i);

			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			i++;
		}

		//north-west of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(cp.getRow() - i, cp.getCol() - i);
			
			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			i++;

		}

		//south-west of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(cp.getRow() + i, cp.getCol() - i);
			
			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			i++;
		}

		//south-east of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(cp.getRow() + i, cp.getCol() + i);
			
			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			i++;
		}
		
		
		//knights.
		boolean b = knightCheck(ChessBoard.getChessPiece(cp.getRow() - 2, cp.getCol() - 1), cp);
		if (b == true)
			return true;
		b = knightCheck(ChessBoard.getChessPiece(cp.getRow() - 2, cp.getCol() + 1), cp);
		if (b == true)
			return true;
		b = knightCheck(ChessBoard.getChessPiece(cp.getRow() - 1, cp.getCol() - 2), cp);
		if (b == true)
			return true;
		b = knightCheck(ChessBoard.getChessPiece(cp.getRow() - 1, cp.getCol() + 2), cp);
		if (b == true)
			return true;
		b = knightCheck(ChessBoard.getChessPiece(cp.getRow() + 2, cp.getCol() - 1), cp);
		if (b == true)
			return true;
		b = knightCheck(ChessBoard.getChessPiece(cp.getRow() + 2, cp.getCol() + 1), cp);
		if (b == true)
			return true;
		b = knightCheck(ChessBoard.getChessPiece(cp.getRow() + 1, cp.getCol() - 2), cp);
		if (b == true)
			return true;
		b = knightCheck(ChessBoard.getChessPiece(cp.getRow() + 1, cp.getCol() + 2), cp);
		if (b == true)
			return true;
		
		
		//pawns.
		//checks for pawns.
		//black.
		if (cp.getColor() == true) {
			b = pawnCheck(ChessBoard.getChessPiece(cp.getRow() + 1, cp.getCol() - 1), cp);
			if (b == true)
				return true;
			b = pawnCheck(ChessBoard.getChessPiece(cp.getRow() + 1, cp.getCol() + 1), cp);
			if (b == true)
				return true;
		}
		//white.
		else {
			b = pawnCheck(ChessBoard.getChessPiece(cp.getRow() - 1, cp.getCol() - 1), cp);
			if (b == true)
				return true;
			b = pawnCheck(ChessBoard.getChessPiece(cp.getRow() - 1, cp.getCol() + 1), cp);
			if (b == true)
				return true;
		}
		

		//if there are no pieces able to take the checking piece.
		return false;
		
	}
	
	
	//Function to help the checkmate function find any knights threatening to take the checking piece.
	private static boolean knightCheck(int foundPiece, ChessPiece cp) {
		//there is a knight that could potentially take the checking piece.
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
				ChessBoard.pieces.get(foundPiece).getPiece() == pType.KNIGHT) {
			return true;
		}
		return false;
	}
	
	//Function to help the checkmate function find any pawns threatening to take the checking piece.
	private static boolean pawnCheck(int foundPiece, ChessPiece cp) {
		//there is a pawn that could potentially take the checking piece.
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
				ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN) {
			return true;
		}
		return false;
	}
	
	
	//only purpose is to find which direction/line the king is in check by.
	private static String findCheckDirection(ChessPiece king, ChessPiece checkingPiece) {
		String result = "";
		int row;
		int col;
		int foundPiece;
		int i;
		
		//vertical check, for queen or rooks.
		//rows above piece.
		for (row = king.getRow() - 1; row >= 0; row--) {
			foundPiece = ChessBoard.getChessPiece(row, king.getCol());
			
			//the vertical line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "Above";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			
		} 
		//rows under piece.
		for (row = king.getRow() + 1; row < 8; row++) {
			//getting the piece the vertical line ran into.
			foundPiece = ChessBoard.getChessPiece(row, king.getCol());
			
			//the vertical line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "Below";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			
		}

		//horizontal check, for queen or rooks.
		//columns left of piece.
		for (col = king.getCol() - 1; col >= 0; col--) {
			//getting the piece that the horizontal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow(), col);
			
			//the horizontal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "Left";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

		}

		//columns right of piece.
		for (col = king.getCol() + 1; col < 8; col++) {
			//getting the piece that the horizontal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow(), col);
			
			//the horizontal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "Right";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

		}

		//diagonal check.
		//north-east of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() - i, king.getCol() + i);

			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "North-East";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			i++;
		}

		//north-west of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() - i, king.getCol() - i);
			
			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "North-West";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			i++;

		}

		//south-west of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() + i, king.getCol() - i);
			
			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "South-West";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}
			i++;
		}

		//south-east of piece.
		i = 1;
		while (i < 8) {
			//getting the piece that the diagonal line ran into.
			foundPiece = ChessBoard.getChessPiece(king.getRow() + i, king.getCol() + i);
		
			//the diagonal line ran into a piece that could potentially take the checking piece.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					ChessBoard.pieces.get(foundPiece) == checkingPiece) {
				return "South-East";
			}
			//if it ran into another piece that can't take it 
			else if (foundPiece != -1) {
				break;
			}

			i++;
		}
		
		return result;
	}
	
	
	//Function to check if any piece can move to a specific cell. 
	private static boolean checkForPieceToCell(ChessPiece king, int curRow, int curCol) {
		int foundPiece, row, col, i;
		
		//checking each direction from the cell to see if a piece can potentially go to it.
		//Above.
		row = curRow - 1;
		col = curCol;
		while (row >= 0) {
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				System.out.println("above" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.ROOK)) {
				break;
			}
			row--;
		}
				
		//Below.
		row = curRow + 1;
		col = curCol;
		while (row < 8) {
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				System.out.println("below" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.ROOK)) {
				break;
			}
			
			row++;
		}

		//Left.
		row = curRow;
		col = curCol - 1;
		while (col >= 0) {
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				System.out.println("left" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.ROOK)) {
				break;
			}
			
			col--;
		}

		//Right.
		row = curRow;
		col = curCol + 1;
		while (col < 8) {
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				System.out.println("right" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.ROOK)) {
				break;
			}
			
			col++;
		}
		
		//North-East.
		i = 1;
		while (i < 8) {
			row = curRow - i;
			col = curCol + i;
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				System.out.println("NE" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.BISHOP)) {
				break;
			}
			
			i++;
		}

		//North-West.
		i = 1;
		while (i < 8) {
			row = curRow - i;
			col = curCol - i;
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				System.out.println("NW" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.BISHOP)) {
				break;
			}
			

			i++;
		}

		//South-West.
		i = 1;
		while (i < 8) {
			row = curRow + i;
			col = curCol - i;
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				System.out.println("SW" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.BISHOP)) {
				break;
			}
			
			
			i++;
		}

		//South-East.
		i = 1;
		while (i < 8) {
			row = curRow + i;
			col = curCol + i;
			foundPiece = ChessBoard.getChessPiece(row, col);
			
			//stops when it reaches a potential piece that could block the check.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				System.out.println("SE" + ChessBoard.pieces.get(foundPiece).toString());
				return true;
			}
			//piece found on opposite team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor()) {
				break;
			}
			//piece found thats not a queen or rook on same side.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() != pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() != pType.BISHOP)) {
				break;
			}
			
			
			i++;
		}

		
		//knights.
		boolean b = knightCheckMate(ChessBoard.getChessPiece(curRow - 2, curCol - 1), king);
		if (b == true)
			return true;
		b = knightCheckMate(ChessBoard.getChessPiece(curRow - 2, curCol + 1), king);
		if (b == true)
			return true;
		b = knightCheckMate(ChessBoard.getChessPiece(curRow - 1, curCol - 2), king);
		if (b == true)
			return true;
		b = knightCheckMate(ChessBoard.getChessPiece(curRow - 1, curCol + 2), king);
		if (b == true)
			return true;
		b = knightCheckMate(ChessBoard.getChessPiece(curRow + 2, curCol - 1), king);
		if (b == true)
			return true;
		b = knightCheckMate(ChessBoard.getChessPiece(curRow + 2, curCol + 1), king);
		if (b == true)
			return true;
		b = knightCheckMate(ChessBoard.getChessPiece(curRow + 1, curCol - 2), king);
		if (b == true)
			return true;
		b = knightCheckMate(ChessBoard.getChessPiece(curRow + 1, curCol + 2), king);
		if (b == true)
			return true;
		
		if (king.getColor() == true) {
			b = pawnCheckMate(ChessBoard.getChessPiece(curRow + 1, curCol), king, curRow);
			if (b == true)
				return true;
			b = pawnCheckMate(ChessBoard.getChessPiece(curRow + 2, curCol), king, curRow);
			if (b == true)
				return true;
		}
		//white.
		else {
			b = pawnCheckMate(ChessBoard.getChessPiece(curRow - 1, curCol), king, curRow);
			if (b == true)
				return true;
			b = pawnCheckMate(ChessBoard.getChessPiece(curRow - 2, curCol), king, curRow);
			if (b == true)
				return true;
		}
		
		return false;
	}
	
	
	//Function to help the checkmate function find any knights that can block the checking piece.
	private static boolean knightCheckMate(int foundPiece, ChessPiece cp) {
		//there is a knight that could potentially block the checking piece.
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp.getColor() && 
				ChessBoard.pieces.get(foundPiece).getPiece() == pType.KNIGHT) {
			System.out.println("knight" + ChessBoard.pieces.get(foundPiece).toString());
			return true;
		}
		return false;
	}

	//Function to help the checkmate function find any pawns that can block the checking piece.
	private static boolean pawnCheckMate(int foundPiece, ChessPiece cp, int curRow) {
		//the white pawn is out of position to block the check.
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getRow() < curRow && cp.getColor() == false) {
			return false;
		}
		//the black pawn is out of position to block the check.
		else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getRow() > curRow && cp.getColor() == true) {
			return false;
		}
		//there is a pawn that could potentially block the checking piece.
		else if (foundPiece != -1 && (Math.abs(ChessBoard.pieces.get(foundPiece).getRow() - curRow) == 2) && 
				ChessBoard.pieces.get(foundPiece).getMoveCount() == 0 && ChessBoard.pieces.get(foundPiece).getColor() == cp.getColor()) {
			return true;
		}
		else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp.getColor() && 
				ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN) {
			System.out.println("pawn" + ChessBoard.pieces.get(foundPiece).toString());
			return true;
		}
		return false;
	}

	
	//Helps the showAvailableMoves function with repetitive code.
	private static void calculatingAvailableKingMoves(ChessPiece cp, int row, int col) {
		//saving the actual # of checks before running the temporary kings checks.
		int backupChecks = allChecks;

		//making a second copy of the king to test for a check in its potential new position.
		ChessPiece tempKing = new King(row, col, ChessPiece.pType.KING, cp.getColor(), null, 0);
		boolean tempInCheck = checksOnKing(tempKing);
		System.out.println("\nCheck: " + tempInCheck + "\nTemp King: " + tempKing.toString());
		
		//if the move puts the king in check, no highlight is shown.
		if (tempInCheck == false) {

			//making sure its inside grid.
			if (row < 8 && row >= 0 && col < 8 && col >= 0) {
				int foundPiece = ChessBoard.getChessPiece(row, col);

				//empty space.
				if (foundPiece == -1) {
					//potential move is correct
					if (cp.getColor() == true)
						potentialMovesBlack++;
					else 
						potentialMovesWhite++;
				}
				//piece thats on the opposite team.
				else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor()) {
					//potential move is correct
					if (cp.getColor() == true)
						potentialMovesBlack++;
					else 
						potentialMovesWhite++;
				}
			}
		}
		//resetting the temporary checks.
		allChecks = backupChecks;
	}
	
	
	
	//Function to check if the castling path is safe for the king.
	private static boolean checkCastlePath(ChessPiece cp1) {
		boolean pathIsNotBlocked1, pathIsNotBlocked2;
		
		if (longCastle == true) {
			if (castlingBlack == true) {
				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 0, 2);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 0, 3);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}
			else if (castlingWhite == true) {
				
				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 7, 2);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 7, 3);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}

		}
		else if (shortCastle == true) {
			if (castlingBlack == true) {
				
				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 0, 5);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 0, 6);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}
			else if (castlingWhite == true) {
				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 7, 5);
				System.out.println(pathIsNotBlocked1);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 7, 6);
				System.out.println(pathIsNotBlocked2);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}
		}
		return false;
	}
	

	//Function to make sure the king can castle, checks the directions 
	private static boolean checkLinesToCastlingCells(ChessPiece king, int curRow, int curCol) {
		int foundPiece, row, col, i;

		//checking each direction from the cell to see if a piece can potentially go to it.
		//Above.
		row = curRow - 1;
		col = curCol;
		while (row >= 0) {
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}

			row--;
		}

		//Below.
		row = curRow + 1;
		col = curCol;
		while (row < 8) {
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}


			row++;
		}

		//Left.
		row = curRow;
		col = curCol - 1;
		while (col >= 0) {
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}


			col--;
		}

		//Right.
		row = curRow;
		col = curCol + 1;
		while (col < 8) {
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.ROOK)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}


			col++;
		}

		//North-East.
		i = 1;
		while (i < 8) {
			row = curRow - i;
			col = curCol + i;
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}


			i++;
		}

		//North-West.
		i = 1;
		while (i < 8) {
			row = curRow - i;
			col = curCol - i;
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}


			i++;
		}

		//South-West.
		i = 1;
		while (i < 8) {
			row = curRow + i;
			col = curCol - i;
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}


			i++;
		}

		//South-East.
		i = 1;
		while (i < 8) {
			row = curRow + i;
			col = curCol + i;
			foundPiece = ChessBoard.getChessPiece(row, col);

			//stops when it reaches a piece blocking the castle.
			if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != king.getColor() && 
					(ChessBoard.pieces.get(foundPiece).getPiece() == pType.QUEEN || ChessBoard.pieces.get(foundPiece).getPiece() == pType.BISHOP)) {
				return true;
			}
			//piece found on same team.
			else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == king.getColor()) {
				break;
			}

			i++;
		}


		//knights.
		boolean b = knightCastleCheck(ChessBoard.getChessPiece(curRow - 2, curCol - 1), king);
		if (b == true)
			return true;
		b = knightCastleCheck(ChessBoard.getChessPiece(curRow - 2, curCol + 1), king);
		if (b == true)
			return true;
		b = knightCastleCheck(ChessBoard.getChessPiece(curRow - 1, curCol - 2), king);
		if (b == true)
			return true;
		b = knightCastleCheck(ChessBoard.getChessPiece(curRow - 1, curCol + 2), king);
		if (b == true)
			return true;
		b = knightCastleCheck(ChessBoard.getChessPiece(curRow + 2, curCol - 1), king);
		if (b == true)
			return true;
		b = knightCastleCheck(ChessBoard.getChessPiece(curRow + 2, curCol + 1), king);
		if (b == true)
			return true;
		b = knightCastleCheck(ChessBoard.getChessPiece(curRow + 1, curCol - 2), king);
		if (b == true)
			return true;
		b = knightCastleCheck(ChessBoard.getChessPiece(curRow + 1, curCol + 2), king);
		if (b == true)
			return true;


		//checks for pawns.
		//black.
		if (king.getColor() == true) {
			b = pawnCastleCheck(ChessBoard.getChessPiece(curRow + 1, curCol - 1), king);
			if (b == true)
				return true;
			b = pawnCastleCheck(ChessBoard.getChessPiece(curRow + 1, curCol + 1), king);
			if (b == true)
				return true;
		}
		//white.
		else {
			b = pawnCastleCheck(ChessBoard.getChessPiece(curRow - 1, curCol - 1), king);
			if (b == true)
				return true;
			b = pawnCastleCheck(ChessBoard.getChessPiece(curRow - 1, curCol + 1), king);
			if (b == true)
				return true;
		}


		return false;
	}
	
	
	//Function to help the castling function find any pawns that can block the castle.
	private static boolean knightCastleCheck(int foundPiece, ChessPiece cp) {
		//there is a knight that could potentially block the castle.
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
				ChessBoard.pieces.get(foundPiece).getPiece() == pType.KNIGHT) {
			return true;
		}
		return false;
	}

	//Function to help the castling function find any pawns that can block the castle.
	private static boolean pawnCastleCheck(int foundPiece, ChessPiece cp) {
		//there is a pawn that could potentially block the castle.
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && 
				ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN) {
			return true;
		}

		return false;
	}

	
	//Function to end the game and send a popup for a checkmate.
	private static void checkmatePopup(boolean c) {
		String color = "";
		
		//stopping timers
		MainFrame.timerWhite.stop();
		MainFrame.timerBlack.stop();
		
		//re-scaling draw icon.
		ImageIcon ic = new ImageIcon("res\\checkmate.png");
		Image img = ic.getImage();
		Image newimg = img.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH ) ;  
		ic = new ImageIcon(newimg);

		//sends winner popup.
		color = (c== true) ? "Black" : "White";
		//JOptionPane.showOptionDialog(null, "Congratulations " + color + "!\nYou won the game by checkmate.", "VICTORY!", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);
		JOptionPane.showOptionDialog(null, "" + color + "-side has won the game by checkmate.", "Winner By Checkmate", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);
		
		//disables board because game is over.
		ChessBoard.disableBoard(MainFrame.chessboard);
		
		//stops the bot, if its a bot game.
		if (HomePage.botGame == true)
			HomePage.botGame = false;
	}
	
	
	//Function that make sure the king isn't moving next to the other king.
	private static boolean checkForOtherKing(ChessPiece king) {
		ChessPiece otherKing;
		int foundPiece;
		
		if (king.getColor() == true)
			otherKing = ChessBoard.getKing(false);
		else 
			otherKing = ChessBoard.getKing(true);
		
		//checks for other king.
		foundPiece = ChessBoard.getChessPiece(king.getRow() - 1, king.getCol());
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		foundPiece = ChessBoard.getChessPiece(king.getRow() - 1, king.getCol() + 1);
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		foundPiece = ChessBoard.getChessPiece(king.getRow() - 1, king.getCol() - 1);
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		foundPiece = ChessBoard.getChessPiece(king.getRow(), king.getCol() + 1);
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		foundPiece = ChessBoard.getChessPiece(king.getRow(), king.getCol() - 1);
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		foundPiece = ChessBoard.getChessPiece(king.getRow() + 1, king.getCol());
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		foundPiece = ChessBoard.getChessPiece(king.getRow() + 1, king.getCol() + 1);
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		foundPiece = ChessBoard.getChessPiece(king.getRow() + 1, king.getCol() - 1);
		if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece) == otherKing) 
			return true;
		
		
		return false;
	}
	
	
	//Function to check if the castling path is safe for the king.
	public static boolean checkSpecificCastlePath(ChessPiece cp1, String direction, boolean color) {
		boolean pathIsNotBlocked1, pathIsNotBlocked2;

		if (direction == "Long") {
			if (color == true) {
				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 0, 2);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 0, 3);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}
			else if (color == false) {

				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 7, 2);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 7, 3);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}

		}
		else if (direction == "Short") {
			if (color == true) {

				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 0, 5);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 0, 6);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}
			else if (color == false) {
				pathIsNotBlocked1 = checkLinesToCastlingCells(cp1, 7, 5);
				System.out.println(pathIsNotBlocked1);
				if (pathIsNotBlocked1 == true) {
					return true;
				}
				pathIsNotBlocked2 = checkLinesToCastlingCells(cp1, 7, 6);
				System.out.println(pathIsNotBlocked2);
				if (pathIsNotBlocked2 == true) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	public static boolean checkCastleCollisions(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol, String direction, boolean color) {
		boolean b = false;
		int foundPiece = -1;
		int col = 0;

		//checking if the piece its taking is the same color or not.
		foundPiece = ChessBoard.getChessPiece(goRow, goCol);
		if (foundPiece != -1) {
			//if trying to take same color piece and its not a rook (because selecting a rook could be the cause of a castle)
			if (ChessBoard.pieces.get(foundPiece).getPiece() != ChessPiece.pType.ROOK && cp1.getColor() == ChessBoard.pieces.get(foundPiece).getColor()) {
				System.out.println("You're trying to take your own piece.");
				return true;
			}
		}	
		
		
		if (foundPiece != -1) {
			if (cp1.getColor() == ChessBoard.pieces.get(foundPiece).getColor()) {
				b = true;
				//black side castle.
				if (cp1.getColor() == true && color == true) {
					//checking for long.
					if (cp2.getCol() == 0) {
						for (col = cp1.getCol() - 1; col > 0; col--) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(0, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						direction = "Long";
						b = false;
					}
					//checking for short
					else {
						//columns right of king.
						for (col = cp1.getCol() + 1; col < 7; col++) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(0, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						direction = "Short";
						b = false;
					}
				}
				//white side castle.
				else if (cp1.getColor() == false && color == false) {
					//checking for long.
					if (cp2.getCol() == 0) {
						for (col = cp1.getCol() - 1; col > 0; col--) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(7, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						direction = "Long";
						b = false;
					}
					//checking for short
					else {
						//columns right of king.
						for (col = cp1.getCol() + 1; col < 7; col++) {
							//getting the piece that the horizontal line ran into.
							foundPiece = ChessBoard.getChessPiece(7, col);
							//the horizontal line ran into a same color piece, so we cant castle.
							if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() == cp1.getColor()) {
								System.out.println("You can't castle there are pieces in the way.");
								return true;
							}
						}
						direction = "Short";
						b = false;
					}
				}
			}
		}

		return b;
	}

}
