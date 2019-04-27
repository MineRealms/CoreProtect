package net.coreprotect.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.coreprotect.Functions;
import net.coreprotect.command.ApplyCommand;
import net.coreprotect.command.CancelCommand;
import net.coreprotect.command.HelpCommand;
import net.coreprotect.command.InspectCommand;
import net.coreprotect.command.LookupCommand;
import net.coreprotect.command.PurgeCommand;
import net.coreprotect.command.ReloadCommand;
import net.coreprotect.command.RollbackRestoreCommand;
import net.coreprotect.command.UndoCommand;
import net.coreprotect.command.VersionCommand;
import net.coreprotect.command.WorldEditHandler;
import net.coreprotect.model.BlockInfo;
import net.coreprotect.model.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

   private static CommandHandler instance;
   protected static List natural_blocks = BlockInfo.natural_blocks;


   public static CommandHandler getInstance() {
      if(instance == null) {
         instance = new CommandHandler();
      }

      return instance;
   }

   protected static List parseAction(String[] args_input) {
      String[] args = (String[])args_input.clone();
      ArrayList result = new ArrayList();
      int count = 0;
      boolean next = false;
      String[] var5 = args;
      int var6 = args.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String i = var5[var7];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("a:") && !i.equals("action:")) {
               if(!next && !i.startsWith("a:") && !i.startsWith("action:")) {
                  next = false;
               } else {
                  result.clear();
                  i = i.replaceAll("action:", "");
                  i = i.replaceAll("a:", "");
                  if(i.startsWith("#")) {
                     i = i.replaceFirst("#", "");
                  }

                  if(!i.equals("broke") && !i.equals("break") && !i.equals("remove") && !i.equals("destroy") && !i.equals("block-break") && !i.equals("block-remove") && !i.equals("-block") && !i.equals("block-")) {
                     if(!i.equals("placed") && !i.equals("place") && !i.equals("block-place") && !i.equals("+block") && !i.equals("block+")) {
                        if(!i.equals("block") && !i.equals("block-change") && !i.equals("change")) {
                           if(!i.equals("click") && !i.equals("clicks") && !i.equals("interact") && !i.equals("interaction") && !i.equals("player-interact") && !i.equals("player-interaction") && !i.equals("player-click")) {
                              if(!i.equals("death") && !i.equals("deaths") && !i.equals("entity-death") && !i.equals("entity-deaths") && !i.equals("kill") && !i.equals("kills") && !i.equals("entity-kill") && !i.equals("entity-kills")) {
                                 if(!i.equals("container") && !i.equals("container-change") && !i.equals("containers") && !i.equals("chest") && !i.equals("transaction") && !i.equals("transactions")) {
                                    if(!i.equals("-container") && !i.equals("container-") && !i.equals("remove-container")) {
                                       if(!i.equals("+container") && !i.equals("container+") && !i.equals("container-add") && !i.equals("add-container")) {
                                          if(i.equals("chat")) {
                                             result.add(Integer.valueOf(6));
                                          } else if(!i.equals("command") && !i.equals("commands")) {
                                             if(!i.equals("login") && !i.equals("+session") && !i.equals("session+") && !i.equals("+connection") && !i.equals("connection+")) {
                                                if(!i.equals("logout") && !i.equals("-session") && !i.equals("session-") && !i.equals("-connection") && !i.equals("connection-")) {
                                                   if(!i.equals("session") && !i.equals("sessions") && !i.equals("connection") && !i.equals("connections")) {
                                                      if(!i.equals("username") && !i.equals("usernames") && !i.equals("user") && !i.equals("users") && !i.equals("name") && !i.equals("names") && !i.equals("uuid") && !i.equals("uuids") && !i.equals("username-change") && !i.equals("username-changes") && !i.equals("name-change") && !i.equals("name-changes")) {
                                                         result.add(Integer.valueOf(-1));
                                                      } else {
                                                         result.add(Integer.valueOf(9));
                                                      }
                                                   } else {
                                                      result.add(Integer.valueOf(8));
                                                   }
                                                } else {
                                                   result.add(Integer.valueOf(8));
                                                   result.add(Integer.valueOf(0));
                                                }
                                             } else {
                                                result.add(Integer.valueOf(8));
                                                result.add(Integer.valueOf(1));
                                             }
                                          } else {
                                             result.add(Integer.valueOf(7));
                                          }
                                       } else {
                                          result.add(Integer.valueOf(4));
                                          result.add(Integer.valueOf(1));
                                       }
                                    } else {
                                       result.add(Integer.valueOf(4));
                                       result.add(Integer.valueOf(0));
                                    }
                                 } else {
                                    result.add(Integer.valueOf(4));
                                 }
                              } else {
                                 result.add(Integer.valueOf(3));
                              }
                           } else {
                              result.add(Integer.valueOf(2));
                           }
                        } else {
                           result.add(Integer.valueOf(0));
                           result.add(Integer.valueOf(1));
                        }
                     } else {
                        result.add(Integer.valueOf(1));
                     }
                  } else {
                     result.add(Integer.valueOf(0));
                  }

                  next = false;
               }
            } else {
               next = true;
            }
         }

         ++count;
      }

      return result;
   }

   protected static Location parseCoordinates(Location location, String[] args_input) {
      String[] args = (String[])args_input.clone();
      int count = 0;
      byte next = 0;
      String[] var5 = args;
      int var6 = args.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String i = var5[var7];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("c:") && !i.equals("coord:") && !i.equals("coords:") && !i.equals("cord:") && !i.equals("cords:") && !i.equals("coordinate:") && !i.equals("coordinates:") && !i.equals("cordinate:") && !i.equals("cordinates:")) {
               if(next != 2 && !i.startsWith("c:") && !i.startsWith("coord:") && !i.startsWith("coords:") && !i.startsWith("cord:") && !i.startsWith("cords:") && !i.startsWith("coordinate:") && !i.startsWith("coordinates:") && !i.startsWith("cordinate:") && !i.startsWith("cordinates:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("coordinates:", "");
                  i = i.replaceAll("coordinate:", "");
                  i = i.replaceAll("cordinates:", "");
                  i = i.replaceAll("cordinate:", "");
                  i = i.replaceAll("coords:", "");
                  i = i.replaceAll("coord:", "");
                  i = i.replaceAll("cords:", "");
                  i = i.replaceAll("cord:", "");
                  i = i.replaceAll("c:", "");
                  if(i.contains(",")) {
                     String[] i2 = i.split(",");
                     double x = 0.0D;
                     double y = 0.0D;
                     double z = 0.0D;
                     int c_count = 0;
                     String[] var17 = i2;
                     int var18 = i2.length;

                     for(int var19 = 0; var19 < var18; ++var19) {
                        String coord = var17[var19];
                        coord = coord.replaceAll("[^0-9.\\-]", "");
                        if(coord.length() > 0 && !coord.equals(".") && !coord.equals("-")) {
                           double parsedCoord = Double.parseDouble(coord);
                           if(c_count == 0) {
                              x = parsedCoord;
                           } else if(c_count == 1) {
                              z = parsedCoord;
                           } else if(c_count == 2) {
                              y = z;
                              z = parsedCoord;
                           }

                           ++c_count;
                        }
                     }

                     if(y < 0.0D) {
                        y = 0.0D;
                     }

                     if(y > 255.0D) {
                        y = 255.0D;
                     }

                     if(c_count > 1) {
                        location.setX(x);
                        location.setY(y);
                        location.setZ(z);
                     }
                  }

                  next = 0;
               }
            } else {
               next = 2;
            }
         }

         ++count;
      }

      return location;
   }

   protected static boolean parseCount(String[] args_input) {
      String[] args = (String[])args_input.clone();
      boolean result = false;
      int count = 0;
      String[] var4 = args;
      int var5 = args.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String i = var4[var6];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(i.equals("#count") || i.equals("#sum")) {
               result = true;
            }
         }

         ++count;
      }

      return result;
   }

   protected static List parseExcluded(CommandSender player, String[] args_input) {
      String[] args = (String[])args_input.clone();
      ArrayList excluded = new ArrayList();
      int count = 0;
      byte next = 0;
      String[] var6 = args;
      int var7 = args.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String i = var6[var8];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("e:") && !i.equals("exclude:")) {
               if(next != 5 && !i.startsWith("e:") && !i.startsWith("exclude:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("exclude:", "");
                  i = i.replaceAll("e:", "");
                  if(!i.contains(",")) {
                     if(i.equals("#natural")) {
                        excluded.addAll(natural_blocks);
                     } else {
                        Material var17 = Functions.getType(i);
                        if(var17 != null) {
                           excluded.add(var17);
                        } else {
                           EntityType var18 = Functions.getEntityType(i);
                           if(var18 != null) {
                              excluded.add(var18);
                           }
                        }
                     }

                     next = 0;
                  } else {
                     String[] i_material = i.split(",");
                     String[] i_entity = i_material;
                     int var12 = i_material.length;

                     for(int var13 = 0; var13 < var12; ++var13) {
                        String i3 = i_entity[var13];
                        if(i3.equals("#natural")) {
                           excluded.addAll(natural_blocks);
                        } else {
                           Material i3_material = Functions.getType(i3);
                           if(i3_material != null) {
                              excluded.add(i3_material);
                           } else {
                              EntityType i3_entity = Functions.getEntityType(i3);
                              if(i3_entity != null) {
                                 excluded.add(i3_entity);
                              }
                           }
                        }
                     }

                     if(i.endsWith(",")) {
                        next = 5;
                     } else {
                        next = 0;
                     }
                  }
               }
            } else {
               next = 5;
            }
         }

         ++count;
      }

      return excluded;
   }

   protected static List parseExcludedUsers(CommandSender player, String[] args_input) {
      String[] args = (String[])args_input.clone();
      ArrayList excluded = new ArrayList();
      int count = 0;
      byte next = 0;
      String[] var6 = args;
      int var7 = args.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String i = var6[var8];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("e:") && !i.equals("exclude:")) {
               if(next != 5 && !i.startsWith("e:") && !i.startsWith("exclude:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("exclude:", "");
                  i = i.replaceAll("e:", "");
                  if(!i.contains(",")) {
                     boolean var18 = false;
                     if(i.equals("#natural")) {
                        var18 = true;
                     } else {
                        Material var19 = Functions.getType(i);
                        if(var19 != null) {
                           var18 = true;
                        } else {
                           EntityType var20 = Functions.getEntityType(i);
                           if(var20 != null) {
                              var18 = true;
                           }
                        }
                     }

                     if(!var18) {
                        excluded.add(i);
                     }

                     next = 0;
                  } else {
                     String[] isBlock = i.split(",");
                     String[] i_material = isBlock;
                     int i_entity = isBlock.length;

                     for(int var13 = 0; var13 < i_entity; ++var13) {
                        String i3 = i_material[var13];
                        boolean isBlock1 = false;
                        if(i3.equals("#natural")) {
                           isBlock1 = true;
                        } else {
                           Material i3_material = Functions.getType(i3);
                           if(i3_material != null) {
                              isBlock1 = true;
                           } else {
                              EntityType i3_entity = Functions.getEntityType(i3);
                              if(i3_entity != null) {
                                 isBlock1 = true;
                              }
                           }
                        }

                        if(!isBlock1) {
                           excluded.add(i3);
                        }
                     }

                     if(i.endsWith(",")) {
                        next = 5;
                     } else {
                        next = 0;
                     }
                  }
               }
            } else {
               next = 5;
            }
         }

         ++count;
      }

      return excluded;
   }

   protected static boolean parseForceGlobal(String[] args_input) {
      String[] args = (String[])args_input.clone();
      boolean result = false;
      int count = 0;
      byte next = 0;
      String[] var5 = args;
      int var6 = args.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String i = var5[var7];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("r:") && !i.equals("radius:")) {
               if(next != 2 && !i.startsWith("r:") && !i.startsWith("radius:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("radius:", "");
                  i = i.replaceAll("r:", "");
                  if(!i.equals("#global") && !i.equals("global") && !i.equals("off") && !i.equals("-1") && !i.equals("none") && !i.equals("false")) {
                     if(i.startsWith("#")) {
                        int world_id = Functions.matchWorld(i);
                        if(world_id > 0) {
                           result = true;
                        }
                     }
                  } else {
                     result = true;
                  }

                  next = 0;
               }
            } else {
               next = 2;
            }
         }

         ++count;
      }

      return result;
   }

   protected static Location parseLocation(CommandSender user, String[] args) {
      Location location = null;
      if(user instanceof Player) {
         location = ((Player)user).getLocation();
      } else if(user instanceof BlockCommandSender) {
         location = ((BlockCommandSender)user).getBlock().getLocation();
      }

      if(location != null) {
         location = parseCoordinates(location, args);
      }

      return location;
   }

   protected static int parseNoisy(String[] args_input) {
      String[] args = (String[])args_input.clone();
      byte noisy = 0;
      int count = 0;
      if(((Integer)Config.config.get("verbose")).intValue() == 1) {
         noisy = 1;
      }

      String[] var4 = args;
      int var5 = args.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String i = var4[var6];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("n") && !i.equals("noisy") && !i.equals("v") && !i.equals("verbose") && !i.equals("#v") && !i.equals("#verbose")) {
               if(i.equals("#silent")) {
                  noisy = 0;
               }
            } else {
               noisy = 1;
            }
         }

         ++count;
      }

      return noisy;
   }

   protected static int parsePreview(String[] args_input) {
      String[] args = (String[])args_input.clone();
      byte result = 0;
      int count = 0;
      String[] var4 = args;
      int var5 = args.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String i = var4[var6];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(i.equals("#preview")) {
               result = 1;
            } else if(i.equals("#preview_cancel")) {
               result = 2;
            }
         }

         ++count;
      }

      return result;
   }

   protected static Integer[] parseRadius(String[] args_input, CommandSender user, Location location) {
      String[] args = (String[])args_input.clone();
      Integer[] radius = null;
      int count = 0;
      byte next = 0;
      String[] var7 = args;
      int var8 = args.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         String i = var7[var9];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("r:") && !i.equals("radius:")) {
               if(next != 2 && !i.startsWith("r:") && !i.startsWith("radius:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("radius:", "");
                  i = i.replaceAll("r:", "");
                  if(!i.equals("#worldedit") && !i.equals("#we")) {
                     if((!i.startsWith("#") || i.length() <= 1) && !i.equals("global") && !i.equals("off") && !i.equals("-1") && !i.equals("none") && !i.equals("false")) {
                        int var24 = 0;
                        int r_x = 0;
                        int r_y = -1;
                        int r_z = 0;
                        String[] r_dat = new String[]{i};
                        boolean validRadius = false;
                        if(i.contains("x")) {
                           r_dat = i.split("x");
                        }

                        String[] xmin = r_dat;
                        int xmax = r_dat.length;

                        int ymin;
                        for(ymin = 0; ymin < xmax; ++ymin) {
                           String ymax = xmin[ymin];
                           String zmin = ymax.replaceAll("[^0-9.]", "");
                           if(zmin.length() > 0 && zmin.length() == ymax.length() && zmin.replaceAll("[^0-9]", "").length() > 0) {
                              double zmax = Double.parseDouble(zmin);
                              if(var24 == 0) {
                                 r_x = (int)zmax;
                                 r_z = (int)zmax;
                              } else if(var24 == 1) {
                                 r_y = (int)zmax;
                              } else if(var24 == 2) {
                                 r_z = (int)zmax;
                              }

                              validRadius = true;
                           }

                           ++var24;
                        }

                        if(location != null) {
                           int var25 = location.getBlockX() - r_x;
                           xmax = location.getBlockX() + r_x;
                           ymin = -1;
                           int var26 = -1;
                           int var27 = location.getBlockZ() - r_z;
                           int var28 = location.getBlockZ() + r_z;
                           if(r_y > -1) {
                              ymin = location.getBlockY() - r_y;
                              var26 = location.getBlockY() + r_y;
                           }

                           int max = r_x;
                           if(r_y > r_x) {
                              max = r_y;
                           }

                           if(r_z > max) {
                              max = r_z;
                           }

                           if(validRadius) {
                              radius = new Integer[]{Integer.valueOf(max), Integer.valueOf(var25), Integer.valueOf(xmax), Integer.valueOf(ymin), Integer.valueOf(var26), Integer.valueOf(var27), Integer.valueOf(var28), Integer.valueOf(0)};
                           } else {
                              radius = new Integer[]{Integer.valueOf(-1)};
                           }
                        }
                     }
                  } else if(user.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
                     Integer[] rcount = WorldEditHandler.runWorldEditCommand(user);
                     if(rcount != null) {
                        radius = rcount;
                     }
                  }

                  next = 0;
               }
            } else {
               next = 2;
            }
         }

         ++count;
      }

      return radius;
   }

   protected static List parseRestricted(CommandSender player, String[] args_input) {
      String[] args = (String[])args_input.clone();
      ArrayList restricted = new ArrayList();
      int count = 0;
      byte next = 0;
      String[] var6 = args;
      int var7 = args.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String i = var6[var8];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("b:") && !i.equals("block:") && !i.equals("blocks:")) {
               if(next != 4 && !i.startsWith("b:") && !i.startsWith("block:") && !i.startsWith("blocks:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("blocks:", "");
                  i = i.replaceAll("block:", "");
                  i = i.replaceAll("b:", "");
                  if(!i.contains(",")) {
                     if(i.equals("#natural")) {
                        restricted.addAll(natural_blocks);
                     } else {
                        Material var17 = Functions.getType(i);
                        if(var17 != null) {
                           restricted.add(var17);
                        } else {
                           EntityType var18 = Functions.getEntityType(i);
                           if(var18 == null) {
                              player.sendMessage("§3CoreProtect §f- Sorry, \"" + i + "\" is an invalid block name.");
                              player.sendMessage("§3CoreProtect §f- Please view \"/co help blocks\".");
                              return null;
                           }

                           restricted.add(var18);
                        }
                     }

                     next = 0;
                  } else {
                     String[] i_material = i.split(",");
                     String[] i_entity = i_material;
                     int var12 = i_material.length;

                     for(int var13 = 0; var13 < var12; ++var13) {
                        String i3 = i_entity[var13];
                        if(i3.equals("#natural")) {
                           restricted.addAll(natural_blocks);
                        } else {
                           Material i3_material = Functions.getType(i3);
                           if(i3_material != null) {
                              restricted.add(i3_material);
                           } else {
                              EntityType i3_entity = Functions.getEntityType(i3);
                              if(i3_entity == null) {
                                 player.sendMessage("§3CoreProtect §f- Sorry, \"" + i3 + "\" is an invalid block name.");
                                 player.sendMessage("§3CoreProtect §f- Please view \"/co help blocks\".");
                                 return null;
                              }

                              restricted.add(i3_entity);
                           }
                        }
                     }

                     if(i.endsWith(",")) {
                        next = 4;
                     } else {
                        next = 0;
                     }
                  }
               }
            } else {
               next = 4;
            }
         }

         ++count;
      }

      return restricted;
   }

   protected static int parseTime(String[] args_input) {
      String[] args = (String[])args_input.clone();
      int time = 0;
      int count = 0;
      boolean next = false;
      double w = 0.0D;
      double d = 0.0D;
      double h = 0.0D;
      double m = 0.0D;
      double s = 0.0D;
      String[] var15 = args;
      int var16 = args.length;

      for(int var17 = 0; var17 < var16; ++var17) {
         String i = var15[var17];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("t:") && !i.equals("time:")) {
               if(!next && !i.startsWith("t:") && !i.startsWith("time:")) {
                  next = false;
               } else {
                  i = i.replaceAll("time:", "");
                  i = i.replaceAll("t:", "");
                  i = i.replaceAll("y", "y:");
                  i = i.replaceAll("m", "m:");
                  i = i.replaceAll("w", "w:");
                  i = i.replaceAll("d", "d:");
                  i = i.replaceAll("h", "h:");
                  i = i.replaceAll("s", "s:");
                  String[] i2 = i.split(":");
                  String[] rs = i2;
                  int var21 = i2.length;

                  for(int var22 = 0; var22 < var21; ++var22) {
                     String i3 = rs[var22];
                     String i4;
                     if(i3.endsWith("w")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           w = Double.parseDouble(i4);
                        }
                     } else if(i3.endsWith("d")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           d = Double.parseDouble(i4);
                        }
                     } else if(i3.endsWith("h")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           h = Double.parseDouble(i4);
                        }
                     } else if(i3.endsWith("m")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           m = Double.parseDouble(i4);
                        }
                     } else if(i3.endsWith("s")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           s = Double.parseDouble(i4);
                        }
                     }
                  }

                  double var25 = w * 7.0D * 24.0D * 60.0D * 60.0D + d * 24.0D * 60.0D * 60.0D + h * 60.0D * 60.0D + m * 60.0D + s;
                  time = (int)var25;
                  next = false;
               }
            } else {
               next = true;
            }
         }

         ++count;
      }

      return time;
   }

   protected static String parseTimeString(String[] args_input) {
      String[] args = (String[])args_input.clone();
      String time = "";
      int count = 0;
      boolean next = false;
      double w = 0.0D;
      double d = 0.0D;
      double h = 0.0D;
      double m = 0.0D;
      double s = 0.0D;
      String[] var15 = args;
      int var16 = args.length;

      for(int var17 = 0; var17 < var16; ++var17) {
         String i = var15[var17];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("t:") && !i.equals("time:")) {
               if(!next && !i.startsWith("t:") && !i.startsWith("time:")) {
                  next = false;
               } else {
                  i = i.replaceAll("time:", "");
                  i = i.replaceAll("t:", "");
                  i = i.replaceAll("y", "y:");
                  i = i.replaceAll("m", "m:");
                  i = i.replaceAll("w", "w:");
                  i = i.replaceAll("d", "d:");
                  i = i.replaceAll("h", "h:");
                  i = i.replaceAll("s", "s:");
                  String[] i2 = i.split(":");
                  String[] var20 = i2;
                  int var21 = i2.length;

                  for(int var22 = 0; var22 < var21; ++var22) {
                     String i3 = var20[var22];
                     String i4;
                     if(i3.endsWith("w")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           w = Double.parseDouble(i4);
                           time = time + " " + w + " week(s)";
                        }
                     } else if(i3.endsWith("d")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           d = Double.parseDouble(i4);
                           time = time + " " + d + " day(s)";
                        }
                     } else if(i3.endsWith("h")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           h = Double.parseDouble(i4);
                           time = time + " " + h + " hour(s)";
                        }
                     } else if(i3.endsWith("m")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           m = Double.parseDouble(i4);
                           time = time + " " + m + " minute(s)";
                        }
                     } else if(i3.endsWith("s")) {
                        i4 = i3.replaceAll("[^0-9.]", "");
                        if(i4.length() > 0 && i4.replaceAll("[^0-9]", "").length() > 0) {
                           s = Double.parseDouble(i4);
                           time = time + " " + s + " second(s)";
                        }
                     }
                  }

                  next = false;
               }
            } else {
               next = true;
            }
         }

         ++count;
      }

      return time;
   }

   private static void parseUser(List users, String string) {
      string = string.trim();
      if(string.contains(",")) {
         String[] data = string.split(",");
         String[] var3 = data;
         int var4 = data.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String user = var3[var5];
            validUserCheck(users, user);
         }
      } else {
         validUserCheck(users, string);
      }

   }

   protected static List parseUsers(String[] args_input) {
      String[] args = (String[])args_input.clone();
      ArrayList users = new ArrayList();
      int count = 0;
      byte next = 0;
      String[] var5 = args;
      int var6 = args.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String i = var5[var7];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(next == 2) {
               if(i.endsWith(",")) {
                  next = 2;
               } else {
                  next = 0;
               }
            } else if(!i.equals("p:") && !i.equals("user:") && !i.equals("u:")) {
               if(next != 1 && !i.startsWith("p:") && !i.startsWith("user:") && !i.startsWith("u:")) {
                  if(!i.endsWith(",") && !i.endsWith(":")) {
                     if(i.contains(":")) {
                        next = 0;
                     } else {
                        parseUser(users, i);
                        next = 0;
                     }
                  } else {
                     next = 2;
                  }
               } else {
                  i = i.replaceAll("user:", "");
                  i = i.replaceAll("p:", "");
                  i = i.replaceAll("u:", "");
                  if(!i.contains(",")) {
                     parseUser(users, i);
                     next = 0;
                  } else {
                     String[] i2 = i.split(",");
                     String[] var10 = i2;
                     int var11 = i2.length;

                     for(int var12 = 0; var12 < var11; ++var12) {
                        String i3 = var10[var12];
                        parseUser(users, i3);
                     }

                     if(i.endsWith(",")) {
                        next = 1;
                     } else {
                        next = 0;
                     }
                  }
               }
            } else {
               next = 1;
            }
         }

         ++count;
      }

      return users;
   }

   protected static int parseWorld(String[] args_input) {
      String[] args = (String[])args_input.clone();
      int world_id = 0;
      int count = 0;
      byte next = 0;
      String[] var5 = args;
      int var6 = args.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String i = var5[var7];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("r:") && !i.equals("radius:")) {
               if(next != 2 && !i.startsWith("r:") && !i.startsWith("radius:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("radius:", "");
                  i = i.replaceAll("r:", "");
                  if(!i.equals("#worldedit") && !i.equals("#we") && !i.equals("#global") && !i.equals("global") && !i.equals("off") && !i.equals("-1") && !i.equals("none") && !i.equals("false")) {
                     if(i.startsWith("#")) {
                        world_id = Functions.matchWorld(i);
                     }
                  } else {
                     world_id = 0;
                  }

                  next = 0;
               }
            } else {
               next = 2;
            }
         }

         ++count;
      }

      return world_id;
   }

   protected static boolean parseWorldEdit(String[] args_input) {
      String[] args = (String[])args_input.clone();
      boolean result = false;
      int count = 0;
      byte next = 0;
      String[] var5 = args;
      int var6 = args.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String i = var5[var7];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("r:") && !i.equals("radius:")) {
               if(next != 2 && !i.startsWith("r:") && !i.startsWith("radius:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("radius:", "");
                  i = i.replaceAll("r:", "");
                  if(i.equals("#worldedit") || i.equals("#we")) {
                     result = true;
                  }

                  next = 0;
               }
            } else {
               next = 2;
            }
         }

         ++count;
      }

      return result;
   }

   protected static String parseWorldName(String[] args_input) {
      String[] args = (String[])args_input.clone();
      String world_name = "";
      int count = 0;
      byte next = 0;
      String[] var5 = args;
      int var6 = args.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String i = var5[var7];
         if(count > 0) {
            i = i.trim().toLowerCase();
            i = i.replaceAll("\\\\", "");
            i = i.replaceAll("\'", "");
            if(!i.equals("r:") && !i.equals("radius:")) {
               if(next != 2 && !i.startsWith("r:") && !i.startsWith("radius:")) {
                  next = 0;
               } else {
                  i = i.replaceAll("radius:", "");
                  i = i.replaceAll("r:", "");
                  if(!i.equals("#worldedit") && !i.equals("#we") && !i.equals("#global") && !i.equals("global") && !i.equals("off") && !i.equals("-1") && !i.equals("none") && !i.equals("false")) {
                     if(i.startsWith("#")) {
                        world_name = i.replaceFirst("#", "");
                     }
                  } else {
                     world_name = "";
                  }

                  next = 0;
               }
            } else {
               next = 2;
            }
         }

         ++count;
      }

      return world_name;
   }

   private static void validUserCheck(List users, String user) {
      List bad_users = Arrays.asList(new String[]{"n", "noisy", "v", "verbose", "#v", "#verbose", "#silent", "#preview", "#preview_cancel", "#count", "#sum"});
      String check = user.replaceAll("[^a-zA-Z0-9#_]", "");
      if(check.equals(user) && check.length() > 0) {
         if(user.equalsIgnoreCase("#global")) {
            user = "#global";
         }

         if(!bad_users.contains(user.toLowerCase())) {
            users.add(user);
         }
      }

   }

   public boolean onCommand(CommandSender user, Command command, String commandLabel, String[] args) {
      String commandName = command.getName().toLowerCase();
      if(!commandName.equals("core") && !commandName.equals("coreprotect") && !commandName.equals("co")) {
         return false;
      } else {
         int resultc = args.length;
         if(resultc > -1) {
            String corecommand = "help";
            if(resultc > 0) {
               corecommand = args[0].toLowerCase();
            }

            boolean permission = false;
            if(user.isOp()) {
               permission = true;
            }

            if(!permission) {
               if(user.hasPermission("coreprotect.rollback") && (corecommand.equals("rollback") || corecommand.equals("rb") || corecommand.equals("ro") || corecommand.equals("apply") || corecommand.equals("cancel"))) {
                  permission = true;
               } else if(user.hasPermission("coreprotect.restore") && (corecommand.equals("restore") || corecommand.equals("rs") || corecommand.equals("re") || corecommand.equals("undo") || corecommand.equals("apply") || corecommand.equals("cancel"))) {
                  permission = true;
               } else if(user.hasPermission("coreprotect.inspect") && (corecommand.equals("i") || corecommand.equals("inspect"))) {
                  permission = true;
               } else if(user.hasPermission("coreprotect.help") && corecommand.equals("help")) {
                  permission = true;
               } else if(user.hasPermission("coreprotect.purge") && corecommand.equals("purge")) {
                  permission = true;
               } else if(user.hasPermission("coreprotect.lookup") && (corecommand.equals("l") || corecommand.equals("lookup") || corecommand.equals("near"))) {
                  permission = true;
               } else if(user.hasPermission("coreprotect.reload") && corecommand.equals("reload")) {
                  permission = true;
               }
            }

            if(!corecommand.equals("rollback") && !corecommand.equals("restore") && !corecommand.equals("rb") && !corecommand.equals("rs") && !corecommand.equals("ro") && !corecommand.equals("re")) {
               if(corecommand.equals("apply")) {
                  ApplyCommand.runCommand(user, permission, args);
               } else if(corecommand.equals("cancel")) {
                  CancelCommand.runCommand(user, permission, args);
               } else if(corecommand.equals("undo")) {
                  UndoCommand.runCommand(user, permission, args);
               } else if(corecommand.equals("help")) {
                  HelpCommand.runCommand(user, permission, args);
               } else if(corecommand.equals("purge")) {
                  PurgeCommand.runCommand(user, permission, args);
               } else if(!corecommand.equals("inspect") && !corecommand.equals("i")) {
                  if(!corecommand.equals("lookup") && !corecommand.equals("l")) {
                     if(corecommand.equals("near")) {
                        LookupCommand.runCommand(user, permission, new String[]{"l", "r:5x5"});
                     } else if(corecommand.equals("version")) {
                        VersionCommand.runCommand(user, permission, args);
                     } else if(corecommand.equals("reload")) {
                        ReloadCommand.runCommand(user, permission, args);
                     } else {
                        user.sendMessage("§3CoreProtect §f- Command \"§3/co " + corecommand + "§f\" not found.");
                     }
                  } else {
                     LookupCommand.runCommand(user, permission, args);
                  }
               } else {
                  InspectCommand.runCommand(user, permission, args);
               }
            } else {
               RollbackRestoreCommand.runCommand(user, permission, args, 0);
            }
         } else {
            user.sendMessage("§3CoreProtect §f- Please use \"§3/co <parameters>§f\".");
         }

         return true;
      }
   }

}
