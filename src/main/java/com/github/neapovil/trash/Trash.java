package com.github.neapovil.trash;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class Trash extends JavaPlugin
{
    private static Trash instance;
    private Map<UUID, BukkitTask> task = new HashMap<>();

    @Override
    public void onEnable()
    {
        instance = this;

        new CommandAPICommand("trash")
                .withPermission("trash.command.gui")
                .executesPlayer((player, args) -> {
                    final ChestGui gui = new ChestGui(1, "Trash");

                    task.put(player.getUniqueId(), this.getServer().getScheduler().runTaskTimer(this, () -> gui.getInventory().clear(), 0, 1));

                    gui.setOnClose(event -> {
                        task.remove(player.getUniqueId()).cancel();
                    });

                    gui.show(player);
                })
                .register();

        new CommandAPICommand("trash")
                .withArguments(new LiteralArgument("all").withPermission("trash.command.all"))
                .executesPlayer((player, args) -> {
                    player.getInventory().clear();
                    player.sendMessage("Inventory cleared.");
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
}
