import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class Main extends Applet implements Runnable, KeyListener {

    // Double Buffered Graphics
    private Graphics gfx;
    private Image img;

    // Player Points
    private int bluePoints = 0;
    private int pinkPoints = 0;

    // Random used in game
    private Random rand = new Random();

    // Slow Fruit Effects
    private boolean snakeOneSlowed = false;
    private boolean snakeTwoSlowed = false;

    private int snakeOneSlowTimer = 0;
    private int snakeTwoSlowTimer = 0;

    // Grow Fruit
    private boolean growFruitAvailable = false;
    private int growFruitX = 40 + rand.nextInt(23) * 40;
    private int growFruitY = 40 + rand.nextInt(23) * 40;

    // Slow Fruit
    private boolean slowFruitAvailable = false;
    private int slowFruitX = 40 + rand.nextInt(23) * 40;
    private int slowFruitY = 40 + rand.nextInt(23) * 40;

    // First Fruit
    private int fruitOneX = 40 + rand.nextInt(23) * 40;
    private int fruitOneY = 40 + rand.nextInt(23) * 40;

    // Second Fruit
    private int fruitTwoX = 40 + rand.nextInt(23) * 40;
    private int fruitTwoY = 40 + rand.nextInt(23) * 40;

    // Snake One Head Position
    private int snakeOneHeadX = 760;
    private int snakeOneHeadY = 240;

    // Snake Two Head Position
    private int snakeTwoHeadX = 200;
    private int snakeTwoHeadY = 240;

    // Snake One Tail Positions
    private int snakeOneLength = 1;
    private List<Integer> snakeOneTailX = new ArrayList<>();
    private List<Integer> snakeOneTailY = new ArrayList<>();

    // Snake Two Tail Positions
    private int snakeTwoLength = 1;
    private List<Integer> snakeTwoTailX = new ArrayList<>();
    private List<Integer> snakeTwoTailY = new ArrayList<>();

    // Snake X and Y Movements
    private int snakeOneXMovement = 0, snakeOneYMovement = 0;
    private int snakeTwoXMovement = 0, snakeTwoYMovement = 0;

    // Pausing/Stopping the Game
    private boolean pauseGame = false;
    private boolean stopGame = false;
    private boolean gameOver = false;

    // Keys currently pressed
    private Set<Integer> keysPressed = new HashSet<>();


    // RENDERING

    public void paint(Graphics g){
        renderBackground();
        renderScores();
        renderFruit();

        if(!stopGame) {
            renderSnakes();
        }
        else if(gameOver){
            renderGameOver();
        }

        g.drawImage(img, 0, 0, this);
    }

    public void update(Graphics g){
        paint(g);
    }

    private void renderBackground() {
        gfx.setColor(Color.LIGHT_GRAY);
        gfx.fillRect(0,0,1000,1200);
        gfx.setColor(Color.BLACK);
        gfx.fillRect(35,35,930,930);
        gfx.setColor(Color.LIGHT_GRAY);
        gfx.fillRect(40,40,920,920);

        int border = 1;
        for(int i = 40; i <= 920; i+=40){
            for(int j = 40; j <= 920; j+=40){
                gfx.setColor(Color.WHITE);
                gfx.fillRect(i+border, j+border, 40-2*border, 40-2*border);
            }
        }


        gfx.setColor(Color.WHITE);
        gfx.fillOval(690,570,100,100);
        gfx.setColor(Color.BLACK);
        gfx.fillOval(710,590,60,60);

        gfx.setColor(Color.WHITE);
        gfx.fillOval(290,370,100,100);
        gfx.setColor(Color.BLACK);
        gfx.fillOval(310,390,60,60);
    }

    private void renderScores() {

        gfx.setColor(Color.BLACK);
        gfx.fillRect(40,980 ,180,50);
        gfx.setColor(Color.WHITE);
        gfx.fillRect(42,982,176,46);

        gfx.setColor(Color.BLUE);
        gfx.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        gfx.drawString("Blue: ", 60, 1020);
        gfx.drawString(Integer.toString(bluePoints), 160, 1020);

        gfx.setColor(Color.BLACK);
        gfx.fillRect(780,980 ,180,50);
        gfx.setColor(Color.WHITE);
        gfx.fillRect(782,982,176,46);

        gfx.setColor(Color.RED);
        gfx.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        gfx.drawString("Red: ", 800, 1020);
        gfx.drawString(Integer.toString(pinkPoints), 900, 1020);
    }

    private void renderFruit(){
        int border = 2;

        gfx.setColor(Color.BLACK);
        gfx.fillOval(fruitOneX, fruitOneY+7,40,33);
        gfx.setColor(Color.RED);
        gfx.fillOval(fruitOneX + border, fruitOneY+7+border,40-2*border,33-2*border);
        gfx.setColor(Color.BLACK);
        gfx.fillRect(fruitOneX +17, fruitOneY,6,20);

        gfx.setColor(Color.BLACK);
        gfx.fillOval(fruitTwoX, fruitTwoY+7,40,33);
        gfx.setColor(Color.ORANGE);
        gfx.fillOval(fruitTwoX+border, fruitTwoY+7+border,40-2*border,33-2*border);
        gfx.setColor(Color.BLACK);
        gfx.fillRect(fruitTwoX +17, fruitTwoY,6,20);

        if(slowFruitAvailable) {

            gfx.setColor(Color.BLACK);
            gfx.fillOval(slowFruitX, slowFruitY+7,40,33);
            gfx.setColor(Color.CYAN);
            gfx.fillOval(slowFruitX + border, slowFruitY+7+border,40-2*border,33-2*border);
            gfx.setColor(Color.BLACK);
            gfx.fillRect(slowFruitX +17, slowFruitY,6,20);
        }
        if(growFruitAvailable) {

            gfx.setColor(Color.BLACK);
            gfx.fillOval(growFruitX, growFruitY+7,40,33);
            gfx.setColor(Color.MAGENTA);
            gfx.fillOval(growFruitX + border, growFruitY+7+border,40-2*border,33-2*border);
            gfx.setColor(Color.BLACK);
            gfx.fillRect(growFruitX +17, growFruitY,6,20);
        }
    }

    private void renderGameOver(){
        gfx.setColor(Color.RED);
        gfx.setFont(new Font("TimesRoman", Font.PLAIN, 80));
        gfx.drawString("GAME OVER...", 250, 400);
        gfx.drawString("SCORE:", 300, 500);
        gfx.drawString(Integer.toString(snakeOneLength), 600, 500);
    }

    private void renderSnakes(){
        for(int i = 0; i < snakeOneLength; i++) {

            gfx.setColor(Color.BLUE);
            gfx.fillRect(snakeOneTailX.get(i)+5, snakeOneTailY.get(i)+5,30,30);
        }

        gfx.setColor(Color.BLUE);
        gfx.fillRect(snakeOneHeadX +2, snakeOneHeadY +2,36,36);

        for(int i = 0; i < snakeTwoLength; i++) {
            gfx.setColor(Color.RED);
            gfx.fillRect(snakeTwoTailX.get(i)+5, snakeTwoTailY.get(i)+5,30,30);
        }

        gfx.setColor(Color.RED);
        gfx.fillRect(snakeTwoHeadX +2, snakeTwoHeadY +2,36,36);
    }


    // Game Logic and Loop

    public void init(){
        this.resize(1000,1050);
        this.addKeyListener(this);

        img = createImage(1000,1050);
        gfx = img.getGraphics();

        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        for(;;){
            if(!pauseGame) {

                checkFruitsConsumption();

                try {
                    Thread.sleep(speed());
                }	catch(InterruptedException e){
                    e.printStackTrace();
                }

                stepSnakes();

                if(!stopGame) {
                    repaint();
                } else {
                    readyNewGame();
                }
            }

            else try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    private void checkFruitsConsumption(){
        if(fruitOneX == snakeOneHeadX && fruitOneY == snakeOneHeadY) {
            fruitOneX = 40 + rand.nextInt(23) * 40;
            fruitOneY = 40 + rand.nextInt(23) * 40;
            snakeOneLength++;
        }
        if(fruitOneX == snakeTwoHeadX && fruitOneY == snakeTwoHeadY) {
            fruitOneX = 40 + rand.nextInt(23) * 40;
            fruitOneY = 40 + rand.nextInt(23) * 40;
            snakeTwoLength++;
        }
        if(fruitTwoX == snakeOneHeadX && fruitTwoY == snakeOneHeadY) {
            fruitTwoX = 40 + rand.nextInt(23) * 40;
            fruitTwoY = 40 + rand.nextInt(23) * 40;
            snakeOneLength++;
        }
        if(fruitTwoX == snakeTwoHeadX && fruitTwoY == snakeTwoHeadY) {
            fruitTwoX = 40 + rand.nextInt(23) * 40;
            fruitTwoY = 40 + rand.nextInt(23) * 40;
            snakeTwoLength++;
        }
        if(slowFruitX == snakeTwoHeadX && slowFruitY == snakeTwoHeadY) {
            slowFruitX = 40 + rand.nextInt(23) * 40;
            slowFruitY = 40 + rand.nextInt(23) * 40;
            slowFruitAvailable = false;
            snakeOneSlowed = true;

        }
        if(slowFruitX == snakeOneHeadX && slowFruitY == snakeOneHeadY) {
            slowFruitX = 40 + rand.nextInt(23) * 40;
            slowFruitY = 40 + rand.nextInt(23) * 40;
            slowFruitAvailable = false;
            snakeTwoSlowed = true;
        }
        if(growFruitX == snakeTwoHeadX && growFruitY == snakeTwoHeadY && growFruitAvailable) {
            growFruitX = 40 + rand.nextInt(23) * 40;
            growFruitY = 40 + rand.nextInt(23) * 40;
            snakeTwoLength +=3;
        }
        if(growFruitX == snakeOneHeadX && growFruitY == snakeOneHeadY && growFruitAvailable) {
            growFruitX = 40 + rand.nextInt(23) * 40;
            growFruitY = 40 + rand.nextInt(23) * 40;
            snakeOneLength +=3;
        }
        slowFruitAvailable = snakeOneLength % 4 == 0 || snakeTwoLength % 4 == 0;

        growFruitAvailable = snakeOneLength + snakeTwoLength % 10 == 5 || snakeOneLength + snakeTwoLength % 10 == 6 || snakeOneLength + snakeTwoLength % 10 == 7;
    }

    private void stepSnakes(){

        if(snakeOneHeadX == snakeTwoHeadX && snakeOneHeadY == snakeTwoHeadY) {
            if(snakeOneXMovement == 0 && snakeOneYMovement == 0) {
                bluePoints++;
            }
            else if(snakeTwoXMovement == 0 && snakeTwoYMovement == 0) {
                pinkPoints++;
            }
            stopGame = true;
        }
        if(snakeOneHeadY == 920 && snakeOneYMovement == 40) {
            snakeOneHeadY = 0;

        }
        else if(snakeOneHeadY == 40 && snakeOneYMovement == -40) {
            snakeOneHeadY = 960;
        }
        if(snakeOneHeadX == 920 && snakeOneXMovement == 40) {
            snakeOneHeadX = 0;
        }
        else if(snakeOneHeadX == 40 && snakeOneXMovement == -40) {
            snakeOneHeadX = 960;
        }
        if(snakeTwoHeadY == 920 && snakeTwoYMovement == 40) {
            snakeTwoHeadY = 0;

        }
        else if(snakeTwoHeadY == 40 && snakeTwoYMovement == -40) {
            snakeTwoHeadY = 960;
        }
        if(snakeTwoHeadX == 920 && snakeTwoXMovement == 40) {
            snakeTwoHeadX = 0;
        }
        else if(snakeTwoHeadX == 40 && snakeTwoXMovement == -40) {
            snakeTwoHeadX = 960;
        }

        if(snakeTwoHeadX + snakeTwoXMovement == 720 && snakeTwoHeadY + snakeTwoYMovement == 600) {
            snakeTwoHeadX = 320- snakeTwoXMovement;
            snakeTwoHeadY = 400- snakeTwoYMovement;
        }
        else if(snakeTwoHeadX + snakeTwoXMovement == 320 && snakeTwoHeadY + snakeTwoYMovement == 400) {
            snakeTwoHeadX = 720- snakeTwoXMovement;
            snakeTwoHeadY = 600- snakeTwoYMovement;
        }
        if(snakeOneHeadX + snakeOneXMovement == 720 && snakeOneHeadY + snakeOneYMovement == 600) {
            snakeOneHeadX = 320- snakeOneXMovement;
            snakeOneHeadY = 400- snakeOneYMovement;
        }
        else if(snakeOneHeadX + snakeOneXMovement == 320 && snakeOneHeadY + snakeOneYMovement == 400) {
            snakeOneHeadX = 720- snakeOneXMovement;
            snakeOneHeadY = 600- snakeOneYMovement;
        }


        if(!snakeOneSlowed || snakeOneSlowTimer == 10) {

            snakeOneHeadX += snakeOneXMovement;
            snakeOneHeadY += snakeOneYMovement;
            snakeOneTailX.add(0, snakeOneHeadX);
            snakeOneTailY.add(0, snakeOneHeadY);
            snakeOneSlowTimer = 0;
            snakeOneSlowed = false;
        }
        else {
            snakeOneSlowTimer++;
        }
        if(!snakeTwoSlowed || snakeTwoSlowTimer == 10) {

            snakeTwoHeadX += snakeTwoXMovement;
            snakeTwoHeadY += snakeTwoYMovement;


            snakeTwoTailX.add(0, snakeTwoHeadX);
            snakeTwoTailY.add(0, snakeTwoHeadY);
            snakeTwoSlowTimer = 0;
            snakeTwoSlowed = false;
        }
        else {
            snakeTwoSlowTimer++;
        }


        for(int i = 1; i < snakeOneLength; i++	) {
            if(snakeOneTailX.get(i) == snakeOneHeadX && snakeOneTailY.get(i) == snakeOneHeadY) {
                stopGame = true;
                pinkPoints++;
            }
            if(snakeOneTailX.get(i) == snakeTwoHeadX && snakeOneTailY.get(i) == snakeTwoHeadY) {
                stopGame = true;
                bluePoints++;
            }
        }
        for(int i = 1; i < snakeTwoLength; i++	) {
            if(snakeTwoTailX.get(i) == snakeOneHeadX && snakeTwoTailY.get(i) == snakeOneHeadY) {
                stopGame = true;
                pinkPoints++;
            }
            if(snakeTwoTailX.get(i) == snakeTwoHeadX && snakeTwoTailY.get(i) == snakeTwoHeadY) {
                stopGame = true;
                bluePoints++;
            }
        }

    }

    private void readyNewGame(){
        System.out.println("starting new game");
        try{
            Thread.sleep(1300);
        }	catch(InterruptedException e){
            e.printStackTrace();
        }
        snakeOneHeadX = 760; snakeOneHeadY = 240; snakeTwoHeadX = 200; snakeTwoHeadY = 240;
        growFruitX = 40 + rand.nextInt(23) * 40;
        growFruitY = 40 + rand.nextInt(23) * 40;
        slowFruitX = 40 + rand.nextInt(23) * 40;
        slowFruitY = 40 + rand.nextInt(23) * 40;
        slowFruitAvailable = false;
        snakeOneSlowed = false; snakeTwoSlowed = false;
        fruitOneX = 40 + rand.nextInt(23) * 40;
        fruitOneY = 40 + rand.nextInt(23) * 40;
        fruitTwoX = 40 + rand.nextInt(23) * 40;
        fruitTwoY = 40 + rand.nextInt(23) * 40;
        snakeOneXMovement = 0; snakeOneYMovement = 0;
        snakeTwoXMovement = 0; snakeTwoYMovement = 0;
        stopGame = false;
        snakeOneLength = 1;
        snakeTwoLength = 1;
        snakeOneTailX.clear();
        snakeTwoTailX.clear();
        snakeTwoTailY.clear();
        snakeOneTailY.clear();
    }

    private int speed(){
        int speed = 150-(9*snakeOneLength);
        if(speed < 80) {
            speed = 80;
        }
        return speed;
    }




    // Key Listener Methods

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
        for(int a: keysPressed) {
            if(a == KeyEvent.VK_E) {
                gameOver = true;
            }
            if(a == KeyEvent.VK_W) {
                snakeTwoYMovement = -40;
                snakeTwoXMovement = 0;
            }
            if(a == KeyEvent.VK_S) {
                snakeTwoYMovement = 40;
                snakeTwoXMovement = 0;
            }
            if(a == KeyEvent.VK_A) {
                snakeTwoXMovement = -40;
                snakeTwoYMovement = 0;
            }
            if(a == KeyEvent.VK_D) {
                snakeTwoXMovement = 40;
                snakeTwoYMovement = 0;
            }
            if(a == KeyEvent.VK_UP) {
                snakeOneYMovement = -40;
                snakeOneXMovement = 0;
            }
            if(a == KeyEvent.VK_DOWN) {
                snakeOneYMovement = 40;
                snakeOneXMovement = 0;
            }
            if(a == KeyEvent.VK_LEFT) {
                snakeOneXMovement = -40;
                snakeOneYMovement = 0;
            }
            if(a == KeyEvent.VK_RIGHT) {
                snakeOneXMovement = 40;
                snakeOneYMovement = 0;
            }
            if(a == KeyEvent.VK_P) {
                if(pauseGame) {
                    pauseGame = false;
                    System.out.println("not paused");
                }
                else {
                    pauseGame = true;
                    System.out.println("paused");
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

}














