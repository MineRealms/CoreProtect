package net.coreprotect.command;

import java.util.List;
import net.coreprotect.command.CancelCommand;
import net.coreprotect.command.RollbackRestoreCommand;
import net.coreprotect.model.Config;
import org.bukkit.command.CommandSender;

public class UndoCommand {

   protected static void runCommand(CommandSender user, boolean permission, String[] args) {
      try {
         if(Config.last_rollback.get(user.getName()) != null) {
            List e = (List)Config.last_rollback.get(user.getName());
            int time = ((Integer)((Object[])e.get(0))[0]).intValue();
            args = (String[])((String[])e.get(1));
            String[] valid = args;
            int var6 = args.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String arg = valid[var7];
               if(arg.equals("#preview")) {
                  CancelCommand.runCommand(user, permission, args);
                  return;
               }
            }

            boolean var10 = true;
            if(!args[0].equals("rollback") && !args[0].equals("rb") && !args[0].equals("ro")) {
               if(!args[0].equals("restore") && !args[0].equals("rs") && !args[0].equals("re")) {
                  var10 = false;
               } else {
                  args[0] = "rollback";
               }
            } else {
               args[0] = "restore";
            }

            if(var10) {
               Config.last_rollback.remove(user.getName());
               RollbackRestoreCommand.runCommand(user, permission, args, time);
            }
         } else {
            user.sendMessage("§3CoreProtect §f- No previous rollback/restore found.");
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }
}
