import java.awt.*;
import java.awt.event.*;
import java.util.*;

//The basic ScrollingGame, featuring Avoids, Gets, and SpecialGets
//Players must reach a score threshold to win
//If player runs out of HP (via too many Avoid collisions) they lose
public class ScrollingGame extends GameEngine {
    
 
    
    //Starting Player coordinates
    protected static final int STARTING_PLAYER_X = 0;
    protected static final int STARTING_PLAYER_Y = 100;
    
    //Score needed to win the game
    protected static final int SCORE_TO_WIN = 1400;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    protected static final int MAX_GAME_SPEED = 500;
    //Interval that the speed changes when pressing speed up/down keys
    protected static final int SPEED_CHANGE = 20;    
    
    protected static final String INTRO_SPLASH_FILE = "assets/splash2.gif";        
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    protected static final int SPAWN_INTERVAL = 45;

    public static final int MAX_ENTITY_PER_SPAWN = 5;
    public static final String BG_IMAGE = "assets/galaxy50_speed.gif";

    public static final String LOSE_IMAGE = "assets/lose_splash.png";
    public static final String WIN_IMAGE = "assets/win_splash.png";

    
    
    //A Random object for all your random number generation needs!
    protected static final Random rand = new Random();
    
            
    
    //Player's current score
    protected int score;
    protected int level = 1;
    protected int prevLevelScore = 0;
    protected int prevLevel = 1;
    
    //Stores a reference to game's Player object for quick reference
    //(This Player will also be in the displayList)
    protected Player player;
    
    
    
    
    
    public ScrollingGame(){
        super();
    }
    
