package net.coreprotect.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.coreprotect.consumer.Process;
import net.coreprotect.model.Config;

public class Consumer implements Runnable {

   public static boolean resetConnection = false;
   public static int current_consumer = 0;
   public static boolean is_paused = false;
   private static boolean running = false;
   protected static boolean pause_success = false;
   static Map consumer = Collections.synchronizedMap(new HashMap());
   static Map consumer_id = Collections.synchronizedMap(new HashMap());
   static Map consumer_users = Collections.synchronizedMap(new HashMap());
   static Map consumer_strings = Collections.synchronizedMap(new HashMap());
   static Map consumer_object = Collections.synchronizedMap(new HashMap());
   static Map consumer_signs = Collections.synchronizedMap(new HashMap());
   static Map consumer_containers = Collections.synchronizedMap(new HashMap());
   static Map consumer_inventories = Collections.synchronizedMap(new HashMap());
   static Map consumer_block_list = Collections.synchronizedMap(new HashMap());
   static Map consumer_object_array_list = Collections.synchronizedMap(new HashMap());
   static Map consumer_object_list = Collections.synchronizedMap(new HashMap());


   private static void errorDelay() {
      try {
         Thread.sleep(30000L);
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }

   public static int getConsumerId() {
      return ((Integer)consumer_id.get(Integer.valueOf(current_consumer))).intValue();
   }

   public static void initialize() {
      consumer.put(Integer.valueOf(0), new ArrayList());
      consumer.put(Integer.valueOf(1), new ArrayList());
      consumer_users.put(Integer.valueOf(0), new HashMap());
      consumer_users.put(Integer.valueOf(1), new HashMap());
      consumer_strings.put(Integer.valueOf(0), new HashMap());
      consumer_strings.put(Integer.valueOf(1), new HashMap());
      consumer_object.put(Integer.valueOf(0), new HashMap());
      consumer_object.put(Integer.valueOf(1), new HashMap());
      consumer_signs.put(Integer.valueOf(0), new HashMap());
      consumer_signs.put(Integer.valueOf(1), new HashMap());
      consumer_inventories.put(Integer.valueOf(0), new HashMap());
      consumer_inventories.put(Integer.valueOf(1), new HashMap());
      consumer_block_list.put(Integer.valueOf(0), new HashMap());
      consumer_block_list.put(Integer.valueOf(1), new HashMap());
      consumer_object_array_list.put(Integer.valueOf(0), new HashMap());
      consumer_object_array_list.put(Integer.valueOf(1), new HashMap());
      consumer_object_list.put(Integer.valueOf(0), new HashMap());
      consumer_object_list.put(Integer.valueOf(1), new HashMap());
      consumer_containers.put(Integer.valueOf(0), new HashMap());
      consumer_containers.put(Integer.valueOf(1), new HashMap());
      consumer_id.put(Integer.valueOf(0), Integer.valueOf(0));
      consumer_id.put(Integer.valueOf(1), Integer.valueOf(0));
   }

   public static boolean isRunning() {
      return running;
   }

   private static void pauseConsumer() {
      while(true) {
         try {
            if(Config.server_running && (is_paused || Config.purge_running)) {
               pause_success = true;
               resetConnection = true;
               Thread.sleep(100L);
               continue;
            }
         } catch (Exception var1) {
            var1.printStackTrace();
         }

         pause_success = false;
         return;
      }
   }

   public void run() {
      running = true;
      is_paused = false;

      while(Config.server_running || Config.converter_running) {
         try {
            byte e = 0;
            if(current_consumer == 0) {
               current_consumer = 1;
               consumer_id.put(Integer.valueOf(current_consumer), Integer.valueOf(0));
            } else {
               e = 1;
               current_consumer = 0;
               consumer_id.put(Integer.valueOf(current_consumer), Integer.valueOf(0));
            }

            Thread.sleep(500L);
            pauseConsumer();
            Process.processConsumer(e);
         } catch (Exception var2) {
            var2.printStackTrace();
            errorDelay();
         }
      }

      running = false;
   }

}
