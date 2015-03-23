package cz.autoclient;

import cz.autoclient.robots.*;


/**
 * Unit test for simple App.
 */
public class TestRobots 
{
   public static void main(String[] args) throws Exception
   {
     Robot bot = new ContinueBot();
     RobotManager rurs = new RobotManager(1000);
     rurs.addRobot(bot);
     rurs.start();
     rurs.join();
   }
}
