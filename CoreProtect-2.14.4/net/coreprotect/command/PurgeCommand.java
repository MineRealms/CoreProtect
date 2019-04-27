package net.coreprotect.command;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.coreprotect.CoreProtect;
import net.coreprotect.Functions;
import net.coreprotect.command.CommandHandler;
import net.coreprotect.consumer.Consumer;
import net.coreprotect.database.Database;
import net.coreprotect.model.Config;
import net.coreprotect.patch.Patch;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PurgeCommand extends Consumer {

   protected static void runCommand(final CommandSender player, boolean permission, String[] args) {
      int resultc = args.length;
      final int seconds = CommandHandler.parseTime(args);
      if(Config.converter_running) {
         player.sendMessage("§3CoreProtect §f- Upgrade in progress. Please try again later.");
      } else if(Config.purge_running) {
         player.sendMessage("§3CoreProtect §f- Purge in progress. Please try again later.");
      } else if(!permission) {
         player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
      } else if(resultc <= 1) {
         player.sendMessage("§3CoreProtect §f- Please use \"/co purge t:<time>\".");
      } else if(seconds <= 0) {
         player.sendMessage("§3CoreProtect §f- Please use \"/co purge t:<time>\".");
      } else if(player instanceof Player && seconds < 2592000) {
         player.sendMessage("§3CoreProtect §f- You can only purge data older than 30 days.");
      } else if(seconds < 86400) {
         player.sendMessage("§3CoreProtect §f- You can only purge data older than 24 hours.");
      } else {
         final boolean optimizeCheck = false;
         String[] optimize = args;
         int runnable = args.length;

         for(int thread = 0; thread < runnable; ++thread) {
            String arg = optimize[thread];
            if(arg.trim().equalsIgnoreCase("#optimize")) {
               optimizeCheck = true;
               break;
            }
         }

         class BasicThread implements Runnable {

            public void run() {
               try {
                  int e = (int)(System.currentTimeMillis() / 1000L);
                  int ptime = e - seconds;
                  long removed = 0L;
                  Connection connection = null;

                  for(int query = 0; query <= 5; ++query) {
                     connection = Database.getConnection(false);
                     if(connection != null) {
                        break;
                     }

                     Thread.sleep(1000L);
                  }

                  if(connection == null) {
                     Functions.messageOwnerAndUser(player, "Database busy. Please try again later.");
                     return;
                  }

                  Functions.messageOwnerAndUser(player, "Data purge started. This may take some time.");
                  Functions.messageOwnerAndUser(player, "Do not restart your server until completed.");
                  Config.purge_running = true;

                  while(!PurgeCommand.pause_success) {
                     Thread.sleep(1L);
                  }

                  Consumer.is_paused = true;
                  String var36 = "";
                  PreparedStatement preparedStmt = null;
                  boolean abort = false;
                  String purge_prefix = "tmp_" + Config.prefix;
                  if(((Integer)Config.config.get("use-mysql")).intValue() == 0) {
                     var36 = "ATTACH DATABASE \'" + Config.sqlite + ".tmp\' AS tmp_db";
                     preparedStmt = connection.prepareStatement(var36);
                     preparedStmt.execute();
                     preparedStmt.close();
                     purge_prefix = "tmp_db." + Config.prefix;
                  }

                  String[] version_split = CoreProtect.getInstance().getDescription().getVersion().split("\\.");
                  Integer[] current_version = new Integer[]{Integer.valueOf(Integer.parseInt(version_split[0])), Integer.valueOf(Integer.parseInt(version_split[1])), Integer.valueOf(Integer.parseInt(version_split[2]))};
                  Integer[] last_version = Patch.getLastVersion(connection);
                  boolean newVersion = Functions.newVersion(last_version, current_version);
                  if(newVersion) {
                     Functions.messageOwnerAndUser(player, "Purge failed. Please try again later.");
                     Consumer.is_paused = false;
                     Config.purge_running = false;
                     return;
                  }

                  if(((Integer)Config.config.get("use-mysql")).intValue() == 0) {
                     Iterator purge_tables = Config.databaseTables.iterator();

                     while(purge_tables.hasNext()) {
                        String table = (String)purge_tables.next();

                        try {
                           var36 = "DROP TABLE IF EXISTS " + purge_prefix + table + "";
                           preparedStmt = connection.prepareStatement(var36);
                           preparedStmt.execute();
                           preparedStmt.close();
                        } catch (Exception var31) {
                           var31.printStackTrace();
                        }
                     }

                     Functions.createDatabaseTables(purge_prefix, true);
                  }

                  List var37 = Arrays.asList(new String[]{"sign", "container", "skull", "session", "chat", "command", "entity", "block"});
                  Iterator var38 = Config.databaseTables.iterator();

                  String table1;
                  while(var38.hasNext()) {
                     table1 = (String)var38.next();
                     String tableName = table1.replaceAll("_", " ");
                     Functions.messageOwnerAndUser(player, "Processing " + tableName + " data...");
                     if(((Integer)Config.config.get("use-mysql")).intValue() == 0) {
                        String e1 = "";
                        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + purge_prefix + table1);
                        ResultSetMetaData resultSetMetaData = rs.getMetaData();
                        int columnCount = resultSetMetaData.getColumnCount();

                        String old_count;
                        for(int error = 1; error <= columnCount; ++error) {
                           old_count = resultSetMetaData.getColumnName(error);
                           if(e1.length() == 0) {
                              e1 = old_count;
                           } else {
                              e1 = e1 + "," + old_count;
                           }
                        }

                        rs.close();
                        boolean var39 = false;

                        try {
                           old_count = "";
                           if(var37.contains(table1)) {
                              old_count = " WHERE time >= \'" + ptime + "\'";
                           }

                           var36 = "INSERT INTO " + purge_prefix + table1 + " SELECT " + e1 + " FROM " + Config.prefix + table1 + old_count;
                           preparedStmt = connection.prepareStatement(var36);
                           preparedStmt.execute();
                           preparedStmt.close();
                        } catch (Exception var30) {
                           var39 = true;
                           var30.printStackTrace();
                        }

                        if(var39) {
                           Functions.messageOwnerAndUser(player, "Unable to process " + tableName + " data!");
                           Functions.messageOwnerAndUser(player, "Attempting to repair. This may take some time...");

                           try {
                              var36 = "DELETE FROM " + purge_prefix + table1;
                              preparedStmt = connection.prepareStatement(var36);
                              preparedStmt.execute();
                              preparedStmt.close();
                           } catch (Exception var29) {
                              var29.printStackTrace();
                           }

                           try {
                              var36 = "REINDEX " + Config.prefix + table1;
                              preparedStmt = connection.prepareStatement(var36);
                              preparedStmt.execute();
                              preparedStmt.close();
                           } catch (Exception var28) {
                              var28.printStackTrace();
                           }

                           try {
                              old_count = " NOT INDEXED";
                              var36 = "INSERT INTO " + purge_prefix + table1 + " SELECT " + e1 + " FROM " + Config.prefix + table1 + old_count;
                              preparedStmt = connection.prepareStatement(var36);
                              preparedStmt.execute();
                              preparedStmt.close();
                           } catch (Exception var32) {
                              var32.printStackTrace();
                              abort = true;
                              break;
                           }

                           if(var37.contains(table1)) {
                              try {
                                 var36 = "DELETE FROM " + purge_prefix + table1 + " WHERE time < \'" + ptime + "\'";
                                 preparedStmt = connection.prepareStatement(var36);
                                 preparedStmt.execute();
                                 preparedStmt.close();
                              } catch (Exception var27) {
                                 var27.printStackTrace();
                              }
                           }
                        }

                        int var41 = 0;

                        try {
                           var36 = "SELECT COUNT(*) as count FROM " + Config.prefix + table1 + " LIMIT 0, 1";
                           preparedStmt = connection.prepareStatement(var36);

                           ResultSet new_count;
                           for(new_count = preparedStmt.executeQuery(); new_count.next(); var41 = new_count.getInt("count")) {
                              ;
                           }

                           new_count.close();
                           preparedStmt.close();
                        } catch (Exception var34) {
                           var34.printStackTrace();
                        }

                        int var40 = 0;

                        try {
                           var36 = "SELECT COUNT(*) as count FROM " + purge_prefix + table1 + " LIMIT 0, 1";
                           preparedStmt = connection.prepareStatement(var36);

                           ResultSet e2;
                           for(e2 = preparedStmt.executeQuery(); e2.next(); var40 = e2.getInt("count")) {
                              ;
                           }

                           e2.close();
                           preparedStmt.close();
                        } catch (Exception var33) {
                           var33.printStackTrace();
                        }

                        removed += (long)(var41 - var40);
                     }

                     if(((Integer)Config.config.get("use-mysql")).intValue() == 1) {
                        try {
                           if(var37.contains(table1)) {
                              var36 = "DELETE FROM " + Config.prefix + table1 + " WHERE time < \'" + ptime + "\'";
                              preparedStmt = connection.prepareStatement(var36);
                              preparedStmt.execute();
                              removed += (long)preparedStmt.getUpdateCount();
                              preparedStmt.close();
                           }
                        } catch (Exception var26) {
                           var26.printStackTrace();
                        }
                     }
                  }

                  if(((Integer)Config.config.get("use-mysql")).intValue() == 1 && optimizeCheck) {
                     Functions.messageOwnerAndUser(player, "Optimizing database. Please wait...");
                     var38 = Config.databaseTables.iterator();

                     while(var38.hasNext()) {
                        table1 = (String)var38.next();
                        var36 = "OPTIMIZE LOCAL TABLE " + Config.prefix + table1 + "";
                        preparedStmt = connection.prepareStatement(var36);
                        preparedStmt.execute();
                        preparedStmt.close();
                     }
                  }

                  connection.close();
                  if(abort) {
                     if(((Integer)Config.config.get("use-mysql")).intValue() == 0) {
                        (new File(Config.sqlite + ".tmp")).delete();
                     }

                     Config.loadDatabase();
                     Functions.messageOwnerAndUser(player, "§cPurge failed. Database may be corrupt.");
                     Consumer.is_paused = false;
                     Config.purge_running = false;
                     return;
                  }

                  if(((Integer)Config.config.get("use-mysql")).intValue() == 0) {
                     (new File(Config.sqlite)).delete();
                     (new File(Config.sqlite + ".tmp")).renameTo(new File(Config.sqlite));
                     Functions.messageOwnerAndUser(player, "Indexing database. Please wait...");
                  }

                  Config.loadDatabase();
                  Functions.messageOwnerAndUser(player, "Data purge successful.");
                  Functions.messageOwnerAndUser(player, NumberFormat.getInstance().format(removed) + " row(s) of data deleted.");
               } catch (Exception var35) {
                  Functions.messageOwnerAndUser(player, "Purge failed. Please try again later.");
                  var35.printStackTrace();
               }

               Consumer.is_paused = false;
               Config.purge_running = false;
            }
         }

         BasicThread var10 = seconds.new BasicThread();
         Thread var11 = new Thread(var10);
         var11.start();
      }
   }
}
