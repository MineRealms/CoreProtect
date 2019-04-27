package net.coreprotect.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.coreprotect.Functions;
import net.coreprotect.consumer.Queue;
import net.coreprotect.database.Database;
import net.coreprotect.database.Lookup;
import net.coreprotect.model.BlockInfo;
import net.coreprotect.model.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener extends Queue implements Listener {

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockBreak(BlockBreakEvent event) {
      if(!event.isCancelled()) {
         String player = event.getPlayer().getName();
         Block bl = event.getBlock();
         int x = bl.getX();
         int y = bl.getY();
         int z = bl.getZ();
         World world = bl.getWorld();
         Location l1 = new Location(world, (double)(x + 1), (double)y, (double)z);
         Location l2 = new Location(world, (double)(x - 1), (double)y, (double)z);
         Location l3 = new Location(world, (double)x, (double)y, (double)(z + 1));
         Location l4 = new Location(world, (double)x, (double)y, (double)(z - 1));
         Location l5 = new Location(world, (double)x, (double)(y + 1), (double)z);
         Location l6 = new Location(world, (double)x, (double)(y - 1), (double)z);
         int l = 1;
         byte m = 7;
         if(Functions.checkConfig(world, "natural-break") == 0) {
            l = 6;
         }

         if(Functions.checkConfig(world, "block-break") == 0) {
            m = 6;
         }

         Block block = bl;
         Material type = bl.getType();

         for(byte data = Functions.getData(bl); l < m; ++l) {
            Location lc = l1;
            if(l == 2) {
               lc = l2;
            }

            if(l == 3) {
               lc = l3;
            }

            if(l == 4) {
               lc = l4;
            }

            if(l == 5) {
               lc = l5;
            }

            Block b = block;
            boolean check_down = false;
            Material bt = type;
            byte bd = data;
            boolean log = true;
            Material bt1;
            byte var38;
            if(l < 6) {
               if(l == 4 && (type.equals(Material.WOODEN_DOOR) || type.equals(Material.SPRUCE_DOOR) || type.equals(Material.BIRCH_DOOR) || type.equals(Material.JUNGLE_DOOR) || type.equals(Material.ACACIA_DOOR) || type.equals(Material.DARK_OAK_DOOR) || type.equals(Material.IRON_DOOR_BLOCK))) {
                  lc = l6;
                  check_down = true;
               }

               Block b1 = world.getBlockAt(lc);
               bt1 = b1.getType();
               if(l == 5 && BlockInfo.falling_block_types.contains(bt1) && Functions.checkConfig(world, "block-movement") == 1) {
                  int bd1 = y + 2;

                  for(boolean bn = false; !bn; ++bd1) {
                     Block e = world.getBlockAt(x, bd1, z);
                     Material line1 = e.getType();
                     if(!BlockInfo.falling_block_types.contains(line1)) {
                        lc = new Location(world, (double)x, (double)(bd1 - 1), (double)z);
                        bn = true;
                     }
                  }
               }

               Block var37;
               byte var39;
               if(!BlockInfo.track_any.contains(bt1)) {
                  if(l != 5 && !check_down) {
                     if(!BlockInfo.track_side.contains(bt1)) {
                        log = false;
                     } else if(!type.equals(Material.STONE_BUTTON) && !type.equals(Material.WOOD_BUTTON)) {
                        if(!bt1.equals(Material.RAILS) && !bt1.equals(Material.POWERED_RAIL) && !bt1.equals(Material.DETECTOR_RAIL) && !bt1.equals(Material.ACTIVATOR_RAIL)) {
                           if(bt1.equals(Material.BED_BLOCK) && !type.equals(Material.BED_BLOCK)) {
                              log = false;
                           }
                        } else {
                           var38 = Functions.getData(b1);
                           if(l == 1 && var38 != 3) {
                              log = false;
                           } else if(l == 2 && var38 != 2) {
                              log = false;
                           } else if(l == 3 && var38 != 4) {
                              log = false;
                           } else if(l == 4 && var38 != 5) {
                              log = false;
                           }
                        }
                     } else {
                        var37 = world.getBlockAt(lc);
                        var39 = Functions.getData(var37);
                        if(var39 != l) {
                           log = false;
                        }
                     }
                  } else if(!BlockInfo.track_top.contains(bt1)) {
                     log = false;
                  }

                  if(!log) {
                     if(type.equals(Material.PISTON_EXTENSION)) {
                        if(bt1.equals(Material.PISTON_STICKY_BASE) || bt1.equals(Material.PISTON_BASE)) {
                           log = true;
                        }
                     } else if(l == 5 && BlockInfo.falling_block_types.contains(bt1)) {
                        log = true;
                     }
                  }
               } else if(bt1.equals(Material.PISTON_EXTENSION)) {
                  if(!type.equals(Material.PISTON_STICKY_BASE) && !type.equals(Material.PISTON_BASE)) {
                     log = false;
                  }
               } else {
                  var37 = world.getBlockAt(lc);
                  var39 = Functions.getData(var37);
                  if(var39 != l) {
                     log = false;
                  }
               }

               if(log) {
                  b = world.getBlockAt(lc);
                  bt = b.getType();
                  bd = Functions.getData(b);
               }
            }

            BlockState var36 = b.getState();
            bt1 = bt;
            var38 = bd;
            int var40 = l;
            if(log && (bt.equals(Material.SKULL) || bt.equals(Material.WALL_BANNER) || bt.equals(Material.STANDING_BANNER))) {
               try {
                  if(var36 instanceof Banner || var36 instanceof Skull) {
                     Queue.queueAdvancedBreak(player, var36, bt1, var38, type, var40);
                  }

                  log = false;
               } catch (Exception var35) {
                  var35.printStackTrace();
               }
            }

            if(log && (bt.equals(Material.SIGN_POST) || bt.equals(Material.WALL_SIGN)) && Functions.checkConfig(world, "sign-text") == 1) {
               try {
                  Sign var41 = (Sign)b.getState();
                  String var42 = var41.getLine(0);
                  String line2 = var41.getLine(1);
                  String line3 = var41.getLine(2);
                  String line4 = var41.getLine(3);
                  Queue.queueSignText(player, var36, var42, line2, line3, line4, 5);
               } catch (Exception var34) {
                  var34.printStackTrace();
               }
            }

            if(log) {
               Database.containerBreakCheck(player, block.getType(), block, block.getLocation());
               Functions.iceBreakCheck(var36, player, bt);
               Queue.queueBlockBreak(player, var36, bt, bd, type, l);
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockBurn(BlockBurnEvent event) {
      World world = event.getBlock().getWorld();
      if(!event.isCancelled() && Functions.checkConfig(world, "block-burn") == 1) {
         String player = "#fire";
         Block block = event.getBlock();
         Material type = block.getType();
         byte data = Functions.getData(event.getBlock());
         Queue.queueBlockBreak(player, block.getState(), type, data);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockFromTo(BlockFromToEvent event) {
      Material type = event.getBlock().getType();
      Material type2 = event.getToBlock().getType();
      if(!event.isCancelled()) {
         World world = event.getBlock().getWorld();
         if(Functions.checkConfig(world, "water-flow") == 1 && (type.equals(Material.WATER) || type.equals(Material.STATIONARY_WATER)) || Functions.checkConfig(world, "lava-flow") == 1 && (type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA))) {
            List flow_break = Arrays.asList(new Material[]{Material.AIR, Material.SAPLING, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.WEB, Material.LONG_GRASS, Material.DEAD_BUSH, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.TORCH, Material.FIRE, Material.REDSTONE_WIRE, Material.CROPS, Material.RAILS, Material.LEVER, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.SNOW, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.VINE, Material.COCOA, Material.TRIPWIRE_HOOK, Material.TRIPWIRE, Material.CARROT, Material.POTATO, Material.WOOD_BUTTON, Material.SKULL, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.ACTIVATOR_RAIL, Material.CARPET, Material.DOUBLE_PLANT});
            if(flow_break.contains(type2) || (type.equals(Material.WATER) || type.equals(Material.STATIONARY_WATER)) && (type2.equals(Material.LAVA) || type2.equals(Material.STATIONARY_LAVA)) || (type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA)) && (type2.equals(Material.WATER) || type2.equals(Material.STATIONARY_WATER))) {
               String f = "#flow";
               if(!type.equals(Material.WATER) && !type.equals(Material.STATIONARY_WATER)) {
                  if(type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA)) {
                     f = "#lava";
                  }
               } else {
                  f = "#water";
               }

               Block block = event.getBlock();
               int unixtimestamp = (int)(System.currentTimeMillis() / 1000L);
               int x = event.getToBlock().getX();
               int y = event.getToBlock().getY();
               int z = event.getToBlock().getZ();
               int wid = Functions.getWorldId(block.getWorld().getName());
               if(Functions.checkConfig(world, "liquid-tracking") == 1) {
                  String p = Lookup.who_placed_cache(block);
                  if(p.length() > 0) {
                     f = p;
                  }
               }

               Config.lookup_cache.put("" + x + "." + y + "." + z + "." + wid + "", new Object[]{Integer.valueOf(unixtimestamp), f, type});
               Queue.queueBlockPlace(f, event.getToBlock(), event.getToBlock().getState(), type, Functions.getData(event.getBlock()));
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockIgnite(BlockIgniteEvent event) {
      World world = event.getBlock().getWorld();
      if(!event.isCancelled() && Functions.checkConfig(world, "block-ignite") == 1) {
         Block block = event.getBlock();
         if(block == null) {
            return;
         }

         if(event.getPlayer() == null) {
            Queue.queueBlockPlace("#fire", block.getState(), block.getState(), Material.FIRE);
         } else {
            Player player = event.getPlayer();
            Queue.queueBlockPlace(player.getName(), block.getState(), (BlockState)null, Material.FIRE);
            int unixtimestamp = (int)(System.currentTimeMillis() / 1000L);
            int world_id = Functions.getWorldId(block.getWorld().getName());
            Config.lookup_cache.put("" + block.getX() + "." + block.getY() + "." + block.getZ() + "." + world_id + "", new Object[]{Integer.valueOf(unixtimestamp), player.getName(), block.getType()});
         }
      }

   }

   protected void onBlockPiston(BlockPistonEvent event) {
      List event_blocks = null;
      if(event instanceof BlockPistonExtendEvent) {
         event_blocks = ((BlockPistonExtendEvent)event).getBlocks();
      } else if(event instanceof BlockPistonRetractEvent) {
         event_blocks = ((BlockPistonRetractEvent)event).getBlocks();
      }

      World world = event.getBlock().getWorld();
      if(Functions.checkConfig(world, "pistons") == 1 && !event.isCancelled()) {
         ArrayList nblocks = new ArrayList();
         ArrayList blocks = new ArrayList();
         Iterator b = event_blocks.iterator();

         Block bm;
         while(b.hasNext()) {
            Block d = (Block)b.next();
            bm = d.getRelative(event.getDirection());
            if(Functions.checkConfig(world, "block-movement") == 1) {
               bm = Functions.fallingSand(bm, d.getState(), "#piston");
            }

            nblocks.add(bm);
            blocks.add(d.getState());
         }

         Block var20 = event.getBlock();
         BlockFace var21 = event.getDirection();
         bm = var20.getRelative(var21);
         int wid = Functions.getWorldId(bm.getWorld().getName());
         int unixtimestamp = (int)(System.currentTimeMillis() / 1000L);
         boolean log = false;

         for(int l = 0; l <= nblocks.size(); ++l) {
            int e = l - 1;
            Block c = null;
            if(e == -1) {
               c = bm;
            } else {
               c = (Block)nblocks.get(e);
            }

            if(c != null) {
               int block = c.getX();
               int nblock = c.getY();
               int block1 = c.getZ();
               Material t = c.getType();
               String cords = "" + block + "." + nblock + "." + block1 + "." + wid + "." + t.name() + "";
               if(Config.piston_cache.get(cords) == null) {
                  log = true;
               }

               Config.piston_cache.put(cords, new Object[]{Integer.valueOf(unixtimestamp)});
            }
         }

         if(log) {
            String var22 = "#piston";
            Iterator var23 = blocks.iterator();

            while(var23.hasNext()) {
               BlockState var24 = (BlockState)var23.next();
               Queue.queueBlockBreak(var22, var24, var24.getType(), Functions.getData(var24));
            }

            int var25 = 0;

            for(Iterator var26 = nblocks.iterator(); var26.hasNext(); ++var25) {
               Block var27 = (Block)var26.next();
               BlockState var28 = (BlockState)blocks.get(var25);
               Queue.queueBlockPlace(var22, var27.getState(), var28.getType(), Functions.getData(var28));
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockPistonExtend(BlockPistonExtendEvent event) {
      this.onBlockPiston(event);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockPistonRetract(BlockPistonRetractEvent event) {
      this.onBlockPiston(event);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockPlace(BlockPlaceEvent event) {
      World world = event.getBlockPlaced().getWorld();
      if(!event.isCancelled() && Functions.checkConfig(world, "block-place") == 1) {
         Player player = event.getPlayer();
         Block b = event.getBlockPlaced();
         Block block = b;
         BlockState breplaced = event.getBlockReplacedState();
         Material force_type = null;
         byte force_data = -1;
         boolean abort = false;
         Material block_type = b.getType();
         List stairs = Arrays.asList(new Material[]{Material.WOOD_STAIRS, Material.COBBLESTONE_STAIRS, Material.BRICK_STAIRS, Material.SMOOTH_STAIRS, Material.NETHER_BRICK_STAIRS, Material.SANDSTONE_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.QUARTZ_STAIRS});
         List dir_blocks = Arrays.asList(new Material[]{Material.PISTON_STICKY_BASE, Material.PISTON_BASE, Material.DIODE_BLOCK_OFF, Material.SKULL, Material.REDSTONE_COMPARATOR_OFF});
         if(!Functions.listContains(BlockInfo.containers, block_type) && !Functions.listContains(dir_blocks, block_type) && !Functions.listContains(stairs, block_type)) {
            if(block_type.equals(Material.FIRE)) {
               ItemStack item = event.getItemInHand();
               if(!item.getType().equals(Material.FIRE)) {
                  abort = true;
               }
            }
         } else {
            Queue.queueBlockPlaceDelayed(player.getName(), b, breplaced, 0);
            abort = true;
         }

         if(!abort) {
            if(Functions.checkConfig(world, "block-movement") == 1) {
               block = Functions.fallingSand(b, (BlockState)null, player.getName());
               if(!block.equals(b)) {
                  force_type = b.getType();
               }
            }

            Queue.queueBlockPlace(player, block.getState(), b, breplaced, force_type, force_data);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onBlockSpread(BlockSpreadEvent event) {
      if(!event.isCancelled() && Functions.checkConfig(event.getBlock().getWorld(), "vine-growth") == 1) {
         Block block = event.getBlock();
         BlockState blockstate = event.getNewState();
         if(blockstate.getType().equals(Material.VINE)) {
            Queue.queueBlockPlace("#vine", block.getState(), blockstate.getType(), Functions.getData(blockstate));
         }
      }

   }
}
