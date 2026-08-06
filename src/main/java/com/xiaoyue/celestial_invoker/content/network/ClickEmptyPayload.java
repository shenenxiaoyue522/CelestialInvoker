package com.xiaoyue.celestial_invoker.content.network;

import com.xiaoyue.celestial_invoker.content.generic.item.api.IClickInteraction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClickEmptyPayload {

    public boolean right;
    public InteractionHand hand;

    public ClickEmptyPayload(boolean right, InteractionHand hand) {
        this.right = right;
        this.hand = hand;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(right);
        buf.writeEnum(hand);
    }

    public static ClickEmptyPayload decode(FriendlyByteBuf buf) {
        return new ClickEmptyPayload(buf.readBoolean(), buf.readEnum(InteractionHand.class));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            if (player != null) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof IClickInteraction item) {
                    if (right) {
                        item.onRightClickEmpty(player, stack, hand);
                    } else {
                        item.onLeftClickEmpty(player, stack, hand);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public void toServer() {
        NetworkHandler.INSTANCE.sendToServer(this);
    }
}
