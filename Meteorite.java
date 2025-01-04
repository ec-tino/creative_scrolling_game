//meteorites are random generated from the top of the window
// they drop down across the window and cause damage to player
// if it collides with the player
public class Meteorite extends Entity implements Collectable, Scrollable{
    
    //Location of image file to be drawn for an Avoid
    public static final String METEORITE_IMAGE_FILE = "assets/meteorite.gif";
    //Dimensions of the Avoid    
    public static final int METEORITE_WIDTH = 75;
    public static final int METEORITE_HEIGHT = 75;
    //Speed that the avoid moves each time the game scrolls
    public static final int METEORITE_SCROLL_SPEED = 1;
    public static final int METEORITE_DAMAGE_VALUE = -2;

    public Meteorite(){
        this(0,0);
    }

    public Meteorite(int x, int y){
        super(x, y, METEORITE_WIDTH, METEORITE_HEIGHT, METEORITE_IMAGE_FILE);
    }

    public int getScrollSpeed(){
        return METEORITE_SCROLL_SPEED;
    }

    public void scroll(){
        setY(getY() + METEORITE_SCROLL_SPEED);
        
    }

    public int getPoints(){
        return 0;
    }

    public int getDamage(){
        return METEORITE_DAMAGE_VALUE;
    }
    //check collision between meteorites and other entities
}
