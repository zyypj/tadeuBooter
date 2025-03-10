package com.github.zyypj.tadeuBooter.minecraft.serialization;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;

/**
 * Classe utilitária para serializar e desserializar objetos de Inventory e ItemStack do Bukkit.
 * Fornece métodos para codificar esses objetos em arrays de bytes ou strings Base64 para armazenamento
 * ou transferência, e decodificá-los de volta para sua forma original.
 */
public final class InventorySerialization {

    private InventorySerialization() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }

    /**
     * Converte um inventário em uma String..
     *
     * @param inventory O inventário a ser convertido.
     * @return String contendo os dados do inventário.
     */
    public static String inventoryToString(Inventory inventory) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("size", inventory.getSize());

        for (int i = 0; i < inventory.getSize(); i++) {
            config.set("items." + i, inventory.getItem(i));
        }

        StringWriter writer = new StringWriter();
        try {
            config.save(String.valueOf(writer));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao salvar o inventário para String", e);
        }

        return writer.toString();
    }

    /**
     * Converte uma String para um inventário do Bukkit.
     *
     * @param data  A string do inventário salvo.
     * @param title O título do inventário ao recriá-lo.
     * @return O inventário restaurado.
     */
    public static Inventory inventoryFromString(String data, String title) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("A string do inventário não pode ser nula ou vazia.");
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(data));
        int size = config.getInt("size");
        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (int i = 0; i < size; i++) {
            if (config.contains("items." + i)) {
                inventory.setItem(i, config.getItemStack("items." + i));
            }
        }

        return inventory;
    }

    /**
     * Serializa um único ItemStack em um array de bytes.
     *
     * @param item o ItemStack a ser serializado
     * @return o array de bytes serializado
     */
    public static byte[] encodeItemStack(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(item);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao codificar o ItemStack", e);
        }
    }

    /**
     * Serializa um único ItemStack em uma string Base64.
     *
     * @param item o ItemStack a ser serializado
     * @return a string codificada em Base64
     */
    public static String encodeItemStackToString(ItemStack item) {
        return Base64Util.encode(encodeItemStack(item));
    }

    /**
     * Desserializa um único ItemStack de um array de bytes.
     *
     * @param buf o array de bytes a ser desserializado
     * @return o ItemStack desserializado
     */
    public static ItemStack decodeItemStack(byte[] buf) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Falha ao decodificar o ItemStack", e);
        }
    }

    /**
     * Desserializa um único ItemStack de uma string Base64.
     *
     * @param data a string codificada em Base64
     * @return o ItemStack desserializado
     */
    public static ItemStack decodeItemStack(String data) {
        return decodeItemStack(Base64Util.decode(data));
    }

    /**
     * Serializa um array de ItemStacks em um array de bytes.
     *
     * @param items o array de ItemStacks a ser serializado
     * @return o array de bytes serializado
     */
    public static byte[] encodeItemStacks(ItemStack[] items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao codificar os ItemStacks", e);
        }
    }

    /**
     * Serializa um array de ItemStacks em uma string Base64.
     *
     * @param items o array de ItemStacks a ser serializado
     * @return a string codificada em Base64
     */
    public static String encodeItemStacksToString(ItemStack[] items) {
        return Base64Util.encode(encodeItemStacks(items));
    }

    /**
     * Desserializa um array de ItemStacks de um array de bytes.
     *
     * @param buf o array de bytes a ser desserializado
     * @return o array desserializado de ItemStacks
     */
    public static ItemStack[] decodeItemStacks(byte[] buf) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            return items;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Falha ao decodificar os ItemStacks", e);
        }
    }

    /**
     * Desserializa um array de ItemStacks de uma string Base64.
     *
     * @param data a string codificada em Base64
     * @return o array desserializado de ItemStacks
     */
    public static ItemStack[] decodeItemStacks(String data) {
        return decodeItemStacks(Base64Util.decode(data));
    }

    /**
     * Serializa um Inventory em um array de bytes.
     *
     * @param inventory o Inventory a ser serializado
     * @return o array de bytes serializado
     */
    public static byte[] encodeInventory(Inventory inventory) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao codificar o Inventory", e);
        }
    }

    /**
     * Serializa um Inventory em uma string Base64.
     *
     * @param inventory o Inventory a ser serializado
     * @return a string codificada em Base64
     */
    public static String encodeInventoryToString(Inventory inventory) {
        return Base64Util.encode(encodeInventory(inventory));
    }

    /**
     * Desserializa um Inventory de um array de bytes.
     *
     * @param buf   o array de bytes a ser desserializado
     * @param title o título do Inventory a ser criado
     * @return o Inventory desserializado
     */
    public static Inventory decodeInventory(byte[] buf, String title) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int size = dataInput.readInt();
            Inventory inventory = Bukkit.getServer().createInventory((InventoryHolder) null, size, title);
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Falha ao decodificar o Inventory", e);
        }
    }

    /**
     * Desserializa um Inventory de uma string Base64.
     *
     * @param data  a string codificada em Base64
     * @param title o título do Inventory a ser criado
     * @return o Inventory desserializado
     */
    public static Inventory decodeInventory(String data, String title) {
        return decodeInventory(Base64Util.decode(data), title);
    }
}