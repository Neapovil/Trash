package com.github.neapovil.trash;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;

import dev.jorel.commandapi.CommandAPICommand;

public final class Trash extends JavaPlugin
{
    private static Trash instance;

    @Override
    public void onEnable()
    {
        instance = this;

        new CommandAPICommand("trash")
                .withPermission("trash.command.gui")
                .executesPlayer((player, args) -> {
                    final ChestGui gui = new ChestGui(1, "Trash");
                    final Pattern pattern = new Pattern("111101111");
                    final PatternPane pane = new PatternPane(0, 0, 9, 1, pattern);
                    final GuiItem guiitem = new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

                    guiitem.setAction(event -> event.setCancelled(true));

                    pane.bindItem('1', guiitem);

                    gui.addPane(pane);

                    gui.setOnBottomClick(event -> {
                        if (!event.getClick().equals(ClickType.SHIFT_LEFT))
                        {
                            return;
                        }

                        if (event.getCurrentItem() == null)
                        {
                            return;
                        }

                        event.getCurrentItem().setAmount(0);
                    });

                    gui.setOnOutsideClick(event -> event.setCancelled(true));

                    gui.setOnTopClick(event -> {
                        if (event.getCursor() == null)
                        {
                            return;
                        }

                        if (event.getCurrentItem() != null)
                        {
                            return;
                        }

                        if (event.getClick().equals(ClickType.RIGHT))
                        {
                            event.getCursor().setAmount(event.getCursor().getAmount() - 1);
                            event.setCancelled(true);
                            return;
                        }

                        event.getCursor().setAmount(0);
                    });

                    gui.show(player);
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
