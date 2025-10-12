package com.xiaoyue.celestial_invoker.content.generic.network;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import com.xiaoyue.celestial_invoker.content.generic.item.IClickInteraction;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
            if (right) {
                if (stack.getItem() instanceof IClickInteraction item) {
                    PlayerInteractEvent.RightClickEmpty event = new PlayerInteractEvent.RightClickEmpty(player, hand);
                    item.onRightClickEmpty(stack, event, player);
                }
            } else {
                if (stack.getItem() instanceof IClickInteraction item) {
                    PlayerInteractEvent.LeftClickEmpty event = new PlayerInteractEvent.LeftClickEmpty(player);
                    item.onLeftClickEmpty(stack, event, player);
                }
            }
        }
    }

    public void toServer() {
        CelestialInvoker.HANDLER.toServer(this);
    }
}
