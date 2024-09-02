package kangarko.olddays.packets.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;

public abstract class AbstractPacket {
   protected PacketContainer handle;

   protected AbstractPacket(PacketContainer handle, PacketType type) {
      if (handle == null) {
         throw new IllegalArgumentException("Packet handle cannot be NULL.");
      } else if (!equal(handle.getType(), type)) {
         throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);
      } else {
         this.handle = handle;
      }
   }

   private static boolean equal(Object a, Object b) {
      return a == b || a != null && a.equals(b);
   }

   public PacketContainer getHandle() {
      return this.handle;
   }

   public void sendPacket(Player receiver) {
      try {
         ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, this.getHandle());
      } catch (InvocationTargetException var3) {
         throw new RuntimeException("Cannot send packet.", var3);
      }
   }

   /** @deprecated */
   @Deprecated
   public void recievePacket(Player sender) {
      try {
         ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, this.getHandle());
      } catch (Exception var3) {
         throw new RuntimeException("Cannot recieve packet.", var3);
      }
   }

   public void receivePacket(Player sender) {
      try {
         ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, this.getHandle());
      } catch (Exception var3) {
         throw new RuntimeException("Cannot recieve packet.", var3);
      }
   }
}
