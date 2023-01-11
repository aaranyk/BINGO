/*[BingoGameFinal.java]
 * This program runs a game of BINGO, a chance game.
 * @author Aarany Kugendran
 * @version 1.0, 2021-06-01
*/

//Insert imports that are used throughout the program
//Graphics & GUI imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

//File & sound imports
import java.io.File;
import javax.sound.sampled.*;

//Scanner(user input) and Random gen imports
import java.util.Scanner;
import java.util.Random;

public class BingoGameFinal extends JFrame implements ActionListener{
  
  //Create labels and panels that will be implemented to the screen
  private JFrame bingoCardFrame;
  private JLabel title;
  public JLabel announcerLabel;
  public JLabel counterLabel;
  public JLabel errorLabel;
  public JLabel winningLabel;
  private JPanel controlPanel;
  private JPanel bingoPanel;
  
  //Create scanner and random generator
  Scanner input = new Scanner(System.in);
  Random random = new Random();
  
  /****************** CLASS VARIABLES********************/
  //Declare main variables used throughout the program
  int bingoNumber = 0;
  int numOfTries = 0;
  
  //Ask user to enter the desired bingo card size. Ex. 4 for 4X4 OR 6 for 6X6: ");
  int bingoSize = input.nextInt();
  
  //Create public variables to be used in the GUI
  //This 2D array variable is created to store the bingo card numbers
  public int[][] bingoCardArray = new int[bingoSize][bingoSize];
  
  //Create bingoCard through JButtons(buttons) through user inputted size
  JButton buttons[] = new JButton[bingoSize*bingoSize];
  
  //Create 2D array var to locate the positions where user finds the # on the card(this is used through the highlight method)
  public boolean[][] highlightedPositions = new boolean[bingoSize][bingoSize];
  
  //Create variable to showcase the fixed range(In this code, I set it to 101)
  public int randomNumberRange = 121;  
  
  
  /******BingoGameFinal - Setups up the Window and Starts displaying it *****/
  public BingoGameFinal(){
    super("My Game");    
    screenBuilder();
  }
  /****** End of BingoGameFinal ******/
  
  /*****Main method*****/
  public static void main(String[] args){
    BingoGameFinal bingoControl = new BingoGameFinal();
    bingoControl.displayBingoCard(); 
  }
  /***** End of main *****/
  
  /*****Construction method - Constructing the BINGO CARD + BUTTONS onto the Panels/Screen*****/
  private void displayBingoCard(){
    //Display title of the game
    title.setText("BINGO!");
    title.setFont(new Font("Serif", Font.PLAIN, 28));
    
    //Create a button called "Next Number" to identify the next random button if the user cannot find the announced value on the bingo card
    JButton nextButton = new JButton("Next Number");
    nextButton.addActionListener(this);
    nextButton.setActionCommand("Get Next Number");
    
    //Assign unique random numbers to the bingo card and initialize the highlighted positions boolean array
    assignUniqueNumbers(bingoCardArray,bingoSize);
    initializeBooleanArray(highlightedPositions,bingoSize);
    
    //Add buttons/labels onto the control Panel, ultimatley plastering it to the screen
    controlPanel.add(nextButton);
    controlPanel.add(announcerLabel);
    controlPanel.add(counterLabel);
    
    //Initilize the button number to zero
    int butCounter = 0;
    
    //Goes through the bingo card and assigns a button for each position on the 2D array
    for(int i =0; i<bingoSize;i++){
      for(int j = 0; j<bingoSize;j++){
        int randumNum = bingoCardArray[i][j] ;
        buttons[butCounter] = new JButton(Integer.toString(randumNum));
        
        //Set action listeners for buttons
        buttons[butCounter].addActionListener(this);
        
        //Defines an action command for the buttons(to store the unique number from the bingoCardArray)
        buttons[butCounter].setActionCommand(Integer.toString(randumNum));
        bingoPanel.add(buttons[butCounter]);
        butCounter = butCounter +1;
      }
    }
    //Update the annoucement button
    updateAnnouncer(true);
    //Sets the frame area visible
    bingoCardFrame.setVisible(true);
  }
  /****** End of Construction Method ******/
  