    public ScrollingGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }
    
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void pregame(){
        this.setBackgroundImage(BG_IMAGE);
        this.setBackgroundColor(Color.BLACK);
        player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player); 
        score = 0;
        setSplashImage(INTRO_SPLASH_FILE);
    }
    
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        for (int i = 0; i < displayList.size(); i++){
            if (displayList.get(i) instanceof Collectable){
                handlePlayerCollision((Collectable)displayList.get(i));
                //update difficulty here
                // increase game speed
                if (updateGameSpeed(prevLevel, level)){
                    int newSpeed = getGameSpeed()+ SPEED_CHANGE;
                    setGameSpeed(newSpeed);
                    prevLevel = level;
                }
            }
        }
        
        scrollEntities();  
        gcOutOfWindowEntities(); 
        //Spawn new entities only at a certain interval
        if (super.getTicksElapsed() % SPAWN_INTERVAL == 0){            
            spawnEntities();
            //if diificulty reaches 5, spawn meteorites
        }
        
        //Update the title text on the top of the window
        String textTitle = "HP: " + player.getHP() +", Score: " + score + ", Level: " + level;
        setTitleText(textTitle);        
       
    }
    
    
    //Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities(){
        for (int i = 0; i < displayList.size(); i++){

            if (displayList.get(i) instanceof Scrollable ){
                ((Scrollable)displayList.get(i)).scroll();
            }
            
            //****  finish me!  ****  
            //How do you know which Entities to scroll?
           
        }
    }
    
    
    //Handles "garbage collection" of the displayList
    //Removes entities from the displayList that have scrolled offscreen
    //(i.e. will no longer need to be drawn in the game window).
    protected void gcOutOfWindowEntities(){
       
        //****   implement me!   ****
        //also: this function is not currently being called anywhere
        //where should it be called?
        Entity pickOne;
        for (int i = 0; i < displayList.size(); i++){
            if (displayList.get(i) instanceof Scrollable ){
                pickOne = displayList.get(i);
                if (pickOne instanceof Meteorite){
                    if (pickOne.getX() >= getWindowWidth() || pickOne.getY() >= getWindowHeight()){
                        displayList.remove(pickOne);
                        i--;
                    }
                }
                else{
                    if (pickOne.getRightEdge() <= 0){
                        displayList.remove(pickOne);
                        i--;
                    }
                }

                /* 
                if (! displayList.get(i) instanceof Meteorite && displayList.get(i).getRightEdge() <= 0){ //if entity is out of window, clear it. delete or set to null
                    displayList.remove(displayList.get(i));  //do i have to reduce i by 1 because i removed one item already??
                    i--; //to pick next item after shuffling

                    //checking meteorites and removing them from displayList
                }
                else{
                    if (displayList.get(i).getX() >= getWindowWidth() || displayList.get(i).getY() >= getWindowHeight()){
                        displayList.remove(displayList.get(i));
                        i--;
                    }
                }
                */
            }
        }
       
    }
    
    
    //Called whenever it has been determined that the Player collided with a collectable
    protected void handlePlayerCollision(Collectable collidedWith){
       
        //****   implement me!   ****
        if (collidedWith instanceof Get){
            if (player.isCollidingWith((Entity)collidedWith)){
                score += ((Get)collidedWith).getPoints();       //increase score
                if (collidedWith instanceof SpecialGet){
                    player.modifyHP(((SpecialGet)collidedWith).increaseHP());
                }
                displayList.remove((Entity)collidedWith);
            }
            
        }
        else{
            if (player.isCollidingWith((Entity)collidedWith)){
                score += (collidedWith).getPoints();
                player.modifyHP((collidedWith).getDamage());
                
                displayList.remove((Entity)collidedWith);
            }
            /* 
            if (player.isCollidingWith((Entity)collidedWith)){
                score += ((Avoid)collidedWith).getPoints();
                player.modifyHP(((Avoid)collidedWith).getDamage());
                
                displayList.remove((Entity)collidedWith);
            }
            */
        }
        //handling change in difficulty
        if (updateLevel(prevLevelScore, score)){
            level++;
            prevLevelScore = score;
        }
       
    }

    //-----------helper function----------------------------
    //----------checks scores to update game difficulty-----
    private static boolean updateLevel(int oldScore, int newScore){
        if (newScore - oldScore >= 200){
            return true;
        }
        return false;
    }

    private static boolean updateGameSpeed(int oldLevel, int newLevel){
        if (newLevel - oldLevel >= 1){
            return true;
        }
        return false;
    }
    //--------------------------------------------------------
    
    //Spawn new Entities on the right edge of the game window
    protected void spawnEntities(){
        int count = rand.nextInt(MAX_ENTITY_PER_SPAWN + 1);  //randomly picking the number of entities to print per spawn
        for (int i = 0; i < count; i++){    
            double ranGenerate = Math.random();   //determining the chance at which entities are printed

            Entity specGet = new SpecialGet(getWindowWidth(), 0);
            Entity normGet = new Get(getWindowWidth(), 0);
            Entity badGet = new Avoid(getWindowWidth(), 0);
            //new addition
            
            //displayList.add(badGet);

            ///* 
            Entity pick;

            if (ranGenerate < 0.5){
                int newY = rand.nextInt(getWindowHeight() - Get.GET_HEIGHT +1);
                if (ranGenerate < 0.1){
                    specGet.setY(newY);
                    pick = specGet;
                }
                else{
                    normGet.setY(newY);
                    pick = normGet;
                }
            }
            else{
                int newY = rand.nextInt(getWindowHeight() - Avoid.AVOID_HEIGHT +1);
                badGet.setY(newY);
                pick = badGet;
            }
            if ((checkCollision(pick)).size() == 0){  //add if there are no collisions
                displayList.add(pick);
            }
            //*/ 
        }
        //spawning meteorites after reaching level 4
        if (level >= 4){
            if (createMeteorite()){
                int maxNumMeteorites = 1;
                int numMeteorites = rand.nextInt(maxNumMeteorites + 1);
                for (int i = 0; i < numMeteorites; i++){
                    Entity meteorite = new Meteorite(getWindowWidth(),0);
                    int newX = rand.nextInt(getWindowWidth() - Meteorite.METEORITE_WIDTH + 1);
                    meteorite.setX(newX);

                    if ((checkCollision(meteorite)).size() == 0 ){
                        displayList.add(meteorite);
                    }
                }
            }
        }
        
        //****   implement me!   ****
            
    }
    //----helper function to determine whether to throw a meteorite or not
    private static boolean createMeteorite(){
        int decide = rand.nextInt(2);
        return decide == 0;
    }
    //-----------------------------------------------------

    
    
    
    //Called once the game is over, performs any end-of-game operations
    protected void postgame(){
        if (player.getHP() == 0){
            super.setTitleText("GAME OVER - You Lose!");
            super.setSplashImage(LOSE_IMAGE);
        }
        else{
            super.setTitleText("GAME OVER - You Won!");
            super.setSplashImage(WIN_IMAGE);
        }
        
    }
    
    
    //Determines if the game is over or not
    //Game can be over due to either a win or lose state
    protected boolean determineIfGameIsOver(){

        if(score >= SCORE_TO_WIN || player.getHP() == 0 ){
            return true;
        }
        
        return false;   //****   placeholder... implement me!   ****
       
    }
    
    
    
    //Reacts to a single key press on the keyboard
    protected void reactToKey(int key){
        
        
        setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key) + ",  DisplayList size: " + displayList.size());
        //if a splash screen is active, only react to the "advance splash" key... nothing else!
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            
            return;
        }
        else if (key == KEY_PAUSE_GAME){
            if (!isPaused){
                super.isPaused = true;
            }
            else{
                super.isPaused = false;
            }
        }
        else if (key == UP_KEY && ! isPaused){
            if (player.getY() > 0){
                player.moveUp();
            }
        }
        else if (key == DOWN_KEY && ! isPaused){
            if (player.getY() < (getWindowHeight()-player.getHeight())){
                player.moveDown();
            }
        }
        else if (key == LEFT_KEY && ! isPaused){
            if (player.getX() > 0){
                player.moveLeft();
            }  
        }
        else if (key == RIGHT_KEY && ! isPaused){
            if (player.getX() < getWindowWidth()-player.getWidth()){
                player.moveRight();
            }  
        }
        /* 
        else if (key == SPEED_UP_KEY && ! isPaused){
            if (getGameSpeed() + SPEED_CHANGE <= MAX_GAME_SPEED){
                int newSpeed = getGameSpeed() + SPEED_CHANGE;
                setGameSpeed(newSpeed);
            }
        }
        else if (key == SPEED_DOWN_KEY && ! isPaused){
            if (getGameSpeed() - SPEED_CHANGE > 0){
                int newSpeed = getGameSpeed() - SPEED_CHANGE;
                setGameSpeed(newSpeed);
            }
        }
        */
        // else if (key == KEY_TOGGLE_DEBUG){
        //     setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key) + ",  DisplayList size: " + displayList.size());
        // }
            
        
    }    
    
    
    //Handles reacting to a single mouse click in the game window
    //Won't be used in Milestone #2... you could use it in Creative Game though!
    protected MouseEvent reactToMouseClick(MouseEvent click){
        if (click != null){ //ensure a mouse click occurred
            int clickX = click.getX();
            int clickY = click.getY();
            setDebugText("Click at: " + clickX + ", " + clickY);
        }
        return click;//returns the mouse event for any child classes overriding this method
    }
    
    
    
    
}
