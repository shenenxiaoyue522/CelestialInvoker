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
    public boolean isRight;

    @SerialClass.SerialField
    public InteractionHand hand;

    public ClickEmptyPacket(boolean isRight, InteractionHand hand) {
        this.isRight = isRight;
        this.hand = hand;
    }

    @Deprecated
    public ClickEmptyPacket() {
        this(true, InteractionHand.MAIN_HAND);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            if (isRight) {
                PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player, hand);
                ItemStack stack = event.getItemStack();
                if (stack.getItem() instanceof IClickInteraction item) {
                    item.onRightClickEmpty(stack, event, player);
                }
            } else {
                PlayerInteractEvent.LeftClickEmpty event = new PlayerInteractEvent.LeftClickEmpty(player);
                ItemStack stack = event.getItemStack();
                if (stack.getItem() instanceof IClickInteraction item) {
                    item.onLeftClickEmpty(stack, event, player);
                }
            }
        }
    }

    public void toServer() {
        CelestialInvoker.HANDLER.toServer(this);
    }
}
