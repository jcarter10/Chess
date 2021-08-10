/*
 * By: Jordan Carter
 * Description: Acts as the home page for the user, lets them choose between 3 options and facilitates the program depending
 * 				on their answer. *Can also use this class as the main class.
 * 
 */

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JButton;

public class HomePage extends JFrame {

	//Globals
	public static boolean customGame = false;
	public static boolean botGame = false;
	public static boolean onlineGame = false;
	public static JFrame hp;
	public static boolean easyBot = false;
	public static boolean mediumBot = false;
	
	//Variables
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//runs home screen.
					hp = new HomePage();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}
	
	
	//Creates the frame.
	public HomePage() {
		setSize(500, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		//setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//title icon
		JLabel titleIcon = new JLabel("Chess");
		titleIcon.setFont(new Font("Times New Roman", Font.BOLD, 50));
		titleIcon.setBounds(183, 57, 129, 78);
		contentPane.add(titleIcon);
		
		JTextPane writtenByText = new JTextPane();
		writtenByText.setEditable(false);
		writtenByText.setText("Written By: Jordan Carter");
		writtenByText.setBounds(5, 435, 150, 20);
		writtenByText.setBackground(new Color(238, 238, 238));
		contentPane.add(writtenByText);
		
		//button properties for a computer game
		JButton botButton = new JButton("Computer");
		botButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		botButton.setBounds(195, 192, 100, 30);
		botButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				botGame = true;
				runBotGame();
			}
		});
		contentPane.add(botButton);
		
		//button properties for a custom game.
		JButton customButton = new JButton("Custom");
		customButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		customButton.setBounds(195, 252, 100, 30);
		customButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customGame = true;
				runCustomGame();
			}
		});
		contentPane.add(customButton);
		
		//button properties for a online game.
		JButton onlineButton = new JButton("Online");
		onlineButton.setBounds(195, 312, 100, 30);
		onlineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runOnlineGame();
			}
		});
		contentPane.add(onlineButton);
		
		//setting JFrame visible here because buttons are invisible if set before button creation.
		setVisible(true);
	}
	
	
	//starts up a custom game for the user.
	public static void runCustomGame() {
		customGame = true;
		
		System.out.println("Game Type: Custom\n");
		
		//hides the homepage.
		MainPlayer.hp.setVisible(false);
		//sets the gui.
		new MainFrame();
		//sets all the backend data for the board.
		new ChessBoard();
	}
	
	
	//starts up a bot game for the user.
	public static void runBotGame() {
		String buttons[] = {"Easy", "Medium"};
		int i = 0;
		
		//asks user for difficulty.
		int answer = (JOptionPane.showOptionDialog(null, "Pick the bot difficulty please.", "Bot Game",
				JOptionPane.ERROR_MESSAGE, 0, new ImageIcon("res\\robot.png"), buttons, buttons[i]));
		//easy difficulty bot.
		if (answer == 0) {
			easyBot = true;
			System.out.println("Game Type: Easy Bot");
		}
		//medium difficulty bot.
		else if (answer == 1) {
			mediumBot = true;
			System.out.println("Game Type: Medium Bot");
		}
		else {
			System.exit(0);
		}
		
		botGame = true;
		
		//hides the homepage.
		MainPlayer.hp.setVisible(false);
		
		//sets the gui.
		new MainFrame();
		
		//sets all the backend data for the board.
		new ChessBoard();
	}
	
	//starts up an online game for the user (where the current user acts as the server)
	public static void runOnlineGame() {
		onlineGame = true;
		
		System.out.println("Game Type: Online\n");
		
		//guest player.
		if (GuestPlayer.guest == true) {
			//invoke the GUI later so that the guest can read/write in background on socket.
			SwingUtilities.invokeLater(() -> {
				//connects to the server
				GuestPlayer.connect();
				
				//sets the gui.
				JFrame mfClient = new MainFrame();
				mfClient.setTitle("Client");

				//sets all the backend data for the board.
				new ChessBoard();
				
				//stops timers
				MainFrame.timerWhite.stop();
				MainFrame.timerBlack.stop();
			});
		} 
		//main player.
		else {
			//invoke the GUI later so that the main/server can read/write in background on socket.
			SwingUtilities.invokeLater(() -> {
				//hides the homepage cause were done with it.
				MainPlayer.hp.setVisible(false);

				try {
					//creates the server from main
					MainPlayer.startServer();

					//sets the gui
					JFrame mfServer = new MainFrame();
					mfServer.setTitle("Server");
					
					//adding a new window listener to save game move data to text file when game is over.
					mfServer.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							//append the game history to the text file.
							try {
								BufferedWriter bw = new BufferedWriter(new FileWriter("GameHistory", true));
								bw.write(MainFrame.movesListText.getText());
								bw.flush();
								bw.close();
								
								//stops timers
								MainFrame.timerWhite.stop();
								MainFrame.timerBlack.stop();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
							//closing app when done data saving
							System.exit(0);
						}
					});
					
					//sets all the backend data for the board.
					new ChessBoard();

				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
}
