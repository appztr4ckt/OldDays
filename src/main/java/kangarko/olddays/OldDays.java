package kangarko.olddays;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import kangarko.olddays.packets.PacketListener;
import kangarko.olddays.util.AlternativeNoSprint;
import kangarko.olddays.util.ConfigUpdater;
import kangarko.olddays.util.SoundProvider;
import kangarko.olddays.util.Utils;
import me.kangarko.compatbridge.model.CompMaterial;
import me.kangarko.compatbridge.model.CompatBridge;
import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class OldDays extends JavaPlugin implements Listener {
   public static OldDays plugin;
   private final Random rand = new Random();
   private final List<Material> crops;

   public OldDays() {
      this.crops = Arrays.asList(CompMaterial.WHEAT.getMaterial(), Material.POTATO, Material.CARROT, Material.PUMPKIN_STEM, Material.MELON_STEM);
   }

   public void onEnable() {
      plugin = this;
      SoundProvider.doItToIt();
      this.getServer().getPluginManager().registerEvents(this, this);
      this.saveDefaultConfig();
      this.getConfig().options().copyDefaults(true);
      ConfigUpdater.configCheck();
      if (this.getConfig().getBoolean("disable.sprint") && this.getConfig().getBoolean("advanced.alternative-nosprint")) {
         this.getServer().getPluginManager().registerEvents(new AlternativeNoSprint(), this);
      }

      if (this.getConfig().getBoolean("enable.old-animal-spawn")) {
         this.getServer().getPluginManager().registerEvents(new AnimalSpawn(), this);
      }

      if (this.getConfig().getBoolean("disable-recipes.one-point-three-update")) {
         this.registerRecipes();
      }

      if (this.getConfig().getBoolean("misc.packets.enabled")) {
         if (this.getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            this.getLogger().warning("Please install ProtocolLib to enable packet features.");
         } else {
            (new PacketListener()).initPacketListener();
         }
      }

   }

   public void onDisable() {
      plugin = null;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String cL, String[] args) {
      if (args.length == 1) {
         if (!"reload".equalsIgnoreCase(args[0]) && !"r".equalsIgnoreCase(args[0])) {
            sender.sendMessage(ChatColor.GOLD + "OldDays // " + ChatColor.RED + "Invalid command. Usage:");
            sender.sendMessage(ChatColor.GOLD + "OldDays // " + ChatColor.WHITE + "/olddays reload" + ChatColor.GRAY + " // Reload the config");
         } else if (sender.hasPermission("olddays.reload")) {
            this.getServer().getPluginManager().disablePlugin(this);
            this.getServer().getPluginManager().enablePlugin(this);
            this.reloadConfig();
            sender.sendMessage(ChatColor.GOLD + "OldDays // " + ChatColor.GRAY + "Config was reloaded successfully.");
         } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission for this!");
         }
      } else {
         sender.sendMessage(ChatColor.GOLD + "OldDays // " + ChatColor.YELLOW + (this.rand.nextBoolean() ? "Get back to the good old days! " : "Disable new Minecraft features & enable old"));
         sender.sendMessage(ChatColor.GOLD + "OldDays // " + ChatColor.WHITE + "Version: " + ChatColor.GRAY + this.getDescription().getVersion() + ChatColor.WHITE + " by " + ChatColor.YELLOW + "kangarko");
         sender.sendMessage(ChatColor.GOLD + "OldDays // " + ChatColor.WHITE + "Type " + ChatColor.GRAY + "/olddays reload" + ChatColor.WHITE + " to reload the config");
      }

      return true;
   }

   private void registerRecipes() {
      String[][] recipes = new String[][]{{"PAA", "PAA", "PAA"}, {"APA", "APA", "APA"}, {"AAP", "AAP", "AAP"}, {"AAA", "AAA", "PPP"}, {"AAA", "PPP", "AAA"}, {"PPP", "AAA", "AAA"}};
      int count = 0;
      String[][] var6 = recipes;
      int var5 = recipes.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         String[] recipe = var6[var4];
         ItemStack book = new ItemStack(Material.BOOK, 1);
         ShapedRecipe r;
         if (Utils.hasNamespacedRecipes) {
            r = new ShapedRecipe(new NamespacedKey(this, "book_" + count++), book);
         } else {
            r = new ShapedRecipe(book);
         }

         r.shape(recipe);
         r.setIngredient('P', Material.PAPER);
         this.getServer().addRecipe(r);
      }

   }

   public boolean isWorldIgnored(Entity player) {
      return player == null ? true : this.isWorldIgnored(player.getWorld());
   }

   public boolean isWorldIgnored(World world) {
      return this.getConfig().getStringList("disabled-worlds").contains(world.getName());
   }

   @EventHandler
   public void onPlayerKick(PlayerKickEvent e) {
      if (!this.isWorldIgnored((Entity)e.getPlayer())) {
         if (this.getConfig().getBoolean("disable-other.spam-kick") && e.getReason().equalsIgnoreCase("disconnect.spam")) {
            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("disable-other.fly-kick") && e.getReason().equalsIgnoreCase("Flying is not enabled on this server")) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onItemCraftEvent(PrepareItemCraftEvent e) {
      if (e.getInventory() != null && e.getInventory().getHolder() != null && e.getInventory().getResult() != null) {
         Player pl = (Player)e.getInventory().getHolder();
         if (!this.isWorldIgnored((Entity)pl)) {
            Material result = e.getInventory().getResult().getType();

            try {
               if (this.getConfig().getBoolean("disable-recipes.colored-wood-planks") && CompMaterial.OAK_WOOD.is(result) && e.getInventory().getResult().getData().getData() > 0) {
                  e.getInventory().getResult().getData().setData((byte)0);
                  e.getInventory().setResult(new ItemStack(CompMaterial.OAK_WOOD.getMaterial(), 4));
                  pl.updateInventory();
               }

               if (this.getConfig().getBoolean("disable-recipes.horse-update") && (CompMaterial.isHorseArmor(result) || result == Material.HAY_BLOCK || CompMaterial.isLeash(result) || CompMaterial.isCarpet(result) || CompMaterial.isHardClay(result) || result == Material.COAL_BLOCK)) {
                  e.getInventory().setResult((ItemStack)null);
                  pl.updateInventory();
               }

               if (this.getConfig().getBoolean("disable-recipes.redstone-update") && (result == Material.ACTIVATOR_RAIL || result == Material.REDSTONE_BLOCK || result == Material.DAYLIGHT_DETECTOR || result == Material.DROPPER || result == Material.HOPPER || result == Material.HOPPER_MINECART || CompMaterial.TNT_MINECART.is(result) || CompMaterial.COMPARATOR.is(result) || result == Material.TRAPPED_CHEST || CompMaterial.isHeavyPressurePlate(result) || CompMaterial.NETHER_BRICKS.is(result) || result == Material.QUARTZ || result == Material.QUARTZ_BLOCK || result == Material.QUARTZ_STAIRS)) {
                  e.getInventory().setResult((ItemStack)null);
                  pl.updateInventory();
               }

               if (this.getConfig().getBoolean("disable-recipes.pretty-scary-update") && (CompMaterial.isFirework(result) || result == Material.BEACON || result == Material.ANVIL || CompMaterial.FLOWER_POT.is(result) || CompMaterial.COBBLESTONE_WALL.is(result) || result == Material.ITEM_FRAME || CompMaterial.isWoodButton(result) || CompMaterial.CARROT_ON_A_STICK.is(result) || result == Material.PUMPKIN_PIE)) {
                  e.getInventory().setResult((ItemStack)null);
                  pl.updateInventory();
               }

               if (this.getConfig().getBoolean("disable-recipes.one-point-three-update") && (result == Material.EMERALD_BLOCK || result == Material.ENDER_CHEST || result == Material.TRIPWIRE_HOOK || CompMaterial.WRITABLE_BOOK.is(result))) {
                  e.getInventory().setResult((ItemStack)null);
                  pl.updateInventory();
               }

               if (this.getConfig().getBoolean("disable-recipes.one-point-two-update") && (CompMaterial.FIRE_CHARGE.is(result) || CompMaterial.isRedstoneLamp(result) || result == Material.SANDSTONE && e.getInventory().getResult().getData().getData() > 0 || CompMaterial.WRITABLE_BOOK.is(result))) {
                  e.getInventory().setResult((ItemStack)null);
                  pl.updateInventory();
               }

               if (e.isRepair() && this.getConfig().getBoolean("disable-other.item-repair")) {
                  e.getInventory().setResult((ItemStack)null);
                  pl.updateInventory();
               }
            } catch (Exception var5) {
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClick(InventoryClickEvent e) {
      final Player pl = (Player)e.getWhoClicked();
      if (!this.isWorldIgnored((Entity)pl)) {
         boolean cancel = false;
         if (this.getConfig().getStringList("disable-inventory.actions").contains(e.getAction().toString())) {
            cancel = true;
         }

         if (this.getConfig().getStringList("disable-inventory.click-types").contains(e.getClick().toString())) {
            cancel = true;
         }

         if (cancel) {
            e.setResult(Result.DENY);
            (new BukkitRunnable() {
               public void run() {
                  pl.updateInventory();
               }
            }).runTaskLater(this, 1L);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onEnchantEvent(PrepareItemEnchantEvent e) {
      if (e.getInventory() != null && e.getInventory().getHolder() != null) {
         Player pl = (Player)e.getInventory().getHolder();
         if (pl != null) {
            if (!this.isWorldIgnored((Entity)pl)) {
               if (e.getItem().getType() == Material.BOOK && this.getConfig().getBoolean("disable-enchant.book")) {
                  e.setCancelled(true);
               }

            }
         }
      }
   }

   @EventHandler
   public void PlayerJoin(PlayerJoinEvent e) {
      Player pl = e.getPlayer();
      if (!this.isWorldIgnored((Entity)pl)) {
         if (this.getConfig().getBoolean("disable.hunger") && !this.getConfig().getBoolean("advanced.alternative-nosprint")) {
            pl.setFoodLevel(20);
         }

         if (this.getConfig().getBoolean("disable-xp.clear-on-join")) {
            pl.setExp(0.0F);
            pl.setLevel(0);
            Utils.debug(pl, "�6Your XP and exp levels were cleared by OldDays.");
         }

      }
   }

   @EventHandler
   public void onPlayerToggleSprint(PlayerToggleSprintEvent e) {
      Player pl = e.getPlayer();
      if (!this.isWorldIgnored((Entity)pl)) {
         if (this.getConfig().getBoolean("advanced.alternative-nosprint")) {
            pl.setFoodLevel(6);
         } else {
            this.disableSprint(pl, e);
         }
      }

   }

   private void disableSprint(final Player pl, PlayerToggleSprintEvent e) {
      if (this.getConfig().getBoolean("disable.sprint")) {
         pl.setSprinting(false);
         (new BukkitRunnable() {
            public void run() {
               pl.setSprinting(false);
            }
         }).runTaskLater(this, 2L);
         if (!this.getConfig().getString("messages.sprint").equalsIgnoreCase("none")) {
            pl.sendMessage(this.getConfig().getString("messages.sprint").replace("&", "§"));
         }
      }

   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent e) {
      if (!this.isWorldIgnored((Entity)e.getEntity())) {
         if (e.getEntity() instanceof Player) {
            Player pl = (Player)e.getEntity();
            if (this.getConfig().getBoolean("disable.hunger")) {
               if (!this.getConfig().getBoolean("advanced.alternative-nosprint")) {
                  pl.setFoodLevel(20);
               }

               return;
            }

            if (this.getConfig().getBoolean("disable.sprint") && !this.getConfig().getBoolean("advanced.alternative-nosprint")) {
               pl.setFoodLevel(20);
            }
         }

      }
   }

   @EventHandler
   public void onEntityAutoHeal(EntityRegainHealthEvent e) {
      if (!this.isWorldIgnored(e.getEntity())) {
         if (e.getEntity() instanceof Player && this.getConfig().getBoolean("disable.auto-heal") && e.getRegainReason() == RegainReason.SATIATED) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent e) {
      Player pl = e.getPlayer();
      if (!this.isWorldIgnored((Entity)pl)) {
         if (this.getConfig().getBoolean("disable-xp.block-break")) {
            e.setExpToDrop(0);
         }

         if (e.getBlock().getType() == Material.CACTUS && this.getConfig().getBoolean("enable.old-cactus") && pl.getGameMode() == GameMode.SURVIVAL) {
            if (pl.getHealth() - 2.0D > 0.0D) {
               pl.setHealth(pl.getHealth() - 2.0D);
               pl.playSound(pl.getLocation(), SoundProvider.getHurtFleshSound(), 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            } else {
               pl.setHealth(0.0D);
               pl.playSound(pl.getLocation(), SoundProvider.getHurtFleshSound(), 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }
         }

         if (this.getConfig().getBoolean("enable.old-adventure-mode") && pl.getGameMode() == GameMode.ADVENTURE) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onFurnaceSmelt(FurnaceExtractEvent e) {
      if (!this.isWorldIgnored((Entity)e.getPlayer()) && this.getConfig().getBoolean("disable-xp.furnace-smelt")) {
         e.setExpToDrop(0);
      }

   }

   @EventHandler
   public void onEntityKill(EntityDeathEvent e) {
      if (!this.isWorldIgnored((Entity)e.getEntity()) && this.getConfig().getBoolean("disable-xp.entity-kill")) {
         e.setDroppedExp(0);
      }

   }

   @EventHandler
   public void onExpBottleEvent(ExpBottleEvent e) {
      if (!this.isWorldIgnored((Entity)e.getEntity()) && this.getConfig().getBoolean("disable-xp.exp-bottle")) {
         e.setExperience(0);
      }

   }

   @EventHandler
   public void onPlayerFish(PlayerFishEvent e) {
      if (!this.isWorldIgnored((Entity)e.getPlayer()) && this.getConfig().getBoolean("disable-xp.fishing")) {
         e.setExpToDrop(0);
      }

   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent e) {
      if (!this.isWorldIgnored((Entity)e.getEntity())) {
         if (this.getConfig().getBoolean("disable-xp.player-death")) {
            e.setDroppedExp(0);
         }

         if (this.getConfig().getBoolean("disable.new-death-messages")) {
            String hrac = e.getEntity().getName();
            String sprava = e.getDeathMessage();
            if (sprava.contains("fell off a ladder")) {
               e.setDeathMessage(hrac + " hit the ground too hard");
            }

            if (sprava.contains("fell off some vines")) {
               e.setDeathMessage(hrac + " hit the ground too hard");
            }

            if (sprava.contains("fell out of the water")) {
               e.setDeathMessage(hrac + " hit the ground too hard");
            }

            if (sprava.contains("fell from a high place")) {
               e.setDeathMessage(hrac + " hit the ground too hard");
            }

            if (sprava.contains("was doomed to fall")) {
               e.setDeathMessage(hrac + " hit the ground too hard");
            }

            if (sprava.contains("fell into a patch of fire")) {
               e.setDeathMessage(hrac + " hit the ground too hard");
            }

            if (sprava.contains("fell into a patch of cacti")) {
               e.setDeathMessage(hrac + " hit the ground too hard");
            }

            if (sprava.contains("got finished off by")) {
               e.setDeathMessage(hrac + " was slain by " + e.getEntity().getKiller().getName());
            }

            if (sprava.contains("walked into a fire")) {
               e.setDeathMessage(hrac + " went up in flames");
            }

            if (sprava.contains("was burnt to a crisp")) {
               e.setDeathMessage(hrac + " burned to death");
            }

            if (sprava.contains("tried to swim in lava")) {
               e.setDeathMessage(hrac + " tried to swim in lava");
            }

            if (sprava.contains("walked into a cactus")) {
               e.setDeathMessage(hrac + " was pricked to death");
            }

            if (sprava.contains("was killed trying to hurt")) {
               e.setDeathMessage(hrac + " was slain by " + e.getEntity().getKiller().getName());
            }
         }

         if (e.getDeathMessage().contains("died") && this.getConfig().getBoolean("enable-sounds.old-fall-damage")) {
            e.setDeathMessage(e.getEntity().getName() + " hit the ground too hard");
         }

      }
   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent e) {
      LivingEntity en = e.getEntity();
      if (!this.isWorldIgnored((Entity)en)) {
         EntityType typ = e.getEntityType();
         if (this.getConfig().getStringList("disable-spawn-list").contains(typ.toString())) {
            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("disable-spawn.zombie-villager") && en instanceof Zombie && ((Zombie)en).isVillager()) {
            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("disable-spawn.baby-zombies")) {
            if (typ != EntityType.ZOMBIE && typ != EntityType.ZOMBIE_VILLAGER) {
               if (typ == EntityType.PIG_ZOMBIE) {
                  ((PigZombie)en).setBaby(false);
               }
            } else {
               ((Zombie)en).setBaby(false);
            }
         }

         if (this.getConfig().getBoolean("disable-spawn.wither-skeleton") && en instanceof Skeleton && ((Skeleton)en).getSkeletonType() == SkeletonType.WITHER) {
            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("disable-other.monsters-wearing-armor")) {
            if (typ != EntityType.SKELETON) {
               en.getEquipment().clear();
            } else {
               en.getEquipment().clear();
               en.getEquipment().setItemInHand(new ItemStack(Material.BOW, 1));
            }

            en.setCanPickupItems(false);
         }

         if (this.getConfig().getBoolean("disable-other.zombie-sieges") && e.getSpawnReason() == SpawnReason.VILLAGE_INVASION) {
            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("disable-other.zombie-reinforcements") && e.getSpawnReason() == SpawnReason.REINFORCEMENTS) {
            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("enable.old-mob-health")) {
            if (typ == EntityType.SHEEP) {
               en.setHealth(8.0D);
            } else if (typ == EntityType.ZOMBIE) {
               en.setHealth(20.0D);
            }
         }

         if (this.getConfig().getBoolean("disable-other.spider-with-potions") && typ == EntityType.SPIDER) {
            Iterator var5 = en.getActivePotionEffects().iterator();

            while(var5.hasNext()) {
               PotionEffect potion = (PotionEffect)var5.next();
               en.removePotionEffect(potion.getType());
            }
         }

         if (this.getConfig().getBoolean("disable.breeding") && e.getSpawnReason() == SpawnReason.BREEDING) {
            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("disable-spawn.baby-animals-from-eggs") && e.getSpawnReason() == SpawnReason.SPAWNER_EGG && en instanceof Ageable && !((Ageable)en).isAdult()) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public void onEntityTarget(EntityTargetLivingEntityEvent e) {
      if (!this.isWorldIgnored(e.getEntity())) {
         if (e.getTarget() != null && e.getEntity() != null) {
            if (this.getConfig().getBoolean("disable.breeding") && e.getTarget() instanceof Player && e.getEntity() instanceof Ageable) {
               e.setCancelled(true);
            }

         }
      }
   }

   @EventHandler
   public void onEntityPortal(EntityPortalEvent e) {
      if (!this.isWorldIgnored(e.getEntity())) {
         if (this.getConfig().getBoolean("disable-other.entity-entering-portal") && !(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onEntityDrop(EntityDeathEvent e) {
      if (!this.isWorldIgnored((Entity)e.getEntity())) {
         int choice;
         if (this.getConfig().getBoolean("disable-new-drops.zombie") && e.getEntityType() == EntityType.ZOMBIE) {
            e.getDrops().clear();
            choice = this.rand.nextInt(4);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(Material.FEATHER, 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(Material.FEATHER, 2));
            }
         }

         if (this.getConfig().getBoolean("disable-new-drops.chicken") && e.getEntityType() == EntityType.CHICKEN) {
            e.getDrops().clear();
            choice = this.rand.nextInt(3);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(Material.FEATHER, 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(Material.FEATHER, 2));
            }
         }

         if (this.getConfig().getBoolean("disable-new-drops.cow") && e.getEntityType() == EntityType.COW) {
            e.getDrops().clear();
            choice = this.rand.nextInt(3);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(Material.LEATHER, 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(Material.LEATHER, 2));
            }
         }

         if (this.getConfig().getBoolean("disable-new-drops.spider") && (e.getEntityType() == EntityType.SPIDER || e.getEntityType() == EntityType.CAVE_SPIDER)) {
            e.getDrops().clear();
            choice = this.rand.nextInt(2);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(Material.STRING, 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(Material.STRING, 2));
            }
         }

         if (this.getConfig().getBoolean("disable-new-drops.pig") && e.getEntityType() == EntityType.PIG) {
            e.getDrops().clear();
            choice = this.rand.nextInt(2);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(CompMaterial.PORKCHOP.getMaterial(), 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(CompMaterial.PORKCHOP.getMaterial(), 2));
            }
         }

         if (this.getConfig().getBoolean("disable-new-drops.ghast") && e.getEntityType() == EntityType.GHAST) {
            e.getDrops().clear();
            choice = this.rand.nextInt(2);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(CompMaterial.GUNPOWDER.getMaterial(), 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(CompMaterial.GUNPOWDER.getMaterial(), 2));
            }
         }

         if (this.getConfig().getBoolean("disable-new-drops.zombie-pigman") && e.getEntityType() == EntityType.PIG_ZOMBIE) {
            e.getDrops().clear();
            choice = this.rand.nextInt(2);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(CompMaterial.COOKED_PORKCHOP.getMaterial(), 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(CompMaterial.COOKED_PORKCHOP.getMaterial(), 2));
            }
         }

         if (this.getConfig().getBoolean("disable-new-drops.blaze") && e.getEntityType() == EntityType.BLAZE) {
            e.getDrops().clear();
            choice = this.rand.nextInt(2);
            if (choice == 0) {
               e.getDrops().add(new ItemStack(Material.GLOWSTONE_DUST, 1));
            } else if (choice == 1) {
               e.getDrops().add(new ItemStack(Material.GLOWSTONE_DUST, 2));
            }
         }

      }
   }

   @EventHandler
   public void onEntityShootBow(EntityShootBowEvent e) {
      if (!this.isWorldIgnored((Entity)e.getEntity())) {
         if (e.getEntity() instanceof Player && this.getConfig().getBoolean("enable.old-bow")) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent e) {
      Player pl = e.getPlayer();
      if (!this.isWorldIgnored((Entity)pl)) {
         if (this.getConfig().getBoolean("disable-other.nether-wart-farming-outside-nether") && e.getBlockPlaced().getType() == CompMaterial.NETHER_WART.getMaterial()) {
            if (this.getConfig().getString("advanced.nether-detection").equalsIgnoreCase("normal") && pl.getWorld().getEnvironment() != Environment.NETHER) {
               if (this.getConfig().getString("messages.block-nether-warth") != "none") {
                  pl.sendMessage(this.getConfig().getString("messages.block-nether-warth").replace("&", "§"));
               }

               e.setCancelled(true);
            } else if (this.getConfig().getString("advanced.nether-detection").equalsIgnoreCase("name") && !e.getBlockPlaced().getWorld().getName().contains("nether")) {
               if (this.getConfig().getString("messages.block-nether-warth") != "none") {
                  pl.sendMessage(this.getConfig().getString("messages.block-nether-warth").replace("&", "§"));
               }

               e.setCancelled(true);
            }
         }

         if (this.getConfig().getBoolean("disable-placing.string") && e.getBlockPlaced().getType() == Material.TRIPWIRE) {
            if (!this.getConfig().getString("messages.block-place").equalsIgnoreCase("none")) {
               pl.sendMessage(this.getConfig().getString("messages.block-place").replace("&", "§").replace("%BLOCK", "string"));
            }

            e.setCancelled(true);
         }

         if (this.getConfig().getBoolean("disable.rotated-logs") && CompMaterial.isLog(e.getBlockPlaced().getType())) {
            byte data = e.getBlockPlaced().getData();
            Block block = e.getBlockPlaced().getWorld().getBlockAt(e.getBlockPlaced().getLocation());
            if (data != 4 && data != 8 && data != 12) {
               if (data != 5 && data != 9 && data != 13) {
                  if (data != 6 && data != 10 && data != 14) {
                     if (data == 7 || data == 11 || data == 15) {
                        CompatBridge.setData(block, 3);
                     }
                  } else {
                     CompatBridge.setData(block, 2);
                  }
               } else {
                  CompatBridge.setData(block, 1);
               }
            } else {
               CompatBridge.setData(block, 0);
            }
         }

      }
   }

   @EventHandler
   public void onEntityBreakDoor(EntityBreakDoorEvent e) {
      if (!this.isWorldIgnored((Entity)e.getEntity())) {
         if (this.getConfig().getBoolean("disable-other.zombie-break-door")) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onPlayerClickEntity(PlayerInteractEntityEvent e) {
      Player pl = e.getPlayer();
      if (!this.isWorldIgnored((Entity)pl)) {
         Entity en = e.getRightClicked();
         if (this.getConfig().getBoolean("disable-other.villager-trading") && en.getType() == EntityType.VILLAGER) {
            if (pl.getItemInHand().getType() == Material.BOW && pl.getInventory().contains(Material.ARROW)) {
               pl.launchProjectile(Arrow.class);
               pl.getWorld().playSound(pl.getLocation(), SoundProvider.getShootArrowSound(), 1.0F, 0.0F);
               int sip = pl.getInventory().first(Material.ARROW);
               ItemStack stakSipov = pl.getInventory().getItem(sip);
               if (stakSipov.getAmount() > 1) {
                  stakSipov.setAmount(stakSipov.getAmount() - 1);
               } else if (stakSipov.getAmount() <= 1) {
                  pl.getInventory().clear(sip);
               }
            } else if (!this.getConfig().getString("messages.trade").equalsIgnoreCase("none")) {
               pl.sendMessage(this.getConfig().getString("messages.trade").replace("&", "§"));
            }

            e.setCancelled(true);
         }

         if (en.getType() == EntityType.HORSE && this.getConfig().getBoolean("disable-other.horse-rightclick")) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onPunchingSheep(EntityDamageByEntityEvent e) {
      if (!this.isWorldIgnored(e.getEntity())) {
         if (e.getDamager() instanceof Player) {
            Player pl = (Player)e.getDamager();
            Entity en = e.getEntity();
            if (en.getType() == EntityType.SHEEP && !((Sheep)en).isSheared() && ((Sheep)en).isAdult() && this.getConfig().getBoolean("enable.getting-wool-by-sheep-punching")) {
               ((Sheep)en).setSheared(true);
               byte farbaVlny = ((Sheep)en).getColor().getDyeData();
               int choice = this.rand.nextInt(3);
               if (choice == 0) {
                  en.getWorld().dropItemNaturally(en.getLocation(), CompMaterial.makeWool(farbaVlny, 1));
               } else if (choice == 1) {
                  en.getWorld().dropItemNaturally(en.getLocation(), CompMaterial.makeWool(farbaVlny, 2));
               } else if (choice == 2) {
                  en.getWorld().dropItemNaturally(en.getLocation(), CompMaterial.makeWool(farbaVlny, 3));
               } else if (choice == 3) {
                  en.getWorld().dropItemNaturally(en.getLocation(), CompMaterial.makeWool(farbaVlny, 2));
               }
            }

            if (this.getConfig().getBoolean("enable.old-sword-damage")) {
               if (pl.getItemInHand().getType() == Material.DIAMOND_SWORD) {
                  e.setDamage(10.0D);
               }

               if (pl.getItemInHand().getType() == Material.IRON_SWORD) {
                  e.setDamage(8.0D);
               }
            }

            if (pl.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) && this.getConfig().getBoolean("enable.old-strength-potion")) {
               Iterator var10 = pl.getActivePotionEffects().iterator();

               while(var10.hasNext()) {
                  PotionEffect Effect = (PotionEffect)var10.next();
                  if (Effect.getType() == PotionEffectType.INCREASE_DAMAGE) {
                     double percentage = (double)(Effect.getAmplifier() + 1) * 1.3D + 1.0D;
                     int finalDmg;
                     if (e.getDamage() / percentage <= 1.0D) {
                        finalDmg = (Effect.getAmplifier() + 1) * 3 + 1;
                     } else {
                        finalDmg = (int)(e.getDamage() / percentage) + (Effect.getAmplifier() + 1) * 3;
                     }

                     e.setDamage((double)finalDmg);
                     break;
                  }
               }
            }

         }
      }
   }

   @EventHandler
   public void onPlayerShearSheep(PlayerShearEntityEvent e) {
      if (!this.isWorldIgnored((Entity)e.getPlayer())) {
         if (this.getConfig().getBoolean("enable.getting-wool-by-sheep-punching") && e.getEntity().getType() == EntityType.SHEEP) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onPlayerConsume(PlayerItemConsumeEvent e) {
      if (!this.isWorldIgnored((Entity)e.getPlayer())) {
         if (this.getConfig().getBoolean("disable.raw-chicken-poison") && !this.getConfig().getBoolean("disable.hunger") && CompMaterial.CHICKEN.is(e.getItem().getType()) && e.getPlayer().hasPotionEffect(PotionEffectType.HUNGER)) {
            e.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
         }

      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent e) {
      Player pl = e.getPlayer();
      if (!this.isWorldIgnored((Entity)pl)) {
         if (this.getConfig().getBoolean("disable-other.potion-effects") && pl.getItemInHand().getType() == Material.POTION) {
            e.setCancelled(true);
            Iterator var4 = pl.getActivePotionEffects().iterator();

            while(var4.hasNext()) {
               PotionEffect potion = (PotionEffect)var4.next();
               pl.removePotionEffect(potion.getType());
            }
         }

         if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (this.getConfig().getBoolean("enable.old-bow") && pl.getItemInHand().getType() == Material.BOW) {
               if (pl.getGameMode() == GameMode.SURVIVAL && !pl.getInventory().contains(Material.ARROW)) {
                  return;
               }

               pl.getItemInHand().setDurability((short)0);
               pl.launchProjectile(Arrow.class);
               pl.getWorld().playSound(pl.getLocation(), SoundProvider.getShootArrowSound(), 1.0F, 0.0F);
               if (pl.getGameMode() == GameMode.SURVIVAL) {
                  int sip = pl.getInventory().first(Material.ARROW);
                  ItemStack stakSipov = pl.getInventory().getItem(sip);
                  if (stakSipov.getAmount() > 1) {
                     stakSipov.setAmount(stakSipov.getAmount() - 1);
                  } else if (stakSipov.getAmount() <= 1) {
                     pl.getInventory().clear(sip);
                  }
               }
            }

            if (this.getConfig().getBoolean("disable-other.ender-pearl") && pl.getItemInHand().getType() == Material.ENDER_PEARL) {
               e.setCancelled(true);
            }

            if (this.getConfig().getBoolean("disable-spawn.from-mob-eggs") && CompMaterial.isMonsterEgg(pl.getItemInHand().getType())) {
               if (!this.getConfig().getString("messages.block-spawner-eggs").equalsIgnoreCase("none")) {
                  pl.sendMessage(this.getConfig().getString("messages.block-spawner-eggs").replace("&", "§"));
               }

               e.setCancelled(true);
            }
         }

         Material clickedType = e.hasBlock() ? e.getClickedBlock().getType() : null;
         if (this.getConfig().getBoolean("enable.quartz-ore-to-netherrack") && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) && CompMaterial.NETHER_QUARTZ_ORE.is(clickedType) && this.getConfig().getString("advanced.nether-detection").equalsIgnoreCase("normal")) {
            if (e.getClickedBlock().getWorld().getEnvironment() == Environment.NETHER) {
               e.getClickedBlock().setType(Material.NETHERRACK);
            } else if (this.getConfig().getString("advanced.nether-detection").equalsIgnoreCase("name") && e.getClickedBlock().getWorld().getName().contains("nether")) {
               e.getClickedBlock().setType(Material.NETHERRACK);
            }
         }

         if (this.getConfig().getBoolean("enable.punching-tnt") && e.getAction() == Action.LEFT_CLICK_BLOCK && clickedType == Material.TNT) {
            Location loc = e.getClickedBlock().getLocation();
            loc.setX(loc.getX() + 0.5D);
            loc.setY(loc.getY() + 0.6D);
            loc.setZ(loc.getZ() + 0.5D);
            pl.getWorld().spawn(loc, TNTPrimed.class);
            pl.playSound(loc, SoundProvider.getFuseSound(), 1.0F, 1.0F);
            e.getClickedBlock().setType(Material.AIR);
            e.setCancelled(true);
         }

         if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (this.getConfig().getStringList("disable-open").contains(clickedType.toString())) {
               if (this.getConfig().getString("messages.block-click") != "none") {
                  pl.sendMessage(this.getConfig().getString("messages.block-click").replace("&", "§"));
               }

               e.setCancelled(true);
            }

            if (this.getConfig().getBoolean("disable-other.snow-layers") && clickedType == Material.SNOW && pl.getItemInHand().getType() == Material.SNOW) {
               e.setCancelled(true);
            }

            if (this.getConfig().getBoolean("disable-placing.cocoa-beans") && CompMaterial.INK_SAC.is(pl.getItemInHand()) && pl.getItemInHand().getData().getData() == 3 && CompMaterial.isLog(clickedType)) {
               if (!this.getConfig().getString("messages.block-place").equalsIgnoreCase("none")) {
                  pl.sendMessage(this.getConfig().getString("messages.block-place").replace("&", "§").replace("%BLOCK", "cocoa plant"));
               }

               e.setCancelled(true);
            }

            if (CompMaterial.BONE_MEAL.is(pl.getItemInHand())) {
               Block clickedBlok = e.getClickedBlock();
               int posY;
               if (this.getConfig().getBoolean("enable.instant-bonemeal")) {
                  if (!CompMaterial.WHEAT.is(clickedType) && clickedType != Material.POTATO && clickedType != Material.CARROT && clickedType != Material.PUMPKIN_STEM && clickedType != Material.MELON_STEM) {
                     if (clickedBlok.getType() == Material.COCOA) {
                        MaterialData data = new CocoaPlant(clickedBlok.getType(), clickedBlok.getData());
                        ((CocoaPlant)data).setSize(CocoaPlantSize.LARGE);
                        CompatBridge.setData(clickedBlok, ((Crops)data).getData());
                        Utils.takeHandItem(e);
                     } else if (CompMaterial.isSapling(clickedBlok.getType())) {
                        MaterialData data = new Tree(clickedBlok.getType(), clickedBlok.getData());
                        TreeSpecies type = ((Tree)data).getSpecies();
                        TreeType treetype = null;
                        if (type == TreeSpecies.BIRCH) {
                           treetype = TreeType.BIRCH;
                        } else if (type == TreeSpecies.JUNGLE) {
                           JungleTree.junglestrom(clickedBlok);
                        } else if (type == TreeSpecies.REDWOOD) {
                           treetype = TreeType.REDWOOD;
                        } else if (type == TreeSpecies.GENERIC) {
                           posY = this.rand.nextInt(8);
                           if (posY == 0) {
                              treetype = TreeType.BIG_TREE;
                           } else {
                              treetype = TreeType.TREE;
                           }
                        }

                        if (type != TreeSpecies.JUNGLE) {
                           clickedBlok.setType(Material.AIR);
                           if (!clickedBlok.getWorld().generateTree(clickedBlok.getLocation(), treetype)) {
                              clickedBlok.setType(CompMaterial.OAK_SAPLING.getMaterial());
                           }
                        }

                        Utils.takeHandItem(e);
                     }
                  } else {
                     MaterialData data = new Crops(clickedBlok.getType(), clickedBlok.getData());
                     ((Crops)data).setState(CropState.RIPE);
                     CompatBridge.setData(clickedBlok, data.getData());
                     Utils.takeHandItem(e);
                  }
               }

               if (this.getConfig().getBoolean("enable.old-bonemeal-on-grass")) {
                  if (clickedType == Material.GRASS) {
                     World world = pl.getWorld();

                     label606:
                     for(int tries = 0; tries < 128; ++tries) {
                        int posX = clickedBlok.getX();
                        posY = clickedBlok.getY() + 1;
                        int posZ = clickedBlok.getZ();

                        for(int i = 0; i < tries / 16; ++i) {
                           posX += this.rand.nextInt(3) - 1;
                           posY += (this.rand.nextInt(3) - 1) * this.rand.nextInt(3) / 2;
                           posZ += this.rand.nextInt(3) - 1;
                           if (world.getBlockAt(posX, posY - 1, posZ).getType() != Material.GRASS) {
                              continue label606;
                           }
                        }

                        if (world.getBlockAt(posX, posY, posZ).getType() == Material.AIR) {
                           if (this.rand.nextInt(10) != 0) {
                              CompatBridge.setTypeAndData(world.getBlockAt(posX, posY, posZ), (CompMaterial)CompMaterial.TALL_GRASS, (byte)1);
                           } else if (this.rand.nextInt(3) != 0) {
                              world.getBlockAt(posX, posY, posZ).setType(CompMaterial.DANDELION_YELLOW.getMaterial(), true);
                           } else {
                              world.getBlockAt(posX, posY, posZ).setType(CompMaterial.ROSE_RED.getMaterial(), true);
                           }
                        }
                     }

                     Utils.takeHandItem(e);
                     e.setCancelled(true);
                  } else if (clickedType == CompMaterial.TALL_GRASS.getMaterial() || CompMaterial.isDoublePlant(clickedType)) {
                     e.setCancelled(true);
                  }
               }
            }
         }

         if (this.getConfig().getBoolean("enable.instant-food")) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
               if (clickedType != CompMaterial.FARMLAND.getMaterial()) {
                  if (pl.getItemInHand().getType() == CompMaterial.POTATO.getMaterial()) {
                     if (pl.getHealth() < 20.0D && pl.getHealth() <= 19.0D) {
                        pl.setHealth(pl.getHealth() + 1.0D);
                     }

                     if (pl.getItemInHand().getAmount() > 1) {
                        pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                     } else if (pl.getItemInHand().getAmount() == 1) {
                        e.setUseItemInHand(Result.DENY);
                        pl.getInventory().setItemInHand((ItemStack)null);
                     }
                  }

                  if (pl.getItemInHand().getType() == CompMaterial.CARROT.getMaterial()) {
                     if (pl.getHealth() < 20.0D) {
                        if (pl.getHealth() + 2.0D > 20.0D) {
                           pl.setHealth(20.0D);
                        } else if (pl.getHealth() + 2.0D <= 20.0D) {
                           pl.setHealth(pl.getHealth() + 2.0D);
                        }
                     }

                     if (pl.getItemInHand().getAmount() > 1) {
                        pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                     } else if (pl.getItemInHand().getAmount() == 1) {
                        e.setUseItemInHand(Result.DENY);
                        pl.getInventory().setItemInHand((ItemStack)null);
                     }
                  }
               }

               if (pl.getItemInHand().getType() == Material.APPLE) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 4.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 4.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 4.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.BAKED_POTATO) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 4.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 4.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 4.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.BREAD) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 4.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 4.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 4.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.COOKED_CHICKEN) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 6.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 6.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 6.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == CompMaterial.COOKED_COD.getMaterial()) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 6.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 6.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 6.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.COOKED_BEEF) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 8.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 8.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 8.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.COOKIE) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 2.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 2.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 2.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.GOLDEN_APPLE) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 10.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 10.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 10.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.GOLDEN_CARROT) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 9.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 9.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 9.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == CompMaterial.COOKED_PORKCHOP.getMaterial()) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 8.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 8.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 8.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.MELON) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 1.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 1.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 1.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == CompMaterial.MUSHROOM_STEW.getMaterial()) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 4.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 4.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 4.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().getItemInHand().setType(Material.BOWL);
                  }
               }

               if (pl.getItemInHand().getType() == Material.PUMPKIN_PIE) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 6.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 6.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 6.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == CompMaterial.BEEF.getMaterial()) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 3.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 3.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 3.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == CompMaterial.CHICKEN.getMaterial()) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 3.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 3.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 3.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == CompMaterial.COD.getMaterial()) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 3.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 3.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 3.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == CompMaterial.PORKCHOP.getMaterial()) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 3.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 3.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 3.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.ROTTEN_FLESH) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 1.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 1.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 1.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.SPIDER_EYE) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 2.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 2.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 2.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }

               if (pl.getItemInHand().getType() == Material.POISONOUS_POTATO) {
                  if (pl.getHealth() < 20.0D) {
                     if (pl.getHealth() + 1.0D > 20.0D) {
                        pl.setHealth(20.0D);
                     } else if (pl.getHealth() + 1.0D <= 20.0D) {
                        pl.setHealth(pl.getHealth() + 1.0D);
                     }
                  }

                  if (pl.getItemInHand().getAmount() > 1) {
                     pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
                  } else if (pl.getItemInHand().getAmount() == 1) {
                     e.setUseItemInHand(Result.DENY);
                     pl.getInventory().setItemInHand((ItemStack)null);
                  }
               }
            }

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
               if (clickedType != CompMaterial.CAKE.getMaterial()) {
                  return;
               }

               if (pl.getHealth() < 20.0D) {
                  if (pl.getHealth() + 2.0D > 20.0D) {
                     pl.setHealth(20.0D);
                  } else if (pl.getHealth() + 2.0D <= 20.0D) {
                     pl.setHealth(pl.getHealth() + 2.0D);
                  }
               }

               if (pl.getItemInHand().getAmount() > 1) {
                  pl.getItemInHand().setAmount(pl.getItemInHand().getAmount() - 1);
               } else if (pl.getItemInHand().getAmount() == 1) {
                  e.setUseItemInHand(Result.DENY);
                  pl.getInventory().setItemInHand((ItemStack)null);
               }
            }
         }

      }
   }

   @EventHandler
   public void onEntityCombust(EntityCombustByEntityEvent e) {
      if (!this.isWorldIgnored(e.getEntity())) {
         if (this.getConfig().getBoolean("disable-other.zombie-setting-player-on-fire") && e.getEntity() instanceof Player && e.getCombuster().getType() == EntityType.ZOMBIE) {
            e.setCancelled(true);
         }

      }
   }

   public void fallTree(Block block) {
      FallingBlock fallingBlock = CompatBridge.spawnFallingBlock(block.getWorld(), block.getLocation(), block.getType(), block.getData());
      fallingBlock.setDropItem(true);
      block.setType(Material.AIR);
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void treeChopping(BlockBreakEvent e) {
      if (!this.isWorldIgnored((Entity)e.getPlayer())) {
         Block blok = e.getBlock();
         if (this.getConfig().getBoolean("advanced.tree-physics") && CompMaterial.isLog(blok.getType())) {
            Block bottom;
            for(bottom = blok; CompMaterial.isLog(bottom.getType()); bottom = bottom.getRelative(BlockFace.DOWN)) {
            }

            if (bottom.getType() == Material.DIRT || bottom.getType() == Material.GRASS) {
               for(Block above = blok.getRelative(BlockFace.UP); CompMaterial.isLog(above.getType()); above = above.getRelative(BlockFace.UP)) {
                  this.fallTree(above);
               }
            }
         } else if (this.crops.contains(blok.getType())) {
            Crops crops = new Crops(blok.getType(), blok.getData());
            boolean cancel = false;
            if (crops.getState() == CropState.SEEDED && this.rand.nextDouble() > this.getConfig().getDouble("misc.drop-chances.crops.first-stage")) {
               cancel = true;
            } else if (crops.getState() == CropState.GERMINATED && this.rand.nextDouble() > this.getConfig().getDouble("misc.drop-chances.crops.second-stage")) {
               cancel = true;
            } else if (crops.getState() == CropState.VERY_SMALL && this.rand.nextDouble() > this.getConfig().getDouble("misc.drop-chances.crops.third-stage")) {
               cancel = true;
            } else if (crops.getState() == CropState.SMALL && this.rand.nextDouble() > this.getConfig().getDouble("misc.drop-chances.crops.fourth-stage")) {
               cancel = true;
            }

            if (cancel) {
               e.setCancelled(true);
               blok.setType(Material.AIR);
            }
         }

      }
   }
}
