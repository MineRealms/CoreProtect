package net.coreprotect.consumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.coreprotect.Functions;
import net.coreprotect.consumer.Consumer;
import net.coreprotect.database.Database;
import net.coreprotect.database.Logger;
import net.coreprotect.database.Lookup;
import net.coreprotect.model.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Process {

   private static Connection connection = null;
   private static int lastConnection = 0;


   private static void validateConnection() {
      try {
         if(connection != null) {
            int e = (int)(System.currentTimeMillis() / 1000L) - lastConnection;
            if(e > 900 || !connection.isValid(5) || !Config.server_running || Consumer.resetConnection) {
               connection.close();
               connection = null;
               Consumer.resetConnection = false;
            }
         }

         if(connection == null && Config.server_running) {
            connection = Database.getConnection(false);
            lastConnection = (int)(System.currentTimeMillis() / 1000L);
         }
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }

   public static void processConsumer(int process_id) {
      label174: {
         try {
            validateConnection();
            if(connection != null) {
               Consumer.is_paused = true;
               Statement e = connection.createStatement();
               ArrayList consumer_data = (ArrayList)Consumer.consumer.get(Integer.valueOf(process_id));
               Map users = (Map)Consumer.consumer_users.get(Integer.valueOf(process_id));
               Map blocks = (Map)Consumer.consumer_object.get(Integer.valueOf(process_id));
               Database.beginTransaction(e);
               Iterator preparedStmt_signs = users.entrySet().iterator();

               while(preparedStmt_signs.hasNext()) {
                  Entry preparedStmt_blocks = (Entry)preparedStmt_signs.next();
                  String[] preparedStmt_skulls = (String[])preparedStmt_blocks.getValue();
                  String preparedStmt_containers = preparedStmt_skulls[0];
                  String preparedStmt_worlds = preparedStmt_skulls[1];
                  if(Config.player_id_cache.get(preparedStmt_containers.toLowerCase()) == null) {
                     Database.loadUserID(connection, preparedStmt_containers, preparedStmt_worlds);
                  }
               }

               Database.commitTransaction(e);
               PreparedStatement var36 = Database.prepareStatement(connection, 0, false);
               PreparedStatement var37 = Database.prepareStatement(connection, 1, false);
               PreparedStatement var38 = Database.prepareStatement(connection, 2, true);
               PreparedStatement var39 = Database.prepareStatement(connection, 3, false);
               PreparedStatement var40 = Database.prepareStatement(connection, 4, false);
               PreparedStatement preparedStmt_chat = Database.prepareStatement(connection, 5, false);
               PreparedStatement preparedStmt_command = Database.prepareStatement(connection, 6, false);
               PreparedStatement preparedStmt_session = Database.prepareStatement(connection, 7, false);
               PreparedStatement preparedStmt_entities = Database.prepareStatement(connection, 8, true);
               PreparedStatement preparedStmt_materials = Database.prepareStatement(connection, 9, false);
               PreparedStatement preparedStmt_art = Database.prepareStatement(connection, 10, false);
               PreparedStatement preparedStmt_entity = Database.prepareStatement(connection, 11, false);
               Database.beginTransaction(e);

               for(int i = 0; i < consumer_data.size(); ++i) {
                  Object[] data = (Object[])consumer_data.get(i);
                  if(data != null) {
                     int id = ((Integer)data[0]).intValue();
                     int action = ((Integer)data[1]).intValue();
                     Material block_type = (Material)data[2];
                     int block_data = ((Integer)data[3]).intValue();
                     Material replace_type = (Material)data[4];
                     int replace_data = ((Integer)data[5]).intValue();
                     int force_data = ((Integer)data[6]).intValue();
                     if(users.get(Integer.valueOf(id)) != null && blocks.get(Integer.valueOf(id)) != null) {
                        String user = ((String[])users.get(Integer.valueOf(id)))[0];
                        Object object = blocks.get(Integer.valueOf(id));

                        try {
                           switch(action) {
                           case 0:
                              processBlockBreak(var37, var38, process_id, id, block_type, block_data, replace_type, force_data, user, object);
                              break;
                           case 1:
                              processBlockPlace(var37, var38, block_type, block_data, replace_type, replace_data, force_data, user, object);
                              break;
                           case 2:
                              processSignText(var36, process_id, id, force_data, user, object);
                              break;
                           case 3:
                              processContainerBreak(var39, process_id, id, user, object);
                              break;
                           case 4:
                              processPlayerInteraction(var37, user, object);
                              break;
                           case 5:
                              processContainerTransaction(var39, process_id, id, force_data, user, object);
                              break;
                           case 6:
                              processStructureGrowth(e, var37, process_id, id, user, object);
                              break;
                           case 7:
                              processRollbackUpdate(e, process_id, id, force_data, 0);
                              break;
                           case 8:
                              processRollbackUpdate(e, process_id, id, force_data, 1);
                              break;
                           case 9:
                              processWorldInsert(var40, user, force_data);
                              break;
                           case 10:
                              processSignUpdate(e, object, block_data, force_data);
                              break;
                           case 11:
                              processSkullUpdate(e, object, force_data);
                              break;
                           case 12:
                              processPlayerChat(preparedStmt_chat, process_id, id, force_data, user);
                              break;
                           case 13:
                              processPlayerCommand(preparedStmt_command, process_id, id, force_data, user);
                              break;
                           case 14:
                              processPlayerLogin(connection, preparedStmt_session, process_id, id, object, block_data, replace_data, force_data, user);
                              break;
                           case 15:
                              processPlayerLogout(preparedStmt_session, object, force_data, user);
                              break;
                           case 16:
                              processEntityKill(var37, preparedStmt_entities, process_id, id, object, user);
                              break;
                           case 17:
                              processEntitySpawn(e, object, force_data);
                              break;
                           case 18:
                              processHangingRemove(object, force_data);
                              break;
                           case 19:
                              processHangingSpawn(object, block_type, block_data, force_data);
                              break;
                           case 20:
                              processNaturalBlockBreak(e, var37, process_id, id, user, object, block_type, block_data);
                              break;
                           case 21:
                              processMaterialInsert(preparedStmt_materials, user, force_data);
                              break;
                           case 22:
                              processMaterialInsert(preparedStmt_art, user, force_data);
                              break;
                           case 23:
                              processMaterialInsert(preparedStmt_entity, user, force_data);
                              break;
                           case 24:
                              processPlayerKill(var37, id, object, user);
                           }
                        } catch (Exception var33) {
                           var33.printStackTrace();
                        }

                        users.remove(Integer.valueOf(id));
                        blocks.remove(Integer.valueOf(id));
                     }
                  }
               }

               Database.commitTransaction(e);
               consumer_data.clear();
               var36.close();
               var37.close();
               var38.close();
               var39.close();
               var40.close();
               preparedStmt_chat.close();
               preparedStmt_command.close();
               preparedStmt_session.close();
               preparedStmt_entities.close();
               preparedStmt_materials.close();
               preparedStmt_art.close();
               preparedStmt_entity.close();
               e.close();
               break label174;
            }
         } catch (Exception var34) {
            var34.printStackTrace();
            break label174;
         } finally {
            validateConnection();
         }

         return;
      }

      Consumer.is_paused = false;
   }

   private static void processBlockBreak(PreparedStatement preparedStmt, PreparedStatement preparedStmt_skulls, int process_id, int id, Material block_type, int block_data, Material replace_type, int force_data, String user, Object object) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         List meta = Functions.processMeta(block);
         if(block instanceof Skull) {
            Logger.log_skull_break(preparedStmt, preparedStmt_skulls, user, block);
         } else {
            Logger.log_break(preparedStmt, user, block.getLocation(), Functions.block_id(block_type), block_data, meta);
            if(force_data == 5 && (block_type.equals(Material.WOODEN_DOOR) || block_type.equals(Material.SPRUCE_DOOR) || block_type.equals(Material.BIRCH_DOOR) || block_type.equals(Material.JUNGLE_DOOR) || block_type.equals(Material.ACACIA_DOOR) || block_type.equals(Material.DARK_OAK_DOOR) || block_type.equals(Material.IRON_DOOR_BLOCK)) && !replace_type.equals(Material.WOODEN_DOOR) && !replace_type.equals(Material.SPRUCE_DOOR) && !replace_type.equals(Material.BIRCH_DOOR) && !replace_type.equals(Material.JUNGLE_DOOR) && !replace_type.equals(Material.ACACIA_DOOR) && !replace_type.equals(Material.DARK_OAK_DOOR) && !replace_type.equals(Material.IRON_DOOR_BLOCK)) {
               int d = block_data;
               if(block_data < 9) {
                  d = block_data + 8;
               }

               Location location = block.getLocation();
               location.setY(location.getY() + 1.0D);
               Logger.log_break(preparedStmt, user, location, Functions.block_id(block_type), d, (List)null);
            }
         }
      }

   }

   private static void processBlockPlace(PreparedStatement preparedStmt, PreparedStatement preparedStmt_skulls, Material block_type, int block_data, Material replace_type, int replace_data, int force_data, String user, Object object) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         List meta = Functions.processMeta(block);
         if(block_type.equals(Material.SKULL)) {
            Logger.log_skull_place(preparedStmt, preparedStmt_skulls, user, block, Functions.block_id(replace_type), replace_data);
         } else if(force_data == 1) {
            Logger.log_place(preparedStmt, user, block, Functions.block_id(replace_type), replace_data, block_type, block_data, true, meta);
         } else {
            Logger.log_place(preparedStmt, user, block, Functions.block_id(replace_type), replace_data, block_type, block_data, false, meta);
         }
      }

   }

   private static void processContainerBreak(PreparedStatement preparedStmt, int process_id, int id, String user, Object object) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Map containers = (Map)Consumer.consumer_containers.get(Integer.valueOf(process_id));
         if(containers.get(Integer.valueOf(id)) != null) {
            ItemStack[] container = (ItemStack[])containers.get(Integer.valueOf(id));
            Logger.log_container_break(preparedStmt, user, block.getLocation(), block.getType(), container);
            containers.remove(Integer.valueOf(id));
         }
      }

   }

   private static void processContainerTransaction(PreparedStatement preparedStmt, int process_id, int id, int force_data, String user, Object object) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Map inventories = (Map)Consumer.consumer_inventories.get(Integer.valueOf(process_id));
         if(inventories.get(Integer.valueOf(id)) != null) {
            Object inventory = inventories.get(Integer.valueOf(id));
            String logging_chest_id = user.toLowerCase() + "." + block.getX() + "." + block.getY() + "." + block.getZ();
            if(Config.logging_chest.get(logging_chest_id) != null) {
               int current_chest = ((Integer)Config.logging_chest.get(logging_chest_id)).intValue();
               if(Config.old_container.get(logging_chest_id) == null) {
                  return;
               }

               int force_size = 0;
               if(Config.force_containers.get(logging_chest_id) != null) {
                  force_size = ((List)Config.force_containers.get(logging_chest_id)).size();
               }

               if(current_chest == force_data || force_size > 0) {
                  Logger.log_container(preparedStmt, user, block.getType(), inventory, block.getLocation());
                  List old = (List)Config.old_container.get(logging_chest_id);
                  if(old.size() == 0) {
                     Config.old_container.remove(logging_chest_id);
                     Config.logging_chest.remove(logging_chest_id);
                  }
               }
            }

            inventories.remove(Integer.valueOf(id));
         }
      }

   }

   private static void processEntityKill(PreparedStatement preparedStmt, PreparedStatement preparedStmt_entities, int process_id, int id, Object object, String user) {
      if(object instanceof Object[]) {
         BlockState block = (BlockState)((Object[])((Object[])object))[0];
         EntityType type = (EntityType)((Object[])((Object[])object))[1];
         Map object_lists = (Map)Consumer.consumer_object_list.get(Integer.valueOf(process_id));
         if(object_lists.get(Integer.valueOf(id)) != null) {
            List object_list = (List)object_lists.get(Integer.valueOf(id));
            int entityId = Functions.getEntityId(type);
            Logger.log_entity_kill(preparedStmt, preparedStmt_entities, user, block, object_list, entityId);
            object_lists.remove(Integer.valueOf(id));
         }
      }

   }

   private static void processEntitySpawn(Statement statement, Object object, int row_id) {
      if(object instanceof Object[]) {
         BlockState block = (BlockState)((Object[])((Object[])object))[0];
         EntityType type = (EntityType)((Object[])((Object[])object))[1];
         String query = "SELECT data FROM " + Config.prefix + "entity WHERE rowid=\'" + row_id + "\' LIMIT 0, 1";
         List data = Database.getEntityData(statement, block, query);
         Functions.spawnEntity(block, type, data);
      }

   }

   private static void processHangingRemove(Object object, int delay) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Functions.removeHanging(block, delay);
      }

   }

   private static void processHangingSpawn(Object object, Material type, int data, int delay) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Functions.spawnHanging(block, type, data, delay);
      }

   }

   private static void processMaterialInsert(PreparedStatement preparedStmt, String name, int material_id) {
      Database.insertMaterial(preparedStmt, material_id, name);
   }

   private static void processNaturalBlockBreak(Statement statement, PreparedStatement preparedStmt, int process_id, int id, String user, Object object, Material block_type, int block_data) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Map block_lists = (Map)Consumer.consumer_block_list.get(Integer.valueOf(process_id));
         if(block_lists.get(Integer.valueOf(id)) != null) {
            List block_list = (List)block_lists.get(Integer.valueOf(id));
            Iterator var11 = block_list.iterator();

            while(var11.hasNext()) {
               BlockState list_block = (BlockState)var11.next();
               String removed = Lookup.who_removed_cache(list_block);
               if(removed.length() > 0) {
                  user = removed;
               }
            }

            block_lists.remove(Integer.valueOf(id));
            Logger.log_break(preparedStmt, user, block.getLocation(), Functions.block_id(block_type), block_data, (List)null);
         }
      }

   }

   private static void processPlayerChat(PreparedStatement preparedStmt, int process_id, int id, int time, String user) {
      Map strings = (Map)Consumer.consumer_strings.get(Integer.valueOf(process_id));
      if(strings.get(Integer.valueOf(id)) != null) {
         String message = (String)strings.get(Integer.valueOf(id));
         Logger.log_chat(preparedStmt, time, user, message);
         strings.remove(Integer.valueOf(id));
      }

   }

   private static void processPlayerCommand(PreparedStatement preparedStmt, int process_id, int id, int time, String user) {
      Map strings = (Map)Consumer.consumer_strings.get(Integer.valueOf(process_id));
      if(strings.get(Integer.valueOf(id)) != null) {
         String message = (String)strings.get(Integer.valueOf(id));
         Logger.log_command(preparedStmt, time, user, message);
         strings.remove(Integer.valueOf(id));
      }

   }

   private static void processPlayerInteraction(PreparedStatement preparedStmt, String user, Object object) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Logger.log_interact(preparedStmt, user, block);
      }

   }

   private static void processPlayerKill(PreparedStatement preparedStmt, int id, Object object, String user) {
      if(object instanceof Object[]) {
         BlockState block = (BlockState)((Object[])((Object[])object))[0];
         String player = (String)((Object[])((Object[])object))[1];
         Logger.log_player_kill(preparedStmt, user, block, player);
      }

   }

   private static void processPlayerLogin(Connection connection, PreparedStatement preparedStmt, int process_id, int id, Object object, int configSessions, int configUsernames, int time, String user) {
      if(object instanceof BlockState) {
         Map strings = (Map)Consumer.consumer_strings.get(Integer.valueOf(process_id));
         if(strings.get(Integer.valueOf(id)) != null) {
            String uuid = (String)strings.get(Integer.valueOf(id));
            BlockState block = (BlockState)object;
            Logger.log_username(connection, user, uuid, configUsernames, time);
            if(configSessions == 1) {
               Logger.log_session(preparedStmt, user, block, time, 1);
            }

            strings.remove(Integer.valueOf(id));
         }
      }

   }

   private static void processPlayerLogout(PreparedStatement preparedStmt, Object object, int time, String user) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Logger.log_session(preparedStmt, user, block, time, 0);
      }

   }

   private static void processRollbackUpdate(Statement statement, int process_id, int id, int action, int table) {
      Map update_lists = (Map)Consumer.consumer_object_array_list.get(Integer.valueOf(process_id));
      if(update_lists.get(Integer.valueOf(id)) != null) {
         List list = (List)update_lists.get(Integer.valueOf(id));
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            Object[] list_row = (Object[])var7.next();
            int rowid = ((Integer)list_row[0]).intValue();
            int rolled_back = ((Integer)list_row[9]).intValue();
            if(rolled_back == action) {
               Database.performUpdate(statement, rowid, action, table);
            }
         }

         update_lists.remove(Integer.valueOf(id));
      }

   }

   private static void processSignText(PreparedStatement preparedStmt, int process_id, int id, int force_data, String user, Object object) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Map signs = (Map)Consumer.consumer_signs.get(Integer.valueOf(process_id));
         if(signs.get(Integer.valueOf(id)) != null) {
            String[] sign_text = (String[])signs.get(Integer.valueOf(id));
            Logger.sign_text(preparedStmt, user, block, sign_text[0], sign_text[1], sign_text[2], sign_text[3], force_data);
            signs.remove(Integer.valueOf(id));
         }
      }

   }

   private static void processSignUpdate(Statement statement, Object object, int action, int time) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         int x = block.getX();
         int y = block.getY();
         int z = block.getZ();
         int wid = Functions.getWorldId(block.getWorld().getName());
         String query = "";
         if(action == 0) {
            query = "SELECT line_1, line_2, line_3, line_4 FROM " + Config.prefix + "sign WHERE wid=\'" + wid + "\' AND x=\'" + x + "\' AND z=\'" + z + "\' AND y=\'" + y + "\' AND time < \'" + time + "\' ORDER BY rowid DESC LIMIT 0, 1";
         } else {
            query = "SELECT line_1, line_2, line_3, line_4 FROM " + Config.prefix + "sign WHERE wid=\'" + wid + "\' AND x=\'" + x + "\' AND z=\'" + z + "\' AND y=\'" + y + "\' AND time >= \'" + time + "\' ORDER BY rowid ASC LIMIT 0, 1";
         }

         Database.getSignData(statement, block, query);
         Functions.updateBlock(block);
      }

   }

   private static void processSkullUpdate(Statement statement, Object object, int row_id) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         String query = "SELECT type,data,rotation,owner FROM " + Config.prefix + "skull WHERE rowid=\'" + row_id + "\' LIMIT 0, 1";
         Database.getSkullData(statement, block, query);
         Functions.updateBlock(block);
      }

   }

   private static void processStructureGrowth(Statement statement, PreparedStatement preparedStmt, int process_id, int id, String user, Object object) {
      if(object instanceof BlockState) {
         BlockState block = (BlockState)object;
         Map block_lists = (Map)Consumer.consumer_block_list.get(Integer.valueOf(process_id));
         if(block_lists.get(Integer.valueOf(id)) != null) {
            List block_list = (List)block_lists.get(Integer.valueOf(id));
            String result_data = Lookup.who_placed(statement, block);
            if(result_data.length() > 0) {
               user = result_data;
            }

            Iterator var10 = block_list.iterator();

            while(var10.hasNext()) {
               BlockState list_block = (BlockState)var10.next();
               if(list_block.getY() >= block.getY()) {
                  Logger.log_place(preparedStmt, user, list_block, 0, 0, (Material)null, -1, false, (List)null);
               }
            }

            block_lists.remove(Integer.valueOf(id));
         }
      }

   }

   private static void processWorldInsert(PreparedStatement preparedStmt, String world, int world_id) {
      Database.insertWorld(preparedStmt, world_id, world);
   }

}
