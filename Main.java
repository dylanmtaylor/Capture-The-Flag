// Capture the Flag Project
// Written by Dylan Taylor, Rhyan Smith, Justin Gompers, and Manny Castillo
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

@SuppressWarnings("serial") //Hides pointless "static final serialVersionUID" warnings. Just ignore this.
public class Main extends JFrame {

	// Constant Application Specific Settings
	final static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); //gets the screen's dimensions
	protected static JFrame mFrame; //The object for the window
	final static short x = 530; // The width of the window
	final static short y = 520; // The height of the window
	final static short rulesWidth = 550; //rules panel width
	final static short rulesHeight = 620; //rules panel height
	final static boolean forceRulesOnTop = true;

	// Constant Settings Related to the Actual Game
	final static boolean bell = true; //plays a coin sound between turns if no other sound is played for that action.
	final static boolean useExplosions = true; //enables or disables explosions; recommended to leave this set to true
	final static boolean forceVisible = false; //forces all pieces to be visible for debugging purposes
	final static boolean musicEnabled = true; //self-explanatory
	final static String musicFile = "music.au"; //background music to play
	final static boolean mouseVerbose = false; //displays a lot of extra logging messages
	final static boolean enableFlip = false; //This is HIGHLY experimental, but fun to play with. recommended to keep this disabled	final static boolean debugPiecePlacement = false;
	final static short rows = 9; // The number of rows to draw
	final static short columns = 10; // The number of columns to draw
	final static short tile_size = 50; // The size of every individual tile on the board
	final static short xpadding = (x - columns * tile_size) / 2; // horizontal window padding
	final static short ypadding = (y - rows * tile_size) / 2; // vertical window padding
	public static boolean hideAllExceptTerrain = false;
	public static boolean gameStarted = false; // remains false until actual game is started
	public static boolean gameOver = false;
	public static short gameResult = 0; //This contains the winner of the game, or 0 if the game ended in a draw.
	final static short waitBetweenMoves = 250; //in milliseconds -- because the game would otherwise feel too fast paced
	final static short numMountains = 3;
	final static short numWater = 2;
	public static short currentPiecePlacing; //used to let the mouse hover method know what piece we are currently placing	public static boolean redPiecesPlaced = false;	public static boolean orangePiecesPlaced = false;	public static short piecePlacementNumber = 0;	public static short numberOfPiece = 0;

	// Constant Integer Object IDs
	public final static short FLAG = 9001; // Can't move
	public final static short FAKEFLAG = 9002;
	public final static short WATER = 9003;
	public final static short MOUNTAIN = 9004;
	public final static short BOMB = 9005;

	// Constant Integer Army IDs - NOTE: All variables are singular for consistency
	public final static short GENERAL = 9006;
	public final static short TANK = 9007;
	public final static short CANNON = 9008;
	public final static short MACHINEGUN = 9009;
	public final static short INFANTRY = 9010;
	public final static short MINER = 9011; // Can remove bombs
	public final static short FROGMAN = 9012; // Can swim across water

	// Constant Integer Neutral ID
	public final static short EMPTY = 9013; // Empty field

	// Constant Integer Territory Control IDs, one army will be orange, one army will be red
	public final static short NEUTRAL = 9014;
	public final static short ORANGEARMY = 9015;
	public final static short REDARMY = 9016;

	// Constant Integer Attack Results
	public final static short ATTACK_VICTORY = 9017; // attacker defeats attacked
	public final static short ATTACK_DEFEAT = 9018; // attacker gets defeated
	public final static short ATTACK_DRAW = 9019; // both pieces are destroyed

	// Constant Integer "Special" IDs
	public final static short FROGMAN_ON_WATER = 9017;
	public final static short VICTORY_FANFARE = 9018;
	public final static short EXPLOSION = 9019;
	public final static short POPPING = 9020;
	public final static short UP = 9021;
	public final static short DOWN = 9022;
	public final static short LEFT = 9023;
	public final static short RIGHT = 9024;
	public final static short DEFEAT_TROMBONE_SOUND = 9025;
	public final static short INVALID_SELECTION_SOUND_EFFECT = 9026;
	public final static short FAKEFLAG_SOUND = 9027;

	// Constant Integer Ranges for Object Types, contains starting and
	public final static short[] ARMYUNITS = new short[] { 9006, 9012 };
	public final static short[] OBJECTS = new short[] { 9001, 9005 };

	// Constant Color Values for Graphics and Text
	final static Color backgroundColor = new Color(82, 61, 0);
	final static Color squareOutlineColor = new Color(25, 45, 25);
	final static Color orangeArmyTurnTextColor = new Color(255, 191, 128);
	final static Color redArmyTurnTextColor = new Color(255, 128, 128);
	final static Color neutralPieceTextColor = new Color(128, 255, 128);
	final static Color actionTextColor = new Color(128, 128, 255);
	final static Color rulesTextColor = new Color(229,229,255, 100);
	final static Color emptySquareBackgroundColor = new Color(51, 102, 0);
	final static Color redArmyFlagBackgroundColor = new Color(255, 128, 128);
	final static Color orangeArmyFlagBackgroundColor = new Color(255, 191, 128);
	final static Color redArmyObscuredTileBackgroundColor = new Color(132, 30, 30); //hidden piece
	final static Color orangeArmyObscuredTileBackgroundColor = new Color(153, 92, 30); //hidden piece
	final static Color blankFlagBackgroundColor = new Color(220,220,220);
	final static Color waterBackgroundColor = new Color(26, 79, 132);
	final static Color mountainBackgroundColor = new Color(207, 226, 243);
	final static Color bombBackgroundColor = new Color(105, 105, 105);
	final static Color generalBackgroundColor = new Color(255,223,138);
	final static Color tankBackgroundColor = new Color(153,255,153);
	final static Color cannonBackgroundColor = new Color(31,31,31);
	final static Color infantryBackgroundColor = new Color(130,208,53);
	final static Color machineGunBackgroundColor = new Color(219,217,87);
	final static Color minerBackgroundColor = new Color(208,130,53);
	final static Color frogmanBackgroundColor = new Color(151,151,231);
	
	//Colors used for text only
	final static Paint banannaYellow = new Color(230,230,61);
	final static Paint limeGreen = new Color(145,230,61);
	final static Paint cherryRed = new Color(230,60,60);
	final static Paint lightBlue = new Color(198,179,255);
	final static Paint lightAmber = new Color(220,166,121);

	// Hover colors will be used for board interaction, until the square is selected using the enter 
	final static Color hoverOutlineColor = new Color(20, 20, 20, 200);
	final static Color hoverSquareAlphaOverlay = new Color(20, 20, 20, 50);

	// The following colors are red. I plan on having an orange color and a red color, depending on which army's turn it is.
	final static Color selectedOutlineColor = new Color(105, 20, 20, 200); // mostly opaque; 200/255
	final static Color selectedSquareAlphaOverlay = new Color(105, 20, 20, 100);

	// General Purpose Variables and Objects
	protected static Random gen = new SecureRandom(); // Used for generating random numbers if necessary

	// Game Related Variables and Objects
	// 9014 = Neutral (not used for turns); 9015 = Orange Army (Player 1); 9016 = Red Army (Player 2)
	static short currentPlayer = (short) (gen.nextInt(2) + 9015); // The current player. Randomly selected before the game starts.
	static short board[][] = new short[rows][columns]; // An integer array to keep track of where objects are positioned on the board
	static short control[][] = new short[rows][columns]; // An integer array to keep track of which army controls each square on the board

	// Variables Related to Interface and Controls
	static boolean select = false; // whether or not a Point is selected or not
	static boolean hover = false; // whether or not we are selecting a square using the board
	final static Point impossiblePoint = new Point(-1,-1);
	static Point selected = impossiblePoint; // the location (x,y) of the selected square in the window
	static Point lastClicked = impossiblePoint; // the last clicked square in the window, regardless if selected or not
	static Point lastSelected = impossiblePoint; // the last square selected
	static Point mouseSquare = impossiblePoint; //last square the mouse was over
	static Point boardSquare = impossiblePoint; //square used for the board selection 

	// Pieces to manually place, and the number of each piece per team
	final static short[] piecesToPlace = new short[]{FLAG,FAKEFLAG,BOMB,GENERAL,TANK,CANNON,MACHINEGUN,INFANTRY,MINER,FROGMAN};
	final static short[] numberToPlace = new short[]{1,1,2,1,2,1,2,5,2,2};
	final static short[] topHalfRange = new short[]{0,3};
	final static short noMansLand = 4; //middle ground, pieces can't be placed here
	final static short[] bottomHalfRange = new short[]{5,8};
	final static short piece_placement_polling_time = 75; //time to wait between checking if a piece has been placed. in milliseconds.

	//Used for rendering and displaying the rules panel
	static JFrame ruleFrame;
	static JPanel ruleContainer;

	public static void main(final String[] args) {
		mFrame = new JFrame("Capture the Flag");
		new BasicAudioPlayer(Resources.coinFlipSoundEffect, 0).start(); //play a coin flipping sound
		if (musicEnabled) new BasicAudioPlayer(Resources.getMusic(), -1).start();
		mFrame.setResizable(false);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.getContentPane().add(new GamePanel());
		mFrame.pack();
		try  { //Makes the User Interface manager use the native platform look and feel when drawing the GUI.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(final Exception e) {};
		mFrame.setLocation((screen.width - x)/2, (screen.height - y)/2); //We want to spawn the game in the middle of the screen...
		mFrame.setVisible(true);
		startGame();
	}

	/**
	 * This method is run at the beginning of the game, before attacks can begin. This method handles
	 * the random placement of mountains and water, and allows the players to place the pieces.
	 * @author Dylan Taylor
	 */
	public static void startGame() {
		System.out.printf("The %s army will go first.\n", (currentPlayer == 9015 ? "orange" : "red"));
		resetBoard();		mFrame.repaint();
		displayNextPlayerAlert(); //Lets first player know to place their pieces
		currentPiecePlacing = piecesToPlace[0]; //reset piece being placed		//piece placement and game starting is now handled already using placePiece
	}	
	/**
	 * Clears all pieces from the board and resets who controls each piece
	 * @author Dylan Taylor
	 */	public static void clearBoard() {		for (final short[] column : board) Arrays.fill(column, EMPTY); // Fills all columns in the board with 'empty' spaces
		for (final short[] column : control) Arrays.fill(column, NEUTRAL); // Sets the territory of each square to neutral, controlled by no army.
		return;	}
	
	/**
	 * Resets the board and scatters the terrain until the player decides the placement of terrain is acceptable
	 * @author Dylan Taylor
	 */
	public static void resetBoard() {
		do {			clearBoard();
			scatterTerrain();
			mFrame.repaint();		} while (!terrainConfirm());
	}
		/**
	 * Asks if the scattering of terrain is acceptable.
	 * @return Whether or not the players are satisfied with the scattering
	 * @author Dylan Taylor
	 */
	public static boolean terrainConfirm() {
		final JFrame confirmFrame = new JFrame();
		confirmFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		confirmFrame.setResizable(false);
		return (JOptionPane.showConfirmDialog(confirmFrame, "Is this scattering of terrain acceptable?", "Select An Option", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}	
	/**
	 * Scatters the terrain pieces...
	 */	public static void scatterTerrain() {		//scatter the mountains and water.
		scatter(MOUNTAIN, numMountains);
		scatter(WATER, numWater);		}

	/**	 * This method [placePieces] is DEPRECATED. It has been replaced with the _much_ better placePiece method. It is kept here for reference ONLY.
	 * Has the current player place their pieces on the screen.
	 * Red army has the top 4 rows of the field, Orange army has the bottom 4 rows of the field
	 * Each army will get to place:
	 *     # 1 Flag
	 *     # 1 Fake Flag (Blank)
	 *     # 2 Bombs
	 *     # 1 General
	 *     # 2 Tanks
	 *     # 1 Cannon
	 *	   # 2 Machine Guns
	 *	   # 5 Infantry
	 *     # 2 Miners
	 *     # 2 Frogmen
	 * Water and Mountains are already placed automatically upon the game starting. Players do not place these.
	 * @author Dylan Taylor
	 */
	/*public static void placePieces() {
		final Point impossiblePoint = new Point(-1,-1);
		Point click = impossiblePoint; //used to copy lastClicked in case player clicks again
		Point prevclick; //used to prevent trying to place all pieces on same square due to the loop
		boolean valid = false;
		//boolean soundPlayed = false;
		for (short pieceType = 0; pieceType < piecesToPlace.length; pieceType++) {
			for (short piecesPlaced = 0; piecesPlaced < numberToPlace[pieceType]; piecesPlaced++) {				
				currentPiecePlacing = piecesToPlace[pieceType];
				System.out.printf("Click or use the keyboard to place %s (%d/%d)\n", getSpecifiedPieceAsString(piecesToPlace[pieceType]),(piecesPlaced + 1),numberToPlace[pieceType]);
				lastSelected = impossiblePoint; //sets lastClicked to a Point that is impossible to click
				valid = false;
				//soundPlayed = false;
				do {					//System.out.println("Running through the piece placement loop"); //at least THIS works
					delayGame(piece_placement_polling_time);
					if (lastSelected != impossiblePoint) {
						prevclick = click;
						click = lastSelected;
						try {
							if ((board[click.y][click.x] == EMPTY) && (click != prevclick)) {
								if (withinRange(click.y, (currentPlayer == REDARMY ? topHalfRange : bottomHalfRange))) {
									System.out.printf("Placing %s at: [x=%d,y=%d]\n", getSpecifiedPieceAsString(piecesToPlace[pieceType]),click.x,click.y);
									board[click.y][click.x] = piecesToPlace[pieceType];
									control[click.y][click.x] = currentPlayer;
									//if (!soundPlayed) {
										playSoundEffect(POPPING);
										//soundPlayed = true;
									//}
									valid = true;
									mFrame.repaint();
									continue; //goes to next iteration of the for loop
								} else {
									//if (!soundPlayed) {
										playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
									//	soundPlayed = true;
									//}
									System.out.println("You are not allowed to place a piece in the selected square.");
								}
							} else {								
								if (click != prevclick) playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
							}
						} catch (final Exception e) {
							System.out.println("Exception occured: " + e);							continue; //do nothing
						}
					} else System.out.println("DEBUGGING: Last selected is equal to the impossible point");
				} while (!valid);
			}
		}
	}*/		/**
	 * This is performed after a piece is placed. This is a fundamental rewrite of the old placePieces method.
	 * I'm rewriting this because the placePieces method didn't work properly when the player chose to play again.
	 * This is a _dramatic_ change in how piece placement is done, and it's probably for the better.
	 * @author Dylan Taylor
	 */	public static void placePiece(final boolean mouse) {
		final Point click = (mouse) ? lastClicked : boardSquare; 
		boolean valid = false;
		try {			if ((board[click.y][click.x] == EMPTY)) {
				if (withinRange(click.y, (currentPlayer == REDARMY ? topHalfRange : bottomHalfRange))) {					System.out.printf("Placing %s at: [x=%d,y=%d]\n", getSpecifiedPieceAsString(piecesToPlace[piecePlacementNumber]),click.x,click.y);
					board[click.y][click.x] = piecesToPlace[piecePlacementNumber];
					control[click.y][click.x] = currentPlayer;
						playSoundEffect(POPPING);
					valid = true;
					mFrame.repaint();				} else {
					playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);					System.out.println("You are not allowed to place a piece in the selected square.");
					return; //prevent the number placed counter from incrementing				}
			} else return; //can't place a piece on a non-empty square		} catch (final Exception e) {			System.out.println("Exception occured: " + e);		}		//if we placed the right number of every piece
		if (((piecePlacementNumber + 1) == piecesToPlace.length) && ((numberOfPiece + 1) == numberToPlace[piecePlacementNumber])) {
			if (currentPlayer == REDARMY) redPiecesPlaced = true;			else if (currentPlayer == ORANGEARMY) orangePiecesPlaced = true;			if (redPiecesPlaced && orangePiecesPlaced) { //if both teams finished placing their pieces
				System.out.println("All pieces placed. Let's do this!");								gameStarted = true; //the game has begun, let the battle begin!				switchPlayer(); //displays the first player alert, not necessary to call it manually
			} else {
				piecePlacementNumber = 0;
				numberOfPiece = 0;				switchPlayer();				displayNextPlayerAlert(); //display the alert saying that it's now the other army's turn to place pieces
			}
		} else {
			if ((numberOfPiece + 1) == numberToPlace[piecePlacementNumber]) {
				piecePlacementNumber++;				numberOfPiece = 0;
			} else numberOfPiece++;
		}
		currentPiecePlacing = piecesToPlace[piecePlacementNumber];	}

	public static void inGameMouseClick() {
		doInGameSelection(lastClicked, true);
	}

	public static void inGameKeyboardSelection() {
		System.out.println("Selecting square...");
		lastSelected = boardSquare;
		doInGameSelection(boardSquare, false);
	}

	/**
	 * Handles what is done when the mouse is clicked during the game, after all pieces are places
	 * @author Dylan Taylor, Rhyan Smith
	 */
	public static void doInGameSelection(final Point clicked, final boolean mouse) {
		boolean soundPlayed = false;
		if (!select) { //if a square is not selected already
			if (control[clicked.y][clicked.x] == currentPlayer) { //the current player has control over the square
				if (isArmyUnit(clicked.y,clicked.x)) { //the square is an army unit
					System.out.println("Army unit selected.");
					selected = clicked; //set the currently selected point
					select = true; //flag that a square is selected
					showSelectedSquare();
					if (!soundPlayed) {
						playSoundEffect(POPPING);
						soundPlayed = true;
					}
				} else { //they selected an object.
					showClickedSquare(mouse);
					System.out.println("You can only move an army unit that you control.");
					System.out.println("Moving an object is not allowed.");
					if (!soundPlayed) {
						playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
						soundPlayed = true;
					}
					return; //objects can NOT be moved, not allowed. Ignore this click
				}
			} else {
				showClickedSquare(mouse);
				System.out.println("You can't select an opponent's piece or an empty piece");
				if (!soundPlayed) {
					playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
					soundPlayed = true;
				}
			}
		} else {
			//System.out.println("A square was clicked, and something was selected previously... we should move the piece now");
			showClickedSquare(mouse);
			if (clicked == selected) { //clicked Point is the same as the selected point
				System.out.println("Clicked square same as selection. Unselecting.");				
				if (!soundPlayed) {
					playSoundEffect(POPPING);
					soundPlayed = true;
				}
				select = false; //unselect the current square, assume player clicked by mistake
			} else {
				if (isValidMove(selected.x,selected.y,clicked.x,clicked.y)) { //if the move is allowed... ensures source is army, and checks rules.
					if (isArmyUnit(clicked.y,clicked.x)) { //check if the destination square is an army unit
						System.out.println("The destination square is an army unit.");
						if ((board[clicked.y][clicked.x] == FROGMAN_ON_WATER || board[clicked.y][clicked.x] == FROGMAN_ON_WATER)
								&& board[selected.y][selected.x] != FROGMAN) {
							if (!soundPlayed) {
								playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
								soundPlayed = true;
							}
							System.out.println("Only a \"Frogman\" can move onto water."); return; //Prevents water from being deleted
						}
						doAttack(selected.x,selected.y,clicked.x,clicked.y); //if so, attack the destination square						
						if (!soundPlayed) {
							playSoundEffect(POPPING);
							soundPlayed = true;
						}
					} else { //the destination square is NOT an army unit
						switch (board[clicked.y][clicked.x]) { //switch based on destination square
							case WATER: case FROGMAN_ON_WATER: if (board[selected.y][selected.x] == FROGMAN) {
								if (!soundPlayed) {
									playSoundEffect(FROGMAN);
									soundPlayed = true;
								}
								break;
							} else {
								System.out.println("Only a \"Frogman\" can move onto water.");
								if (!soundPlayed) {
									playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
									soundPlayed = true;
								}
							} return; //only the frogman can move onto water.
							//mountain destination piece should be handled in the is valid move method, but just in case...
							case MOUNTAIN: 
							  System.out.println("Moving a piece onto a mountain is not allowed."); 
							  if (!soundPlayed) {
									playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
									soundPlayed = true;
							  }
							  return;
							case FLAG: 
								System.out.println("Flag captured!"); break;
							case FAKEFLAG: 
								playSoundEffect(FAKEFLAG_SOUND);
								System.out.println("Enemy flag captured! ... wait a minute, this one's a FAKE!");
								displayFakeEndGameAlert();
								break;
							case EMPTY:
								if (bell) new BasicAudioPlayer(Resources.coinFlipSoundEffect, 0).start(); //play a coin flipping sound, but only if the square is empty
								case BOMB: //if it's empty or a bomb, handle moving into it
								break; //movement code is below the switch statement... break out of it
						}						
						if (!soundPlayed) {
							playSoundEffect(POPPING);
							soundPlayed = true;
						}
						movePiece(selected.x,selected.y,clicked.x,clicked.y);
					}
					select = false; //nothing is selected any more
					mFrame.repaint(100); //redraw all the pieces AFTER movement is performed, 100 is max repashort time allowed
					delayGame(waitBetweenMoves); //wait 3 seconds
					mFrame.repaint(); //in case draw didn't finish in time
					switchPlayer();
				} else {
					System.out.println("Invalid selection. Move not allowed. Unselecting square.");
					if (!soundPlayed) {
						playSoundEffect(INVALID_SELECTION_SOUND_EFFECT);
						soundPlayed = true;
					}
					select = false; //unselects the square.
					return;
				}
			}
		}	
	}

	/**
	 * Gets information about the square located at the specified point. Used by selected square and clicked square
	 * @return A string containing information on the square
	 * @author Dylan Taylor
	 */
	public static String getSquareInformation(final Point square) {
		final StringBuilder squareInfo = new StringBuilder(square.toString());
		squareInfo.append((!isHidden(square.y,square.x) ? " (" + getSquarePieceAsString(square.y, square.x) + ") " : " (HIDDEN)\n"));
		if (!isHidden(square.y,square.x)) {
			squareInfo.append("Square Owner: ");
			squareInfo.append(control[square.y][square.x] == REDARMY ? "Red Army" :
				control[square.y][square.x] == ORANGEARMY ? "Orange Army" : "Neutral");
			squareInfo.append("\n"); //new line
		}
		return squareInfo.toString();
	}

	/**
	 * Determines whether or not the square should be hidden
	 * @return whether or not the square is hidden
	 * @author Dylan Taylor
	 */
	public static boolean isHidden(final int y2, final int x2) {
		if (forceVisible || gameOver || (board[y2][x2] == EXPLOSION)) {
			return false;
		} else if (control[y2][x2] == NEUTRAL) {
			return false; //neutral squares are ALWAYS hidden
		} else if (hideAllExceptTerrain) {
			return true;
		} else if (currentPlayer != control[y2][x2]) {
			return true; //neutral squares are already handled. no need for redundancy
		} else {
			/*System.out.println("Visibility is not handled for this piece.");*/ return false; //because visible would be equal to false
		}
	}

	/**
	 * Used to check if a piece is terrain or not
	 * @param piece_type
	 * @return whether the piece is terrain or not
	 * @author Dylan Taylor
	 */
	public static boolean isTerrain(final short piece_type) {
		return piece_type == MOUNTAIN || piece_type == WATER;
	}

	/**
	 * Cool effect that flips the board, and make cheating much harder.
	 * Highly experimental, but fun to play with. Doesn't work right yet...
	 * @author Dylan Taylor
	 */
	public static void flipTheBoard() {
		for (short column = 0; column < board.length; column++) {
			short end_row = (short) (board[column].length-1);
			for (short start_row = 0; start_row < end_row; start_row++) {
				final short tempp = board[start_row][column];
				final short tempc = control[start_row][column];
				board[start_row][column] = board[end_row][column];
				control[start_row][column] = control[end_row][column];
				board[end_row][column] = tempp;
				control[end_row][column] = tempc;
				end_row--;
			}
		}
		/*for (int[] row : board) {
     short end_col = row.length+1;
     for (short start_col = 0; start_col < end_col; start_col++) {
         short temp_piece = row[start_col];
         row[start_col] = row[end_col];
         row[end_col] = temp_piece;
         end_col--;
     }
    }
    for (int[] crow : control) {
     short end_col = crow.length+1;
     for (short start_col = 0; start_col < end_col; start_col++) {
         short temp_control = crow[start_col];
         crow[start_col] = crow[end_col];
         crow[end_col] = temp_control;
         end_col--;
     }
    }*/
	}


	/**
	 * Displays the square selected's coordinates, as well as the type of piece there, if it's not hidden
	 * @author Dylan Taylor
	 */
	public static void showSelectedSquare() {
		System.out.print("Selected Square: " + getSquareInformation(selected));
	}

	/**
	 * Displays the square clicked's coordinates, as well as the type of piece there, if it's not hidden
	 * @author Dylan Taylor
	 */
	public static void showClickedSquare(final boolean mouse) {
		System.out.print("Clicked Square: " + getSquareInformation((mouse) ? lastClicked : boardSquare));
	}

	/**
	 * Delays the game, by waiting the specified number of milliseconds.
	 * 1000 milliseconds is equivalent to one second.
	 * @param delay_time_in_milliseconds Self explanatory
	 * @author Dylan Taylor
	 */
	private static void delayGame(final long delay_time_in_milliseconds) {
		try {
			Thread.currentThread();
			Thread.sleep(delay_time_in_milliseconds);
		} catch (final Exception e) {};
	}

	/**
	 * Handles the logic behind one piece attacking another.
	 * @param x2 Attacking piece's column
	 * @param y2 Attacking piece's row
	 * @param x3 Defending piece's column
	 * @param y3 Defending piece's row
	 * @author Dylan Taylor
	 */
	public static void doAttack(final int x2, final int y2, final int x3, final int y3) {
		//Basic precautions against improper usage...
		if (!isArmyUnit(y2, x2) || !isArmyUnit(y3, x3)) {
			System.out.println("One of the pieces in the attack was not an army unit");
			return; //if this method is called using invalid parameters, handle it by doing nothing
		}
		if (board[y3][x3] == FROGMAN_ON_WATER || board[y3][x3] == FROGMAN_ON_WATER) {
			if (board[y2][x2] != FROGMAN) {
				System.out.println("Only the \"Frogman\" can move onto water."); return; //Failsafe to prevent water from being deleted
			}
		}
		if (control[y2][x2] == control[y3][x3]) {
			System.out.println("You cannot attack your own pieces.");
			return; //prevents players from attacking their own pieces, in case we forgot to handle this earlier
		}
		final short attackingSquare = board[y2][x2];
		final short defendingSquare = board[y3][x3];
		final String attacker = getSquarePieceAsString(y2, x2);
		final String defender = getSquarePieceAsString(y3, x3);
		switch(attackResult(board[y2][x2], board[y3][x3])) { //determine the result of the source square attacking the destination square
			case ATTACK_DEFEAT:
				System.out.printf("The attacking \"%s\" was defeated by the defending \"%s\".\n",attacker,defender);
				playSoundEffect(defendingSquare);
				clearSquare(y2, x2); break; //attacking piece is destroyed. defending piece remains where it is.
			case ATTACK_DRAW:
				System.out.printf("The two \"%s\" pieces defeated each other in battle.\n",attacker);
				playSoundEffect(attackingSquare);
				clearSquare(y2, x2); clearSquare(y3, x3); break; //both pieces are destroyed.
			case ATTACK_VICTORY: //mouse is before the board
				playSoundEffect(attackingSquare);
				System.out.printf("The attacking \"%s\" defeated the defending \"%s\".\n",attacker,defender);
				movePiece(x2,y2,x3,y3); break; //This requires slightly more complex logic than before, but it's still pretty basic stuff.
		}
		checkIfGameIsOver();
	}

	/**
	 * This method moves a piece from the source square to the destination square, OVERWRITING the old destination square.
	 * IMPORTANT: It does NOT check if an attacking piece would be the victor, etc. Use doAttack() for that.
	 * The ONLY real logic in this method is the code for an army piece stepping on a bomb and a frogman on water.
	 * @param x2 Source piece's column
	 * @param y2 Source piece's row
	 * @param x3 Destination piece's column
	 * @param y3 Destination piece's row
	 * @author Dylan Taylor
	 */
	public static void movePiece(final int x2, final int y2, final int x3, final int y3) {
		if (!isValidMove(x2, y2, x3, y3)) { //Prevents illegal moves
			System.out.println("Illegal move requested"); return;
		} else {
			System.out.println("This move is valid.");
		}
		//Special cases
		if (board[y2][x2] == FROGMAN && board[y3][x3] == WATER) {
			System.out.println("The frogman is now on water.");
			board[y3][x3] = FROGMAN_ON_WATER; //set to frogman on water instead of just being a frogman
			control[y3][x3] = control[y2][x2]; //transfer control
			clearSquare(y2, x2); //clear the original square of the frogman
			return; //prevent code below from interfering with our logic
		} else if (board[y2][x2] == FROGMAN && board[y3][x3] == FROGMAN_ON_WATER) { //frogman attacking frogman on water... rare but possible
			clearSquare(y2, x2); //delete attacking frogman
			board[y3][x3] = WATER; //reset the destination frogman on water to just water
			control[y3][x3] = NEUTRAL; //return control to nobody
			return; //prevent later code from interfering
		} else if (isArmyUnit(y2, x2) && board[y3][x3] == BOMB) { //if the source is an army unit, and the destination is a bomb
			System.out.println("The destination square contained a BOMB!");
			if (board[y2][x2] != MINER) { //if the army unit is not a miner, destroy them _and_ the bomb, since it exploded
				clearSquare(y2, x2);				
				new BasicAudioPlayer(Resources.bombSoundEffect,0).start(); //play the bomb sound effect
				if (useExplosions) {
					System.out.println("Setting square to explosion");
					control[y3][x3] = NEUTRAL;
					board[y3][x3] = EXPLOSION;
				}
				System.out.printf("The bomb killed your %s. There is no longer an active bomb on that square.\n",getSquarePieceAsString(y2, x2));
				return; //so that control of the square doesn't get messed up.
			} else { //otherwise, continue moving into the square as usual. The bomb will be overwritten later in this method
				new BasicAudioPlayer(Resources.pickaxeSoundEffect,2).start(); //play the pickaxe sound effect, 3 times.
				System.out.println("The miner just removed the bomb at destination square.");
			}
		} else if ((board[y3][x3] == FROGMAN_ON_WATER || board[y3][x3] == FROGMAN_ON_WATER) && board[y2][x2] != FROGMAN) {
			System.out.println("Only the \"Frogman\" can move onto water."); return; //Failsafe to prevent water from being deleted
		}
		//Handles victory fanfare sound in case a flag is captured
		if (board[y3][x3] == VICTORY_FANFARE) {
			playSoundEffect(VICTORY_FANFARE);
		} 
		control[y3][x3] = control[y2][x2]; //the destination square is now under the owner of the source square's control
		if (board[y2][x2] != FROGMAN_ON_WATER) { //because we don't want to delete water
			board[y3][x3] = board[y2][x2]; //move the piece into the destination square
			clearSquare(y2, x2); //clear the source square, resetting it to empty.
		} else {
			System.out.println("The frogman is no longer in the water");
			board[y3][x3] = FROGMAN; //the frogman is no longer on water
			board[y2][x2] = WATER; //make sure that the water is still there
			control[y2][x2] = NEUTRAL; //water needs to be owned by nobody
		}
		checkIfGameIsOver();
	}

	/**
	 * Checks whether the game is over, by determining if both teams still have any army units left,
	 * as well as whether or not both teams still have their flags, or if they were captured.
	 * @author Dylan Taylor
	 */
	public static void checkIfGameIsOver() {
		if (!gameStarted) {
			return;
		}
		boolean redHasFlag = false;
		boolean redHasArmy = false;
		boolean orangeHasFlag = false;
		boolean orangeHasArmy = false;
		//go through all the pieces on the board, checking whether each team has army units and a flag left.
		for (short row = 0; row < rows; row++) { // for every row on the board
			for (short piece = 0; piece < board[row].length; piece++) { //for every piece in the row
				if (isArmyUnit(board[row][piece])) { //if the piece is an army unit
					switch (control[row][piece]) { //determine who owns the army unit
						case REDARMY: redHasArmy = true; continue;
						case ORANGEARMY: orangeHasArmy = true; continue;
					}
				} else if (board[row][piece] == FLAG) {
					switch (control[row][piece]) { //determine who owns the flag
						case REDARMY: redHasFlag = true; continue;
						case ORANGEARMY: orangeHasFlag = true; continue;
					}
				}
			}
		}
		if (redHasArmy && orangeHasArmy && redHasFlag && orangeHasFlag) {
			return; //the game must go on!
		} else { //one or both of the armies lost the game. The game is now over.
			gameOver = true;
		}
		final boolean redArmyLost = !redHasArmy || !redHasFlag; //determines if the red army lost
		final boolean orangeArmyLost = !orangeHasArmy || !orangeHasFlag; //determines if the orange army lost
		final boolean bothArmiesLost = (redArmyLost && orangeArmyLost);
		if (redArmyLost && !orangeArmyLost) {
			gameResult = ORANGEARMY;
			System.out.println("The orange army is victorious!");
		} else if (orangeArmyLost && !redArmyLost) {
			gameResult = REDARMY;
			System.out.println("The red army is victorious!");
		} else { //BOTH armys are defeated. This is rare, and should only occur if the last units of both teams are the same type and attack each other.
			gameResult = 0;
			System.out.println("Both armys are defeated.");
		}
		if (!bothArmiesLost) {
			playSoundEffect(VICTORY_FANFARE);
		} else {
			playSoundEffect(DEFEAT_TROMBONE_SOUND);	
		}		
		displayEndGameAlert();
	}

	/**
	 * Checks whether the specified square contains an army unit. This is a convenience method.
	 * @param y2 The column of the square (x).
	 * @param x2 The row of the square (y).
	 * @return whether or not the piece is an army unit
	 * @author Dylan Taylor
	 */
	public static boolean isArmyUnit(final int y2, final int x2) {
		return isArmyUnit(board[y2][x2]);
	}

	/**
	 * Checks whether the specified piece type is an army unit. This is a convenience method.
	 * @param piece_type The constant integer ID of the piece
	 * @return whether or not the piece is an army unit
	 */
	public static boolean isArmyUnit(final short piece_type) {
		return withinRange(piece_type, ARMYUNITS) || piece_type == FROGMAN_ON_WATER;
	}

	/**
	 * Sets the type of the object on the specified square. Parameters:
	 * @param attacker_type The type of the army attacking. Use the constant integers for the army type.
	 * @param y The type of army being attacked
	 * @returns ATTACK_VICTORY if attacker defeats attacked, ATTACK_DEFEAT if attacker defeated, ATTACK_DRAW if both die
	 * @author Dylan Taylor
	 */
	public static short attackResult(final short attacker, final short attacked) {
		return attacker == attacked ? ATTACK_DRAW : attacker < attacked ? ATTACK_VICTORY : ATTACK_DEFEAT;
	}

	public static void displayFakeEndGameAlert() {
		final JFrame fakeFrame = new JFrame();
		fakeFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		fakeFrame.setResizable(false);
		String fakeGameMessage = "Enemy flag captured... wait a minute, this one is a FAKE!";
		JOptionPane.showMessageDialog(fakeFrame, fakeGameMessage);
	}

	/**
	 * Switches the currently active player, hiding all of the other player's pieces, and making the next player's pieces
	 * visible. This method creates a dialog box saying it's the next player's turn, and doesn't show the new pieces on the screen
	 * until the "Ok" button is clicked. This helps to prevent cheating.
	 * @author Dylan Taylor, Justin Gompers
	 */
	public static void switchPlayer() {
		hideAllExceptTerrain = true; //this variable is used to make all the pieces disappear (with the exception of exploded tiles).
		mFrame.repaint(); //redraw the board to make all the pieces disappear.		
		currentPlayer = currentPlayer == ORANGEARMY ? REDARMY : ORANGEARMY; //actually switches the current player
		if (gameStarted) {
			displayNextPlayerAlert(); //displays a dialog box saying that their turn is over.
			//this is used to clear exploded tiles after the next player alert.
			for (short r = 0; r < rows; r++) {
				for (short square = 0; square < board[r].length; square++) {
					if (board[r][square] == EXPLOSION) {
						System.out.println("Clearing explosion square");
						clearSquare(r, square);
					}
				}
			}
		} else {
			new BasicAudioPlayer(Resources.coinFlipSoundEffect, 0).start(); //play a coin flipping sound
		}
		hideAllExceptTerrain = false; //we no longer want to hide all of the pieces
		if (enableFlip) flipTheBoard();
		mFrame.repaint();
	}

	/**
	 * Gets the current army's color as a string.
	 * @param opponent returns the opposite army's color
	 * @param capital Whether the first letter should be capitalized or not
	 * @author Dylan Taylor
	 */
	public static String getArmyColor(final boolean opponent, final boolean capital) {
		final String army = currentPlayer == 9015 && !opponent ? "Orange" : "Red";
		return capital ? army : army.toLowerCase();
	}

	/**
	 * Displays an alert dialog box saying that it is now the other army's team.
	 * @author Dylan Taylor
	 */
	public static void displayNextPlayerAlert() {
		final JFrame dialogFrame = new JFrame();
		dialogFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dialogFrame.setResizable(false); //prevents resizing of the dialog box
		if (gameStarted && !gameOver) {
			JOptionPane.showMessageDialog(dialogFrame, ("It is now the " + getArmyColor(false,false) + " army's turn.\n"),
					new String(getArmyColor(true,true) + " Army's Turn Ended"), JOptionPane.PLAIN_MESSAGE);
		} else if (!gameOver) { //still setting up the game
			JOptionPane.showMessageDialog(dialogFrame, ("It is now the " + getArmyColor(false,false) + " army's turn to place their pieces.\n"
					+ "Your pieces go in the " + (currentPlayer == REDARMY ? "top" : "bottom") + " four rows of the board."),
					new String(getArmyColor(false,true) + " Army, Place Your Pieces!"), JOptionPane.PLAIN_MESSAGE);
		}
		dialogFrame.setLocationRelativeTo(mFrame); //centers the dialog box on the main game window.
	}

	/**
	 * Displays an alert dialog box saying which team will go first.
	 * @author Dylan Taylor
	 */
	public static void displayFirstPlayerAlert() {
		final JFrame dialogFrame = new JFrame();
		dialogFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dialogFrame.setResizable(false); //prevents resizing of the dialog box
		if (gameStarted) {
			JOptionPane.showMessageDialog(dialogFrame, ("It is now the " + getArmyColor(false,false) + " army's turn."),
					new String(getArmyColor(false,true) + " Army Will Go First"), JOptionPane.PLAIN_MESSAGE);
		}
		dialogFrame.setLocationRelativeTo(mFrame); //centers the dialog box on the main game window.
	}

	/**
	 * Displays a dialog box with the rules in it. 
	 * @author Dylan Taylor
	 */
	public static void showRules() {
		System.out.println("Displaying game rules...");
		ruleFrame = new JFrame();
		ruleContainer = new RulesPanel();
		ruleFrame.add(ruleContainer);		
		ruleFrame.pack();
		ruleFrame.setSize(rulesWidth, rulesHeight);                
		ruleFrame.setTitle("Rules of The Game");
		ruleFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ruleFrame.setResizable(false);
		ruleFrame.setLocationRelativeTo(mFrame);
		ruleFrame.setVisible(true);
	}

	/**
	 * Displays an alert when the game is finished showing which army is victorious.
	 * @author Justin Gompers, Dylan Taylor
	 */
	public static void displayEndGameAlert() {
		final JFrame endFrame = new JFrame();
		endFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		endFrame.setResizable(false);
		String endGameMessage = "";
		if (gameResult != 0) {
			final StringBuilder sb = new StringBuilder("The ");
			sb.append(gameResult == ORANGEARMY ? "Orange" : "Red");
			sb.append(" Army Is Victorious!");
			endGameMessage = sb.toString();
			playSoundEffect(VICTORY_FANFARE); //plays the victory sound effect
		} else {
			endGameMessage = "Neither army won. Both shall walk home in defeat.";
			playSoundEffect(DEFEAT_TROMBONE_SOUND); //sound effect here for if neither army was victorious
		}
		endGameMessage += " Would you like to play again?";
		if (JOptionPane.showConfirmDialog(endFrame, endGameMessage, "Game Over, Select An Option", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {			//To ensure 'play again' works, some redundant code is necessary
			gameStarted = false; gameOver = false; select = false; hover = false; gameResult = 0; 			//this is valid code, believe it or not... resets selections to an impossible point
			selected = lastSelected = mouseSquare = boardSquare = impossiblePoint; 			piecePlacementNumber = 0; numberOfPiece = 0; redPiecesPlaced = false; orangePiecesPlaced = false;
			currentPlayer = (short) (gen.nextInt(2) + 9015);
			clearBoard();
			scatterTerrain();			mFrame.repaint();			currentPiecePlacing = piecesToPlace[0]; //reset current piece being placed
			displayNextPlayerAlert(); //Lets first player know to place their pieces		}
	}

	/**
	 * Returns a random integer within the range.
	 * @author Dylan Taylor
	 */
	public static short rand(final int i, final int j) {
		short rnum = 0;
		if (j > i) {
			//number from i to j, i being the minimum value.
			rnum = (short) (gen.nextInt((j - i)) + i);
			//System.out.printf("DEBUG: Generating random number between %d and %d: %d\n", i,j,rnum);			
		} else if (j < i) {
			//number from j to i, j being the minimum value.
			rnum = (short) (gen.nextInt((i - j)) + j);
			//System.out.printf("DEBUG: Generating __ random number between %d and %d: %d\n", j,i,rnum);
		} else {
			//System.out.printf("DEBUG: Both start and end values of random range are %d. Returning.\n", j);
			rnum = (short) j; //both values are the same. return the value
		}
		return rnum;
	}

	/**
	 * Determines if an integer is within a range of numbers.
	 * @param y2 The value to check if it's in the range of numbers
	 * @param s An integer array with 2 integers with the starting and ending range values.
	 * @returns false if the range isn't the right length.
	 * @author Dylan Taylor
	 */
	public static boolean withinRange(final int y2, final short[] s) { // checks if a number is within a range of numbers
		if (s.length != 2) {
			return false;
		}
		if (s[0] == s[1]) {
			return y2 == s[0];
		} else if (s[0] < s[1]) { // first value is less than second value
			return y2 >= s[0] && y2 <= s[1]; //min = range[0];    max = range[1];
		} else { //range[1] < range[0]
			return y2 >= s[1] && y2 <= s[0]; //min = range[1];    max = range[0];
		}
	}

	/**
	 * Scatters the specified quantity specified of type of game piece randomly
	 * @param pieceType The type of piece to scatter
	 * @param numPieces The number of pieces to place on the board
	 * @author Dylan Taylor
	 */
	public static void scatter(final short pieceType, final short numPieces) {
		/* old code -- worked, but wasn't correct according to the rules.
		for (short m = 0; m < numPieces; m++) {
			final short x = rand((rows - 1), 0);
			final short y = rand(0, (columns - 1));
			if (board[x][y] == EMPTY) {
				board[x][y] = pieceType;
				control[x][y] = NEUTRAL;
			} else {
				--m;
			} continue;
		}*/
		for (short top = 0; top < numPieces; top++) {
			final short x = rand((columns - 1), 0); 
			final short y = rand(0, 3); //first 4 -- 0 through 3
			//System.out.printf("X: %d, Y: %d\n",x,y);
			if (board[y][x] == EMPTY) {
				System.out.printf("Placing %s at (%d,%d)\n", getSpecifiedPieceAsString(pieceType),x,y);			
				board[y][x] = pieceType;
				control[y][x] = NEUTRAL;
			} else {
				--top;
			} continue;
		}
		for (short bottom = 0; bottom < numPieces; bottom++) {
			final short x = rand((columns - 1), 0); 			
			final short y = rand(5, (rows - 1)); //last 4 -- 5 through 8
			//System.out.printf("X: %d, Y: %d\n",x,y);
			if (board[y][x] == EMPTY) {
				System.out.printf("Placing %s at (%d,%d)\n", getSpecifiedPieceAsString(pieceType),x,y);																					   
				board[y][x] = pieceType;
				control[y][x] = NEUTRAL;
			} else {
				--bottom;
			} continue;
		}
	}

	/**
	 * Returns the name of the piece as a string
	 * @author Manny Castillo, Dylan Taylor
	 */
	public static String getSpecifiedPieceAsString(final short pieceType) {
		switch(pieceType) {
			case EMPTY: return "Empty Square";
			case FLAG: return "Flag";
			case FAKEFLAG:return "Fake Flag";
			case CANNON: return "Cannon";
			case WATER: return "Water";
			case MOUNTAIN: return "Mountain";
			case BOMB: return "Bomb";
			case EXPLOSION: return "Bomb (Exploded)";
			case GENERAL: return "General";
			case TANK: return "Tank";
			case MACHINEGUN: return "Machine Gun";
			case INFANTRY: return "Infantry";
			case MINER: return "Miner";
			case FROGMAN: return "Frogman";
			case FROGMAN_ON_WATER: return "Frogman On Water";
			default: return "Invalid";
		}
	}

	/**
	 * Gets the name of the piece at the specified location as a string
	 * @author Dylan Taylor
	 */
	public static String getSquarePieceAsString(final int y2, final int x2) {
		return getSpecifiedPieceAsString(board[y2][x2]);
	}

	/**
	 * Sets the type of the object on the specified square.
	 * @param x The column of the field. Ranges from 0 to 9.
	 * @param y The row of the field. Ranges from 0 to 8
	 * @param type The type of object on the square. Must be one of the predefined constants.
	 * @author Dylan Taylor
	 */
	protected static void setSquare(final int y2, final int x2, final short type) {
		board[y2][x2] = type;
	}

	/**
	 * Removes the piece on the specified square, setting it to empty. This is a convenience method.
	 * @param x The column of the field. Ranges from 0 to 9.
	 * @param y The row of the field. Ranges from 0 to 8
	 * @author Dylan Taylor
	 */
	protected static void clearSquare(final int y2, final int x2) {
		setSquare(y2,x2,EMPTY); //delete whatever piece is in the square, resetting it to empty
		control[y2][x2] = NEUTRAL; //since the square is empty, nobody owns it. return control to neutral
	}

	/**
	 * Sets the type of the object on the specified square. Parameters:
	 *
	 * @param x The column of the field. Ranges from 0 to 9.
	 * @param y The row of the field. Ranges from 0 to 8
	 * @param type The owner of the object on the square. Must be one of the predefined constants.
	 * @author Dylan Taylor
	 */
	protected static void setControl(final short col, final short row, final short owner) {
		control[col][row] = owner;
	}

	/**
	 * This method is used to determine whether a piece in the origin square is allowed to move to the destination square.
	 * @param y2 The row number of the origin square
	 * @param x2 The column number of the origin square
	 * @param y3 The row number of the destination square
	 * @param x3 The column number of the destination square
	 * @return Whether or not the move is allowed
	 * @author Dylan Taylor
	 */
	protected static boolean isValidMove(final int x2, final int y2, final int x3, final int y3) {
		// There is no need to check if the destination is within the board, as it's not possible to select outside of the board area.
		// First, we need to make sure that they are only moving by one square, and not horizontally.
		final int delta = Math.abs(y2 - y3) + Math.abs(x2 - x3); //absolute difference in board location
		//System.out.printf("Moving by %d absolute squares.\n", delta);
		if (delta != 1) {
			//System.out.println("Moving by something other than 1 absolute square");
			return false; // would be equal to 2 if horizontal, and not equal to 1 if moving by anything but 1 square
		}
		//Next, let's check if the piece is allowed to move. Only army pieces can move. Therefore, we need to check if the piece is an army piece.
		if (!isArmyUnit(board[y2][x2])) {
			System.out.println("The original piece is NOT an army unit.");
			return false; //If the origin piece isn't an 'army' unit, return false.
		}
		switch (board[y3][x3]) { // Now we have to handle special cases, such as the Frogman.
			case MOUNTAIN: return false;
			case WATER:  //if the board's destination square is water
				return board[y2][x2] == FROGMAN; //return whether or not the piece being moved is the Frogman.
			default:
				if (control[y3][x3] == currentPlayer) { //we can't move our army onto another piece from our own army
					System.out.println("Moving onto your own army piece is NOT allowed.");
					return false;
				}
				return true;
		}
	}

	//TODO: Write all RulesPanel code
	/**
	 * Rules Panel -- shows all objects and rules
	 * @author Dylan Taylor
	 */
	protected static class RulesPanel extends JPanel {

		final String gamePieces = "\u300A List of Game Pieces \u300B";
		final String gameRules = "\u300A Rules of the Game \u300B";
		final String armyPieces = "Army Pieces, by Precedence";
		final String pieceFooter = "\u300CPress SPACE to view the rules, or ESCAPE to close this window.\u300D";
		final String ruleFooter = "\u300CPress SPACE to view the pieces, or ESCAPE to close this window.\u300D";
		final String objectsLabel = "Objects";
		final String terrainLabel = "Terrain";
		final short army[] = new short[]{GENERAL,TANK,CANNON,MACHINEGUN,INFANTRY,MINER,FROGMAN};
		final short objects[] = new short[]{BOMB,FLAG,FAKEFLAG};
		final short terrain[] = new short[]{WATER,MOUNTAIN};
		final short xpad = 10;
		final short ypad = 5;   
		short yloc;
		short xloc;
		final boolean PIECES = true;
		final boolean RULES = false;
		boolean mode = PIECES;
		final String tab = "\\\\TAB\\\\";
		final String[] rules = new String[]{
				"\u25C6 Both teams place their pieces in the top 4 or bottom 4 rows.",
				"\u25C6 An army is defeated when they have no army pieces,",
				(tab + "or their flag is captured by the enemy's army."), //tab is replaced later...
				"\u25C6 If both armies are defeated, the game is considered a draw",
				"\u25C6 A piece can only move by one space in any given direction.",
				"\u25C6 Pieces can not be moved diagonally.",                       
				"\u25C6 Players can not move terrain and objects once they are placed.",
				"\u25C6 The outcome of a battle is determined by the piece\'s precedence.",
				"\u25C6 The piece with the higher precedence is always victorious.",
				"\u25C6 If two pieces with the same precedence fight, both are defeated.",
				"\u25C6 It is not possible for any piece to cross over mountains.",
				"\u25C6 Only the \"Frogman\" piece is capable of swimming in water.",
				"\u25C6 Only another \"Frogman\" can attack a \"Frogman\" in water.",
				"\u25C6 The \"Miner\" piece is capable of removing bombs.",
				"\u25C6 If any other piece steps on a bomb, both pieces are destroyed."
		};            
		protected RulesPanel() {
			super();
			setBackground(backgroundColor);		
			setSize(rulesWidth,rulesHeight);
			Main.ruleFrame.addKeyListener(new KeyListener(){

				public void keyTyped(KeyEvent e) {
					return; //unused
				}

				public void keyPressed(KeyEvent e) {
					return; //unused
				}

				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						Main.ruleFrame.dispose();
						Main.ruleContainer.setVisible(false);
					} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						mode = !mode;
						Main.ruleFrame.repaint();
					}
				}});
		}

		@Override
		protected void paintComponent(final Graphics g) {                    
			super.paintComponent(g); // Clears the buffer
			final Graphics2D g2 = (Graphics2D) g;
			final FontMetrics fm = g2.getFontMetrics(); //used to measure the actual size of text
			final short pieceTitleWidth = (short) fm.getStringBounds(gamePieces, g2).getWidth();
			final short ruleTitleWidth = (short) fm.getStringBounds(gameRules, g2).getWidth();
			final short pieceFooterWidth = (short)  fm.getStringBounds(pieceFooter, g2).getWidth();
			final short ruleFooterWidth = (short) fm.getStringBounds(ruleFooter, g2).getWidth();
			final short pieceColumnWidth = (short) ((3.5 * xpad) + (2 * tile_size) + fm.getStringBounds(armyPieces, g2).getWidth() +
					fm.getStringBounds(getSpecifiedPieceAsString(FAKEFLAG), g2).getWidth());
			final short centerPieceXPadding = (short) ((rulesWidth - pieceColumnWidth) / 2);
			// The following lines smooth the edges of everything drawn using anti-aliasing and bilinear interpolation.
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			yloc = 20;
			xloc = xpad + 5;
			g2.setPaint(limeGreen);        
			g2.drawString(((mode) ? gamePieces : gameRules), 
					((rulesWidth - ((mode) ? pieceTitleWidth : ruleTitleWidth)) / 2) , yloc); 
			if (mode) { //if we are displaying the pieces
				String pieceName;
				Rectangle2D bounds;                                                  
				yloc = 45;                        
				g2.setPaint(banannaYellow);
				xloc = centerPieceXPadding;
				g2.drawString(armyPieces, xloc, yloc);
				yloc += fm.getStringBounds(armyPieces, g2).getHeight() + 2; 
				//final short armyPieceXStart = (short) (centerPieceXPadding - tile_size - xpad); //wrong
				for (short piece : army) {
					xloc = centerPieceXPadding;
					//fill square background
					g2.setPaint(GamePanel.getSquareTypePaint(piece));
					g2.fillRect(xloc, yloc, tile_size, tile_size);
					//draw square image
					g2.drawImage(GamePanel.getSquareImage(piece), null, (xloc + (tile_size - GamePanel.getSquareImage(piece).getWidth()) / 2), (yloc + (tile_size - GamePanel.getSquareImage(piece).getHeight()) / 2));
					//outline the square
					g2.setPaint(squareOutlineColor);
					g2.drawRect(xloc, yloc, tile_size, tile_size);
					//label the piece
					g2.setPaint(cherryRed);
					pieceName = getSpecifiedPieceAsString(piece) + " [" + Integer.toString((9013 - piece)) + "]";
					bounds = fm.getStringBounds(pieceName, g2);
					yloc += (tile_size);
					xloc += (tile_size + xpad);
					g2.setPaint(cherryRed);                            
					g2.drawString(pieceName, xloc, yloc);
					yloc += (ypad + bounds.getHeight());                            
				}
				short obj_xloc = (short) (xloc + fm.getStringBounds(armyPieces, g2).getWidth() + xpad); 
				yloc = 45;
				g2.setPaint(banannaYellow);
				g2.drawString(objectsLabel, obj_xloc, yloc);
				yloc += fm.getStringBounds(objectsLabel, g2).getHeight() + 2; 
				for (short piece : objects) {
					xloc = obj_xloc;
					//fill square background
					if (piece != FLAG) {
						g2.setPaint(GamePanel.getSquareTypePaint(piece));
					} else {
						g2.setPaint((currentPlayer == ORANGEARMY) ? orangeArmyFlagBackgroundColor : redArmyFlagBackgroundColor);
					}
					g2.fillRect(xloc, yloc, tile_size, tile_size);
					//draw square image
					if (piece != FLAG) {
						g2.drawImage(GamePanel.getSquareImage(piece), null, (xloc + (tile_size - GamePanel.getSquareImage(piece).getWidth()) / 2), (yloc + (tile_size - GamePanel.getSquareImage(piece).getHeight()) / 2));    
					} else {
						BufferedImage flagImage = (currentPlayer == ORANGEARMY) ? Resources.orangeFlag : Resources.redFlag;
						g2.drawImage(flagImage, null, (xloc + (tile_size - flagImage.getWidth()) / 2), (yloc + (tile_size - flagImage.getHeight()) / 2));
					}                            
					//outline the square
					g2.setPaint(squareOutlineColor);
					g2.drawRect(xloc, yloc, tile_size, tile_size);
					//label the piece
					g2.setPaint(cherryRed);
					pieceName = getSpecifiedPieceAsString(piece);
					bounds = fm.getStringBounds(pieceName, g2);
					yloc += (tile_size);
					xloc += (tile_size + xpad);
					g2.setPaint(cherryRed);                            
					g2.drawString(pieceName, xloc, yloc);
					yloc += (ypad + bounds.getHeight());          
				}
				yloc += (3 * ypad); //dividing space between objects and terrain
				g2.setPaint(banannaYellow);
				g2.drawString(terrainLabel, obj_xloc, yloc);
				yloc += fm.getStringBounds(terrainLabel, g2).getHeight() + 2; 
				for (short piece : terrain) {
					xloc = obj_xloc;
					//fill square background
					g2.setPaint(GamePanel.getSquareTypePaint(piece));
					g2.fillRect(xloc, yloc, tile_size, tile_size);
					//draw square image
					g2.drawImage(GamePanel.getSquareImage(piece), null, (xloc + (tile_size - GamePanel.getSquareImage(piece).getWidth()) / 2), (yloc + (tile_size - GamePanel.getSquareImage(piece).getHeight()) / 2));                      
					//outline the square
					g2.setPaint(squareOutlineColor);
					g2.drawRect(xloc, yloc, tile_size, tile_size);
					//label the piece
					g2.setPaint(cherryRed);
					pieceName = getSpecifiedPieceAsString(piece);
					bounds = fm.getStringBounds(pieceName, g2);
					yloc += (tile_size);
					xloc += (tile_size + xpad);
					g2.setPaint(cherryRed);                            
					g2.drawString(pieceName, xloc, yloc);
					yloc += (ypad + bounds.getHeight());
	
				}
			} else {
				yloc += 50;
				g2.setPaint(lightAmber);
				for (String rule : rules) {
					//g2.drawString(rule, ((rulesWidth - (int)fm.getStringBounds(rule, g2).getWidth()) / 2), yloc);
					if (!rule.contains(tab)) {
						g2.drawString(rule, 45, yloc);
					} else {
						g2.drawString(rule.substring(tab.length()), 85, yloc);
					}					
					yloc += fm.getStringBounds(rule, g2).getHeight() + 15; 
				}                            
			}
			g2.setPaint(lightBlue);
			g2.drawString(((mode) ? pieceFooter : ruleFooter), 
					((rulesWidth - ((mode) ? pieceFooterWidth : ruleFooterWidth)) / 2) , rulesHeight - 40); 
		}
	}

	protected static class GamePanel extends JPanel {
		private short xloc;
		private short yloc;
		private Point clickPoint;
		private Point mousePoint;

		protected GamePanel() {
			super();
			setBackground(backgroundColor);
			setPreferredSize(new Dimension(Main.x, Main.y));
			addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseMoved(final MouseEvent e) { 
					mousePoint = e.getPoint();
					final short mouse_col = (short) ((mousePoint.x - xpadding) / tile_size);
					final short mouse_row = (short) ((mousePoint.y - ypadding) / tile_size);
					if (mouseSquare.x != mouse_col || mouseSquare.y != mouse_row) { //if the new mouse square is not the same as the old one
						if (mousePoint.x <= xpadding || mousePoint.y <= ypadding ||
								mouse_col >= columns || mouse_row >= rows || mouse_col < 0 || mouse_row < 0) { //mouse is before the board
							mouseSquare = new Point(-1,-1);
						} else {
							mouseSquare = new Point(mouse_col,mouse_row);
						}
						mouseSquareChanged();
					}
				}

				@Override
				public void mouseDragged(final MouseEvent arg0) {
					return; //unused
				}
			});
			addMouseListener(new MouseAdapter() {
				/*
				 * mousePressed is used instead of mouseClicked because in order for mouseClicked to register a click, the mouse button must be
				 * pressed and released in approximately the same location, and the click is not registered until the mouse is released. Using
				 * mousePressed makes the interface 'feel' _significantly_ more responsive.
				 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
				 * @author Dylan Taylor
				 */
				public void mousePressed(final MouseEvent e) {
					clickPoint = e.getPoint();
					// the following line ignores all clicks in the area before the board area
					if (clickPoint.x <= xpadding || clickPoint.y <= ypadding) return;
					final short click_col = (short) ((clickPoint.x - xpadding) / tile_size);
					final short click_row = (short) ((clickPoint.y - ypadding) / tile_size);
					lastClicked = new Point(click_col, click_row);
					lastSelected = lastClicked;
					// the user clicked in the area after the actual board area. do nothing.
					if (click_col >= columns || click_row >= rows || click_col < 0 || click_row < 0) return;
					confirmSelection(true);  //determine whether setting up the game or in the game and go to the neccessary method
					repaint(); // redraw the board
				}
			});
			/**
			 * Used for board selection of squares
			 * @author Rhyan Smith, Dylan Taylor
			 */
			final KeyListener gameKeyListener = new KeyListener() {
				public void keyPressed(final KeyEvent e) {
					System.out.println(KeyEvent.getKeyText(e.getKeyCode()) + " pressed");
					// Note: WSAD cluster and keypad can be used as an alternative to the arrow keys
					switch (e.getKeyCode()) {
						case KeyEvent.VK_ENTER:
						case KeyEvent.VK_SPACE:
							hover = false;			
							lastSelected = boardSquare;
							confirmSelection(false);
							return;
						case KeyEvent.VK_F1:
							showRules();
							return;
						case KeyEvent.VK_UP:
						case KeyEvent.VK_W:
						case KeyEvent.VK_NUMPAD8:
							hover = true;
							moveBoardSelection(UP);
							return;
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_S:
						case KeyEvent.VK_NUMPAD2:
							hover = true;
							moveBoardSelection(DOWN);
							return;
						case KeyEvent.VK_LEFT:
						case KeyEvent.VK_A:
						case KeyEvent.VK_NUMPAD4:
							moveBoardSelection(LEFT);
							hover = true;
							return;
						case KeyEvent.VK_RIGHT:
						case KeyEvent.VK_D:
						case KeyEvent.VK_NUMPAD6:
							hover = true;
							moveBoardSelection(RIGHT);
							return;
						case KeyEvent.VK_ESCAPE:
							if (hover) {
								if (select)	System.out.println("Unselecting board selection.");
							} else {
								System.out.println("Nothing is selected with the board. Doing nothing.");
							}
							hover = false; //unselect board selection
							return;
						default:
							System.out.println("This  is not handled. No action performed. : " + KeyEvent.getKeyText(e.getKeyCode()));
							return;
					}
				}

				public void keyReleased(final KeyEvent e) {
					//System.out.println(KeyEvent.getKeyText(e.getKeyCode()) + " released");
					return; //we only want to use the pressed event.
				}

				public void keyTyped(final KeyEvent e) {
					return; // we don't use this method, just needs to be implemented.
				}
			};
			Main.mFrame.addKeyListener(gameKeyListener); // adds listener to the gamePanel
		}

		/**
		 * Moves the board selection when a board  is pressed.
		 * @author Rhyan Smith
		 */
		private static void moveBoardSelection(final short DIRECTION) {
			System.out.println("Moving board selection using keyboard...");
			if ((boardSquare.x == -1) && (boardSquare.y == -1)) {
				switch (DIRECTION) {
					case UP: boardSquare = new Point(0,8); return;
					default: boardSquare = new Point(0,0); return;
				}
			} else {
				switch (DIRECTION) { //boundaries are 0,0 to 9,8
					case UP:
						if (boardSquare.y > 0) boardSquare = new Point(boardSquare.x,(boardSquare.y - 1));
						break;
					case DOWN:
						if (boardSquare.y < 8) boardSquare = new Point(boardSquare.x,(boardSquare.y + 1));
						break;
					case LEFT:
						if (boardSquare.x > 0) boardSquare = new Point((boardSquare.x - 1),boardSquare.y);
						break;
					case RIGHT:
						if (boardSquare.x < 9) boardSquare = new Point((boardSquare.x + 1),boardSquare.y);
						break;
					default: System.out.println("Invalid board movement requested."); return;
				}
			}
			System.out.print("board selection:" + getSquareInformation(boardSquare));
			mFrame.repaint(); //redraw the board with the new board selection
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g); // Clears the buffer
			final Graphics2D g2 = (Graphics2D) g;
			final FontMetrics fm = g2.getFontMetrics(); //used to measure the actual size of text
			final Font regular = g2.getFont();
			final Font bold = new Font(regular.getFamily(),Font.BOLD, regular.getSize());
			final Color F1Color = new Color(255,231,158,rulesTextColor.getAlpha() + 65);
			// The following lines smooth the edges of everything drawn using anti-aliasing and bilinear interpolation.
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			// display text telling players how to view the rules
			g2.setPaint(rulesTextColor);
			g2.drawString("Press",xpadding, ypadding - 10);
			final int pressWidth = (int) fm.getStringBounds("Press", g).getWidth();
			g2.setFont(bold);
			g2.setPaint(F1Color);
			g2.drawString(" F1 ", xpadding + pressWidth, ypadding - 10);
			final int precedingWidth = (int) fm.getStringBounds(" F1 ", g2).getWidth() + pressWidth;
			g2.setFont(regular);
			g2.setPaint(rulesTextColor);
			g2.drawString("to view the rules of the game, and a list of all the pieces.", xpadding + precedingWidth, ypadding - 10);
			// display which army's turn it is on the screen, 9015 = orange, 9016 = red
			g2.setPaint(currentPlayer == ORANGEARMY ? orangeArmyTurnTextColor : redArmyTurnTextColor);
			g2.drawString(new String((currentPlayer == 9015 ? "Orange" : "Red") + " Army's Turn"), (xpadding + 5),
					(ypadding + rows * tile_size + 20));
			// displays what tile the mouse is hovering over if it isn't hidden and the mouse is on a valid square
			// also displays the action that will be performed upon a click of the square
			if (boardSquare.x != -1 && boardSquare.y != -1) {
				// this code displays the name of the piece the mouse is hovering over, and sets the color of the text
				final String piece = !isHidden(boardSquare.y, boardSquare.x) ? getSquarePieceAsString(boardSquare.y, boardSquare.x) :
					board[boardSquare.y][boardSquare.x] != FROGMAN_ON_WATER ?  "Unknown" : "Water"; //Makes 'frogman on water' say 'water'... stealthy :P
				//NOTE: If there is an opponent's "frogman on water", the square will, appear as water, and say "Water", but have text with the opponent's
				//color on it. This is INTENTIONAL, and is not a bug. :) This allows the frogman on water to hide from enemies.
				final short pieceControl = control[boardSquare.y][boardSquare.x];
				if (pieceControl != currentPlayer && pieceControl != NEUTRAL) { //the piece is controlled by the current player's opponent
					g2.setPaint(currentPlayer == REDARMY ? orangeArmyTurnTextColor : redArmyTurnTextColor); //set the text color to the opponents color
				} else if (pieceControl == NEUTRAL) {
					g2.setPaint(neutralPieceTextColor);
				}
				//getString bounds creates a rectangle2d with the proportions of the string of text in it.
				//the getWidth method returns a double, but an integer is required, so we type cast to truncate the decimals
				g2.drawString(piece, (x - xpadding - (int)fm.getStringBounds(piece, g2).getWidth()), (ypadding + rows * tile_size + 20));
				//Now to display the action relevant to the piece they are hovering over
				g2.setPaint(actionTextColor); //because we are displaying an action, we need to set the text color for the action
				final String actionString = getActionString(); //gets the string representation of the action that will be performed if the user clicks
				g2.drawString(actionString, (x/2 - (int)fm.getStringBounds(actionString, g2).getWidth()/2), (ypadding + rows * tile_size + 20));
			}
			xloc = xpadding;
			BufferedImage cImg; // current image
			for (short r = 0; r < columns; r++) { // r represents the row
				yloc = ypadding;
				for (short c = 0; c < rows; c++) { // c represents the column
					if (board[c][r] != FROGMAN_ON_WATER) {
						if (!isHidden(c,r)) {
							//System.out.println("Drawing regular square that's not hidden or frogman on water"); //was way too annoying in the console
							// Fill the square with the color of the item in it.
							g2.setPaint(getSquareTypePaint(c, r)); // Checks the fill color for the square type.
							g2.fillRect(xloc, yloc, tile_size, tile_size);
							cImg = getSquareImage(c, r);
							g2.drawImage(cImg, null, (xloc + (tile_size - cImg.getWidth()) / 2), (yloc + (tile_size - cImg.getHeight()) / 2));
						} else {
							// System.out.println("Regular square is hidden. Not drawing in it."); //commented out because we KNOW hiding works.
							// Fill the hidden square with the color for an occupied, hidden square.
							switch (control[c][r]) {
								case REDARMY:
									g2.setPaint(redArmyObscuredTileBackgroundColor);
									break;
								case ORANGEARMY:
									g2.setPaint(orangeArmyObscuredTileBackgroundColor);
									break;
								case NEUTRAL:
									g2.setPaint(getSquareTypePaint(EMPTY));
									break;
								default:
									g2.setPaint(Color.MAGENTA);
									break;
							}
							g2.fillRect(xloc, yloc, tile_size, tile_size);
						}
					} else {
						if (!isHidden(c,r)) {
							//System.out.println("Drawing frogman on water...");
							// Fill the square with the color for water.
							g2.setPaint(getSquareTypePaint(WATER)); // Forces square color to be that of water
							g2.fillRect(xloc, yloc, tile_size, tile_size); //Fill the square with the water color
							//draw the water image, then draw the frogman image on top of that
							cImg = getSquareImage(WATER);
							g2.drawImage(cImg, null, (xloc + (tile_size - cImg.getWidth()) / 2), (yloc + (tile_size - cImg.getHeight()) / 2));
							cImg = getSquareImage(FROGMAN);
							g2.drawImage(cImg, null, (xloc + (tile_size - cImg.getWidth()) / 2), (yloc + (tile_size - cImg.getHeight()) / 2));
						} else { //if the frogman on the water is hidden, draw regular water.
							g2.setPaint(getSquareTypePaint(WATER));
							g2.fillRect(xloc, yloc, tile_size, tile_size);
							cImg = getSquareImage(WATER);
							g2.drawImage(cImg, null, (xloc + (tile_size - cImg.getWidth()) / 2), (yloc + (tile_size - cImg.getHeight()) / 2));
						}
					}
					g2.setPaint(squareOutlineColor);
					g2.drawRect(xloc, yloc, tile_size, tile_size);
					yloc += tile_size;
				}
				xloc += tile_size;
			}
			// Once we finish drawing the board, we need to show the selected square.
			// This has to be done last so the outline of the selected square doesn't get overlapped.
			if (select) { // If a square is selected
				// Draw an alpha transparent layer above the selected square
				g2.setPaint(selectedSquareAlphaOverlay);
				g2.fillRect((xpadding + tile_size * selected.x), (ypadding + tile_size * selected.y), tile_size, tile_size);
			}
			if (hover) { // If a square is selected
				//can't have hover color on top of currently selected square's color
				//if ((selected.x != boardSquare.x) && (selected.y != boardSquare.y)) {
				// Draw an alpha transparent layer above the selected square
				g2.setPaint(hoverSquareAlphaOverlay);
				g2.fillRect((xpadding + tile_size * boardSquare.x), (ypadding + tile_size * boardSquare.y), tile_size, tile_size);
				// Outline the square with an opaque outline
				g2.setPaint(hoverOutlineColor);
				g2.drawRect((xpadding + tile_size * boardSquare.x), (ypadding + tile_size * boardSquare.y), tile_size, tile_size);
				//}
			}
			if (select) {
				// Outline the square with an opaque outline. This is after the selection code, so that the selected square's outline
				// is 'above' the shading of the square the user is hovering over, if applicable
				g2.setPaint(selectedOutlineColor);
				g2.drawRect((xpadding + tile_size * selected.x), (ypadding + tile_size * selected.y), tile_size, tile_size);
			}
		}

		/**
		 * This method gets the action string based on what square the mouse is hovering over.
		 * @return The action that will be performed if the user clicks on the square, as a string.
		 * @author Dylan Taylor
		 */
		public static String getActionString() {
			if (gameOver) return ""; //"The Game Is Over"; //because no actions can be performed if the game is over
			final Point sq = boardSquare; //in case the board square changes during the execution of this method, the square will remain consistent
			if (select) { //if we have a piece selected already... no need to check if the game started or not here.
				if (sq.x == selected.x && sq.y == selected.y) { // if the square is the selected square
					return "Clear Piece Selection";    // let them know that they will clear the selection by clicking here
				} else if (isValidMove(selected.x,selected.y,sq.x,sq.y)) { //if the move is allowed
					if (board[sq.y][sq.x] == EMPTY) { // if the square they're hovering over is empty
						return "Move Here";
					} else if ((board[sq.y][sq.x] == WATER || board[sq.y][sq.x] == FROGMAN_ON_WATER) && board[selected.y][selected.x] == FROGMAN) {
						return "Move/Attack Here"; // frogman moving onto water or attacking another frogman on the water
					} else { //another valid move...
						if (board[sq.y][sq.x] == WATER || board[sq.y][sq.x] == FROGMAN_ON_WATER) {
							if (board[selected.y][selected.x] == FROGMAN || board[selected.y][selected.x] == FROGMAN_ON_WATER) {
								return "Move/Attack Here";
							} else {
								return ""; //only frogman or a frogman on the water can attack or move to a water square
							}
						} else {
							return "Move/Attack Here";
						}
					}
				}
			} else { //we don't currently have a selection
				if (gameStarted) { //if the game is started already
					if (isArmyUnit(sq.y,sq.x) && control[sq.y][sq.x] == currentPlayer) { //hovering over an army unit the current player controls
						final StringBuilder sb = new StringBuilder("Select ");
						sb.append(getSquarePieceAsString(sq.y,sq.x));
						return sb.toString();
					} //if it's not an army unit, it can't be selected, so the default string will be returned.
				} else {
					if (board[sq.y][sq.x] == EMPTY) { //the square is empty
						if (withinRange(sq.y, (currentPlayer == REDARMY ? topHalfRange : bottomHalfRange))) { //if they are allowed to place a piece here
							final StringBuilder sb = new StringBuilder("Place ");
							sb.append(getSpecifiedPieceAsString(currentPiecePlacing));
							sb.append(" Here");
							if (!sb.toString().contains("Invalid")) return sb.toString();
						} else {							if (debugPiecePlacement) return "Not Within Range";						}
					} else {						if (debugPiecePlacement) return "Square Not Empty";					}
				}
			}
			return ""; //defaults to a blank string, if no action can be performed.
		}

		/**
		 * Gets the color that the specified square should be filled with
		 * @param sq_col The X coordinate of the square in the table. This ranges from 0 to 9.
		 * @param sq_row The Y coordinate of the square in the table. This ranges from 0 to 8.
		 * @return Returns the color of the square, or magenta, if the case for the color of the square is not handled properly
		 * @author Dylan Taylor
		 */
		private static Paint getSquareTypePaint(final short sq_col, final short sq_row) {
			if (isHidden(sq_col,sq_row)) { //special case for the Frogman while on water, otherwise returns empty square color
				return board[sq_col][sq_row] == FROGMAN_ON_WATER ? waterBackgroundColor : emptySquareBackgroundColor;
			} else if (board[sq_col][sq_row] == FLAG) { //special case for the flag, as each team has a different colored flag. Returns magenta if neutral team.
				return getArmyBackgroundColor(control[sq_col][sq_row]);
			} else {
				return getSquareTypePaint(board[sq_col][sq_row]); //gets the paint color based off of the type of square
			}
		}

		/**
		 * Gets the background color for the specified army
		 * @param army The army that controls the square
		 * @author Dylan Taylor
		 * @returns The color for the army, or magenta if there is a problem
		 */
		public static Color getArmyBackgroundColor(final short army) {
			return army != NEUTRAL ? (army == ORANGEARMY ? orangeArmyFlagBackgroundColor : redArmyFlagBackgroundColor) : Color.MAGENTA;
		}

		/**
		 * Gets the square fill color for the specified piece type
		 * @param piece_type The constant integer ID for the piece
		 * @return Returns the color of the square, or magenta, if the case for the color of the square is not handled properly
		 * @author Dylan Taylor
		 */
		private static Paint getSquareTypePaint(final short piece_type) {
			switch (piece_type) { // break statements are not necessary since we return immediately, and they would be unreachable code.
				default:         return Color.MAGENTA; // this color indicates a logical error.
				case EMPTY:      return emptySquareBackgroundColor;
				case FAKEFLAG:   return blankFlagBackgroundColor;
				case MOUNTAIN:   return mountainBackgroundColor;
				case BOMB: case EXPLOSION: return bombBackgroundColor;
				case GENERAL:    return generalBackgroundColor;
				case TANK:       return tankBackgroundColor;
				case CANNON:     return cannonBackgroundColor;
				case MACHINEGUN: return machineGunBackgroundColor;
				case INFANTRY:   return infantryBackgroundColor;
				case MINER:      return minerBackgroundColor;
				case FROGMAN:    return frogmanBackgroundColor;
				case WATER: case FROGMAN_ON_WATER: return waterBackgroundColor; //using a case statement this way is allowed, don't edit it
			}
		}

		/**
		 * Gets the image that should be drawn in the specified square. Returns blank image if square is hidden.
		 * @param sq_col The X coordinate of the square in the table. This ranges from 0 to 9.
		 * @param sq_row The Y coordinate of the square in the table. This ranges from 0 to 8.
		 * @return Returns the color of the square, or magenta, if the case for the color of the square is not handled properly
		 * @author Dylan Taylor
		 */
		private static BufferedImage getSquareImage(final short sq_col, final short sq_row) {
			//System.out.println("Getting image for square: " + sq_col + ", " + sq_row);
			if (isHidden(sq_col,sq_row)) { //special case for the Frogman while on water, otherwise returns empty square image
				return board[sq_col][sq_row] == FROGMAN_ON_WATER ? Resources.water : Resources.blankImage;
			} else if (board[sq_col][sq_row] == FLAG) { //special case for the flag, as each team has a different colored flag. Returns magenta if neutral team.
				return control[sq_col][sq_row] != NEUTRAL ? (control[sq_col][sq_row] == ORANGEARMY ? Resources.orangeFlag : Resources.redFlag) : Resources.blankFlag;
			} else {
				return getSquareImage(board[sq_col][sq_row]); //gets the paint color based off of the type of square
			}
		}

		/**
		 * Gets the image for the specified piece type. Returns blank image if invalid piece type
		 * @param piece_type The constant integer ID for the piece
		 * @return Returns the color of the square, or magenta, if the case for the color of the square is not handled properly
		 * @author Dylan Taylor
		 */
		private static BufferedImage getSquareImage(final short piece_type) {
			switch (piece_type) {
				case EMPTY:      return Resources.blankImage;
				case FAKEFLAG:   return Resources.blankFlag;
				case WATER:      return Resources.water;
				case MOUNTAIN:   return Resources.mountain;
				case BOMB:       return Resources.bomb;
				case GENERAL:    return Resources.general;
				case TANK:       return Resources.tank;
				case CANNON:     return Resources.cannon;
				case MACHINEGUN: return Resources.machineGun;
				case INFANTRY:   return Resources.infantry;
				case MINER:      return Resources.pickaxe;
				case FROGMAN:    return Resources.flippers;
				case EXPLOSION:  return Resources.explosion;
				default: // defaults to this if no square type is set
					System.out.println("No image found for specified square...");
					return Resources.blankImage;
			}
		}

		/**
		 * This method is run when a selection is confirmed, triggering the actual event associated with the square. This is automatic for the mouse,
		 * since clicking indicates the user wants to interact with the square, but for the board, this method isn't called until enter is pressed
		 * to actually select the square.
		 * @author Dylan Taylor, Rhyan Smith, Justin Gompers
		 */
		private void confirmSelection(final boolean mouse) {
			if (gameOver) { // Prevents anything from happening after the game is over
				System.out.println("The game is over...");
				displayEndGameAlert();
				return;
			}				
			if (gameStarted){
				if (mouse) {
					inGameMouseClick();
				} else {
					inGameKeyboardSelection();
				}
			} else {
				//System.out.println("Selection was confirmed, and the game is not yet started.");
				showClickedSquare(mouse);				placePiece(mouse);
			}
		}

		/**
		 * Called when the mouse is on a different square than it was on before
		 * @author Dylan Taylor
		 */
		private void mouseSquareChanged() {
			if (mouseSquare.x != -1 && mouseSquare.y != -1) { //if outside of the board, don't even bother printing anything
				if (!hover) hover = true; //force hovering to be turned on.
				boardSquare = mouseSquare; //sets the current square we are hovering over to the mouse's location
				if (mouseVerbose) {
					System.out.print("Mouse is now hovering over " + getSquareInformation(mouseSquare));
				}
			}
			mFrame.repaint();
		}
	}

	/**
	 * Plays a sound depending on the army piece
	 * @author Dylan Taylor
	 */
	public static void playSoundEffect(final short piece) {
		System.out.print("Playing sound effect: ");
		switch (piece) {
			case POPPING:
				System.out.println("Pop");
				new BasicAudioPlayer(Resources.poppingSoundEffect, 0).start();
				return;
			case INFANTRY:
				new BasicAudioPlayer(Resources.assaultRifleSoundEffect, 1).start(); //assault rifle sound, twice
				System.out.println(getSpecifiedPieceAsString(piece));
				return;
			case MACHINEGUN:
				new BasicAudioPlayer(Resources.machineGunSoundEffect, 4).start(); //machine gun sound effect 5 times
				System.out.println(getSpecifiedPieceAsString(piece));
				return;
			case GENERAL:
				new BasicAudioPlayer(Resources.generalSoundEffect, 0).start();
				System.out.println(getSpecifiedPieceAsString(piece));;
				return;
			case TANK:
				new BasicAudioPlayer(Resources.tankSoundEffect, 0).start();
				System.out.println(getSpecifiedPieceAsString(piece));
				return;
			case MINER:
				new BasicAudioPlayer(Resources.pickaxeSoundEffect, 2).start();
				System.out.println(getSpecifiedPieceAsString(piece));
				return;
			case CANNON:
				new BasicAudioPlayer(Resources.cannonSoundEffect, 0).start();
				System.out.println(getSpecifiedPieceAsString(piece));
				return;
			case FROGMAN:
			case FROGMAN_ON_WATER:
				new BasicAudioPlayer(Resources.waterDripSoundEffect, 0).start();
				System.out.println(getSpecifiedPieceAsString(piece));
				return;
			case BOMB:
				new BasicAudioPlayer(Resources.bombSoundEffect, 0).start();
				System.out.println(getSpecifiedPieceAsString(piece));
				return;
			case VICTORY_FANFARE:
				System.out.println("Victory Fanfare");
				new BasicAudioPlayer(Resources.victoryFanfareSoundEffect, 0).start();
				return;
			case DEFEAT_TROMBONE_SOUND:
				System.out.println("Defeat Sound Effect"); //Played when BOTH teams lose the game
				new BasicAudioPlayer(Resources.victoryFanfareSoundEffect, 0).start();
				return;
			case INVALID_SELECTION_SOUND_EFFECT:
				System.out.println("Invalid Selection Sound Effect"); //Played when BOTH teams lose the game
				new BasicAudioPlayer(Resources.invalidSelectionSoundEffect, 0).start();
				return;
			case FAKEFLAG_SOUND:
				System.out.println("Fake Flag Sound Effect"); //Played when BOTH teams lose the game
				new BasicAudioPlayer(Resources.fakeFlagSoundEffect, 0).start();
				return;							
			default: //default to the coin flipping sound, as we already have a massive game as it is, there's no reason to add to much bloat
				System.out.println("Coin Flip");
				new BasicAudioPlayer(Resources.coinFlipSoundEffect, 0).start();
		}
	}

	/**
	 * A very basic audio playing class that runs in a separate thread
	 * @param inputStream The AudioInputStream to play
	 * @param loops The number of additional times to play the sound. 0 means the sound will only be played once.
	 * @author Dylan Taylor
	 */
	public static final class BasicAudioPlayer extends Thread {
		private final AudioInputStream audioStream; //source of the audio data
		private final int loopcount;

		public BasicAudioPlayer(final AudioInputStream inputStream, final int i) {
			audioStream = inputStream;
			loopcount = (i != -1) ? i : Clip.LOOP_CONTINUOUSLY;
		}

		public void run() {
			try {
				try {
					final Clip clip = AudioSystem.getClip();
					if (clip.isRunning()) { //if the clip is still running from last time
						clip.stop(); //stop the clip
						clip.close(); //close it, freeing up memory
					}
					clip.open(audioStream);
					if (loopcount != 0) clip.loop(loopcount);
					try {
						clip.start(); //start playing the audio clip
						clip.drain(); //waits until the sound buffer is played before closing the audio clip
					} finally {	
						//clip.stop();
						//clip.close(); //close the audio clip
						System.out.println("Sound buffer drained and closed. New audio clip should now be able to be played.");
					}
				} finally {
					audioStream.reset(); //reset the audio stream so that a new sound can be played
				}
			} catch (final Exception e) {
				System.out.println("Error playing audio clip: " + e);
				new BasicAudioPlayer(audioStream, loopcount);
				try {
					finalize();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	/**
	 * This class contains Base64-encoded resources used in the project. Do not even attempt to edit these by hand. It's virtually impossible. The
	 * reason to use encoded resources instead of an external file, is to have all of the resources in a single file, and reduce load time. The images
	 * are all encoded as highly compressed and indexed PNG files to save space. All source images are less than 1kb in size.
	 * @author Dylan Taylor, Justin Gompers
	 */
	public abstract static class Resources {
		final static char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray(); //used for decoding base64
		// These are the Base64 encoded resources. Decode converts them from strings of text back into their original byte arrays.
		private final static byte[] blankImageBytes = decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQMAAAAl21bKAAAAAXNSR0IArs4c6QAAAANQTFRFAAAAp3o92gAAAAF0Uk5TAEDm2GYAAAAKSURBVAjXY2AAAAACAAHiIbwzAAAAAElFTkSuQmCC"); // A blank, transparent 1 x 1 PNG image
		private final static byte[] redFlagBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACMAAAAxAgMAAAAtqfhaAAAAAXNSR0IArs4c6QAAAAxQTFRF0vUAAgEAXhAQ/SImUXj1FwAAAAF0Uk5TAEDm2GYAAABlSURBVBjTY2AgFojAWSFwViiCFQAiRYE4bKkDkJEKYv2bGhoa/QXE+v9v1er/T8AsEECwrsBZF0hmYTPvC5z1Fc76CXJfPoj1FMQKXf///8sQsD9Co6aGQH0kGoDmt6HIEsETfwCrhGoGkCa88AAAAABJRU5ErkJggg=="); //Red army flag
		private final static byte[] orangeFlagBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACMAAAAxAgMAAAAtqfhaAAAAAXNSR0IArs4c6QAAAAxQTFRFaXplBwMAZikA+WUA8Vy46QAAAAF0Uk5TAEDm2GYAAABlSURBVBjTY2AgFojAWSFwViiCFQAiRYE4bKkDkJEKYv2bGhoa/QXE+v9v1er/T8AsEECwrsBZF0hmYTPvC5z1Fc76CXJfPoj1FMQKXf///8sQsD9Co6aGQH0kGoDmt6HIEsETfwCrhGoGkCa88AAAAABJRU5ErkJggg=="); //orange army flag
		private final static byte[] blankFlagBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACMAAAAxAgMAAAAtqfhaAAAAAXNSR0IArs4c6QAAAAxQTFRFIFYABAQEMzMz////FnVG5AAAAAF0Uk5TAEDm2GYAAABlSURBVBjTY2AgFojAWSFwViiCFQAiRYE4bKkDkJEKYv2bGhoa/QXE+v9v1er/T8AsEECwrsBZF0hmYTPvC5z1Fc76CXJfPoj1FMQKXf///8sQsD9Co6aGQH0kGoDmt6HIEsETfwCrhGoGkCa88AAAAABJRU5ErkJggg=="); //white flag (fake)
		private final static byte[] cannonBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACwAAAAgBAMAAACbaIdJAAAAAXNSR0IArs4c6QAAABVQTFRFb3JtCBAbk0EbT1BTwW42kJKV66hgN2sObAAAAAF0Uk5TAEDm2GYAAAEeSURBVCjPbdJLasQwDADQFGe6F6H7jGO6nlT1HMAIrwvGFxiM7n+Eyt9J0xGEkIesT5JpOoZCiRjD7Swl7M/Q+Iwd7D8NNgJ8NO6nLaINAEvVS+Nc/8CSJ8cxIO6wxM5SOROGHSSEa0sVgv3aEAIURoht5shJ74CFbbShMTO7a+NF2hR+uzP5pGEpjDbeGjttuHKese/IWthdFywvZu2ctNb+sRVkHvworGXF6J88Z/6UI0TENPiSkjZGLiLPevA7cWdygz1npsoyUuFZNmysjdONmRP5nCQ8zZld3jHJQeNcZSVMLteQHpJkpKxbJ0XC2/jsjzKD1PyW+/hJFHWeqdaoQXm/8lwbtrg7SV+nc+SSL1jRn5rH9PUFq1PyLxxmYZUjuphpAAAAAElFTkSuQmCC"); //cannon
		private final static byte[] pickaxeBytes = decode("iVBORw0KGgoAAAANSUhEUgAAAB0AAAAdCAYAAABWk2cPAAAAAXNSR0IArs4c6QAAAelJREFUSMe9lr1uwjAUhY+tSkgsLEwMtO8AqsrPhMQDRALExsSGxABCTN26VF36GjDwBkygVBUwsLElHjJlYUFicgfk1HWdNCGmd7RifzrnXh+HIEXlshmuWz+eziRqH4k6TLdZBk3Gw2D99e09FhAA7nSHTsbD4BAVJoOSwrRKBVA9UIY5LoPvMSzX28SwX0pz2QzvtC04LgMA+B7D54eN6WgQwEQt19urYNo+5rIZ3u91udWs8sN+x1+eJ/yw3/FcNsOtZpX3e10eNjxJioT1VK5Wq4vpaGBMoRbaqJUAAPlCEQ/3xQD8+FQxAqVxP5yOBui0LZiwl+qszRcu6mbzBRyXYWOvkC8U4XsMJoqoQHlKZ/MFAKDTtgKbxVVKY3OovbP5AsfTmRxPZzKbL7CxV3BcBsdlmIyHqWymOpUCKGfpcr2F77Efio0pVYEqeGOvAACNWulqtUEiOS4LBSpgLq5TowYs11uetL9UxF/ckq0uV+pXKaZ/2RoFdlyGfKGYeLDotcMgptr3LhOdRDGJerDjPhSNWgnlSh0bexUrn1ND1byO8/TRtEC5x99THW01gcGSrY6KS6NQGSyeRR2YmobK9zgsuYwr1SlWc4DeCqoqlgPkZkqjekxvDZWvk/yD8C8lfnEB4AtvEUy+7V3wcAAAAABJRU5ErkJggg=="); //represents miner
		private final static byte[] waterBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACoAAAAgAQMAAABelnh+AAAAAXNSR0IArs4c6QAAAAZQTFRFAAAAkrvjZcIR4AAAAAF0Uk5TAEDm2GYAAACNSURBVAjXZc4xCsJAEAXQHyy2TGvlXsNC2Ct5AHEXLCxzpQELr2EqSwWbFTb7M5OISPzFTDPz+IDDGhqHna02HWx5OercB+EqIceeZwEHkoL45p0J4RlP1HsJKFjGYfuDtWnCvFTDQk+nGGds+GC8GHYLTflXFs1q821W4ovd1OxhWOZVMa/Nqn7LBhkjQV9OV99rP9wAAAAASUVORK5CYII="); //water
		private final static byte[] mountainBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACQAAAAgAgMAAAAH/zIdAAAAAXNSR0IArs4c6QAAAAxQTFRFZS1nLh8JUzcQYEATdAlCmQAAAAF0Uk5TAEDm2GYAAACgSURBVBjTXc6xDcIwEIXhC8gbkBHYIyO48FnCQqImLMES1K5zKTJCRqBiCVK7IBIxPjs2iFd91dMPACAgb190K8KmSK7YHLLEWcIuqTtCveoEGFXTV3aL13hC9q6jNI1vw0eVJu/aKDP4R8cSZpw0JfnlSYp18TMRl9W9X5KwSEe5IMPqXfOjqmUNToJohyz606toVlE+CEM7yzoEzCtSH1+ibQhC2mPyAAAAAElFTkSuQmCC"); //mountain
		private final static byte[] bombBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACwAAAArCAMAAAA0X5qLAAAAAXNSR0IArs4c6QAAADNQTFRFBwAHEhMRKCknOjw5uiYSSElGpzovWltY4EYJrFk2cXBtiX878nYNno0mj5CM8MYAw8PC8A7rLQAAAAF0Uk5TAEDm2GYAAAFoSURBVDjLldWBtoQQEAZgQ4yV5P2f9o5RNsxWd057UufrP0JWqVKfRb0vH99p59zi4x79sizOPWHvY9ypovfP8QvTfd8+n/AGr+u+rymnZ+ziltK6hkz1InmjUDow5PSiG6QzHTkk0BaeXnBbg6WmBY1obzGPnS9DTLkWUStuGi49TopzPjImQNgAQDkjBiq0eo4vaTWu0GZLTZwnr+jyY/rFAQddVwV1oNgazDgl1kZ628OevSjjmZOQrY5BsGdyQLYFI0qaX7ElV5wZ4zRZeFgJT90Gc8HEv92wOK0DrU0XXbJrl0nrscdad9F1DmtZI+KrPqvcAhELWsYt2o62X4G64artlVoj4asuDxznaW1XPOhmDYzL6Kuvvl6PuNfsW1tPGKDXlxpwatGTpxswrX2R18t7PJTwWTEXHgBQP/QcL9mGBw5g1a0+H6gtee8DqX5u83q2dxvrf2zP9Zs/SPoyaHvqbv0BILMUI2amD74AAAAASUVORK5CYII="); //bomb
		private final static byte[] flipperBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACgAAAAkAgMAAACGUpCFAAAAAXNSR0IArs4c6QAAAAxQTFRFZS1n8EkO/1Qn/WAiEgMP1gAAAAF0Uk5TAEDm2GYAAADgSURBVBjTVc8xjsIwEAXQHyISiQKOQM8lyBG2oyM022y/LeYI1FRbrMTGQeICSOQIKaCGYqtUKUBIiOjjsWMkirHejEczNjCuAQQmQPIBhIVQ8QLELDwHcuU4PNFTKVP2bOhpBjkG5I2SOz5eZMsueaoxFsamWmNIHiwLYSkHZfuLccvG8KpKpPbRnePkA+lEtgfVPoE6mq6WVybCrWHVyD+Fc30R3nMTeuZZ5YmlBvRWBP3Gzmpn+TlF+L2x/Foj0D+uIUfXzYLO0NcLy1GGqILjL6I/x94/onPLJULb+gQwgqGWk6pfIQAAAABJRU5ErkJggg=="); //represents "frogman"
		private final static byte[] starBytes = decode("iVBORw0KGgoAAAANSUhEUgAAAC4AAAAtAgMAAACsQ7EKAAAAAXNSR0IArs4c6QAAAAlQTFRFAABnYlQA//8AaGGVbgAAAAF0Uk5TAEDm2GYAAACxSURBVCjPjZAxEgIhDEVDsb2FeB6OQEG2oLeQ88gRKMgpJYQIjjJjimXe7E/yAIDrgKX2YN1fgH4HcQETww7ScxFIeQdUFgEq7huw1UmVDw92ggNzklTlxTigsJ9NAplnjFwVPZwpzWXZ1HNVL4EzxTY05T7gxj0/oU/TAZEK0YScFEys7UZBoXh8Q0Jn43jG4xGa33i5496aUQH1wyf3mgG2r0DxsfJf4CoLBIbvBeAF8v9digbU+bgAAAAASUVORK5CYII="); //represents general
		private final static byte[] infantryBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACEAAAAeBAMAAABQ5mokAAAAAXNSR0IArs4c6QAAAA9QTFRFZXQtST0tdFtEaG9ZnJyKtabnNgAAAAF0Uk5TAEDm2GYAAAC+SURBVCjPZdHtEYMgDAZgbB3AhAUkXcAaB+iV7D9T84FFzveP50MIUVI6U9OQRyGRz1VIM5BBQeK+h5yobEMJACFuvQQsSGtI8Qp9B4T/JqBoBYtvaq+WkAm7UPE2N6l4JROR3Q4i5iZPqFJ1nhfzrrKmWaqtCVt2E9FYxeFGPrXI12bOulRD0psVrOLQQ+IzJggxa2KzN2oSD6dBktwEMvMyiAJvvXM0Oi+olWb5X09I6ZD0h8+cr5esQ+cTfj5EKY1/1b/FAAAAAElFTkSuQmCC");
		private final static byte[] machineGunBytes = decode("iVBORw0KGgoAAAANSUhEUgAAAC0AAAAjBAMAAADyPp7ZAAAAAXNSR0IArs4c6QAAABJQTFRFY29sExMTKioqOzs7VlZWfn5+SwJLKAAAAAF0Uk5TAEDm2GYAAAEkSURBVCjPddLhsYMgDABgtAsY2gFKwgCasEAhblD2X+UF61l8Z3P+kM8kBNW5FrekFrIAIsLdFlNTEan1nXQ1xGCPHrX5qDkrAsCKmiu3ys2zan5XCzXX5tX6CHPynLEF6AtDyzdHbhICgAdIBe62W3MIAT3GlvTyUiTWSkVaf0vdHUUTidi1zWktyvr+uGCbNLg9xihSFhS07ozuiHHb3Nx65GfncAR26Z98O3PwC3Hv1lbQDc9xjv+cmN0w32eSrr/DbY6BpxvpqYC2+fwClErnjvb9Bns9c++8l1M6FZDkz429vLPv+R5Tmi98QOoLqBweY/oewR9je8Ty9aj5cMbpwgeEL7vH4eeIha9drp36z9o7/vBffeR6noe8Lt3+kH75BwIIQVMdS0RcAAAAAElFTkSuQmCC"); //mg
		private final static byte[] tankBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACoAAAAiAgMAAABU/qOlAAAAAXNSR0IArs4c6QAAAAxQTFRFZS1nEi4NLmMqZrZsYkw/WgAAAAF0Uk5TAEDm2GYAAAESSURBVBjTPdCxTsMwEAbgS6tWQt37CKxM7H0ED6SikZCYIrVIdGcJj8BSpBapMOCCHREeAAm/AgMbAwssyZDZibjjHKe56ZNPtv87AK4gPIV9aWPPW/aJ6Kd1j21bj9h47X3Epol35vzhfUWv+8tBQYbo0/sL2Y/+ye27IYwbD5PakFWNQ1PxuXIfBDHlbPnMPkwpZT/l3Lj/xsaVgCBJarZd5jcQYILaoCYtYPiylXKhlsge/80vXLZqI+AOY0fKpxO4LDhkTaQ4RMRhqCDccbhIs0uq1g8AM+eszBUPOZaa8K1Wc44zUJG2ZSX9sm43O8yU8LP/qtSuW/dXC3XW7gcOZDjrFn0cnnTurURnGDj/A/dLrKDCmyL8AAAAAElFTkSuQmCC"); //tank
		private final static byte[] explosionBytes = decode("iVBORw0KGgoAAAANSUhEUgAAACQAAAAoBAMAAABk7EXQAAAAAXNSR0IArs4c6QAAABtQTFRFIQAAZwUAkiIAvUkB6XYE9J0R/sQa/+89//6BccFzVAAAAAF0Uk5TAEDm2GYAAAF8SURBVCjPXZJBcsIwDEUNpaxxuACJma4hTvcMdrgAcU6A5Qt0LG4Qjt3v4EBaLTKTN1/SlywhXqHLjfgbH7qUM7aySQU2k9hTZ+1ZztDKueDdWRbvzFVPzKTLGbI9M0dTl8ULtQ5o6ExdzRID3wfqjH4jlHoM7Ls3Qt79MUTvzWFCASKoKHbHTAggIf45T2YpIO0+BL4dM1rHVIlj4Gue8hPt+AH3REY+l+HeqDtmFJj4joGA6uca4TwmIb6drqd+PKLkXmdXU3h31So1DC9E3tTFQTjXo0gMYy2HLR7Emrwjirl8LRVQYN/Ts6MH2huxujxioJCRrvYnsfxK4/HTqzN1kx65pbFOMgwZHnmpbNOnAROCzAix2Fb6EsehEd7onVgUUrWQvBDcS1k0aRnUk3Oon1HPvsU/TmV8okVRqvZmGts6oxuUSkyqpqu3+ttUCjc2bmepNE4r/Spd7aajPCaEmbNIjD1kgbss9Px65WYpq639d9JVYzGO+AXROZ7Z3jt4wAAAAABJRU5ErkJggg==");
		// These are base64 encoded sound effects. To save space, many of these are cut short, and looped a few times.
		private final static byte[] invalidSelectionSoundEffectBytes = decode("LnNuZAAAABgAAAGeAAAAAgAAH0AAAAABBw0ODhIOEQ8RGRokKx4VIt0FqNMpnOfKk8zR2gEXTnl/fn9/f3bpgIeAqE5/d39Un4CJgBd/c38xgIeAuXl4f0CAiYDKf3d/GoCPgNx/e3/qgIiBFH1+K5qAgcVCczzHhICwMXxvG6OAgLkjW1AIsoul4ilJNQLHsMXsDRUH59Pb6wQOCPnn6fUIEQn/+PL5DRMKAfXx/w0gJBoL+vT/GCAkHgoC/QgZHR8XCwUEDxofIhQH/QETJDlAOSEK/fgOJDE2KA/79wQcLTYxHhEIDxojKh0QBQYTJTI0JhYKBQ8eKioiFgwPGSIrKB0PCgkTISopGg0KChMfKCUYDgULFCQqLB4OBwAEDBceIBsWFA8NEQ4PERQSFREQDwsJCAwMDhMVFBcTEw4ODw4NDA0LCwkNDQwMDRMSFBUTEQ0LDBAPEA8NCgsPDhgYGRYOCwYMERgcHRkTDQ0RFhgZFxMSFBgZGhkSCAsNFBkcGBAPDREVFxYUEhMVEhMRDREREBIUDgkDBgoMExAQDQgJCAoMCwgIBgUEBgcGCgYHCQn/");
		private final static byte[] poppingSoundEffectBytes = decode("LnNuZAAAABgAAAXeAAAAAgAAH0AAAAACAAAAAAAAAAAAAAAAAAAAAAEBAQEAAAAAAAD//////////////////////v7///////8AAAAAAgEEAwQEBQUGBgUFAwQCAvz+9fbq7dLW2tMqGVpaHy7e5ePeA/4NDvf85OXs6QUAFBP+BNjd19Ty7vr67fDo6Ovr7ezz8vb37O7q6Pz4CAcFBwAB//////7/+fr39//9AgP5+/b2/v0FBQcHCgkODQwNCwsNDAsMCwoREBYVExQPEA0NDQ0KCwUGAwMCAgMDBAQEBAQEAgICAQUFCAgICAkJDQwPDw4ODw8TEhcWGxofHiEhHyAeHiAfICAdHhoaFRUREg0OBwgBAvz9+fn29vPz8fHv7+7u7+/u7uvr6+vx7/X08fLs7evr7Ozu7vDw7+/s7evr7Ozs7O/u8vLw8e7u8O/x8fHx8fHx8fPy9/b5+fn5+Pn5+fv7/f39/f39/v3///7+/f38/Pn6+Pj4+Pj4+Pj4+Pj4+Pj3+Pb29fX19fX19vX29vX29vX39vj4+Pj39/j4+fn6+vv7+/v6+vn5+/v//wAAAQEBAQICBAMGBQcHCAgICAcHBwcIBwgICAgHBwgICAgJCAoKCwsKCwsKCwsLCwkKCAgHBwcHBgYEBQQEAwMDAwICAgIBAQAAAAAAAAEAAQEBAQMCBAQFBQUFBAQGBQcHBwYIBwkJCgoLCwsLDAsMDAwMCwsLCwsLCgoJCQkJCgoKCgkJBwgHBwcHBQYDAwEB/wD////////+/v7+//////7+/f38/Pz8/Pz8/Pv8+vv6+vn5+fn5+ff49/f4+Pj49vb19fX19vb19fT19PT09PT09PT09PT09fX19fb29/f4+Pn5+vr7+/z8/v3+/v//AP8AAAEBAQEBAQAAAAAAAAAAAAD//////v7+/v7+/v7+/v7+/////////v7+/v39/f38/fz8/Pz8/Pz8/Pz8/Pz8+/v7+/v7+/v7+/v7/Pz8/P39/f3+/gD/AAAAAAEBAgIDAwMDBAQFBQYGBgYGBgYGBgYGBgYGBQUFBQUFBAQEBAQEAwMDAwMDAwMDAwMDAwMDAwQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAMDAwMDAwICAgIBAQEBAAAAAP8A//////7+/v7+/v7+/f39/f7+/v7+/v7+/v7+/v7+/v7+/v7+/v79/f39/f39/f39/Pz8/Pv8+/v7+/v7+/v7+/z8/Pz8/Pz8/f39/f39/v7+/v7+/v7+/v39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f3+/v7+/////wD/AAABAQEBAgECAgMCAwMDAwQEBAQEBAQEBAQFBQUFBQUEBAQEBAQEBAQEAwMDAwMDAgICAgICAQEBAQEBAQEBAQAAAAAAAAAAAAAAAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAgICAgICAgICAgICAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQAAAAAAAAAAAAD///////////7+/v7+/v7+/f39/f39/f39/fz8/Pz8/Pz8/Pz8/Pz8/Pz8/Pz8/Pz8/Pz8/fz9/f39/f39/f39/f39/f39/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7///////8A/wAAAAAAAAEBAQEBAQEBAgICAgICAgICAgMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAQEBAQEBAQEBAQEBAQEAAAAAAAD////////+//7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7///////////////8=");
		private final static byte[] bombSoundEffectBytes = decode("LnNuZAAAABgAABs1AAAAAgAAH0AAAAAB//7//v/+//7//v7///7//v///v/+//7//v/+//7//v/+//7//v/+//7//v///v/+//7//v/+//7//v/+/////v/+//7////+//7///7//v////7//v///v/+//7//v/+//7//v///v///v/+/////v/+//7+///+//7///7//v7///7//v/+//7//v/+//7///7//v///v/+/v///v/+//7//v/+/v/+///+//7//v/+//7//v/+/v/+/////v/+//7//v/+//7//v/+//7//v/+//7//v/+//8AAQABAAEAAQABAAEAAQABAAEAAQAA/wD/AP8A//8A/wD/AP7//v/9AP0A/AwMFxoeJCIrJi4oLiwuLi0xKzQqNSLBvKWvm5KgmKqhsKizrbOysLesv6Lvd256YXNqa29peWd/Y2c2OWxVf2NlWtOnoaeonamYsdY1fXlNLwq5+gY1BsrdzNzBzwkVwqDc5Rc6M04+f2R/bXh0Uz1LbVJjUj2roZSCj4CAgosYRXVhTWolUFNlX31nbl5fZUdKVklhb1d5Et3VoaqasLGvhpGMgJGAtevMgKGA0Nr+f2RHAGZwaH1geFVmY3JoKTsvKRkvIQtFf0MOWnkjA9SA1kxYYFBfMNLTnpmSgJnu2KedzeuRh8r2y8q+vbuA8VxacVd/UndSX2Flcl1/YH9yIdAnSx+ws6yQttvhm8zj9TtAdn1bQBUK68LubWh8f0D7LGEF6OOMjYCOr5oYYmp3JAb0iYOAovodU38doqu18iBPfyCumJ63qLHR6YSMoKOIgqKAxDI7tMCdgIyAo8b6pYumgICJqJObxsfUsi5/X3FyfzBvZk1VQ0ohA+aAnOLGHU1bXQDq74imCAeauwsR3v7Wndfil6+8uZmYtYicgJeCk4SWkYCkyLyAqamLSFpYf2d/dHp2XUhJf0idy7Wfna+igKGAyD0be3t4f0sxVH92f3x/aRhIc/ZEdy4tFwXPjYWS0pXZuKTSlK+yq6LBzrCFqImlycZdbBQPGeofcFVlZm5mRVlKMNmKzyXlrwGWpauavZ6rnsihxwcA+eUtYywQYn9hWF13ZWFvN0ThgL6gufX0j6eVmo6PmICYgI6AloSfurq8y9I5UixSYVxSVFpNP3B5Mw3qB+2pzYeAgYCBgIik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");
		private final static byte[] pickaxeSoundEffectBytes = decode("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/fz6+QEABAP///v7/v8DBAEA+/r9/AMDAgP7/Pz7AgEEA/7++vv/AAQE/wD6+/3+AwQBAvv8/P0CAwID/fz7+wABBAP//vv7/v8EAwD/+/v9/gIDAQL7+/z7AgEDAv39+vsAAQME/v/6+/7/AgMAAfv7/P0CAwIB/fz8+wIBAwL+/fv7/wACAwD//Pv//wMCAQH7/Pz9AgECAf39/PwBAQID//77/P7/AgP///z7/v4CAwEC/P38/QECAQL+/vz8AAECA/7/+/z+/wID/wD7/P3+AgMBAvz9/fwBAAIB/v38/P8AAwIA//z7//4DAgEB+/z8/QICAQL9/v38AQEBAv7/+/z/AAMDAP/8/P//AgEBAP39/fwCAQIB/v39/QABAgL+//z7//4CAQAA/P39/gECAQD9/P38AQEBAv3+/P0A/wMD/v/7/P7/AgEA//39/v0CAQIC/f78/QABAgH///39AP8CAQD//f3+/wECAQD9/f3+AgACAf79/f0AAAIC///8/f/+AgEA//38//8CAQEA/fz+/QEBAgL9/vz9AP8CAf///fz//gIB/wD8/f3+AQIAAf79/fwBAAIB//79/f8AAQIA//38//8BAgD//v3+/QEAAQD+/v39/wAAAf///P3/AAEC/wD8/f//AAH/AP3+/v4BAQAB/f7+/QD/AQD//v38//8CAv8A/v3//gEBAAH+/v3+AAEBAP/+/v0A/wEB/v/9/P/+AQAA//7+/v8AAQEB/f79/gD/AQD//v79AAAAAf8A/P7+/wAB/wD+/f7+AQEAAf39/v0AAAAB/v/9/v8AAAH/AP3+//4BAAD//v3+/QEAAQD//v79AP8BAP/+/v3//wEBAAD9/v7/AQH/AP3+/f4BAAEA///+/QD/AQD//v79//8AAQD//v3//gEBAP/+/f79AP8BAP/+/v0A/wEA/wD9/v7/AAH/AP79//4BAQAB/v/9/QD/AQD//v79AAABAQAA/f7+/wABAP/+/f/+AQABAP/+/v7/AAAB/v/9/v8AAAH/AP3+//4BAAAA/f79/v8AAAH//v7+/wAAAf/+/v3//gEB/wD9/v7//wD/AP3//f7/AAAB/v/9/v8AAAH+//3+/v8AAQD///7//gAA/wD+//3+/wAAAf7//f7/AAAB/wD+/v7//wD/AP/+//4A/wEA/v/9/gD/Af///v79//8BAf8A/f7+//8A/wD//v//AP8BAP/+/v0A/wD////9/v/+AQAA///+////AP8A/v///gD/AQD////+AP//AP7//f7+//8A/wD+//7///8A/////v0A/wAA//7//gD/AQAA//7+/v//AP8A//7//gD/AAD+//7//wAA/////v//AAEAAAD//v/+AP8A//////4A/wD///7//wAA/wD+///+//4A/wD///8=");
		private final static byte[] assaultRifleSoundEffectBytes = decode("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");
		private final static byte[] machineGunSoundEffectBytes = decode("LnNuZAAAABgAAAO6AAAAAgAAH0AAAAAB69vo9Pbw7fn8CAXxCPvy7Of39t7z/OML7vDk2ATs0Qb4tfv55+ff1vvz5A/tBRLj7OQGGeMM9uLv4Prr7fjj+eXw/ucA9gUJ8hkG/QXkDPHZBNH+9uUD5A/5/P7u/+/k5trd6+vn7vH88Q38/wfyBPUF8/T66vfz9An2+/r7+/0BAgby6Ojk5fr9+gIAAgcA+vj96u39/vn57vX39/rw7PLt9OLv7uQF6fry+ATy5AoD/f/3/wj2/vDgCuj4/fYG+u0TAwUS8PEF8QX47Q4A//gKDhL99vzzEO8E9/QN+gUKBPUHBiD3DxYI9hP+5fnzECIez/YJBf73+/cM3xIT8Bf8EuX5DOPwGC4i8erv+QsOB/kLCQ4R7Qn+7xgDCO/+/w0Z+vD02i0HIBrRFgMSDAMD7/cnBegdDwf7+Brn4vw48MUU+yL11yoD9Rbw/gEg3vgwM/vhFAcR2AAC/sQR2JKLycelBIDdOH9+f/HPfUL2rYzjnICLJBzd8H8lgFV+f2zdgICS4ICAtm9+f39jEiAaHFC+EB6lmICAn52yAz9S3t7a5AgHf394U0hzHoGlzfDJFX5Lf39uf0sd8zJ5Rgn2vcjb4/32Ld2SkYCFk6HMsrz99gaqt77KAt+ogICAgYCFsgwvIzcxLSvxvoyN9BAaCcv97x8jBwAbGPTS2ltXaT1eZ2B3f35/eTwfB0h5VWRsZX9+TyD/+fDyC2V+e2x/f2NRItja8/Pu4FZ+f35zRx3Sya6z9yRNOz08Sx7W+SxMJyBRZRwQDAHy+wAMMSItGCtTWDgvJjM7TBrKwMkNEPf84Q8p9tzoNl85FSY4C+wUOCjP5+b29grltOPsKxEDFAQwKPw4Ix4C6wTx0tft5PUM9gYXCg8QEgsC9djl//X40tnw/xQ1JiIiDxLu5fjz+vTz7u4QDwcF/CcbDgbw7t7l3uf48P0A2u7v+QQNCPcDBAj87/Ps3ODV3efv9fjy5eHez8PMyePz7/357+zv7u/69Pb98Pb+/wH/Aff49vX79//27Ovp6+bh4N/b3N7m5OXq9vv07+vl2tza4/kEBAP6+vT6/vXy5Nvj9Ozb0MjI0dPUy9bh+gXy9PHl4NnV6evx4s7V4u/y7/r48/ny6OHh5+vs5fP2/QIDBQD+9/fv8Obm6fLt+eLx5+Llzu7zz/kA6fjTBePTAwIC8OIdBAkH3fH6BgPn2f/a7enY4vAFBOPa3eD4Gx4K+ucF9SQY6ubcBBHt8/r3GvUICQr60Onj4gDh5e4A");
		private final static byte[] cannonSoundEffectBytes = decode("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BgX//wQH/wkC/w0ECQIBAgAHAgQGBgIGA/4IA/r/BQL8+vn4+/8AAQP89Pj+/vn5APj3+vb9APvy7Pb3+fz+/fj7+wX98/gA//wC//7/BAICAP3++wECAAQKA///AQIFAP7//wL9/QIEAP8CA/8AAQIGCAH9//38/wD+/QEC/f8CCAv//PwACAIC/wIEAAUICAH+AwQFAgAC/v78AgX8/AAEA/z7AAYF/fr7/gQIAPz9A/r4+/wE/QP3/QT/A/3/+Pj4+gX+/v/+APj7BAwA+fQDB//++gT9/vf+BwEA/Pr+/v3/AAL6/QEEA/v5/P7+/QQJAwL+AAEDAQIF////AAYJAwMCAwcIBQH++vb2/gYLCAQCBAL+/wD++vr9BAQCAPz9Av76+fz9/P738vr++fr9//4A/gEB//7+AwQFBAQEAQD/AP8CAQECAf////7+/f/9AP7+//39/v7+//8CAgH/AP////7/AQEAAAICAQEBAAECAgEA/////gECAgD/AQEA//8AAQEAAAD/AAACAgEA/wAAAAD///79//4BAgEB//79//79/P///gEDAf//AAAA//7+///+/P39/wD//v7//f7+/f4A//7+//8A/v8AAQEB//38/P4A//4AAAD+/wD/AQECAQAAAQMDAQAAAgMFBQMA////AQIAAAEBAP7+AQD/AAD///7///38/f7/AP/9/wH++vv+AgH//v39/v7+/f/+/v/9///+/v7//v8BAwMAAQECAwAA/wAAAQAABAUB/wEAAgH//v7+/QABAP8A/v39//8BAAAB/fz/AAD//wECAwMFBQIEAwEAAAD/AAEAAQMB/v4AAQEA/fr6+f0AAAD///3/Af/+//79/vz+AP79/v3+AQD//Pz+/wEA//37+gACAwQEBP79/gIB/QABAQH//wD+/PsAAwIA/Pv+AgMBAQYHBgYDAwD9/P4DAgIA+/3///3+//r6+fn6+vv6+/wBAwMDAwH+AgMDAwQFBAQGBwcFBwYGBwYFAgQEBAMCAQEB/wAEBAAAAQD9+vv8/fz8/gD59vn5+/7//fz4+Pr49/r79vj5+fn6/vr6+/r6+fr9/f39/f0ABQUGBAQFAgMDAwQHCAQDBQYHBwYEAwUGCgcEAwAC");
		private final static byte[] tankSoundEffectBytes = decode("LnNuZAAAABgAACBpAAAAAgAAH0AAAAABAP////8A////////////////////////AP////8A////////AAD//v///v8A/wD//////v/////+//////7//////wD///7//////////////v///v8A///+////////////AP///////wD+//////////////////7//wADAQLv7v4NBwj/+gP9/QAA+wP9/gD7/QD8AgP9AP77AQH+BAD9Af/+Afr/AQD6D/PN6e75Bf7RvOkXKSwvKyYmIiEoISUbDiD07+z0Hu7L1dnMz9QE9cTv/PDd0tjh3dLX1tnW3N3dzuj06AsiFhElJCgnLSYhKCkkJSYpJiEaHycaJiAXIh8QDQ0TBvv6+/jw9u3o7Obq5Nzj3trj6Nvp6uHl4t7e6fDo5OTs6eHp8ezi6Pf9+gAQEgYRHCAeICgkIyUiHR0hGxMUGBgPGyMQBgoSEAMJEgn7Awr07fD07ODs8O3k5uvr7/Hv6vD79fH+/vLz+/3z7fP36+v6+vX1/wf9AQ8OBgwXEg4RFxULERUPCA0NAAIGBgABCwj8/gUB+Pv++vP2/vXy+vr18/v/+PkD/vb8A/74/AQB9/oA+/b8APfz+wH79gEG+vkCBf3+BAQAAQkGAAYIA/8ECgL8BQoB/wYGAQAHCPz7Av/4/AD89vkB/vn/Bf79BQT+/AMGAAEJCAIGCwP9/QH+9fj79e7x9PHu8PTy8Pj69/oAAv7+Af/8BAwIAQUDAv8DBQL9AwcSJDclFxAF+tLNwMTkCwPrwNS6stHxDhcR/Pbi//wA4xcMASguKVBaRxUP/NgD4ODzAPruzd7O8PUKAfIROh0lU0k2Pm0vSkYeFCAJMjlJFCZIBgj29AIH49TjAOj+z9z79dWrqLfR/Pj9EMvb7t/M9SAdQFAECgv7ARUNBQMJAvQB/C8rGw3u5zE0DfbquK3j+QAZFwgRFiUQQzY6D+P10bXjDx7/AwHTBATz/BUgLBv+6SEYFwLd8OvNy+7jyLrp3vcPDSMQ1s7U9t7i3twJ//YbKCcxJC0JMh4GGSAKCAMKCBviyOXK5d3m+Q0KDPcTChQcHjVINSs8MC4rHw4yDvHtydjp2enrBvX7Cf8LGPPxBw8T6Afcyb+w3PXQ7ejx+uLixw4Q+Obu/PH2C+f5A/Hu2Ovw8vHwCBos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");
		private final static byte[] waterDripSoundEffectBytes = decode("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BAD+/P8BBfz5BBL7/fwC/AL+DP0A+gER+gbzEO4N9Rb6B/b+DvgP7g7wD/UI+wT//vz8Bf4A/AT/APsC/Qf///gBAwIB/v78/gD+A/8B+foCAgX7/vsCAQAC/wH7//0B///9+wD8BfwC+gH+/wD9AP0C/QL9AP7+/v8BAP38/v8AAvv++gD9AAD9/fr+/QD8APkA+gH8/////fn//gH+/vr+/v/+/gAA/f37AP8C//77+v7+BAD/+voAAQX9AP3+/v0CAAT5/PgAAAD8+fv7//79/Pv8/f4A/f77/f4B/v38/P7///78//z//AIE+P/+//gIAPcD/fwCAfz/Av/7AAT6/QX9+gQD8gYK9P8L+fwI+wEDAP///wQA+gr7+Qz99wwA9QkC9wQE/AIC/gEDAQQB+gkD9w4C9w77+A//+Qv+9gz9+w35/QrzAgr3BQbzAgX3CQP4Bv76Cf/+Cfr/BfwBBvz+BPv/Av4BAP0A/gAC/AH+/QH//QD//v/9//7+AP78//39/gD9/v78//38/vz8//r//v39/vv+//z///78//79/f/+/P7+/v7//v3///z///79//79/wAA//////////8B//8A//8AAP//////Af7///7//////wD//gEC//8CAAAAAgEA/wABAP//Af///QEB/QEA/wH/AQAAAf8BAP4AAgH/AAAAAP7////9/P7//f3////9/QAA//3+//3+/v7//v3+/////////v8B/wD//v8AAAAAAQAA//8A//8A/gABAAEBAQEBAAABAP/+/v/+AP/9/v/+//8AAP8BAAECAgICAwECAgAAAP////3+/////wAA/wAAAQEBAQECAgECAgEBAQAA/////////v7+//7+/f3+/v7+/v///wH///7+/v3+/fz8/Pz9/f7+//8BAAAAAP///v39/f39/f3+/f/+/wD////+//7+//79/f79/v3+/v3+///+///+/v//AP////////79/v7+/v7//wD/AAABAAABAQEBAQEAAQD//////v/+////AP8AAQABAAEAAAAAAP8A/wAAAQAA/////v/+/v7+//7/AAAAAAAAAQABAAEAAAEAAQABAQABAAEAAAABAQABAgIDAgMDAgMCAgMCAwICAwMCAwIDAgIBAAEAAQAAAAABAAAAAAD//wD//v7+/f79/v///v/+//7+/v3+/v/+//79/v39/fz9/f39/v3+/v39/fz8/Pv7+/r7+/v7+/v7/Pv8+/z7/P38/fz9/f79/v38/fz8/fz8/P38/Q==");
		private final static byte[] coinFlipSoundEffectBytes = decode("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");
		private final static byte[] victoryFanfareSoundEffectBytes = decode("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");
		private final static byte[] defeatSoundEffectBytes = decode("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");
		private final static byte[] fakeFlagSoundEffectBytes = decode("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");
		private final static byte[] generalSoundEffectBytes = decode("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");
		// These  are the actual images that are read from the decoded byte arrays.
		// These images can be directly drawn using g2.drawImage(resources.ImageName, BufferedImageOp (filter; used for processing/effects), x, y);
		protected final static BufferedImage blankImage = getImage(blankImageBytes);
		protected final static BufferedImage redFlag = getImage(redFlagBytes);
		protected final static BufferedImage orangeFlag = getImage(orangeFlagBytes);
		protected final static BufferedImage blankFlag = getImage(blankFlagBytes);
		protected final static BufferedImage cannon = getImage(cannonBytes);
		protected final static BufferedImage pickaxe = getImage(pickaxeBytes);
		protected final static BufferedImage water = getImage(waterBytes);
		protected final static BufferedImage mountain = getImage(mountainBytes);
		protected final static BufferedImage bomb = getImage(bombBytes);
		protected final static BufferedImage flippers = getImage(flipperBytes);
		protected final static BufferedImage general = getImage(starBytes);
		protected final static BufferedImage infantry = getImage(infantryBytes);
		protected final static BufferedImage machineGun = getImage(machineGunBytes);
		protected final static BufferedImage tank = getImage(tankBytes);
		protected final static BufferedImage explosion = getImage(explosionBytes);
		protected final static AudioInputStream poppingSoundEffect = getSound(poppingSoundEffectBytes);
		protected final static AudioInputStream coinFlipSoundEffect = getSound(coinFlipSoundEffectBytes);
		protected final static AudioInputStream bombSoundEffect = getSound(bombSoundEffectBytes);
		protected final static AudioInputStream pickaxeSoundEffect = getSound(pickaxeSoundEffectBytes);
		protected final static AudioInputStream assaultRifleSoundEffect = getSound(assaultRifleSoundEffectBytes);
		protected final static AudioInputStream machineGunSoundEffect = getSound(machineGunSoundEffectBytes);
		protected final static AudioInputStream cannonSoundEffect = getSound(cannonSoundEffectBytes);
		protected final static AudioInputStream tankSoundEffect = getSound(tankSoundEffectBytes);
		protected final static AudioInputStream waterDripSoundEffect = getSound(waterDripSoundEffectBytes);
		protected final static AudioInputStream victoryFanfareSoundEffect = getSound(victoryFanfareSoundEffectBytes);
		protected final static AudioInputStream defeatSoundEffect = getSound(defeatSoundEffectBytes);
		protected final static AudioInputStream invalidSelectionSoundEffect = getSound(invalidSelectionSoundEffectBytes);
		protected final static AudioInputStream fakeFlagSoundEffect = getSound(fakeFlagSoundEffectBytes);
		protected final static AudioInputStream generalSoundEffect = getSound(generalSoundEffectBytes);
		/**
		 * Reads the byte array of the specified resource and returns a ByteArrayInputStream.
		 *
		 * @author Dylan Taylor
		 * @param resourceBytes A byte array containing the data of the resource.
		 */
		public static ByteArrayInputStream getResource(final byte[] resourceBytes) {
			return new ByteArrayInputStream(resourceBytes);
		}

		/**
		 * Reads the byte array of the specified image and returns a BufferedImage.
		 *
		 * @author Dylan Taylor
		 * @param resourceBytes A byte array containing the data of the resource.
		 */
		public static BufferedImage getImage(final byte[] imageBytes) {
			try {
				return ImageIO.read(getResource(imageBytes));
			} catch (final Exception e) {
				System.out.println("Error reading image. Stack trace: " + e);
			}
			return null; // this will probably produce a crash...
		}

		/**
		 * Reads the byte array of the specified sound file and returns an AudioInputStream.
		 *
		 * @author Dylan Taylor
		 * @param resourceBytes A byte array containing the data of the resource.
		 */
		public static AudioInputStream getSound(final byte[] soundBytes) {
			try {
				return AudioSystem.getAudioInputStream(getResource(soundBytes));
			} catch (final Exception e) {
				System.out.println("Error reading sound. Stack trace: " + e);
			}
			return null; // this will probably produce a crash...
		}

		/**
		 * Gets the music from the music file as an AudioInputStream
		 *
		 * @author Dylan Taylor
		 */
		public static AudioInputStream getMusic() {
			try {
				File music = new File(Main.musicFile);
				return AudioSystem.getAudioInputStream(music);
			} catch (final Exception e) {
				return null;
			}
		}

		/**
		 * A very fast and memory efficient Base64 decoder that was written by Mikael Grev, and released under the BSD license,
		 * which allows usage for any purpose, commercial or non-commercial, as long as credit is given to the original author.
		 * This method is used in order to decode our base64 encoded resources back into their original form.
		 * We chose to use this method instead of the base64 decoded built into Java due to speed advantages.
		 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (base64 @ miginfocom . com) All rights reserved.
		 * @param s The source string. Length 0 will return an empty array. <code>null</code> will throw an exception.
		 * @return The decoded array of bytes. May be of length 0.
		 */
		public final static byte[] decode(String s) {            
			final int[] IA = new int[256];
			Arrays.fill(IA, -1);
			for (int i = 0, iS = CA.length; i < iS; i++) IA[CA[i]] = i; IA['='] = 0;
			int sLen = s.length(); // Check special case
			if (sLen == 0) return new byte[0];
			int sIx = 0, eIx = sLen - 1; // Start and end index after trimming.
			while (sIx < eIx && IA[s.charAt(sIx) & 0xff] < 0) sIx++; // Trim illegal chars from start
			while (eIx > 0 && IA[s.charAt(eIx) & 0xff] < 0) eIx--; // Trim illegal chars from end
			int pad = s.charAt(eIx) == '=' ? (s.charAt(eIx - 1) == '=' ? 2 : 1) : 0;  // get the padding count (=) (0, 1 or 2), count '=' at end.
			int cCnt = eIx - sIx + 1;   // Content count including possible separators
			int sepCnt = sLen > 76 ? (s.charAt(76) == '\r' ? cCnt / 78 : 0) << 1 : 0;
			int len = ((cCnt - sepCnt) * 6 >> 3) - pad; // The number of decoded bytes
			byte[] dArr = new byte[len]; // Preallocate byte[] of exact length
			int d = 0; // Decode all but the last 0 - 2 bytes.
			for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) { // Assemble three bytes into an int from four "valid" characters.
				int i = IA[s.charAt(sIx++)] << 18 | IA[s.charAt(sIx++)] << 12 | IA[s.charAt(sIx++)] << 6 | IA[s.charAt(sIx++)];
				dArr[d++] = (byte) (i >> 16); dArr[d++] = (byte) (i >> 8); dArr[d++] = (byte) i; // Add the bytes
				if (sepCnt > 0 && ++cc == 19) { sIx += 2; cc = 0; } // If line separator, jump over it.
			}
			if (d < len) { // Decode last 1-3 bytes (incl '=') into 1-3 bytes
				int i = 0;
				for (int j = 0; sIx <= eIx - pad; j++) i |= IA[s.charAt(sIx++)] << (18 - j * 6);
				for (int r = 16; d < len; r -= 8) dArr[d++] = (byte) (i >> r);
			}
			return dArr;
		}
	}
}
