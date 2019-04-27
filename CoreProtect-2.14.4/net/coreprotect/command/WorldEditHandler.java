package net.coreprotect.command;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import net.coreprotect.worldedit.WorldEdit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldEditHandler {

   protected static Integer[] runWorldEditCommand(CommandSender user) {
      Integer[] result = null;

      try {
         WorldEditPlugin e = WorldEdit.getWorldEdit(user.getServer());
         if(e != null && user instanceof Player) {
            LocalSession ls = e.getSession((Player)user);
            World lw = ls.getSelectionWorld();
            if(lw != null) {
               Region region = ls.getSelection(lw);
               if(region != null && lw.getName().equals(((Player)user).getWorld().getName())) {
                  Vector v1 = region.getMinimumPoint();
                  int x1 = v1.getBlockX();
                  int y1 = v1.getBlockY();
                  int z1 = v1.getBlockZ();
                  int w = region.getWidth();
                  int h = region.getHeight();
                  int l = region.getLength();
                  int max = w;
                  if(h > w) {
                     max = h;
                  }

                  if(l > max) {
                     max = l;
                  }

                  int xmax = x1 + (w - 1);
                  int ymax = y1 + (h - 1);
                  int zmax = z1 + (l - 1);
                  result = new Integer[]{Integer.valueOf(max), Integer.valueOf(x1), Integer.valueOf(xmax), Integer.valueOf(y1), Integer.valueOf(ymax), Integer.valueOf(z1), Integer.valueOf(zmax), Integer.valueOf(1)};
               }
            }
         }
      } catch (Exception var20) {
         var20.printStackTrace();
      }

      return result;
   }
}
