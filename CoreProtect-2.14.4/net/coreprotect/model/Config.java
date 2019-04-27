package net.coreprotect.model;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.coreprotect.CoreProtect;
import net.coreprotect.Functions;
import net.coreprotect.consumer.Queue;
import net.coreprotect.database.Database;
import net.coreprotect.model.BlockInfo;
import net.coreprotect.patch.Patch;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Config extends Queue {

   public static String driver = "com.mysql.jdbc.Driver";
   public static String sqlite = "plugins/CoreProtect/database.db";
   public static String host = "127.0.0.1";
   public static int port = 3306;
   public static String database = "database";
   public static String username = "root";
   public static String password = "";
   public static String prefix = "co_";
   public static boolean server_running = false;
   public static boolean converter_running = false;
   public static boolean purge_running = false;
   public static int world_id = 0;
   public static int material_id = 0;
   public static int entity_id = 0;
   public static int art_id = 0;
   public static Map worlds = Collections.synchronizedMap(new HashMap());
   public static Map worlds_reversed = Collections.synchronizedMap(new HashMap());
   public static Map materials = Collections.synchronizedMap(new HashMap());
   public static Map materials_reversed = Collections.synchronizedMap(new HashMap());
   public static Map entities = Collections.synchronizedMap(new HashMap());
   public static Map entities_reversed = Collections.synchronizedMap(new HashMap());
   public static Map art = Collections.synchronizedMap(new HashMap());
   public static Map art_reversed = Collections.synchronizedMap(new HashMap());
   public static Map config = Collections.synchronizedMap(new HashMap());
   public static Map rollback_hash = Collections.synchronizedMap(new HashMap());
   public static Map inspecting = Collections.synchronizedMap(new HashMap());
   public static Map lookup_cache = Collections.synchronizedMap(new HashMap());
   public static Map break_cache = Collections.synchronizedMap(new HashMap());
   public static Map piston_cache = Collections.synchronizedMap(new HashMap());
   public static Map entity_cache = Collections.synchronizedMap(new HashMap());
   public static Map blacklist = Collections.synchronizedMap(new HashMap());
   public static Map logging_chest = Collections.synchronizedMap(new HashMap());
   public static Map old_container = Collections.synchronizedMap(new HashMap());
   public static Map force_containers = Collections.synchronizedMap(new HashMap());
   public static Map lookup_type = Collections.synchronizedMap(new HashMap());
   public static Map lookup_page = Collections.synchronizedMap(new HashMap());
   public static Map lookup_command = Collections.synchronizedMap(new HashMap());
   public static Map lookup_blist = Collections.synchronizedMap(new HashMap());
   public static Map lookup_elist = Collections.synchronizedMap(new HashMap());
   public static Map lookup_e_userlist = Collections.synchronizedMap(new HashMap());
   public static Map lookup_ulist = Collections.synchronizedMap(new HashMap());
   public static Map lookup_alist = Collections.synchronizedMap(new HashMap());
   public static Map lookup_radius = Collections.synchronizedMap(new HashMap());
   public static Map lookup_time = Collections.synchronizedMap(new HashMap());
   public static Map lookup_rows = Collections.synchronizedMap(new HashMap());
   public static Map uuid_cache = Collections.synchronizedMap(new HashMap());
   public static Map uuid_cache_reversed = Collections.synchronizedMap(new HashMap());
   public static Map player_id_cache = Collections.synchronizedMap(new HashMap());
   public static Map player_id_cache_reversed = Collections.synchronizedMap(new HashMap());
   public static Map last_rollback = Collections.synchronizedMap(new HashMap());
   public static Map active_rollbacks = Collections.synchronizedMap(new HashMap());
   public static Map entity_block_mapper = Collections.synchronizedMap(new HashMap());
   public static ConcurrentHashMap language = new ConcurrentHashMap();
   public static List databaseTables = new ArrayList();


   private static void checkPlayers(Connection connection) {
      player_id_cache.clear();
      Iterator var1 = CoreProtect.getInstance().getServer().getOnlinePlayers().iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();
         if(player_id_cache.get(player.getName().toLowerCase()) == null) {
            Database.loadUserID(connection, player.getName(), player.getUniqueId().toString());
         }
      }

   }

   private static void loadBlacklist() {
      try {
         blacklist.clear();
         String e = "plugins/CoreProtect/blacklist.txt";
         boolean exists = (new File(e)).exists();
         if(exists) {
            RandomAccessFile blfile = new RandomAccessFile(e, "rw");
            long blc = blfile.length();
            if(blc > 0L) {
               while(blfile.getFilePointer() < blfile.length()) {
                  String blacklist_user = blfile.readLine().replaceAll(" ", "").toLowerCase();
                  if(blacklist_user.length() > 0) {
                     blacklist.put(blacklist_user, Boolean.valueOf(true));
                  }
               }
            }

            blfile.close();
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   private static void loadConfig() {
      try {
         String e = "#CoreProtect Config\n";
         String noisy = "\n# If enabled, extra data is displayed when doing rollbacks and restores.\n# If disabled, you can manually trigger it in-game by adding \"#verbose\"\n# to the end of your rollback statement.\nverbose: true\n";
         String mysql = "\n# MySQL is optional and not required.\n# If you prefer to use MySQL, enable the following and fill out the fields.\nuse-mysql: false\ntable-prefix: co_\nmysql-host: 127.0.0.1\nmysql-port: 3306\nmysql-database: database\nmysql-username: root\nmysql-password: \n";
         String update = "\n# If enabled, CoreProtect will check for updates when your server starts up.\n# If an update is available, you\'ll be notified via your server console.\ncheck-updates: true\n";
         String api = "\n# If enabled, other plugins will be able to utilize the CoreProtect API.\napi-enabled: true\n";
         String defaultradius = "\n# If no radius is specified in a rollback or restore, this value will be\n# used as the radius. Set to \"0\" to disable automatically adding a radius.\ndefault-radius: 10\n";
         String maxradius = "\n# The maximum radius that can be used in a command. Set to \"0\" to disable.\n# To run a rollback or restore without a radius, you can use \"r:#global\".\nmax-radius: 100\n";
         String rollbackitems = "\n# If enabled, items taken from containers (etc) will be included in rollbacks.\nrollback-items: true\n";
         String rollbackentities = "\n# If enabled, entities, such as killed animals, will be included in rollbacks.\nrollback-entities: true\n";
         String skipgenericdata = "\n# If enabled, generic data, like zombies burning in daylight, won\'t be logged.\nskip-generic-data: true\n";
         String blockplace = "\n# Logs blocks placed by players.\nblock-place: true\n";
         String blockbreak = "\n# Logs blocks broken by players.\nblock-break: true\n";
         String naturalbreak = "\n# Logs blocks that break off of other blocks; for example, a sign or torch\n# falling off of a dirt block that a player breaks. This is required for\n# beds/doors to properly rollback.\nnatural-break: true\n";
         String blockmovement = "\n# Properly track block movement, such as sand or gravel falling.\nblock-movement: true\n";
         String pistons = "\n# Properly track blocks moved by pistons.\npistons: true\n";
         String blockburn = "\n# Logs blocks that burn up in a fire.\nblock-burn: true\n";
         String blockignite = "\n# Logs when a block naturally ignites, such as from fire spreading.\nblock-ignite: true\n";
         String explosions = "\n# Logs explosions, such as TNT and Creepers.\nexplosions: true\n";
         String entitychange = "\n# Track when an entity changes a block, such as an Enderman destroying blocks.\nentity-change: true\n";
         String entitykills = "\n# Logs killed entities, such as killed cows and enderman.\nentity-kills: true\n";
         String signtext = "\n# Logs text on signs. If disabled, signs will be blank when rolled back.\nsign-text: true\n";
         String buckets = "\n# Logs lava and water sources placed/removed by players who are using buckets.\nbuckets: true\n";
         String leafdecay = "\n# Logs natural tree leaf decay.\nleaf-decay: true\n";
         String treegrowth = "\n# Logs tree growth. Trees are linked to the player who planted the sappling.\ntree-growth: true\n";
         String mushroomgrowth = "\n# Logs mushroom growth.\nmushroom-growth: true\n";
         String vinegrowth = "\n# Logs natural vine growth.\nvine-growth: true\n";
         String portals = "\n# Logs when portals such as Nether portals generate naturally.\nportals: true\n";
         String waterflow = "\n# Logs water flow. If water destroys other blocks, such as torches,\n# this allows it to be properly rolled back.\nwater-flow: true\n";
         String lavaflow = "\n# Logs lava flow. If lava destroys other blocks, such as torches,\n# this allows it to be properly rolled back.\nlava-flow: true\n";
         String liquidtracking = "\n# Allows liquid to be properly tracked and linked to players.\n# For example, if a player places water which flows and destroys torches,\n# it can all be properly restored by rolling back that single player.\nliquid-tracking: true\n";
         String itemlogging = "\n# Track item transactions, such as when a player takes items from a\n# chest, furnace, or dispenser. Necessary for any item based rollbacks.\nitem-transactions: true\n";
         String playerinteract = "\n# Track player interactions, such as when a player opens a door, presses\n# a button, or opens a chest. Player interactions can\'t be rolled back.\nplayer-interactions: true\n";
         String playermessages = "\n# Logs messages that players send in the chat.\nplayer-messages: true\n";
         String playercommands = "\n# Logs all commands used by players.\nplayer-commands: true\n";
         String playersessions = "\n# Logs the logins and logouts of players.\nplayer-sessions: true\n";
         String usernamechanges = "\n# Logs when a player changes their Minecraft username.\nusername-changes: true\n";
         String worldedit = "\n# Logs changes made via the plugin \"WorldEdit\" if it\'s in use on your server.\nworldedit: true\n";
         config.clear();
         File config_file = new File("plugins/CoreProtect/config.yml");
         boolean exists = config_file.exists();
         if(!exists) {
            config_file.createNewFile();
         }

         File dir = new File("plugins/CoreProtect");
         String[] children = dir.list();
         if(children != null) {
            String[] var41 = children;
            int var42 = children.length;

            for(int var43 = 0; var43 < var42; ++var43) {
               String element = var41[var43];
               String filename = element;
               if(!element.startsWith(".") && element.endsWith(".yml")) {
                  try {
                     String e1 = filename.replaceAll(".yml", "-");
                     if(e1.equals("config-")) {
                        e1 = "";
                     }

                     RandomAccessFile configfile = new RandomAccessFile("plugins/CoreProtect/" + filename, "rw");
                     long config_length = configfile.length();
                     if(config_length > 0L) {
                        while(configfile.getFilePointer() < configfile.length()) {
                           String line = configfile.readLine();
                           if(line.contains(":") && !line.startsWith("#")) {
                              line = line.replaceFirst(":", "§ ");
                              String[] i2 = line.split("§");
                              String option = i2[0].trim().toLowerCase();
                              String setting;
                              if(e1.length() == 0) {
                                 if(option.equals("verbose")) {
                                    setting = i2[1].trim().toLowerCase();
                                    if(setting.startsWith("t")) {
                                       config.put(e1 + "verbose", Integer.valueOf(1));
                                    } else if(setting.startsWith("f")) {
                                       config.put("verbose", Integer.valueOf(0));
                                    }
                                 }

                                 if(option.equals("use-mysql")) {
                                    setting = i2[1].trim().toLowerCase();
                                    if(setting.startsWith("t")) {
                                       config.put("use-mysql", Integer.valueOf(1));
                                    } else if(setting.startsWith("f")) {
                                       config.put("use-mysql", Integer.valueOf(0));
                                    }
                                 }

                                 if(option.equals("table-prefix")) {
                                    prefix = i2[1].trim();
                                 }

                                 if(option.equals("mysql-host")) {
                                    host = i2[1].trim();
                                 }

                                 if(option.equals("mysql-port")) {
                                    setting = i2[1].trim();
                                    setting = setting.replaceAll("[^0-9]", "");
                                    if(setting.length() == 0) {
                                       setting = "0";
                                    }

                                    port = Integer.parseInt(setting);
                                 }

                                 if(option.equals("mysql-database")) {
                                    database = i2[1].trim();
                                 }

                                 if(option.equals("mysql-username")) {
                                    username = i2[1].trim();
                                 }

                                 if(option.equals("mysql-password")) {
                                    password = i2[1].trim();
                                 }

                                 if(option.equals("check-updates")) {
                                    setting = i2[1].trim().toLowerCase();
                                    if(setting.startsWith("t")) {
                                       config.put("check-updates", Integer.valueOf(1));
                                    } else if(setting.startsWith("f")) {
                                       config.put("check-updates", Integer.valueOf(0));
                                    }
                                 }

                                 if(option.equals("api-enabled")) {
                                    setting = i2[1].trim().toLowerCase();
                                    if(setting.startsWith("t")) {
                                       config.put("api-enabled", Integer.valueOf(1));
                                    } else if(setting.startsWith("f")) {
                                       config.put("api-enabled", Integer.valueOf(0));
                                    }
                                 }

                                 if(option.equals("default-radius")) {
                                    setting = i2[1].trim();
                                    setting = setting.replaceAll("[^0-9]", "");
                                    if(setting.length() == 0) {
                                       setting = "0";
                                    }

                                    config.put("default-radius", Integer.valueOf(Integer.parseInt(setting)));
                                 }

                                 if(option.equals("max-radius")) {
                                    setting = i2[1].trim();
                                    setting = setting.replaceAll("[^0-9]", "");
                                    if(setting.length() == 0) {
                                       setting = "0";
                                    }

                                    config.put("max-radius", Integer.valueOf(Integer.parseInt(setting)));
                                 }

                                 if(option.equals("rollback-items")) {
                                    setting = i2[1].trim().toLowerCase();
                                    if(setting.startsWith("t")) {
                                       config.put("rollback-items", Integer.valueOf(1));
                                    } else if(setting.startsWith("f")) {
                                       config.put("rollback-items", Integer.valueOf(0));
                                    }
                                 }

                                 if(option.equals("rollback-entities")) {
                                    setting = i2[1].trim().toLowerCase();
                                    if(setting.startsWith("t")) {
                                       config.put("rollback-entities", Integer.valueOf(1));
                                    } else if(setting.startsWith("f")) {
                                       config.put("rollback-entities", Integer.valueOf(0));
                                    }
                                 }
                              }

                              if(option.equals("skip-generic-data")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "skip-generic-data", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "skip-generic-data", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("block-place")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "block-place", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "block-place", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("block-break")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "block-break", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "block-break", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("natural-break")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "natural-break", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "natural-break", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("block-movement")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "block-movement", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "block-movement", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("pistons")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "pistons", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "pistons", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("block-burn")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "block-burn", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "block-burn", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("block-ignite")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "block-ignite", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "block-ignite", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("explosions")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "explosions", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "explosions", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("entity-change")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "entity-change", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "entity-change", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("entity-kills")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "entity-kills", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "entity-kills", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("sign-text")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "sign-text", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "sign-text", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("buckets")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "buckets", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "buckets", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("leaf-decay")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "leaf-decay", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "leaf-decay", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("tree-growth")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "tree-growth", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "tree-growth", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("mushroom-growth")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "mushroom-growth", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "mushroom-growth", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("vine-growth")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "vine-growth", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "vine-growth", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("portals")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "portals", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "portals", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("water-flow")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "water-flow", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "water-flow", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("lava-flow")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "lava-flow", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "lava-flow", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("liquid-tracking")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "liquid-tracking", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "liquid-tracking", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("item-transactions")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "item-transactions", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "item-transactions", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("player-interactions")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "player-interactions", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "player-interactions", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("player-messages")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "player-messages", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "player-messages", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("player-commands")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "player-commands", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "player-commands", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("player-sessions")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "player-sessions", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "player-sessions", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("username-changes")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "username-changes", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "username-changes", Integer.valueOf(0));
                                 }
                              }

                              if(option.equals("worldedit")) {
                                 setting = i2[1].trim().toLowerCase();
                                 if(setting.startsWith("t")) {
                                    config.put(e1 + "worldedit", Integer.valueOf(1));
                                 } else if(setting.startsWith("f")) {
                                    config.put(e1 + "worldedit", Integer.valueOf(0));
                                 }
                              }
                           }
                        }
                     }

                     if(e1.length() == 0) {
                        if(config_length < 1L) {
                           configfile.write(e.getBytes());
                        }

                        if(config.get("verbose") == null) {
                           config.put("verbose", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(noisy.getBytes());
                        }

                        if(config.get("use-mysql") == null) {
                           config.put("use-mysql", Integer.valueOf(0));
                           configfile.seek(configfile.length());
                           configfile.write(mysql.getBytes());
                        }

                        if(config.get("check-updates") == null) {
                           config.put("check-updates", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(update.getBytes());
                        }

                        if(config.get("api-enabled") == null) {
                           config.put("api-enabled", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(api.getBytes());
                        }

                        if(config.get("default-radius") == null) {
                           config.put("default-radius", Integer.valueOf(10));
                           configfile.seek(configfile.length());
                           configfile.write(defaultradius.getBytes());
                        }

                        if(config.get("max-radius") == null) {
                           config.put("max-radius", Integer.valueOf(100));
                           configfile.seek(configfile.length());
                           configfile.write(maxradius.getBytes());
                        }

                        if(config.get("rollback-items") == null) {
                           config.put("rollback-items", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(rollbackitems.getBytes());
                        }

                        if(config.get("rollback-entities") == null) {
                           config.put("rollback-entities", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(rollbackentities.getBytes());
                        }

                        if(config.get("skip-generic-data") == null) {
                           config.put("skip-generic-data", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(skipgenericdata.getBytes());
                        }

                        if(config.get("block-place") == null) {
                           config.put("block-place", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(blockplace.getBytes());
                        }

                        if(config.get("block-break") == null) {
                           config.put("block-break", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(blockbreak.getBytes());
                        }

                        if(config.get("natural-break") == null) {
                           config.put("natural-break", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(naturalbreak.getBytes());
                        }

                        if(config.get("block-movement") == null) {
                           config.put("block-movement", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(blockmovement.getBytes());
                        }

                        if(config.get("pistons") == null) {
                           config.put("pistons", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(pistons.getBytes());
                        }

                        if(config.get("block-burn") == null) {
                           config.put("block-burn", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(blockburn.getBytes());
                        }

                        if(config.get("block-ignite") == null) {
                           config.put("block-ignite", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(blockignite.getBytes());
                        }

                        if(config.get("explosions") == null) {
                           config.put("explosions", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(explosions.getBytes());
                        }

                        if(config.get("entity-change") == null) {
                           config.put("entity-change", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(entitychange.getBytes());
                        }

                        if(config.get("entity-kills") == null) {
                           config.put("entity-kills", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(entitykills.getBytes());
                        }

                        if(config.get("sign-text") == null) {
                           config.put("sign-text", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(signtext.getBytes());
                        }

                        if(config.get("buckets") == null) {
                           config.put("buckets", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(buckets.getBytes());
                        }

                        if(config.get("leaf-decay") == null) {
                           config.put("leaf-decay", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(leafdecay.getBytes());
                        }

                        if(config.get("tree-growth") == null) {
                           config.put("tree-growth", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(treegrowth.getBytes());
                        }

                        if(config.get("mushroom-growth") == null) {
                           config.put("mushroom-growth", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(mushroomgrowth.getBytes());
                        }

                        if(config.get("vine-growth") == null) {
                           config.put("vine-growth", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(vinegrowth.getBytes());
                        }

                        if(config.get("portals") == null) {
                           config.put("portals", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(portals.getBytes());
                        }

                        if(config.get("water-flow") == null) {
                           config.put("water-flow", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(waterflow.getBytes());
                        }

                        if(config.get("lava-flow") == null) {
                           config.put("lava-flow", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(lavaflow.getBytes());
                        }

                        if(config.get("liquid-tracking") == null) {
                           config.put("liquid-tracking", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(liquidtracking.getBytes());
                        }

                        if(config.get("item-transactions") == null) {
                           config.put("item-transactions", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(itemlogging.getBytes());
                        }

                        if(config.get("player-interactions") == null) {
                           config.put("player-interactions", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(playerinteract.getBytes());
                        }

                        if(config.get("player-messages") == null) {
                           config.put("player-messages", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(playermessages.getBytes());
                        }

                        if(config.get("player-commands") == null) {
                           config.put("player-commands", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(playercommands.getBytes());
                        }

                        if(config.get("player-sessions") == null) {
                           config.put("player-sessions", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(playersessions.getBytes());
                        }

                        if(config.get("username-changes") == null) {
                           config.put("username-changes", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(usernamechanges.getBytes());
                        }

                        if(config.get("worldedit") == null) {
                           config.put("worldedit", Integer.valueOf(1));
                           configfile.seek(configfile.length());
                           configfile.write(worldedit.getBytes());
                        }
                     }

                     configfile.close();
                  } catch (Exception var54) {
                     var54.printStackTrace();
                  }
               }
            }
         }

         if(((Integer)config.get("use-mysql")).intValue() == 0) {
            prefix = "co_";
         }

         loadBlacklist();
      } catch (Exception var55) {
         var55.printStackTrace();
      }

   }

   public static void loadDatabase() {
      if(((Integer)config.get("use-mysql")).intValue() == 0) {
         try {
            File e = File.createTempFile("CoreProtect_" + System.currentTimeMillis(), ".tmp");
            e.setExecutable(true);
            if(!e.canExecute()) {
               File tempFolder = new File("cache");
               boolean exists = tempFolder.exists();
               if(!exists) {
                  tempFolder.mkdir();
               }

               System.setProperty("java.io.tmpdir", "cache");
            }

            e.delete();
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      Functions.createDatabaseTables(prefix, false);
   }

   private static void loadTypes(Statement statement) {
      try {
         materials.clear();
         materials_reversed.clear();
         material_id = 0;
         String e = "SELECT id,material FROM " + prefix + "material_map";
         ResultSet rs = statement.executeQuery(e);

         int id;
         String entity;
         while(rs.next()) {
            id = rs.getInt("id");
            entity = rs.getString("material");
            materials.put(entity, Integer.valueOf(id));
            materials_reversed.put(Integer.valueOf(id), entity);
            if(id > material_id) {
               material_id = id;
            }
         }

         rs.close();
         art.clear();
         art_reversed.clear();
         art_id = 0;
         e = "SELECT id,art FROM " + prefix + "art_map";
         rs = statement.executeQuery(e);

         while(rs.next()) {
            id = rs.getInt("id");
            entity = rs.getString("art");
            art.put(entity, Integer.valueOf(id));
            art_reversed.put(Integer.valueOf(id), entity);
            if(id > art_id) {
               art_id = id;
            }
         }

         rs.close();
         entities.clear();
         entities_reversed.clear();
         entity_id = 0;
         e = "SELECT id,entity FROM " + prefix + "entity_map";
         rs = statement.executeQuery(e);

         while(rs.next()) {
            id = rs.getInt("id");
            entity = rs.getString("entity");
            entities.put(entity, Integer.valueOf(id));
            entities_reversed.put(Integer.valueOf(id), entity);
            if(id > entity_id) {
               entity_id = id;
            }
         }

         rs.close();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      BlockInfo.loadData();
   }

   private static void loadWorlds(Statement statement) {
      try {
         worlds.clear();
         worlds_reversed.clear();
         world_id = 0;
         String e = "SELECT id,world FROM " + prefix + "world";
         ResultSet rs = statement.executeQuery(e);

         while(rs.next()) {
            int worlds = rs.getInt("id");
            String world = rs.getString("world");
            worlds.put(world, Integer.valueOf(worlds));
            worlds_reversed.put(Integer.valueOf(worlds), world);
            if(worlds > world_id) {
               world_id = worlds;
            }
         }

         List worlds1 = CoreProtect.getInstance().getServer().getWorlds();
         Iterator world2 = worlds1.iterator();

         while(world2.hasNext()) {
            World world1 = (World)world2.next();
            String worldname = world1.getName();
            if(worlds.get(worldname) == null) {
               int id = world_id + 1;
               worlds.put(worldname, Integer.valueOf(id));
               worlds_reversed.put(Integer.valueOf(id), worldname);
               world_id = id;
               Queue.queueWorldInsert(id, worldname);
            }
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   public static boolean performInitialization() {
      try {
         loadConfig();
         loadDatabase();
         Connection e = Database.getConnection(true);
         Statement statement = e.createStatement();
         checkPlayers(e);
         loadWorlds(statement);
         loadTypes(statement);
         if(Functions.checkWorldEdit()) {
            Functions.loadWorldEdit();
         }

         server_running = true;
         boolean validVersion = Patch.versionCheck(statement);
         statement.close();
         e.close();
         return validVersion;
      } catch (Exception var3) {
         var3.printStackTrace();
         return false;
      }
   }

}
