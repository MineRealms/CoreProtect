package net.coreprotect.thread;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.coreprotect.model.Config;

public class CacheCleanUp implements Runnable {

   public void run() {
      while(Config.server_running) {
         try {
            int e = 0;

            while(e < 4) {
               Thread.sleep(5000L);
               short scan_time = 30;
               Map cache = Config.lookup_cache;
               switch(e) {
               case 1:
                  cache = Config.break_cache;
                  break;
               case 2:
                  cache = Config.piston_cache;
                  break;
               case 3:
                  cache = Config.entity_cache;
                  scan_time = 3600;
               }

               int timestamp = (int)(System.currentTimeMillis() / 1000L) - scan_time;
               Iterator it = cache.entrySet().iterator();

               while(true) {
                  if(it.hasNext()) {
                     try {
                        Entry e1 = (Entry)it.next();
                        Object[] data = (Object[])e1.getValue();
                        int time = ((Integer)data[0]).intValue();
                        if(time < timestamp) {
                           try {
                              it.remove();
                           } catch (Exception var10) {
                              ;
                           }
                        }
                        continue;
                     } catch (Exception var11) {
                        ;
                     }
                  }

                  ++e;
                  break;
               }
            }
         } catch (Exception var12) {
            var12.printStackTrace();
         }
      }

   }
}
