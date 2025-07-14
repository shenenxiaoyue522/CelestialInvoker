package com.xiaoyue.celestial_invoker.content.generator;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.fml.LogicalSide;

public class RegistrateLootModifierProvider extends GlobalLootModifierProvider implements RegistrateProvider {
    private final AbstractRegistrate<?> owner;

    public RegistrateLootModifierProvider(PackOutput output, AbstractRegistrate<?> owner) {
        super(output, owner.getModid());
        this.owner = owner;
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    }

    @Override
    protected void start() {
        owner.genData(GeneratorTypes.LOOT_MODIFIER, this);
    }

    @Override
    public String getName() {
        return "Global Loot Modifier Registrate Provider";
    }
}
