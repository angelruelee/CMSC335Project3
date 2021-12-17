import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.util.Date;

///main class with the JFrame and components
public class Main extends JFrame implements Runnable, ChangeListener {

	///initialize all variables
    //simulator is always running, especially the time
    private static boolean running;
    //for when the threads are started
    private boolean startRunning;
    private int carCounter; //set counter for Add a Car button
    private int lightCounter; //set counter for Add a Light button
    //JLabels including ones for pictures
    static JLabel date = new JLabel(); //label for the running date
    static JLabel lightIconOne = new JLabel(); //label for first light image
    static JLabel lightIconTwo = new JLabel(); //label for 2nd light image
    static JLabel lightIconThree = new JLabel(); //label for 3rd light image
    static JLabel carIconOne = new JLabel(); //label for first car image
    static JLabel carIconTwo = new JLabel();
    static JLabel carIconThree = new JLabel();
    static JLabel firstPosition = new JLabel(); //label first car x pos.
    static JLabel secondPosition = new JLabel();
    static JLabel thirdPosition = new JLabel();
    static JLabel firstSpeed = new JLabel(); //label first car speed.
    static JLabel secondSpeed = new JLabel();
    static JLabel thirdSpeed = new JLabel();
    
    //JButtons for the start/stop, pause, and to add more cars and lights
    private final JButton start = new JButton("Start");
    private final JButton stop = new JButton("Stop");
    private final JButton pause = new JButton("Pause");
    private final JButton addACar = new JButton("Add a Car");
    private final JButton addTrafficLight = new JButton("Add a Light");
	
	///THREADS
    static Thread simulator; ///main thread to start simulation
    //traffic light threads
    TrafficLight firstLight = new TrafficLight(lightIconOne);
    TrafficLight secondLight = new TrafficLight(lightIconTwo);
    TrafficLight thirdLight = new TrafficLight(lightIconThree);
    //array of traffic lights to control them all at once
    TrafficLight[] trafficLightThreads = {firstLight, secondLight, 
                                          thirdLight}; 
    //car threads
    Car firstCar = new Car(carIconOne, firstPosition, firstSpeed);
    Car secondCar = new Car(carIconTwo, secondPosition, secondSpeed);
    Car thirdCar = new Car(carIconThree, thirdPosition, thirdSpeed);
    //array of cars to control them all at once
    Car[] carThreads = {firstCar, secondCar, thirdCar};
    
    //JProgressBars showing the horizontal position of cars on the 3000 meter road
    JProgressBar firstCarProgress = new JProgressBar(0,3000);
    JProgressBar secondCarProgress = new JProgressBar(0,3000);
    JProgressBar thirdCarProgress = new JProgressBar(0,3000);

    //light trackers for the sniffer
    boolean firstLightSniffer;
    boolean secondLightSniffer;
    boolean thirdLightSniffer;

    public Main() {
        setSize(1200,355);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Traffic Congestion Mitigation Company"); 
        running = Thread.currentThread().isAlive();
        GUI();
    }
 
