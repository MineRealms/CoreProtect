package net.coreprotect.worldedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import java.util.Arrays;
import java.util.List;
import net.coreprotect.Functions;
import net.coreprotect.consumer.Queue;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class WorldEdit extends Queue {

   public static WorldEditPlugin getWorldEdit(Server server) {
      Plugin pl = server.getPluginManager().getPlugin("WorldEdit");
      return pl != null && pl instanceof WorldEditPlugin?(WorldEditPlugin)pl:null;
   }

   protected static void logData(Actor actor, BlockState oldBlock, Material oldBlockType, BlockState newBlock, ItemStack[] container_contents) {
      List signs = Arrays.asList(new Material[]{Material.SIGN_POST, Material.WALL_SIGN});
      Material old_type = oldBlock.getType();
      byte old_data = Functions.getData(oldBlock);
      Material new_type = newBlock.getType();
      byte new_data = Functions.getData(newBlock);
      if(!old_type.equals(new_type) || old_data != new_data) {
         try {
            if(Functions.checkConfig(oldBlock.getWorld(), "sign-text") == 1 && signs.contains(old_type)) {
               Sign e = (Sign)oldBlock;
               String line1 = e.getLine(0);
               String line2 = e.getLine(1);
               String line3 = e.getLine(2);
               String line4 = e.getLine(3);
               Queue.queueSignText(actor.getName(), newBlock, line1, line2, line3, line4, 5);
            }

            if(container_contents != null) {
               Queue.queueContainerBreak(actor.getName(), newBlock, oldBlockType, container_contents);
            }
         } catch (Exception var15) {
            var15.printStackTrace();
         }

         if(new_type.equals(Material.SKULL)) {
            Queue.queueBlockPlaceDelayed(actor.getName(), newBlock.getBlock(), oldBlock, 0);
         } else if(old_type.equals(Material.AIR) && !new_type.equals(Material.AIR)) {
            Queue.queueBlockPlace(actor.getName(), newBlock, newBlock.getType(), Functions.getData(newBlock));
         } else if(!old_type.equals(Material.AIR) && !new_type.equals(Material.AIR)) {
            queueBlockPlace(actor.getName(), newBlock, oldBlock, newBlock.getType(), Functions.getData(newBlock));
         } else if(!old_type.equals(Material.AIR) && new_type.equals(Material.AIR)) {
            Queue.queueBlockBreak(actor.getName(), oldBlock, old_type, old_data);
         }
      }

   }
}
