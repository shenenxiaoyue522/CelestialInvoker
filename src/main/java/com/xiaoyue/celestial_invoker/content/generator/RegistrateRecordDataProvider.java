package com.xiaoyue.celestial_invoker.content.generator;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateProvider;
import dev.xkmc.l2library.serial.config.RecordDataProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RegistrateRecordDataProvider extends RecordDataProvider implements RegistrateProvider {

    private final AbstractRegistrate<?> owner;
    public final Map<String, Record> map = new HashMap<>();

    public RegistrateRecordDataProvider(DataGenerator generator, AbstractRegistrate<?> owner) {
        super(generator, "Record Data Registrate Provider");
        this.owner = owner;
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    }

    @Override
    public void add(BiConsumer<String, Record> cons) {
        owner.genData(GeneratorTypes.RECORD_DATA, this);
        map.forEach(cons);
    }
}