  /*****Screen Builder method - Setting up the screen + adding elements for the Panels/Screen*****/
  private void screenBuilder(){
     //Set up the screen(ex. screen name - Bingo Card, screen size - 800 by 800 pixels,and background colour - YELLOW)
     bingoCardFrame = new JFrame("Bingo Card");
     bingoCardFrame.setSize(800,800);
     bingoCardFrame.setLayout(new GridLayout(3,1));
     bingoCardFrame.getContentPane().setBackground(Color.YELLOW);
     bingoCardFrame.addWindowListener(new WindowAdapter() {
       public void windowClosing(WindowEvent windowEvent){
         System.exit(0); //Close frame & quit
       }
     });
     
     //Building the screen further through other elements
     
     //Position title and labels on the screen
     title = new JLabel("", JLabel.CENTER);
     announcerLabel = new JLabel("",JLabel.CENTER);
     counterLabel = new JLabel("",JLabel.CENTER);
     errorLabel = new JLabel("", JLabel.CENTER);
     winningLabel = new JLabel("", JLabel.CENTER);
     
     //Add and set the layout of the panels
     controlPanel = new JPanel();
     controlPanel.setLayout(new GridLayout());
     bingoPanel = new JPanel();
     bingoPanel.setLayout(new GridLayout(bingoSize,bingoSize));
     
     //Customize the label size
     announcerLabel.setSize(350,100);
     counterLabel.setSize(350,100);
     
     //Add the elements to the game
     bingoCardFrame.add(title);
     bingoCardFrame.add(controlPanel);
     bingoCardFrame.add(bingoPanel);
     bingoCardFrame.add(errorLabel);
     bingoCardFrame.add(winningLabel);
     bingoCardFrame.setVisible(true);   
   }
   /***** End of Screen Building Method *****/
   
   @Override
   /*****Button Actions Method - Actions that are triggered by the buttons are handled in this method*****/
     public void actionPerformed(ActionEvent buttonAction) {
     //This variable retrieves the action command from the user when they click the buttons
     String action = buttonAction.getActionCommand();
     try{
       //Declare the variables used inside the method
       int bingoCardNumber = 0;
       int currentDisplayNumber = 0;
       
       //Random number the computer displays on the screen for the user to find
       currentDisplayNumber = Integer.parseInt(announcerLabel.getText());
       
       //If the user clicks the get "next number" button, then the computer will display the next number
       if(!action.equals("Get Next Number")){
         bingoCardNumber = Integer.parseInt(action);    
         //If the bingo card number is equal to the announced number then the computer will identify and highlight the selected position
         if(bingoCardNumber == currentDisplayNumber){
           highlightButton(bingoCardNumber, bingoSize);
           
           //If the user wins the BINGO game horizontally, then the winning message is displayed
           if(isBingoHor(bingoSize)== true){
             winningLabel.setText("BINGO! You won.");
           }
           //If the user wins the BINGO game vertically, then the winning message is displayed
           else if(isBingoVer(bingoSize) == true){
             winningLabel.setText("BINGO! You won.");
           }
           //If the user wins the BINGO game diagonally rightward, then the winning message is displayed
           else if(isBingoRightDiagonal(bingoSize) == true){
             winningLabel.setText("BINGO! You won.");
           }
           //If the user wins the BINGO game diagonally leftward, then the winning message is displayed
           else if(isBingoLeftDiagonal(bingoSize) == true){
             winningLabel.setText("BINGO! You won.");
           }else{
             //Else the system will display the next random number
             updateAnnouncer(true);
           }
           errorLabel.setText("");
         }else{
           //If the user clicks the wrong button(the button that is not equal to the announcer), then the text below is displayed
           errorLabel.setText("Incorrect. Try again");
         }
       }else{
         //Counts the number of tries when the next number button is clicked(ultimatley the amount of tries it takes untill the user wins the game)
         numOfTries = numOfTries + 1;
         updateAnnouncer(true);
       }
       
     }catch(Exception e){
       e.printStackTrace();
     }
   }
   /***** End of Button Actions Method *****/
   
   /*initializeIntArrayWZero
    * This method initializes the bingo card with 0s
    * @param initializeArray, a 2D array that holds "size" amount of 0s
    * @param size, a variable that holds the integer values of the dimensions of the bingo card
    */
   public void initializeIntArray(int [][] initializeArray, int size){
     for(int i =0; i<size;i++){
       for(int j = 0; j<size;j++){
         initializeArray[i][j] = 0;
       }
     }
   }
   
