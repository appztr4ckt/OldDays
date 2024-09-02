package kangarko.olddays.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import kangarko.olddays.OldDays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ConfigUpdater {
   private static ConfigUpdater.Status status;
   private static String latestVersion;

   static {
      latestVersion = OldDays.plugin.getDescription().getVersion();
   }

   public static void configCheck() {
      String plVersion = OldDays.plugin.getConfig().getString("version");
      if (!OldDays.plugin.getConfig().getBoolean("advanced.update-config")) {
         status = ConfigUpdater.Status.DISABLED;
      } else if (!latestVersion.contains("SNAPSHOT") && !latestVersion.contains("DEV") && !plVersion.contains("SNAPSHOT") && !plVersion.contains("DEV")) {
         if (!latestVersion.equals(plVersion)) {
            try {
               if (plVersion.equals("1.2.4")) {
                  updateConfigTo125();
               } else if (Integer.valueOf(plVersion.replace(".", "")) < 124) {
                  status = ConfigUpdater.Status.TOO_OLD;
               } else if (Integer.valueOf(plVersion.replace(".", "")) > Integer.valueOf(latestVersion.replace(".", ""))) {
                  status = ConfigUpdater.Status.INVALID;
               }
            } catch (Exception var10) {
               status = ConfigUpdater.Status.ERROR;
               Logger.getLogger("Minecraft").log(Level.WARNING, "&cUnable to update OldDays configuration.", var10);
            } finally {
               try {
                  OldDays.plugin.saveConfig();
                  status = ConfigUpdater.Status.SUCCESS;
               } catch (Exception var9) {
                  var9.printStackTrace();
                  status = ConfigUpdater.Status.ERROR;
               }

            }
         } else {
            status = ConfigUpdater.Status.UPDATE_NOT_NECESSARY;
         }

         if (status != null) {
            if (!status.equals(ConfigUpdater.Status.DISABLED) && !status.equals(ConfigUpdater.Status.UPDATE_NOT_NECESSARY)) {
               Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', status.msg));
               if (status.equals(ConfigUpdater.Status.SUCCESS)) {
                  updateVersionMark();
               }
            }

         }
      } else {
         status = ConfigUpdater.Status.DISABLED;
         System.out.println("No config update on snapshot, happy testing!");
      }
   }

   private static void updateConfigTo125() {
      setBoolean("advanced.old-bonemeal-on-grass");
      set("misc.drop-chances.crops.first-stage", 0.2D);
      set("misc.drop-chances.crops.second-stage", 0.3D);
      set("misc.drop-chances.crops.third-stage", 0.55D);
      set("misc.drop-chances.crops.fourth-stage", 0.75D);
      setBoolean("misc.packets.enabled");
      setBoolean("misc.packets.disable-player-sample-in-motd");
      setBoolean("misc.packets.disable-chest-sound-and-animation");
      setBoolean("misc.packets.disable-fall-particles");
      setBoolean("misc.packets.disable-bonemeal-particles");
   }

   private static void updateVersionMark() {
      try {
         OldDays.plugin.getConfig().set("version", latestVersion);
         OldDays.plugin.getConfig().save(new File(OldDays.plugin.getDataFolder(), "config.yml"));
      } catch (Exception var1) {
         Logger.getLogger("Minecraft").log(Level.WARNING, "&cUnable to update OldDays configuration version mark.", var1);
      }

   }

   private static void set(String path, Object value) {
      OldDays.plugin.getConfig().set(path, value);
   }

   private static void setBoolean(String path) {
      set(path, true);
   }

   private static enum Status {
      SUCCESS("&aConfiguration was updated for version " + OldDays.plugin.getDescription().getVersion()),
      ERROR("&c Config was NOT updated! Please regenerate it."),
      TOO_OLD("&4Your OldDays version is too old. We cannot update your configuration. Consider regenerating your config.yml."),
      INVALID("&4!!Critical Warning!! &cYour configuration seems to be invalid! Consider regenerating it before any damage occur."),
      DISABLED,
      UPDATE_NOT_NECESSARY;

      String msg;

      private Status() {
      }

      private Status(String msg) {
         this.msg = msg;
      }
   }
}
