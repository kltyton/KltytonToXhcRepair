package com.kltyton.kltytontoxhcrepair.mixin.exordium;

import com.kltyton.kltytontoxhcrepair.util.IBufferedComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.render.BufferedComponent;
import dev.tr7zw.exordium.render.Model;
import dev.tr7zw.exordium.util.CustomShaderManager;
import dev.tr7zw.exordium.util.DelayedRenderCallManager;
import dev.tr7zw.exordium.util.rendersystem.MultiStateHolder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Pseudo
@Mixin(DelayedRenderCallManager.class)
public class DelayedRenderCallManagerMixin {
    @Shadow
    @Final
    private MultiStateHolder stateHolder;

    @Shadow
    @Final
    private List<BufferedComponent> componentRenderCalls;

    @Inject(method = "renderComponents", at = @At("HEAD"), remap = false, cancellable = true)
    public void renderComponents(CallbackInfo ci) {
        ci.cancel();

        // 保存当前 OpenGL 状态
        this.stateHolder.fetch();

        CustomShaderManager shaderManager = ExordiumModBase.instance.getCustomShaderManager();
        RenderSystem.disableDepthTest();   // UI 不参与深度测试
        RenderSystem.depthMask(false);     // 不写深度缓冲
        RenderSystem.enableBlend();        // 开启混合
        Objects.requireNonNull(shaderManager);
        RenderSystem.setShader(shaderManager::getPositionMultiTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Model model = BufferedComponent.getModel();

        List<BufferedComponent> crosshairComponents = new ArrayList<>();
        List<BufferedComponent> normalComponents = new ArrayList<>();
        for (BufferedComponent component : this.componentRenderCalls) {
            if (((IBufferedComponent) component).getCrosshair()) {
                crosshairComponents.add(component);
            } else {
                normalComponents.add(component);
            }
        }

        if (!normalComponents.isEmpty()) {
            RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );
            drawBatch(normalComponents, shaderManager, model);
        }

        if (!crosshairComponents.isEmpty()) {
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );
            drawBatch(crosshairComponents, shaderManager, model);
        }

        this.stateHolder.apply();
        this.componentRenderCalls.clear();
    }

    /**
     * 批量绘制一组组件（按 8 张纹理为一批）
     */
    @Unique
    private void drawBatch(List<BufferedComponent> components, CustomShaderManager shaderManager, Model model) {
        int textureId = 0;
        for (BufferedComponent component : components) {
            RenderSystem.setShaderTexture(textureId, component.getTextureId());
            ++textureId;

            if (textureId == 8) {
                shaderManager.getPositionMultiTexTextureCountUniform().set(8);
                model.draw(RenderSystem.getModelViewMatrix());
                textureId = 0;
            }
        }
        if (textureId > 0) {
            shaderManager.getPositionMultiTexTextureCountUniform().set(textureId);
            model.draw(RenderSystem.getModelViewMatrix());
        }
    }

}
