package com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.impl.simple;

import com.github.zyypj.tadeuBooter.minecraft.inventories.inventory.CustomInventory;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.configuration.impl.ViewerConfigurationImpl;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.impl.ViewerImpl;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class SimpleViewer extends ViewerImpl {

    public SimpleViewer(String name, CustomInventory customInventory) {
        super(name, customInventory, new ViewerConfigurationImpl.Simple());
    }

}
