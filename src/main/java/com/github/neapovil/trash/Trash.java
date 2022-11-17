package com.github.neapovil.trash;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;

public final class Trash extends JavaPlugin implements Listener
{
    private static Trash instance;
    private final NamespacedKey key = new NamespacedKey(this, "trash");

    @Override
    public void onEnable()
    {
        instance = this;

        this.getServer().getPluginManager().registerEvents(this, this);

        new CommandAPICommand("trash")
                .withPermission("trash.command.gui")
                .executesPlayer((player, args) -> {
                    final Inventory inventory = this.getServer().createInventory(player, 9, Component.text("Trash"));

                    player.getPersistentDataContainer().set(this.key, PersistentDataType.INTEGER, inventory.hashCode());

                    player.openInventory(inventory);
                })
                .register();
    }

    @Override
    public void onDisable()
    {
    }

    public static Trash getInstance()
    {
        return instance;
    }

    @EventHandler
    private void inventoryClose(InventoryCloseEvent event)
    {
        if (this.isTrash((Player) event.getPlayer(), event.getInventory().hashCode()))
        {
            event.getPlayer().getPersistentDataContainer().remove(this.key);
        }
    }

    @EventHandler
    private void topClick(InventoryClickEvent event)
    {
        if (!this.isTrash((Player) event.getWhoClicked(), event.getInventory().hashCode()))
        {
            return;
        }

        if (event.getClickedInventory() == null)
        {
            return;
        }

        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))
        {
            return;
        }

        if (event.getCursor() == null)
        {
            return;
        }

        event.setCancelled(true);

        if (event.isRightClick())
        {
            event.getCursor().subtract();
        }

        if (event.isLeftClick())
        {
            event.getCursor().setAmount(0);
        }
    }

    @EventHandler
    private void shiftClick(InventoryClickEvent event)
    {
        if (!this.isTrash((Player) event.getWhoClicked(), event.getInventory().hashCode()))
        {
            return;
        }

        if (!event.isShiftClick())
        {
            return;
        }

        if (event.getCurrentItem() == null)
        {
            return;
        }

        event.setCancelled(true);

        event.getCurrentItem().setAmount(0);
    }

    private boolean isTrash(Player player, int inventoryHashcode)
    {
        if (!player.getPersistentDataContainer().has(this.key))
        {
            return false;
        }

        final int hashcode = player.getPersistentDataContainer().get(this.key, PersistentDataType.INTEGER);

        if (inventoryHashcode != hashcode)
        {
            return false;
        }

        return true;
    }
}
