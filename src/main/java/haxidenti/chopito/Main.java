package haxidenti.chopito;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private static List<Material> woods;
    private static List<Material> leaves;
    private static List<Material> axeList;

    private static int RADIUS_XZ = 6;
    private static int RADIUS_Y = 12;

    {
        woods = new ArrayList<>();
        woods.add(Material.ACACIA_WOOD);
        woods.add(Material.BIRCH_WOOD);
        woods.add(Material.DARK_OAK_WOOD);
        woods.add(Material.JUNGLE_WOOD);
        woods.add(Material.OAK_WOOD);
        woods.add(Material.SPRUCE_WOOD);
        woods.add(Material.OAK_LOG);
        woods.add(Material.ACACIA_LOG);
        woods.add(Material.BIRCH_LOG);
        woods.add(Material.DARK_OAK_LOG);
        woods.add(Material.JUNGLE_LOG);
        woods.add(Material.SPRUCE_LOG);

        leaves = new ArrayList<>();
        leaves.add(Material.ACACIA_LEAVES);
        leaves.add(Material.BIRCH_LEAVES);
        leaves.add(Material.OAK_LEAVES);
        leaves.add(Material.JUNGLE_LEAVES);
        leaves.add(Material.DARK_OAK_LEAVES);
        leaves.add(Material.SPRUCE_LEAVES);

        axeList = new ArrayList<>();
        axeList.add(Material.DIAMOND_AXE);
        axeList.add(Material.GOLDEN_AXE);
        axeList.add(Material.IRON_AXE);
        axeList.add(Material.NETHERITE_AXE);
        axeList.add(Material.STONE_AXE);
        axeList.add(Material.WOODEN_AXE);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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

        if (player.isSneaking() || !axeList.contains(itemInHand.getType())) {
            return;
        }
        Block block = e.getBlock();
        if (!woods.contains(block.getType())) {
            return;
        }
        CWorld world = new CWorld(block.getWorld());

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        // y + is UP
        int destY = y + RADIUS_Y;
        int destX = x + (RADIUS_XZ / 2);
        int destZ = z + (RADIUS_Y / 2);

        for (int loopY = y; loopY < destY; loopY++) {
            // Rectange with X & Z
            for (int loopX = x - (RADIUS_XZ / 2); loopX < destX; loopX++) {
                for (int loopZ = z - (RADIUS_XZ / 2); loopZ < destZ; loopZ++) {
                    Material blockType = world.getBlock(loopX, loopY, loopZ);
                    if (woods.contains(blockType) || leaves.contains(blockType)) {
                        world.destroyBlock(loopX, loopY, loopZ);
                    }
                }
            }
        }
    }
}