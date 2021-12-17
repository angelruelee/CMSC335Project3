import javax.swing.*;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

//coding reference: TrafficLightDemo.java from the CodeZip file
// An enumeration of the colors of a traffic light
//SWITCH phase is important because cars need to resume after pausing on RED
//but GREEN cannot be used because car.resume() messes with
//the pause button's car.resume()
enum Color {  
  RED, GREEN, YELLOW, SWITCH
}

//superclass for Traffic Light thread object
class TrafficLight implements Runnable {
    Thread thread; //thread for traffic to use in the main class
    private Color color; // enum for color
    private final boolean running = true; ///instantiate running
    private boolean stop = false; // false by default, but switched to true later
    private boolean changed = false; // true when the light has changed
    private boolean paused; // sets to true when paused
    private final JLabel trafficImage; //apply traffic image to this label

    public TrafficLight(JLabel trafficImage) {
    color = Color.GREEN; ///start with color green
    this.trafficImage = trafficImage; ///to apply icon to traffic labels in main class
    } 

    // start up the light
    public void run() { 
    while(running) { ///while thread is running
      try {
        switch(getColor()) {
          case GREEN: //in case of green, apply green image
            BufferedImage greenURL = ImageIO.read(this.getClass().getResourceAsStream("images/greenlight.png"));
            Image greenlight = greenURL;
            ImageIcon greenIcon = new ImageIcon(greenlight); 
            trafficImage.setIcon(greenIcon);
            Thread.sleep(5000); // green for 5 seconds
            break; 
          case YELLOW: //apply yellow image
            BufferedImage yellowURL = ImageIO.read(this.getClass().getResourceAsStream("images/yellowlight.png"));
            Image yellowlight = yellowURL;
            ImageIcon yellowIcon = new ImageIcon(yellowlight); 
            trafficImage.setIcon(yellowIcon);
            Thread.sleep(2000); // yellow for 2 seconds  
            break; 
          case RED: //apply red image
            BufferedImage redURL = ImageIO.read(this.getClass().getResourceAsStream("images/redlight.png"));
            Image redlight = redURL;
            ImageIcon redIcon = new ImageIcon(redlight); 
            trafficImage.setIcon(redIcon);
            Thread.sleep(5000); // red for 5 seconds
            break;
          case SWITCH: ////switch enum for red to green
            BufferedImage switchURL = ImageIO.read(this.getClass().getResourceAsStream("images/greenlight.png"));
            Image switchlight = switchURL;
            ImageIcon switchIcon = new ImageIcon(switchlight); 
            trafficImage.setIcon(switchIcon);
            Thread.sleep(10); // switch phase for 0.01 seconds, only to resume cars again
            break; 
        }
        synchronized(this) { //synchronized the threads
                while(paused) { //while pause is true
                    wait(); //wait if paused
                }
            }
        } catch (InterruptedException ex) {
            paused = true; //if  interrupted, pause
        } catch (IOException ex) {
            Logger.getLogger(TrafficLight.class.getName()).log(Level.SEVERE, null, ex);
        }
      changeColor(); ///change color after showing proper Icon
    }//end while 
    }//end run() 

    //change color
    synchronized void changeColor() { 
    switch(color) { 
      case SWITCH: 
        color = Color.GREEN; 
        break; 
      case RED: 
        color = Color.SWITCH;
        break;
      case YELLOW: 
        color = Color.RED; 
        break; 
      case GREEN: 
       color = Color.YELLOW; 
    } 
    changed = true;
    notify(); // signal that the light has changed 
    } 

    ///start a thread
    synchronized void start() {
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    synchronized void pause() {
        paused = true;
        notify(); //signal that pause value changed
    }
    ///resume existing thread
    synchronized void resume() {
        paused = false; //switch paused back to false
        notify(); //signal that pause value changed
    }

    ///get current color
    synchronized Color getColor() { 
    return color; 
    }
}///end of TrafficLight superclass