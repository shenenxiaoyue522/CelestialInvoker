package com.xiaoyue.celestial_invoker.invoker.subscribe;

import com.xiaoyue.celestial_invoker.CelestialInvoker;
import net.minecraftforge.fml.IModLoadingState;
import net.minecraftforge.fml.IModStateProvider;
import net.minecraftforge.fml.ModLoadingPhase;
import net.minecraftforge.fml.ModLoadingState;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.util.List;

public class CelestialStatesProvider implements IModStateProvider {
    private static List<ModFileScanData> scanDataList;

    public static List<ModFileScanData> getScanDataList() {
        if (scanDataList.isEmpty()) {
            CelestialInvoker.LOGGER.error("Mod scan data is empty, this is a error");
        }
        return scanDataList;
    }

    final ModLoadingState GET_SUBSCRIBE_DATA = ModLoadingState.withInline("GET_SUBSCRIBE_DATA", "INJECT_CAPABILITIES",
            ModLoadingPhase.GATHER, mods -> scanDataList = mods.getAllScanData());

    @Override
    public List<IModLoadingState> getAllStates() {
        return List.of(GET_SUBSCRIBE_DATA);
    }
}
