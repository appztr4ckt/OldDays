package kangarko.olddays;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;

public class JungleTree {
   /** @deprecated */
   @Deprecated
   public static boolean junglestrom(Block sapling) {
      int posX = sapling.getX();
      int posY = sapling.getY();
      int posZ = sapling.getZ();
      World world = sapling.getWorld();
      boolean generate = false;
      Block sideUp = world.getBlockAt(posX + 1, posY, posZ);
      Block corner = world.getBlockAt(posX + 1, posY, posZ + 1);
      Block sideRight = world.getBlockAt(posX, posY, posZ + 1);
      if (sideUp.getData() == corner.getData() && corner.getData() == sideRight.getData() && sideUp.getData() == sideRight.getData() && sideUp.getData() == sapling.getData()) {
         generate = true;
      }

      if (!generate) {
         sideUp = world.getBlockAt(posX, posY, posZ + 1);
         corner = world.getBlockAt(posX - 1, posY, posZ + 1);
         sideRight = world.getBlockAt(posX - 1, posY, posZ);
         if (sideUp.getData() == corner.getData() && corner.getData() == sideRight.getData() && sideUp.getData() == sideRight.getData() && sideUp.getData() == sapling.getData()) {
            generate = true;
         }
      }

      if (!generate) {
         sideUp = world.getBlockAt(posX - 1, posY, posZ);
         corner = world.getBlockAt(posX - 1, posY, posZ - 1);
         sideRight = world.getBlockAt(posX, posY, posZ - 1);
         if (sideUp.getData() == corner.getData() && corner.getData() == sideRight.getData() && sideUp.getData() == sideRight.getData() && sideUp.getData() == sapling.getData()) {
            generate = true;
         }
      }

      if (!generate) {
         sideUp = world.getBlockAt(posX, posY, posZ - 1);
         corner = world.getBlockAt(posX + 1, posY, posZ - 1);
         sideRight = world.getBlockAt(posX + 1, posY, posZ);
         if (sideUp.getData() == corner.getData() && corner.getData() == sideRight.getData() && sideUp.getData() == sideRight.getData() && sideUp.getData() == sapling.getData()) {
            generate = true;
         }
      }

      TreeType treeType;
      if (generate) {
         sideUp.setType(Material.AIR);
         corner.setType(Material.AIR);
         sideRight.setType(Material.AIR);
         sapling.setType(Material.AIR);
         treeType = TreeType.JUNGLE;
      } else {
         sapling.setType(Material.AIR);
         treeType = TreeType.SMALL_JUNGLE;
      }

      if (!sapling.getWorld().generateTree(sapling.getLocation(), treeType)) {
         sapling.setType(jungleSapling());
         if (generate) {
            sideUp.setType(jungleSapling());
            corner.setType(jungleSapling());
            sideRight.setType(jungleSapling());
         }

         return false;
      } else {
         return true;
      }
   }

   private static Material jungleSapling() {
      try {
         return Material.valueOf("JUNGLE_SAPLING");
      } catch (Throwable var1) {
         return Material.valueOf("SAPLING");
      }
   }
}