    ///GUI with components populating it
    private void GUI() {
        setLayout(null);
        JLabel title = new JLabel("Traffic Real-time Simulation GUI");
        JLabel timeLabel = new JLabel("The current local time is: ");
        JLabel instructionOne = new JLabel("Click Start to begin the simulation,"
                + " and Pause to pause.");
        JLabel instructionTwo = new JLabel("Then Resume, or Stop."
                + " Add more cars/lights as desired.");

		///populate frame
        add(title);
        add(instructionOne);
        add(instructionTwo);
        ///for Time
        add(timeLabel);
        add(date);
        ///for JButtons    
        add(start);
        add(pause);
        add(stop);
        add(addACar);
        add(addTrafficLight);
        pause.setEnabled(false);
        stop.setEnabled(false);
        addACar.setEnabled(false);
        addTrafficLight.setEnabled(false);
		///add the first car and light stuff
        add(lightIconOne);
        add(carIconOne);
        add(firstPosition);
        add(firstSpeed);

    ///setBounds to coordinate position for all the J variables
    ///(distance from left, distance from top, width, height)
       title.setBounds(10, 15, 225, 25);
       instructionOne.setBounds(10, 35, 400, 25);
       instructionTwo.setBounds(10, 55, 400, 25);
       timeLabel.setBounds(10, 80, 225, 25);
       date.setBounds(155, 80, 225, 25);
       start.setBounds(10, 110, 100, 25);
       pause.setBounds(120, 110, 100, 25);
       addACar.setBounds(230, 110, 100, 25);
       addTrafficLight.setBounds(375, 110, 110, 25);
       lightIconOne.setBounds(25 + 1000/3, 255, 10, 30); //light icon at pos. 1000
       
    //add change listeners to the progress bars to update them
        firstCarProgress.addChangeListener(this);  
        secondCarProgress.addChangeListener(this);
        thirdCarProgress.addChangeListener(this);
      
        
    //start button starts the car and light threads
    start.addActionListener((ActionEvent e) -> {

        simulator.start(); ///simulator starts with press of button
        firstCar.start();
        firstLight.start();
        firstLightSniffer = true; //set first light's tracker to true
        startRunning = true; //set to true
        //enable the other buttons
        stop.setBounds(10, 110, 100, 25);
        stop.setEnabled(true);
        pause.setEnabled(true);
        addACar.setEnabled(true);
        addTrafficLight.setEnabled(true);
        start.setEnabled(false);
        start.setBounds(0, 0, 0, 0); //start button disappears to be replaced by stop button
    }); //end start listener

    //stop program button to replace start button
    stop.addActionListener((ActionEvent e) -> {
        if(running) { //pause the array of cars and lights
            for(Car car: carThreads) {
                car.pause();
            }
            for(TrafficLight trafficLight: trafficLightThreads) {
               trafficLight.pause();
            }
            startRunning = false; //stop threads
            running = false; //stop everything
            //disable the other buttons
            start.setEnabled(false);
            pause.setEnabled(false);
            addACar.setEnabled(false);
            addTrafficLight.setEnabled(false);
            stop.setText("Exit"); //change label back to pause
        } else { ///if simulation is not running (already stopped)
            System.exit(0); //close
        }
    });//end stop listener    
    
    //pause button pauses all threads under startRunning
    pause.addActionListener((ActionEvent e) -> {
        if(startRunning) {
            //pause the car and light arrays
            for(Car car: carThreads) {
                car.pause();
            }
            for(TrafficLight trafficLight: trafficLightThreads) {
                trafficLight.pause();
            }
            startRunning = false; //pause running by turning this to false
            pause.setText("Resume"); //change label of button
        } else { //if simulation is not running (already paused) then resume threads
            for(Car car:carThreads) {
             if(car.paused) {
                   car.resume();
             }
            }
            for(TrafficLight trafficLight: trafficLightThreads) {
                trafficLight.resume();
            }
            startRunning = true; //resume running by setting to true
            pause.setText("Pause"); //change label back to pause
        }
    });//end pause listener
    
    //add a car listener
    addACar.addActionListener((ActionEvent e) -> {
        carCounter++; //adds to the number of clicks so far
        switch (carCounter){
            case 1: //on first click
                secondCar.start(); //start thread for second car
                add(carIconTwo); //add icon for this car
                add(secondPosition); //add position label
                add(secondSpeed); //add speed label
                addACar.setText("Another Car"); //change text of button
                addACar.setBounds(230, 110, 135, 25); //adjust bounds for button
                break;
            case 2: //on second click
                thirdCar.start(); //start thread for third car
                add(carIconThree); //add icon for this car
                add(thirdPosition); //add position label
                add(thirdSpeed); //add speed label
                addACar.setEnabled(false); //disable button after case 2
                break;
            }//end switch
    });//end of addACar listener
    
    //add a light listener
    addTrafficLight.addActionListener((ActionEvent e) -> {
        lightCounter++; //adds to the number of clicks so far
        switch (lightCounter){
            case 1: //on first click
                secondLight.start(); //start thread for second light
                secondLightSniffer = true; //set second light tracker to true
                add(lightIconTwo); //add icon for this light
                lightIconTwo.setBounds(25 + 2000/3, 255, 10, 30); //light icon at pos. 2000
                addTrafficLight.setBounds(375, 110, 140, 25);
                addTrafficLight.setText("Add Another Light"); //change text of button
                break;
            case 2: //on second click
                thirdLight.start(); //start thread for third light
                thirdLightSniffer = true; //set third light tracker to true
                add(lightIconThree); //add icon for this light
                lightIconThree.setBounds(25 + 3000/3, 255, 10, 30); //light icon at pos. 3000
                addTrafficLight.setEnabled(false); //disable button after case 2
                break;
            }//end switch
    });//end of addTrafficLight listener 
    
    }///end GUI

