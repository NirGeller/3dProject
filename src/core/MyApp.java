package core;

import geli.*;
import geli.g3d.*;
import java.util.Random;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * Document : 
 * Created on :
 * Author :
 */

public class MyApp extends BasicApp 
{

    //o הגדרת התכונות
    
    private boolean help;
    private boolean moveball; 
    private boolean End;
    private final double [] Reset;
    private final GMatrix3d m;
    private final GMatrix3d cm;
    private final GPoint3d cam;
    private final GBody3d  ball;
    private final GBody3d [] outer_platform;
    private final GBody3d [] inner_border;
    private final GBody3d [] outer_border;
    private final CubesCollection platform;
    private final Player player;
    private final GLight3d lightme;
    private final GPolygon3d [] life;
    private final Random rnd;
    //o איתחול התכונות
    public MyApp() 
    {
        this.help = false;
        m = new GMatrix3d();
        cm = new GMatrix3d();
        
        //setting the Random
        rnd = new Random();
        //setting end
        End = false;
        
        //setting moveball
        moveball = true;
        
        //setting the reset 
        Reset = new double[6]; // 0 - x trans; 1 - y trans; 2 - z trans; 3 - x rot; 4 - y rot; 5 - z rot;
       
        //setting the camera
        cam = new GPoint3d(400, 0, 1500);
       
        //setting the platform
        int CubeEdge = 8;
        platform = new CubesCollection(CubeEdge, 50, 50, 20, Color.RED, Color.RED);
        platform.getPlatform().setFillColor(Color.green);
        platform.getPlatform().setFillColor(Color.green);
        GBody3d VirtualPlatform = platform.getPlatform(); //instead of accessing the platform inside (cubesCollection)platform i created a temporary platfrom here to use to place all the other bodies

        //setting the outer platforms
        int size = CubeEdge*6; // the width of the left and right platforms and the depth of the top and bottom
        outer_platform = new GBody3d[4]; 
        outer_platform[0] = GBody3d.createBox(0, 0, -size, CubeEdge * 50 , 20, size); // top
            outer_platform[0].setFrameColor(Color.BLUE);
        outer_platform[1] = GBody3d.createBox(-size, 0, -size,size, 20, CubeEdge * 50 + size * 2); // left
            outer_platform[1].setFrameColor(Color.BLUE);
        outer_platform[2] = GBody3d.createBox(0, 0, CubeEdge * 50, CubeEdge * 50 , 20, size); // bottom
            outer_platform[2].setFrameColor(Color.BLUE);
        outer_platform[3] = GBody3d.createBox(CubeEdge * 50, 0, -size, size, 20, CubeEdge * 50 + size * 2); // right
            outer_platform[3].setFrameColor(Color.BLUE);
        
        //setting the player
        player = new Player(4);
        m.setZRotMatrix(Math.PI);
            player.getPlayer().apply(m);
        
        //setting the players life
        life = new GPolygon3d[player.getLife()];
        for (int i=0;i<player.getLife();i++)
        {
            life[i] = GPolygon3d.create(1000, 15, new GPoint3d(20 + 40*i,20,0));
            life[i].setFillColor(Color.red);
            life[i].setFrameColor(Color.red);
        }    
            
        //setting the ball
        int r = 8;
        ball = GBody3d.createSphere(20, 20, r, new GVector3d(platform.getCube(0,0).getV(3), platform.getCube(0,0).getV(1)).add(new GVector3d(platform.getCube(0,0).getV(1), platform.getCube(0,0).getV(2)).scalarProd(0.34)));
        ball.setFillColor(Color.BLUE);
        ball.setFrameColor(Color.BLUE); 

        //setting the inner border
        inner_border = new GBody3d[4];
        inner_border[0] = GBody3d.createBox(0, 0, 0, VirtualPlatform.getV(0).distance(VirtualPlatform.getV(1)), 2*r, 1); //top
        inner_border[1] = GBody3d.createBox(0, 0, 0, 1, 2*r, VirtualPlatform.getV(1).distance(VirtualPlatform.getV(2)));
        inner_border[2] = GBody3d.createBox(0, 0, 0, VirtualPlatform.getV(0).distance(VirtualPlatform.getV(1)), 2*r, 1);
        inner_border[3] = GBody3d.createBox(0, 0, 0, 1, 2*r, VirtualPlatform.getV(0).distance(VirtualPlatform.getV(5)));
        
        //setting the outer border
        outer_border = new GBody3d[4];
        outer_border[0] = GBody3d.createBox(0, 0, 0, outer_platform[1].getV(7).distance(outer_platform[3].getV(6)), 20, 10);
        outer_border[0].setFillColor(Color.yellow);
        outer_border[1] = GBody3d.createBox(0, 0, 0, 10, 20, outer_platform[1].getV(4).distance(outer_platform[1].getV(7)));
        outer_border[1].setFillColor(Color.yellow);
        outer_border[2] = GBody3d.createBox(0, 0, 0, outer_platform[1].getV(7).distance(outer_platform[3].getV(6)), 20, 10);
        outer_border[2].setFillColor(Color.yellow);
        outer_border[3] = GBody3d.createBox(0, 0, 0, 10, 20, outer_platform[1].getV(4).distance(outer_platform[1].getV(7)));
        outer_border[3].setFillColor(Color.yellow);
        
        //positioning the balls
        m.setTransMatrix((VirtualPlatform.getV(1).getX()- VirtualPlatform.getV(0).getX())/2 - r,  - r + 1 , (VirtualPlatform.getV(0).getZ()- VirtualPlatform.getV(3).getZ())/2 - r);
        ball.apply(m);
        
        
        //positioning the inner borders
        m.setTransMatrix(VirtualPlatform.getV(7).getX(), VirtualPlatform.getV(7).getY() - 2*r - CubeEdge , VirtualPlatform.getV(7).getZ() - 1);
            inner_border[0].apply(m);
        m.setTransMatrix(VirtualPlatform.getV(7).getX(), VirtualPlatform.getV(7).getY() - 2*r - CubeEdge, VirtualPlatform.getV(7).getZ());
            inner_border[1].apply(m);
        m.setTransMatrix(VirtualPlatform.getV(7).getX(), VirtualPlatform.getV(7).getY() - 2*r - CubeEdge, VirtualPlatform.getV(4).getZ());
            inner_border[2].apply(m);
        m.setTransMatrix(VirtualPlatform.getV(6).getX() - 1, VirtualPlatform.getV(7).getY() - 2*r - CubeEdge, VirtualPlatform.getV(7).getZ());
            inner_border[3].apply(m);
        
        //posistioning the outer borders
        m.setTransMatrix(outer_platform[1].getV(7).getX() , outer_platform[1].getV(7).getY() - 20, outer_platform[1].getV(7).getZ());
            outer_border[0].apply(m);
        m.setTransMatrix(outer_platform[1].getV(7).getX(), outer_platform[1].getV(7).getY() - 20, outer_platform[1].getV(7).getZ());
            outer_border[1].apply(m);
        m.setTransMatrix(outer_platform[1].getV(4).getX(), outer_platform[1].getV(4).getY() -20, outer_platform[1].getV(4).getZ());
            outer_border[2].apply(m);
        m.setTransMatrix(outer_platform[3].getV(6).getX(), outer_platform[3].getV(6).getY() -20, outer_platform[3].getV(6).getZ());
            outer_border[3].apply(m);
        
            
        //posistiong the player
        m.setTransMatrix(-CubeEdge*1.5, -20, CubeEdge*1.5);
            player.getPlayer().apply(m);
            
        //moving the objects to the 'middle' of the screen
        m.setTransMatrix(200, 300, 30);
            platform.apply(m);
            for (int i=0; i<4; i++)
            {
                inner_border[i].apply(m);
                outer_platform[i].apply(m);
                outer_border[i].apply(m);
            }
            player.getPlayer().apply(m);
            ball.apply(m);
            
            
        //setting the players respawn point
        player.setRespawn(player.getPlayer().getV(0));
       
        //setting the light
        lightme = new GLight3d( new GVector3d(0 - getCanvasWidth() ,getCanvasHeight(), 0),0.1);


    }
        
