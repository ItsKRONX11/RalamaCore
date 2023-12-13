package net.ralama.spigot.commands;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import net.ralama.packets.api.Language;
import net.ralama.packets.in.PlayerLanguagePacket;
import net.ralama.spigot.ItemBuilder;
import net.ralama.spigot.RalamaSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class LocaleCommand implements CommandExecutor, Listener {
    private final Inventory languageInventory;

    public LocaleCommand() {
        this.languageInventory = Bukkit.createInventory(null, 9, "Change your language"); // Romanian
        this.languageInventory.setItem(3, new ItemBuilder(XMaterial.LAVA_BUCKET.parseMaterial()).name("§9Ro§emani§can").lore("Schimba-ti limba in romana.").build());
        this.languageInventory.setItem(5, new ItemBuilder(XMaterial.WATER_BUCKET.parseMaterial()).name("§9En§fgli§esh").lore("Change your language to english").build());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;

        Player sender = (Player) commandSender;
        sender.openInventory(languageInventory);
        sender.playSound(sender.getLocation(), XSound.BLOCK_CHEST_OPEN.parseSound(), 1.0f, 1.0f);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase("Change your language")) return;

        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        player.playSound(player.getLocation(), XSound.ITEM_BUCKET_FILL.parseSound(), 1.0f, 1.0f);

        if (e.getCurrentItem().getType() == XMaterial.LAVA_BUCKET.parseMaterial()) {
            RalamaSpigot.getInstance().sendPacket(new PlayerLanguagePacket(player.getName(), Language.ROMANIAN));
        } else if (e.getCurrentItem().getType() == XMaterial.WATER_BUCKET.parseMaterial()) {
            RalamaSpigot.getInstance().sendPacket(new PlayerLanguagePacket(player.getName(), Language.ENGLISH));
        }
        player.closeInventory();
    }
}
