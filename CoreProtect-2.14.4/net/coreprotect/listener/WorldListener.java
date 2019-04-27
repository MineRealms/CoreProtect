package net.coreprotect.listener;

import java.util.Iterator;
import java.util.List;
import net.coreprotect.Functions;
import net.coreprotect.consumer.Queue;
import net.coreprotect.database.Lookup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class WorldListener extends Queue implements Listener {

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onLeavesDecay(LeavesDecayEvent event) {
      World world = event.getBlock().getWorld();
      if(!event.isCancelled() && Functions.checkConfig(world, "leaf-decay") == 1) {
         String player = "#decay";
         Block block = event.getBlock();
         Material type = event.getBlock().getType();
         byte data = Functions.getData(event.getBlock());
         Queue.queueBlockBreak(player, block.getState(), type, data);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void OnPortalCreate(PortalCreateEvent event) {
      World world = event.getWorld();
      if(!event.isCancelled() && Functions.checkConfig(world, "portals") == 1) {
         String user = "#portal";
         Iterator var4 = event.getBlocks().iterator();

         Block block;
         Material type;
         while(var4.hasNext()) {
            block = (Block)var4.next();
            type = block.getType();
            if(type.equals(Material.FIRE)) {
               String result_data = Lookup.who_placed_cache(block);
               if(result_data.length() > 0) {
                  user = result_data;
               }
               break;
            }
         }

         var4 = event.getBlocks().iterator();

         while(var4.hasNext()) {
            block = (Block)var4.next();
            type = block.getType();
            if(user.equals("#portal") && !type.equals(Material.OBSIDIAN)) {
               Queue.queueBlockPlaceDelayed(user, block, (BlockState)null, 20);
            } else if(type.equals(Material.AIR) || type.equals(Material.FIRE)) {
               Queue.queueBlockPlaceDelayed(user, block, (BlockState)null, 0);
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onStructureGrow(StructureGrowEvent event) {
      TreeType treeType = event.getSpecies();
      String user = "#tree";
      boolean tree = true;
      if(treeType != null) {
         List blocks = event.getBlocks();
         if(blocks.size() <= 1) {
            Iterator world = blocks.iterator();

            while(world.hasNext()) {
               BlockState player = (BlockState)world.next();
               if(player.getType().equals(Material.SAPLING)) {
                  return;
               }
            }
         }

         if(treeType.name().toLowerCase().contains("mushroom")) {
            user = "#mushroom";
            tree = false;
         }

         if(!event.isCancelled()) {
            World world1 = event.getWorld();
            if(tree && Functions.checkConfig(world1, "tree-growth") == 1 || !tree && Functions.checkConfig(world1, "mushroom-growth") == 1) {
               Player player1 = event.getPlayer();
               Location location = event.getLocation();
               if(player1 != null) {
                  user = player1.getName();
               }

               Queue.queueStructureGrow(user, world1.getBlockAt(location).getState(), blocks);
            }
         }

      }
   }
}