    //o עדכון מצב התכונות
    public void update(long delta) 
    {
        if (keyboard.keyDownOnce(KeyEvent.VK_F12)) 
        {
            this.help = !this.help;
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_UP)) // if the user presser the arrow up key the player starts moving upwards
        {
            player.getPlayer().setVelocity(new GVector3d(platform.getCube(0, 0).getV(0), platform.getCube(0, 0).getV(3)));
        }
        
        if (keyboard.keyDownOnce(KeyEvent.VK_LEFT)) // if the user presser the arrow up key the player starts moving upwards
        {
            player.getPlayer().setVelocity(new GVector3d(platform.getCube(0, 0).getV(1), platform.getCube(0, 0).getV(0)));
        }   
        
        if (keyboard.keyDownOnce(KeyEvent.VK_DOWN)) // if the user presser the arrow up key the player starts moving upwards
        {
            player.getPlayer().setVelocity(new GVector3d(platform.getCube(0, 0).getV(3), platform.getCube(0, 0).getV(0)));
        }
        
        if (keyboard.keyDownOnce(KeyEvent.VK_RIGHT)) // if the user presser the arrow up key the player starts moving upwards
        {
            player.getPlayer().setVelocity(new GVector3d(platform.getCube(0, 0).getV(0), platform.getCube(0, 0).getV(1)));
        }
        
