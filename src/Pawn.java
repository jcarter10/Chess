/*
 * By: Jordan Carter
 * Description: Contains all of the chess rules for the Pawn chess piece.
 * 
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Pawn extends ChessPiece{
	
	//globals
	public static boolean enPassant = false;
	public static boolean nextMovePotentialEnpassant = false;
	public static int potentialMovesWhite = 0;
	public static int potentialMovesBlack = 0;
	
	
	public Pawn(int row, int col, pType piece, boolean color, Icon ic, int moveCount) {
		super(row, col, piece, color, ic, moveCount);
	}
	
	
	//Checking if the Pawns current move is valid.
	public static boolean checkValidity(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol) {
		boolean validMove = false;
		boolean pieceBlocking;
		int val = 1;
		int foundPiece;
		
		/*
		if (HomePage.onlineGame == true) {
			boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
			if (cur == true) {
				cp1.setColor(false);
				if (cp1 == cp2)
					cp2.setColor(false);
			}
			else { 
				cp1.setColor(true);
				if (cp1 == cp2)
					cp2.setColor(true);
			}
		}
		*/
		
		System.out.println("Potential En-Passant: " + nextMovePotentialEnpassant);		
		//checks for pieces blocking the path. 
		pieceBlocking = checkCollisions(cp1, cp2, goRow, goCol);
		
		//if its the first move for pawn, it can move once or two spaces.
		if (cp1.getMoveCount() == 0) {
			val = 2;
		}
		
		System.out.println("Sending in " + cp1.toString() + " and " + cp2.toString());
		System.out.println("goRow: " + goRow + " and goCol: " + goCol);
		
		//all possible pawn movements.
		//moving forward once or twice.
		if ((Math.abs(goRow - cp1.getRow()) == val || (Math.abs(goRow - cp1.getRow()) == val - 1)) && Math.abs(cp1.getCol() - goCol) == 0) {
			//black move
			if (cp1.getColor() == true) {
				validMove = (goRow - cp1.getRow() == val || goRow - cp1.getRow() == val - 1) ? true : false;
			}
			//white move
			else {
				validMove = (goRow - cp1.getRow() == -val || goRow - cp1.getRow() == -val + 1 ) ? true : false;
			}
			
			//checking to see if the current move sets up an en-passant for the next move (must be the pawns first move as well as a double move).
			if (validMove == true && val == 2) {
				if (cp1.getColor() == true) {
					//looking for a piece east from the newly moved pawn.
					foundPiece = ChessBoard.getChessPiece(cp1.getRow() + val, cp1.getCol() + 1);
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN) {
						nextMovePotentialEnpassant = true;
					}
					//looking for a piece west from the newly moved pawn.
					foundPiece = ChessBoard.getChessPiece(cp1.getRow() + val, cp1.getCol() - 1);
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN) {
						nextMovePotentialEnpassant = true;
					}
				}
				else if (cp1.getColor() == false ) {
					//looking for a piece east from the newly moved pawn.
					foundPiece = ChessBoard.getChessPiece(cp1.getRow() - val, cp1.getCol() + 1);
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN) {
						nextMovePotentialEnpassant = true;
					}
					//looking for a piece west from the newly moved pawn.
					foundPiece = ChessBoard.getChessPiece(cp1.getRow() - val, cp1.getCol() - 1);
					if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN) {
						nextMovePotentialEnpassant = true;
					}
				}
			}
		}
		//taking a piece diagonally (cp1 != cp2 because if there isnt a piece in new location, I set cp2 = cp1.
		else if (Math.abs(goRow - cp1.getRow()) == 1 && Math.abs(goCol - cp1.getCol()) == 1 && cp1 != cp2) {
			//black move
			if (cp1.getColor() == true) {
				validMove = (goRow - cp1.getRow() == 1 && Math.abs(goCol - cp1.getCol()) == 1) ? true : false;
			}
			//white move
			else {
				validMove = (goRow - cp1.getRow() == -1 && Math.abs(goCol - cp1.getCol()) == 1) ? true : false;
			}
		}

		//checking for a possible piece to en-passant.
		else if (cp1 == cp2 && nextMovePotentialEnpassant == true) {
			//black
			if (cp1.getColor() == true && cp1.getRow() == 4) {
				//looking for a pawn on the rightside of it.
				foundPiece = ChessBoard.getChessPiece(cp1.getRow(), cp1.getCol() + 1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 4) {
					validMove = true;
					enpassant(cp1, ChessBoard.pieces.get(foundPiece), cp1.getRow() + 1, ChessBoard.pieces.get(foundPiece).getCol(), ChessBoard.pieces.get(foundPiece).getRow(), ChessBoard.pieces.get(foundPiece).getCol());
					enPassant = true;
				}
				else {
					if (enPassant == false)
						validMove = false;
				}
				
				//looking for a pawn on the leftside of it.
				foundPiece = ChessBoard.getChessPiece(cp1.getRow(), cp1.getCol() -1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 4) {
					validMove = true;
					enpassant(cp1, ChessBoard.pieces.get(foundPiece), cp1.getRow() + 1, ChessBoard.pieces.get(foundPiece).getCol(), ChessBoard.pieces.get(foundPiece).getRow(), ChessBoard.pieces.get(foundPiece).getCol());
					enPassant = true;
				}
				else {
					if (enPassant == false)
						validMove = false;
				}
			}
			//white
			else if (cp1.getColor() == false && cp1.getRow() == 3) {
				//looking for a pawn on the rightside of it.
				foundPiece = ChessBoard.getChessPiece(cp1.getRow(), cp1.getCol() + 1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 3) {
					validMove = true;
					enpassant(cp1, ChessBoard.pieces.get(foundPiece), cp1.getRow() - 1, ChessBoard.pieces.get(foundPiece).getCol(), ChessBoard.pieces.get(foundPiece).getRow(), ChessBoard.pieces.get(foundPiece).getCol());
					enPassant = true;
				}
				else {
					if (enPassant == false)
						validMove = false;
				}
				
				//looking for a pawn on the leftside of it.
				foundPiece = ChessBoard.getChessPiece(cp1.getRow(), cp1.getCol() - 1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp1.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 3) {
					validMove = true;
					enpassant(cp1, ChessBoard.pieces.get(foundPiece), cp1.getRow() - 1, ChessBoard.pieces.get(foundPiece).getCol(), ChessBoard.pieces.get(foundPiece).getRow(), ChessBoard.pieces.get(foundPiece).getCol());
					enPassant = true;
				}
				else {
					if (enPassant == false)
						validMove = false;
				}
			}
		}
		//invalid move
		else {
			validMove = false;
			System.out.println("Invalid Move!!! Play another...");
		}

		//checking to see if pawn is at the end of the board, if so it can change into another piece.
		if ((goRow == 0 || goRow == 7) && validMove == true) {
				createPopup(cp1);
		}
		
		//Returning boolean based on multiple conditions that were set.
		if (pieceBlocking == true) {
			validMove = false;
			System.out.println("There is a piece in the way of the pawn.");
		}
		//move was valid
		else if (validMove == true) {
			cp1.incrementMoveCount();
		}

		/*//changing piece back to its rightful color if online game
		if (HomePage.onlineGame == true) {
			boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
			if (cur == true) {
				cp1.setColor(true);
				if (cp1 == cp2)
					cp2.setColor(true);
			}
			else { 
				cp1.setColor(false);
				if (cp1 == cp2)
					cp2.setColor(false);
			}
		}*/
		
		System.out.println("Move count = " + cp1.getMoveCount());
		return validMove;
	}
	
	
	
	
	
	//if the next cell contains a piece, the pawn cannot move to it, so set to true.
	private static boolean checkCollisions(ChessPiece cp1, ChessPiece cp2, int goRow, int goCol) {
		boolean b;
		int foundPiece;
	
		//checking to see if pawn is taking its own colors pawn.
		foundPiece = ChessBoard.getChessPiece(goRow, goCol);
		System.out.println("Foundpiece:" + foundPiece);
		if (foundPiece != -1) {
			if (cp1.getColor() == ChessBoard.pieces.get(foundPiece).getColor()) {
				System.out.println("You're trying to take your own piece.");
				return true;
			}
		}
		
		//no piece was found in new location.
		if (foundPiece == -1) {
			b = false;
		}
		//piece was found.
		else {
			if (goCol == cp1.getCol() + 1 || goCol == cp1.getCol() - 1) {
				b = false;
			}
			else {
				b = true;
			}
			
		}
		
		return b;
	}
	
	//pawn promotion pop-up, asks the user to change the pawn to a desired piece.
	private static void createPopup(ChessPiece cp1) {
		ImageIcon[] buttons;
		int answer = 0;
		int i = 0;
		int loc;
		ChessPiece cp;
		
		//incase it's the bots turn.
		boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
		if (ChessBoard.botColor == cur && HomePage.botGame == true) {
			cp = new Queen(cp1.getRow(), cp1.getCol(), ChessPiece.pType.QUEEN, cp1.getColor(), new ImageIcon("res\\Chess_qdt60.png"), cp1.getMoveCount());
			loc = ChessBoard.pieces.indexOf(cp1);
			ChessBoard.pieces.set(loc, cp);
			ChessBoard.grid[cp.getRow()][cp.getCol()].setIcon(cp.getIcon());

			ChessBoard.pawnPromotion = "Queen";
			System.out.println(ChessBoard.pieces.get(loc).toString());
		}
		//custom game, or the users turn.
		else {
			//getting color and icon.
			if (cp1.getColor() == true) {
				buttons = new ImageIcon[]{new ImageIcon("res\\Chess_qdt60.png"), new ImageIcon("res\\Chess_ndt60.png"), new ImageIcon("res\\Chess_bdt60.png"), new ImageIcon("res\\Chess_rdt60.png")};
			}
			else {
				buttons = new ImageIcon[]{new ImageIcon("res\\Chess_qlt60.png"), new ImageIcon("res\\Chess_nlt60.png"), new ImageIcon("res\\Chess_blt60.png"), new ImageIcon("res\\Chess_rlt60.png")};
			}

			//setting information for dialog popup.
			answer = (JOptionPane.showOptionDialog(null, "Change into one of the following pieces.", "Pawn Promotion",
					JOptionPane.ERROR_MESSAGE, 0, cp1.getIcon(), buttons, buttons[i]));


			loc = ChessBoard.pieces.indexOf(cp1);
			//depending on which piece was chosen, pawn turns into that object.
			switch (answer) {
			//Queen
			case 0:
				cp = new Queen(cp1.getRow(), cp1.getCol(), ChessPiece.pType.QUEEN, cp1.getColor(), buttons[0], cp1.getMoveCount());
				ChessBoard.pieces.set(loc, cp);
				ChessBoard.grid[cp.getRow()][cp.getCol()].setIcon(cp.getIcon());

				ChessBoard.pawnPromotion = "Queen";
				System.out.println(ChessBoard.pieces.get(loc).toString());
				break;
				//Knight
			case 1:
				cp = new Knight(cp1.getRow(), cp1.getCol(), ChessPiece.pType.KNIGHT, cp1.getColor(), buttons[1], cp1.getMoveCount());
				ChessBoard.pieces.set(loc, cp);
				ChessBoard.grid[cp.getRow()][cp.getCol()].setIcon(cp.getIcon());

				ChessBoard.pawnPromotion = "Knight";
				System.out.println(ChessBoard.pieces.get(loc).toString());
				break;
				//Bishop
			case 2:
				cp = new Bishop(cp1.getRow(), cp1.getCol(), ChessPiece.pType.BISHOP, cp1.getColor(), buttons[2], cp1.getMoveCount());
				ChessBoard.pieces.set(loc, cp);
				ChessBoard.grid[cp.getRow()][cp.getCol()].setIcon(cp.getIcon());

				ChessBoard.pawnPromotion = "Bishop";
				System.out.println(ChessBoard.pieces.get(loc).toString());
				break;
				//Rook
			case 3:
				cp = new Rook(cp1.getRow(), cp1.getCol(), ChessPiece.pType.ROOK, cp1.getColor(), buttons[3], cp1.getMoveCount());
				ChessBoard.pieces.set(loc, cp);
				ChessBoard.grid[cp.getRow()][cp.getCol()].setIcon(cp.getIcon());

				ChessBoard.pawnPromotion = "Rook";
				System.out.println(ChessBoard.pieces.get(loc).toString());
				break;
			//Setting the default to queen if option isn't chosen or pop-up is exited.
			default: 
				cp = new Queen(cp1.getRow(), cp1.getCol(), ChessPiece.pType.QUEEN, cp1.getColor(), buttons[0], cp1.getMoveCount());
				ChessBoard.pieces.set(loc, cp);
				ChessBoard.grid[cp.getRow()][cp.getCol()].setIcon(cp.getIcon());

				ChessBoard.pawnPromotion = "Queen";
				System.out.println(ChessBoard.pieces.get(loc).toString());
				break;
			}
		}
	}
	
	
	//shows all current available moves for the piece.
	public static void showAvailableMoves(boolean show, ChessPiece cp) {
		int foundPiece, foundPiece1, foundPiece2, foundPiece3;
		int row, row1, row2, row3;
		int col, col1, col2, col3;
		Icon icon2 = new ImageIcon("res\\circle90x90.png");
		
		//en-passant
		if (nextMovePotentialEnpassant == true) {
			//black
			if (cp.getColor() == true && cp.getRow() == 4) {
				//looking for a pawn on the rightside of it.
				foundPiece = ChessBoard.getChessPiece(cp.getRow(), cp.getCol() + 1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 4) {
					//enabling potential move highlights.
					if (show == true) {
						if ((cp.getCol() + 1 < 8 && cp.getCol() + 1 >= 0)) {	
							//saving possible movement to list (for the computer).
							boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
							if (ChessBoard.botColor == cur && HomePage.botGame == true) {
								ChessBoard.possibleAIChessPiece.add(cp);
								ChessBoard.possibleAIMovementsX.add(cp.getRow() + 1);
								ChessBoard.possibleAIMovementsY.add(cp.getCol() + 1);
								
								//saving each moves potential point value.
								if (HomePage.mediumBot == true) {
									//en-passant, +1 for taking a pawn.
									ChessBoard.possibleMoveValue.add(1);
								}
							}
							//sets highlight.
							else {
								ChessBoard.grid[cp.getRow() + 1][cp.getCol() + 1].setIcon(icon2);
							}
						}
					}
					//disabling potential move highlights.
					else {
						//finding the cells to reset the border highlights.
						if ((cp.getCol() + 1 < 8 && cp.getCol() + 1 >= 0)) {
							ChessBoard.grid[cp.getRow() + 1][cp.getCol() + 1].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
						}
					}
				}

				//looking for a pawn on the leftside of it.
				foundPiece = ChessBoard.getChessPiece(cp.getRow(), cp.getCol() - 1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 4) {
					//enabling potential move highlights.
					if (show == true) {
						if ((cp.getCol() - 1 < 8 && cp.getCol() - 1 >= 0)) {	
							//saving possible movement to list (for the computer).
							boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
							if (ChessBoard.botColor == cur && HomePage.botGame == true) {
								ChessBoard.possibleAIChessPiece.add(cp);
								ChessBoard.possibleAIMovementsX.add(cp.getRow() + 1);
								ChessBoard.possibleAIMovementsY.add(cp.getCol() - 1);
								
								//saving each moves potential point value.
								if (HomePage.mediumBot == true) {
									//en-passant, +1 for taking a pawn.
									ChessBoard.possibleMoveValue.add(1);
								}
							}
							//sets highlight.
							else {
								ChessBoard.grid[cp.getRow() + 1][cp.getCol() - 1].setIcon(icon2);
							}
						}
					}
					//disabling potential move highlights.
					else {
						//finding the cells to reset the border highlights.
						if ((cp.getCol() - 1 < 8 && cp.getCol() - 1 >= 0)) {
							ChessBoard.grid[cp.getRow() + 1][cp.getCol() - 1].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
						}
					}
				}
			}
			//white
			else if (cp.getColor() == false && cp.getRow() == 3) {
				//looking for a pawn on the rightside of it.
				foundPiece = ChessBoard.getChessPiece(cp.getRow(), cp.getCol() + 1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 3) {
					//enabling potential move highlights.
					if (show == true) {
						if ((cp.getCol() + 1 < 8 && cp.getCol() + 1 >= 0)) {		
							//saving possible movement to list (for the computer).
							boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
							if (ChessBoard.botColor == cur && HomePage.botGame == true) {
								ChessBoard.possibleAIChessPiece.add(cp);
								ChessBoard.possibleAIMovementsX.add(cp.getRow() - 1);
								ChessBoard.possibleAIMovementsY.add(cp.getCol() + 1);
								
								//saving each moves potential point value.
								if (HomePage.mediumBot == true) {
									//en-passant, +1 for taking a pawn.
									ChessBoard.possibleMoveValue.add(1);
								}
							}
							//sets highlight.
							else {
								ChessBoard.grid[cp.getRow() - 1][cp.getCol() + 1].setIcon(icon2);
							}
						}
					}
					//disabling potential move highlights.
					else {
						//finding the cells to reset the border highlights.
						if ((cp.getCol() + 1 < 8 && cp.getCol() + 1 >= 0)) {
							ChessBoard.grid[cp.getRow() - 1][cp.getCol() + 1].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
						}
					}
				}

				//looking for a pawn on the leftside of it.
				foundPiece = ChessBoard.getChessPiece(cp.getRow(), cp.getCol() - 1);
				if (foundPiece != -1 && ChessBoard.pieces.get(foundPiece).getColor() != cp.getColor() && ChessBoard.pieces.get(foundPiece).getPiece() == pType.PAWN && 
						ChessBoard.pieces.get(foundPiece).getMoveCount() == 1 && ChessBoard.pieces.get(foundPiece).getRow() == 3) {
					//enabling potential move highlights.
					if (show == true) {
						if ((cp.getCol() - 1 < 8 && cp.getCol() - 1 >= 0)) {	
							//saving possible movement to list (for the computer).
							boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
							if (ChessBoard.botColor == cur && HomePage.botGame == true) {
								ChessBoard.possibleAIChessPiece.add(cp);
								ChessBoard.possibleAIMovementsX.add(cp.getRow() - 1);
								ChessBoard.possibleAIMovementsY.add(cp.getCol() - 1);
								
								//saving each moves potential point value.
								if (HomePage.mediumBot == true) {
									//en-passant, +1 for taking a pawn.
									ChessBoard.possibleMoveValue.add(1);
								}
							}
							//sets highlight.
							else {
								ChessBoard.grid[cp.getRow() - 1][cp.getCol() - 1].setIcon(icon2);
							}
						}
					}
					//disabling potential move highlights.
					else {
						//finding the cells to reset the border highlights.
						if ((cp.getCol() - 1 < 8 && cp.getCol() - 1 >= 0)) {
							ChessBoard.grid[cp.getRow() - 1][cp.getCol() - 1].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
						}
					}
				}
			}
		}
		
		
		//regular pawn movements
		//setting data for black pawns.
		if (cp.getColor() == true) {
			//pawn moving forward.
			row = cp.getRow() + 1;
			col = cp.getCol();
			foundPiece = ChessBoard.getChessPiece(row, col);

			//pieces diagonal of pawn.
			row1 = cp.getRow() + 1;
			col1 = cp.getCol() + 1;
			foundPiece1 = ChessBoard.getChessPiece(row1, col1);

			row2 = cp.getRow() + 1;
			col2 = cp.getCol() - 1;
			foundPiece2 = ChessBoard.getChessPiece(row2, col2);
		}
		//data for white pawns.
		else {
			//pawn moving forward.
			row = cp.getRow() - 1;
			col = cp.getCol();
			foundPiece = ChessBoard.getChessPiece(row, col);

			//pieces diagonal of pawn.
			row1 = cp.getRow() - 1;
			col1 = cp.getCol() + 1;
			foundPiece1 = ChessBoard.getChessPiece(row1, col1);

			row2 = cp.getRow() - 1;
			col2 = cp.getCol() - 1;
			foundPiece2 = ChessBoard.getChessPiece(row2, col2);
		}
		
		//pawn after 1st move.
		if (cp.getMoveCount() != 0) {
			//enabling potential move highlights.
			if (show == true) {
				
				//when no piece is blocking pawn from moving forward.
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
				//pawn is able to take a piece diagonally.
				if (foundPiece1 != -1 && ChessBoard.pieces.get(foundPiece1).getColor() != cp.getColor()) {
					if ((col2 < 8 && col2 >= 0)) {
						//saving possible movement to list (for the computer).
						boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
						if (ChessBoard.botColor == cur && HomePage.botGame == true) {
							ChessBoard.possibleAIChessPiece.add(cp);
							ChessBoard.possibleAIMovementsX.add(row1);
							ChessBoard.possibleAIMovementsY.add(col1);
							
							//saving each moves potential point value.
							if (HomePage.mediumBot == true) {
								//gets the value of the taken piece.
								int val = ChessBoard.getValue(ChessBoard.pieces.get(foundPiece1));
								ChessBoard.possibleMoveValue.add(val);
							}
						}
						//sets highlight.
						else {
							ChessBoard.grid[row1][col1].setIcon(combineImages(foundPiece1, row1, col1));
						}
					}
				}
				//pawn is able to take a piece diagonally.
				if (foundPiece2 != -1 && ChessBoard.pieces.get(foundPiece2).getColor() != cp.getColor()) {
					if ((col2 < 8 && col2 >= 0)) {	
						//saving possible movement to list (for the computer).
						boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
						if (ChessBoard.botColor == cur && HomePage.botGame == true) {
							ChessBoard.possibleAIChessPiece.add(cp);
							ChessBoard.possibleAIMovementsX.add(row2);
							ChessBoard.possibleAIMovementsY.add(col2);
							
							//saving each moves potential point value.
							if (HomePage.mediumBot == true) {
								//gets the value of the taken piece.
								int val = ChessBoard.getValue(ChessBoard.pieces.get(foundPiece2));
								ChessBoard.possibleMoveValue.add(val);
							}
						}
						//sets highlight.
						else {
							ChessBoard.grid[row2][col2].setIcon(combineImages(foundPiece2, row2, col2));
						}
					}
				}
			}
			//disabling potential move highlights.
			else {
				//finding the cells to reset the border highlights.
				if (foundPiece == -1) {
					ChessBoard.grid[row][col].setIcon(null);
				}
				else {
					ChessBoard.grid[row][col].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
				}
				
				if (foundPiece1 != -1) {
					if ((col1 < 8 && col1 >= 0)) {
						ChessBoard.grid[row1][col1].setIcon(ChessBoard.pieces.get(foundPiece1).getIcon());
					}
				}

				
				if (foundPiece2 != -1) {
					if ((col2 < 8 && col2 >= 0)) {
						ChessBoard.grid[row2][col2].setIcon(ChessBoard.pieces.get(foundPiece2).getIcon());
					}
				}
			}
		}
		//pawn on 1st move.
		else {
			//setting data for black pawns.
			if (cp.getColor() == true) {
				//setting extra data because pawn can move forward 2 cells.
				row3 = cp.getRow() + 2;
				col3 = cp.getCol();
				foundPiece3 = ChessBoard.getChessPiece(row3, col3);
			}
			//data for white pawns.
			else {
				//setting extra data because pawn can move forward 2 cells.
				row3 = cp.getRow() - 2;
				col3 = cp.getCol();
				foundPiece3 = ChessBoard.getChessPiece(row3, col3);
			}

			//enabling potential move highlights.
			if (show == true) {
				//when no piece is blocking pawn from moving forward.
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
				//when no piece is blocking pawn from moving forward two cells.
				if (foundPiece3 == -1 && foundPiece == -1) {
					//saving possible movement to list (for the computer).
					boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
					if (ChessBoard.botColor == cur && HomePage.botGame == true) {
						ChessBoard.possibleAIChessPiece.add(cp);
						ChessBoard.possibleAIMovementsX.add(row3);
						ChessBoard.possibleAIMovementsY.add(col3);
						
						//saving each moves potential point value.
						if (HomePage.mediumBot == true) {
							//regular move so no point value.
							ChessBoard.possibleMoveValue.add(0);
						}
					}
					//sets highlight.
					else {
						ChessBoard.grid[row3][col3].setIcon(icon2);
					}
				}
				//pawn is able to take a piece diagonally.
				if (foundPiece1 != -1 && ChessBoard.pieces.get(foundPiece1).getColor() != cp.getColor()) {
					if ((col2 < 8 && col2 >= 0)) {
						//saving possible movement to list (for the computer).
						boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
						if (ChessBoard.botColor == cur && HomePage.botGame == true) {
							ChessBoard.possibleAIChessPiece.add(cp);
							ChessBoard.possibleAIMovementsX.add(row1);
							ChessBoard.possibleAIMovementsY.add(col1);
							
							//saving each moves potential point value.
							if (HomePage.mediumBot == true) {
								//gets the value of the taken piece.
								int val = ChessBoard.getValue(ChessBoard.pieces.get(foundPiece1));
								ChessBoard.possibleMoveValue.add(val);
							}
						}
						//sets highlight.
						else {
							ChessBoard.grid[row1][col1].setIcon(combineImages(foundPiece1, row1, col1));
						}
					}
				}
				//pawn is able to take a piece diagonally.
				if (foundPiece2 != -1 && ChessBoard.pieces.get(foundPiece2).getColor() != cp.getColor()) {
					if ((col2 < 8 && col2 >= 0)) {	
						//saving possible movement to list (for the computer).
						boolean cur = (ChessBoard.currentTurn % 2 == 0) ? true : false;
						if (ChessBoard.botColor == cur && HomePage.botGame == true) {
							ChessBoard.possibleAIChessPiece.add(cp);
							ChessBoard.possibleAIMovementsX.add(row2);
							ChessBoard.possibleAIMovementsY.add(col2);
							
							//saving each moves potential point value.
							if (HomePage.mediumBot == true) {
								//gets the value of the taken piece.
								int val = ChessBoard.getValue(ChessBoard.pieces.get(foundPiece2));
								ChessBoard.possibleMoveValue.add(val);
							}
						}
						//sets highlight.
						else {
							ChessBoard.grid[row2][col2].setIcon(combineImages(foundPiece2, row2, col2));
						}
					}
				}
			}
			//disabling potential move highlights.
			else {
				//finding the cells to reset the border highlights.
				if (foundPiece == -1) {
					ChessBoard.grid[row][col].setIcon(null);
				}
				else {
					ChessBoard.grid[row][col].setIcon(ChessBoard.pieces.get(foundPiece).getIcon());
				}
				
				if (foundPiece3 == -1) {
					ChessBoard.grid[row3][col3].setIcon(null);
				}
				else {
					System.out.println(ChessBoard.pieces.get(foundPiece3).toString());
					ChessBoard.grid[row3][col3].setIcon(ChessBoard.pieces.get(foundPiece3).getIcon());
				}
				
				if (foundPiece1 != -1) {
					if ((col1 < 8 && col1 >= 0)) {
						ChessBoard.grid[row1][col1].setIcon(ChessBoard.pieces.get(foundPiece1).getIcon());
					}
				}
				
				if (foundPiece2 != -1) {
					if ((col2 < 8 && col2 >= 0)) {
						ChessBoard.grid[row2][col2].setIcon(ChessBoard.pieces.get(foundPiece2).getIcon());
					}
				}
			}
		}
	}
	
	
	//function to help with the occurance of an en-passant.
	public static void enpassant(ChessPiece p1, ChessPiece p2, int p1Row, int p1Col, int p2Row, int p2Col) {
		//adding taken piece to respective list.
		//black piece
		if (p2.getColor() == true) {
			ChessBoard.blackTaken.add(p2);
			//blackPiecesTaken++;
		}
		//white pieces
		else {
			ChessBoard.whiteTaken.add(p2);
			//whitePiecesTaken++;
		}
		
		//saving pawns old cell for icon update.
		int oldRow = p1.getRow();
		int oldCol = p1.getCol();

		//setting pawns new position
		p1.setRow(p1Row);
		p1.setCol(p1Col);

		//setting current piece to new cell for next update.
		ChessBoard.grid[p1Row][p1Col].setIcon(p1.getIcon());
		//clear icon of old cell.
		ChessBoard.grid[oldRow][oldCol].setIcon(null);

		//saving rooks old cell for icon update.
		//oldRow = p2.getRow();
		//oldCol = p2.getCol();

		//setting rooks new position
		//p2.setRow(p2Row);
		//p2.setCol(p2Col);

		//clear icon of taken pawn.
		ChessBoard.grid[p2Row][p2Col].setIcon(null);

		//removing taken piece from list.
		ChessBoard.pieces.remove(ChessBoard.getChessPiece(p2Row, p2Col));
		
		//resetting the 50 move counter because a piece was taken.
		ChessBoard.moveRule50 = 0;
	}
	
	
	
	private static ImageIcon combineImages(int foundPiece, int row, int col) {
		Icon icon1 = ChessBoard.pieces.get(foundPiece).getIcon();
		Icon icon2 = new ImageIcon("res\\circle90x90.png");

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

		return new ImageIcon(combinedImage);
	}
}

