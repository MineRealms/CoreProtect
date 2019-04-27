package net.coreprotect.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import net.coreprotect.Functions;
import net.coreprotect.consumer.Consumer;
import net.coreprotect.consumer.Queue;
import net.coreprotect.model.BlockInfo;
import net.coreprotect.model.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

public class Database extends Queue {

   public static void beginTransaction(Statement statement) {
      try {
         if(((Integer)Config.config.get("use-mysql")).intValue() == 1) {
            statement.executeUpdate("START TRANSACTION");
         } else {
            statement.executeUpdate("BEGIN TRANSACTION");
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public static void commitTransaction(Statement statement) {
      try {
         if(((Integer)Config.config.get("use-mysql")).intValue() == 1) {
            statement.executeUpdate("COMMIT");
         } else {
            statement.executeUpdate("COMMIT TRANSACTION");
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public static void containerBreakCheck(String user, Material type, Object container, Location location) {
      if(BlockInfo.containers.contains(type) && !BlockInfo.shulker_boxes.contains(type) && Functions.checkConfig(location.getWorld(), "item-transactions") == 1) {
         try {
            ItemStack[] e = Functions.getContainerContents(type, container, location);
            if(e != null) {
               BlockState blockState = location.getBlock().getState();
               ArrayList force_list = new ArrayList();
               force_list.add(Functions.get_container_state(e));
               Config.force_containers.put(user.toLowerCase() + "." + blockState.getX() + "." + blockState.getY() + "." + blockState.getZ(), force_list);
               ItemStack[] containerState = Functions.get_container_state(e);
               Queue.queueContainerBreak(user, blockState, type, containerState);
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }

   }

   public static Connection getConnection(boolean force) {
      Connection connection = null;

      try {
         if(!force && (Config.converter_running || Config.purge_running)) {
            return connection;
         }

         if(((Integer)Config.config.get("use-mysql")).intValue() == 1) {
            String e = "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database + "?useUnicode=true&characterEncoding=utf-8&connectTimeout=10000&useSSL=false";
            Class.forName(Config.driver).newInstance();
            connection = DriverManager.getConnection(e, Config.username, Config.password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("SET NAMES \'utf8\'");
            statement.close();
         } else {
            long e1 = System.currentTimeMillis();

            while(Consumer.is_paused && !force) {
               Thread.sleep(1L);
               long database = System.currentTimeMillis() - e1;
               if(database >= 250L) {
                  return connection;
               }
            }

            String database1 = "jdbc:sqlite:" + Config.sqlite + "";
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(database1);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return connection;
   }

   public static List getEntityData(Statement statement, BlockState block, String query) {
      Object result = new ArrayList();

      try {
         ResultSet e;
         List input;
         for(e = statement.executeQuery(query); e.next(); result = input) {
            byte[] data = e.getBytes("data");
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ins = new ObjectInputStream(bais);
            input = (List)ins.readObject();
         }

         e.close();
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      return (List)result;
   }

   public static void getSignData(Statement statement, BlockState block, String query) {
      try {
         if(!(block instanceof Sign)) {
            return;
         }

         Sign e = (Sign)block;
         ResultSet rs = statement.executeQuery(query);

         while(rs.next()) {
            String line1 = rs.getString("line_1");
            String line2 = rs.getString("line_2");
            String line3 = rs.getString("line_3");
            String line4 = rs.getString("line_4");
            e.setLine(0, line1);
            e.setLine(1, line2);
            e.setLine(2, line3);
            e.setLine(3, line4);
         }

         rs.close();
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   public static void getSkullData(Statement statement, BlockState block, String query) {
      try {
         if(!(block instanceof Skull)) {
            return;
         }

         Skull e = (Skull)block;
         ResultSet rs = statement.executeQuery(query);

         while(rs.next()) {
            int type = rs.getInt("type");
            int data = rs.getInt("data");
            int rotation = rs.getInt("rotation");
            String owner = rs.getString("owner");
            SkullType skulltype = Functions.getSkullType(type);
            BlockFace skullrotation = Functions.getBlockFace(rotation);
            e = (Skull)Functions.setRawData(e, (byte)data);
            e.setSkullType(skulltype);
            e.setRotation(skullrotation);
            if(owner != null && owner.length() > 0) {
               e.setOwner(owner);
            }
         }

         rs.close();
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   public static void insertBlock(PreparedStatement preparedStmt, int time, int id, int wid, int x, int y, int z, int type, int data, List meta, int action, int rolled_back) {
      try {
         byte[] e = null;
         if(meta != null) {
            e = Functions.convertByteData(meta);
         }

         preparedStmt.setInt(1, time);
         preparedStmt.setInt(2, id);
         preparedStmt.setInt(3, wid);
         preparedStmt.setInt(4, x);
         preparedStmt.setInt(5, y);
         preparedStmt.setInt(6, z);
         preparedStmt.setInt(7, type);
         preparedStmt.setInt(8, data);
         preparedStmt.setObject(9, e);
         preparedStmt.setInt(10, action);
         preparedStmt.setInt(11, rolled_back);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var13) {
         var13.printStackTrace();
      }

   }

   public static void insertChat(PreparedStatement preparedStmt, int time, int user, String message) {
      try {
         preparedStmt.setInt(1, time);
         preparedStmt.setInt(2, user);
         preparedStmt.setString(3, message);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void insertCommand(PreparedStatement preparedStmt, int time, int user, String message) {
      try {
         preparedStmt.setInt(1, time);
         preparedStmt.setInt(2, user);
         preparedStmt.setString(3, message);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void insertContainer(PreparedStatement preparedStmt, int time, int id, int wid, int x, int y, int z, int type, int data, int amount, List metadata, int action, int rolled_back) {
      try {
         byte[] e = Functions.convertByteData(metadata);
         preparedStmt.setInt(1, time);
         preparedStmt.setInt(2, id);
         preparedStmt.setInt(3, wid);
         preparedStmt.setInt(4, x);
         preparedStmt.setInt(5, y);
         preparedStmt.setInt(6, z);
         preparedStmt.setInt(7, type);
         preparedStmt.setInt(8, data);
         preparedStmt.setInt(9, amount);
         preparedStmt.setObject(10, e);
         preparedStmt.setInt(11, action);
         preparedStmt.setInt(12, rolled_back);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var14) {
         var14.printStackTrace();
      }

   }

   public static void insertEntity(PreparedStatement preparedStmt, int time, List data) {
      try {
         ByteArrayOutputStream e = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(e);
         oos.writeObject(data);
         oos.flush();
         oos.close();
         e.close();
         byte[] byte_data = e.toByteArray();
         preparedStmt.setInt(1, time);
         preparedStmt.setObject(2, byte_data);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static void insertMaterial(PreparedStatement preparedStmt, int id, String name) {
      try {
         preparedStmt.setInt(1, id);
         preparedStmt.setString(2, name);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static void insertSession(PreparedStatement preparedStmt, int time, int user, int wid, int x, int y, int z, int action) {
      try {
         preparedStmt.setInt(1, time);
         preparedStmt.setInt(2, user);
         preparedStmt.setInt(3, wid);
         preparedStmt.setInt(4, x);
         preparedStmt.setInt(5, y);
         preparedStmt.setInt(6, z);
         preparedStmt.setInt(7, action);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   public static void insertSign(PreparedStatement preparedStmt, int time, int id, int wid, int x, int y, int z, String line1, String line2, String line3, String line4) {
      try {
         preparedStmt.setInt(1, time);
         preparedStmt.setInt(2, id);
         preparedStmt.setInt(3, wid);
         preparedStmt.setInt(4, x);
         preparedStmt.setInt(5, y);
         preparedStmt.setInt(6, z);
         preparedStmt.setString(7, line1);
         preparedStmt.setString(8, line2);
         preparedStmt.setString(9, line3);
         preparedStmt.setString(10, line4);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var12) {
         var12.printStackTrace();
      }

   }

   public static void insertSkull(PreparedStatement preparedStmt, int time, int type, int data, int rotation, String owner) {
      try {
         preparedStmt.setInt(1, time);
         preparedStmt.setInt(2, type);
         preparedStmt.setInt(3, data);
         preparedStmt.setInt(4, rotation);
         preparedStmt.setString(5, owner);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   private static int insertUser(Connection connection, String user) {
      int id = -1;

      try {
         int e = (int)(System.currentTimeMillis() / 1000L);
         PreparedStatement preparedStmt = connection.prepareStatement("INSERT INTO " + Config.prefix + "user (time, user) VALUES (?, ?)", 1);
         preparedStmt.setInt(1, e);
         preparedStmt.setString(2, user);
         preparedStmt.executeUpdate();
         ResultSet keys = preparedStmt.getGeneratedKeys();
         keys.next();
         id = keys.getInt(1);
         keys.close();
         preparedStmt.close();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return id;
   }

   public static void insertWorld(PreparedStatement preparedStmt, int id, String world) {
      try {
         preparedStmt.setInt(1, id);
         preparedStmt.setString(2, world);
         preparedStmt.executeUpdate();
         preparedStmt.clearParameters();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static int loadUserID(Connection connection, String user, String uuid) {
      int id = -1;

      try {
         String e = "user LIKE ?";
         if(uuid != null) {
            e = e + " OR uuid = ?";
         }

         String query = "SELECT rowid as id, uuid FROM " + Config.prefix + "user WHERE " + e + " ORDER BY rowid ASC LIMIT 0, 1";
         PreparedStatement preparedStmt = connection.prepareStatement(query);
         preparedStmt.setString(1, user);
         if(uuid != null) {
            preparedStmt.setString(2, uuid);
         }

         ResultSet rs;
         for(rs = preparedStmt.executeQuery(); rs.next(); uuid = rs.getString("uuid")) {
            id = rs.getInt("id");
         }

         rs.close();
         preparedStmt.close();
         if(id == -1) {
            id = insertUser(connection, user);
         }

         Config.player_id_cache.put(user.toLowerCase(), Integer.valueOf(id));
         Config.player_id_cache_reversed.put(Integer.valueOf(id), user);
         if(uuid != null) {
            Config.uuid_cache.put(user.toLowerCase(), uuid);
            Config.uuid_cache_reversed.put(uuid, user);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      return id;
   }

   public static String loadUserName(Connection connection, int id) {
      String user = "";
      String uuid = null;

      try {
         Statement e = connection.createStatement();
         String query = "SELECT user, uuid FROM " + Config.prefix + "user WHERE rowid=\'" + id + "\' LIMIT 0, 1";

         ResultSet rs;
         for(rs = e.executeQuery(query); rs.next(); uuid = rs.getString("uuid")) {
            user = rs.getString("user");
         }

         if(user.length() == 0) {
            return user;
         }

         Config.player_id_cache.put(user.toLowerCase(), Integer.valueOf(id));
         Config.player_id_cache_reversed.put(Integer.valueOf(id), user);
         if(uuid != null) {
            Config.uuid_cache.put(user.toLowerCase(), uuid);
            Config.uuid_cache_reversed.put(uuid, user);
         }

         rs.close();
         e.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return user;
   }

   public static void performUpdate(Statement statement, int id, int action, int table) {
      try {
         byte e = 1;
         if(action == 1) {
            e = 0;
         }

         if(table == 1) {
            statement.executeUpdate("UPDATE " + Config.prefix + "container SET rolled_back=\'" + e + "\' WHERE rowid=\'" + id + "\'");
         } else {
            statement.executeUpdate("UPDATE " + Config.prefix + "block SET rolled_back=\'" + e + "\' WHERE rowid=\'" + id + "\'");
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static PreparedStatement prepareStatement(Connection connection, int type, boolean keys) {
      PreparedStatement prepared_statement = null;

      try {
         String e = "INSERT INTO " + Config.prefix + "sign (time, user, wid, x, y, z, line_1, line_2, line_3, line_4) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         String query_1 = "INSERT INTO " + Config.prefix + "block (time, user, wid, x, y, z, type, data, meta, action, rolled_back) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         String query_2 = "INSERT INTO " + Config.prefix + "skull (time, type, data, rotation, owner) VALUES (?, ?, ?, ?, ?)";
         String query_3 = "INSERT INTO " + Config.prefix + "container (time, user, wid, x, y, z, type, data, amount, metadata, action, rolled_back) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         String query_4 = "INSERT INTO " + Config.prefix + "world (id, world) VALUES (?, ?)";
         String query_5 = "INSERT INTO " + Config.prefix + "chat (time, user, message) VALUES (?, ?, ?)";
         String query_6 = "INSERT INTO " + Config.prefix + "command (time, user, message) VALUES (?, ?, ?)";
         String query_7 = "INSERT INTO " + Config.prefix + "session (time, user, wid, x, y, z, action) VALUES (?, ?, ?, ?, ?, ?, ?)";
         String query_8 = "INSERT INTO " + Config.prefix + "entity (time, data) VALUES (?, ?)";
         String query_9 = "INSERT INTO " + Config.prefix + "material_map (id, material) VALUES (?, ?)";
         String query_10 = "INSERT INTO " + Config.prefix + "art_map (id, art) VALUES (?, ?)";
         String query_11 = "INSERT INTO " + Config.prefix + "entity_map (id, entity) VALUES (?, ?)";
         switch(type) {
         case 0:
            prepared_statement = prepareStatement(connection, e, keys);
            break;
         case 1:
            prepared_statement = prepareStatement(connection, query_1, keys);
            break;
         case 2:
            prepared_statement = prepareStatement(connection, query_2, keys);
            break;
         case 3:
            prepared_statement = prepareStatement(connection, query_3, keys);
            break;
         case 4:
            prepared_statement = prepareStatement(connection, query_4, keys);
            break;
         case 5:
            prepared_statement = prepareStatement(connection, query_5, keys);
            break;
         case 6:
            prepared_statement = prepareStatement(connection, query_6, keys);
            break;
         case 7:
            prepared_statement = prepareStatement(connection, query_7, keys);
            break;
         case 8:
            prepared_statement = prepareStatement(connection, query_8, keys);
            break;
         case 9:
            prepared_statement = prepareStatement(connection, query_9, keys);
            break;
         case 10:
            prepared_statement = prepareStatement(connection, query_10, keys);
            break;
         case 11:
            prepared_statement = prepareStatement(connection, query_11, keys);
         }
      } catch (Exception var16) {
         var16.printStackTrace();
      }

      return prepared_statement;
   }

   private static PreparedStatement prepareStatement(Connection connection, String query, boolean keys) {
      PreparedStatement prepared_statement = null;

      try {
         if(keys) {
            prepared_statement = connection.prepareStatement(query, 1);
         } else {
            prepared_statement = connection.prepareStatement(query);
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return prepared_statement;
   }
}
