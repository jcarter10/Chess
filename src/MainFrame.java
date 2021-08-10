/*
 * By: Jordan Carter
 * Description: The JFrame that is used as the GUI for the entire game length. The chessboard will sit inside of this frame as well, and all the information around that chessboard 
 * 				is contained in here as well.
 * 
 */

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.Timer;
import java.awt.Font;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.border.BevelBorder;

public class MainFrame extends JFrame {
	//Serial 
	private static final long serialVersionUID = 1L;

	//Globals
	public static int HEIGHT = 855;
	public static int WIDTH = 950;
	public static JPanel contentPane;
	public static JPanel chessboard;							//JPanel that holds the gridlayout representing the chessboard. 
	public static JTextPane currentPieceText = new JTextPane(); //Have to initilize textpane here or else NULLPOINTEREXCEPTION when trying to use.
	public static JTextPane currentTurnText = new JTextPane();
	public static JPanel whitePieces = new JPanel();
	public static JPanel blackPieces = new JPanel();
	public static JTextPane turnText = new JTextPane();
	public static JPanel eastPanel = new JPanel();
	public static JTextPane movesListText = new JTextPane();
	public static JTextPane blackPointsText = new JTextPane();
	public static JTextPane whitePointsText = new JTextPane();
	public static Timer timerBlack;
	public static Timer timerWhite;
	public static JButton drawButton;
	public static JButton resignButton;
	public static JPanel numValues;
	public static JPanel alphValues;
	
	//Variables
	//600000 = 10 minutes in milliseconds.
	int countW = 600000;
	int countB = 600000;
	int minutes;
	int seconds;
	String str = "";
	DecimalFormat df = new DecimalFormat("00");


	//Constructor
	public MainFrame() {
		//variables
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);

