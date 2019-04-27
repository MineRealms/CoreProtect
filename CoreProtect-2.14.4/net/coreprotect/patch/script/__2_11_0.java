package net.coreprotect.patch.script;

import java.sql.Statement;
import net.coreprotect.model.Config;
import org.bukkit.Art;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class __2_11_0 {

   protected static boolean patch(Statement statement) {
      try {
         if(((Integer)Config.config.get("use-mysql")).intValue() == 1) {
            statement.executeUpdate("START TRANSACTION");
         } else {
            statement.executeUpdate("BEGIN TRANSACTION");
         }

         Art[] e = Art.values();
         int var2 = e.length;

         int var3;
         Integer type;
         String name;
         for(var3 = 0; var3 < var2; ++var3) {
            Art material = e[var3];
            type = Integer.valueOf(material.getId());
            name = material.toString().toLowerCase();
            statement.executeUpdate("INSERT INTO " + Config.prefix + "art_map (id, art) VALUES (\'" + type + "\', \'" + name + "\')");
            Config.art.put(name, type);
            Config.art_reversed.put(type, name);
            if(type.intValue() > Config.art_id) {
               Config.art_id = type.intValue();
            }
         }

         EntityType[] var8 = EntityType.values();
         var2 = var8.length;

         for(var3 = 0; var3 < var2; ++var3) {
            EntityType var10 = var8[var3];
            type = Integer.valueOf(var10.getTypeId());
            name = var10.toString().toLowerCase();
            statement.executeUpdate("INSERT INTO " + Config.prefix + "entity_map (id, entity) VALUES (\'" + type + "\', \'" + name + "\')");
            Config.entities.put(name, type);
            Config.entities_reversed.put(type, name);
            if(type.intValue() > Config.entity_id) {
               Config.entity_id = type.intValue();
            }
         }

         Material[] var9 = Material.values();
         var2 = var9.length;

         for(var3 = 0; var3 < var2; ++var3) {
            Material var11 = var9[var3];
            type = Integer.valueOf(var11.getId());
            name = var11.toString().toLowerCase();
            statement.executeUpdate("INSERT INTO " + Config.prefix + "material_map (id, material) VALUES (\'" + type + "\', \'" + name + "\')");
            Config.materials.put(name, type);
            Config.materials_reversed.put(type, name);
            if(type.intValue() > Config.material_id) {
               Config.material_id = type.intValue();
            }
         }

         if(((Integer)Config.config.get("use-mysql")).intValue() == 1) {
            statement.executeUpdate("COMMIT");
         } else {
            statement.executeUpdate("COMMIT TRANSACTION");
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return true;
   }
}
