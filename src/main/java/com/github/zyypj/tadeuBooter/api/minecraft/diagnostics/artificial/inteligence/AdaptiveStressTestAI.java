package com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.artificial.inteligence;

import com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.methods.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.DiagnosticTools;
import com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.AttackManager;
import com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.AttackMethod;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdaptiveStressTestAI {

    private static final double TPS_LIMIT = 16.0;
    private static final int ENTITY_LIMIT = 500;
    private static boolean running = false;
    private static JavaPlugin plugin;
    private static final Random random = new Random();
    private static int attackIntensity = 10;
    private static final List<AttackMethod> attackQueue = new ArrayList<>();
    private static final List<String> executedTests = new ArrayList<>();
    private static String CHATGPT_API_KEY = "";

    public static void start(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        running = true;
        Bukkit.getLogger().info("[AdaptiveStressTestAI] IA iniciada! Ajustando ataques...");

        DiagnosticTools.startTPSMonitor(plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!running) return;

            double tps = getTPS();
            int entityCount = countEntities();

            if (tps < TPS_LIMIT) {
                Bukkit.getLogger().warning("[AdaptiveStressTestAI] ⚠️ TPS caiu para " + tps + "! Parando ataques...");
                stopAttacks();
                analyzeAndRecommend();
                return;
            }

            if (entityCount > ENTITY_LIMIT) {
                Bukkit.getLogger().warning("[AdaptiveStressTestAI] ⚠️ Muitas entidades carregadas (" + entityCount + "). Considere otimizar as configurações.");
            }

            executeSmartAttack();

        }, 0L, 200L);
    }

    public static void stop() {
        running = false;
        stopAttacks();
        Bukkit.getLogger().info("[AdaptiveStressTestAI] IA de Testes foi desativada.");
    }

    /**
     * Define a chave da API do ChatGPT para comunicação.
     */
    public static void setCGPTApi(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Bukkit.getLogger().warning("[AdaptiveStressTestAI] ❌ Chave de API inválida!");
            return;
        }
        CHATGPT_API_KEY = apiKey.trim();
        Bukkit.getLogger().info("[AdaptiveStressTestAI] ✅ Chave da API do ChatGPT definida com sucesso!");
    }

    private static void executeSmartAttack() {
        AttackMethod attack = getSmartAttack();
        if (attack != null) {
            Bukkit.getLogger().info("[AdaptiveStressTestAI] 🚀 Executando ataque: "
                    + attack.getClass().getSimpleName() + " com intensidade " + attackIntensity);
            AttackManager.startAttack(attack);
            attackQueue.add(attack);
            executedTests.add("Executado: " + attack.getClass().getSimpleName() + " com intensidade " + attackIntensity);
            attackIntensity += 5;
        }
    }

    private static void stopAttacks() {
        for (AttackMethod attack : attackQueue) {
            AttackManager.stopAttack(attack.getTarget());
        }
        attackQueue.clear();
        attackIntensity = 10;
        Bukkit.getLogger().info("[AdaptiveStressTestAI] 🛑 Todos os ataques foram interrompidos.");
    }

    private static double getTPS() {
        String tpsString = DiagnosticTools.getTPS();
        Pattern pattern = Pattern.compile("TPS Atual:\\s*([\\d\\.]+)");
        Matcher matcher = pattern.matcher(tpsString);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 20.0;
            }
        }
        return 20.0;
    }

    private static int countEntities() {
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            count += world.getEntities().size();
        }
        return count;
    }

    private static AttackMethod getSmartAttack() {
        List<AttackMethod> attacks = Arrays.asList(
                new PingFloodAttack("localhost", 10, attackIntensity, plugin),
                new PacketSpamAttack("localhost", 10, attackIntensity, plugin),
                new FakePlayersAttack("localhost", 10, attackIntensity / 2),
                new CommandSpamAttack("localhost", 10, attackIntensity / 2, plugin),
                new ChatFloodAttack("localhost", 10, attackIntensity / 2, plugin),
                new EntitySpawnFloodAttack("localhost", 10, attackIntensity, plugin),
                new BlockUpdateFloodAttack("localhost", 10, attackIntensity / 2, plugin)
        );
        return attacks.get(random.nextInt(attacks.size()));
    }

    private static void analyzeAndRecommend() {
        Bukkit.getLogger().info("[AdaptiveStressTestAI] 🔍 Iniciando análise e recomendação...");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String recommendation = getChatGPTRecommendation();
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getLogger().info("[AdaptiveStressTestAI] 🤖 Resposta da IA: \n" + recommendation);
            });
        });
    }

    private static String getChatGPTRecommendation() {
        try {
            String serverStatus = "TPS: " + getTPS() + ", Entidades carregadas: "
                    + countEntities() + ", Jogadores online: " + Bukkit.getOnlinePlayers().size();
            String memoryUsage = DiagnosticTools.getMemoryUsage();
            String systemInfo = DiagnosticTools.getSystemInfo();
            String pluginsInfo = getPluginsInfo();
            String executedTestsInfo = String.join("\n", executedTests);
            String configs = readConfigs();

            String prompt = "O servidor Minecraft apresentou queda de TPS. Aqui estão as informações detalhadas:\n" +
                    "Configurações:\n" + configs + "\n" +
                    "Status do Servidor:\n" + serverStatus + "\n" +
                    "Uso de Memória:\n" + memoryUsage + "\n" +
                    "Informações do Sistema:\n" + systemInfo + "\n" +
                    "Plugins instalados:\n" + pluginsInfo + "\n" +
                    "Testes executados:\n" + executedTestsInfo + "\n" +
                    "Com base nessas informações, sugira melhorias específicas nas configurações (spigot.yml, bukkit.yml, paper.yml, server.properties) e recomenda plugins para otimização.";

            return ChatGPTClient.sendMessage(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao conectar com a API do ChatGPT.";
        }
    }

    private static String getPluginsInfo() {
        StringBuilder plugins = new StringBuilder();
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            plugins.append(p.getName()).append(" (")
                    .append(p.getDescription().getVersion()).append(")\n");
        }
        return plugins.toString();
    }

    private static String readConfigs() {
        return readFile("spigot.yml") + "\n\n" +
                readFile("bukkit.yml") + "\n\n" +
                readFile("paper.yml") + "\n\n" +
                readFile("server.properties");
    }

    private static String readFile(String fileName) {
        File file = new File(plugin.getDataFolder().getParent(), fileName);
        if (!file.exists()) return "[Arquivo " + fileName + " não encontrado.]";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            return "[Erro ao ler " + fileName + "]";
        }
    }

    public static class ChatGPTClient {
        private static final int MAX_RETRIES = 3;

        public static String sendMessage(String prompt) throws IOException {
            if (CHATGPT_API_KEY.isEmpty()) {
                Bukkit.getLogger().warning("[ChatGPTClient] ❌ Nenhuma chave de API definida! Use setCGPTApi().");
                return "Erro: API Key não definida.";
            }
            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                try {
                    URL url = new URL("https://api.openai.com/v1/chat/completions");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "Bearer " + CHATGPT_API_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    String jsonInput = "{ \"model\": \"gpt-4\", \"messages\": [{\"role\": \"user\", \"content\": \""
                            + prompt.replace("\"", "\\\"") + "\"}]}";

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonInput.getBytes());
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        Bukkit.getLogger().warning("[ChatGPTClient] Erro na resposta: " + responseCode);
                        attempt++;
                        continue;
                    }

                    StringBuilder response = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line.trim());
                        }
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(response.toString()).getAsJsonObject();
                    return jsonObject.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                } catch (Exception e) {
                    attempt++;
                    Bukkit.getLogger().warning("[ChatGPTClient] Tentativa " + attempt + " falhou: " + e.getMessage());
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                }
            }
            return "Erro: Não foi possível obter resposta do ChatGPT após várias tentativas.";
        }
    }
}