    //run, check if simulator is running and sets car position
    @Override
    public void run() {
        while(running) {
            sniffer();
            Main.date.setText(date()); //set the date
            firstCarProgress.setValue(firstCar.position()); //update position of car based off change-listener
            secondCarProgress.setValue(secondCar.position());
            thirdCarProgress.setValue(thirdCar.position());
        }//end while
    }//end run
    
    //method to sniff positions of cars at lights and stop/resume cars
    public void sniffer() {
        if(firstLightSniffer) { //if first light tracker is true/on
            switch(firstLight.getColor()) { //for the first light
                case RED: //when red
                    for(Car car: carThreads) { //for a car in carThreads
                        //and a car is within a certain distance to it
                        if(car.position()>750 && car.position()<999) {
                            car.pause(); //pause the car
                            car.carSpeed.setText("Speed: " + 0 + " mph"); //set carSpeed label to 0 while red
                        }
                    }
                    break;
                case SWITCH: //if switch phase. Switch is important phase
                    //because if green, it  messes with pause button due to resume()
                    for(Car car:carThreads) {
                        if(car.paused) { //and the car is paused
                            car.resume(); //resume the car
                        }
                    }
                    break;
            }//end of first light
        }//end if first light tracker
        
        if(secondLightSniffer) { //if second light tracker is true/on
            switch(secondLight.getColor()) { //for the second light
                case RED: //when red
                    for(Car car: carThreads) { //for a car in carThreads
                        //and a car is within a certain distance to it
                        if(car.position()>1750 && car.position()<1999) {
                            car.pause(); //pause the car
                            car.carSpeed.setText("Speed: " + 0 + " mph"); //set carSpeed label to 0 while red
                        }
                    }
                    break;
                case SWITCH: //if switch phase
                    for(Car car:carThreads) {
                        if(car.paused) { //and the car is paused
                            car.resume(); //resume the car
                        }
                    }
                    break;
            }//end of second light
        }//end if second light tracker
        
        if(thirdLightSniffer) { //if third light tracker is true/on
            switch(thirdLight.getColor()) { //for the third light
                case RED: //when red
                    for(Car car: carThreads) { //for a car in carThreads
                        //and the car is within a certain distance to it
                        if(car.position()>2750 && car.position()<2999) {
                            car.pause(); //pause the car
                            car.carSpeed.setText("Speed: " + 0 + " mph"); //set carSpeed label to 0 while red
                        }
                    }
                    break;
                case SWITCH: //if switch phase
                    for(Car car:carThreads) {
                        if(car.paused) { //and the car is paused
                            car.resume(); //resume the car
                        }
                    }
                    break;
            }//end of third light
        }//end if third light tracker
    }//end sniffer method

    //method for the time stamp
    public String date() {
        Date date = new Date(); ///get the date and time
        return date.toString(); ///convert to string
    } 

    ///live simulator that constantly updates the car threads' state change
    @Override
    public void stateChanged(ChangeEvent e) {
        carIconOne.repaint();//Update car icons constantly
        carIconTwo.repaint();
        carIconThree.repaint();
        
        firstPosition.repaint();//Update car positions constantly
        secondPosition.repaint();
        thirdPosition.repaint();
        
        firstSpeed.repaint();//Update car speeds constantly
        secondSpeed.repaint();
        thirdSpeed.repaint(); 
        
        //update car pic positions (distance from left, distance from top, width, height)
        carIconOne.setBounds(5 + firstCarProgress.getValue()/3, 268, 35, 20);
        firstPosition.setBounds(5 + firstCarProgress.getValue()/3, 283, 105, 20);
        firstSpeed.setBounds(5 + firstCarProgress.getValue()/3, 298, 100, 20);
        carIconTwo.setBounds(5 + secondCarProgress.getValue()/3, 268, 35, 20);
        secondPosition.setBounds(5 + secondCarProgress.getValue()/3, 235, 105, 20);
        secondSpeed.setBounds(5 + secondCarProgress.getValue()/3, 250, 100, 20);
        carIconThree.setBounds(5 + thirdCarProgress.getValue()/3, 268, 35, 20);
        thirdPosition.setBounds(5 + thirdCarProgress.getValue()/3, 205, 105, 20);
        thirdSpeed.setBounds(5 + thirdCarProgress.getValue()/3, 220, 100, 20);
    }//end stateChanged     
    
    ///main method
    public static void main(String[] args) {
        Main main = new Main();
        simulator = new Thread(main); //start simulator thread
    }//end main method
}//end Main class