package kangarko.olddays.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Status.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import kangarko.olddays.OldDays;
import kangarko.olddays.packets.wrapper.WrapperPlayServerBlockAction;
import kangarko.olddays.packets.wrapper.WrapperPlayServerNamedSoundEffect;
import kangarko.olddays.packets.wrapper.WrapperPlayServerWorldEvent;
import org.bukkit.Material;

public class PacketListener {
   public PacketListener() {
      this.initPacketListener();
   }

   public void initPacketListener() {
      final OldDays instance = OldDays.plugin;
      ProtocolManager pm = ProtocolLibrary.getProtocolManager();
      pm.addPacketListener(new PacketAdapter(instance, new PacketType[]{Server.OUT_SERVER_INFO}) {
         public void onPacketSending(PacketEvent e) {
            if (instance.getConfig().getBoolean("misc.packets.disable-player-sample-in-motd")) {
               ((WrappedServerPing)e.getPacket().getServerPings().read(0)).setPlayers((Iterable)null);
            }

         }
      });
      pm.addPacketListener(new PacketAdapter(instance, new PacketType[]{com.comphenix.protocol.PacketType.Play.Server.BLOCK_ACTION, com.comphenix.protocol.PacketType.Play.Server.NAMED_SOUND_EFFECT}) {
         public void onPacketSending(PacketEvent e) {
            if (instance.getConfig().getBoolean("misc.packets.disable-chest-sound-and-animation")) {
               PacketType packet = e.getPacketType();
               if (packet == com.comphenix.protocol.PacketType.Play.Server.BLOCK_ACTION) {
                  Material mat = (new WrapperPlayServerBlockAction(e.getPacket())).getBlockType();
                  if (mat == Material.CHEST || mat == Material.TRAPPED_CHEST || mat == Material.ENDER_CHEST) {
                     e.setCancelled(true);
                  }
               } else if (packet == com.comphenix.protocol.PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                  String sound = (new WrapperPlayServerNamedSoundEffect(e.getPacket())).getSoundEffect().toString();
                  if (sound.contains("CHEST")) {
                     e.setCancelled(true);
                  }
               }
            }

         }
      });
      pm.addPacketListener(new PacketAdapter(instance, new PacketType[]{com.comphenix.protocol.PacketType.Play.Server.WORLD_EVENT}) {
         public void onPacketSending(PacketEvent e) {
            int effect = (new WrapperPlayServerWorldEvent(e.getPacket())).getEffectId();
            if (instance.getConfig().getBoolean("misc.packets.disable-fall-particles") && (effect == 2006 || effect == 46)) {
               e.setCancelled(true);
            } else if (instance.getConfig().getBoolean("misc.packets.disable-bonemeal-particles") && effect == 2005 || effect == 21) {
               e.setCancelled(true);
            }

         }
      });
   }

   public static class ParticleEffects {
      public static final int SPAWN_SMOKE_PARTICLES = 2000;
      public static final int BLOCK_BREAK = 2001;
      public static final int SPLASH_POTION = 2002;
      public static final int EYE_OF_ENDER = 2003;
      public static final int MOB_SPAWN_EFFECT = 2004;
      public static final int HAPPY_VILLAGER = 2005;
      public static final int FALL_PARTICLES = 2006;
      private static final PacketListener.ParticleEffects INSTANCE = new PacketListener.ParticleEffects();

      public static PacketListener.ParticleEffects getInstance() {
         return INSTANCE;
      }
   }
}
