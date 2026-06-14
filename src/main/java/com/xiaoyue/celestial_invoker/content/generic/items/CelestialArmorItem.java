package com.xiaoyue.celestial_invoker.content.generic.items;

import com.xiaoyue.celestial_invoker.content.common.Bindings;
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

public class CelestialArmorItem extends ArmorItem {

    @SubscribeTooltip(id = "set_effect")
    public static TooltipEntry setEffect = TooltipEntry.define("Set effect: ");
    @SubscribeTooltip(id = "alt_down")
    public static TooltipEntry altDown = TooltipEntry.define("Press [%s] to display set effects");

    public CelestialArmorItem(Holder<ArmorMaterial> material, Type pType, Properties pProperties) {
        super(material, pType, pProperties);
    }

    protected DefenseData getDefenseData(ItemStack stack) {
        return new DefenseData(0f, 0f, 0f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        this.addTooltips(stack, list, type.getSlot());
        if (hasArmorSetTooltip(stack) && !Screen.hasAltDown()) {
            addArmorSetTooltips(stack, list);
        } else {
            list.add(altDown.withGray(Component.literal("ALT").withStyle(ChatFormatting.YELLOW)));
        }
    }

    protected void addTooltips(ItemStack stack, List<Component> list, EquipmentSlot slot) {

    }

    public boolean hasArmorSetTooltip(ItemStack stack) {
        return false;
    }

    public void addArmorSetTooltips(ItemStack stack, List<Component> list) {
        Player player = Minecraft.getInstance().player;
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
        if (Bindings.isArmorSlotIndex(pSlotId)) {
            this.onArmorTick(pStack, pLevel, self, type.getSlot());
        }
        this.onInventoryTick(pStack, pLevel, self, type.getSlot(), selected);
    }

    protected void onArmorTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot) {

    }

    protected void onInventoryTick(ItemStack stack, Level level, LivingEntity entity, EquipmentSlot slot, boolean selected) {

    }

    public static boolean fullSetArmor(@Nullable LivingEntity entity) {
        return getSetArmorAmount(entity) >= 4;
    }

    public static int getSetArmorAmount(@Nullable LivingEntity entity) {
        int amount = 0;
        if (entity == null) return amount;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor() && entity.getItemBySlot(slot).getItem() instanceof CelestialArmorItem armor && armor.hasSetArmor(entity, slot)) amount++;
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
