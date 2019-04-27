package net.coreprotect;

import java.io.File;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.Functions;
import net.coreprotect.bukkit.MetricsLite;
import net.coreprotect.command.CommandHandler;
import net.coreprotect.consumer.Consumer;
import net.coreprotect.consumer.Process;
import net.coreprotect.listener.BlockListener;
import net.coreprotect.listener.EntityListener;
import net.coreprotect.listener.HangingListener;
import net.coreprotect.listener.PlayerListener;
import net.coreprotect.listener.WorldListener;
import net.coreprotect.model.Config;
import net.coreprotect.thread.CacheCleanUp;
import net.coreprotect.thread.CheckUpdate;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreProtect extends JavaPlugin {

   protected static CoreProtect instance;
   private CoreProtectAPI api = new CoreProtectAPI();


   public static CoreProtect getInstance() {
      return instance;
   }

   public CoreProtectAPI getAPI() {
      return this.api;
   }

   private static boolean performVersionChecks() {
      try {
         String e = "1.11";
         String[] bukkitVersion = getInstance().getServer().getBukkitVersion().split("-|\\.");
         if(Functions.newVersion(bukkitVersion[0] + "." + bukkitVersion[1], e)) {
            System.out.println("[CoreProtect] Spigot " + e + " or higher is required.");
            return false;
         } else {
            String requiredJavaVersion = "1.8";
            String[] javaVersion = (System.getProperty("java.version") + ".0").split("\\.");
            if(Functions.newVersion(javaVersion[0] + "." + javaVersion[1], requiredJavaVersion)) {
               System.out.println("[CoreProtect] Java " + requiredJavaVersion + " or higher is required.");
               return false;
            } else {
               return true;
            }
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         return false;
      }
   }

   public void onEnable() {
      instance = this;
      PluginDescriptionFile pluginDescription = this.getDescription();
      boolean start = performVersionChecks();
      if(start) {
         try {
            Consumer.initialize();
            this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
            this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
            this.getServer().getPluginManager().registerEvents(new HangingListener(), this);
            this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
            this.getCommand("coreprotect").setExecutor(CommandHandler.getInstance());
            this.getCommand("core").setExecutor(CommandHandler.getInstance());
            this.getCommand("co").setExecutor(CommandHandler.getInstance());
            boolean cacheCleanUpThread = (new File("plugins/CoreProtect/")).exists();
            if(!cacheCleanUpThread) {
               (new File("plugins/CoreProtect")).mkdir();
            }

            start = Config.performInitialization();
         } catch (Exception var7) {
            var7.printStackTrace();
            start = false;
         }
      }

      if(start) {
         System.out.println("[CoreProtect] " + pluginDescription.getName() + " has been successfully enabled!");
         if(((Integer)Config.config.get("use-mysql")).intValue() == 1) {
            System.out.println("[CoreProtect] Using MySQL for data storage.");
         } else {
            System.out.println("[CoreProtect] Using SQLite for data storage.");
         }

         Thread cacheCleanUpThread1;
         if(((Integer)Config.config.get("check-updates")).intValue() == 1) {
            cacheCleanUpThread1 = new Thread(new CheckUpdate(true));
            cacheCleanUpThread1.start();
         }

         cacheCleanUpThread1 = new Thread(new CacheCleanUp());
         cacheCleanUpThread1.start();
         Thread consumerThread = new Thread(new Consumer());
         consumerThread.start();

         try {
            new MetricsLite(this);
         } catch (Exception var6) {
            ;
         }
      } else {
         System.out.println("[CoreProtect] " + pluginDescription.getName() + " was unable to start.");
         this.getServer().getPluginManager().disablePlugin(this);
      }

   }

   public void onDisable() {
      safeShutdown();
   }

   private static void safeShutdown() {
      try {
         int e = (int)(System.currentTimeMillis() / 1000L);
         boolean processConsumer = Config.server_running;
         if(Config.converter_running) {
            processConsumer = false;
         }

         boolean message_shown = false;

         for(Config.server_running = false; (Consumer.isRunning() || Config.converter_running) && !Config.purge_running; Thread.sleep(1L)) {
            int time = (int)(System.currentTimeMillis() / 1000L);
            if(time > e && !message_shown) {
               if(Config.converter_running) {
                  Functions.messageOwner("Finishing up data conversion. Please wait...");
               } else {
                  Functions.messageOwner("Finishing up data logging. Please wait...");
               }

               message_shown = true;
            }
         }

         if(message_shown) {
            System.out.println("[CoreProtect] Success! Resuming server shutdown.");
         }

         if(processConsumer) {
            Process.processConsumer(Consumer.current_consumer);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }
}
