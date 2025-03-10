package com.github.zyypj.tadeuBooter.diagnostics.attack;

import com.github.zyypj.tadeuBooter.logging.LoggerUtils;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;

public class AttackManager {
    private static final Map<String, AttackMethod> activeAttacks = new HashMap<>();

    /**
     * Inicia um ataque específico.
     *
     * @param attack O ataque a ser iniciado.
     */
    public static void startAttack(AttackMethod attack) {
        if (activeAttacks.containsKey(attack.target)) {
            LoggerUtils.log("Já existe um ataque ativo contra " + attack.target);
            return;
        }

        attack.start();
        activeAttacks.put(attack.target, attack);
        LoggerUtils.log("Ataque iniciado contra " + attack.target);
    }

    /**
     * Para um ataque específico.
     *
     * @param target O alvo do ataque a ser parado.
     */
    public static void stopAttack(String target) {
        AttackMethod attack = activeAttacks.remove(target);
        if (attack != null) {
            attack.stop();
            LoggerUtils.log("Ataque encerrado contra " + target);
        }
    }

    /**
     * Para todos os ataques ativos.
     */
    public static void stopAllAttacks() {
        for (String target : activeAttacks.keySet()) {
            stopAttack(target);
        }
        LoggerUtils.log("Todos os ataques foram encerrados.");
    }
}