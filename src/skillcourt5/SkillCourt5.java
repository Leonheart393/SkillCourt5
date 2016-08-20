/* 
 *  TODO: Beeping sound for start.
 *  TODO: Beeping sound when three seconds left.
 *  TODO: Add a User Sequence option.
 */

package skillcourt5;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class SkillCourt5
{
    static Boolean lock = false;
    static Boolean isGreen = false;
    static Boolean gameEnded = false;
    Start_Menu start_menu;
    int num_of_pads;
    Arduino[] arduino_array;
    
    /**
     * @throws java.lang.InterruptedException added this for all the Thread.sleeps().
     */
    
    public void showFinalResults(User user, Statistics stats) throws InterruptedException {
        final Object[] options  = {
            "Upload Score",
            "View previous score",
            "New Game",
            "Play again",
            "Quit game"
        };
        
        int finalOptions = JOptionPane.showOptionDialog(null, 
                stats.printResults(),
                "Final Results",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                options[0]);
        
        if(finalOptions == 0) 
        {
            try {
                user.uploadScore(stats.getScore(), Math.round((stats.greenHits/stats.totalHits) * 100));
                showFinalResults(user, stats);
            } catch (Exception ex) {
                Logger.getLogger(SkillCourt5.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(finalOptions == 1)
        {
            user.viewScore();
            showFinalResults(user, stats);
        }
        else if(finalOptions == 2)
        {
            newGame(user);
        }
        else if(finalOptions == 3)
        {
            try {
                playAgain(user);
            } catch (Exception ex) {
                Logger.getLogger(SkillCourt5.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(finalOptions == 4)
        {
            System.exit(0);
        }
    }
    
    public void sendColors(int numPads, Arduino[] pads) {
        int rand = 0;
        
        /*Adjusts random if 1 pad is selected, anything more is normal.*/
        if(num_of_pads == 1)
        {
            rand = ThreadLocalRandom.current().nextInt(0, 2);
        }
        else
        {
            rand = ThreadLocalRandom.current().nextInt(0, num_of_pads);
        }
        
        for(int i = 0; i < num_of_pads; i++)
        {
            if(i == rand)
            {
                arduino_array[i].sendData(0);
            }
            else
            {
                arduino_array[i].sendData(1);
            }
        }
             
    }
    
    public void newGame(User user) throws InterruptedException 
    {
        Statistics.bool = false;
        start_menu = new Start_Menu();
        start_menu.setVisible(true);
        start_menu.setLocationRelativeTo(null);
        
        /*This loops until the Start_Menu submit button is clicked.*/
        while(!start_menu.next)
        {
           Thread.sleep(50);
        }
        
        num_of_pads = start_menu.numOfPads;
        
        arduino_array = new Arduino[num_of_pads];
        
        /*Initialize the number of arrays entered.*/
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i] = new Arduino();
            if (arduino_array[i].initialize()) 
            {
                System.out.println("Program started, preparing to send colors."); 
            }
            /*Wait for Arduino to establish connection before connecting to next*/
            Thread.sleep(50);
        }
        
        /*Best way to have starting lights blink so far..*/
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i].sendData(4);
        }
        Thread.sleep(4000);//Give the lights time to blink, simulates 3 seconds.
        
        Statistics stats = new Statistics();
        
        if(start_menu.time <= 60)
        {
            Statistics.ms = 0;//***
            Statistics.msec = "00";//***
            Statistics.s = start_menu.time;//***
            Statistics.sec = "" + start_menu.time;//***
            Statistics.m = 0;//***
        }
        else
        {
            //TODO: start_menu.time > 60
        } 
 
        Thread t = new Thread(stats);
        t.start();
        sendColors(num_of_pads, arduino_array);
        /*Inifinite loop until the timer runs out.*/
        while(!stats.bool)//***   //Statistics.bool to remove warning.
        {
                                   
            while(lock == false && !stats.bool)//***
            {
                Thread.sleep(10);
            }
            if(stats.bool)//***
            {
                break;
            }
            if(isGreen)
            {
                stats.addGreen(1);
                stats.addPoint(1);
                stats.totalHits++;//***
                stats.greenHits++;//***
                t.interrupt();
                sendColors(num_of_pads, arduino_array);
                lock = false;
            }
            else
            {
                stats.addRed(1);
                stats.subtractPoint(1);
                stats.totalHits++;//***
                t.interrupt();
                sendColors(num_of_pads, arduino_array);
                lock = false;
            }   
            //lock = false;
                           
        }
        
        /*Turn all the LEDs blue signaling that the session has ended.*/
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i].sendData(2);
        }
        
        stats.stats_timer.stop();//?
        
        for (int i = 0; i < 5; i++)
        {
            Statistics.countdown.setText("00:00:00");//***
            Thread.sleep(500);
            Statistics.countdown.setText(" ");//***
            Thread.sleep(500);
        }
        Statistics.countdown.setText("Times Up!");
        Thread.sleep(1000);//This sleep is just to show the Times up.
        stats.kill();
        
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i].sendData(3);
            arduino_array[i].close();
        }
        showFinalResults(user, stats);
    }
    
    public void playAgain(User user) throws Exception {
        Statistics.bool = false;
        /*Initialize the number of arrays entered.*/
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i] = new Arduino();
            if (arduino_array[i].initialize()) 
            {
                System.out.println("Program started, preparing to send colors."); 
            }
            /*Wait for Arduino to establish connection before connecting to next*/
            Thread.sleep(50);
        }
        
        /*Best way to have starting lights blink so far..*/
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i].sendData(4);
        }
        Thread.sleep(4000);//Give the lights time to blink, simulates 3 seconds.
        
        Statistics stats = new Statistics();
        
        if(start_menu.time <= 60)
        {
            Statistics.ms = 0;//***
            Statistics.msec = "00";//***
            Statistics.s = start_menu.time;//***
            Statistics.sec = "" + start_menu.time;//***
            Statistics.m = 0;//***
        }
        else
        {
            //TODO: start_menu.time > 60
        } 
 
        Thread t = new Thread(stats);
        t.start();
        
        /*Inifinite loop until the timer runs out.*/
        while(!stats.bool)//***   //Statistics.bool to remove warning.
        {
            int rand = 0;
            
            /*Adjusts random if 1 pad is selected, anything more is normal.*/
            if(num_of_pads == 1)
            {
                rand = ThreadLocalRandom.current().nextInt(0, 2);
            }
            else
            {
                rand = ThreadLocalRandom.current().nextInt(0, num_of_pads);
            }
            
            /* Change/Send pad colors and update statitics screen.*/
            for(int i = 0; i < num_of_pads; i++)
            {
                if(i == rand)
                {
                    arduino_array[i].sendData(0);
                }
                else
                {
                    arduino_array[i].sendData(1);
                }
                
                /*This prevents the pads from changing until one has been hit*/
                if(i == (num_of_pads - 1))
                {                   
                    while(lock == false && !stats.bool)//***
                    {
                        Thread.sleep(10);
                    }
                    if(stats.bool)//***
                    {
                        break;
                    }
                    if(isGreen)
                    {
                        stats.addGreen(1);
                        stats.addPoint(1);
                        stats.totalHits++;//***
                        stats.greenHits++;//***
                        t.interrupt();
                    }
                    else
                    {
                        stats.addRed(1);
                        stats.subtractPoint(1);
                        stats.totalHits++;//***
                        t.interrupt();
                    }   
                   lock = false;
                }
            }
        }
        
        /*Turn all the LEDs blue signaling that the session has ended.*/
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i].sendData(2);
        }
        
        stats.stats_timer.stop();//?
        
        for (int i = 0; i < 5; i++)
        {
            Statistics.countdown.setText("00:00:00");//***
            Thread.sleep(500);
            Statistics.countdown.setText(" ");//***
            Thread.sleep(500);
        }
        Statistics.countdown.setText("Times Up!");
        Thread.sleep(1000);//This sleep is just to show the Times up.
        stats.kill();
        
        for(int i = 0; i < num_of_pads; i++)
        {
            arduino_array[i].sendData(3);
            arduino_array[i].close();
        }
        showFinalResults(user, stats);
    }
    
    
    @SuppressWarnings("SleepWhileInLoop")
    public static void main(String[] args) throws InterruptedException
    {
        SkillCourt5 sc = new SkillCourt5();
        sc.newGame(new User("we", "ewe"));
    }
}