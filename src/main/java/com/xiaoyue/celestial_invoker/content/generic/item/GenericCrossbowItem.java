package com.xiaoyue.celestial_invoker.content.generic.item;

import com.google.common.collect.Lists;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class GenericCrossbowItem extends CrossbowItem {

    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    public GenericCrossbowItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (isCharged(itemstack)) {
            performShooting(pLevel, pPlayer, pHand, itemstack, getShootingPower(itemstack), 1.0f);
            setCharged(itemstack, false);
            return InteractionResultHolder.consume(itemstack);
        } else if (!pPlayer.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                pPlayer.startUsingItem(pHand);
            }
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    private static float getShootingPower(ItemStack crossbow) {
        return containsChargedProjectile(crossbow, Items.FIREWORK_ROCKET) ? 1.6f : 3.15f;
    }

    public static void performShooting(Level pLevel, LivingEntity pShooter, InteractionHand hand, ItemStack crossbow, float pVelocity, float pInaccuracy) {
        if (pShooter instanceof Player player) {
            if (ForgeEventFactory.onArrowLoose(crossbow, pShooter.level(), player, 1, true) < 0) {
                return;
            }
        }
        List<ItemStack> ammoList = getChargedProjectiles(crossbow);
        float[] afloat = getShotPitches(pShooter.getRandom());
        for(int i = 0; i < ammoList.size(); ++i) {
            ItemStack ammo = ammoList.get(i);
            boolean flag = pShooter instanceof Player player && player.getAbilities().instabuild;
            if (!ammo.isEmpty()) {
                if (i == 0) {
                    shootProjectile(pLevel, pShooter, hand, crossbow, ammo, afloat[i], flag, pVelocity, pInaccuracy, 0.0F);
                } else if (i == 1) {
                    shootProjectile(pLevel, pShooter, hand, crossbow, ammo, afloat[i], flag, pVelocity, pInaccuracy, -10.0F);
                } else if (i == 2) {
                    shootProjectile(pLevel, pShooter, hand, crossbow, ammo, afloat[i], flag, pVelocity, pInaccuracy, 10.0F);
                }
            }
        }
        onCrossbowShot(pLevel, pShooter, crossbow);
    }

    private static void shootProjectile(Level pLevel, LivingEntity pShooter, InteractionHand pHand, ItemStack crossbow, ItemStack ammo, float pSoundPitch, boolean pIsCreativeMode, float pVelocity, float pInaccuracy, float pProjectileAngle) {
        if (!pLevel.isClientSide()) {
            boolean flag = ammo.is(Items.FIREWORK_ROCKET);
            Projectile projectile;
            if (flag) {
                projectile = new FireworkRocketEntity(pLevel, ammo, pShooter, pShooter.getX(), pShooter.getEyeY() - (double) 0.15f, pShooter.getZ(), true);
            } else {
                projectile = getArrow(pLevel, pShooter, crossbow, ammo);
                if (pIsCreativeMode || pProjectileAngle != 0.0F) {
                    ((AbstractArrow)projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
            if (crossbow.getItem() instanceof GenericCrossbowItem item) {
                Projectile customProjectile = item.getCustomProjectile(pShooter, pHand, crossbow, projectile);
                if (customProjectile != null) {
                    projectile = customProjectile;
                }
            }
            if (crossbow.getItem() instanceof GenericCrossbowItem item) {
                item.onConfigShoot(pShooter, pHand, crossbow, ammo, projectile, projectile instanceof AbstractArrow arrow ? arrow : null);
            }
            if (pShooter instanceof CrossbowAttackMob mob && mob.getTarget() != null) {
                mob.shootCrossbowProjectile(mob.getTarget(), crossbow, projectile, pProjectileAngle);
            } else {
                Vec3 upVector = pShooter.getUpVector(1.0f);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(pProjectileAngle * ((float) Math.PI / 180f), upVector.x, upVector.y, upVector.z);
                Vec3 vec3 = pShooter.getViewVector(1.0f);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
                projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), pVelocity, pInaccuracy);
            }
            crossbow.hurtAndBreak(flag ? 3 : 1, pShooter, (p_40858_) -> p_40858_.broadcastBreakEvent(pHand));
            pLevel.addFreshEntity(projectile);
            pLevel.playSound(null, pShooter.getX(), pShooter.getY(), pShooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, pSoundPitch);
        }
    }

    protected void onConfigShoot(LivingEntity shooter, InteractionHand hand, ItemStack crossbow, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow) {
    }

    private static AbstractArrow getArrow(Level pLevel, LivingEntity entity, ItemStack crossbow, ItemStack ammo) {
        ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
        AbstractArrow arrow = arrowitem.createArrow(pLevel, ammo, entity);
        if (entity instanceof Player) {
            arrow.setCritArrow(true);
        }
        arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        arrow.setShotFromCrossbow(true);
        int piercing = crossbow.getEnchantmentLevel(Enchantments.PIERCING);
        if (piercing > 0) {
            arrow.setPierceLevel((byte) piercing);
        }
        return arrow;
    }

    protected Projectile getCustomProjectile(LivingEntity shooter, InteractionHand hand, ItemStack crossbow, Projectile origin) {
        return null;
    }

    private static float[] getShotPitches(RandomSource pRandom) {
        boolean flag = pRandom.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(flag, pRandom), getRandomShotPitch(!flag, pRandom)};
    }

    private static float getRandomShotPitch(boolean pIsHighPitched, RandomSource pRandom) {
        float f = pIsHighPitched ? 0.63F : 0.43F;
        return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack pCrossbowStack) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundTag tag = pCrossbowStack.getTag();
        if (tag != null && tag.contains("ChargedProjectiles", 9)) {
            ListTag listtag = tag.getList("ChargedProjectiles", 10);
            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag ammo = listtag.getCompound(i);
                list.add(ItemStack.of(ammo));
            }
        }
        return list;
    }

    private static void onCrossbowShot(Level pLevel, LivingEntity pShooter, ItemStack pCrossbowStack) {
        if (pShooter instanceof ServerPlayer serverplayer) {
            if (!pLevel.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, pCrossbowStack);
            }
            serverplayer.awardStat(Stats.ITEM_USED.get(pCrossbowStack.getItem()));
        }
        clearChargedProjectiles(pCrossbowStack);
    }

    private static void clearChargedProjectiles(ItemStack pCrossbowStack) {
        CompoundTag compoundtag = pCrossbowStack.getTag();
        if (compoundtag != null) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
            listtag.clear();
            compoundtag.put("ChargedProjectiles", listtag);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity pEntityLiving, int pTimeLeft) {
        int i = this.getUseDuration(stack) - pTimeLeft;
        float f = getPowerForTime(i, stack);
        if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(pEntityLiving, stack)) {
            setCharged(stack, true);
            SoundSource soundsource = pEntityLiving instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound(null, pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundsource, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    protected int getChargeTime(ItemStack crossbow) {
        int quick_charge = crossbow.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
        return quick_charge == 0 ? 25 : 25 - 5 * quick_charge;
    }

    public static int getChargeDuration(ItemStack crossbow) {
        if (crossbow.getItem() instanceof GenericCrossbowItem item) {
            return item.getChargeTime(crossbow);
        }
        return CrossbowItem.getChargeDuration(crossbow);
    }

    private static float getPowerForTime(int pUseTime, ItemStack crossbow) {
        float f = (float) pUseTime / getChargeDuration(crossbow);
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    private static boolean tryLoadProjectiles(LivingEntity pShooter, ItemStack crossbow) {
        int multishot = crossbow.getEnchantmentLevel(Enchantments.MULTISHOT);
        int count = multishot == 0 ? 1 : 3;
        boolean flag = pShooter instanceof Player && ((Player)pShooter).getAbilities().instabuild;
        ItemStack ammo = pShooter.getProjectile(crossbow);
        ItemStack ammoCopy = ammo.copy();
        for(int i = 0; i < count; ++i) {
            if (i > 0) {
                ammo = ammoCopy.copy();
            }
            if (ammo.isEmpty() && flag) {
                ammo = new ItemStack(Items.ARROW);
                ammoCopy = ammo.copy();
            }
            if (!loadProjectile(pShooter, crossbow, ammo, i > 0, flag)) {
                return false;
            }
        }
        return true;
    }

    private static boolean loadProjectile(LivingEntity pShooter, ItemStack crossbow, ItemStack ammo, boolean pHasAmmo, boolean pIsCreative) {
        if (ammo.isEmpty()) {
            return false;
        } else {
            boolean flag = pIsCreative && ammo.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !pIsCreative && !pHasAmmo) {
                itemstack = ammo.split(1);
                if (ammo.isEmpty() && pShooter instanceof Player player) {
                    player.getInventory().removeItem(ammo);
                }
            } else {
                itemstack = ammo.copy();
            }
            addChargedProjectile(crossbow, itemstack);
            return true;
        }
    }

    private static void addChargedProjectile(ItemStack crossbow, ItemStack ammo) {
        CompoundTag tag = crossbow.getOrCreateTag();
        ListTag list;
        if (tag.contains("ChargedProjectiles", 9)) {
            list = tag.getList("ChargedProjectiles", 10);
        } else {
            list = new ListTag();
        }
        CompoundTag ammoTag = new CompoundTag();
        ammo.save(ammoTag);
        list.add(ammoTag);
        tag.put("ChargedProjectiles", list);
    }

    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack crossbow, int pCount) {
        if (!pLevel.isClientSide) {
            int quick_charge = crossbow.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
            SoundEvent startSound = this.getStartSound(quick_charge);
            SoundEvent loadingSound = quick_charge == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(crossbow.getUseDuration() - pCount) / (float) getChargeDuration(crossbow);
            if (f < 0.2f) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }
            if (f >= 0.2f && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), startSound, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (f >= 0.5f && loadingSound != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), loadingSound, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    private SoundEvent getStartSound(int pEnchantmentLevel) {
        switch (pEnchantmentLevel) {
            case 1 -> {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            }
            case 2 -> {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            }
            case 3 -> {
                return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            }
            default -> {
                return SoundEvents.CROSSBOW_LOADING_START;
            }
        }
    }
}
