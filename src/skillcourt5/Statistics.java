package skillcourt5;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class Statistics implements Runnable 
{
    private int score;
    private int greenPad;
    private int redPad;
    private volatile boolean isRunning = true;
    public float greenHits;
    public float totalHits = 0;
    public static String msec = "00", sec = "00", min = "00";

    public static int m, s, ms;
    public static JLabel countdown;
    
    public Timer stats_timer;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();;
    static boolean bool = false;
    
    public Statistics() 
    {  
        stats_timer = new Timer(15, new startTimer()); 
        
        score = 0;
        greenPad = 0;
        redPad = 0;
    }
    
    public int getScore() 
    {
        return score;
    }
    
    public void addPoint(int point) 
    {
        score += point;
    }
    
    public void subtractPoint(int point) 
    {
        score -= point;
    }
    public void addGreen(int point) 
    {
        greenPad += point;
    }
    public void addRed(int point) 
    {
        redPad += point;
    }
    public void addHit(int point)
    {
        totalHits += 1;
    }
    
    public int getGreen()
    {
        return greenPad;
    }
    
    public String printResults()
    {
        return "Final score: " + score + "\n" + "Green Pad hits: " + greenPad + "\n" + "Red Pad hits: " + redPad + "\n" + "Accuracy: " + Math.round((greenHits/totalHits) * 100) + "%";
    }
    
    public void kill()
    {
        isRunning = false;
    }
    
    /*Logic for the countdown timer*/
    public class startTimer implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(m > 0)
            {
                if(s == 0)
                {
                    s = 59;
                    sec = "" + s;
                    m--;
                    if(m < 10)
                    {
                        min = "0" + m;
                    }
                    else
                    {
                        min = "" + m;
                    }
                    countdown.setText(min + ":" + sec + ":" + msec);
                }
                else if(s > 0)
                {
                    System.out.println("S1 = " + s);
                    s--;
                    if(s < 10)
                    {
                        sec = "0" + s;
                    }
                    else
                    {
                        sec = "" + s;
                    }
                }
                countdown.setText(min + ":" + sec + ":" + msec);
            }
            else if(s > 0)
            {
                if(ms == 0)
                {
                    ms = 59;
                    msec = "" + ms;
                    s--;
                    if(s < 10)
                    {
                        sec = "0" + s;
                    }
                    else
                    {
                        sec = "" + s;
                    }
                    countdown.setText(min + ":" + sec + ":" + msec);
                }
                else if(ms > 0)
                {
                    ms--;
                    if(ms < 10)
                    {
                        msec = "0" + ms;
                    }
                    else
                    {
                        msec = "" + ms;
                    }
                }
                countdown.setText(min + ":" + sec + ":" + msec);
            }
            else if(m == 0 && s == 0)
            {
                if(ms > 0)
                {
                    ms--;
                    if(ms < 10)
                    {
                        msec = "0" + ms;
                    }
                    else
                    {
                        msec = "" + ms;
                    }
                }
                countdown.setText(min + ":" + sec + ":" + msec);
                bool = true;
                toolkit.beep();
            }
            else if(m == 0 && s == 0 && ms == 0)
            {
                System.out.println("HERE");
                countdown.setText("TIMES UP!");
                bool = true;
            }
        }
    }
    
    @Override
    public void run()
    {
        JFrame myFrame = new JFrame("Scoreboard");
        myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        myFrame.setUndecorated(true);

        JLabel greenLabel = new JLabel("<html>Score: " + score + "<br>" + "Green Pads hit: " +
                greenPad + "<br>" + "Accuracy: " + "0" + "%"+"</html>");
        greenLabel.setFont(new Font("Serif", Font.BOLD, 200));
        myFrame.add(greenLabel, BorderLayout.NORTH);
        
        /*Timer for stats screen*/
        countdown = new JLabel(min + ":" + sec + ":" + msec);
        countdown.setFont(new Font("Serif", Font.BOLD, 200));
        myFrame.add(countdown);
        
        myFrame.setVisible(true);
        
        stats_timer.start();
        while(isRunning) 
        {          
            try
            {               
                Thread.sleep(500);
            } 
            catch (InterruptedException ex) 
            {
                greenLabel.setText("<html>Score: " + score + "<br>" + "Green Pads hit: " +
                        greenPad + "<br>" + "Accuracy: " + Math.round((greenHits/totalHits) * 100) + "%</html>");
            }           
        }
        myFrame.dispose();
    }
}