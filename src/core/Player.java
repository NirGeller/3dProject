package core;
import geli.g3d.*;

public class Player 
{
    private GBody3d player; // the 3d object of the player
    private int [] location; // the location of the player on the cubes collection
    private boolean inside;// true if the player is inside the "danger zone" - the inner platform where the balls are
    private int life; // the number of tries the user has until he loses
    private GPoint3d respawn; // holds the starting point of the player and also the point in which the player respawns
    public Player(int life)
    {
        this.life = life;
        player =  GBody3d.createPyramid(8, 12, 20);  
        location = new int[2];
        inside = false;
    }    
    
    public void apply(GMatrix3d m)
    {
        player.apply(m);
        respawn.apply(m);
    }
    
    public void turn(GMatrix3d m)
    {
       player.turn(m);
       respawn.apply(m);
    }
    
    public void defeated()
    {
        life--;
    }
    
    public GBody3d getPlayer()
    {
        return player;
    }

    public GPoint3d getRespawn() 
    {
        return respawn;
    }

    public int getLife()
    {
        return life;
    }
    

    public int[] getLocation()
    {
        return location;
    }

    public boolean isInside()
    {
        return inside;
    }

    public void setInside(boolean inside) 
    {
        this.inside = inside;
    }

    public void setRespawn(GPoint3d Respawn) 
    {
        respawn = new GPoint3d(Respawn.getX(), Respawn.getY(), Respawn.getZ());
        
    }
    
    

}
