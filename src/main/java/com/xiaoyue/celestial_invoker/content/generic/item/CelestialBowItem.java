package com.xiaoyue.celestial_invoker.content.generic.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

public class CelestialBowItem extends BowItem {
    public CelestialBowItem(Properties pProperties) {
        super(pProperties);
    }

    public float getDrawSpeed(LivingEntity user, ItemStack bow) {
        return 1.0f;
    }

    @Override
    public void releaseUsing(ItemStack bow, Level level, LivingEntity user, int pTimeLeft) {
        if (user instanceof Player player) {
            boolean flag = player.getAbilities().instabuild || bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
            ItemStack ammo = player.getProjectile(bow);
            int change = this.getUseDuration(bow) - pTimeLeft;
            change = ForgeEventFactory.onArrowLoose(bow, level, player, change, !ammo.isEmpty() || flag);
            if (change < 0) {
                return;
            }
            if (ammo.isEmpty() && !flag) {
                return;
            }
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }
            float pull = this.getBowPowerForTime(user, bow, (float)change);
            if (pull < 0.1) {
                return;
            }
            this.checkAndShoot(player, level, bow, ammo, pull);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    public void checkAndShoot(Player player, Level level, ItemStack bow, ItemStack ammo, float pull) {
        boolean canShoot = player.getAbilities().instabuild || ammo.getItem() instanceof ArrowItem a && a.isInfinite(ammo, bow, player);
        if (!level.isClientSide()) {
            ArrowItem arrowItem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
            AbstractArrow arrow = arrowItem.createArrow(level, ammo, player);
            arrow = this.customArrow(arrow);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0f, pull * 3.0f, 1.0F);
            if (pull == 1.0f) {
                arrow.setCritArrow(true);
            }
            int s = bow.getEnchantmentLevel(Enchantments.POWER_ARROWS);
            if (s > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + s * 0.5f + 0.5f);
            }
            int p = bow.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
            if (p > 0) {
                arrow.setKnockback(p);
            }
            if (bow.getEnchantmentLevel(Enchantments.FLAMING_ARROWS) > 0) {
                arrow.setSecondsOnFire(100);
            }
            this.onConfigShoot(bow, player, ammo, arrowItem, arrow, pull);
            bow.hurtAndBreak(1, player, (pl) -> pl.broadcastBreakEvent(player.getUsedItemHand()));
            if (canShoot || player.getAbilities().instabuild && (ammo.is(Items.SPECTRAL_ARROW) || ammo.is(Items.TIPPED_ARROW))) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.addFreshEntity(arrow);
        }

        float pitch = 1.0F / (level.getRandom().nextFloat() * 0.4f + 1.2F) + pull * 0.5f;
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, pitch);
        if (!canShoot && !player.getAbilities().instabuild) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        }
    }

    protected void onConfigShoot(ItemStack bow, Player shooter, ItemStack ammo, ArrowItem arrowItem, AbstractArrow arrow, float pull) {
    }

    public float getPullForTime(LivingEntity user, ItemStack bow, float time) {
        float actual = this.getDrawSpeed(user, bow) * 20.0F;
        float f = time / actual * 1.5F;
        return Math.min(1.0F, f);
    }

    public float getBowPowerForTime(LivingEntity user, ItemStack bow, float time) {
        float f = this.getPullForTime(user, bow, time);
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return Math.min(1.0F, f);
    }
}
