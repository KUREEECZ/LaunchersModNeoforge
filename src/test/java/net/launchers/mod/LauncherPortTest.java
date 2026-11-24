package net.launchers.mod;

import io.netty.buffer.Unpooled;
import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.launchers.mod.entity.LauncherBlockEntity;
import net.launchers.mod.entity.abstraction.AbstractLauncherBlockEntity;
import net.launchers.mod.initializer.LMBlock;
import net.launchers.mod.network.packet.UnboundedEntityVelocityPayload;
import net.launchers.mod.utils.MathUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EphemeralTestServerProvider.class)
class LauncherPortTest {
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("launchersmod", path);
    }

    @Test
    void registersAllFourLaunchers() {
        for (String name : List.of("launcher_block", "powered_launcher_block", "extreme_launcher_block", "ultimate_launcher_block")) {
            AbstractLauncherBlock block = assertInstanceOf(AbstractLauncherBlock.class, BuiltInRegistries.BLOCK.get(id(name)));
            BlockItem item = assertInstanceOf(BlockItem.class, BuiltInRegistries.ITEM.get(id(name)));
            assertSame(block, item.getBlock());
            assertNotNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.get(id(name)).create(BlockPos.ZERO, block.defaultBlockState()));
            assertEquals(Direction.NORTH, block.defaultBlockState().getValue(AbstractLauncherBlock.FACING));
            assertFalse(block.defaultBlockState().getValue(AbstractLauncherBlock.TRIGGERED));
            assertEquals(RenderShape.MODEL, block.defaultBlockState().getRenderShape());
        }
        assertTrue(BuiltInRegistries.SOUND_EVENT.containsKey(id("launcher_block_launch")));
    }

    @Test
    void preservesForceMultipliers() {
        assertEquals(1.25F, LMBlock.LAUNCHER_BLOCK.get().baseMultiplier);
        assertEquals(1.25F * 0.335F, LMBlock.LAUNCHER_BLOCK.get().stackMultiplier);
        assertEquals(2.125F, LMBlock.POWERED_LAUNCHER_BLOCK.get().baseMultiplier);
        assertEquals(2.125F * 0.2975F, LMBlock.POWERED_LAUNCHER_BLOCK.get().stackMultiplier);
        assertEquals(2.95F, LMBlock.EXTREME_LAUNCHER_BLOCK.get().baseMultiplier);
        assertEquals(2.95F * 0.275F, LMBlock.EXTREME_LAUNCHER_BLOCK.get().stackMultiplier);
        assertEquals(13.75F, LMBlock.ULTIMATE_LAUNCHER_BLOCK.get().baseMultiplier);
        assertEquals(0F, LMBlock.ULTIMATE_LAUNCHER_BLOCK.get().stackMultiplier);
    }

    @Test
    void preservesAllSixLaunchDirections() {
        for (Direction direction : Direction.values()) {
            assertEquals(new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()), MathUtils.fromDirection(direction));
        }
    }

    @Test
    void velocityPacketDoesNotClampHighVelocities() {
        var packet = new UnboundedEntityVelocityPayload(new Vec3(-24.0625, 13.75, 128.5), 12345);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            UnboundedEntityVelocityPayload.CODEC.encode(buffer, packet);
            assertEquals(packet, UnboundedEntityVelocityPayload.CODEC.decode(buffer));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void preservesExtensionRetractionAndNbt() {
        var block = LMBlock.LAUNCHER_BLOCK.get();
        var entity = new LauncherBlockEntity(BlockPos.ZERO, block.defaultBlockState());
        assertTrue(entity.isRetracted());

        CompoundTag tag = new CompoundTag();
        tag.putInt("launcherState", AbstractLauncherBlockEntity.LauncherState.MOVING.ordinal());
        tag.putBoolean("extending", true);
        entity.loadCustomOnly(tag, RegistryAccess.EMPTY);
        AbstractLauncherBlockEntity.tick(null, BlockPos.ZERO, block.defaultBlockState(), entity);
        assertEquals(0.5F, entity.getDeltaProgress(0.5F));
        assertEquals(1F, entity.getDeltaProgress(2F));
        AbstractLauncherBlockEntity.tick(null, BlockPos.ZERO, block.defaultBlockState(), entity);
        assertEquals(AbstractLauncherBlockEntity.LauncherState.EXTENDED, entity.launcherState);

        tag.putBoolean("extending", false);
        tag.putFloat("progress", 1F);
        entity.loadCustomOnly(tag, RegistryAccess.EMPTY);
        for (int i = 1; i <= 4; i++) {
            AbstractLauncherBlockEntity.tick(null, BlockPos.ZERO, block.defaultBlockState(), entity);
            assertEquals(1F - i * 0.25F, entity.getDeltaProgress(1F));
        }
        AbstractLauncherBlockEntity.tick(null, BlockPos.ZERO, block.defaultBlockState(), entity);
        assertTrue(entity.isRetracted());
        CompoundTag saved = entity.getUpdateTag(RegistryAccess.EMPTY);
        assertEquals(0F, saved.getFloat("progress"));
        assertFalse(saved.getBoolean("extending"));
        assertEquals(AbstractLauncherBlockEntity.LauncherState.RETRACTED.ordinal(), saved.getInt("launcherState"));
    }

    @Test
    void loadsRecipesAndLootTables(MinecraftServer server) {
        for (String name : List.of("launcher_block", "powered_launcher_block", "extreme_launcher_block", "ultimate_launcher_block")) {
            var recipe = server.getRecipeManager().byKey(id(name)).orElseThrow();
            assertSame(BuiltInRegistries.ITEM.get(id(name)), recipe.value().getResultItem(server.registryAccess()).getItem());
            var key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, id("blocks/" + name));
            assertNotSame(net.minecraft.world.level.storage.loot.LootTable.EMPTY, server.reloadableRegistries().getLootTable(key));
        }
    }

    @Test
    void lcalcPreservesStackCalculationAndUltimateRestriction(MinecraftServer server) throws Exception {
        var output = new CapturingSource();
        var source = server.createCommandSourceStack().withSource(output);
        var dispatcher = server.getCommands().getDispatcher();
        assertEquals(1, dispatcher.execute("lcalc p e e l", source));
        float expectedForce = 2.125F * (1F + 2.95F * 0.275F + 2.95F * 0.275F + 1.25F * 0.335F);
        assertTrue(output.message.contains("Force: " + expectedForce), output.message);
        assertEquals(1, dispatcher.execute("lcalc u", source));
        assertTrue(output.message.contains("Force: 13.75"), output.message);
        assertEquals(0, dispatcher.execute("lcalc u l", source));
        assertTrue(output.message.contains("One or more parameters are not correct."), output.message);
        assertEquals(0, dispatcher.execute("lcalc", source));
    }

    private static class CapturingSource implements CommandSource {
        String message;

        @Override public void sendSystemMessage(Component component) { message = component.getString(); }
        @Override public boolean acceptsSuccess() { return true; }
        @Override public boolean acceptsFailure() { return true; }
        @Override public boolean shouldInformAdmins() { return false; }
    }
}
