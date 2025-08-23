package com.kltyton.kltytontoxhcrepair.mixin.machinery;

import com.kltyton.fabricmixintest.ConfigExt;
import immersive_aircraft.config.configEntries.BooleanConfigEntry;
import immersive_machinery.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Mixin(Config.class)
public abstract class ConfigMixin implements ConfigExt {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(String name, CallbackInfo ci) {
        // 获取 Config 实例
        Config config = (Config) (Object) this;

        // 创建新的可修改 Map
        Map<String, Boolean> newValidCrops = new HashMap<>();
        newValidCrops.put("minecraft:grass", true);
        // 农夫乐事 (Farmer's Delight) 作物
        newValidCrops.put("farmersdelight:cabbage_crop", true);
        newValidCrops.put("farmersdelight:tomato_crop", true);
        newValidCrops.put("farmersdelight:onion_crop", true);
        newValidCrops.put("farmersdelight:rice_crop", true);
        newValidCrops.put("farmersdelight:rice_crop_water", true);
        // 沉浸农艺 (Immersive Agriculture) 作物
        newValidCrops.put("immersive_agriculture:wheat_crop", true);
        newValidCrops.put("immersive_agriculture:carrot_crop", true);
        newValidCrops.put("immersive_agriculture:potato_crop", true);
        newValidCrops.put("immersive_agriculture:beetroot_crop", true);
        // 作物盛景 (Croparia) 作物
        newValidCrops.put("croparia:crop_elem_0", true);
        newValidCrops.put("croparia:crop_elem_1", true);
        newValidCrops.put("croparia:crop_elem_2", true);
        newValidCrops.put("croparia:crop_elem_3", true);
        newValidCrops.put("croparia:crop_elem_4", true);
        // 其他常见模组作物
        newValidCrops.put("pamhc2crops:tomato_crop", true);
        newValidCrops.put("pamhc2crops:lettuce_crop", true);
        newValidCrops.put("pamhc2crops:corn_crop", true);
        newValidCrops.put("mysticalworld:aubergine_crop", true);
        newValidCrops.put("mysticalworld:pearl_bean_crop", true);
        newValidCrops.put("supplementaries:flax_crop", true);
        newValidCrops.put("croptopia:artichoke_crop", true);
        newValidCrops.put("croptopia:asparagus_crop", true);
        newValidCrops.put("croptopia:barley_crop", true);
        newValidCrops.put("croptopia:broccoli_crop", true);
        newValidCrops.put("croptopia:cabbage_crop", true);
        newValidCrops.put("croptopia:corn_crop", true);
        newValidCrops.put("croptopia:cucumber_crop", true);
        newValidCrops.put("croptopia:eggplant_crop", true);
        newValidCrops.put("croptopia:greenbean_crop", true);
        newValidCrops.put("croptopia:kale_crop", true);
        newValidCrops.put("croptopia:leek_crop", true);
        newValidCrops.put("croptopia:lettuce_crop", true);
        newValidCrops.put("croptopia:oat_crop", true);
        newValidCrops.put("croptopia:onion_crop", true);
        newValidCrops.put("croptopia:pepper_crop", true);
        newValidCrops.put("croptopia:radish_crop", true);
        newValidCrops.put("croptopia:rice_crop", true);
        newValidCrops.put("croptopia:spinach_crop", true);
        newValidCrops.put("croptopia:squash_crop", true);
        newValidCrops.put("croptopia:sweetpotato_crop", true);
        newValidCrops.put("croptopia:tomato_crop", true);
        newValidCrops.put("croptopia:turnip_crop", true);
        newValidCrops.put("croptopia:zucchini_crop", true);

        // 替换字段值（使用反射）
        try {
            Field validCropsField = Config.class.getDeclaredField("validCrops");
            validCropsField.setAccessible(true);
            validCropsField.set(config, newValidCrops);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("修改valid庄稼失败", e);
        }
    }
    // 支持的成熟度属性名称列表
    @Unique
    public String[] supportedMaturityProperties = {
            "age",           // 默认原版属性
            "maturity",      // 一些模组使用的属性
            "growth",        // 另一种常见属性
            "stage",         // 阶段属性
            "progress",      // 进度属性
            "level"          // 等级属性
    };

    // 是否启用模组作物的自动检测
    @Unique
    @BooleanConfigEntry(true)
    public boolean enableModdedCropAutoDetection;

    // 是否启用通过类名检测作物的功能
    @Unique
    @BooleanConfigEntry(true)
    public boolean enableCropClassDetection;
    @Unique
    @Override
    public boolean fabricMixinTest$getEnableModdedCropAutoDetection() {
        return enableModdedCropAutoDetection;
    }
    @Unique
    @Override
    public boolean fabricMixinTest$getEnableCropClassDetection() {
        return enableCropClassDetection;
    }
    @Unique
    @Override
    public String[] fabricMixinTest$getSupportedMaturityProperties() {
        return supportedMaturityProperties;
    }
}