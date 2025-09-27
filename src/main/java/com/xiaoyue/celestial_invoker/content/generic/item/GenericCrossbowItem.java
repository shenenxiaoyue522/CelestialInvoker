package com.xiaoyue.celestial_invoker.content.generic.item;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class GenericCrossbowItem extends CrossbowItem {

    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    public GenericCrossbowItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return ARROW_OR_FIREWORK;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (isCharged(stack)) {
            performShootAmmo(pLevel, pPlayer, pHand, stack, getShootingPower(stack), 1.0F);
            setCharged(stack, false);
            return InteractionResultHolder.consume(stack);
        } else if (!pPlayer.getProjectile(stack).isEmpty()) {
            if (!isCharged(stack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                pPlayer.startUsingItem(pHand);
            }
            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    public float getShootingPower(ItemStack crossbow) {
        return containsChargedProjectile(crossbow, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        int i = this.getUseDuration(pStack) - pTimeLeft;
        float f = getPowerForTime(i, pStack);
        if (f >= 1.0F && !isCharged(pStack) && tryLoadProjectiles(pEntityLiving, pStack)) {
            setCharged(pStack, true);
            SoundSource soundsource = pEntityLiving instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            pLevel.playSound(null, pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundsource, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    public boolean tryLoadProjectiles(LivingEntity pShooter, ItemStack crossbow) {
        int i = crossbow.getEnchantmentLevel(Enchantments.MULTISHOT);
        int j = i == 0 ? 1 : 3;
        boolean flag = pShooter instanceof Player && ((Player)pShooter).getAbilities().instabuild;
        ItemStack itemstack = pShooter.getProjectile(crossbow);
        ItemStack copyStack = itemstack.copy();
        for(int k = 0; k < j; ++k) {
            if (k > 0) {
                itemstack = copyStack.copy();
            }
            if (itemstack.isEmpty() && flag) {
                itemstack = new ItemStack(Items.ARROW);
                copyStack = itemstack.copy();
            }
            if (!loadProjectile(pShooter, crossbow, itemstack, k > 0, flag)) {
                return false;
            }
        }

        return true;
    }

    public boolean loadProjectile(LivingEntity pShooter, ItemStack crossbow, ItemStack pAmmoStack, boolean pHasAmmo, boolean pIsCreative) {
        if (pAmmoStack.isEmpty()) {
            return false;
        } else {
            boolean flag = pIsCreative && pAmmoStack.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !pIsCreative && !pHasAmmo) {
                itemstack = pAmmoStack.split(1);
                if (pAmmoStack.isEmpty() && pShooter instanceof Player) {
                    ((Player)pShooter).getInventory().removeItem(pAmmoStack);
                }
            } else {
                itemstack = pAmmoStack.copy();
            }

            addChargedProjectile(crossbow, itemstack);
            return true;
        }
    }

    public static boolean isCharged(ItemStack crossbow) {
        CompoundTag compoundtag = crossbow.getTag();
        return compoundtag != null && compoundtag.getBoolean("Charged");
    }

    public static void setCharged(ItemStack crossbow, boolean pIsCharged) {
        CompoundTag compoundtag = crossbow.getOrCreateTag();
        compoundtag.putBoolean("Charged", pIsCharged);
    }

    public void addChargedProjectile(ItemStack crossbow, ItemStack pAmmoStack) {
        CompoundTag compoundtag = crossbow.getOrCreateTag();
        ListTag listTag;
        if (compoundtag.contains("ChargedProjectiles", 9)) {
            listTag = compoundtag.getList("ChargedProjectiles", 10);
        } else {
            listTag = new ListTag();
        }
        CompoundTag newTag = new CompoundTag();
        pAmmoStack.save(newTag);
        listTag.add(newTag);
        compoundtag.put("ChargedProjectiles", listTag);
    }

    public static List<ItemStack> getChargedProjectiles(ItemStack crossbow) {
        List<ItemStack> list = Lists.newArrayList();
        CompoundTag compoundtag = crossbow.getTag();
        if (compoundtag != null && compoundtag.contains("ChargedProjectiles", 9)) {
            ListTag listTag = compoundtag.getList("ChargedProjectiles", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                list.add(ItemStack.of(listTag.getCompound(i)));
            }
        }
        return list;
    }

    public static void clearChargedProjectiles(ItemStack crossbow) {
        CompoundTag compoundtag = crossbow.getTag();
        if (compoundtag != null) {
            ListTag listtag = compoundtag.getList("ChargedProjectiles", 9);
            listtag.clear();
            compoundtag.put("ChargedProjectiles", listtag);
        }
    }

    public static boolean containsChargedProjectile(ItemStack crossbow, Item pAmmoItem) {
        return getChargedProjectiles(crossbow).stream().anyMatch((p_40870_) -> p_40870_.is(pAmmoItem));
    }

    private void shootProjectile(Level pLevel, LivingEntity pShooter, InteractionHand pHand, ItemStack crossbow, ItemStack pAmmoStack, float pSoundPitch, boolean pIsCreativeMode, float pVelocity, float pInaccuracy, float pProjectileAngle) {
        if (!pLevel.isClientSide) {
            boolean flag = pAmmoStack.is(Items.FIREWORK_ROCKET);
            Projectile projectile;
            if (flag) {
                projectile = new FireworkRocketEntity(pLevel, pAmmoStack, pShooter, pShooter.getX(), pShooter.getEyeY() - (double)0.15F, pShooter.getZ(), true);
            } else {
                projectile = getArrow(pLevel, pShooter, crossbow, pAmmoStack);
                if (pIsCreativeMode || pProjectileAngle != 0.0F) {
                    ((AbstractArrow)projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
            if (pShooter instanceof CrossbowAttackMob attackMob) {
                if (attackMob.getTarget() != null) {
                    attackMob.shootCrossbowProjectile(attackMob.getTarget(), crossbow, projectile, pProjectileAngle);
                }
            } else {
                Vec3 vec31 = pShooter.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(pProjectileAngle * ((float)Math.PI / 180F), vec31.x, vec31.y, vec31.z);
                Vec3 vec3 = pShooter.getViewVector(1.0F);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
                projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), pVelocity, pInaccuracy);
            }
            crossbow.hurtAndBreak(flag ? 3 : 1, pShooter, e -> e.broadcastBreakEvent(pHand));
            onConfigShoot(crossbow, pShooter, pHand, pAmmoStack, projectile);
            pLevel.addFreshEntity(projectile);
            pLevel.playSound(null, pShooter.getX(), pShooter.getY(), pShooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, pSoundPitch);
        }
    }

    protected void onConfigShoot(ItemStack crossbow, LivingEntity shooter, InteractionHand hand, ItemStack ammo, Projectile projectile) {
    }

    public AbstractArrow getArrow(Level pLevel, LivingEntity pLivingEntity, ItemStack crossbow, ItemStack pAmmoStack) {
        ArrowItem arrowitem = (ArrowItem)(pAmmoStack.getItem() instanceof ArrowItem ? pAmmoStack.getItem() : Items.ARROW);
        AbstractArrow arrow = arrowitem.createArrow(pLevel, pAmmoStack, pLivingEntity);
        arrow = getCustomArrow(pLevel, pLivingEntity, crossbow, pAmmoStack, arrow);
        if (pLivingEntity instanceof Player) {
            arrow.setCritArrow(true);
        }
        arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        arrow.setShotFromCrossbow(true);
        int i = crossbow.getEnchantmentLevel(Enchantments.PIERCING);
        if (i > 0) {
            arrow.setPierceLevel((byte)i);
        }
        return arrow;
    }

    protected AbstractArrow getCustomArrow(Level level, LivingEntity entity, ItemStack crossbow, ItemStack ammo, AbstractArrow arrow) {
        return arrow;
    }

    public void performShootAmmo(Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand, ItemStack crossbow, float pVelocity, float pInaccuracy) {
        if (pShooter instanceof Player player) {
            if (ForgeEventFactory.onArrowLoose(crossbow, pShooter.level(), player, 1, true) < 0) {
                return;
            }
        }
        List<ItemStack> list = getChargedProjectiles(crossbow);
        float[] afloat = getShotPitches(pShooter.getRandom());
        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = pShooter instanceof Player && ((Player)pShooter).getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                if (i == 0) {
                    shootProjectile(pLevel, pShooter, pUsedHand, crossbow, itemstack, afloat[i], flag, pVelocity, pInaccuracy, 0.0F);
                } else if (i == 1) {
                    shootProjectile(pLevel, pShooter, pUsedHand, crossbow, itemstack, afloat[i], flag, pVelocity, pInaccuracy, -10.0F);
                } else if (i == 2) {
                    shootProjectile(pLevel, pShooter, pUsedHand, crossbow, itemstack, afloat[i], flag, pVelocity, pInaccuracy, 10.0F);
                }
            }
        }
        onCrossbowShot(pLevel, pShooter, crossbow);
    }

    private static float[] getShotPitches(RandomSource pRandom) {
        boolean flag = pRandom.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(flag, pRandom), getRandomShotPitch(!flag, pRandom)};
    }

    private static float getRandomShotPitch(boolean pIsHighPitched, RandomSource pRandom) {
        float f = pIsHighPitched ? 0.63F : 0.43F;
        return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + f;
    }

    public static void onCrossbowShot(Level pLevel, LivingEntity pShooter, ItemStack crossbow) {
        if (crossbow.getItem() instanceof GenericCrossbowItem item) {
            item.afterCrossbowShot(pLevel, pShooter, crossbow);
        }
        if (pShooter instanceof ServerPlayer serverplayer) {
            if (!pLevel.isClientSide) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, crossbow);
            }
            serverplayer.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
        }
        clearChargedProjectiles(crossbow);
    }

    protected void afterCrossbowShot(Level level, LivingEntity shooter, ItemStack crossbow) {

    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pCount) {
        if (!pLevel.isClientSide) {
            int i = pStack.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
            SoundEvent startSound = this.getStartSound(i);
            SoundEvent loadingSound = i == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(pStack.getUseDuration() - pCount) / (float) getChargeDuration(pStack);
            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }
            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), startSound, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (f >= 0.5F && loadingSound != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), loadingSound, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return getChargeDuration(pStack) + 3;
    }

    public static int getChargeDuration(ItemStack crossbow) {
        int i = crossbow.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
        if (crossbow.getItem() instanceof GenericCrossbowItem item) {
            return item.getChargeTime(crossbow);
        }
        return i == 0 ? 25 : 25 - 5 * i;
    }

    protected int getChargeTime(ItemStack crossbow) {
        int i = crossbow.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
        return i == 0 ? 25 : 25 - 5 * i;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CROSSBOW;
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

    private static float getPowerForTime(int pUseTime, ItemStack crossbow) {
        float f = (float) pUseTime / (float)getChargeDuration(crossbow);
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        List<ItemStack> projectiles = getChargedProjectiles(pStack);
        if (isCharged(pStack) && !projectiles.isEmpty()) {
            ItemStack itemstack = projectiles.get(0);
            pTooltip.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(itemstack.getDisplayName()));
            if (pFlag.isAdvanced() && itemstack.is(Items.FIREWORK_ROCKET)) {
                List<Component> components = Lists.newArrayList();
                Items.FIREWORK_ROCKET.appendHoverText(itemstack, pLevel, components, pFlag);
                if (!components.isEmpty()) {
                    components.replaceAll(component -> Component.literal("  ").append((Component) component).withStyle(ChatFormatting.GRAY));
                    pTooltip.addAll(components);
                }
            }
        }

    }

    @Override
    public boolean useOnRelease(ItemStack pStack) {
        return pStack.is(this);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }
}
