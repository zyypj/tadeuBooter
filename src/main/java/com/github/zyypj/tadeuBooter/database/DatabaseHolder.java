package com.github.zyypj.tadeuBooter.database;

import java.io.File;
import java.util.logging.Logger;

public interface DatabaseHolder<H> {

    String getName();

    File getDataFolder();

    Logger getLogger();

    H getHandle();
}
