/*
 * Nicholas Andrew Mugleston
 * COSC 1174
 * 4/1/2020
 * Homework 6, Poker 3
 */

package sample;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

//The class that will deal a hand in poker
public class Main extends Application {
	
	//Will hold the whole deck of cards; it is not to be edited - it will be used to restock the use deck when it is implemented
	public static final LinkedList<PlayingCard> fullDeck = new LinkedList<PlayingCard>();
	
	//Holds the cards in the deck that the player is using
	public static LinkedList<PlayingCard> useDeck = new LinkedList<PlayingCard>();
	
	//Holds the cards in the player's hand
	public static PlayingCard[] hand = new PlayingCard[5];
	
	//Holds the cards the player plays against
	public static PlayingCard[] enemyHand = new PlayingCard[5];
	
	//The hold buttons the player can press to hold their cards
	public static ToggleButton[] holds = new ToggleButton[5];
	
	//The front images of the cards in the player's hand
	public static ImageView[] cardFronts = new ImageView[5];
	
	//The back images of the cards in the player's hand
	public static ImageView[] cardBacks = new ImageView[5];
	
	//The x pixel coordinates that the cards should take respectively
	public static int[] cardPositionsX = new int[] {300,400,500,600,700};
	
	//The y pixel coordinates that the cards should take respectively
	public static int[] cardPositionsY = new int[] {100,100,100,100,100};
	
	//The x pixel coordinates that the buttons should take respectively
	public static int[] buttonPositionsX = new int[] {315,415,515,615,715};
	
	//The y pixel coordinates that the buttons should take respectively
	public static int[] buttonPositionsY = new int[] {200,200,200,200,200};
	
	//The deal button that gives the player a fresh hand
	public static Button deal  = new Button("Deal");
	
	//The draw button that allows the player to replace any non-held cards in their hand
	public static Button draw  = new Button("Draw");
	
	//The button the player presses when they want to bet 1 US dollar
	public static RadioButton rb1 = new RadioButton("$1");
	
	//The button the player presses when they want to bet 10 US dollars
	public static RadioButton rb10 = new RadioButton("$10");
	
	//The button the player presses when they want to bet 100 US dollars
	public static RadioButton rb100 = new RadioButton("$100");
	
	//The X coordinates of the three bet amount buttons
	public static int[] rbPosX = {40,40,40};
	
	//The Y coordinates of the three bet amount buttons
	public static int[] rbPosY = {100,150,200};
	
	//The object necessary to only let the player select one bet amount at a time
	public static ToggleGroup betButtons = new ToggleGroup();
	
	//The amount of money the player has in their "pocket"
	public static int funds = 200;
	
	//The amount the player has selected to bet
	public static int bet = 0;
	
	//The X coordinated of the following two labels
	public static int[] labelPosX = {100,100};
	
	//The Y coordinated of the following two labels
	public static int[] labelPosY = {125,175};
	
	//The format the amount of money the player has should be prefaced with
	public static String contextDisplayFunds = "Funds: $";
	
	//The object that displays how much money the player has to the screen
	public static Label displayFunds = new Label(contextDisplayFunds+funds);
	
	//The format the amount of money the player selected to bet should be prefaced with
	public static String contextDisplayBet = "Current Bet: $";
	
	//The object that displays how much money the player selected to bet
	public static Label displayBet = new Label(contextDisplayBet+bet);
	