        if (keyboard.keyDownOnce(KeyEvent.VK_NUMPAD4)) //moves the crane left   
        {
            Reset[0] += 10;
            
            m.setTransMatrix(-10, 0, 0);
            ball.apply(m);
            platform.apply(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].apply(m);
                  outer_platform[i].apply(m);
                  outer_border[i].apply(m);
            }
            player.apply(m);
            
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_NUMPAD8)) // moves the crane upwards
        {
            Reset[1] += 10;
            
            m.setTransMatrix(0, -10, 0);
            ball.apply(m);
            platform.apply(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].apply(m);
                  outer_platform[i].apply(m);
                  outer_border[i].apply(m);

            }
            player.apply(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_NUMPAD6)) // moves the crane right
        {
            Reset[0] += -10;
            
            m.setTransMatrix(10, 0, 0);
            ball.apply(m);
            platform.apply(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].apply(m);
                  outer_platform[i].apply(m);
                  outer_border[i].apply(m);
            }
            player.apply(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_NUMPAD2)) // moves the crane down
        {
            Reset[1] += -10;
            
            m.setTransMatrix(0, 10, 0);
            ball.apply(m);
            platform.apply(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].apply(m);
                  outer_platform[i].apply(m);
                  outer_border[i].apply(m);
            }
            player.apply(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_PAGE_UP)) // moves the crane away from you
        {
            Reset[2] += 10;
            
            m.setTransMatrix(0, 0, -10);
            ball.apply(m);
            platform.apply(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].apply(m);
                  outer_platform[i].apply(m);
                  outer_border[i].apply(m);
            }
            player.apply(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_PAGE_DOWN)) // moves the crane closer to you
        {
            Reset[2] += -10;
            
            m.setTransMatrix(0, 0, 10);
            ball.apply(m);
            platform.apply(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].apply(m);
                  outer_platform[i].apply(m);
                  outer_border[i].apply(m);
            }
            player.apply(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_C)) //moves the pyramid to the center of the screen
        {
            GPoint3d center = platform.getPlatform().center();
            m.setTransMatrix(getCanvasWidth() / 2 - center.getX(), getCanvasHeight() / 2 - center.getY(), 0);
            ball.apply(m);
            platform.apply(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].apply(m);
                  outer_platform[i].apply(m);
                  outer_border[i].apply(m);
            }
            player.apply(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_X)) // Rotates the crane around the x axis
        {
            Reset[3] += -0.157079633;
            
            GPoint3d center = platform.getPlatform().center();
            m.setTransMatrix(-center.getX(), -center.getY(), -center.getZ());
            cm.setXRotMatrix(0.157079633);
            m.multiply(cm);
            cm.setTransMatrix(center.getX(), center.getY(), center.getZ());
            m.multiply(cm);
            ball.turn(m);
            platform.turn(m);
            for (int i=0;i<4;i++)
            {
                inner_border[i].turn(m);
                outer_platform[i].turn(m);
                outer_border[i].turn(m);
            }
            player.turn(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_Y)) // Rotates the crane around the y axis
        {
            Reset[4] += -0.157079633;
            
            GPoint3d center = platform.getPlatform().center();
            m.setTransMatrix(-center.getX(), -center.getY(), -center.getZ());
            cm.setYRotMatrix(0.157079633);
            m.multiply(cm);
            cm.setTransMatrix(center.getX(), center.getY(), center.getZ());
            m.multiply(cm);
            ball.turn(m);
            platform.turn(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].turn(m);
                  outer_platform[i].turn(m);
                  outer_border[i].turn(m);
            }
            player.turn(m);
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_Z)) // Rotates the crane around the z axis
        {
            Reset[5] += -0.157079633;
            
            GPoint3d center = platform.getPlatform().center();
            m.setTransMatrix(-center.getX(), -center.getY(), -center.getZ());
            cm.setZRotMatrix(0.157079633);
            m.multiply(cm);
            cm.setTransMatrix(center.getX(), center.getY(), center.getZ());
            m.multiply(cm);
            ball.turn(m);
            platform.turn(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].turn(m);
                  outer_platform[i].turn(m);
                  outer_border[i].turn(m);
            }
            player.turn(m);
        }
        
        if (keyboard.keyDownOnce(KeyEvent.VK_R)) // Resets the rotations and translations
        {
            GPoint3d center = platform.getPlatform().center();
           
            //reset the Z axes rotation
            m.setTransMatrix(-center.getX(), -center.getY(), -center.getZ());
            cm.setZRotMatrix(Reset[5]);
            m.multiply(cm);
            cm.setTransMatrix(center.getX(), center.getY(), center.getZ());
            m.multiply(cm);
            
            //reset the Y axes rotation
            cm.setTransMatrix(-center.getX(), -center.getY(), -center.getZ());
            m.multiply(cm);
            cm.setYRotMatrix(Reset[4]);
            m.multiply(cm);
            cm.setTransMatrix(center.getX(), center.getY(), center.getZ());
            m.multiply(cm);
            
            //reset the X axes rotation
            cm.setTransMatrix(-center.getX(), -center.getY(), -center.getZ());
            m.multiply(cm);
            cm.setXRotMatrix(Reset[3]);
            m.multiply(cm);
            cm.setTransMatrix(center.getX(), center.getY(), center.getZ());
            m.multiply(cm);
            
            //reset the translations
            cm.setTransMatrix(Reset[0], Reset[1], Reset[2]);
            m.multiply(cm);
            
            ball.turn(m);
            platform.turn(m);
            for (int i=0;i<4;i++)
            {
                  inner_border[i].turn(m);
                  outer_platform[i].turn(m);
                  outer_border[i].turn(m);
            }
            player.getPlayer().turn(m);
            
            for (int i=0;i<6;i++)
            {
                Reset[i] = 0;
            }
        }
        
        //<< ---- - -- - - - - - - - - - - - - here start the code the moves the ball and the player >>
        
        //moves the Player
        setPath(player.getPlayer());
        //cross();
        if (player.getPlayer().intersects(outer_border[0]) || player.getPlayer().intersects(outer_border[1]) || player.getPlayer().intersects(outer_border[2]) || player.getPlayer().intersects(outer_border[3]))
        {
            player.getPlayer().setVelocity(new GVector3d());
        }
        player.getPlayer().move();
        
        //moves the ball and changes the balls velocity if the ball hits the inner borders or goes into the deleted aray
        if (platform.Lose(ball))
        {
            m.setTransMatrix(player.getRespawn().getX() - player.getPlayer().getV(0).getX(), player.getRespawn().getY() - player.getPlayer().getV(0).getY(), player.getRespawn().getZ() - player.getPlayer().getV(0).getZ());
            player.getPlayer().apply(m);
            player.setInside(false);
            player.getPlayer().setVelocity(new GVector3d());
            platform.Clear();
            player.defeated();
        }
        if (BallOutside())
        {
            CubeRicochet(ball);
            return;
        }
        if (moveball)
            ball.move();
        WallRicochet(ball); // changes the velocity of the ball if it hits the wall
        
        //<<--------- check the outcome of the game ---------->>
         if (player.getLife() == 0)
             End = true;
         if (platform.win())
             End = true;
         
    }
    
    private void setPath(GBody3d body) // adds to the path the cubes that the player crossed 
    {
        for(int i=0; i<platform.getFinal().length; i++)
        {
            for(int j=0; j<platform.getFinal()[0].length; j++)
            {
                if (platform.getCube(i, j).pointIsInside(player.getPlayer().getV(0)))
                {
                    platform.addPath(i, j);
                    if (!platform.getFinal()[i][j] && player.isInside())
                    {
                        platform.Delete(ball);
                        platform.Clear();
                        player.setInside(false);
                    }
                    else
                        if(platform.getFinal()[i][j])
                            player.setInside(true);
                    return;
                }
            }
        }
        if (player.isInside())
        {
            platform.Delete(ball);
            platform.Clear();
            player.setInside(false);
        }
        
    }

    private void CubeRicochet(GBody3d ball) // if the ball intersects with a cube that is "dead" then this function changes the ball's velocity according the which side of the cube the ball hit
    {
        GVector3d velocity = new GVector3d(ball.getVelocity()) ;
        GVector3d wall;
        GVector3d normal;
        //assuming the ball hit the top or bottom
        wall = new GVector3d(inner_border[0].getV(0), inner_border[0].getV(1));
        normal = inner_border[0].getFaceNormal(0);
        if (Math.acos(velocity.dot(wall)/(velocity.length()*wall.length())) > Math.PI/2) // checks if the wall vector is in the right direction
            wall = wall.scalarProd(-1);
        if (Math.acos(velocity.dot(normal)/(velocity.length()*normal.length())) < Math.PI/2) // checks if the normal vector is in the right direction
            normal = normal.scalarProd(-1);
        //set the new velocity & move the ball
        ball.setVelocity(Ricochet(velocity, wall, normal));
        ball.move();
        //check if the ball is still outside
        if (BallOutside())
        {
            //return the ball to the previous location
            ball.setVelocity(ball.getVelocity().scalarProd(-1));
            ball.move();
            
            //do the same but for the left or right wall
            wall = new GVector3d(inner_border[0].getV(0), inner_border[0].getV(3));
            normal = inner_border[1].getFaceNormal(1);
            if (Math.acos(velocity.dot(wall)/(velocity.length()*wall.length())) > Math.PI/2) // checks if the wall vector is in the right direction
               wall = wall.scalarProd(-1);
            if (Math.acos(velocity.dot(normal)/(velocity.length()*normal.length())) < Math.PI/2) // checks if the normal vector is in the right direction
               normal = normal.scalarProd(-1);
            //set the new velocity & move the ball
            ball.setVelocity(Ricochet(velocity, wall, normal));
            ball.move();
        }
        moveball = true;
        
    }
 
    private void WallRicochet(GBody3d ball) // sets the balls velocity if it hits of the inner borders 
    {
        GVector3d wall;
        if (ball.intersects(inner_border[0])) // if the ball hits the first wall
        {
            wall = new GVector3d(inner_border[0].getV(0), inner_border[0].getV(1));  // sets teh vector of the wall
            if (Math.acos(ball.getVelocity().dot(wall) / (ball.getVelocity().length() * wall.length())) < Math.PI / 2) // if the angle between the speed vector and the wall vector is less then PI/2 then the wall vector is correct
            {
                ball.setVelocity(Ricochet(ball.getVelocity(), new GVector3d(inner_border[0].getV(0), inner_border[0].getV(1)), inner_border[0].getFaceNormal(0)));
                return;
            }
            ball.setVelocity(Ricochet(ball.getVelocity(), new GVector3d(inner_border[0].getV(1), inner_border[0].getV(0)), inner_border[0].getFaceNormal(0)));
            return;
        }

        if (ball.intersects(inner_border[1]))  // if the ball hits the second wall
        {
            wall = new GVector3d(inner_border[0].getV(1), inner_border[0].getV(2));
            if (Math.acos(ball.getVelocity().dot(wall) / (ball.getVelocity().length() * wall.length())) < Math.PI / 2)
            {
                ball.setVelocity(Ricochet(ball.getVelocity(), wall, inner_border[1].getFaceNormal(1)));
                return;
            }
            ball.setVelocity(Ricochet(ball.getVelocity(), wall.scalarProd(-1), inner_border[1].getFaceNormal(1)));
            return;
        }

        if (ball.intersects(inner_border[2]) ) // if the ball hits the third wall
        {
            wall = new GVector3d(inner_border[0].getV(2), inner_border[0].getV(3));
            if (Math.acos(ball.getVelocity().dot(wall) / (ball.getVelocity().length() * wall.length())) < Math.PI / 2)
            {
                ball.setVelocity(Ricochet(ball.getVelocity(), wall, inner_border[2].getFaceNormal(2)));
                return;
            }
            ball.setVelocity(Ricochet(ball.getVelocity(), wall.scalarProd(-1), inner_border[2].getFaceNormal(2)));
            return;
        }
        
        if (ball.intersects(inner_border[3]))  // if the ball hits the fourth wall
        {
            wall = new GVector3d(inner_border[0].getV(0), inner_border[0].getV(3));
            if (Math.acos(ball.getVelocity().dot(wall) / (ball.getVelocity().length() * wall.length())) < Math.PI / 2)
            {
                ball.setVelocity(Ricochet(ball.getVelocity(), wall, inner_border[3].getFaceNormal(3)));
                return;
            }
            ball.setVelocity(Ricochet(ball.getVelocity(), wall.scalarProd(-1), inner_border[3].getFaceNormal(3)));
        }

    }
    
    private GVector3d Ricochet(GVector3d speed ,GVector3d wall ,GVector3d wall_norm) // return the speed vector of the ball after it hits the wall
    {
        speed = speed.scalarProd(-1); // flips the speed vector (in order to find the angle between the wall_norm and the speed)
        double angle = Math.acos(speed.dot(wall_norm)/(speed.length()*wall_norm.length())); // finds the angle between the speed vector and the wall_norm vector
        wall = wall.scalarProd(Math.sin(angle)*speed.length()/wall.length()); // sets a new value to wall according the angle between wall_norm and speed
        wall_norm = wall_norm.scalarProd(Math.cos(angle)*speed.length()/wall_norm.length()); // sets a new value to wall_norm according the angle between wall_norm and speed
        return wall.add(wall_norm); // return the addition of wall and wall_norm which is actually the new vector speed
    }

    private boolean BallOutside() // checks if the ball is outside of the Area that is left
    {
        boolean [][] temp = platform.getFinal();
        for (int i = 0; i < temp.length; i++)
        {
            for (int j = 0; j < temp[0].length; j++)
            {
                if(!temp[i][j])
                    if(platform.getCube(i, j).intersects(ball))
                        return true;
            }
        }
        return false;
    }
    //o ציור אובייקטים ויזואליים
    public void draw(Graphics2D g) 
    {
        //because of the fact that when we have less cubes to draw everything starts moving faster we decided to draw the unecessary cubes anyway
        boolean [][] temp = platform.getFinal();
        for (int i=0;i<temp.length;i++)
        {
            for (int j=0;j<temp[0].length;j++)
            {
                if(!temp[i][j])
                    platform.getCube(i, j).draw(g, cam, lightme);
            }
        }
        
        //o מחיקת משטח גרפי
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getCanvasWidth(), getCanvasHeight());
       
        //draws the help
        if (this.help) 
            drawHelp(g);
        if (!End) 
        {
            if (platform.getPlatform().isFaceVisible(4, cam)) // if the platforms are face up first draw platforms
            {
                drawPlatforms(g, cam, lightme); //draw all 5 platforms 
                if (ball.center().getZ() > player.getPlayer().center().getZ())
                {
                    ball.draw(g, cam, lightme); // draw the ball
                    player.getPlayer().draw(g, cam, lightme); // draw the player
                }
                else
                {
                    player.getPlayer().draw(g, cam, lightme); // draw the player
                    ball.draw(g, cam, lightme); // draw the ball
                }
            } 
            else // first draw balls and player the platforms 
            {
                if (ball.center().getZ() > player.getPlayer().center().getZ())
                {
                    ball.draw(g, cam, lightme); // draw the ball
                    player.getPlayer().draw(g, cam, lightme); // draw the player
                }
                else
                {
                    player.getPlayer().draw(g, cam, lightme); // draw the player
                    ball.draw(g, cam, lightme); // draw the ball
                }
                drawPlatforms(g, cam, lightme);
            }

            //draw life
            for (int i = 0; i < player.getLife(); i++)
            {
                life[i].draw(g, cam);
            }

            
        }
        else
        {
            drawEnd(g);
        }
       
    }

    public void drawPlatforms(Graphics2D g, GPoint3d cam, GLight3d lightme) //draws the platfrom in the cetner and all the four surounding outer platforms in the right order
    {
        if (outer_platform[1].isFaceVisible(1, cam) && outer_platform[3].isFaceVisible(3, cam))
        {
            outer_platform[1].draw(g, cam, lightme);
            outer_platform[3].draw(g, cam, lightme);
            drawCenter(g, cam, lightme); 
            return;
        }
        
        if (outer_platform[1].isFaceVisible(1, cam))
        {
            outer_platform[1].draw(g, cam, lightme);
            drawCenter(g, cam, lightme);
            outer_platform[3].draw(g, cam, lightme);
            return;
        }
       
        outer_platform[3].draw(g, cam, lightme);
        drawCenter(g, cam, lightme);   
        outer_platform[1].draw(g, cam, lightme);
    }
    
    public void drawCenter(Graphics2D g, GPoint3d cam, GLight3d lightme) // draws the platforms that are in the center (outer platform 0, outer platform 2 and the center platform(cubes collection)
    {
        if(outer_platform[0].isFaceVisible(0, cam) && outer_platform[2].isFaceVisible(2, cam))
        {
           outer_platform[0].draw(g, cam, lightme); 
           outer_platform[2].draw(g, cam, lightme);
           platform.draw(g, cam, lightme);
           return;
        }
        
        if (outer_platform[0].isFaceVisible(0, cam))
        {
            outer_platform[0].draw(g, cam, lightme);
            platform.draw(g, cam, lightme);
            outer_platform[2].draw(g, cam, lightme);
            return;
        }
        
        outer_platform[2].draw(g, cam, lightme);
        platform.draw(g, cam, lightme);
        outer_platform[0].draw(g, cam, lightme);
    }
    
    public void drawHelp(Graphics2D g)
    {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Help   : F12", 200, 30);
    }
    
    public void drawEnd(Graphics2D g)
    {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (player.getLife() == 0)
            g.drawString("You Loooose!!!", 200, 100);
        else
            g.drawString("You Winnnnn!!!", 200, 100);
    }

    public static void main(String[] args)
    {
        MyApp app = new MyApp();
        app.run();
    }
}
