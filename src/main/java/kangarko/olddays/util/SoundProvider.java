package kangarko.olddays.util;

import org.bukkit.Sound;

public class SoundProvider {
   private static boolean newSoundsClass = true;

   public static void doItToIt() {
      try {
         Sound.valueOf("BLOCK_END_GATEWAY_SPAWN").ordinal();
      } catch (Throwable var1) {
         newSoundsClass = false;
      }

   }

   public static Sound getHurtFleshSound() {
      return newSoundsClass ? Sound.ENTITY_PLAYER_HURT : Sound.valueOf("HURT_FLESH");
   }

   public static Sound getShootArrowSound() {
      return newSoundsClass ? Sound.ENTITY_ARROW_SHOOT : Sound.valueOf("SHOOT_ARROW");
   }

   public static Sound getFuseSound() {
      return newSoundsClass ? Sound.ITEM_FLINTANDSTEEL_USE : Sound.valueOf("FUSE");
   }
}
