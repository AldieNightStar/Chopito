package haxidenti.chopito;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin implements Listener {

    private static List<Material> woods;
    private static List<Material> leaves;
    private static List<Material> axeList;
    private static List<Material> saplings;

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

        saplings = new ArrayList<>();
        saplings.add(Material.SPRUCE_SAPLING);
        saplings.add(Material.ACACIA_SAPLING);
        saplings.add(Material.BIRCH_SAPLING);
        saplings.add(Material.OAK_SAPLING);
        saplings.add(Material.DARK_OAK_SAPLING);
        saplings.add(Material.JUNGLE_SAPLING);
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
        World blockWorld = block.getWorld();
        CWorld world = new CWorld(blockWorld);

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        AtomicBoolean grounded = new AtomicBoolean(isGrounded(blockWorld, x, y, z));

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
                        Collection<ItemStack> drops = world.destroyBlockAndDrop(loopX, loopY, loopZ);
                        // If player chops tree exact on ground and sapling is falling, then plant again :)
                        drops.forEach(d -> {
                            if (grounded.get() && isSapling(d.getType())) {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                                    world.setBlock(x, y, z, d.getType());
                                });
                                grounded.set(false);
                            } else {
                                blockWorld.dropItem(new Location(blockWorld, x, y, z), d);
                            }
                        });
                    }
                }
            }
        }
    }

    private static boolean isSapling(Material type) {
        return saplings.contains(type);
    }

    private static boolean isGrounded(World world, int x, int y, int z) {
        Material type = world.getBlockAt(x, y-1, z).getType();
        return type.equals(Material.DIRT) || type.equals(Material.GRASS) || type.equals(Material.COARSE_DIRT);
    }
}