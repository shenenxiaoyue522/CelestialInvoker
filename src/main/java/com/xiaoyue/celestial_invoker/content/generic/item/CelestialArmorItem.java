package com.xiaoyue.celestial_invoker.content.generic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.content.common.entry.AttributeAdder;
import com.xiaoyue.celestial_invoker.invoker.tooltip.SubscribeTooltip;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipEntry;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class CelestialArmorItem extends ArmorItem {

    @SubscribeTooltip(id = "set_effect")
    public static TooltipEntry setEffect = TooltipEntry.define("Set effect: ");
    @SubscribeTooltip(id = "alt_down")
    public static TooltipEntry altDown = TooltipEntry.define("Press [%s] to display set effects");
    public static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(Type.class), (map) -> {
        map.put(Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        map.put(Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        map.put(Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        map.put(Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });

    public CelestialArmorItem(ArmorMaterial material, Type pType, Properties pProperties) {
        super(material, pType, pProperties);
    }

    @Override
    public final void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        this.addTooltips(stack, list, type.getSlot());
        if (hasArmorSetTooltip(stack)) {
            if (Screen.hasAltDown()) {
                addArmorSetTooltips(stack, list);
            } else {
                list.add(altDown.withGray(Component.literal("ALT").withStyle(ChatFormatting.YELLOW)));
            }
        }
    }

    protected void addTooltips(ItemStack stack, List<Component> list, EquipmentSlot slot) {

    }

    public boolean hasArmorSetTooltip(ItemStack stack) {
        return false;
    }

    public void addArmorSetTooltips(ItemStack stack, List<Component> list) {
        Player player = Proxy.getClientPlayer();
        list.add(getArmorSetTitle(player));
        addArmorSetEffectTooltips(stack, list);
        List<Item> items = getSetArmors();
        for (Item armor : items) {
            MutableComponent cmp = Component.literal("> ").append(armor.getDescription());
            EquipmentSlot slot = ((ArmorItem) armor).getEquipmentSlot();
            cmp.withStyle(hasSetArmor(player, slot) ? ChatFormatting.GREEN : ChatFormatting.GRAY);
            list.add(cmp);
        }
    }

    public MutableComponent getArmorSetName() {
        return Component.literal("");
    }

    private MutableComponent getArmorSetTitle(Player player) {
        Component end = getArmorSetName().append(" (" + getSetArmorAmount(player) + "/" + getSetArmorRequiredAmount() + ")")
                .withStyle(ChatFormatting.GRAY);
        return setEffect.get().append(" ").append(end);
    }

    public void addArmorSetEffectTooltips(ItemStack stack, List<Component> list) {

    }

    public int getSetArmorRequiredAmount() {
        return getSetArmors().size();
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
            AttributeAdder entry = AttributeAdder.builder().uuid(uuid);
            entry.attr(Attributes.ARMOR).name("Armor modifier").value(defense).toMap(modify)
                    .attr(Attributes.ARMOR_TOUGHNESS).name("Armor toughness").value(toughness).toMap(modify);
            if (this.knockbackResistance > 0.0F) {
                entry.attr(Attributes.KNOCKBACK_RESISTANCE).name("Armor knockback resistance").value(knockbackResistance).toMap(modify);
            }
            this.getAttributes(slot, stack, modify);
        }
        return modify;
    }

    protected DefenseData getExtraDefense(ItemStack stack) {
        return new DefenseData(0f, 0f, 0f);
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
            } else {
                this.onInventoryTick(pStack, pLevel, self, type.getSlot(), selected);
            }
        }
    }

    protected void onArmorTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot) {

    }

    protected void onInventoryTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot, boolean selected) {

    }

    public boolean fullSetArmor(@Nullable LivingEntity entity) {
        return getSetArmorAmount(entity) >= 4;
    }

    public int getSetArmorAmount(@Nullable LivingEntity entity) {
        int amount = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor() && hasSetArmor(entity, slot)) amount++;
        }
        return amount;
    }

    public boolean hasSetArmor(@Nullable LivingEntity entity, EquipmentSlot slot) {
        if (entity == null) return false;
        ItemStack stack = entity.getItemBySlot(slot);
        if (stack.isEmpty()) return false;
        return isSetArmor(stack, slot);
    }

    public boolean isSetArmor(ItemStack stack, EquipmentSlot slot) {
        for (Item armor : getSetArmors()) {
            if (stack.is(armor)) return true;
        }
        return false;
    }

    public List<Item> getSetArmors() {
        return List.of();
    }

    public record DefenseData(float armor, float toughness, float knockbackResistance) {
    }
}
