package net.coreprotect.command;

import net.coreprotect.model.Config;
import net.coreprotect.thread.CheckUpdate;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

   protected static void runCommand(final CommandSender player, boolean permission, String[] args) {
      if(permission) {
         if(Config.converter_running) {
            player.sendMessage("§3CoreProtect §f- Upgrade in progress. Please try again later.");
            return;
         }

         if(Config.purge_running) {
            player.sendMessage("§3CoreProtect §f- Purge in progress. Please try again later.");
            return;
         }

         class BasicThread implements Runnable {

            public void run() {
               try {
                  Config.performInitialization();
                  if(((Integer)Config.config.get("check-updates")).intValue() == 1) {
                     Thread e = new Thread(new CheckUpdate(false));
                     e.start();
                  }

                  player.sendMessage("§3CoreProtect §f- Configuration reloaded.");
               } catch (Exception var2) {
                  var2.printStackTrace();
               }

            }
         }

         BasicThread runnable = player.new BasicThread();
         Thread thread = new Thread(runnable);
         thread.start();
      } else {
         player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
      }

   }
}
