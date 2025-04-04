package com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack;

import com.github.zyypj.tadeuBooter.api.minecraft.logger.Debug;

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
            Debug.log("&a[MRK-CORE-Attack]Já existe um ataque ativo contra " + attack.target, false);
            return;
        }

        attack.start();
        activeAttacks.put(attack.target, attack);
        Debug.log("&a[MRK-CORE-Attack]Ataque iniciado contra " + attack.target, false);
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
            Debug.log("&a[MRK-CORE-Attack] Ataque encerrado contra " + target, false);
        }
    }

    /**
     * Para todos os ataques ativos.
     */
    public static void stopAllAttacks() {
        for (String target : activeAttacks.keySet()) {
            stopAttack(target);
        }
        Debug.log("&a[MRK-CORE-Attack]Todos os ataques foram encerrados.", false);
    }
}