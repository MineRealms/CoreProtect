package net.coreprotect.command;

import net.coreprotect.model.Config;
import org.bukkit.command.CommandSender;

public class InspectCommand {

   protected static void runCommand(CommandSender player, boolean permission, String[] args) {
      if(permission) {
         boolean command = true;
         if(Config.inspecting.get(player.getName()) == null) {
            Config.inspecting.put(player.getName(), Boolean.valueOf(false));
         }

         if(args.length > 1) {
            String action = args[1];
            if(action.equalsIgnoreCase("on")) {
               command = true;
            } else if(action.equalsIgnoreCase("off")) {
               command = false;
            }
         }

         if(!((Boolean)Config.inspecting.get(player.getName())).booleanValue()) {
            if(!command) {
               player.sendMessage("§3CoreProtect §f- Inspector already disabled.");
            } else {
               player.sendMessage("§3CoreProtect §f- Inspector now enabled.");
               Config.inspecting.put(player.getName(), Boolean.valueOf(true));
            }
         } else if(command) {
            player.sendMessage("§3CoreProtect §f- Inspector already enabled.");
         } else {
            player.sendMessage("§3CoreProtect §f- Inspector now disabled.");
            Config.inspecting.put(player.getName(), Boolean.valueOf(false));
         }
      } else {
         player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
      }

   }
}
