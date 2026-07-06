package com.xiaoyue.celestial_invoker.content.generic.item;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.xiaoyue.celestial_invoker.content.common.Bindings;
import com.xiaoyue.celestial_invoker.content.common.registrar.ArmorSetEntry;
import com.xiaoyue.celestial_invoker.content.generic.item.api.ISetHandler;
import com.xiaoyue.celestial_invoker.invoker.tooltip.SubscribeTooltip;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class CelestialArmorItem extends ArmorItem {

    @SubscribeTooltip(id = "set_effect")
    public static TooltipEntry setEffectTooltip = TooltipEntry.define("Set effect: ");
    @SubscribeTooltip(id = "alt_down")
    public static TooltipEntry altDownTooltip = TooltipEntry.define("Press [%s] to display set effects");

    public CelestialArmorItem(Holder<ArmorMaterial> material, Type pType, Properties pProperties) {
        super(material, pType, pProperties);
    }

    protected DefenseData getDefenseData(ItemStack stack) {
        return new DefenseData(0f, 0f, 0f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        this.addTooltips(stack, list, type.getSlot());
        if (getArmorSet() != null) {
            if (Screen.hasAltDown() || !requiredAltDown()) {
                Player player = Minecraft.getInstance().player;
                list.add(getSetTitle(player));
                addSetTooltips(stack, list);
                Set<Type> types = getArmorSet().getSet().keySet();
                if (!types.isEmpty()) {
                    list.add(Component.empty());
                }
                types.forEach(type -> {
                    ItemEntry<? extends Item> entry = getArmorSet().getSet().get(type);
                    if (entry != null) {
                        MutableComponent cmp = Component.literal("> ").append(entry.get().getDescription());
                        cmp.withStyle(hasSetArmor(player, type) ? ChatFormatting.GREEN : ChatFormatting.GRAY);
                        list.add(cmp);
                    }
                });
            } else if (requiredAltDown()) {
                list.add(altDownTooltip.withGray(Component.literal("ALT").withStyle(ChatFormatting.YELLOW)));
            }
        }
    }

    public void addTooltips(ItemStack stack, List<Component> list, EquipmentSlot slot) {
    }

    public boolean requiredAltDown() {
        return true;
    }

    public MutableComponent getSetName() {
        return Component.literal("");
    }

    private MutableComponent getSetTitle(Player player) {
        Component end = getSetName().append(" (" + getSetArmorCount(player) + "/" + getRequiredCount() + ")")
                .withStyle(ChatFormatting.GRAY);
        return setEffectTooltip.get().append(" ").append(end);
    }

    public void addSetTooltips(ItemStack stack, List<Component> list) {
    }

    public int getRequiredCount() {
        return this instanceof ISetHandler handler ? handler.requiredCount() : getArmorSet().getSet().size();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return super.getDefaultAttributeModifiers();
    }

    @Override
    public final ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        float defense = this.getDefenseData(stack).armor;
        float toughness = this.getDefenseData(stack).toughness;
        float knockbackResistance = this.getDefenseData(stack).knockbackResistance;
        ResourceLocation id = ResourceLocation.withDefaultNamespace("armor." + type.getName());
        builder.add(Attributes.ARMOR, new AttributeModifier(id, defense, AttributeModifier.Operation.ADD_VALUE), slotGroup());
        builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE), slotGroup());
        if (knockbackResistance > 0.0F) {
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }
        this.getAttributes(stack, builder);
        return builder.build();
    }

    public EquipmentSlotGroup slotGroup() {
        return EquipmentSlotGroup.bySlot(type.getSlot());
    }

    protected void getAttributes(ItemStack stack, ItemAttributeModifiers.Builder builder) {
    }

    @Override
    public final void inventoryTick(ItemStack pStack, Level pLevel, Entity entity, int pSlotId, boolean selected) {
        if (!(entity instanceof LivingEntity self)) return;
        this.onInventoryTick(pStack, pLevel, self, type.getSlot(), selected, Bindings.isArmorSlotIndex(pSlotId));
    }

    public void onInventoryTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot, boolean selected, boolean current) {
    }

    public int getSetArmorCount(@Nullable LivingEntity entity) {
        if (entity == null) return 0;
        return getArmorSet().getSetArmorCount(entity);
    }

    public boolean hasSetArmor(@Nullable LivingEntity entity, Type slot) {
        if (entity == null) return false;
        ItemStack stack = entity.getItemBySlot(slot.getSlot());
        if (stack.isEmpty()) return false;
        return isSetArmor(stack, slot);
    }

    public boolean isSetArmor(ItemStack stack, Type slot) {
        ItemEntry<? extends Item> entry = getArmorSet().getSet().get(slot);
        if (entry == null) return false;
        return stack.is(entry.get());
    }

    public ArmorSetEntry<? extends Item> getArmorSet() {
        return null;
    }

    public record DefenseData(float armor, float toughness, float knockbackResistance) {
    }
}
