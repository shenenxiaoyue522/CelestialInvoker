package com.xiaoyue.celestial_invoker.content.generic.shared;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.generic.items.api.IClickInteract;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClickEmptyPayload(boolean right, InteractionHand hand) implements CustomPacketPayload {
    public static final Type<ClickEmptyPayload> ID = new Type<>(CelestialInvoker.loc("player_click_empty"));

    private ClickEmptyPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readEnum(InteractionHand.class));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static StreamCodec<FriendlyByteBuf, ClickEmptyPayload> CODEC = CustomPacketPayload.codec(ClickEmptyPayload::write, ClickEmptyPayload::new);

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.right);
        buffer.writeEnum(this.hand);
    }

    public static void acceptItem(Player player, InteractionHand hand, boolean right) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof IClickInteract item) {
            if (right) {
                item.onRightClickEmpty(player, stack, hand);
            } else {
                item.onLeftClickEmpty(player, stack, hand);
            }
        }
    }

    public static void handlePacket(ClickEmptyPayload packet, IPayloadContext ctx) {
        if (ctx.flow().equals(PacketFlow.SERVERBOUND)) {
            ctx.enqueueWork(() -> acceptItem(ctx.player(), packet.hand, packet.right));
        }
    }
}
