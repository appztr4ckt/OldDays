package me.kangarko.compatbridge.model;

import org.bukkit.DyeColor;

public enum CompDye {
   WHITE,
   ORANGE,
   MAGENTA,
   LIGHT_BLUE,
   YELLOW,
   LIME,
   PINK,
   GRAY,
   LIGHT_GRAY("SILVER"),
   CYAN,
   PURPLE,
   BLUE,
   BROWN,
   GREEN,
   RED,
   BLACK;

   private final DyeColor dye;

   private CompDye() {
      this((String)null);
   }

   private CompDye(String name) {
      DyeColor color;
      try {
         color = DyeColor.valueOf(this.name());
      } catch (IllegalArgumentException var6) {
         if (name == null) {
            throw new RuntimeException("Missing legacy name for DyeColor." + this.name());
         }

         color = DyeColor.valueOf(name);
      }

      if (color == null) {
         throw new RuntimeException("Failed to resolve DyeColor." + this.name());
      } else {
         this.dye = color;
      }
   }

   public DyeColor getDye() {
      return this.dye;
   }

   public static final CompDye fromWoolData(byte data) {
      return fromDye(DyeColor.getByWoolData(data));
   }

   public static final CompDye fromDye(DyeColor dye) {
      CompDye[] var4;
      int var3 = (var4 = values()).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         CompDye comp = var4[var2];
         if (comp.getDye() == dye) {
            return comp;
         }
      }

      throw new RuntimeException("Could not get CompDye from DyeColor." + dye.toString());
   }
}
