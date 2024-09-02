package me.kangarko.compatbridge.utils;

import org.bukkit.Material;
import org.bukkit.Sound;

public class VersionResolver {
   public static boolean MC_1_9 = true;
   public static boolean MC_1_8 = true;
   public static boolean MC_1_13 = true;

   static {
      try {
         Material.valueOf("TRIDENT");
      } catch (IllegalArgumentException var3) {
         MC_1_13 = false;
      }

      try {
         Sound.valueOf("BLOCK_END_GATEWAY_SPAWN").ordinal();
      } catch (Throwable var2) {
         MC_1_9 = false;
      }

      try {
         Material.valueOf("PRISMARINE");
      } catch (IllegalArgumentException var1) {
         MC_1_8 = false;
      }

   }

   public static final boolean isAtLeast1_13() {
      return MC_1_13;
   }

   public static final boolean isAtLeast1_9() {
      return MC_1_9;
   }

   public static final boolean isAtLeast1_8() {
      return MC_1_8;
   }
}