   /*isNumberExist
    * This method checks the random number to see if the number exists in the bingo card, this allows individualism amongst the bingo card values
    * @param randomNumber, a variable that holds an integer generated by the random generator.
    * @param bingoCardArray, a 2D array that holds the values of the bingo card.
    * @param size, a variable that holds the integer value of the dimensions of the bingo card.
    * @return isFound(a boolean value), which determines if the number is found in the bingo card.
    */
   public boolean isNumberExist(int randomNumber,int [][] bingoCardArray, int size){
     boolean isFound = false;
     for(int i =0; i<size;i++){
       for(int j = 0; j<size;j++){
         //If the random number already exists in the bingo card
         if(bingoCardArray[i][j] == randomNumber){
           isFound = true;
         }
       }
     }
     return isFound;
   }
   
   /*assignUniqueNumbers
    * This method initializes the bingo card with distinctive and random generated integers.
    * @param initializeArray, a 2D array that holds "size" amount of random generated values.
    * @param size, a variable that holds the integer value of the dimensions of the bingo card.
    */
   public void assignUniqueNumbers(int [][] initializeArray, int size){
     //When this method is called in the beginning of the program, the method must initialize/restart the array with values of 0 in each space of the 2D array
     initializeIntArray(initializeArray, size);
     //The loop goes through the bingo card and fills the card with unique integer values
     for(int i =0; i<size;i++){
       for(int j = 0; j<size;j++){
         boolean isExist = false;
         //The do-while loop generates untill the whole card is filled with unique values
         do{
           int randomNumber = random.nextInt(randomNumberRange);
           //Use method isNumberExist to check if the random number is also already assigned in the bingo card
           isExist = isNumberExist(randomNumber, initializeArray, size);
           //If the unique number is not in the array(isExist is false), then that number is officially assigned to the bingo card.
           if(isExist == false){
             initializeArray[i][j] = randomNumber;
           }
         }while(isExist == true);
       }
     }
   }
   
   /*initializeBooleanArray
    *This method is used to help find the positions that need to be highlighted in the BINGO card by initialize the default array values to false
    *@param correctValueArray, a 2D array that stores boolean values that will be used. 
    *@param size, a variable that holds the integer value of the dimensions of the bingo card.
    */
   public void initializeBooleanArray(boolean [][] correctValueArray, int size){
     for(int i =0; i<size;i++){
       for(int j = 0; j<size;j++){
         correctValueArray[i][j] = false;
       }
     }
   }
   
   /*highlightButton
    *This method highlights the spots on the BINGO card that matches the computer's number call
    *@param bingoValue, a variable that stores the integer value that represents a number on the bingo card.
    *@param size, a variable that holds the integer value of the dimensions of the bingo card.
    */
   public void highlightButton(int bingoValue, int size){
     JButton colorButton;
     int buttonCounter = 0;
     for(int i = 0; i<size; i++){
       for(int j = 0; j<size; j++){
         //As the method loops through the buttons of the bingo card, if the number/position from the button is equivalent to the announcer's number, then the button is highlighted to green, the correct sound plays(a cow sound),and an image of the Raider is filled onto the button paritally.
         if(bingoCardArray[i][j] == bingoValue){
           colorButton = buttons[buttonCounter];
           colorButton.setBackground(Color.GREEN);
           Icon icon = new ImageIcon("bingo1.png");
           colorButton.setIcon(icon);
           highlightedPositions[i][j] = true;
           playSound("cow");
         }
         buttonCounter = buttonCounter + 1;
       }
     }
   }
   
   /*isUniqueAnnouncerValues
    *This method is used to check if the announced value is not found in the card.
    *@param bingoValue, a variable that stores the integer value that represents a number on the bingo card.
    *@param size, a variable that holds the integer value of the dimensions of the bingo card.
    *@return isUnique(a boolean variable) which determines whether the number announced by the computer is existent or not through true/false values. This prevents the announced value from being asked again when the user found it.
    */
   public boolean isUniqueAnnouncerValues(int bingoValue, int size){
     boolean isUnique = true;
     //Loops through the bingo card(every position) and verifies that the potential announced value passed is not exist within the card, thus, if not, isUnique is false.
     for(int i = 0; i<size; i++){
       for(int j = 0; j<size; j++){
         if(highlightedPositions[i][j] == true){
           if(bingoCardArray[i][j] == bingoValue){
             isUnique = false;
           }
         }
       }
     }
     return isUnique;
   }
   
