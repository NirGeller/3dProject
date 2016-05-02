package core;
import geli.g3d.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

public class CubesCollection 
{
    // define the properties
    double count; // counts the number of the dead cubes
    GBody3d platform; 
    GBody3d [][] cubes; // holds all the cubes the build the platform
    boolean [][] Area; // all the cubes on one side of the path
    boolean [][] checked; // holds all the cubes that were already checked this time around
    boolean [][] Final; // hold all the dead cubes as false and the rest of the cubes as true 
    boolean Draw; // checks if the cubes that are true in Area should be deleted or the cubes that are false
    List <int []> path; // holds all the cubes that the player when over
    
    CubesCollection(double cubeEdge, int numRows, int numCols, double h, Color fill, Color frame)
    {
        //define variables
        count = 0;
        GMatrix3d m = new GMatrix3d();
        path = new ArrayList<int []>();
        Area = new boolean[numRows][numCols];
        Draw = false;
        checked = new boolean[numRows][numCols];
        Final = new boolean[numRows][numCols];
        
        for (int i=0;i<numRows; i++)
        {
            for (int j=0; j<numCols; j++)
            {
                Final[i][j]= true;
            }
        }
        cubes = new GBody3d[numRows][numCols];
        
        //set the properties
        m.setTransMatrix(0, cubeEdge, 0);
        platform = GBody3d.createBox(0, 0, 0, cubeEdge * numRows, h,  cubeEdge * numCols);
        platform.apply(m);
        // set all the cubes
        for (int i=0;i<numRows; i++)
        {
            for (int j=0; j<numCols; j++)
            {
                cubes[i][j] = GBody3d.createBox(0, 0, 0, cubeEdge, cubeEdge, cubeEdge);
                cubes[i][j].setFillColor(fill);
                cubes[i][j].setFrameColor(frame);
                m.setTransMatrix(j*cubeEdge, 0, i*cubeEdge);
                cubes[i][j].apply(m);
            }
        }
        
    }
    
    public void addPath(int row,int col) // add a certain cube the the path list
    {
        int [] temp = {row,col};
        cubes[row][col].setFillColor(Color.yellow); // for debugging reasons - need to be deleted
        path.add(temp);
    }
    
    public boolean isPath(int row, int col) // check if a certain location is inside the list
    {
        for (int [] check : path)
        {
            if (row == check[0] && col == check[1])
                return true;
        }
        return false;
    }
    
    public void Clear()//clears all the data for the next run
    {
        path.clear();
        for (int i=0;i<checked.length; i++)
        {
            for (int j=0; j<checked.length; j++)
            {
                checked[i][j] = false;
                cubes[i][j].setFillColor(Color.red);
                Area[i][j] = false;
            }
        }
       
    }
    
    public void FindArea(GBody3d ball) // checks if the ball is on of the cubes inside Area or not
    {
        for (int i=0;i<Area.length; i++)
        {
            for (int j=0; j<Area[i].length; j++)
            {
                if(ball.intersects(cubes[i][j]))
                {
                    if(Area[i][j] == false)
                    {
                        Draw = false;
                        return;
                    }  
                    Draw = true;
                    return;
                }
            }
            
        }
    }
    
    public void Delete(GBody3d ball) // deletes all the selected cubes
    {
        for(int i=0; i<cubes.length; i++)
        {
            for(int j=0; j<cubes[0].length;j++)
            {
                if (!isPath(i, j) && Final[i][j])
                {
                    Find(i,j);
                    FindArea(ball);
                    Merge();
                    return;
                }
            }
        }
        
       
    }
    
