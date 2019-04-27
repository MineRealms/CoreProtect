package net.coreprotect.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import net.coreprotect.CoreProtect;
import net.coreprotect.Functions;
import net.coreprotect.consumer.Queue;
import net.coreprotect.database.Database;
import net.coreprotect.database.Logger;
import net.coreprotect.listener.HangingListener;
import net.coreprotect.model.BlockInfo;
import net.coreprotect.model.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

public class EntityListener extends Queue implements Listener {

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      if(!event.isCancelled()) {
         if(event.getEntityType().equals(EntityType.ARMOR_STAND)) {
            World world = event.getEntity().getWorld();
            Location location = event.getEntity().getLocation();
            String key = world.getName() + "-" + location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ();
            Iterator it = Config.entity_block_mapper.entrySet().iterator();

            while(it.hasNext()) {
               Entry pair = (Entry)it.next();
               UUID uuid = (UUID)pair.getKey();
               Object[] data = (Object[])pair.getValue();
               if((data[0].equals(key) || data[1].equals(key)) && Functions.getEntityMaterial(event.getEntityType()).equals(data[2])) {
                  Player player = CoreProtect.getInstance().getServer().getPlayer(uuid);
                  Queue.queueBlockPlace(player.getName(), location.getBlock().getState(), location.getBlock(), location.getBlock().getState(), (Material)data[2], (int)event.getEntity().getLocation().getYaw(), 1);
                  it.remove();
               }
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onEntityBlockForm(EntityBlockFormEvent event) {
      World world = event.getBlock().getWorld();
      if(!event.isCancelled() && Functions.checkConfig(world, "entity-change") == 1) {
         Entity entity = event.getEntity();
         Block block = event.getBlock();
         BlockState newState = event.getNewState();
         String e = "";
         if(entity instanceof Snowman) {
            e = "#snowman";
         }

         if(e.length() > 0) {
            Queue.queueBlockPlace(e, block.getState(), newState.getType(), Functions.getData(newState));
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onEntityChangeBlock(EntityChangeBlockEvent event) {
      World world = event.getBlock().getWorld();
      if(!event.isCancelled() && Functions.checkConfig(world, "entity-change") == 1) {
         Entity entity = event.getEntity();
         Block block = event.getBlock();
         Material newtype = event.getTo();
         Material type = event.getBlock().getType();
         byte data = Functions.getData(event.getBlock());
         String e = "";
         if(entity instanceof Enderman) {
            e = "#enderman";
         } else if(entity instanceof EnderDragon) {
            e = "#enderdragon";
         } else if(entity instanceof Wither) {
            e = "#wither";
         } else if(entity instanceof Silverfish && newtype.equals(Material.AIR)) {
            e = "#silverfish";
         }

         if(e.length() > 0) {
            if(newtype.equals(Material.AIR)) {
               Queue.queueBlockBreak(e, block.getState(), type, data);
            } else {
               Queue.queueBlockPlace(e, block);
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      Entity damager = event.getDamager();
      if(event.getEntity() instanceof ItemFrame || event.getEntity() instanceof ArmorStand || event.getEntity() instanceof EnderCrystal) {
         boolean inspecting = false;
         String user = "#entity";
         if(damager != null) {
            Entity entity = event.getEntity();
            Block block = entity.getLocation().getBlock();
            if(damager instanceof Player) {
               Player crystal = (Player)damager;
               user = crystal.getName();
               if(Config.inspecting.get(crystal.getName()) != null && ((Boolean)Config.inspecting.get(crystal.getName())).booleanValue()) {
                  HangingListener.inspectItemFrame(block, crystal);
                  event.setCancelled(true);
                  inspecting = true;
               }
            } else if(damager instanceof Arrow) {
               Arrow crystal1 = (Arrow)damager;
               ProjectileSource data = crystal1.getShooter();
               if(data instanceof Player) {
                  Player player = (Player)data;
                  user = player.getName();
               }
            } else if(damager instanceof TNTPrimed) {
               user = "#tnt";
            } else if(damager instanceof Minecart) {
               String crystal2 = damager.getType().name();
               if(crystal2.contains("TNT")) {
                  user = "#tnt";
               }
            } else if(damager instanceof Creeper) {
               user = "#creeper";
            } else if(!(damager instanceof EnderDragon) && !(damager instanceof EnderDragonPart)) {
               if(!(damager instanceof Wither) && !(damager instanceof WitherSkull)) {
                  if(damager.getType() != null) {
                     user = "#" + damager.getType().name().toLowerCase();
                  }
               } else {
                  user = "#wither";
               }
            } else {
               user = "#enderdragon";
            }

            if(!event.isCancelled() && Functions.checkConfig(entity.getWorld(), "block-break") == 1 && !inspecting) {
               if(entity instanceof ItemFrame) {
                  ItemFrame crystal3 = (ItemFrame)event.getEntity();
                  int data1 = 0;
                  if(crystal3.getItem() != null) {
                     data1 = Functions.block_id(crystal3.getItem().getType());
                  }

                  Queue.queueBlockBreak(user, block.getState(), Material.ITEM_FRAME, data1);
                  Queue.queueBlockPlace(user, block.getState(), Material.ITEM_FRAME, 0);
               } else if(entity instanceof ArmorStand) {
                  Database.containerBreakCheck(user, Material.ARMOR_STAND, entity, block.getLocation());
                  Queue.queueBlockBreak(user, block.getState(), Material.ARMOR_STAND, (int)entity.getLocation().getYaw());
               } else if(entity instanceof EnderCrystal) {
                  EnderCrystal crystal4 = (EnderCrystal)event.getEntity();
                  Queue.queueBlockBreak(user, block.getState(), Material.END_CRYSTAL, crystal4.isShowingBottom()?1:0);
               }
            }
         }
      }

   }

   @EventHandler
   public void onEntityDeath(EntityDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if(entity != null) {
         if(Functions.checkConfig(entity.getWorld(), "entity-kills") == 1) {
            EntityDamageEvent damage = entity.getLastDamageCause();
            if(damage != null) {
               String e = "";
               boolean skip = true;
               if(Functions.checkConfig(entity.getWorld(), "skip-generic-data") == 0 || !(entity instanceof Zombie) && !(entity instanceof Skeleton)) {
                  skip = false;
               }

               if(damage instanceof EntityDamageByEntityEvent) {
                  EntityDamageByEntityEvent entity_type = (EntityDamageByEntityEvent)damage;
                  Entity data = entity_type.getDamager();
                  if(data instanceof Player) {
                     Player age = (Player)data;
                     e = age.getName();
                  } else {
                     ProjectileSource tame;
                     Player attributes;
                     String info;
                     EntityType var31;
                     if(data instanceof Arrow) {
                        Arrow var27 = (Arrow)data;
                        tame = var27.getShooter();
                        if(tame instanceof Player) {
                           attributes = (Player)tame;
                           e = attributes.getName();
                        } else if(tame instanceof LivingEntity) {
                           var31 = ((LivingEntity)tame).getType();
                           if(var31 != null) {
                              info = var31.name().toLowerCase();
                              e = "#" + info;
                           }
                        }
                     } else if(data instanceof ThrownPotion) {
                        ThrownPotion var28 = (ThrownPotion)data;
                        tame = var28.getShooter();
                        if(tame instanceof Player) {
                           attributes = (Player)tame;
                           e = attributes.getName();
                        } else if(tame instanceof LivingEntity) {
                           var31 = ((LivingEntity)tame).getType();
                           if(var31 != null) {
                              info = var31.name().toLowerCase();
                              e = "#" + info;
                           }
                        }
                     } else if(data.getType().name() != null) {
                        e = "#" + data.getType().name().toLowerCase();
                     }
                  }
               } else {
                  DamageCause var24 = damage.getCause();
                  if(var24.equals(DamageCause.FIRE)) {
                     e = "#fire";
                  } else if(var24.equals(DamageCause.FIRE_TICK)) {
                     if(!skip) {
                        e = "#fire";
                     }
                  } else if(var24.equals(DamageCause.LAVA)) {
                     e = "#lava";
                  } else if(var24.equals(DamageCause.BLOCK_EXPLOSION)) {
                     e = "#explosion";
                  }
               }

               EntityType var25 = entity.getType();
               if(e.length() == 0 && !skip) {
                  if(!(entity instanceof Player) && var25.name() != null) {
                     e = "#" + var25.name().toLowerCase();
                  } else if(entity instanceof Player) {
                     e = entity.getName();
                  }
               }

               if(e.startsWith("#wither")) {
                  e = "#wither";
               }

               if(e.startsWith("#enderdragon")) {
                  e = "#enderdragon";
               }

               if(e.startsWith("#primedtnt") || e.startsWith("#tnt")) {
                  e = "#tnt";
               }

               if(e.startsWith("#lightning")) {
                  e = "#lightning";
               }

               if(e.length() > 0) {
                  ArrayList var26 = new ArrayList();
                  ArrayList var29 = new ArrayList();
                  ArrayList var30 = new ArrayList();
                  ArrayList var32 = new ArrayList();
                  ArrayList var33 = new ArrayList();
                  if(entity instanceof Ageable) {
                     Ageable abstractHorse = (Ageable)entity;
                     var29.add(Integer.valueOf(abstractHorse.getAge()));
                     var29.add(Boolean.valueOf(abstractHorse.getAgeLock()));
                     var29.add(Boolean.valueOf(abstractHorse.isAdult()));
                     var29.add(Boolean.valueOf(abstractHorse.canBreed()));
                     var29.add((Object)null);
                  }

                  if(entity instanceof Tameable) {
                     Tameable var34 = (Tameable)entity;
                     var30.add(Boolean.valueOf(var34.isTamed()));
                     if(var34.isTamed() && var34.getOwner() != null) {
                        var30.add(var34.getOwner().getName());
                     }
                  }

                  ArrayList itemMap;
                  if(entity instanceof Attributable) {
                     LivingEntity var35 = entity;
                     Attribute[] chestedHorse = Attribute.values();
                     int llama = chestedHorse.length;

                     for(int decor = 0; decor < llama; ++decor) {
                        Attribute recipe = chestedHorse[decor];
                        AttributeInstance ingredients = var35.getAttribute(recipe);
                        if(ingredients != null) {
                           itemMap = new ArrayList();
                           ArrayList item = new ArrayList();
                           itemMap.add(ingredients.getAttribute());
                           itemMap.add(Double.valueOf(ingredients.getBaseValue()));
                           Iterator metadata = ingredients.getModifiers().iterator();

                           while(metadata.hasNext()) {
                              AttributeModifier modifier = (AttributeModifier)metadata.next();
                              item.add(modifier.serialize());
                           }

                           itemMap.add(item);
                           var32.add(itemMap);
                        }
                     }
                  }

                  if(entity instanceof Creeper) {
                     Creeper var36 = (Creeper)entity;
                     var33.add(Boolean.valueOf(var36.isPowered()));
                  } else if(entity instanceof Enderman) {
                     Enderman var37 = (Enderman)entity;
                     var33.add(var37.getCarriedMaterial().toItemStack().serialize());
                  } else if(entity instanceof IronGolem) {
                     IronGolem var39 = (IronGolem)entity;
                     var33.add(Boolean.valueOf(var39.isPlayerCreated()));
                  } else if(entity instanceof Ocelot) {
                     Ocelot var41 = (Ocelot)entity;
                     var33.add(var41.getCatType());
                     var33.add(Boolean.valueOf(var41.isSitting()));
                  } else if(entity instanceof Pig) {
                     Pig var43 = (Pig)entity;
                     var33.add(Boolean.valueOf(var43.hasSaddle()));
                  } else if(entity instanceof Sheep) {
                     Sheep var45 = (Sheep)entity;
                     var33.add(Boolean.valueOf(var45.isSheared()));
                     var33.add(var45.getColor());
                  } else if(entity instanceof Skeleton) {
                     var33.add((Object)null);
                  } else if(entity instanceof Slime) {
                     Slime var47 = (Slime)entity;
                     var33.add(Integer.valueOf(var47.getSize()));
                  } else if(entity instanceof Villager) {
                     Villager var49 = (Villager)entity;
                     var33.add(var49.getProfession());
                     var33.add(Integer.valueOf(var49.getRiches()));
                     ArrayList var38 = new ArrayList();
                     Iterator var44 = var49.getRecipes().iterator();

                     while(var44.hasNext()) {
                        MerchantRecipe var51 = (MerchantRecipe)var44.next();
                        ArrayList var54 = new ArrayList();
                        ArrayList var57 = new ArrayList();
                        itemMap = new ArrayList();
                        ItemStack var58 = var51.getResult().clone();
                        List var59 = Logger.getItemMeta(var58, var58.getType(), 0);
                        var58.setItemMeta((ItemMeta)null);
                        itemMap.add(var58.serialize());
                        itemMap.add(var59);
                        var54.add(itemMap);
                        var54.add(Integer.valueOf(var51.getUses()));
                        var54.add(Integer.valueOf(var51.getMaxUses()));
                        var54.add(Boolean.valueOf(var51.hasExperienceReward()));
                        Iterator var60 = var51.getIngredients().iterator();

                        while(var60.hasNext()) {
                           ItemStack ingredient = (ItemStack)var60.next();
                           itemMap = new ArrayList();
                           var58 = ingredient.clone();
                           var59 = Logger.getItemMeta(var58, var58.getType(), 0);
                           var58.setItemMeta((ItemMeta)null);
                           itemMap.add(var58.serialize());
                           itemMap.add(var59);
                           var57.add(itemMap);
                        }

                        var54.add(var57);
                        var38.add(var54);
                     }

                     var33.add(var38);
                  } else if(entity instanceof Wolf) {
                     Wolf var50 = (Wolf)entity;
                     var33.add(Boolean.valueOf(var50.isSitting()));
                     var33.add(var50.getCollarColor());
                  } else if(entity instanceof ZombieVillager) {
                     ZombieVillager var52 = (ZombieVillager)entity;
                     var33.add(Boolean.valueOf(var52.isBaby()));
                     var33.add(var52.getVillagerProfession());
                  } else if(entity instanceof Zombie) {
                     Zombie var55 = (Zombie)entity;
                     var33.add(Boolean.valueOf(var55.isBaby()));
                     var33.add((Object)null);
                     var33.add((Object)null);
                  } else if(entity instanceof AbstractHorse) {
                     AbstractHorse var56 = (AbstractHorse)entity;
                     var33.add((Object)null);
                     var33.add((Object)null);
                     var33.add(Integer.valueOf(var56.getDomestication()));
                     var33.add(Double.valueOf(var56.getJumpStrength()));
                     var33.add(Integer.valueOf(var56.getMaxDomestication()));
                     var33.add((Object)null);
                     var33.add((Object)null);
                     ItemStack var53;
                     if(entity instanceof Horse) {
                        Horse var40 = (Horse)entity;
                        ItemStack var46 = var40.getInventory().getArmor();
                        if(var46 != null) {
                           var33.add(var46.serialize());
                        } else {
                           var33.add((Object)null);
                        }

                        var53 = var40.getInventory().getSaddle();
                        if(var53 != null) {
                           var33.add(var53.serialize());
                        } else {
                           var33.add((Object)null);
                        }

                        var33.add(var40.getColor());
                        var33.add(var40.getStyle());
                     } else if(entity instanceof ChestedHorse) {
                        ChestedHorse var42 = (ChestedHorse)entity;
                        var33.add(Boolean.valueOf(var42.isCarryingChest()));
                        if(entity instanceof Llama) {
                           Llama var48 = (Llama)entity;
                           var53 = var48.getInventory().getDecor();
                           if(var53 != null) {
                              var33.add(var53.serialize());
                           } else {
                              var33.add((Object)null);
                           }

                           var33.add(var48.getColor());
                        }
                     }
                  }

                  var26.add(var29);
                  var26.add(var30);
                  var26.add(var33);
                  var26.add(Boolean.valueOf(entity.isCustomNameVisible()));
                  var26.add(entity.getCustomName());
                  var26.add(var32);
                  if(!(entity instanceof Player)) {
                     Queue.queueEntityKill(e, entity.getLocation(), var26, var25);
                  } else {
                     Queue.queuePlayerKill(e, entity.getLocation(), entity.getName());
                  }
               }
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   protected void onEntityExplode(EntityExplodeEvent event) {
      Entity entity = event.getEntity();
      World world = event.getLocation().getWorld();
      String user = "#explosion";
      if(entity instanceof TNTPrimed) {
         user = "#tnt";
      } else if(entity instanceof Minecart) {
         String log = entity.getType().name();
         if(log.contains("TNT")) {
            user = "#tnt";
         }
      } else if(entity instanceof Creeper) {
         user = "#creeper";
      } else if(!(entity instanceof EnderDragon) && !(entity instanceof EnderDragonPart)) {
         if(!(entity instanceof Wither) && !(entity instanceof WitherSkull)) {
            if(entity instanceof EnderCrystal) {
               user = "#endercrystal";
            }
         } else {
            user = "#wither";
         }
      } else {
         user = "#enderdragon";
      }

      boolean var25 = false;
      if(Functions.checkConfig(world, "explosions") == 1) {
         var25 = true;
      }

      if((user.equals("#enderdragon") || user.equals("#wither")) && Functions.checkConfig(world, "entity-change") == 0) {
         var25 = false;
      }

      if(!event.isCancelled() && var25) {
         List b = event.blockList();
         ArrayList nb = new ArrayList();
         Iterator var8;
         Block block;
         if(Functions.checkConfig(world, "natural-break") == 1) {
            var8 = b.iterator();

            while(var8.hasNext()) {
               block = (Block)var8.next();
               int blockType = block.getX();
               int blockState = block.getY();
               int e = block.getZ();
               Location line1 = new Location(world, (double)(blockType + 1), (double)blockState, (double)e);
               Location line2 = new Location(world, (double)(blockType - 1), (double)blockState, (double)e);
               Location line3 = new Location(world, (double)blockType, (double)blockState, (double)(e + 1));
               Location line4 = new Location(world, (double)blockType, (double)blockState, (double)(e - 1));
               Location l5 = new Location(world, (double)blockType, (double)(blockState + 1), (double)e);
               int l = 1;

               for(byte m = 6; l < m; ++l) {
                  Location lc = line1;
                  if(l == 2) {
                     lc = line2;
                  }

                  if(l == 3) {
                     lc = line3;
                  }

                  if(l == 4) {
                     lc = line4;
                  }

                  if(l == 5) {
                     lc = l5;
                  }

                  Block block_t = world.getBlockAt(lc);
                  Material t = block_t.getType();
                  if(BlockInfo.track_any.contains(t) || BlockInfo.track_top.contains(t) || BlockInfo.track_side.contains(t)) {
                     Block bl = world.getBlockAt(lc);
                     nb.add(bl);
                  }
               }
            }
         }

         var8 = b.iterator();

         while(var8.hasNext()) {
            block = (Block)var8.next();
            if(!nb.contains(block)) {
               nb.add(block);
            }
         }

         var8 = nb.iterator();

         while(var8.hasNext()) {
            block = (Block)var8.next();
            Material var26 = block.getType();
            BlockState var27 = block.getState();
            if((var26.equals(Material.SIGN_POST) || var26.equals(Material.WALL_SIGN)) && Functions.checkConfig(world, "sign-text") == 1) {
               try {
                  Sign var28 = (Sign)var27;
                  String var29 = var28.getLine(0);
                  String var30 = var28.getLine(1);
                  String var31 = var28.getLine(2);
                  String var32 = var28.getLine(3);
                  Queue.queueSignText(user, var27, var29, var30, var31, var32, 5);
               } catch (Exception var24) {
                  var24.printStackTrace();
               }
            }

            Database.containerBreakCheck(user, var26, block, block.getLocation());
            Queue.queueBlockBreak(user, var27, var26, Functions.getData(block));
         }
      }

   }
}
