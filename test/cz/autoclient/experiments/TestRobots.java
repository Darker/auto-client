package cz.autoclient.experiments;

import cz.autoclient.PVP_net.Setnames;
import cz.autoclient.robots.*;
import cz.autoclient.settings.Settings;


/**
 * Unit test for simple App.
 */
public class TestRobots 
{
   public static void main(String[] args) throws Exception
   {
     Settings settings = new Settings();
     settings.setSetting(Setnames.AUTO_QUEUE_ENABLED.name, true);
     
     
     Robot bot = new LaunchBot();
     Robot queue = new AutoQueueBot(settings);
     RobotManager rurs = new RobotManager(1000);
     
     rurs.start();
     rurs.addRobot(queue);
     rurs.addRobot(bot);
     Thread.sleep(10000);
     
     rurs.removeRobot(queue);
     
     rurs.join();
   }
}
