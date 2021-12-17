import javax.swing.*;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.util.Random;

//coding reference: TrafficLightDemo.java from the CodeZip file
///class for Car thread object so each car is separate thread
class Car implements Runnable {
    Thread thread; ///car thread
    private boolean running = true; ///instantiate running
    boolean paused; // set to true to pause the simulation 
    private int position; //position of a car
    private double speed; //speed of car
    private double scaledSpeed; //scaled speed of car
    Random random = new Random(); 
    private final JLabel carImage; //apply traffic image to this label
    private final JLabel carPosition; //apply traffic image to this label
    final JLabel carSpeed; //apply traffic image to this label
    
    public Car(JLabel carImage, JLabel carPosition, JLabel carSpeed) {
    this.position = random.nextInt(100); // start position is random from 1 to 250
    this.speed = 10 + random.nextInt(20); //start speed at random from 5 to 30
    this.scaledSpeed = random.nextInt(5); //scaled for labels in the diagram
    this.carImage = carImage; ///to apply icon to traffic labels in main class
    this.carPosition = carPosition;
    this.carSpeed = carSpeed;
    }

    // Start up the car
    public void run() {
    while(running){ ///while thread is running
        try {
            //extract image for cars and the traffic light diagrams 
            BufferedImage carURL = ImageIO.read(this.getClass().getResourceAsStream("images/car.png"));
            Image car = carURL;
            ImageIcon carIcon = new ImageIcon(car);
            carImage.setIcon(carIcon);
            
            while(position < 2999) { //while position is behind 3000 miles
                synchronized(this){ //sync these threads
                if(running) { //if running
                position+=scaledSpeed(); //add to start position whatever is from speed and scale it down by 1000
                Thread.sleep(15); //lag of 0.0015 second per refresh
                }//end if running
                while(paused) {
                    wait(); ///wait
                }
                }//end sync
                
                String positionText = String.valueOf(position); ///convert position Int to string
                carPosition.setText("Position: " + positionText + " m"); //set carPosition label to position text

                String speedText = String.valueOf(speed); ///convert speed Int to string
                carSpeed.setText("Speed: " + speedText + " mph"); //set carSpeed label to speed text
            }//end while   
        } catch (InterruptedException ex) {
            return;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }//end catch
    }//end while
    }///end of run 

    synchronized void start() { ///method to start the thread at press of button
        if(thread == null) { //if no thread
            thread = new Thread(this); //create new Thread
            thread.start(); //then start thread
        }
    }//end start

    synchronized void pause() {
        paused = true; //switch pause value to true
        notify(); //signal that pause value changed
    }//end pause
   
    synchronized void resume() {///resume existing thread
        paused = false; //switch paused back to false
        notify(); //signal that pause value changed
    }//end resume

    public int position() { //car x position for tracker
        return position;
    }//end position

    public double speed() { //car speed for tracker
        if(running) { //if car thread is running
                return speed;
        }else 
            speed = 0; //otherwise speed is 0
        return speed;
    }//end speed
    
    public double scaledSpeed() { //car speed for tracker
        scaledSpeed = speed/10;
        return scaledSpeed;
    }//end scaled speed  
}///end of Car superclass