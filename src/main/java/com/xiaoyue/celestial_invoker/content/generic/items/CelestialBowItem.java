package com.xiaoyue.celestial_invoker.content.generic.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;

public class CelestialBowItem extends BowItem {
    public CelestialBowItem(Properties pProperties) {
        super(pProperties);
    }

    public float getPullTime(LivingEntity user, ItemStack bow) {
        return 1.0f;
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            ItemStack ammo = player.getProjectile(stack);
            if (!ammo.isEmpty()) {
                int i = this.getUseDuration(stack, entity) - timeLeft;
                i = EventHooks.onArrowLoose(stack, level, player, i, !ammo.isEmpty());
                if (i < 0) {
                    return;
                }
                float charged = getBowPowerForTime(player, stack, i);
                if (charged >= 0.1) {
                    List<ItemStack> list = draw(stack, ammo, player);
                    if (level instanceof ServerLevel serverLevel) {
                        if (!list.isEmpty()) {
                            this.shoot(serverLevel, player, player.getUsedItemHand(), stack, list, charged * 3f, 1f, charged == 1f, charged);
                        }
                    }
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + charged * 0.5F);
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, float charged) {
        float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        float f1 = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float) (projectileItems.size() - 1);
        float f2 = (float) ((projectileItems.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;
        for (int i = 0; i < projectileItems.size(); ++i) {
            ItemStack ammo = projectileItems.get(i);
            if (!ammo.isEmpty()) {
                float f4 = f2 + f3 * (float) ((i + 1) / 2) * f1;
                f3 = -f3;
                Projectile projectile = this.createProjectile(level, shooter, weapon, ammo, isCrit);
                onConfigShoot(weapon, shooter, hand, ammo, projectile, charged);
                this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, f4, null);
                level.addFreshEntity(projectile);
                weapon.hurtAndBreak(this.getDurabilityUse(ammo), shooter, LivingEntity.getSlotForHand(hand));
                if (weapon.isEmpty()) {
                    break;
                }
            }
        }
    }

    protected void onConfigShoot(ItemStack bow, LivingEntity shooter, InteractionHand hand, ItemStack ammo, Projectile arrow, float charged) {
    }

    public float getPullForTime(LivingEntity user, ItemStack bow, float time) {
        float actual = this.getPullTime(user, bow) * 20.0F;
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
