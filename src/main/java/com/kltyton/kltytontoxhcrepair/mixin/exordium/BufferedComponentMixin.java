package com.kltyton.kltytontoxhcrepair.mixin.exordium;

import com.kltyton.kltytontoxhcrepair.util.IBufferedComponent;
import dev.tr7zw.exordium.render.BufferedComponent;

import org.spongepowered.asm.mixin.*;

@Pseudo
@Mixin(BufferedComponent.class)
@Implements(@Interface(iface = IBufferedComponent.class, prefix = "custom$"))
public abstract class BufferedComponentMixin implements IBufferedComponent {
    @Unique
    private boolean isCrosshair = false;
    @Unique
    public boolean custom$getCrosshair() {
        return this.isCrosshair;
    }

    @Unique
    public void custom$setCrosshair(boolean isCrosshair) {
        this.isCrosshair = isCrosshair;
    }
}
