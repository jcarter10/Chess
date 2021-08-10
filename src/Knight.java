/*
 * By: Jordan Carter
 * Description: Contains all of the chess rules for the Knight chess piece.
 * 
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Knight extends ChessPiece {
	
	public Knight(int row, int col, pType piece, boolean color, Icon ic, int moveCount) {
		super(row, col, piece, color, ic, moveCount);
	}

	public static boolean checkValidity(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol) {
		boolean validMove = false;
		boolean wrongPiece = false;
		

		//all possible Knight movements.
		//move two squares vertically and one square horizontally (forming the shape of an L). //col +1 or -1 and row + 2 or -2
		if (Math.abs(cp1.getRow() - goRow) == 2 && Math.abs(cp1.getCol() - goCol) == 1) {
			validMove = true;
			//checks if its taking the right piece.
			wrongPiece = checkCollisions(cp1, cp2, goRow, goCol);
			
		}
		//move two squares horizontally and one square vertically (forming the shape of an L). //+2 or -2 col, +1 or -1 row
		else if (Math.abs(cp1.getRow() - goRow) == 1 && Math.abs(cp1.getCol() - goCol) == 2) {
			validMove = true;
			//checks if its taking the right piece.
			wrongPiece = checkCollisions(cp1, cp2, goRow, goCol);
		}
		//invalid move.
		else {
			validMove = false;
			System.out.println("Invalid Move!!! Play another...");
		}
		
		//Returning boolean based on multiple conditions that were set.
		if (wrongPiece == true) {
			validMove = false;
		}
		//move was valid
		else if (validMove == true) {
			cp1.incrementMoveCount();
		}

		System.out.println("Move count = " + cp1.getMoveCount());
		return validMove;
	}
		
	
	//checking if the horse/knight is taking the correct colored piece.
	private static boolean checkCollisions(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol) {
		boolean b = false;
		int foundPiece = -1;

		//it jumps over pieces so it doesn't collide w/ anything other than the piece its taking, so checking for color of piece.
		foundPiece = ChessBoard.getChessPiece(goRow, goCol);
		if (foundPiece != -1) {
			if (cp1.getColor() == ChessBoard.pieces.get(foundPiece).getColor()) {
				System.out.println("You're trying to take your own piece.");
				return true;
			}
		}
		
		return b;
	}
	

	//shows all current available moves for the piece.
	public static void showAvailableMoves(boolean show, ChessPiece cp) {
		int foundPiece, foundPiece1, foundPiece2, foundPiece3, foundPiece4, foundPiece5, foundPiece6, foundPiece7;
		int row, row1, row2, row3, row4, row5, row6, row7;
		int col, col1, col2, col3, col4, col5, col6, col7;

		//setting all possible movements for knights.
		row = cp.getRow() - 2;
		col = cp.getCol() - 1;
		row1 = cp.getRow() - 2;
		col1 = cp.getCol() + 1;
		row2 = cp.getRow() - 1;
		col2 = cp.getCol() - 2;
		row3 = cp.getRow() - 1;
		col3 = cp.getCol() + 2;
		row4 = cp.getRow() + 2;
		col4 = cp.getCol() - 1;
		row5 = cp.getRow() + 2;
		col5 = cp.getCol() + 1;
		row6 = cp.getRow() + 1;
		col6 = cp.getCol() - 2;
		row7 = cp.getRow() + 1;
		col7 = cp.getCol() + 2;

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
	
	
	//helps the showAvailableMoves function with repetitive code.
	private static void checkForCorrectMove(ChessPiece cp, int row, int col, boolean show) {
		Icon icon2 = new ImageIcon("res\\circle90x90.png");
		
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
				//piece thats on the opposite team and is not the king.
				else if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor()) {
					Icon icon1 = ChessBoard.pieces.get(foundPiece).getIcon();

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
			int foundPiece = ChessBoard.getChessPiece(row, col);
			if (row < 8 && row >= 0 && col < 8 && col >= 0) {
				//when there is no piece in cell, just remove icon.
				if (foundPiece == -1) {
					ChessBoard.grid[row][col].setIcon(null);
				}
				//found a piece so set the cell back to the found piece.
				else {
					ChessBoard.grid[row][col].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
				}
			}
		}
	}

}
