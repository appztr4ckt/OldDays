package kangarko.olddays.util;

import kangarko.olddays.OldDays;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Utils {
   private static String debugprefix = "&8[&7OldDays&8]&3 ";
   public static boolean hasNamespacedRecipes = true;

   static {
      try {
         ShapedRecipe.class.getConstructor(NamespacedKey.class, ItemStack.class);
      } catch (ReflectiveOperationException var1) {
         hasNamespacedRecipes = false;
      }

   }

   public static void takeHandItem(PlayerInteractEvent e) {
      Player pl = e.getPlayer();
      ItemStack hand = pl.getItemInHand();
      if (pl.getGameMode() == GameMode.SURVIVAL) {
         if (hand.getAmount() > 1) {
            hand.setAmount(hand.getAmount() - 1);
         } else {
            e.setUseItemInHand(Result.DENY);
            pl.setItemInHand((ItemStack)null);
         }
      }

   }

   public static void debug(Player pl, String msg) {
      if (OldDays.plugin.getConfig().getBoolean("advanced.debug")) {
         pl.sendMessage(colorize(debugprefix + msg));
      }

   }

   public static String colorize(String msg) {
      return ChatColor.translateAlternateColorCodes('&', msg);
   }
}
