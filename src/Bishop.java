/*
 * By: Jordan Carter
 * Description: Contains all of the chess rules for the Bishop chess piece.
 * 
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Bishop extends ChessPiece {
	
	public Bishop(int row, int col, pType piece, boolean color, Icon ic, int moveCount) {
		super(row, col, piece, color, ic, moveCount);
	}

	//Checking if the Rooks current move is valid.
	public static boolean checkValidity(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol) {
		boolean validMove = false;
		boolean pieceBlocking = false;


		//all possible Bishop movements.
		//moving somewhere on the bishops diagonal line.
		if (Math.abs(cp1.getRow() - goRow) == Math.abs(cp1.getCol() - goCol)) {
			validMove = (Math.abs(goRow - cp1.getRow()) > 0 && Math.abs(goRow - cp1.getRow()) < 8) ? true : false;

			//checks for pieces blocking the path. 
			pieceBlocking = checkCollisions(cp1, cp2, goRow, goCol);

		}
		//invalid move.
		else {
			validMove = false;
			System.out.println("Invalid Move!!! Play another...");
		}

		//Returning boolean based on multiple conditions that were set.
		if (pieceBlocking == true) {
			validMove = false;
		}
		//move was valid
		else if (validMove == true) {
			cp1.incrementMoveCount();
		}

		System.out.println("Move count = " + cp1.getMoveCount());
		return validMove;
	}

	//Checking for collisions relative to the direction the rook is moving.
	private static boolean checkCollisions(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol) {
		boolean b = false;
		int foundPiece = -1;
		int i = 1;

		//checking if the piece its taking is the same color or not.
		foundPiece = ChessBoard.getChessPiece(goRow, goCol);
		if (foundPiece != -1) {
			if (cp1.getColor() == ChessBoard.pieces.get(foundPiece).getColor()) {
				System.out.println("You're trying to take your own piece.");
				return true;
			}
		}

		//Bishop moving North-East.
		if (goRow < cp1.getRow() && goCol > cp1.getCol()) {
			while (i < 8) {
				foundPiece = ChessBoard.getChessPiece(cp1.getRow() - i, cp1.getCol() + i);
				//breaks when taken piece location is found.
				if ((cp1.getRow() - i) == goRow) {
					break;
				}
				else if (foundPiece != -1) {
					//piece was found in path to location.
					System.out.println("Piece in the way");
					return true;
				}
				i++;
			}
		}
		//Bishop moving North-West.
		else if (goRow < cp1.getRow() && goCol < cp1.getCol()) {
			while (i < 8) {
				foundPiece = ChessBoard.getChessPiece(cp1.getRow() - i, cp1.getCol() - i);
				//breaks when taken piece location is found.
				if ((cp1.getRow() - i) == goRow) {
					break;
				}
				else if (foundPiece != -1) {
					//piece was found in path to location.
					System.out.println("Piece in the way");
					return true;
				}
				i++;
			}
		}
		//Bishop moving South-West.
		else if (goRow > cp1.getRow() && goCol < cp1.getCol()) {
			while (i < 8) {
				foundPiece = ChessBoard.getChessPiece(cp1.getRow() + i, cp1.getCol() - i);
				//breaks when taken piece location is found.
				if ((cp1.getRow() + i) == goRow) {
					break;
				}
				else if (foundPiece != -1) {
					//piece was found in path to location.
					System.out.println("Piece in the way");
					return true;
				}
				i++;
			}
		}
		//Bishop moving South-East.
		else if (goRow > cp1.getRow() && goCol > cp1.getCol()) {
			while (i < 8) {
				foundPiece = ChessBoard.getChessPiece(cp1.getRow() + i, cp1.getCol() + i);
				//breaks when taken piece location is found.
				if ((cp1.getRow() + i) == goRow) {
					break;
				}
				else if (foundPiece != -1) {
					//piece was found in path to location.
					System.out.println("Piece in the way");
					return true;
				}
				i++;
			}
		}

		return b;
	}


	//shows all current available moves for the piece.
	public static void showAvailableMoves(boolean show, ChessPiece cp) {
		int i = 0;
		int row = 0;
		int col = 0;
		int foundPiece = -1;
		boolean done;	

		//north-east diagonal of piece.
		i = 1;
		while (i < 8) { 
			//looking for a piece found
			row = cp.getRow() - i;
			col = cp.getCol() + i;
			//before they reach the end of board.
			if (col < 8 & row >= 0) {
				foundPiece = ChessBoard.getChessPiece(row, col);
			}
			else {
				break;
			}

			//enabling potential move highlights
			if (show == true) {
				//finding cells to set the border highlights.
				done = addBorder(cp, row, col, foundPiece);
			}
			//disabling potential move highlights
			else {
				//finding the cells to reset the border highlights.
				done = removeBorder(cp, row, col, foundPiece);
			}

			//finished setting/resetting borders for all potential moves.
			if (done == true) {
				break;
			}

			i++;
		}

		//north-west diagonal of piece.
		i = 1;
		while (i < 8) { 
			//looking for a piece found
			row = cp.getRow() - i;
			col = cp.getCol() - i;
			System.out.println(row + " and " + col);
			//before they reach the end of board.
			if (row >= 0 && col >= 0) {
				foundPiece = ChessBoard.getChessPiece(row, col);
			}
			else {
				break;
			}

			//enabling potential move highlights
			if (show == true) {
				//finding cells to set the border highlights.
				done = addBorder(cp, row, col, foundPiece);
			}
			//disabling potential move highlights
			else {
				//finding the cells to reset the border highlights.
				done = removeBorder(cp, row, col, foundPiece);
			}

			//finished setting/resetting borders for all potential moves.
			if (done == true) {
				break;
			}

			i++;
		}

		//south-west diagonal of piece.
		i = 1;
		while (i < 8) { 
			//looking for a piece found
			row = cp.getRow() + i;
			col = cp.getCol() - i;
			//before they reach the end of board.
			if (row < 8 && col >= 0) {
				foundPiece = ChessBoard.getChessPiece(row, col);
			}
			else {
				break;
			}

			//enabling potential move highlights
			if (show == true) {
				//finding cells to set the border highlights.
				done = addBorder(cp, row, col, foundPiece);
			}
			//disabling potential move highlights
			else {
				//finding the cells to reset the border highlights.
				done = removeBorder(cp, row, col, foundPiece);
			}

			//finished setting/resetting borders for all potential moves.
			if (done == true) {
				break;
			}

			i++;
		}

		//south-east diagonal of piece.
		i = 1;
		while (i < 8) { 
			//looking for a piece found
			row = cp.getRow() + i;
			col = cp.getCol() + i;
			//before they reach the end of board.
			if (row < 8 && col < 8) {
				foundPiece = ChessBoard.getChessPiece(row, col);
			}
			else {
				break;
			}

			//enabling potential move highlights
			if (show == true) {
				//finding cells to set the border highlights.
				done = addBorder(cp, row, col, foundPiece);
			}
			//disabling potential move highlights
			else {
				//finding the cells to reset the border highlights.
				done = removeBorder(cp, row, col, foundPiece);
			}

			//finished setting/resetting borders for all potential moves.
			if (done == true) {
				break;
			}

			i++;
		}
	}


	//used to help "set" the potential move border in showAvailableMoves.
	private static boolean addBorder(ChessPiece cp, int row, int col, int foundPiece) {
		boolean result = false;
		Icon icon2 = new ImageIcon("res\\circle90x90.png");

		//no piece in the way.
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
		//piece in the way.
		else {
			//apply grid highlight when its an opposite colored piece that is found.
			if (ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor()) {

				
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
					//Icon icon1 = ChessBoard.pieces.get(foundPiece).getIcon();
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
			//stop highlights after you see an enemy piece or you see your own.
			result = true;
		}	
		return result;
	}


	//used to help "reset" the potential move border in showAvailableMoves.
	private static boolean removeBorder(ChessPiece cp, int row, int col, int foundPiece) {
		boolean result = false;

		//no piece in the way.
		if (foundPiece == -1) {
			ChessBoard.grid[row][col].setIcon(null);
		}
		//piece in the way.
		else {
			//apply grid highlight.
			ChessBoard.grid[row][col].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
			//stop highlights.
			result = true;
		}

		return result;
	}


}
