package net.launchers.mod.network;

import net.launchers.mod.network.packet.UnboundedEntityVelocityPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.players.PlayerList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkHandler {
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(UnboundedEntityVelocityPayload.ID, UnboundedEntityVelocityPayload.CODEC, (payload, context) -> {
            Entity target = context.player().level().getEntity(payload.entityId());
            if (target != null) {
                target.setDeltaMovement(payload.velocity());
            }
        });
    }

    public static void sendToAll(UnboundedEntityVelocityPayload packet, PlayerList manager) {
        for (var target : manager.getPlayers()) {
            PacketDistributor.sendToPlayer(target, packet);
        }
    }
}
