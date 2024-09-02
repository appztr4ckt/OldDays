package me.kangarko.compatbridge.model;

import me.kangarko.compatbridge.utils.VersionResolver;
import org.bukkit.Sound;

public enum CompSound {
   BLOCK_NOTE_HAT("NOTE_BASS", "BLOCK_NOTE_BLOCK_HAT"),
   BLOCK_NOTE_HARP("NOTE_SNARE_DRUM", "BLOCK_ANVIL_BREAK"),
   BLOCK_NOTE_BASS("NOTE_BASS", "BLOCK_NOTE_BLOCK_BASS"),
   BLOCK_ANVIL_FALL("ANVIL_LAND", "BLOCK_ANVIL_FALL"),
   BLOCK_DISPENSER_LAUNCH("ITEM_PICKUP"),
   BLOCK_FIRE_EXTINGUISH("FIRE_IGNITE"),
   BLOCK_GLASS_BREAK("GLASS"),
   BLOCK_CHEST_CLOSE("CHEST_CLOSE"),
   BLOCK_ANVIL_HIT("ANVIL_LAND"),
   ENTITY_ARROW_HIT_PLAYER("SUCCESSFUL_HIT"),
   ENTITY_EXPERIENCE_ORB_PICKUP("ORB_PICKUP"),
   ENTITY_CHICKEN_EGG("CHICKEN_EGG_POP"),
   ENTITY_ENDERDRAGON_FLAP("ENDERDRAGON_WINGS", "ENTITY_ENDER_DRAGON_FLAP"),
   ENTITY_ENDERDRAGON_DEATH("ENDERDRAGON_DEATH", "ENTITY_ENDER_DRAGON_DEATH"),
   ENTITY_ITEMFRAME_BREAK("FIREWORK_BLAST", "ENTITY_ITEM_FRAME_BREAK"),
   ENTITY_ITEM_PICKUP("ITEM_PICKUP"),
   ENTITY_FIREWORK_BLAST("FIREWORK_BLAST", "ENTITY_FIREWORK_ROCKET_BLAST"),
   ENTITY_FIREWORK_LAUNCH("FIREWORK_LAUNCH", "ENTITY_FIREWORK_ROCKET_LAUNCH"),
   ENTITY_FIREWORK_TWINKLE_FAR("FIREWORK_LAUNCH", "ENTITY_FIREWORK_ROCKET_TWINKLE_FAR"),
   ENTITY_FIREWORK_LARGE_BLAST_FAR("FIREWORK_LARGE_BLAST", "ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR"),
   ENTITY_PLAYER_HURT("HURT_FLESH"),
   ENTITY_PLAYER_LEVELUP("LEVEL_UP"),
   ENTITY_PLAYER_BURP("BURP");

   private final Sound sound;
   private final String legacyName;
   private final String modernName;

   public Sound getSound() {
      return this.sound;
   }

   private CompSound(String legacyName) {
      this(legacyName, (String)null);
   }

   private CompSound(String legacyName, String modernName) {
      this.legacyName = legacyName;
      this.modernName = modernName;
      this.sound = parseSound(modernName, this.name(), legacyName);
   }

   private static Sound parseSound(String v1_13, String v1_9, String legacy) {
      if (VersionResolver.isAtLeast1_13()) {
         return Sound.valueOf(v1_13 != null ? v1_13 : v1_9);
      } else {
         return VersionResolver.isAtLeast1_9() ? Sound.valueOf(v1_9) : Sound.valueOf(legacy);
      }
   }

   public static final Sound convert(String original) {
      CompSound s;
      int var2;
      int var3;
      CompSound[] var4;
      if (VersionResolver.MC_1_13) {
         var3 = (var4 = values()).length;

         for(var2 = 0; var2 < var3; ++var2) {
            s = var4[var2];
            if (original.equalsIgnoreCase(s.name()) || original.equalsIgnoreCase(s.legacyName) || s.modernName != null && original.equalsIgnoreCase(s.modernName)) {
               return Sound.valueOf(s.modernName != null ? s.modernName : s.name());
            }
         }
      }

      if (VersionResolver.MC_1_9) {
         var3 = (var4 = values()).length;

         for(var2 = 0; var2 < var3; ++var2) {
            s = var4[var2];
            if (original.equalsIgnoreCase(s.legacyName)) {
               return Sound.valueOf(s.toString());
            }
         }
      } else {
         var3 = (var4 = values()).length;

         for(var2 = 0; var2 < var3; ++var2) {
            s = var4[var2];
            if (original.equalsIgnoreCase(s.toString())) {
               try {
                  return Sound.valueOf(s.legacyName);
               } catch (IllegalArgumentException var6) {
                  if (!VersionResolver.isAtLeast1_8()) {
                     return Sound.valueOf("LEVEL_UP");
                  }

                  throw new RuntimeException(var6);
               }
            }
         }
      }

      try {
         return Sound.valueOf(original);
      } catch (IllegalArgumentException var7) {
         if (!VersionResolver.isAtLeast1_8()) {
            return Sound.valueOf("LEVEL_UP");
         } else {
            throw new RuntimeException(var7);
         }
      }
   }
}