   /*isBingoHor
    * This method checks the user's BINGO game for horizontal wins(ex. bingoSize in a horizontal row)
    * @param size, an int value that holds the integer values of the dimensions of the bingo card
    * @return isHorFound(a boolean value) which determines whether a horizontal win is found(through true/false values).
    */
   public boolean isBingoHor(int size){
     //Initialize reutrn variable to false
     boolean isHorFound = false; 
     //Loops through bingo card to search for positions that have been highlighted through the 2D array highlightedPositions
     for(int i = 0; i<size; i++){
       //Initialize numOfHighlights to 0 when method is called each time the program runs
       int numOfHighlights = 0;
       //While a horizontal win is not found yet, the computer continues to loop through the bingoCard and find the positions that are highlighted. If the highlighted positions are starting to line up in a horizontal way, then each time the program catches that, it adds on to the numOfHighlights counter. 
       if(isHorFound == false){
         for(int j = 0; j<size; j++){
           if(highlightedPositions[i][j] == true){
             numOfHighlights = numOfHighlights + 1;
           }
         }
         //When the numOfHighlights reach the dimension of the bingo card, then the horizontal win is found('size' in a row horizontally) and isHorFound is true
         if(numOfHighlights == size){
           isHorFound = true;
         }else{
           isHorFound = false;
         }
       }
     }
     return isHorFound;
   }
   
   /*isBingoVer
    * This method checks the user's BINGO game for vertical wins(ex. bingoSize in a vertical row)
    * @param size, an int value that holds the integer values of the dimensions of the bingo card
    * @return isVerFound(a boolean value) which determines whether a vertical win is found(through true/false values).
    */
   public boolean isBingoVer(int size){
     //Initialize reutrn variable to false
     boolean isVerFound = false; 
     //Loops through bingo card to search for positions that have been highlighted through the 2D array highlightedPositions
     for(int i = 0; i<size; i++){
       //Initialize numOfHighlights to 0 when method is called each time the program runs
       int numOfHighlights = 0;
       //While a vertical win is not found yet, the computer continues to loop through the bingoCard and find the positions that are highlighted. If the highlighted positions are starting to line up in a vertical way, then each time the program catches that, it adds on to the numOfHighlights counter. 
       if(isVerFound == false){
         for(int j = 0; j<size; j++){
           if(highlightedPositions[j][i] == true){
             numOfHighlights = numOfHighlights + 1;
           }
         }
         //When the numOfHighlights reach the dimension of the bingo card, then the vertical win is found('size' in a row vertically) and isVerFound is true
         if(numOfHighlights == size){
           isVerFound = true;
         }else{
           isVerFound = false;
         }
       }
     }
     return isVerFound;
   }
   
   /*isBingoRightDiagonal
    * This method checks the user's BINGO game for wins that run in a rightward diagonal(ex. bingoSize in a right diagonal way)
    * @param size, an int value that holds the integer values of the dimensions of the bingo card
    * @return isBingoRightDiagonal(a boolean value) which determines whether a rightward diagonal win is found(through true/false values).
    */
   public boolean isBingoRightDiagonal(int size){
     //Initialize reutrn variable to false
     boolean isBingoRightDiagonal = false; 
     //Initialize numOfHighlights to 0 when method is called each time the program runs
     int numOfHighlights = 0;
     //Loops through bingo card to search for positions that have been highlighted through the 2D array highlightedPositions
     for(int i = 0; i<size; i++){
       for(int j = 0; j<size; j++){
         //While a righward diagonal win is not found yet, the computer continues to loop through the bingoCard and find the positions that are highlighted. If the highlighted positions are starting to line up in a rightward diagonal way(meaning the i and j position are the same, ex. i&j can be 3&3, 2&2, or 4&4) then each time the program catches that, it adds on to the numOfHighlights counter. 
         if(i == j){
           if(highlightedPositions[i][j] == true){
             numOfHighlights = numOfHighlights + 1;
           }
         }
       }
     }
     //When the numOfHighlights reach the dimension of the bingo card, then the righward diagonal win is found('size' in a row diagonally to the right) and isBingoRightDiagonal is true
     if(numOfHighlights == size){
       isBingoRightDiagonal = true;
     }else{
       isBingoRightDiagonal = false;
     }
     return isBingoRightDiagonal;
   }
   
