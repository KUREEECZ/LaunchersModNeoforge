package net.launchers.mod.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;

public abstract class CustomPacket {


    public CustomPacket() {
    }

    public abstract void sendTo(Player player);

    protected abstract void onReceive(Minecraft ctx);

    public abstract void write(FriendlyByteBuf buf);
}
