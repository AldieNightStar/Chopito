package haxidenti.chopito;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        ChopitoAPI.init();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        return false;
    }

    @EventHandler
    public void chopEvent(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (player.isSneaking() || !ChopitoAPI.axeList.contains(itemInHand.getType())) {
            return;
        }

        Location location = e.getBlock().getLocation();
        ChopitoAPI.chopAt(location, location, this);
    }


}