   /*isBingoLeftDiagonal
    * This method checks the user's BINGO game for wins that run in a leftward diagonal(ex. bingoSize in a left diagonal way)
    * @param size, an int value that holds the integer values of the dimensions of the bingo card
    * @return isBingoLeftDiagonal(a boolean value) which determines whether a leftward diagonal win is found(through true/false values).
    */
   public boolean isBingoLeftDiagonal(int size){
     //Initialize reutrn variable to false
     boolean isBingoLeftDiagonal = false;
     //Initialize numOfHighlights to 0 when method is called each time the program runs
     int numOfHighlights = 0;
     //Initialize columnCounter to the quantity of size when the method is called each time the program runs
     int columnCounter = size;
     //Loops through bingo card to search for positions that have been highlighted through the 2D array highlightedPositions
     for(int i = 0; i<size; i++){
       for(int j = size-1; j>=0; j--){
         //While a leftward diagonal win is not found yet, the computer continues to loop through the bingoCard and find the positions that are highlighted. If the highlighted positions are starting to line up in a leftward diagonal way then each time the program catches that, it adds on to the numOfHighlights counter. 
         if(j == columnCounter-1){
           if(highlightedPositions[i][j] == true){
             numOfHighlights = numOfHighlights + 1;
           }
         }
       }
       columnCounter = columnCounter - 1;
     }
     //When the numOfHighlights reach the dimension of the bingo card, then the leftward diagonal win is found('size' in a row diagonally to the left) and isBingoLeftDiagonal is true
     if(numOfHighlights == size){
       isBingoLeftDiagonal = true;
     }else{
       isBingoLeftDiagonal = false;
     }
     return isBingoLeftDiagonal;
   }
   
   /*updateAnnouncer
    *This method will generate a unique random number and updates the announcer's value on the screen. This method concurrently reads the bingo card array while the highlight array is updated.
    *Hence,to avoid the endless loop, I limit the number of tries to get the unique value by 10. 
    *@param updateValue is a boolean variable that controls the update of the announcer through true/false values.
    */
   private void updateAnnouncer(boolean updateValue){
     //Declare and initialize varaible used throughout the method
     int nextNumber;
     boolean isUniqueAnnouncerNum = false;
     int counter = 0;
     if(updateValue == true){
       nextNumber = random.nextInt(randomNumberRange);
       //The code below runs untill it finds a unique number or until the count reaches 10(to prevent endless loop). 
       do{
         //If the generated value is unique then the announced number is displayed onto the screen for the viewer to see
         if(isUniqueAnnouncerValues(nextNumber,bingoSize)== true){
           errorLabel.setText("");
           announcerLabel.setText(Integer.toString(nextNumber));
           isUniqueAnnouncerNum = true;
         }
         counter = counter + 1;
         if(isUniqueAnnouncerNum == true){
           counter = 12;
         }
       }while(counter <= 10);
       //Displays number of tries onto the screen when the user clicks the "next number" button or when the announcer updates the screen
       counterLabel.setText("Number of Tries: " + Integer.toString(numOfTries));
    }
   }
   
   /*playSound
    *This method plays the sound using the given soundName
    *@param soundName is a string variable that represents the sound file
    */
   public static void playSound(String soundName) { 
     //If the sound name that is played is called "cow"
     if (soundName.equals("cow")) { 
       try {
         //Searches for audio file named "cow.wav" through files
         File audioFile = new File("cow.wav");
         AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
         DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
         Clip clip = (Clip) AudioSystem.getLine(info);
         clip.addLineListener(new correctSound());
         //Opens and starts the sound clop
         clip.open(audioStream);
         clip.start();
       }catch (Exception e) {
         e.printStackTrace();
       }   
     } 
   }
   
   /*correctSound
    *This method is a function that plays the sound effect
    */
   static class correctSound implements LineListener {
     public void update(LineEvent event) {
       if (event.getType() == LineEvent.Type.STOP) {
         event.getLine().close(); 
       }
     }
   }
}//end of class