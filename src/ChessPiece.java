/*
 * By: Jordan Carter
 * Description: Parent class used to hold all the information needed for the chess piece (ex: location, color, movecount, etc)
 * 
 */

import javax.swing.Icon;

public class ChessPiece {

	//variables
	private int row, col;
	private pType piece;
	private boolean color;	//false = white, true = black
	private Icon ic;
	private int moveCount;
	
	public static enum pType {
		PAWN,
		KNIGHT,
		BISHOP,
		ROOK,
		QUEEN,
		KING
	}
	
	public ChessPiece(int row, int col, pType piece, boolean color, Icon ic, int moveCount) {
		this.row = row;
		this.col = col;
		this.piece = piece;
		this.color = color;
		this.ic = ic;
		this.moveCount = moveCount;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	public pType getPiece() {
		return piece;
	}
	
	public void setPiece(pType piece) {
		this.piece = piece;
	}
	
	public boolean getColor() {
		return color;
	}

	public void setColor(boolean color) {
		this.color = color;
	}
	
	public Icon getIcon() {
		return ic;
	}

	public void setIcon(Icon ic) {
		this.ic = ic;
	}
	
	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}
	
	public void incrementMoveCount() {
		this.setMoveCount(this.getMoveCount() + 1);
	}
	
	public void decrementMoveCount() {
		this.setMoveCount(this.getMoveCount() - 1);
	}
	
	@Override
	public String toString() {
		return "Piece: " + piece + " (" + color + ") 	at location " + row + ", " + col + ".";
	}


	
}
