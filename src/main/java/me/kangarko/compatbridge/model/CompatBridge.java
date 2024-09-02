package me.kangarko.compatbridge.model;

import me.kangarko.compatbridge.utils.VersionResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

public final class CompatBridge {
   public static void setData(Block block, int data) {
      try {
         Block.class.getMethod("setData", Byte.TYPE).invoke(block, (byte)data);
      } catch (NoSuchMethodException var3) {
         block.setBlockData(Bukkit.getUnsafe().fromLegacy(block.getType(), (byte)data), true);
      } catch (ReflectiveOperationException var4) {
         var4.printStackTrace();
      }

   }

   public static FallingBlock spawnFallingBlock(World w, Location l, Material mat, byte data) {
      if (VersionResolver.MC_1_13) {
         return w.spawnFallingBlock(l, Bukkit.getUnsafe().fromLegacy(mat, data));
      } else {
         try {
            return (FallingBlock)w.getClass().getMethod("spawnFallingBlock", Location.class, Integer.TYPE, Byte.TYPE).invoke(w, l, mat.getId(), data);
         } catch (ReflectiveOperationException var5) {
            var5.printStackTrace();
            return null;
         }
      }
   }

   public static void setTypeAndData(Block block, CompMaterial mat, byte data) {
      setTypeAndData(block, mat.getMaterial(), data);
   }

   public static void setTypeAndData(Block block, Material mat, byte data) {
      setTypeAndData(block, mat, data, true);
   }

   public static void setTypeAndData(Block block, Material mat, byte data, boolean physics) {
      if (VersionResolver.MC_1_13) {
         block.setType(mat);
         block.setBlockData(Bukkit.getUnsafe().fromLegacy(mat, data), physics);
      } else {
         try {
            block.getClass().getMethod("setTypeIdAndData", Integer.TYPE, Byte.TYPE, Boolean.TYPE).invoke(block, mat.getId(), data, physics);
         } catch (ReflectiveOperationException var5) {
            var5.printStackTrace();
         }
      }

   }
}
