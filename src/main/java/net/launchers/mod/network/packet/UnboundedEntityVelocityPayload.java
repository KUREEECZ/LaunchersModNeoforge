package net.launchers.mod.network.packet;

import net.launchers.mod.loader.LMLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record UnboundedEntityVelocityPayload(Vec3 velocity, int entityId) implements CustomPacketPayload {
    public static final Type<UnboundedEntityVelocityPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(LMLoader.MOD_ID, "lmpacket"));
    public static final StreamCodec<FriendlyByteBuf, UnboundedEntityVelocityPayload> CODEC = StreamCodec.of((buf, payload) -> payload.write(buf), UnboundedEntityVelocityPayload::read);

    public static UnboundedEntityVelocityPayload read(FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        Vec3 velocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        return new UnboundedEntityVelocityPayload(velocity, entityId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeDouble(velocity.x);
        buf.writeDouble(velocity.y);
        buf.writeDouble(velocity.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
