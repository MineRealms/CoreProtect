package net.coreprotect.command;

import java.util.List;
import net.coreprotect.command.RollbackRestoreCommand;
import net.coreprotect.model.Config;
import org.bukkit.command.CommandSender;

public class CancelCommand {

   protected static void runCommand(CommandSender user, boolean permission, String[] args) {
      try {
         if(Config.last_rollback.get(user.getName()) != null) {
            List e = (List)Config.last_rollback.get(user.getName());
            int time = ((Integer)((Object[])e.get(0))[0]).intValue();
            args = (String[])((String[])e.get(1));
            boolean valid = false;

            for(int i = 0; i < args.length; ++i) {
               if(args[i].equals("#preview")) {
                  valid = true;
                  args[i] = args[i].replaceAll("#preview", "#preview_cancel");
               }
            }

            if(!valid) {
               user.sendMessage("§3CoreProtect §f- No pending rollback/restore found.");
            } else {
               Config.last_rollback.remove(user.getName());
               RollbackRestoreCommand.runCommand(user, permission, args, time);
            }
         } else {
            user.sendMessage("§3CoreProtect §f- No pending rollback/restore found.");
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }
}
