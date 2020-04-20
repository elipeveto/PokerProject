/*
Eli Peveto
COSC 1174
4/2/2020
Ch 16 JavaFX UI Controls and Multimedia
 */
package sample;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Poker3 extends Application
{
    public ToggleButton [] holding = new ToggleButton[5];//This array will hold all the hold buttons.
    public ImageView [] hand = new ImageView[5];//This array will hold the hand of the player.
    private int handCheck[][] = new int[5][2];//This array will hold the players hand converted into numbers for calculating the best hand they have.
    public ArrayList<ImageView> deck = new ArrayList<ImageView>();//This will hold the entire deck in images.
    public ArrayList<ImageView> cloneDeck = new ArrayList<ImageView>();//This will be used to clone the deck before it is shuffled.
    public int drawNumber = 0;//This keeps track which card is being drawn from the deck.
    private static ImageView[] cardBacks = new ImageView[5];//This holds the card backs of the cards.
    private Label instruction = new Label();//This label will be used to give the user instructions and will change often to the situation.
    private int betNumber;//This keeps track of how much is bet by the player.
    private Label bet = new Label("Your Current Bet: $" + betNumber);//This label is used to display the bet amount by the user.
    private int totalCash = 200;//This keeps track of the users cash they can use to bet.
    private int gameStage = 0;//this variable keeps track if the player is in the betting stage or holding cards stage.
    private int times = 1;//This variable is used to insure that the player cannot hold cards that have not shown up yet.
    private static int numberOfPairs = 0;//This keeps track how many pairs the user has in their hand.

    //The next 9 variables are for keeping track of what the winning hand is the player gets if any.
    private static boolean pair;
    private static boolean twoPair;
    private static boolean threePair;
    private static boolean fourPair;
    private static boolean straight;
    private static boolean flush;
    private static boolean fullHouse;
    private static boolean straightFlush;
    private static boolean royalFlush;
    public void start(Stage primaryStage) throws Exception
    {
        BorderPane background = new BorderPane();//This is the background pane to hold everything.
        makeDeck(deck);//makes a deck and shuffles it

        GridPane pane = new GridPane();//This will hold the cards, hold buttons, and the deal button.
        pane.setHgap(20);//This gives some space between the horizontal coordinates.
        pane.setVgap(10);//This gives some space between the vertical coordinates.
        Button deal = new Button("Deal");//This is the deal button.
        pane.add(deal,0, 0);//adds deal button to the top left of pane.

        //next 5 lines make the HOLD buttons to hold the cards the player wants to keep.
        ToggleButton hold1 = new ToggleButton("HOLD");
        ToggleButton hold2 = new ToggleButton("HOLD");
        ToggleButton hold3 = new ToggleButton("HOLD");
        ToggleButton hold4 = new ToggleButton("HOLD");
        ToggleButton hold5 = new ToggleButton("HOLD");

        //the next 5 lines add the hold buttons to an array.
        holding[0] = hold1;
        holding[1] = hold2;
        holding[2] = hold3;
        holding[3] = hold4;
        holding[4] = hold5;

        //next 2 lines add the HOLD buttons to the pane on row 2.
        for(int i = 0;i < 5;i++)
        pane.addRow(1, (Node) holding[i]);

        //the next 3 lines make the 3 betting buttons for 1, 10, and 100.
        Button one$ = new Button("$1");
        Button ten$ = new Button("$10");
        Button hundered$ = new Button("$100");

        HBox betGrid = new HBox(20);//This makes an HBox with spacing 20 to hold the bet buttons and a label to display player cash.
        Label bank = new Label("Your money: $" + totalCash);//makes the label to display the players cash, and the next 2 lines set its font, boldness, color, size, and background color.
        bank.setFont(Font.font("Times New Roman", FontWeight.BOLD,20));
        bank.setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));

        //next 2 lines set the bet buttons font, boldness, size, and color.
        bet.setFont(Font.font("Times New Roman",FontWeight.BOLD,20));
        bet.setTextFill(Color.WHITE);

        VBox betHolder = new VBox(5);//makes a VBox with a bit of spacing to display instructions and the users bet.
        betHolder.getChildren().add(0,instruction);//adds instruction to the top of the VBox.
        betHolder.getChildren().add(1, bet);//adds the players current bet display to the bottom of the VBox.
        betHolder.setAlignment(Pos.CENTER);//sets betHolder to center alignment.
        pane.setAlignment(Pos.CENTER);//sets betHolder to center alignment.

        background.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));//sets the background to black.
        pane.setBackground(new Background(new BackgroundFill(Color.BROWN, CornerRadii.EMPTY, Insets.EMPTY)));//sets just the center pane background to red.
        Label options = new Label("Bet Options: ");//makes a label to tell the user what the bet buttons are for.
        options.setTextFill(Color.WHITE);//sets the options label to white.
        options.setFont(Font.font("", FontWeight.BOLD, 15));//makes options bold and size 15.

        //next 5 lines add the bank label, the options label, and the 3 bet buttons to the bottom pane of the boarder pane.
        betGrid.getChildren().add(0,bank);
        betGrid.getChildren().add(1, options);
        betGrid.getChildren().add(2,one$);
        betGrid.getChildren().add(3,ten$);
        betGrid.getChildren().add(4,hundered$);

        betGrid.setAlignment(Pos.CENTER);//sets betHolder to center alignment.

        instruction.setText("Please make a bet, then press the deal button to start!");//tells the user what to do.
        instruction.setFont(Font.font("Times New Roman", 20));//sets the instructions font and size.
        instruction.setTextFill(Color.RED);//sets instructions color.
        background.setCenter(pane);//sets the game to the center.
        background.setTop(betHolder);//sets instructions and current bet to the top.
        background.setBottom(betGrid);//sets bet buttons to the bottom, as well as player money count.

        deal.setOnAction(e -> dealing(primaryStage, pane, bank));//if the deal button is ever pressed run dealing method.
        //next 3 lines if that button is pressed run its method.
        one$.setOnAction(event -> oneBetting(bank));
        ten$.setOnAction(event -> tenBetting(bank));
        hundered$.setOnAction(event -> hundredBetting(bank));

        // Create a scene and place it in the stage
        Scene scene = new Scene(background,550,250);
        primaryStage.setTitle("Poker"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

    }

    private void dealing(Stage primaryStage, GridPane pane, Label bank)//This method deals with dealing of cards.
    {
        if(betNumber != 0)//If the user has placed a bet then continue only.
        while (times == 1) //This loop keeps the user from holding cards before they should do so.
        {
            for (int i = 0; i < 5; i++)
                holding[i].setSelected(false);
            times++;
        }

        try//the try catch block is required for the animations.
        {
            if(betNumber != 0) //If the user has placed a bet then continue only.
            {
                if(gameStage % 2 == 0)//if they pressed deal once subtract their bet from their total money.
                totalCash -= betNumber;
                bank.setText("Your Money: " + totalCash);//display their current cash total.
                exit(hand, pane, bank);//trigger the exit method.
            }
            else//if the user has not made a bet then teh instruction label changes color and says to make a bet.
                {
                instruction.setText("Please make a bet first!");
                instruction.setTextFill(Color.GREEN);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void resetHandWins()//This method resets all the possible hands to false.
    {
        this.pair = false;
        this.twoPair = false;
        this.threePair = false;
        this.fourPair = false;
        this.straight = false;
        this.flush = false;
        this.fullHouse = false;
        this.straightFlush = false;
        this.royalFlush = false;
    }

    private void winOrLose(boolean threePair, GridPane pane, ImageView[] hand, Label bank) throws Exception //This method runs all the methods I have to check what hand the user has. Then tells if the user wins or not.
    {
        isPair();
        isFullHouse();
        isFlush();
        isStraight();
        isStraightFush();
        isroyalFlush();

      if((pair || twoPair || this.threePair || fourPair || fullHouse || flush || straight || royalFlush || straightFlush) == true)//If the user has any "winning hand" for poker then run the winningPayment method.
        {
            winningPayment(hand, pane, bank);
            System.out.println("WINNERR");
        }
        else//if the user has nothing then run the losingPayment method.
            {
            loseingPayment(hand, pane, bank);
            System.out.println("LOSERRR");
        }
    }
    private void winningPayment(ImageView[] hand, GridPane pane, Label bank) throws Exception //if its a winning hand this method doubles the bet placed before and adds that to the players cash total.
    {
        totalCash += (betNumber * 2);
        betNumber = 0;//sets the bet back to 0.
        instruction.setText("Congrats! Enjoy your reward! Bet to play again!");//tells the user they won.
        bet.setText("Your Bet: $" + betNumber);//displays the bet back to 0.
        bank.setText("Your Money: $" + totalCash);//displays the users new cash total.
        times = 1;//resets the times variable to 1 so when the next round starts the player cannot hold any cards from last round.
        resetHandWins();//runs resetHandWins method.
    }
    private void loseingPayment(ImageView[] hand, GridPane pane, Label bank) throws Exception//if its a losing hand this method just sets the bet back to 0 and keeps the users cash total at the subtracted amount.
    {
        betNumber = 0;
        instruction.setText("Better luck next time! Bet to play again!");//Tells the player that they lost.
        bet.setText("Your Bet: $" + betNumber);//displays the bet back to 0.
        times = 1;//resets the times variable to 1 so when the next round starts the player cannot hold any cards from last round.
        if(totalCash == 0)//Informs the user that they cannot play again with no money to bet.
            instruction.setText("Looks like your out of cash what a shame...");
    }

    private void isPair()//this method checks for one pair, 2 pairs, 3 pairs, and 4 pairs.
    {
        ArrayList<Integer> pair = new ArrayList<>();//makes an array list to hold the users hand from handCheck.
        for(int i = 0;i < 5;i++)//puts the hand numbers into the array list.
            pair.add(handCheck[i][0]);
        Collections.sort(pair);//sorts the hand in numerical order.

        int count = 0;//resets count to 0 every time this method is called.
        numberOfPairs = 0;//resets numberOfPairs to 0 every time this method is called.
        int x = 0;//resets x to 0 every time this method is called.
        int next = 0;//resets next to 0 every time this method is called.

        /*
        The following dual 4 loop and
         */
        for(x = 0;x<5;x++)//card being checked each time.
        for(int i = next;i < 5;i++)//loops through each card in hand.
        {
            if(pair.get(x) == pair.get(i))//checks if the x position equals the i position.
            {
                count++;//if true add to count(which is the number of times that shows up in the hand.
                if(i == 4 && x <= 3)//if there is at leas 2 of the same card at the end of the hand then this method runs the switch statement one more time. Otherwise it wont register the winning hand type.
                {
                    switch (count) //This switch statement works the same as the one bellow this one.
                    {
                        case 1:
                            {
                            next = i;
                            x = next-1;
                            count = 0;
                        }break;
                        case 2:
                        {
                            this.numberOfPairs += 1;
                            count = 0;
                            next = i;
                            x = next-1;

                        }break;
                        case 3:
                        {
                            next = i;
                            x = next-1;
                            this.threePair = true;
                            count = 0;
                        }break;
                        case 4:
                        {
                            next = i;
                            x = next-1;
                            fourPair = true;
                            count = 0;
                            break;
                        }
                    }
            }
            }

            else//if position x does not = position i then that means that count equals the number of times it shows up in the hand.
                {
                    switch (count)//this deals with how many times the sertain card shows up in the hand.
                    {
                        case 1://if there is only one copy of the card then start the loop of x and i at this new cards position that did not match the previous card.
                            {
                            next = i;
                            x = next-1;//since the first loop adds x before the loop runs again I subtract one to keep it in the right spot.
                            count = 0;
                        }break;
                        case 2://if there is 2 of a kind in the hand then it adds one to the variable numberOfPairs(in case there will be a 2 pair), then starts the loop of x and i at this new cards position that did not match the previous card.
                        {
                            this.numberOfPairs += 1;
                            count = 0;
                            next = i;
                            x = next-1;

                        }break;
                        case 3://If there are 3 of a kind then this method starts the loop of x and i at this new cards position that did not match the previous card, and sets the variable threePair to true since there is a 3 of a kind.
                        {
                            next = i;
                            x = next-1;
                            this.threePair = true;
                            count = 0;
                        }break;
                        case 4://If there are 4 of a kind then this method starts the loop of x and i at this new cards position that did not match the previous card, and sets the variable fourPair to true since there is a 4 of a kind.
                        {
                            next = i;
                            x = next-1;
                            fourPair = true;
                            count = 0;
                            break;
                        }
                    }
                    break;
                }
        }
        if(this.numberOfPairs == 1)//if there was only one pair then set pair equal to true.
            this.pair = true;
        if(this.numberOfPairs == 2)//if there was 2 pairs then set twoPair equal to true instead.
            twoPair = true;
    }
    private void isFullHouse()//if the user has a 3 pair and a pair then they have a full house so this sets fullHouse equal to true.
    {
        if(threePair == true && pair == true)
            fullHouse = true;
    }

    private void isFlush()//The second row of the 2D array handCheck is the suit of the cards. If they are all equal then the player has a flush, so this method sets flush equal to true.
    {
        //checks if all are the same suit.
        if(handCheck[0][1] == handCheck[1][1] && handCheck[0][1] == handCheck[2][1] && handCheck[0][1] == handCheck[3][1] && handCheck[0][1] == handCheck[4][1]) {
            this.flush = true;
        }
    }

    private void isStraight()//checks if the hand is a straight.
    {
        //the next 4 lines make an array list, set it equal to the players hand, and sorts it.
        ArrayList<Integer> straight = new ArrayList<>();
        for(int i = 0;i < 5;i++)
            straight.add(handCheck[i][0]);
        Collections.sort(straight);

        //if the 5 cards are in numerical order then the next 2 lines set straight = to true.
        if(straight.get(0)+1 == straight.get(1) && straight.get(1)+1 == straight.get(2) && straight.get(2)+1 == straight.get(3) && straight.get(3)+1 == straight.get(4))
            this.straight = true;
    }

    private void isroyalFlush()//this checks for a royal flush.
    {
        //the next 4 lines make an array list, set it equal to the players hand, and sorts it.
        ArrayList<Integer> straight = new ArrayList<>();
        for(int i = 0;i < 5;i++)
            straight.add(handCheck[i][0]);
        Collections.sort(straight);

        if(this.straight == true && this.flush == true)//checks if the hand is a straight and a flush.
            if(straight.get(0) == 8 && straight.get(0)+1 == straight.get(1) && straight.get(1)+1 == straight.get(2) && straight.get(3)+1 == straight.get(4))//if it is a straight flush and the numbers are the greatest 5 numbers in the deck then it is a royal flush.
            royalFlush = true;//sets royalFlush equal true if it meets the requirements.
    }
    private void isStraightFush()//this method sets straightFlush equal to true if the hand is a flush and a straight.
    {
        if(this.flush == true && this.straight == true)
            straightFlush = true;
    }

    private void oneBetting(Label bank)//this deals with the $1 betting button.
    {
        if(gameStage % 2 == 0)//if they are in the betting stage of the game continue.
        {
            if (betNumber + 1 <= totalCash) //if they have the money left to bet continue.
            {
                instruction.setText("Please make a bet, then press the deal button to start!");//tells them what to do.
                betNumber += 1;//adds one to the bet.
                bet.setText("Your Current Bet: $" + betNumber);//adds to the bet number display.
            } else//if they dont have that much money to bet this tells them that.
                instruction.setText("You don't have that much to bet!");
        }
        else//if they are past the betting stage of the game this tells them that.
            instruction.setText("You cannot bet again. Press deal when ready.");
    }
    private void tenBetting(Label bank)//this deals with the $10 betting button.
    {
        if(gameStage % 2 == 0) //if they are in the betting stage of the game continue.
             {
            if (betNumber + 10 <= totalCash) //if they have the money left to bet continue.
            {
                instruction.setText("Please make a bet, then press the deal button to start!");//tells them what to do.
                betNumber += 10;//adds ten to the bet.
                bet.setText("Your Current Bet: $" + betNumber);//adds to the bet number display.
            } else//if they dont have that much money to bet this tells them that.
                instruction.setText("You don't have that much to bet!");
        }
        else//if they are past the betting stage of the game this tells them that.
            instruction.setText("You cannot bet again. Press deal when ready.");
    }

    private void hundredBetting(Label bank) //this deals with the $100 betting button.
    {
        if(gameStage % 2 == 0)//if they are in the betting stage of the game continue.
        {
        if (betNumber + 100 <= totalCash) //if they have the money left to bet continue.
        {
            instruction.setText("Please make a bet, then press the deal button to start!");//tells them what to do.
            betNumber += 100;//adds 100 to the bet.
            bet.setText("Your Current Bet: $" + betNumber);//adds to the bet number display.
        } else//if they dont have that much money to bet this tells them that.
            instruction.setText("You don't have that much to bet!");
        }
        else//if they are past the betting stage of the game this tells them that.
            instruction.setText("You cannot bet again. Press deal when ready.");
    }

    private void winCheck()//This method turns the image hand into numbers for calculations.
    {
        for(int x = 0;x < 5;x++)//this loops through each card in the hand.
        {
            for(int i = 0; i < 52;i++)//this loops through the whole original deck before it was shuffled.(the clone deck)
                {
                    if(hand[x].equals(cloneDeck.get(i)))//if the card in the hand equals a card from the original deck then it figures out what the card is based on position i of the deck.
                    {
                        //(the cards in the original deck where in order from 2 all the way up to the high card ace)

                        handCheck[x][0] = (int) i % 13;//with % 13 it figures out which card number it is and sets the hand to that card.
                        if(i >= 0 && i <= 12)//the first 13 were spades.
                        {
                            handCheck[x][1] = 1;//1 means spades.
                        }
                        else if(i >= 13 && i <= 25)//the second 13 were hearts.
                        {
                            handCheck[x][1] = 2;//2 means hearts.
                        }
                        else if(i >= 26 && i <= 38)//the third 13 were diamonds.
                        {
                            handCheck[x][1] = 3;//3 means Diamonds.
                        }
                        else if(i >= 39 && i <= 51)//the final 13 were clubs.
                        {
                            handCheck[x][1] = 4;//4 means clubs.
                        }
                    }
                }
        }
    }
    private void makeDeck(ArrayList<ImageView> deck) throws FileNotFoundException//this makes the deck, clones it, and shuffles the deck.
    {
        deck.clear();//this clears deck so later on if I run this it clears the old one away.
        for(int i = 1; i <= 52;i++)//adds all the image cards to the deck.
            deck.add(new ImageView(new Image(new FileInputStream("images/card/"+ i +".png"))));

        cloneDeck = (ArrayList<ImageView>) deck.clone();//clones the deck so I can have a copy in order to reference with the method winCheck.

        Collections.shuffle(deck);//shuffles the deck to play with.

        for(int i = 0;i < 5;i++)//sets the card back for the deck.
        {
            cardBacks[i] = new ImageView(new Image(new FileInputStream("images/card/b1fv.png")));
        }
    }

    private void addNewHand(ImageView[] hand, GridPane pane, Label bank) throws Exception//This adds a new hand to the player.
    {
        removeChildren(hand, pane);//removes old hand if there is one.
        for(int i = 0; i < 5;i++)//this adds the new cards to the players hand.
        {
            if(holding[i].isSelected())//if the hold button was pressed on this card then exit the loop(doesnt change the card).
                continue;
            if(drawNumber == 54)//stops if it runs out of cards to prevent the game from crashing.
                continue;
            hand[i] = deck.get(drawNumber);//sets the hand position equal to the card in the deck equal to draw number.
            pane.add(deck.get(drawNumber++), i ,2);//adds one to draw number after setting the image equal to the cars position in the pane of the game.
            cardBacks[i].setRotationAxis(Rotate.Y_AXIS);//sets the card to rotate along the y axis.
            cardBacks[i].setRotate(90);//rotates it 90 degrees for the flip look.
            pane.add(cardBacks[i], i, 2);//sets displays the card back to the cards.
        }
        enter(hand, pane);//runs the enter method for animation.
        if (gameStage % 2 == 0) //if the player was on the betting stage continue.
        {
            gameStage++;//add one to gameStage so next dealing click it knows to go the the next game stage.
            instruction.setText("Press The Hold Button on cards you wish to keep. Then press deal.");//tells the user what to do.
        } else//if the player was on the holding stage see if they won and add one to gameStage so next dealing click it knows to go the the next game stage.
            {
            gameStage++;
            winCheck();
            winOrLose(threePair, pane, hand, bank);
        }
    }

    private void removeChildren(ImageView[] hand, GridPane pane) throws Exception //This method clears the old hand of cards.
    {
        for(int x = 0;x<5;x++)
        {
            if(holding[x].isSelected())//if the card is held dont discard it.
                continue;
            pane.getChildren().remove(hand[x]);//remove that card.
            pane.getChildren().remove(cardBacks[x]);//removes its back.
        }
    }

    private void enter(ImageView[] hand, GridPane pane) throws Exception //This runs the enter animation
    {

        for(int x = 0;x<5;x++) {
            if(holding[x].isSelected())//if the hold button was pressed on this card then skip this loop.
                continue;

            Node card = hand[x];//Creates a node to be used within the PathTransition
            Path path = new Path();//Defines a Path for the PathTransition
            path.getElements().add(new MoveTo(hand[x].getX()+40,-300));//defines where the path goes to.
            path.getElements().add(new LineTo(cardBacks[x].getX()+40,cardBacks[x].getY()+50));//defines the line to follow.

            PathTransition pathTransition = new PathTransition();//Defines a new Path Transition that will be used for having cards slide onto the screen.
            pathTransition.setDuration(Duration.millis(1000));//how long it will take.
            pathTransition.setNode(card);//defines which card will go along this path transition.
            pathTransition.setPath(path);//defines the path.
            pathTransition.play();//plays it.
        }
    }

    private void exit(ImageView[] hand, GridPane pane, Label bank) throws Exception //this method does the exit animation.
    {

        int i = 0;//this is used to insure the animation happens once.

        if(holding[0].isSelected() && holding[1].isSelected() && holding[2].isSelected() && holding[3].isSelected() && holding[4].isSelected()) //If every button is held then this tells them if they win or lose and runs those methods.
        {
            if (gameStage % 2 == 0) {
                gameStage++;
                instruction.setText("Press The Hold Button on cards you wish to keep. Then press deal.");
            } else {
                gameStage++;
                winCheck();
                winOrLose(threePair, pane, hand, bank);
            }
        }
        for(int x=0;x<5;x++) {


            if(holding[x].isSelected())
            {
                continue;}
            else {
                Node card = hand[x];//Defines a Node using the imageview list so that the reference value is the same as the card in the pane.
                RotateTransition rotator = new RotateTransition(Duration.millis(1000), card); //Rotator Animation that will be used for flipping the card over.
                rotator.setAxis(Rotate.Y_AXIS);
                rotator.setFromAngle(0);
                rotator.setToAngle(90);
                rotator.setInterpolator(Interpolator.LINEAR);
                rotator.setCycleCount(1);

                if(drawNumber != 0)//This helps remove the delay for the first hand
                rotator.play();

                Node card2 = cardBacks[x]; // Defines a Node using the cardBacks list so its reference is the same as the card back added into the pane.
                rotator = new RotateTransition(Duration.millis(1000), card2);
                rotator.setDelay(Duration.millis(400));
                rotator.setAxis(Rotate.Y_AXIS);
                rotator.setFromAngle(180);
                rotator.setToAngle(0);
                rotator.setInterpolator(Interpolator.LINEAR);
                rotator.setCycleCount(1);

                if(drawNumber != 0)//This helps remove the delay for the first hand
                rotator.play();

                Path path = new Path(); //defines the path for our pathtransition later.
                path.getElements().add(new MoveTo(cardBacks[x].getX() + 40, cardBacks[x].getY() + 50));
                path.getElements().add(new LineTo(cardBacks[x].getX() + 40, 4000));

                PathTransition pathTransition = new PathTransition(); // a PathTransition Animation used for pulling the flipped over cards off the screen.
                if(drawNumber != 0) //This helps remove the delay for the first hand
                {
                    pathTransition.setDuration(Duration.millis(1200));
                    pathTransition.setDelay(Duration.millis(1200));
                }
                pathTransition.setNode(card2);
                pathTransition.setPath(path);
                pathTransition.play();

                if (i == 0) //This keeps the loop from doing this part 5 times. Now it only happens once.
                {
                    pathTransition.setOnFinished(e -> //This adds a new hand after the exit animation finishes.
                    {
                        try {

                            addNewHand(hand, pane, bank);//runs addNewHand

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                    i++;
                }
            }
    }
    }
}