    public void Find(int row, int col) // a recursive function that finds all the cubes on one side of the path
    {
        if (!isPath(row, col))
        {
            Area[row][col] = true;
            checked[row][col] = true;
            if (row > 0)
                if (!checked[row - 1][col] && Final[row -1][col])
                    Find(row - 1, col);
            if (row + 1 < cubes.length)
                if (!checked[row + 1][col] && Final[row +1][col] )
                    Find(row + 1, col);
            if (col > 0)
                if (!checked[row][col - 1] && Final[row][col -1])
                    Find(row, col -1);
            if (col + 1 < cubes[0].length)
                if (!checked[row][col + 1] && Final[row][col +1])
                    Find(row, col + 1);
        }
    }
    
    public void Merge() // adds all the cubes that are not on the same side  of the path as the ball to Final as false 
    {
        
        for(int i = 0; i < Area.length; i++)
        {
            for (int j = 0; j < Area[0].length; j++)
            {
                if (Area[i][j] != Draw)
                {
                    if (Final[i][j])
                        count++;
                    Final[i][j] = false;
                    
                }
            }                
        }
    }
    
    public boolean Lose(GBody3d ball) // checks if the player lost
    {
        for(int [] position : path)
        {
            if (ball.intersects(cubes[position[0]][position[1]]))
                return true;
        }
        return false;
    }
    
    public boolean  win()
    {
        if (count / (double)(cubes.length*cubes[0].length) > 0.70)
            return true;
        return false;
    }
    
    public void draw(Graphics2D g, GPoint3d c, GLight3d lightme) 
    {
        if(platform.isFaceVisible(4, c))
        {
            platform.draw(g, c, lightme);
            drawCubes(g, c, lightme);
        }
        else
        {
            drawCubes(g, c, lightme);
            platform.draw(g, c, lightme);
        }
    }
    
    public void drawCubes(Graphics2D g,GPoint3d c, GLight3d lightme)//draw the columns
    {
        //define variables
        int index = 0;
 
        //find the index at which the face at index 0 is no longer visible(that changes the order in which you draw the cubes)
        for(; index < cubes.length; index++)
        {
            if(!cubes[index][0].isFaceVisible(0, c))
            {
                break;
            }
        }
        //draws the rows from the top up to the row at index (not including)
        for(int i=0;i<index;i++)
        {
            drawRow(g, c, i, lightme);
        }
        //draws the rest of the rows from the bottom up to the row at index (including)
        for(int i = cubes.length - 1; i >= index; i--)
        {
            drawRow(g, c, i, lightme);
        }
    }
    
   
    public void drawRow(Graphics2D g, GPoint3d c,int row, GLight3d lightme) //draw the rows
    {
        //define variables
        int index = 0;
        int [] location = new int[2];
        //find the index at which the right face is no longer visible(that changes the order in which you draw the cubes)
        for( ;index<cubes[row].length;index++)
        {
            if (!cubes[row][index].isFaceVisible(1, c))
            {
                break;
            }
        }
        //draws the cubes from the left cube up to the cube at index (not including)
        for(int i=0; i<index; i++)
        {
            if (Final[row][i])
              cubes[row][i].draw(g, c, lightme);
        }
        //draws the rest of the cubes from the right up to the cube at index (including)
        for(int i = cubes[row].length - 1; i >= index; i--)
        {
            if (Final[row][i])
              cubes[row][i].draw(g, c, lightme);
        }
    }
    
    public void apply(GMatrix3d m)
    {
        platform.apply(m);
         for (int i=0;i<cubes.length; i++)
         {
            for (int j=0; j<cubes[i].length; j++)
            {
                cubes[i][j].apply(m);
            }
        }
    }
    
    public void turn(GMatrix3d m)
    {
        platform.turn(m);
         for (int i=0;i<cubes.length; i++)
         {
            for (int j=0; j<cubes[i].length; j++)
            {
                cubes[i][j].turn(m);
            }
        }
    }

    public GBody3d getCube(int row, int col)
    {
        return cubes[row][col];
    }

    public void setCube(int row, int col, GBody3d cube)
    {
        cubes[row][col] = cube;
    }

    public GBody3d getPlatform()
    {
        return platform;
    }

    public boolean[][] getFinal() 
    {
        return Final;
    }
    
    
}
