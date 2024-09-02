package kangarko.olddays.packets.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayServerWorldEvent extends AbstractPacket {
   public static final PacketType TYPE;

   static {
      TYPE = Server.WORLD_EVENT;
   }

   public WrapperPlayServerWorldEvent() {
      super(new PacketContainer(TYPE), TYPE);
      this.handle.getModifier().writeDefaults();
   }

   public WrapperPlayServerWorldEvent(PacketContainer packet) {
      super(packet, TYPE);
   }

   public int getEffectId() {
      return (Integer)this.handle.getIntegers().read(0);
   }

   public void setEffectId(int value) {
      this.handle.getIntegers().write(0, value);
   }

   public BlockPosition getLocation() {
      return (BlockPosition)this.handle.getBlockPositionModifier().read(0);
   }

   public void setLocation(BlockPosition value) {
      this.handle.getBlockPositionModifier().write(0, value);
   }

   public int getData() {
      return (Integer)this.handle.getIntegers().read(1);
   }

   public void setData(int value) {
      this.handle.getIntegers().write(1, value);
   }

   public boolean getDisableRelativeVolume() {
      return (Boolean)this.handle.getBooleans().read(0);
   }

   public void setDisableRelativeVolume(boolean value) {
      this.handle.getBooleans().write(0, value);
   }
}