		//main frame properties
		setSize(WIDTH, HEIGHT);
		setBounds(100, 100, WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setTitle("Chess");
		getContentPane().setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		//chessboard properties.
		chessboard = new JPanel();
		chessboard.setLayout(new GridLayout(ChessBoard.N, ChessBoard.N));
		contentPane.add(chessboard);

		//properties for the header panel which will contain all the information about the current game.
		JPanel headerPanel = new JPanel();
		headerPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		headerPanel.setPreferredSize(new Dimension(50, 50));
		contentPane.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(null);

		//current piece text properties.
		currentPieceText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		currentPieceText.setEditable(false);
		currentPieceText.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		currentPieceText.setText("Nothing Selected...");
		currentPieceText.setBounds(505, 27, 170, 20);
		currentPieceText.setBackground(new Color(238, 238, 238));
		headerPanel.add(currentPieceText);

		//current turn text properties.
		currentTurnText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		currentTurnText.setEditable(false);
		currentTurnText.setText("(White to move).");
		currentTurnText.setBackground(new Color(238, 238, 238));
		currentTurnText.setBounds(560, 3, 119, 20);
		headerPanel.add(currentTurnText);

		//black text pane properties.
		JTextPane blackText = new JTextPane();
		blackText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		blackText.setEditable(false);
		blackText.setText("Black:");
		blackText.setBackground(new Color(238, 238, 238));
		blackText.setBounds(5, 27, 54, 20);
		headerPanel.add(blackText);

		//white text pane properties.
		JTextPane whiteText = new JTextPane();
		whiteText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		whiteText.setEditable(false);
		whiteText.setText("White:");
		whiteText.setBackground(new Color(238, 238, 238));
		whiteText.setBounds(5, 3, 54, 20);
		headerPanel.add(whiteText);

		//black text pane for turn timer.
		JTextPane blackTimerText = new JTextPane();
		blackTimerText.setText("10:00");
		blackTimerText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		blackTimerText.setEditable(false);
		blackTimerText.setBackground(new Color(238, 238, 238));
		blackTimerText.setBounds(55, 27, 40, 20);
		headerPanel.add(blackTimerText);
		//blacks timer properties.
		//every 1 second the timer changes.
		timerBlack = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//counting down by seconds (1000 ms)
				countB -= 1000;
				if (countB >= 0) {
					//changing the time from milliseconds to seconds.
					minutes = (int) (countB / 1000) / 60; 
					seconds = (int) (countB / 1000) % 60;

					blackTimerText.setText(df.format(minutes) + ":" + df.format(seconds));
				} 
				else {
					//stopping timer.
					((Timer) (e.getSource())).stop();

					//scaling image for popup.
					ImageIcon ic = new ImageIcon("res\\flag.png");
					Image img = ic.getImage();
					Image newimg = img.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH ) ;  
					ic = new ImageIcon(newimg);

					//sending popup message.
					JOptionPane.showOptionDialog(null, "Congratulations White!\nYou won the game because your opponent ran out of time.", "VICTORY!", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);
				}
			}
		});
		timerBlack.setInitialDelay(0);
		timerBlack.stop();


		//white text pane for turn timer.
		JTextPane whiteTimerText = new JTextPane();
		whiteTimerText.setText("10:00");
		whiteTimerText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		whiteTimerText.setEditable(false);
		whiteTimerText.setBackground(new Color(238, 238, 238));
		whiteTimerText.setBounds(55, 3, 40, 20);
		headerPanel.add(whiteTimerText);
		//whites timer properties.
		//every 1 second the timer changes.
		timerWhite = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//counting down by seconds (1000 ms)
				countW -= 1000;
				if (countW >= 0) {
					//changing the time from milliseconds to seconds.
					minutes = (int) (countW / 1000) / 60; 
					seconds = (int) (countW / 1000) % 60;

					whiteTimerText.setText(df.format(minutes) + ":" + df.format(seconds));
				} 
				else {
					//stopping timer.
					((Timer) (e.getSource())).stop();

					//scaling image for popup.
					ImageIcon ic = new ImageIcon("res\\flag.png");
					Image img = ic.getImage();
					Image newimg = img.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH ) ;  
					ic = new ImageIcon(newimg);

					//sending popup message.
					JOptionPane.showOptionDialog(null, "Congratulations Black!\nYou won the game because your opponent ran out of time.", "VICTORY!", JOptionPane.INFORMATION_MESSAGE, 0, ic, new Object[]{}, null);
				}
			}
		});
		timerWhite.setInitialDelay(0);
		timerWhite.start();


		//text pane to show how many moves 
		turnText.setText("Turn: 1");
		turnText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		turnText.setEditable(false);
		turnText.setBackground(new Color(238, 238, 238));
		turnText.setBounds(505, 3, 54, 20);
		headerPanel.add(turnText);

		//setting up gridlayout for whites taken pieces.
		whitePieces.setBounds(105, 25, 352, 24);
		whitePieces.setLayout(new GridLayout(1, 16));
		whitePieces.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 
		headerPanel.add(whitePieces);

		//setting up gridlayout for blacks taken pieces.
		blackPieces.setBounds(105, 2, 352, 24);
		blackPieces.setLayout(new GridLayout(1, 0, 0, 0));
		blackPieces.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 
		headerPanel.add(blackPieces);

		//text pane for move history (implementing scroll bar also) onto the east side.
		movesListText = new JTextPane();
		movesListText.setEditable(false);
		movesListText.setBounds(0, 0, 150, 55);
		movesListText.setPreferredSize(new Dimension(149, 50));
		movesListText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		movesListText.setText(" Move History:");	
		movesListText.setBackground(new Color(238, 238, 238));
		//movesListText.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 
		movesListText.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null)); 
		contentPane.add(movesListText, BorderLayout.EAST);

		//text pane for the blacks taken piece point value.
		blackPointsText.setBounds(464, 27, 35, 20);
		blackPointsText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		blackPointsText.setText("+0");
		blackPointsText.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 
		blackPointsText.setBackground(new Color(238, 238, 238));
		blackPointsText.setEditable(false);
		blackPointsText.setParagraphAttributes(attributes, true);
		headerPanel.add(blackPointsText);

		//text pane for the whites taken piece point value.
		whitePointsText.setBounds(464, 3, 35, 20);
		whitePointsText.setFont(new Font("Times New Roman", Font.BOLD, 14));
		whitePointsText.setText("+0");
		whitePointsText.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 
		whitePointsText.setBackground(new Color(238, 238, 238));
		whitePointsText.setEditable(false);
		whitePointsText.setParagraphAttributes(attributes, true);
		headerPanel.add(whitePointsText);

		//Resign button properties.
		resignButton = new JButton("Resign");
		//setting the action listener for the resign button.
		resignButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Header.resign(ChessBoard.currentTurn);
			}
		});
		resignButton.setBounds(821, 25, 75, 23);
		headerPanel.add(resignButton);

		//Draw button properties.
		drawButton = new JButton("Draw");
		//setting the action listener for the resign button.
		drawButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Header.draw();
			}
		});
		drawButton.setBounds(821, 2, 75, 23);
		headerPanel.add(drawButton);



		//numerical value panel properties.
		//setting information for the panel containing the numerical values.
		numValues = new JPanel();
		contentPane.add(numValues, BorderLayout.WEST);
		numValues.setPreferredSize(new Dimension(50, 50));
		numValues.setLayout(new GridLayout(8, 0));
		numValues.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

		//personalizing the labels
		Color c = new Color(238, 238, 238);
		Icon ic1 = new ImageIcon("res\\8.png"); Icon ic2 = new ImageIcon("res\\7.png"); Icon ic3 = new ImageIcon("res\\6.png"); Icon ic4 = new ImageIcon("res\\5.png");
		Icon ic5 = new ImageIcon("res\\4.png"); Icon ic6 = new ImageIcon("res\\3.png"); Icon ic7 = new ImageIcon("res\\2.png"); Icon ic8 = new ImageIcon("res\\1.png");

		JButton jp1 = new JButton(ic1); jp1.setBackground(c); jp1.setEnabled(false);  jp1.setDisabledIcon(ic1); jp1.setIcon(ic1); jp1.setBorderPainted(false);
		JButton jp2 = new JButton(ic2); jp2.setBackground(c); jp2.setEnabled(false);  jp2.setDisabledIcon(ic2); jp2.setIcon(ic2); jp2.setBorderPainted(false);
		JButton jp3 = new JButton(ic3); jp3.setBackground(c); jp3.setEnabled(false);  jp3.setDisabledIcon(ic3); jp3.setIcon(ic3); jp3.setBorderPainted(false);
		JButton jp4 = new JButton(ic4); jp4.setBackground(c); jp4.setEnabled(false);  jp4.setDisabledIcon(ic4); jp4.setIcon(ic4); jp4.setBorderPainted(false);
		JButton jp5 = new JButton(ic5); jp5.setBackground(c); jp5.setEnabled(false);  jp5.setDisabledIcon(ic5); jp5.setIcon(ic5); jp5.setBorderPainted(false);
		JButton jp6 = new JButton(ic6); jp6.setBackground(c); jp6.setEnabled(false);  jp6.setDisabledIcon(ic6); jp6.setIcon(ic6); jp6.setBorderPainted(false);
		JButton jp7 = new JButton(ic7); jp7.setBackground(c); jp7.setEnabled(false);  jp7.setDisabledIcon(ic7); jp7.setIcon(ic7); jp7.setBorderPainted(false);
		JButton jp8 = new JButton(ic8); jp8.setBackground(c); jp8.setEnabled(false);  jp8.setDisabledIcon(ic8); jp8.setIcon(ic8); jp8.setBorderPainted(false);

		numValues.add(jp1); numValues.add(jp2);numValues.add(jp3);numValues.add(jp4);numValues.add(jp5);numValues.add(jp6);numValues.add(jp7);numValues.add(jp8);


		//alphabetical value panel properties.
		//setting information for the panel containing the alphabetical values.
		alphValues = new JPanel();
		contentPane.add(alphValues, BorderLayout.SOUTH);
		alphValues.setPreferredSize(new Dimension(80, 50));
		alphValues.setBorder(new EmptyBorder(0, 0, 0, 0));
		alphValues.setLayout(null);

		//personalizing the labels
		ic1 = new ImageIcon("res\\a.png"); ic2 = new ImageIcon("res\\b.png"); ic3 = new ImageIcon("res\\c.png"); ic4 = new ImageIcon("res\\d.png");
		ic5 = new ImageIcon("res\\e.png"); ic6 = new ImageIcon("res\\f.png"); ic7 = new ImageIcon("res\\g.png"); ic8 = new ImageIcon("res\\h.png");

		jp1 = new JButton(ic1); jp1.setBounds(52, 0, 91, 50);  jp1.setBackground(c); jp1.setEnabled(false);  jp1.setDisabledIcon(ic1); jp1.setIcon(ic1); jp1.setBorderPainted(false);
		jp2 = new JButton(ic2); jp2.setBounds(142, 0, 91, 50); jp2.setBackground(c); jp2.setEnabled(false);  jp2.setDisabledIcon(ic2); jp2.setIcon(ic2); jp2.setBorderPainted(false);
		jp3 = new JButton(ic3); jp3.setBounds(233, 0, 91, 50); jp3.setBackground(c); jp3.setEnabled(false);  jp3.setDisabledIcon(ic3); jp3.setIcon(ic3); jp3.setBorderPainted(false);
		jp4 = new JButton(ic4); jp4.setBounds(324, 0, 91, 50); jp4.setBackground(c); jp4.setEnabled(false);  jp4.setDisabledIcon(ic4); jp4.setIcon(ic4); jp4.setBorderPainted(false);
		jp5 = new JButton(ic5); jp5.setBounds(416, 0, 91, 50); jp5.setBackground(c); jp5.setEnabled(false);  jp5.setDisabledIcon(ic5); jp5.setIcon(ic5); jp5.setBorderPainted(false);
		jp6 = new JButton(ic6); jp6.setBounds(508, 0, 91, 50); jp6.setBackground(c); jp6.setEnabled(false);  jp6.setDisabledIcon(ic6); jp6.setIcon(ic6); jp6.setBorderPainted(false);
		jp7 = new JButton(ic7); jp7.setBounds(600, 0, 91, 50); jp7.setBackground(c); jp7.setEnabled(false);  jp7.setDisabledIcon(ic7); jp7.setIcon(ic7); jp7.setBorderPainted(false);
		jp8 = new JButton(ic8); jp8.setBounds(691, 0, 81, 50); jp8.setBackground(c); jp8.setEnabled(false);  jp8.setDisabledIcon(ic8); jp8.setIcon(ic8); jp8.setBorderPainted(false);

		alphValues.add(jp1); alphValues.add(jp2); alphValues.add(jp3); alphValues.add(jp4); alphValues.add(jp5); alphValues.add(jp6); alphValues.add(jp7); alphValues.add(jp8);
	}

	public static void resetValuesForBlackPOV(JPanel numValues, JPanel alphValues) {
		numValues.removeAll();
		alphValues.removeAll();
		
		Icon ic1 = new ImageIcon("res\\1.png"); Icon ic2 = new ImageIcon("res\\2.png"); Icon ic3 = new ImageIcon("res\\3.png"); Icon ic4 = new ImageIcon("res\\4.png");
		Icon ic5 = new ImageIcon("res\\5.png"); Icon ic6 = new ImageIcon("res\\6.png"); Icon ic7 = new ImageIcon("res\\7.png"); Icon ic8 = new ImageIcon("res\\8.png");

		JButton jp1 = new JButton(ic1); jp1.setEnabled(false);  jp1.setDisabledIcon(ic1); jp1.setIcon(ic1); jp1.setBorderPainted(false);
		JButton jp2 = new JButton(ic2); jp2.setEnabled(false);  jp2.setDisabledIcon(ic2); jp2.setIcon(ic2); jp2.setBorderPainted(false);
		JButton jp3 = new JButton(ic3); jp3.setEnabled(false);  jp3.setDisabledIcon(ic3); jp3.setIcon(ic3); jp3.setBorderPainted(false);
		JButton jp4 = new JButton(ic4); jp4.setEnabled(false);  jp4.setDisabledIcon(ic4); jp4.setIcon(ic4); jp4.setBorderPainted(false);
		JButton jp5 = new JButton(ic5); jp5.setEnabled(false);  jp5.setDisabledIcon(ic5); jp5.setIcon(ic5); jp5.setBorderPainted(false);
		JButton jp6 = new JButton(ic6); jp6.setEnabled(false);  jp6.setDisabledIcon(ic6); jp6.setIcon(ic6); jp6.setBorderPainted(false);
		JButton jp7 = new JButton(ic7); jp7.setEnabled(false);  jp7.setDisabledIcon(ic7); jp7.setIcon(ic7); jp7.setBorderPainted(false);
		JButton jp8 = new JButton(ic8); jp8.setEnabled(false);  jp8.setDisabledIcon(ic8); jp8.setIcon(ic8); jp8.setBorderPainted(false);

		numValues.add(jp1); numValues.add(jp2); numValues.add(jp3); numValues.add(jp4); numValues.add(jp5); numValues.add(jp6); numValues.add(jp7); numValues.add(jp8);

		ic1 = new ImageIcon("res\\h.png"); ic2 = new ImageIcon("res\\g.png"); ic3 = new ImageIcon("res\\f.png"); ic4 = new ImageIcon("res\\e.png");
		ic5 = new ImageIcon("res\\d.png"); ic6 = new ImageIcon("res\\c.png"); ic7 = new ImageIcon("res\\b.png"); ic8 = new ImageIcon("res\\a.png");
		
		jp1 = new JButton(ic1); jp1.setBounds(52, 0, 91, 50);  jp1.setEnabled(false);  jp1.setDisabledIcon(ic1); jp1.setIcon(ic1); jp1.setBorderPainted(false);
		jp2 = new JButton(ic2); jp2.setBounds(142, 0, 91, 50); jp2.setEnabled(false);  jp2.setDisabledIcon(ic2); jp2.setIcon(ic2); jp2.setBorderPainted(false);
		jp3 = new JButton(ic3); jp3.setBounds(233, 0, 91, 50); jp3.setEnabled(false);  jp3.setDisabledIcon(ic3); jp3.setIcon(ic3); jp3.setBorderPainted(false);
		jp4 = new JButton(ic4); jp4.setBounds(324, 0, 91, 50); jp4.setEnabled(false);  jp4.setDisabledIcon(ic4); jp4.setIcon(ic4); jp4.setBorderPainted(false);
		jp5 = new JButton(ic5); jp5.setBounds(416, 0, 91, 50); jp5.setEnabled(false);  jp5.setDisabledIcon(ic5); jp5.setIcon(ic5); jp5.setBorderPainted(false);
		jp6 = new JButton(ic6); jp6.setBounds(508, 0, 91, 50); jp6.setEnabled(false);  jp6.setDisabledIcon(ic6); jp6.setIcon(ic6); jp6.setBorderPainted(false);
		jp7 = new JButton(ic7); jp7.setBounds(600, 0, 91, 50); jp7.setEnabled(false);  jp7.setDisabledIcon(ic7); jp7.setIcon(ic7); jp7.setBorderPainted(false);
		jp8 = new JButton(ic8); jp8.setBounds(691, 0, 81, 50); jp8.setEnabled(false);  jp8.setDisabledIcon(ic8); jp8.setIcon(ic8); jp8.setBorderPainted(false);

		alphValues.add(jp1); alphValues.add(jp2); alphValues.add(jp3); alphValues.add(jp4); alphValues.add(jp5); alphValues.add(jp6); alphValues.add(jp7); alphValues.add(jp8);



	}

}
