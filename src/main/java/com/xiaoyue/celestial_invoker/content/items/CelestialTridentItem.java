package com.xiaoyue.celestial_invoker.content.items;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CelestialTridentItem extends TridentItem {
    public CelestialTridentItem(Properties properties) {
        super(properties);
    }

    private static boolean isTooDamagedToUse(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    public int getChargeTime(ItemStack stack, LivingEntity entity) {
        return 10;
    }

    public Projectile customProjectile(ItemStack stack, Player player, Level level) {
        return new ThrownTrident(level, player, stack);
    }

    public boolean canUse(ItemStack stack, Player player, float strength) {
        return (strength <= 0f || player.isInWaterOrRain()) && !isTooDamagedToUse(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        int usedTime = this.getUseDuration(stack, entity) - timeLeft;
        if (usedTime >= getChargeTime(stack, entity)) {
            float strength = EnchantmentHelper.getTridentSpinAttackStrength(stack, player);
            if (canUse(stack, player, strength)) {
                Holder<SoundEvent> sound = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
                shootTrident(level, stack, player, strength, sound);
                player.awardStat(Stats.ITEM_USED.get(this));
                triggerRiptide(level, player, stack, strength, sound);
            }
        }
    }

    public void shootTrident(Level level, ItemStack stack, Player player, float strength, Holder<SoundEvent> sound) {
        if (level.isClientSide()) return;
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        if (strength == 0f) {
            Projectile thrown = customProjectile(stack, player, level);
            thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
            if (player.hasInfiniteMaterials() && thrown instanceof AbstractArrow arrow) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.addFreshEntity(thrown);
            level.playSound(null, thrown, sound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.hasInfiniteMaterials()) {
                player.getInventory().removeItem(stack);
            }
        }
    }

    public void triggerRiptide(Level level, Player player, ItemStack stack, float strength, Holder<SoundEvent> sound) {
        if (strength > 0.0F) {
            float f7 = player.getYRot();
            float f1 = player.getXRot();
            float f2 = -Mth.sin(f7 * ((float) Math.PI / 180F)) * Mth.cos(f1 * ((float) Math.PI / 180F));
            float f3 = -Mth.sin(f1 * ((float) Math.PI / 180F));
            float f4 = Mth.cos(f7 * ((float) Math.PI / 180F)) * Mth.cos(f1 * ((float) Math.PI / 180F));
            float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
            f2 *= strength / f5;
            f3 *= strength / f5;
            f4 *= strength / f5;
            player.push(f2, f3, f4);
            player.startAutoSpinAttack(20, 8f, stack);
            if (player.onGround()) {
                player.move(MoverType.SELF, new Vec3(0f, 1.1999999f, 0f));
            }
            level.playSound(null, player, sound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
