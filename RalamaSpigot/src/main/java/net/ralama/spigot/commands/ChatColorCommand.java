package net.ralama.spigot.commands;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChatColorCommand implements CommandExecutor {
    private Inventory inv;

    public static void openMagicSettings(Player player) {
        // TODO: Magic settings inventory
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;

        Player sender = (Player) commandSender;

        inv = Bukkit.createInventory(sender, 27, "Choose your color in chat");
        ItemStack frame = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        for (int i : new int[]{0, 1, 2, 3, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26, 26}) {
            inv.setItem(i, frame);
        }

        ItemStack book = XMaterial.BOOK.parseItem();
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName("§b§lC§3§lh§9§la§1§lt§4§lC§c§lo§6§ll§e§lo§a§lr");

        List<String> bookLore = new ArrayList<>();
        bookLore.add("§7In this inventory, you can choose your §bChatColor§7.");
        bookLore.add("§7Please consider donating for more colors.");
        bookLore.add("§b§nhttps://discord.ralama.net/");
        bookMeta.setLore(bookLore);

        book.setItemMeta(bookMeta);
        inv.setItem(4, book);

        addColor(XMaterial.WHITE_DYE, "§f§lWhite");
        addColor(XMaterial.LIGHT_BLUE_DYE, "§b§lAqua");
        addColor(XMaterial.PINK_DYE, "§d§lPink");
        addColor(XMaterial.MAGENTA_DYE, "§5§lPurple");
        addColor(XMaterial.LIME_DYE, "§a§lLime");
        addColor(XMaterial.GREEN_DYE, "§2§lGreen");
        addColor(XMaterial.RED_DYE, "§C§LRed");
        addColor(XMaterial.REDSTONE, "§4§lDark Red");
        addColor(XMaterial.COMPARATOR, "§6Magic Settings");

        sender.playSound(sender.getLocation(), XSound.BLOCK_CHEST_OPEN.parseSound(), 1.0f, 1.0f);
        sender.openInventory(inv);
        return true;
    }

    private void addColor(XMaterial color, String name) {

        ItemStack is = color.parseItem();
        ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(name);
        is.setItemMeta(meta);

        inv.addItem(is);
    }
}
