package com.github.neapovil.trash;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;

public final class Trash extends JavaPlugin
{
    private static Trash instance;
    private BukkitTask task;

    @Override
    public void onEnable()
    {
        instance = this;

        new CommandAPICommand("trash")
                .withPermission("trash.command.gui")
                .executesPlayer((player, args) -> {
                    final ChestGui gui = new ChestGui(1, "Trash");

                    if (task == null)
                    {
                        task = this.getServer().getScheduler().runTaskTimer(this, () -> gui.getInventory().clear(), 0, 1);
                    }

                    gui.setOnClose(event -> {
                        task.cancel();
                        task = null;
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
