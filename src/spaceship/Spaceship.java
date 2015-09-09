
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    int rocketXPos;
    int rocketYPos;
    double rocketDir;
    int rocketXSpeed;
    int rocketYSpeed;
    boolean rocketRight;
    
   
    //int currentMissileIndex;
    int numMissiles = 20;
     Missile missile[];
   // int MissileXPos[]=new int[numMissiles];
    //int MissileYPos[]=new int[numMissiles];
   // boolean MissileVisible[]=new boolean[numMissiles];
    //boolean MissileRight[]=new boolean[numMissiles];
    
    
    //variables for star.
    int numStars=5;
    
    int starXPos[];
    int starYPos[];
    boolean starVisible[];
    boolean hit[] = new boolean[numStars];
    int whichStarHit;
    
    int score;
    int highScore;
    int health;
    boolean gameOver;
    
    
    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    rocketYSpeed++;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rocketYSpeed--;
                            
                } else if (e.VK_LEFT == e.getKeyCode()) 
                {
                    if(rocketXSpeed<15)
                    rocketXSpeed++;
                } 
                else if (e.VK_RIGHT == e.getKeyCode())
                {
                    if(rocketXSpeed>-15)
                    rocketXSpeed--;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                else if (e.VK_SPACE == e.getKeyCode()){
                    missile[Missile.currentMissileIndex].visible = true;
                    missile[Missile.currentMissileIndex].xpos = rocketXPos;
                    missile[Missile.currentMissileIndex].ypos = rocketYPos;
                    Missile.currentMissileIndex++;
                    if (Missile.currentMissileIndex >= Missile.numMissiles)
                        Missile.currentMissileIndex = 0;
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.black);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.black);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);

        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),rocketDir,1.0,1.0 );
        
        
        for (int index=0;index<missile.length;index++)
        {
            if (missile[index].visible)
            {
                g.setColor(Color.red);
                g.fillRect(getX(missile[index].xpos),getYNormal(missile[index].ypos),20,5);
            }
        }
        g.setColor(Color.yellow);
        for(int index=0;index<numStars;index++)
        {
            if(starVisible[index])
        drawCircle(getX(starXPos[index]),getYNormal(starYPos[index]),0.0,1.0,1.0);
        }
        
        g.setColor(Color.white);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("Score: " + score, 10, 50);
        g.setColor(Color.white);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("High Score: " + highScore, 10, 70);
        g.setColor(Color.white);
        g.setFont(new Font("Impact",Font.BOLD,15));
        g.drawString("Health: " + health, 10, 90);
        
        g.setColor(Color.red);
        if(health==3)
        g.setColor(Color.green);
        g.fillArc(10, 90, 20, 20, 90, 120);
        if(health>1)
        g.setColor(Color.green);
        g.fillArc(10, 90, 20, 20, 210, 120);
        if(health>0)
        g.setColor(Color.green);
        g.fillArc(10, 90, 20, 20, 330, 120);
        
        if (gameOver)
        {
            g.setColor(Color.white);
            g.setFont(new Font("Impact",Font.BOLD,60));
            g.drawString("GAME OVER", 100, 350);

        }
        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketXSpeed = 0;
        rocketYSpeed = 0;
        rocketDir=0;
        rocketRight=true;
        whichStarHit=-1;
        
        starXPos = new int[numStars];
        starYPos = new int[numStars];
        starVisible = new boolean[numStars];
        hit = new boolean[numStars];
        
        for(int index=0;index<numStars;index++)
        {
        starXPos[index] = (int)(Math.random()*getWidth2());
        starYPos[index] = (int)(Math.random()*getHeight2());
        hit[index] = false;
        starVisible[index]= true;
        }
        
        Missile.currentMissileIndex = 0;
        missile = new Missile [numMissiles];
        for (int index=0;index<missile.length;index++)
        {
            missile[index] = new Missile();;
        
        }
        if(score>highScore)
            highScore=score;
        score = 0;    
        health=3;
        gameOver = false;
        
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            reset();
            bgSound = new sound("starWars.wav");
        }
        if(gameOver)
            return;

        if(bgSound.donePlaying)
        {
            bgSound = new sound("starWars.wav");
        }
        
        rocketYPos+=rocketYSpeed;
            if(rocketYPos>=getHeight2() )
            {
            rocketYSpeed=0;
            rocketYPos=getHeight2();
            }
            
            if(rocketYPos<=0)
            {
                rocketYSpeed=0;
                rocketYPos=0;
            }
        
            if(rocketXSpeed>0)
            {
                rocketRight=false;
                
                if(rocketDir<180)
                rocketDir+=10;
            }
            if(rocketXSpeed<0)
            {
                rocketRight=true;
                
                if(rocketDir>0)
                rocketDir-=10;
            }
        
        boolean onehit= false;    
        for (int count=0;count<numMissiles;count++)
        {
           
                
            if (missile[count].visible && missile[count].right)
            {
                
                missile[count].xpos += 4;
                if (missile[count].xpos >= getWidth2())
                    missile[count].visible = false;
            }
            else if (missile[count].visible && !missile[count].right)
            {
                
                missile[count].xpos -= 4;
                if (missile[count].xpos >= getWidth2())
                    missile[count].visible = false;
            }
             if(!rocketRight && !missile[count].visible)
                missile[count].right=false;
             else if(rocketRight && !missile[count].visible)
                missile[count].right=true;
        }
        
        for (int count=0;count<missile.length;count++)
        {
            for (int index=0;index<numStars;index++)
            {
                if (starVisible[index]&&missile[count].visible)
                {
                    if (
                        starXPos[index]-20 < missile[count].xpos && 
                        starXPos[index]+20 > missile[count].xpos &&
                        starYPos[index]-20 < missile[count].ypos &&
                        starYPos[index]+20 > missile[count].ypos)
                    {
                        //alienExplodeVisible[index] = true;
                        //alienExplodeXPos[index] = MissileXPos[count];
                        //alienExplodeYPos[index] = MissileYPos[count];
                        starVisible[index] = false;
                        score++;
                    }
                }
            }
        }
           
        for(int index=0;index<numStars;index++)
        {
            starXPos[index]+=rocketXSpeed;
            if(starXPos[index]>getWidth2())
            {
                starXPos[index]=0;
                starYPos[index] = (int)(Math.random()*getHeight2());
                hit[index]=false;
                starVisible[index]=true;
            }
            if(starXPos[index]<0)
            {
                starXPos[index]=getWidth2();
                starYPos[index] = (int)(Math.random()*getHeight2());
                hit[index]=false;
                starVisible[index]=true;
            }
            
            if(rocketXPos+20>starXPos[index] && rocketXPos-20<starXPos[index] &&
               rocketYPos+20>starYPos[index] && rocketYPos-20<starYPos[index] &&starVisible[index])       
            {
                onehit=true;
                if(whichStarHit!=index)
                {
                
                whichStarHit=index;
                zsound = new sound("ouch.wav");
                health--;
                }
                
            }
            if(health==0)
                gameOver=true;
            
            
            
            
        }
        if(!onehit)
            {
            whichStarHit=-1;
            }
        
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
    ///////////////////////////////////////////////////////
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}
class Missile
{
    public static int currentMissileIndex;
    public static final int numMissiles = 20;
    
    
    public int xpos;
    public int ypos;
    public boolean visible;
    public boolean right;
    
    Missile()
    {
        visible=false;
        right=true;
    }
    
  
}