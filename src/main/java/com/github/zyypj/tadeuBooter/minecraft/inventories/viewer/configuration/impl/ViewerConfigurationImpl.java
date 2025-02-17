package com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.configuration.impl;

import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.configuration.border.Border;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.configuration.ViewerConfiguration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Accessors(fluent = true)
@Data
public abstract class ViewerConfigurationImpl implements ViewerConfiguration {

    private String titleInventory;
    private int inventorySize;
    private String backInventory;

    public static class Simple extends ViewerConfigurationImpl {
        // empty implementation
    }

    @Accessors(fluent = true)
    @Setter @Getter
    public static class Paged extends ViewerConfigurationImpl {

        private int itemPageLimit;
        private Border border;

        private int nextPageSlot;
        private int previousPageSlot;
        private int emptyPageSlot;

    }

    public static class Global extends ViewerConfigurationImpl {
        // empty implementation
    }

}