	//Handles the creation of the deck and the display of the user interface
	@Override
	public void start(Stage primaryStage) {
		//Allows us to add the cards to the scene display
		Group root = new Group();
		initializeDecks();
		setButtons(root);
		setHand(root,false);
		setEnemyHand();
		/*-Play testing and debugging---------------------------------------------------------------------------------------
		for(PlayingCard p : hand)
			System.out.print(p.value + " of " + p.suit+", ");
		System.out.println();
		for(PlayingCard p : enemyHand)
			System.out.print(p.value + " of " + p.suit+", ");
		System.out.println();
		System.out.println("-----");
		//------------------------------------------------------------------------------------------------------------------*/
		try {
			//Holds what we want to display
			Scene scene = new Scene(root,800,400);
			primaryStage.setTitle("Poker Hand");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Where the program starts running
	public static void main(String[] args) {
		launch(args);
	}
	
	//Initializes the values, attributes, and event handling for each button
	public static void setButtons(Group root)
	{
		//Creates the 5 hold buttons
		for(int i = 0; i < 5; i++)
		{
			//Creates a hold button to be added the the holds array
			ToggleButton hold = new ToggleButton("Hold");
			hold.setLayoutX(buttonPositionsX[i]);
			hold.setLayoutY(buttonPositionsY[i]);
			
			holds[i] = hold;
			
			root.getChildren().add(holds[i]);
		}
		
		draw.setMnemonicParsing(true);
		draw.setOnAction(e -> {
			try {
				setHand(root,true);
				funds -= bet;
				displayFunds.setText(contextDisplayFunds+funds);
				/*-Play testing and debugging-------------------------------------------------------------------------------------
				for(PlayingCard p : hand)
					System.out.print(p.value + " of " + p.suit+", ");
				System.out.println();
				System.out.println("-----");
				//-----------------------------------------------------------------------------------------------------------------*/
				if(playerWon())
				{
					funds += bet*2;
					displayFunds.setText(contextDisplayFunds+funds);
				}
			} catch (Exception err) {
				err.printStackTrace();
			}
		});
		draw.setLayoutX(220);
		draw.setLayoutY(155);
		
		root.getChildren().add(draw);
		
		deal.setMnemonicParsing(true);
		deal.setOnAction(e -> {
			try {
				setHand(root,false);
			} catch (Exception err) {
				err.printStackTrace();
			}
		});
		deal.setLayoutX(220);
		deal.setLayoutY(125);
		
		root.getChildren().add(deal);
		
		rb1.setMnemonicParsing(true);
		rb1.setOnAction(e -> {
			bet = 1;
			displayBet.setText(contextDisplayBet+bet); 
		});
		rb1.setLayoutX(rbPosX[0]);
		rb1.setLayoutY(rbPosY[0]);
		
		rb10.setMnemonicParsing(true);
		rb10.setOnAction(e -> {
			bet = 10;
			displayBet.setText(contextDisplayBet+bet); 
		});
		rb10.setLayoutX(rbPosX[1]);
		rb10.setLayoutY(rbPosY[1]);
		
		rb100.setMnemonicParsing(true);
		rb100.setOnAction(e -> {
			bet = 100;
			displayBet.setText(contextDisplayBet+bet); 
		});
		rb100.setLayoutX(rbPosX[2]);
		rb100.setLayoutY(rbPosY[2]);
		
		betButtons.getToggles().addAll(rb1,rb10,rb100);
		root.getChildren().addAll(rb1,rb10,rb100);
		
		displayFunds.setLayoutX(labelPosX[0]);
		displayFunds.setLayoutY(labelPosY[0]);
		displayFunds.setUnderline(true);
		
		root.getChildren().add(displayFunds);
		
		displayBet.setLayoutX(labelPosX[1]);
		displayBet.setLayoutY(labelPosY[1]);
		displayBet.setUnderline(true);
		
		root.getChildren().add(displayBet);
	}
	
	//Assigns five cards from the deck to the enemy player's hand
	public static void setEnemyHand()
	{
		for(int i = 0; i < enemyHand.length; i++)
		{
			enemyHand[i] = useDeck.remove((int)(Math.random()*useDeck.size()));
		}
	}
	
	//Returns true if the player won, false if lost or tied
	public static boolean playerWon()
	{
		WinCon player = new WinCon(hand);
		WinCon cp = new WinCon(enemyHand);
		player.checkWinCon();
		cp.checkWinCon();
		return WinCon.compare(player,cp);
	}
	
	//Inserts the cards into the whole deck and the use deck
	public static void initializeDecks()
	{
		for(int i = 1; i <= 52; i++)
		{
			int div = (i-1)/13;
			switch(div)
			{
			case 0:
				useDeck.add(new PlayingCard(i+".png","b1fv.png","b1fh.png",(i-1)%13+1,"Spades"));
				break;
			case 1:
				useDeck.add(new PlayingCard(i+".png","b2fv.png","b2fh.png",(i-1)%13+1,"Hearts"));
				break;
			case 2:
				useDeck.add(new PlayingCard(i+".png","b2fv.png","b2fh.png",(i-1)%13+1,"Diamonds"));
				break;
			case 3:
				useDeck.add(new PlayingCard(i+".png","b1fv.png","b1fh.png",(i-1)%13+1,"Clubs"));
				break;
			}
		}
		fullDeck.addAll(useDeck);
	}
	
	//Sets what cards are in a player's hand; removes old cards if the player pressed deal or if the player pressed draw and they aren't held; handles animations
	public static void setHand(Group root, boolean withHold)
	{
		//Removes cards from the player's hand
		try {
			for(int i = 0; i < 5; i++) 
			{
				if(!holds[i].isSelected() || !withHold)
				{
					//The front of a card in the player's hand
					Node cardFront = cardFronts[i];
					//Sets the animation so the card front will rotate a certain way
					RotateTransition rotator = new RotateTransition(Duration.millis(500), cardFront); 
				    rotator.setAxis(Rotate.Y_AXIS);
				    rotator.setFromAngle(0);
				    rotator.setToAngle(90);
				    rotator.setInterpolator(Interpolator.LINEAR);
				    rotator.setCycleCount(1);
				    rotator.play();
				    
				    //The back of a card in the player's hand
				    Node cardBack = cardBacks[i]; // Defines a Node using the cardBacks list so that the reference value is the same as the cardback added into the pane.
				    
				    rotator = new RotateTransition(Duration.millis(500), cardBack);
				    rotator.setDelay(Duration.millis(500));
				    rotator.setAxis(Rotate.Y_AXIS);
				    rotator.setFromAngle(90);
				    rotator.setToAngle(180);
				    rotator.setInterpolator(Interpolator.LINEAR);
				    rotator.setCycleCount(1);
				    rotator.play();
				    
				    //Defines the path that we will give to our path transition to animate the card
					Path path = new Path(); 
				    path.getElements().add(new MoveTo(cardBacks[i].getX()+36,cardBacks[i].getY()+48));
				    path.getElements().add(new LineTo(cardBacks[i].getX()+36,2000));
				   
				    //Animates the movement of the card
				    PathTransition pathTransition = new PathTransition();
				    pathTransition.setDuration(Duration.millis(1000));
				    pathTransition.setDelay(Duration.millis(1000));
				    pathTransition.setNode(cardBack);
				    pathTransition.setPath(path);
				    pathTransition.play();
				}
			}
		} catch (Exception e) {
			
		}
		
		//Adds cards to the player's hand
		for(int i = 0; i < 5; i++)
		{
			if(!holds[i].isSelected() || !withHold)
			{
				
				hand[i] = useDeck.remove((int)(Math.random()*useDeck.size()));
				
				try {
					//The front image of the card to be added to the player's hand
					ImageView displayFront = new ImageView(new Image(new FileInputStream(hand[i].frontImage)));
					displayFront.setX(cardPositionsX[i]);
					displayFront.setY(cardPositionsY[i]);
					cardFronts[i] = displayFront;
					//The back image of the card to be added to the player's hand
					ImageView displayBack = new ImageView(new Image(new FileInputStream(hand[i].backImageV)));
					displayBack.setX(cardPositionsX[i]);
					displayBack.setY(cardPositionsY[i]);
					displayBack.setRotationAxis(Rotate.Y_AXIS);
					displayBack.setRotate(90);
					cardBacks[i] = displayBack;
					
					root.getChildren().addAll(displayFront,displayBack);
					
				} catch (FileNotFoundException e){
					
				}
			}
		}
		
		//Animates the entrance of the new cards
		for(int i = 0; i < 5; i++) 
		{
			if(!holds[i].isSelected() || !withHold)
			{
				//The front image of the new card to be added to the player's hand
				Node card = cardFronts[i];
				//The path we want the card to take
				Path path = new Path();
				path.getElements().add(new MoveTo(cardFronts[i].getX()+36,-1000));
				path.getElements().add(new LineTo(cardBacks[i].getX()+36,cardBacks[i].getY()+48));
	        
				//How the card takes that path
				PathTransition pathTransition = new PathTransition();
				pathTransition.setDuration(Duration.millis(2000));
				pathTransition.setNode(card);
				pathTransition.setPath(path);
	        	pathTransition.play();
			}
		}
	}
	
}
//This class assigns the value of a poker hand and is used to determine the winning hand
class WinCon
{
	//Whether or not the calling hand has a royal flush hand
	public boolean royalFlush;
	//Whether or not the calling hand has a straight flush hand
	public boolean straightFlush;
	//Whether or not the calling hand has a four of a kind hand
	public boolean fourOfAKind;
	//Whether or not the calling hand has a full house hand
	public boolean fullHouse;
	//Whether or not the calling hand has a flush hand
	public boolean flush;
	//Whether or not the calling hand has a straight hand
	public boolean straight;
	//Whether or not the calling hand has a three of a kind hand
	public boolean threeOfAKind;
	//Whether or not the calling hand has a two pair hand
	public boolean twoPair;
	//Whether or not the calling hand has a one pair hand
	public boolean onePair;
	//Whether or not the calling hand has a high card hand
	public boolean highCard;

	//The following variables are important for tie-breakers between hands of the same type
	//The highest card in a flush
	public int height;
	//The card rank that appears three times in a full house or a three of a kind
	public int tripletValue;
	//The pair that appears in a full house
	public int fullOf;
	//The highest value pair of cards in a one pair or two pair
	public int firstPair;
	//The second highest pair of cards in a two pair
	public int secondPair;
	//The highest ranking individual card not associated with the special part of a hand (the last card in a four of a kind, for example)
	public int firstKicker;
	//The second highest ranking individual card not associated with the special part of a hand
	public int secondKicker;
	//The third highest ranking individual card not associated with the special part of a hand
	public int thirdKicker;
	//The fourth highest ranking individual card not associated with the special part of a hand
	public int fourthKicker;
	//The fifth highest ranking individual card not associated with the special part of a hand
	public int fifthKicker;
	/*What type hand the given hand is and how valuable it is compared to others
	 * 10 - royal flush
	 *  9 - straight flush
	 *  8 - four of a kind
	 *  7 - full house
	 *  6 - flush
	 *  5 - straight
	 *  4 - three of a kind
	 *  3 - two pair
	 *  2 - one pair
	 *  1 - high card
	 */
	public int handValue;
	
	//The numeric representation of the cards in a hand (rank and suit)
	public int[] values;
	//The ranks of the cards in the hand
	public int[] ranks;
	//How many times each rank appears in the hand
	public int[] rankFrequency;
	
	//The hand to be considered
	public PlayingCard[] hand;
	
	//Constructor: assigns the hand to be considered to the passed hand
	public WinCon(PlayingCard[] hand)
	{
		this.hand = hand;
	}
	
	//Sets the values necessary to determine a winning hand to the calling object's hand
	public void checkWinCon()
	{
		values = new int[5];
		ranks = new int[5];
		rankFrequency = new int[15];
		for(int i = 0; i < hand.length; i++)
		{
			//The value of the card in position i of the hand
			int value = hand[i].value;
			
			//Counts Aces as having a value of 14 while it is the first card in the suit in the value system
			if(value == 1 || value == 14 || value == 27 || value == 40)
			{
				values[i] = value+13;
			}
			else
			{
				values[i] = value;
			}
			
			ranks[i] = values[i]%14;
			if(ranks[i] == 0)
			{
				ranks[i] += 14;
			}
			rankFrequency[ranks[i]]++;
		}
		Arrays.sort(values);
		Arrays.sort(ranks);
		
		handValue = -1;
		
		//The if statements below check if the hand qualifies as a special hand in descending value order and exits the method when it is determined to be one of them
		
		royalFlush();
		if(handValue != -1)
		{
			return;
		}
		
		straightFlush();
		if(handValue != -1)
		{
			return;
		}
		
		fourOfAKind();
		if(handValue != -1)
		{
			return;
		}
		
		fullHouse();
		if(handValue != -1)
		{
			return;
		}
		
		flush();
		if(handValue != -1)
		{
			return;
		}
		
		straight();
		if(handValue != -1)
		{
			return;
		}
		
		threeOfAKind();
		if(handValue != -1)
		{
			return;
		}
		
		twoPair();
		if(handValue != -1)
		{
			return;
		}
		
		onePair();
		if(handValue != -1)
		{
			return;
		}
		
		highCard();
	}
	
	//Determines if a poker hand is a royal flush
	public void royalFlush()
	{
		if((values[0]==10 && values[1]==11 && values[2]==12 && values[3]==13 && values[4]==14) || (values[0]==23 && values[1]==24 && values[2]==25 && values[3]==26 && values[4]==27) || (values[0]==36 && values[1]==37 && values[2]==38 && values[3]==39 && values[4]==40) || (values[0]==49 && values[1]==50 && values[2]==51 && values[3]==52 && values[4]==53))
		{
			royalFlush = true; 
			handValue = 10;
			//Play testing and debugging
			//System.out.println("Royal Flush");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a straight flush
	public void straightFlush()
	{
		if((values[0] <= 10 || values[0] <= 23 || values[0] <= 36 || values[0] <= 49) && (values[1] == values[0]+1 && values[2] == values[0]+2 && values[3] == values[0]+3 && values[4] == values[0]+4))
		{
			straightFlush = true;
			height = ranks[4];
			handValue = 9;
			//Play testing and debugging
			//System.out.println("Straight Flush");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a four of a kind
	public void fourOfAKind()
	{
		int tempKicker = 0;
		for(int i = 0; i < rankFrequency.length; i++)
		{
			if(rankFrequency[i] == 4)
			{
				fourOfAKind = true;
				handValue = 8;
				//Play testing and debugging
				//System.out.println("Four of a Kind");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
			if(rankFrequency[i] == 1)
			{
				tempKicker = i;
			}
		}
		if(fourOfAKind)
		{
			firstKicker = tempKicker;
		}
	}

	//Determines if a poker hand is a full house
	public void fullHouse()
	{
		int tempTripletValue = -1;
		int tempFullOf = -1;
		for(int i = 0; i < rankFrequency.length; i++)
		{
			if(rankFrequency[i] == 3)
			{
				tempTripletValue = i;
			}
			if(rankFrequency[i] == 2)
			{
				tempFullOf = i;
			}
		}
		if(tempTripletValue != -1 && tempFullOf != -1)
		{
			tripletValue = tempTripletValue;
			fullOf = tempFullOf;
			fullHouse = true;
			handValue = 7;
			//Play testing and debugging
			//System.out.println("Full House");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a flush
	public void flush()
	{
		int suitCount = 1;
		String suit = hand[0].suit;
		for(int i = 1; i < hand.length; i++)
		{
			if(hand[i].suit.equals(suit))
			{
				suitCount++;
			}
		}
		if(suitCount == 5)
		{
			flush = true;
			handValue = 6;
			height = ranks[4];
			//Play testing and debugging
			//System.out.println("Flush");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a straight
	public void straight()
	{
		if(ranks[1] == ranks[0]+1 && ranks[2] == ranks[0]+2 && ranks[3] == ranks[0]+3 && ranks[4] == ranks[0]+4)
		{
			straight = true;
			handValue = 5;
			height = ranks[4];
			//Play testing and debugging
			//System.out.println("Straight");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a three of a kind
	public void threeOfAKind()
	{
		int tempTripletValue = -1;
		int tempFirstKicker = -1;
		int tempSecondKicker = -1;
		for(int i = rankFrequency.length-1; i >= 0; i--)
		{
			if(rankFrequency[i] == 1)
			{
				if(tempFirstKicker == -1)
				{
					tempFirstKicker = i;
				}
				else
				{
					tempSecondKicker = i;
				}
			}
			else if(rankFrequency[i] == 3)
			{
				tempTripletValue = i;
			}
		}
		if(tempTripletValue != -1 && tempFirstKicker != -1 && tempSecondKicker != -1)
		{
			tripletValue = tempTripletValue;
			firstKicker = tempFirstKicker;
			secondKicker = tempSecondKicker;
			threeOfAKind = true;
			handValue = 4;
			//Play testing and debugging
			//System.out.println("Three of a Kind");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a two pair
	public void twoPair()
	{
		int tempFirstPair = -1;
		int tempSecondPair = -1;
		int tempTwoPairKicker = -1;
		for(int i = rankFrequency.length-1; i >= 0; i--)
		{
			if(rankFrequency[i] == 2)
			{
				if(tempFirstPair == -1)
				{
					tempFirstPair = i;
				}
				else
				{
					tempSecondPair = i;
				}
			}
			else if(rankFrequency[i] == 1)
			{
				tempTwoPairKicker = i;
			}
		}
		if(tempFirstPair != -1 && tempSecondPair != -1 && tempTwoPairKicker != -1)
		{
			firstPair = tempFirstPair;
			secondPair = tempSecondPair;
			firstKicker = tempTwoPairKicker;
			twoPair = true;
			handValue = 3;
			//Play testing and debugging
			//System.out.println("Two Pair");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a one pair
	public void onePair()
	{
		int tempPair = -1;
		int tempFirstKicker = -1;
		int tempSecondKicker = -1;
		int tempThirdKicker = -1;
		for(int i = rankFrequency.length-1; i >= 0; i--)
		{
			if(rankFrequency[i] == 2)
			{
				tempPair = i;
			}
			else if(rankFrequency[i] == 1)
			{
				if(tempFirstKicker == -1)
				{
					tempFirstKicker = i;
				}
				else if(tempSecondKicker == -1)
				{
					tempSecondKicker = i;
				}
				else
				{
					tempThirdKicker = i;
				}
			}
		}
		if(tempPair != -1 && tempFirstKicker != -1 && tempSecondKicker != -1 && tempThirdKicker != -1)
		{
			firstPair = tempPair;
			firstKicker = tempFirstKicker;
			secondKicker = tempSecondKicker;
			thirdKicker = tempThirdKicker;
			onePair = true;
			handValue = 2;
			//Play testing and debugging
			//System.out.println("One Pair");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	//Determines if a poker hand is a high card
	public void highCard()
	{
		firstKicker = ranks[4];
		secondKicker = ranks[3];
		thirdKicker = ranks[2];
		fourthKicker = ranks[1];
		fifthKicker = ranks[0];
		highCard = true;
		handValue = 1;
		//Play testing and debugging
		//System.out.println("High Card");/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	//returns true strictly if the first passed-hand beats the second passed-hand
	public static boolean compare(WinCon p1, WinCon p2)
	{
		if(p1.handValue > p2.handValue)
		{
			return true;
		}
		else if(p1.handValue == p2.handValue)
		{
			switch(p1.handValue)
			{
			case 9:
				if(p1.height > p2.height)
					return true;
				break;
			case 8:
				if(p1.firstKicker > p2.firstKicker)
					return true;
				break;
			case 7:
				if(p1.tripletValue > p2.tripletValue)
					return true;
				else if(p1.tripletValue == p2.tripletValue)
					if(p1.fullOf > p2.fullOf)
						return true;
				break;
			case 6:
				if(p1.height > p2.height)
					return true;
				break;
			case 5:
				if(p1.height > p2.height)
					return true;
				break;
			case 4:
				if(p1.tripletValue > p2.tripletValue)
					return true;
				else if(p1.tripletValue == p2.tripletValue)
					if(p1.firstKicker > p2.firstKicker)
						return true;
					else if(p1.firstKicker == p2.firstKicker)
						if(p1.secondKicker > p2.secondKicker)
							return true;
				break;
			case 3:
				if(p1.firstPair > p2.firstPair)
					return true;
				else if(p1.firstPair == p2.firstPair)
					if(p1.secondPair > p2.secondPair)
						return true;
					else if(p1.secondPair == p2.secondPair)
						if(p1.firstKicker > p2.firstKicker)
							return true;
				break;
			case 2:
				if(p1.firstPair > p2.firstPair)
					return true;
				else if(p1.firstPair == p2.firstPair)
					if(p1.firstKicker > p2.firstKicker)
						return true;
					else if(p1.firstKicker == p2.firstKicker)
						if(p1.secondKicker > p2.secondKicker)
							return true;
						else if(p1.secondKicker == p2.secondKicker)
							if(p1.thirdKicker > p2.thirdKicker)
								return true;
				break;
			case 1:
				if(p1.firstKicker > p2.firstKicker)
					return true;
				else if(p1.firstKicker == p2.firstKicker)
					if(p1.secondKicker > p2.secondKicker)
						return true;
					else if(p1.secondKicker == p2.secondKicker)
						if(p1.thirdKicker > p2.thirdKicker)
							return true;
						else if(p1.thirdKicker == p2.thirdKicker)
							if(p1.fourthKicker > p2.fourthKicker)
								return true;
							else if(p1.fourthKicker == p2.fourthKicker)
								if(p1.fifthKicker > p2.fifthKicker)
									return true;
				break;
			}
		}
		
		return false;
	}
}

//Holds the values associated with playing cards
class PlayingCard
{
	//The front of a playing card, shows color, value, and suit
	public String frontImage;
	//The vertical back of a playing card, blue or red
	public String backImageV;
	//The horizontal back of a playing card, blue or red
	public String backImageH;
	//The numerical value a card can have: 1(Ace)-13(King)
	public int value;
	//The name of the suit of a card
	public String suit;
	
	//Initializes the values of a playing card
	public PlayingCard(String frontImage, String backImageV, String backImageH, int value, String suit)
	{
		this.frontImage = frontImage;
		this.backImageV = backImageV;
		this.backImageH = backImageH;
		this.value = value;
		this.suit = suit;
	}
}