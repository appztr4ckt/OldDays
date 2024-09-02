package kangarko.olddays.util;

import kangarko.olddays.OldDays;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class AlternativeNoSprint implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void FoodLevelChange(FoodLevelChangeEvent e) {
      if (!OldDays.plugin.isWorldIgnored((Entity)e.getEntity())) {
         e.setFoodLevel(6);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void PlayerRespawn(PlayerRespawnEvent e) {
      if (!OldDays.plugin.isWorldIgnored((Entity)e.getPlayer())) {
         e.getPlayer().setFoodLevel(6);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void PlayerJoin(PlayerJoinEvent e) {
      if (!OldDays.plugin.isWorldIgnored((Entity)e.getPlayer())) {
         e.getPlayer().setFoodLevel(6);
      }
   }
}
