package kangarko.olddays.packets.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.SoundCategory;
import org.bukkit.Sound;

public class WrapperPlayServerNamedSoundEffect extends AbstractPacket {
   public static final PacketType TYPE;

   static {
      TYPE = Server.NAMED_SOUND_EFFECT;
   }

   public WrapperPlayServerNamedSoundEffect() {
      super(new PacketContainer(TYPE), TYPE);
      this.handle.getModifier().writeDefaults();
   }

   public WrapperPlayServerNamedSoundEffect(PacketContainer packet) {
      super(packet, TYPE);
   }

   public Sound getSoundEffect() {
      return (Sound)this.handle.getSoundEffects().read(0);
   }

   public void setSoundEffect(Sound value) {
      this.handle.getSoundEffects().write(0, value);
   }

   public SoundCategory getSoundCategory() {
      return (SoundCategory)this.handle.getSoundCategories().read(0);
   }

   public void setSoundCategory(SoundCategory value) {
      this.handle.getSoundCategories().write(0, value);
   }

   public int getEffectPositionX() {
      return (Integer)this.handle.getIntegers().read(0);
   }

   public void setEffectPositionX(int value) {
      this.handle.getIntegers().write(0, value);
   }

   public int getEffectPositionY() {
      return (Integer)this.handle.getIntegers().read(1);
   }

   public void setEffectPositionY(int value) {
      this.handle.getIntegers().write(1, value);
   }

   public int getEffectPositionZ() {
      return (Integer)this.handle.getIntegers().read(2);
   }

   public void setEffectPositionZ(int value) {
      this.handle.getIntegers().write(2, value);
   }

   public float getVolume() {
      return (Float)this.handle.getFloat().read(0);
   }

   public void setVolume(float value) {
      this.handle.getFloat().write(0, value);
   }

   public float getPitch() {
      return (Float)this.handle.getFloat().read(1);
   }

   public void setPitch(float value) {
      this.handle.getFloat().write(1, value);
   }
}
