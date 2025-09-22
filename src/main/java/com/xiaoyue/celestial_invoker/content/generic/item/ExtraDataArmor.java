package com.xiaoyue.celestial_invoker.content.generic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.content.ancillary.entry.AttrModifierEntry;
import com.xiaoyue.celestial_invoker.invoker.tooltip.SubscribeTooltip;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipEntry;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ExtraDataArmor extends ArmorItem {

    @SubscribeTooltip(id = "set_effect")
    public static TooltipEntry setEffect = TooltipEntry.define("Set effect: ");
    public static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        map.put(Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        map.put(Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        map.put(Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });

    public ExtraDataArmor(ArmorMaterial material, Type pType, Properties pProperties) {
        super(material, pType, pProperties);
    }

    public static void postMethod(LivingEntity entity, BiConsumer<ItemStack, ExtraDataArmor> cons) {
        entity.getArmorSlots().forEach(stack -> {
            if (!stack.isEmpty() && stack.getItem() instanceof ExtraDataArmor armor) {
                cons.accept(stack, armor);
            }
        });
    }

    @Override
    public final void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        this.addTooltips(stack, list, type.getSlot());
    }

    protected void addTooltips(ItemStack stack, List<Component> list, EquipmentSlot slot) {

    }

    public void addSeriesArmorTooltips(ItemStack stack, List<Component> list) {
        Player player = Proxy.getClientPlayer();
        list.add(getArmorSetTitle(player));
        list.add(getArmorSetEffectDescription(stack).withStyle(ChatFormatting.GRAY));
        List<Item> stacks = getSeriesArmors();
        for (Item armor : stacks) {
            MutableComponent cmp = Component.literal(" - ").append(armor.getDescription());
            EquipmentSlot slot = armor.getEquipmentSlot(armor.getDefaultInstance());
            cmp.withStyle(hasSeriesArmor(player, slot) ? ChatFormatting.GREEN : ChatFormatting.GRAY);
            list.add(cmp);
        }
    }

    public MutableComponent getArmorSetName() {
        return Component.literal("");
    }

    private MutableComponent getArmorSetTitle(Player player) {
        Component end = getArmorSetName().append(" (" + getSeriesArmorAmount(player) + "/" + getSeriesArmors().size() + ")")
                .withStyle(ChatFormatting.GRAY);
        return setEffect.get().append(" ").append(end);
    }

    public MutableComponent getArmorSetEffectDescription(ItemStack stack) {
        return Component.literal("");
    }

    @Override
    public final Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return ImmutableMultimap.of();
    }

    @Override
    public final Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modify = LinkedHashMultimap.create();
        if (slot.equals(this.type.getSlot())) {
            UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(this.type);
            float defense = this.getDefense() + this.getExtraDefense(stack).armor;
            float toughness = this.getToughness() + this.getExtraDefense(stack).toughness;
            AttrModifierEntry entry = AttrModifierEntry.builder().uuid(uuid);
            entry.attr(Attributes.ARMOR).name("Armor modifier").value(defense).toMap(modify)
                    .attr(Attributes.ARMOR_TOUGHNESS).name("Armor toughness").value(toughness).toMap(modify);
            if (this.knockbackResistance > 0.0F) {
                entry.attr(Attributes.KNOCKBACK_RESISTANCE).name("Armor knockback resistance").value(knockbackResistance).toMap(modify);
            }
            this.getAttributes(slot, stack, modify);
        }
        return modify;
    }

    protected ExtraDataArmor.DefenseData getExtraDefense(ItemStack stack) {
        return new ExtraDataArmor.DefenseData(0f, 0f, 0f);
    }

    protected void getAttributes(EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> modify) {

    }

    @Override
    public final void inventoryTick(ItemStack pStack, Level pLevel, Entity entity, int pSlotId, boolean selected) {
        if (!(entity instanceof LivingEntity self)) return;
        if (pSlotId >= 36) {
            int vanillaIndex = pSlotId - 36;
            if (vanillaIndex < 4) {
                this.onArmorTick(pStack, pLevel, self, type.getSlot());
            }
            this.onInventoryTick(pStack, pLevel, self, type.getSlot(), selected);
        }
    }

    protected void onArmorTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot) {

    }

    protected void onInventoryTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot, boolean selected) {

    }

    public boolean fullSeriesArmor(@Nullable LivingEntity entity) {
        return getSeriesArmorAmount(entity) >= 4;
    }

    public int getSeriesArmorAmount(@Nullable LivingEntity entity) {
        int amount = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor() && hasSeriesArmor(entity, slot)) amount++;
        }
        return amount;
    }

    public boolean hasSeriesArmor(@Nullable LivingEntity entity, EquipmentSlot slot) {
        if (entity == null) return false;
        ItemStack stack = entity.getItemBySlot(slot);
        if (stack.isEmpty()) return false;
        return isSeriesArmor(stack, slot);
    }

    public boolean isSeriesArmor(ItemStack stack, EquipmentSlot slot) {
        for (Item armor : getSeriesArmors()) {
            if (stack.is(armor)) return true;
        }
        return false;
    }

    public List<Item> getSeriesArmors() {
        return List.of();
    }

    public void onHurt(LivingEntity entity, ItemStack stack, LivingHurtEvent event, EquipmentSlot slot) {

    }

    public void onDamage(LivingEntity entity, ItemStack stack, LivingDamageEvent event, EquipmentSlot slot) {

    }

    public void onHurtTarget(LivingEntity attacker, ItemStack stack, LivingHurtEvent event, EquipmentSlot slot) {

    }

    public record DefenseData(float armor, float toughness, float knockbackResistance) {
    }
}
