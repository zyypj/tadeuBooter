package com.github.zyypj.tadeuBooter.minecraft.tool;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemsUtil {

    /**
     * Converte um ItemStack em uma String contendo todas as suas informações.
     *
     * @param item ItemStack a ser convertido.
     * @return String representando todas as propriedades do item.
     */
    public static String itemStackToString(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return "ItemStack{empty}";

        StringBuilder sb = new StringBuilder();

        sb.append("ItemStack{")
                .append("Material=").append(item.getType())
                .append(", Amount=").append(item.getAmount())
                .append(", Durability=").append(item.getDurability());

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta.hasDisplayName()) {
                sb.append(", DisplayName='").append(itemMeta.getDisplayName()).append("'");
            }

            if (itemMeta.hasLore()) {
                sb.append(", Lore=").append(itemMeta.getLore());
            }

            if (!itemMeta.getEnchants().isEmpty()) {
                sb.append(", Enchantments=").append(formatEnchantments(itemMeta.getEnchants()));
            }

            if (!item.getEnchantments().isEmpty()) {
                sb.append(", UnsafeEnchantments=").append(formatEnchantments(item.getEnchantments()));
            }

            if (itemMeta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
                sb.append(", LeatherColor=").append(leatherMeta.getColor());
            }

            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                String skullValue = extractSkullValue(skullMeta);
                if (skullValue != null) {
                    sb.append(", SkullValue='").append(skullValue).append("'");
                } else if (skullMeta.hasOwner()) {
                    sb.append(", SkullOwner='").append(skullMeta.getOwner()).append("'");
                }
            }

            if (!itemMeta.getItemFlags().isEmpty()) {
                sb.append(", ItemFlags=").append(itemMeta.getItemFlags());
            }
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Converte uma string (gerada pelo método itemStackToString) de volta para um ItemStack.
     *
     * @param input A string que representa o ItemStack.
     * @return O ItemStack reconstruído.
     */
    public static ItemStack stringToItemStack(String input) {
        if (input == null || !input.startsWith("ItemStack{") || !input.endsWith("}"))
            return new ItemStack(Material.AIR);

        String content = input.substring("ItemStack{".length(), input.length() - 1);

        Material material = Material.AIR;
        int amount = 1;
        short durability = 0;
        String displayName = null;
        List<String> lore = null;
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        Map<Enchantment, Integer> unsafeEnchantments = new HashMap<>();
        Color leatherColor = null;
        String skullValue = null;
        String skullOwner = null;
        List<ItemFlag> itemFlags = new ArrayList<>();

        Pattern materialPattern = Pattern.compile("Material=([^,]+)");
        Pattern amountPattern = Pattern.compile("Amount=(\\d+)");
        Pattern durabilityPattern = Pattern.compile("Durability=(\\d+)");
        Pattern displayNamePattern = Pattern.compile("DisplayName='([^']*)'");
        Pattern lorePattern = Pattern.compile("Lore=\\[([^]]*)]");
        Pattern enchantmentsPattern = Pattern.compile("Enchantments=([^,}]+)");
        Pattern unsafeEnchantmentsPattern = Pattern.compile("UnsafeEnchantments=([^,}]+)");
        Pattern leatherColorPattern = Pattern.compile("LeatherColor=([^,}]+)");
        Pattern skullValuePattern = Pattern.compile("SkullValue='([^']*)'");
        Pattern skullOwnerPattern = Pattern.compile("SkullOwner='([^']*)'");
        Pattern itemFlagsPattern = Pattern.compile("ItemFlags=([^,}]+)");

        Matcher m = materialPattern.matcher(content);
        if (m.find()) {
            try {
                material = Material.valueOf(m.group(1));
            } catch (Exception e) {
                material = Material.BARRIER;
            }
        }
        m = amountPattern.matcher(content);
        if (m.find()) {
            amount = Integer.parseInt(m.group(1));
        }
        m = durabilityPattern.matcher(content);
        if (m.find()) {
            durability = Short.parseShort(m.group(1));
        }
        m = displayNamePattern.matcher(content);
        if (m.find()) {
            displayName = m.group(1);
        }
        m = lorePattern.matcher(content);
        if (m.find()) {
            String loreString = m.group(1).trim();
            if (!loreString.isEmpty()) {
                lore = Arrays.asList(loreString.split(", "));
            }
        }
        m = enchantmentsPattern.matcher(content);
        if (m.find()) {
            String enchStr = m.group(1).trim();
            if (!enchStr.equals("None")) {
                String[] parts = enchStr.split(", ");
                for (String part : parts) {
                    String[] keyVal = part.split(":");
                    if (keyVal.length == 2) {
                        try {
                            Enchantment ench = Enchantment.getByName(keyVal[0]);
                            int level = Integer.parseInt(keyVal[1]);
                            if (ench != null) {
                                enchantments.put(ench, level);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        m = unsafeEnchantmentsPattern.matcher(content);
        if (m.find()) {
            String enchStr = m.group(1).trim();
            if (!enchStr.equals("None")) {
                String[] parts = enchStr.split(", ");
                for (String part : parts) {
                    String[] keyVal = part.split(":");
                    if (keyVal.length == 2) {
                        try {
                            Enchantment ench = Enchantment.getByName(keyVal[0]);
                            int level = Integer.parseInt(keyVal[1]);
                            if (ench != null) {
                                unsafeEnchantments.put(ench, level);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        m = leatherColorPattern.matcher(content);
        if (m.find()) {
            String colorString = m.group(1).trim();
            Pattern rPattern = Pattern.compile("r=\\s*(\\d+)");
            Pattern gPattern = Pattern.compile("g=\\s*(\\d+)");
            Pattern bPattern = Pattern.compile("b=\\s*(\\d+)");
            Matcher rm = rPattern.matcher(colorString);
            Matcher gm = gPattern.matcher(colorString);
            Matcher bm = bPattern.matcher(colorString);
            if (rm.find() && gm.find() && bm.find()) {
                int rVal = Integer.parseInt(rm.group(1));
                int gVal = Integer.parseInt(gm.group(1));
                int bVal = Integer.parseInt(bm.group(1));
                leatherColor = Color.fromRGB(rVal, gVal, bVal);
            }
        }
        m = skullValuePattern.matcher(content);
        if (m.find()) {
            skullValue = m.group(1);
        }
        m = skullOwnerPattern.matcher(content);
        if (m.find()) {
            skullOwner = m.group(1);
        }
        m = itemFlagsPattern.matcher(content);
        if (m.find()) {
            String flagsString = m.group(1).trim();
            String[] flags = flagsString.split(", ");
            for (String flag : flags) {
                try {
                    ItemFlag itemFlag = ItemFlag.valueOf(flag);
                    itemFlags.add(itemFlag);
                } catch (Exception e) {
                }
            }
        }

        ItemBuilder builder = new ItemBuilder(material);
        if (displayName != null) {
            builder.setDisplayName(displayName);
        }
        if (lore != null) {
            builder.setLore(lore);
        }
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            builder.addEnchant(entry.getKey(), entry.getValue());
        }

        ItemStack item = builder.build();
        for (Map.Entry<Enchantment, Integer> entry : unsafeEnchantments.entrySet()) {
            item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }
        if (leatherColor != null) {
            builder.setLeatherColor(leatherColor);
            item = builder.build();
        }
        if (skullValue != null) {
            builder.setSkullValue(skullValue);
            item = builder.build();
        } else if (skullOwner != null) {
            builder.setSkullOwner(skullOwner);
            item = builder.build();
        }
        if (!itemFlags.isEmpty()) {
            builder.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
            item = builder.build();
        }
        item.setAmount(amount);
        item.setDurability(durability);

        return item;
    }

    /**
     * Formata os encantamentos para exibição legível.
     *
     * @param enchants Mapa de encantamentos do item.
     * @return String formatada com os encantamentos.
     */
    public static String formatEnchantments(Map<Enchantment, Integer> enchants) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            sb.append(entry.getKey().getName()).append(":").append(entry.getValue()).append(", ");
        }
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : "None";
    }

    /**
     * Extrai o valor de textura (Base64) de uma cabeça de jogador.
     *
     * @param skullMeta SkullMeta do item.
     * @return String contendo o valor da textura ou null se não existir.
     */
    public static String extractSkullValue(SkullMeta skullMeta) {
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            Object profile = profileField.get(skullMeta);
            if (profile != null) {
                Method getProperties = profile.getClass().getMethod("getProperties");
                Object properties = getProperties.invoke(profile);
                for (Object property : (Iterable<?>) properties) {
                    Method getNameMethod = property.getClass().getMethod("getName");
                    String name = (String) getNameMethod.invoke(property);
                    if ("textures".equals(name)) {
                        Method getValueMethod = property.getClass().getMethod("getValue");
                        return (String) getValueMethod.invoke(property);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}