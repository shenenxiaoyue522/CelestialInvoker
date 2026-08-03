package com.xiaoyue.celestial_invoker.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xiaoyue.celestial_invoker.mixin.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.HashSet;
import java.util.Set;

import static com.xiaoyue.celestial_invoker.CelestialInvoker.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {

    private final static Set<BlockPos> highlightBlocks = new HashSet<>();

    public static void addHighlightBlock(BlockPos pos) {
        highlightBlocks.add(pos);
    }

    public static void removeHighlightBlock(BlockPos pos) {
        highlightBlocks.remove(pos);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (highlightBlocks.isEmpty()) return;
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)) {
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            LevelRenderer levelRenderer = mc.levelRenderer;
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            VertexConsumer lines = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            for (BlockPos pos : highlightBlocks) {
                BlockState state = mc.level.getBlockState(pos);
                if (mc.getCameraEntity() != null) {
                    ((LevelRendererAccessor) levelRenderer).callRenderHitOutline(poseStack, lines, mc.getCameraEntity(),
                            cameraPos.x, cameraPos.y, cameraPos.z, pos, state);
                }
            }
            poseStack.popPose();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
        }
    }
}
