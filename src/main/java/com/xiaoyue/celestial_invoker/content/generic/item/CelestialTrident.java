package com.xiaoyue.celestial_invoker.content.generic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xiaoyue.celestial_invoker.content.ancillary.entry.AttrModifierEntry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CelestialTrident extends TridentItem {
    public CelestialTrident(Properties pProperties) {
        super(pProperties);
    }

    protected int getChargeTime(ItemStack trident, Player player) {
        return 10;
    }

    protected AbstractArrow getThrownEntity(Level level, Player player, ItemStack trident) {
        return new ThrownTrident(level, player, trident);
    }

    protected void onConfigShoot(ItemStack trident, Player player, Level level, AbstractArrow thrownEntity) {

    }

    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity entity, int pTimeLeft) {
        if (entity instanceof Player player) {
            int chance = this.getUseDuration(pStack) - pTimeLeft;
            if (chance >= getChargeTime(pStack, player)) {
                int riptide = EnchantmentHelper.getRiptide(pStack);
                if (riptide <= 0 || player.isInWaterOrRain()) {
                    if (!pLevel.isClientSide) {
                        pStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                        if (riptide == 0) {
                            AbstractArrow thrownEntity = getThrownEntity(pLevel, player, pStack);
                            onConfigShoot(pStack, player, pLevel, thrownEntity);
                            thrownEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5f + (float) riptide * 0.5f, 1.0f);
                            if (player.getAbilities().instabuild) {
                                thrownEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }
                            pLevel.addFreshEntity(thrownEntity);
                            pLevel.playSound(null, thrownEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!player.getAbilities().instabuild) {
                                player.getInventory().removeItem(pStack);
                            }
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (riptide > 0) {
                        float yRot = player.getYRot();
                        float xRot = player.getXRot();
                        float $$10 = -Mth.sin(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));
                        float $$11 = -Mth.sin(xRot * ((float)Math.PI / 180F));
                        float $$12 = Mth.cos(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));
                        float $$13 = Mth.sqrt($$10 * $$10 + $$11 * $$11 + $$12 * $$12);
                        float $$14 = 3.0F * ((1.0F + (float) riptide) / 4.0F);
                        $$10 *= $$14 / $$13;
                        $$11 *= $$14 / $$13;
                        $$12 *= $$14 / $$13;
                        player.push($$10, $$11, $$12);
                        player.startAutoSpinAttack(20);
                        if (player.onGround()) {
                            player.move(MoverType.SELF, new Vec3(0.0f, 1.2f, 0.0f));
                        }
                        SoundEvent soundEvent;
                        if (riptide >= 3) {
                            soundEvent = SoundEvents.TRIDENT_RIPTIDE_3;
                        } else if (riptide == 2) {
                            soundEvent = SoundEvents.TRIDENT_RIPTIDE_2;
                        } else {
                            soundEvent = SoundEvents.TRIDENT_RIPTIDE_1;
                        }
                        pLevel.playSound(null, player, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public final Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return ImmutableMultimap.of();
    }

    @Override
    public final Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        LinkedHashMultimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        AttrModifierEntry attr = AttrModifierEntry.builder()
                .uuid(BASE_ATTACK_DAMAGE_UUID).name("Tool modifier").value(8f).toMap(map)
                .attr(Attributes.ATTACK_SPEED).uuid(BASE_ATTACK_SPEED_UUID).name("Tool modifier")
                .value(-2.9f).toMap(map);
        addAttributes(slot, stack, map, attr);
        return map;
    }

    protected void addAttributes(EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map, AttrModifierEntry attr) {

    }
}
