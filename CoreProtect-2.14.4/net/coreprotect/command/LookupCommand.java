package net.coreprotect.command;

import java.sql.Connection;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.coreprotect.CoreProtect;
import net.coreprotect.Functions;
import net.coreprotect.command.CommandHandler;
import net.coreprotect.database.Database;
import net.coreprotect.database.Lookup;
import net.coreprotect.model.Config;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LookupCommand {

   protected static void runCommand(final CommandSender player, boolean permission, String[] args) {
      int resultc = args.length;
      final Location lo = CommandHandler.parseLocation(player, args);
      List arg_users = CommandHandler.parseUsers(args);
      Integer[] arg_radius = CommandHandler.parseRadius(args, player, lo);
      final int arg_noisy = CommandHandler.parseNoisy(args);
      final List arg_exclude_users = CommandHandler.parseExcludedUsers(player, args);
      final List arg_exclude = CommandHandler.parseExcluded(player, args);
      final List arg_blocks = CommandHandler.parseRestricted(player, args);
      final String ts = CommandHandler.parseTimeString(args);
      int rbseconds = CommandHandler.parseTime(args);
      final int arg_wid = CommandHandler.parseWorld(args);
      final List arg_action = CommandHandler.parseAction(args);
      final boolean count = CommandHandler.parseCount(args);
      boolean worldedit = CommandHandler.parseWorldEdit(args);
      boolean page_lookup = false;
      if(arg_blocks != null && arg_exclude != null && arg_exclude_users != null) {
         final int arg_excluded = arg_exclude.size();
         final int arg_restricted = arg_blocks.size();
         if(arg_action.size() == 0 && arg_blocks.size() > 0) {
            Iterator type = arg_blocks.iterator();

            while(type.hasNext()) {
               Object allPermission = type.next();
               if(allPermission instanceof Material) {
                  arg_action.add(Integer.valueOf(0));
                  arg_action.add(Integer.valueOf(1));
               } else if(allPermission instanceof EntityType) {
                  arg_action.add(Integer.valueOf(3));
               }
            }
         }

         if(arg_wid == -1) {
            String var66 = CommandHandler.parseWorldName(args);
            player.sendMessage("§3CoreProtect §f- World \"" + var66 + "\" not found.");
         } else {
            final int var60 = 0;
            if(Config.lookup_type.get(player.getName()) != null) {
               var60 = ((Integer)Config.lookup_type.get(player.getName())).intValue();
            }

            String bname;
            String var63;
            if(var60 == 0 && resultc > 1) {
               var60 = 4;
            } else if(resultc > 2) {
               var60 = 4;
            } else if(resultc > 1) {
               page_lookup = true;
               String var61 = args[1];
               if(var61.contains(":")) {
                  String[] bid = var61.split(":");
                  String b = bid[0].replaceAll("[^a-zA-Z_]", "");
                  bname = "";
                  if(bid.length > 1) {
                     bname = bid[1].replaceAll("[^a-zA-Z_]", "");
                  }

                  if(b.length() > 0 || bname.length() > 0) {
                     var60 = 4;
                     page_lookup = false;
                  }
               } else {
                  var63 = var61.replaceAll("[^a-zA-Z_]", "");
                  if(var63.length() > 0) {
                     var60 = 4;
                     page_lookup = false;
                  }
               }
            }

            if(arg_action.contains(Integer.valueOf(6)) || arg_action.contains(Integer.valueOf(7)) || arg_action.contains(Integer.valueOf(8)) || arg_action.contains(Integer.valueOf(9))) {
               page_lookup = true;
            }

            if(!permission && (!page_lookup || !player.hasPermission("coreprotect.inspect"))) {
               player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
            } else if(Config.converter_running) {
               player.sendMessage("§3CoreProtect §f- Upgrade in progress. Please try again later.");
            } else if(Config.purge_running) {
               player.sendMessage("§3CoreProtect §f- Purge in progress. Please try again later.");
            } else if(resultc < 2) {
               player.sendMessage("§3CoreProtect §f- Please use \"/co l <params>\".");
            } else if(arg_action.contains(Integer.valueOf(-1))) {
               player.sendMessage("§3CoreProtect §f- That is not a valid action.");
            } else if(worldedit && arg_radius == null) {
               player.sendMessage("§3CoreProtect §f- WorldEdit selection not found.");
            } else if(arg_radius != null && arg_radius[0].intValue() == -1) {
               player.sendMessage("§3CoreProtect §f- Please enter a valid radius.");
            } else {
               boolean var62 = false;
               if(player.isOp()) {
                  var62 = true;
               }

               if(!var62) {
                  if(!page_lookup && (arg_action.size() == 0 || arg_action.contains(Integer.valueOf(0)) || arg_action.contains(Integer.valueOf(1))) && !player.hasPermission("coreprotect.lookup.block")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }

                  if(arg_action.contains(Integer.valueOf(2)) && !player.hasPermission("coreprotect.lookup.click")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }

                  if(arg_action.contains(Integer.valueOf(3)) && !player.hasPermission("coreprotect.lookup.kill")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }

                  if(arg_action.contains(Integer.valueOf(4)) && !player.hasPermission("coreprotect.lookup.container")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }

                  if(arg_action.contains(Integer.valueOf(6)) && !player.hasPermission("coreprotect.lookup.chat")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }

                  if(arg_action.contains(Integer.valueOf(7)) && !player.hasPermission("coreprotect.lookup.command")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }

                  if(arg_action.contains(Integer.valueOf(8)) && !player.hasPermission("coreprotect.lookup.session")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }

                  if(arg_action.contains(Integer.valueOf(9)) && !player.hasPermission("coreprotect.lookup.username")) {
                     player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                     return;
                  }
               }

               if(arg_action.contains(Integer.valueOf(6)) || arg_action.contains(Integer.valueOf(7)) || arg_action.contains(Integer.valueOf(8)) || arg_action.contains(Integer.valueOf(9))) {
                  if(!arg_action.contains(Integer.valueOf(8)) && (arg_radius != null || arg_wid > 0 || worldedit)) {
                     player.sendMessage("§3CoreProtect §f- \"r:\" can\'t be used with that action.");
                     return;
                  }

                  if(arg_blocks.size() > 0) {
                     player.sendMessage("§3CoreProtect §f- \"b:\" can\'t be used with that action.");
                     return;
                  }

                  if(arg_exclude.size() > 0) {
                     player.sendMessage("§3CoreProtect §f- \"e:\" can\'t be used with that action.");
                     return;
                  }
               }

               if(resultc > 2) {
                  var63 = args[1];
                  if(var63.equalsIgnoreCase("type") || var63.equalsIgnoreCase("id")) {
                     var60 = 6;
                  }
               }

               if(rbseconds <= 0 && !page_lookup && var60 == 4 && (arg_blocks.size() > 0 || arg_users.size() > 0)) {
                  player.sendMessage("§3CoreProtect §f- Please specify the amount of time to lookup.");
               } else {
                  String g;
                  String[] max_radius;
                  String rollbackusers;
                  int c;
                  int cs;
                  final int x;
                  final int y;
                  final int z;
                  final int wid;
                  String rollbackusers2;
                  String unixtimestamp;
                  boolean var64;
                  final int var65;
                  final int var67;
                  int var68;
                  int var69;
                  if(var60 == 1) {
                     var64 = true;
                     var65 = 0;
                     var67 = 7;
                     if(resultc > 1) {
                        g = args[1];
                        if(g.contains(":")) {
                           max_radius = g.split(":");
                           g = max_radius[0];
                           rollbackusers = "";
                           if(max_radius.length > 1) {
                              rollbackusers = max_radius[1];
                           }

                           rollbackusers = rollbackusers.replaceAll("[^0-9]", "");
                           if(rollbackusers.length() > 0) {
                              c = Integer.parseInt(rollbackusers);
                              if(c > 0) {
                                 var67 = c;
                                 var64 = false;
                              }
                           }
                        }

                        g = g.replaceAll("[^0-9]", "");
                        if(g.length() > 0) {
                           var68 = Integer.parseInt(g);
                           if(var68 > 0) {
                              var65 = var68;
                           }
                        }
                     }

                     if(var65 <= 0) {
                        var65 = 1;
                     }

                     g = (String)Config.lookup_command.get(player.getName());
                     max_radius = g.split("\\.");
                     var69 = Integer.parseInt(max_radius[0]);
                     c = Integer.parseInt(max_radius[1]);
                     cs = Integer.parseInt(max_radius[2]);
                     x = Integer.parseInt(max_radius[3]);
                     y = Integer.parseInt(max_radius[4]);
                     z = Integer.parseInt(max_radius[5]);
                     wid = Integer.parseInt(max_radius[6]);
                     if(var64) {
                        var67 = Integer.parseInt(max_radius[7]);
                     }

                     rollbackusers2 = var69 + "." + c + "." + cs + "." + x + "." + y + "." + z + "." + wid + "." + var67;
                     Config.lookup_command.put(player.getName(), rollbackusers2);
                     unixtimestamp = Functions.getWorldName(x);
                     double stime = 0.5D * (double)(var69 + y);
                     double e = 0.5D * (double)(c + z);
                     double final_y = 0.5D * (double)(cs + wid);
                     final Location final_wid = new Location(CoreProtect.getInstance().getServer().getWorld(unixtimestamp), stime, e, final_y);
                     class BasicThread implements Runnable {

                        public void run() {
                           try {
                              Connection e = Database.getConnection(false);
                              if(e != null) {
                                 Statement statement = e.createStatement();
                                 String blockdata = Lookup.chest_transactions(statement, final_wid, player.getName(), var65, var67);
                                 if(blockdata.contains("\n")) {
                                    String[] var4 = blockdata.split("\n");
                                    int var5 = var4.length;

                                    for(int var6 = 0; var6 < var5; ++var6) {
                                       String b = var4[var6];
                                       player.sendMessage(b);
                                    }
                                 } else {
                                    player.sendMessage(blockdata);
                                 }

                                 statement.close();
                                 e.close();
                              } else {
                                 player.sendMessage("§3CoreProtect §f- Database busy. Please try again later.");
                              }
                           } catch (Exception var8) {
                              var8.printStackTrace();
                           }

                        }
                     }

                     BasicThread excluded = final_wid.new BasicThread();
                     Thread restricted = new Thread(excluded);
                     restricted.start();
                  } else if(var60 != 2 && var60 != 3 && var60 != 7) {
                     if(var60 != 4 && var60 != 5) {
                        if(var60 == 6) {
                           var63 = args[2];
                           var63 = var63.replaceAll("[^0-9]", "");
                           if(var63.length() > 0) {
                              var65 = Integer.parseInt(var63);
                              if(var65 > 0) {
                                 bname = Functions.block_name_lookup(var65);
                                 if(bname.length() > 0) {
                                    player.sendMessage("§3CoreProtect §f- The name of block ID #" + var65 + " is \"" + bname + "\".");
                                 } else {
                                    player.sendMessage("§3CoreProtect §f- No data found for block ID #" + var65 + ".");
                                 }
                              } else {
                                 player.sendMessage("§3CoreProtect §f- Please use \"/co lookup type <ID>\".");
                              }
                           } else {
                              player.sendMessage("§3CoreProtect §f- Please use \"/co lookup type <ID>\".");
                           }
                        } else {
                           player.sendMessage("§3CoreProtect §f- Please use \"/co l <params>\".");
                        }
                     } else {
                        var64 = true;
                        var65 = 1;
                        var67 = 4;
                        if(arg_action.contains(Integer.valueOf(6)) || arg_action.contains(Integer.valueOf(7)) || arg_action.contains(Integer.valueOf(9))) {
                           var67 = 7;
                        }

                        if(var60 == 5 && resultc > 1) {
                           g = args[1];
                           if(g.contains(":")) {
                              max_radius = g.split(":");
                              g = max_radius[0];
                              rollbackusers = "";
                              if(max_radius.length > 1) {
                                 rollbackusers = max_radius[1];
                              }

                              rollbackusers = rollbackusers.replaceAll("[^0-9]", "");
                              if(rollbackusers.length() > 0) {
                                 c = Integer.parseInt(rollbackusers);
                                 if(c > 0) {
                                    var67 = c;
                                    var64 = false;
                                 }
                              }
                           }

                           g = g.replaceAll("[^0-9]", "");
                           if(g.length() > 0) {
                              var68 = Integer.parseInt(g);
                              if(var68 > 0) {
                                 var65 = var68;
                              }
                           }
                        }

                        boolean var70 = true;
                        if(arg_users.contains("#global") && arg_radius == null) {
                           var70 = false;
                        }

                        if(var70 && (page_lookup || arg_blocks.size() > 0 || arg_users.size() > 0 || arg_users.size() == 0 && arg_radius != null)) {
                           var68 = ((Integer)Config.config.get("max-radius")).intValue();
                           if(arg_radius != null) {
                              var69 = arg_radius[0].intValue();
                              if(var69 > var68 && var68 > 0) {
                                 player.sendMessage("§3CoreProtect §f- The maximum lookup radius is " + var68 + ".");
                                 player.sendMessage("§3CoreProtect §f- Don\'t specify a radius to do a global lookup.");
                                 return;
                              }
                           }

                           if(arg_users.size() == 0) {
                              arg_users.add("#global");
                           }

                           List var74 = arg_users;
                           c = 0;

                           for(Iterator var71 = arg_users.iterator(); var71.hasNext(); ++c) {
                              String var72 = (String)var71.next();
                              List var73 = CoreProtect.getInstance().getServer().matchPlayer(var72);
                              Iterator var76 = var73.iterator();

                              while(var76.hasNext()) {
                                 Player var78 = (Player)var76.next();
                                 if(var78.getName().equalsIgnoreCase(var72)) {
                                    var74.set(c, var78.getName());
                                 }
                              }
                           }

                           cs = -1;
                           x = 0;
                           y = 0;
                           z = 0;
                           wid = 0;
                           int var82;
                           if(var60 == 5) {
                              rollbackusers2 = (String)Config.lookup_command.get(player.getName());
                              String[] var81 = rollbackusers2.split("\\.");
                              x = Integer.parseInt(var81[0]);
                              y = Integer.parseInt(var81[1]);
                              z = Integer.parseInt(var81[2]);
                              wid = Integer.parseInt(var81[3]);
                              cs = Integer.parseInt(var81[4]);
                              arg_noisy = Integer.parseInt(var81[5]);
                              arg_excluded = Integer.parseInt(var81[6]);
                              arg_restricted = Integer.parseInt(var81[7]);
                              arg_wid = Integer.parseInt(var81[8]);
                              if(var64) {
                                 var67 = Integer.parseInt(var81[9]);
                              }

                              var74 = (List)Config.lookup_ulist.get(player.getName());
                              arg_blocks = (List)Config.lookup_blist.get(player.getName());
                              arg_exclude = (List)Config.lookup_elist.get(player.getName());
                              arg_exclude_users = (List)Config.lookup_e_userlist.get(player.getName());
                              arg_action = (List)Config.lookup_alist.get(player.getName());
                              arg_radius = (Integer[])Config.lookup_radius.get(player.getName());
                              ts = (String)Config.lookup_time.get(player.getName());
                              rbseconds = 1;
                           } else {
                              if(lo != null) {
                                 x = lo.getBlockX();
                                 z = lo.getBlockZ();
                                 wid = Functions.getWorldId(lo.getWorld().getName());
                              }

                              if(var74.size() == 1 && var74.contains("#global") && arg_action.contains(Integer.valueOf(9))) {
                                 player.sendMessage("§3CoreProtect §f- Please use \"/co l a:username u:<user>\".");
                                 return;
                              }

                              if(var74.contains("#container")) {
                                 if(arg_action.contains(Integer.valueOf(6)) || arg_action.contains(Integer.valueOf(7)) || arg_action.contains(Integer.valueOf(8)) || arg_action.contains(Integer.valueOf(9))) {
                                    player.sendMessage("§3CoreProtect §f- \"#container\" is an invalid username.");
                                    return;
                                 }

                                 boolean var80 = false;
                                 if(Config.lookup_type.get(player.getName()) != null) {
                                    var82 = ((Integer)Config.lookup_type.get(player.getName())).intValue();
                                    if(var82 == 1) {
                                       var80 = true;
                                    } else if(var82 == 5 && ((List)Config.lookup_ulist.get(player.getName())).contains("#container")) {
                                       var80 = true;
                                    }
                                 }

                                 if(!var80) {
                                    player.sendMessage("§3CoreProtect §f- Please inspect a valid container first.");
                                    return;
                                 }

                                 if(!player.hasPermission("coreprotect.lookup.container") && !var62) {
                                    player.sendMessage("§3CoreProtect §f- You do not have permission to do that.");
                                    return;
                                 }

                                 unixtimestamp = (String)Config.lookup_command.get(player.getName());
                                 String[] var84 = unixtimestamp.split("\\.");
                                 x = Integer.parseInt(var84[0]);
                                 y = Integer.parseInt(var84[1]);
                                 z = Integer.parseInt(var84[2]);
                                 wid = Integer.parseInt(var84[3]);
                                 arg_action.add(Integer.valueOf(5));
                                 arg_radius = null;
                                 arg_wid = 0;
                              }
                           }

                           final List var83 = var74;
                           var82 = (int)(System.currentTimeMillis() / 1000L);
                           if(cs == -1) {
                              if(rbseconds <= 0) {
                                 cs = 0;
                              } else {
                                 cs = var82 - rbseconds;
                              }
                           }

                           final int var85 = cs;
                           final Integer[] radius = arg_radius;

                           try {
                              player.sendMessage("§3CoreProtect §f- Lookup searching. Please wait...");
                              class BasicThread2 implements Runnable {

                                 public void run() {
                                    try {
                                       ArrayList e = new ArrayList();
                                       Location location = lo;
                                       boolean exists = false;
                                       String bc = x + "." + y + "." + z + "." + wid + "." + var85 + "." + arg_noisy + "." + arg_excluded + "." + arg_restricted + "." + arg_wid + "." + var67;
                                       Config.lookup_command.put(player.getName(), bc);
                                       Config.lookup_page.put(player.getName(), Integer.valueOf(var65));
                                       Config.lookup_time.put(player.getName(), ts);
                                       Config.lookup_type.put(player.getName(), Integer.valueOf(5));
                                       Config.lookup_elist.put(player.getName(), arg_exclude);
                                       Config.lookup_e_userlist.put(player.getName(), arg_exclude_users);
                                       Config.lookup_blist.put(player.getName(), arg_blocks);
                                       Config.lookup_ulist.put(player.getName(), var83);
                                       Config.lookup_alist.put(player.getName(), arg_action);
                                       Config.lookup_radius.put(player.getName(), radius);
                                       Connection connection = Database.getConnection(false);
                                       if(connection != null) {
                                          Statement statement = connection.createStatement();
                                          String baduser = "";
                                          Iterator user_list = var83.iterator();

                                          String unixtimestamp;
                                          while(user_list.hasNext()) {
                                             unixtimestamp = (String)user_list.next();
                                             if((unixtimestamp.equals("#global") || unixtimestamp.equals("#container")) && !arg_action.contains(Integer.valueOf(9))) {
                                                exists = true;
                                             } else {
                                                exists = Lookup.playerExists(connection, unixtimestamp);
                                                if(!exists) {
                                                   baduser = unixtimestamp;
                                                   break;
                                                }

                                                if(arg_action.contains(Integer.valueOf(9)) && Config.uuid_cache.get(unixtimestamp.toLowerCase()) != null) {
                                                   String restrict_world = (String)Config.uuid_cache.get(unixtimestamp.toLowerCase());
                                                   e.add(restrict_world);
                                                }
                                             }
                                          }

                                          if(exists) {
                                             user_list = arg_exclude_users.iterator();

                                             while(user_list.hasNext()) {
                                                unixtimestamp = (String)user_list.next();
                                                if(!unixtimestamp.equals("#global")) {
                                                   exists = Lookup.playerExists(connection, unixtimestamp);
                                                   if(!exists) {
                                                      baduser = unixtimestamp;
                                                      break;
                                                   }
                                                } else {
                                                   baduser = "#global";
                                                   exists = false;
                                                }
                                             }
                                          }

                                          if(exists) {
                                             Object user_list1 = new ArrayList();
                                             if(!arg_action.contains(Integer.valueOf(9))) {
                                                user_list1 = var83;
                                             }

                                             int unixtimestamp1 = (int)(System.currentTimeMillis() / 1000L);
                                             boolean restrict_world1 = false;
                                             if(radius != null) {
                                                restrict_world1 = true;
                                             }

                                             if(location == null) {
                                                restrict_world1 = false;
                                             }

                                             if(arg_wid > 0) {
                                                restrict_world1 = true;
                                                location = new Location(CoreProtect.getInstance().getServer().getWorld(Functions.getWorldName(arg_wid)), (double)x, (double)y, (double)z);
                                             } else if(location != null) {
                                                location = new Location(CoreProtect.getInstance().getServer().getWorld(Functions.getWorldName(wid)), (double)x, (double)y, (double)z);
                                             }

                                             int row_max = var65 * var67;
                                             int page_start = row_max - var67;
                                             int rows = 0;
                                             boolean check_rows = true;
                                             if(var60 == 5 && var65 > 1) {
                                                rows = ((Integer)Config.lookup_rows.get(player.getName())).intValue();
                                                if(page_start < rows) {
                                                   check_rows = false;
                                                }
                                             }

                                             if(check_rows) {
                                                rows = Lookup.countLookupRows(statement, player, e, (List)user_list1, arg_blocks, arg_exclude, arg_exclude_users, arg_action, location, radius, var85, restrict_world1, true);
                                                Config.lookup_rows.put(player.getName(), Integer.valueOf(rows));
                                             }

                                             String arrows;
                                             if(count) {
                                                arrows = NumberFormat.getInstance().format((long)rows);
                                                player.sendMessage("§3CoreProtect §f- " + arrows + " row(s) found.");
                                             } else if(page_start < rows) {
                                                arrows = "                      ";
                                                if(rows > var67) {
                                                   int lookup_list = (int)Math.ceil((double)rows / ((double)var67 + 0.0D));
                                                   String total_pages = "«";
                                                   String data = "»";
                                                   if(var65 > 1 && var65 < lookup_list) {
                                                      total_pages + " | " + data;
                                                   } else if(var65 > 1) {
                                                      "    " + total_pages;
                                                   } else {
                                                      "    " + data;
                                                   }
                                                }

                                                arrows = "";
                                                List lookup_list1 = Lookup.performPartialLookup(statement, player, e, (List)user_list1, arg_blocks, arg_exclude, arg_exclude_users, arg_action, location, radius, var85, page_start, var67, restrict_world1, true);
                                                player.sendMessage("§f----- §3CoreProtect Lookup Results §f-----" + arrows);
                                                String string_amount;
                                                String drb;
                                                String rbd;
                                                double amount;
                                                String dplayer;
                                                Iterator total_pages1;
                                                String[] data1;
                                                if(!arg_action.contains(Integer.valueOf(6)) && !arg_action.contains(Integer.valueOf(7))) {
                                                   int xx;
                                                   String dtype;
                                                   int amount1;
                                                   if(arg_action.contains(Integer.valueOf(8))) {
                                                      total_pages1 = lookup_list1.iterator();

                                                      while(total_pages1.hasNext()) {
                                                         data1 = (String[])total_pages1.next();
                                                         string_amount = data1[0];
                                                         drb = data1[1];
                                                         int rbd1 = Integer.parseInt(data1[2]);
                                                         amount1 = Integer.parseInt(data1[3]);
                                                         int time = Integer.parseInt(data1[4]);
                                                         int dplayer1 = Integer.parseInt(data1[5]);
                                                         xx = Integer.parseInt(data1[6]);
                                                         double yx = (double)unixtimestamp1 - Double.parseDouble(string_amount);
                                                         yx /= 60.0D;
                                                         yx /= 60.0D;
                                                         dtype = (new DecimalFormat("0.00")).format(yx);
                                                         String ddata = "in";
                                                         if(xx == 0) {
                                                            ddata = "out";
                                                         }

                                                         String daction = Functions.getWorldName(rbd1);
                                                         double widx = (double)dtype.replaceAll("[^0-9]", "").length() * 1.5D;
                                                         int tag = (int)(widx + 12.5D);
                                                         String time_since = StringUtils.leftPad("", tag, ' ');
                                                         player.sendMessage("§7" + dtype + "/h ago §f- §3" + drb + " §flogged §3" + ddata + "§f.");
                                                         player.sendMessage("§f" + time_since + "§7^ §o(x" + amount1 + "/y" + time + "/z" + dplayer1 + "/" + daction + ")");
                                                      }
                                                   } else if(arg_action.contains(Integer.valueOf(9))) {
                                                      total_pages1 = lookup_list1.iterator();

                                                      while(total_pages1.hasNext()) {
                                                         data1 = (String[])total_pages1.next();
                                                         string_amount = data1[0];
                                                         drb = (String)Config.uuid_cache_reversed.get(data1[1]);
                                                         rbd = data1[2];
                                                         amount = (double)unixtimestamp1 - Double.parseDouble(string_amount);
                                                         amount /= 60.0D;
                                                         amount /= 60.0D;
                                                         dplayer = (new DecimalFormat("0.00")).format(amount);
                                                         player.sendMessage("§7" + dplayer + "/h ago §f- §3" + drb + " §flogged in as §3" + rbd + "§f.");
                                                      }
                                                   } else {
                                                      total_pages1 = lookup_list1.iterator();

                                                      while(total_pages1.hasNext()) {
                                                         data1 = (String[])total_pages1.next();
                                                         string_amount = "";
                                                         int drb1 = Integer.parseInt(data1[8]);
                                                         rbd = "";
                                                         if(drb1 == 1) {
                                                            rbd = "§m";
                                                         }

                                                         boolean amount2 = false;
                                                         String time1 = data1[0];
                                                         dplayer = data1[1];
                                                         xx = Integer.parseInt(data1[2]);
                                                         int y1 = Integer.parseInt(data1[3]);
                                                         int zx = Integer.parseInt(data1[4]);
                                                         dtype = data1[5];
                                                         int ddata1 = Integer.parseInt(data1[6]);
                                                         int daction1 = Integer.parseInt(data1[7]);
                                                         int wid1 = Integer.parseInt(data1[9]);
                                                         String a = "placed";
                                                         String tag1 = "§f-";
                                                         if(arg_action.contains(Integer.valueOf(4)) || arg_action.contains(Integer.valueOf(5))) {
                                                            amount1 = Integer.parseInt(data1[10]);
                                                            string_amount = "x" + amount1 + " ";
                                                            a = "added";
                                                         }

                                                         if(daction1 == 0) {
                                                            a = "removed";
                                                         } else if(daction1 == 2) {
                                                            a = "clicked";
                                                         } else if(daction1 == 3) {
                                                            a = "killed";
                                                         }

                                                         double time_since1 = (double)unixtimestamp1 - Double.parseDouble(time1);
                                                         time_since1 /= 60.0D;
                                                         time_since1 /= 60.0D;
                                                         String timeago = (new DecimalFormat("0.00")).format(time_since1);
                                                         double time_length = (double)timeago.replaceAll("[^0-9]", "").length() * 1.5D;
                                                         int padding = (int)(time_length + 12.5D);
                                                         String left_padding = StringUtils.leftPad("", padding, ' ');
                                                         String world = Functions.getWorldName(wid1);
                                                         String dname = "";
                                                         boolean isPlayer = false;
                                                         if(daction1 == 3) {
                                                            int block_name_split = Integer.parseInt(dtype);
                                                            if(block_name_split == 0) {
                                                               if(Config.player_id_cache_reversed.get(Integer.valueOf(ddata1)) == null) {
                                                                  Database.loadUserName(connection, ddata1);
                                                               }

                                                               dname = (String)Config.player_id_cache_reversed.get(Integer.valueOf(ddata1));
                                                               isPlayer = true;
                                                            } else {
                                                               dname = Functions.getEntityType(block_name_split).name();
                                                            }
                                                         } else {
                                                            dname = Functions.getType(Integer.parseInt(dtype)).name().toLowerCase();
                                                            dname = Functions.nameFilter(dname, ddata1);
                                                         }

                                                         if(dname.length() > 0 && !isPlayer) {
                                                            dname = "minecraft:" + dname.toLowerCase() + "";
                                                         }

                                                         if(dname.contains("minecraft:")) {
                                                            String[] block_name_split1 = dname.split(":");
                                                            dname = block_name_split1[1];
                                                         }

                                                         player.sendMessage("§7" + timeago + "/h ago " + tag1 + " §3" + rbd + "" + dplayer + " §f" + rbd + "" + a + " " + string_amount + "§3" + rbd + "" + dname + "§f.");
                                                         player.sendMessage("§f" + left_padding + "§7^ §o(x" + xx + "/y" + y1 + "/z" + zx + "/" + world + ")");
                                                      }
                                                   }
                                                } else {
                                                   total_pages1 = lookup_list1.iterator();

                                                   while(total_pages1.hasNext()) {
                                                      data1 = (String[])total_pages1.next();
                                                      string_amount = data1[0];
                                                      drb = data1[1];
                                                      rbd = data1[2];
                                                      amount = (double)unixtimestamp1 - Double.parseDouble(string_amount);
                                                      amount /= 60.0D;
                                                      amount /= 60.0D;
                                                      dplayer = (new DecimalFormat("0.00")).format(amount);
                                                      player.sendMessage("§7" + dplayer + "/h ago §f- §3" + drb + ": §f" + rbd + "");
                                                   }
                                                }

                                                if(rows > var67) {
                                                   int total_pages2 = (int)Math.ceil((double)rows / ((double)var67 + 0.0D));
                                                   if(arg_action.contains(Integer.valueOf(6)) || arg_action.contains(Integer.valueOf(7)) || arg_action.contains(Integer.valueOf(9))) {
                                                      player.sendMessage("-----");
                                                   }

                                                   player.sendMessage("§fPage " + var65 + "/" + total_pages2 + ". View older data by typing \"§3/co l <page>§f\".");
                                                }
                                             } else if(rows > 0) {
                                                player.sendMessage("§3CoreProtect §f- No results found for that page.");
                                             } else {
                                                player.sendMessage("§3CoreProtect §f- No results found.");
                                             }
                                          } else {
                                             player.sendMessage("§3CoreProtect §f- User \"" + baduser + "\" not found.");
                                          }

                                          statement.close();
                                          connection.close();
                                       } else {
                                          player.sendMessage("§3CoreProtect §f- Database busy. Please try again later.");
                                       }
                                    } catch (Exception var45) {
                                       var45.printStackTrace();
                                    }

                                 }
                              }

                              BasicThread2 runnable = lo.new BasicThread2();
                              Thread thread = new Thread(runnable);
                              thread.start();
                           } catch (Exception var59) {
                              var59.printStackTrace();
                           }
                        } else {
                           player.sendMessage("§3CoreProtect §f- Please use \"/co l <params>\".");
                        }
                     }
                  } else {
                     var64 = true;
                     var65 = 1;
                     var67 = 7;
                     if(resultc > 1) {
                        g = args[1];
                        if(g.contains(":")) {
                           max_radius = g.split(":");
                           g = max_radius[0];
                           rollbackusers = "";
                           if(max_radius.length > 1) {
                              rollbackusers = max_radius[1];
                           }

                           rollbackusers = rollbackusers.replaceAll("[^0-9]", "");
                           if(rollbackusers.length() > 0) {
                              c = Integer.parseInt(rollbackusers);
                              if(c > 0) {
                                 var67 = c;
                                 var64 = false;
                              }
                           }
                        }

                        g = g.replaceAll("[^0-9]", "");
                        if(g.length() > 0) {
                           var68 = Integer.parseInt(g);
                           if(var68 > 0) {
                              var65 = var68;
                           }
                        }
                     }

                     g = (String)Config.lookup_command.get(player.getName());
                     max_radius = g.split("\\.");
                     var69 = Integer.parseInt(max_radius[0]);
                     c = Integer.parseInt(max_radius[1]);
                     cs = Integer.parseInt(max_radius[2]);
                     x = Integer.parseInt(max_radius[3]);
                     y = Integer.parseInt(max_radius[4]);
                     if(var64) {
                        var67 = Integer.parseInt(max_radius[5]);
                     }

                     String var75 = var69 + "." + c + "." + cs + "." + x + "." + y + "." + var67;
                     Config.lookup_command.put(player.getName(), var75);
                     String var77 = Functions.getWorldName(x);
                     final Block var79 = CoreProtect.getInstance().getServer().getWorld(var77).getBlockAt(var69, c, cs);
                     class BasicThread implements Runnable {

                        public void run() {
                           try {
                              Connection e = Database.getConnection(false);
                              if(e != null) {
                                 Statement statement = e.createStatement();
                                 String blockdata = null;
                                 if(var60 == 7) {
                                    blockdata = Lookup.interaction_lookup(statement, var79, player.getName(), 0, var65, var67);
                                 } else {
                                    blockdata = Lookup.block_lookup(statement, var79, player.getName(), 0, var65, var67);
                                 }

                                 if(blockdata.contains("\n")) {
                                    String[] var4 = blockdata.split("\n");
                                    int var5 = var4.length;

                                    for(int var6 = 0; var6 < var5; ++var6) {
                                       String b = var4[var6];
                                       player.sendMessage(b);
                                    }
                                 } else if(blockdata.length() > 0) {
                                    player.sendMessage(blockdata);
                                 }

                                 statement.close();
                                 e.close();
                              } else {
                                 player.sendMessage("§3CoreProtect §f- Database busy. Please try again later.");
                              }
                           } catch (Exception var8) {
                              var8.printStackTrace();
                           }

                        }
                     }

                     BasicThread final_x = var60.new BasicThread();
                     Thread var86 = new Thread(final_x);
                     var86.start();
                  }

               }
            }
         }
      }
   }
}
