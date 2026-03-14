package com.xiaoyue.celestial_invoker.content.generic.network;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.generic.item.api.IClickInteraction;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class ClickEmptyPacket extends SerialPacketBase {

    @SerialClass.SerialField
    public boolean right;
    @SerialClass.SerialField
    public InteractionHand hand;

    public ClickEmptyPacket(boolean right, InteractionHand hand) {
        this.right = right;
        this.hand = hand;
    }

    @Deprecated
    public ClickEmptyPacket() {
        this(false, InteractionHand.MAIN_HAND);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
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
    }

    public void toServer() {
        CelestialInvoker.HANDLER.toServer(this);
    }
}
