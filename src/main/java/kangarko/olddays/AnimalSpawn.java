package kangarko.olddays;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.util.Vector;

public class AnimalSpawn implements Listener {
   private final List<EntityType> despawnable;

   public AnimalSpawn() {
      this.despawnable = Arrays.asList(EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.WOLF);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onChunkUnload(ChunkUnloadEvent e) {
      if (!OldDays.plugin.isWorldIgnored(e.getWorld())) {
         Entity[] chunkEntities = e.getChunk().getEntities();
         Entity[] var6 = chunkEntities;
         int var5 = chunkEntities.length;

         for(int var4 = 0; var4 < var5; ++var4) {
            Entity entity = var6[var4];
            if (this.despawnable.contains(entity.getType())) {
               entity.remove();
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onChunkLoad(ChunkLoadEvent e) {
      if (!OldDays.plugin.isWorldIgnored(e.getWorld())) {
         if (Math.random() * 10.0D < 1.0D) {
            double X = 16.0D * Math.random();
            double Z = 16.0D * Math.random();
            double chunkX = (double)(e.getChunk().getX() * 16);
            double chunkZ = (double)(e.getChunk().getZ() * 16);
            Location baseLoc = this.getRealHighestBlockAt(new Location(e.getChunk().getWorld(), chunkX + X, 5.0D, chunkZ + Z)).getLocation();
            Block baseLocBlock = baseLoc.getBlock();
            Material baseType = baseLocBlock.getType();
            String baseName = baseType.toString();
            List<String> comp = Arrays.asList("LONG_GRASS", "TALL_GRASS", "MYCEL", "MYCELIUM");
            if (baseType != Material.GRASS && baseType != Material.SNOW && !comp.contains(baseName) && !baseName.contains("LEAVES")) {
               return;
            }

            if (baseName.contains("LEAVES")) {
               for(short s = 30; s > 0; --s) {
                  Block lowerBlock = baseLocBlock.getRelative(0, s * -1, 0);
                  if (lowerBlock.getType() == Material.GRASS) {
                     baseLoc = lowerBlock.getLocation();
                     baseLocBlock = lowerBlock;
                     break;
                  }
               }
            }

            EntityType type = (EntityType)this.despawnable.get((int)(Math.random() * 5.0D));
            if (Arrays.asList("MYCEL", "MYCELIUM").contains(baseType.toString())) {
               type = EntityType.MUSHROOM_COW;
            }

            if (baseLocBlock.getBiome() == Biome.JUNGLE || baseLocBlock.getBiome() == Biome.JUNGLE_HILLS) {
               if (baseLocBlock.getRelative(0, 1, 0).getType() == Material.AIR && Math.random() > 0.5D) {
                  type = EntityType.OCELOT;
               }

               return;
            }

            int spawnCount = (int)(Math.random() * 4.0D);
            Location spawnLoc = new Location(baseLoc.getWorld(), baseLoc.getX(), baseLoc.getY() + 1.0D, baseLoc.getZ());

            for(int i = spawnCount; i > 0; --i) {
               Vector vector = new Vector(Math.random() * 3.0D, 0.0D, Math.random());
               baseLoc.getWorld().spawnEntity(spawnLoc, type).setVelocity(vector);
            }
         }

      }
   }

   public Block getRealHighestBlockAt(Location loc) {
      Location highest = loc.getWorld().getHighestBlockAt(loc).getLocation().subtract(0.0D, 1.0D, 0.0D);
      return highest.getBlock();
   }
}
