package com.github.zyypj.tadeuBooter.database.controler.player;

import com.github.zyypj.tadeuBooter.database.controler.Controller;
import com.github.zyypj.tadeuBooter.database.controler.player.reason.UnloadReason;
import com.github.zyypj.tadeuBooter.database.dao.DAO;
import com.github.zyypj.tadeuBooter.minecraft.object.Cache;
import lombok.NonNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public abstract class PlayerController<V, C extends Cache<UUID, V, ?>, D extends DAO<UUID, V>> extends Controller<UUID, V, C, D> {

    public PlayerController(@NonNull C cache, @NonNull D dao) {
        super(cache, dao);
    }

    public PlayerController(@NonNull C cache, @NonNull D dao, @NonNull Logger logger) {
        super(cache, dao, logger);
    }

    public abstract CompletableFuture<Void> loadPlayer(@NonNull UUID id);

    public abstract CompletableFuture<Void> unloadPlayer(@NonNull UUID id, @NonNull UnloadReason reason);
}