package com.xiaoyue.celestial_invoker.content.generic.shared;

import com.mojang.serialization.Codec;
import com.xiaoyue.celestial_invoker.register.CIObjects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public record CompoundData(CompoundTag tag) {

    public static final Codec<CompoundData> CODEC = CompoundTag.CODEC.xmap(CompoundData::new, CompoundData::tag);

    public static final StreamCodec<FriendlyByteBuf, CompoundData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> buf.writeNbt(data.tag), buf -> new CompoundData(buf.readNbt()));

    public boolean isEmpty() {
        return tag.isEmpty();
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> tag) {
        CompoundData data = getOrCreate(stack).update(tag);
        if (data.tag.isEmpty()) {
            stack.remove(CIObjects.COMPOUND_DATA);
        } else {
            stack.set(CIObjects.COMPOUND_DATA, data);
        }
    }

    public CompoundData update(Consumer<CompoundTag> tag) {
        CompoundTag data = this.tag.copy();
        tag.accept(data);
        return of(data);
    }

    public static CompoundData of(CompoundTag tag) {
        return new CompoundData(tag);
    }

    public static CompoundData getOrCreate(ItemStack stack) {
        CompoundData data = stack.get(CIObjects.COMPOUND_DATA);
        if (data == null) {
            CompoundData emptyData = new CompoundData(new CompoundTag());
            stack.set(CIObjects.COMPOUND_DATA, emptyData);
            return emptyData;
        }
        return data;
    }
}
