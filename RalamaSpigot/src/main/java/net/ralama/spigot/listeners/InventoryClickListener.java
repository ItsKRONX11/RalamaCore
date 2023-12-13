package net.ralama.spigot.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import net.ralama.spigot.commands.ChatColorCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;


public class InventoryClickListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Choose your color in chat")) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            if (e.getCurrentItem().getType().equals(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial())
                    || e.getCurrentItem().getType().equals(XMaterial.BOOK.parseMaterial())) {
                return;
            }
            if (e.getCurrentItem().getType().equals(XMaterial.COMPARATOR.parseMaterial())) {
                player.closeInventory();
                ChatColorCommand.openMagicSettings(player);
                return;
            }
            player.playSound(player.getLocation(), XSound.ITEM_BUCKET_FILL.parseSound(), 1.0f, 1.0f);
            String color = "§" + e.getCurrentItem().getItemMeta().getDisplayName().split("§")[0];
        }
    }
}
