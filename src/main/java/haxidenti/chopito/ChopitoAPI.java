package haxidenti.chopito;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ChopitoAPI {

    static List<Material> woods;
    static List<Material> leaves;
    static List<Material> axeList;
    static List<Material> saplings;

    static int RADIUS_XZ = 6;
    static int RADIUS_Y = 12;

    static void init() {
        if (woods == null) {
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
        }

        if (leaves == null) {
            leaves = new ArrayList<>();
            leaves.add(Material.ACACIA_LEAVES);
            leaves.add(Material.BIRCH_LEAVES);
            leaves.add(Material.OAK_LEAVES);
            leaves.add(Material.JUNGLE_LEAVES);
            leaves.add(Material.DARK_OAK_LEAVES);
            leaves.add(Material.SPRUCE_LEAVES);
        }

        if (axeList == null) {
            axeList = new ArrayList<>();
            axeList.add(Material.DIAMOND_AXE);
            axeList.add(Material.GOLDEN_AXE);
            axeList.add(Material.IRON_AXE);
            axeList.add(Material.NETHERITE_AXE);
            axeList.add(Material.STONE_AXE);
            axeList.add(Material.WOODEN_AXE);
        }

        if (saplings == null) {
            saplings = new ArrayList<>();
            saplings.add(Material.SPRUCE_SAPLING);
            saplings.add(Material.ACACIA_SAPLING);
            saplings.add(Material.BIRCH_SAPLING);
            saplings.add(Material.OAK_SAPLING);
            saplings.add(Material.DARK_OAK_SAPLING);
            saplings.add(Material.JUNGLE_SAPLING);
        }
    }


    public static void chopAt(Location location, Plugin executorPlugin) {
        Block block = location.getBlock();
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
                                Bukkit.getScheduler().scheduleSyncDelayedTask(executorPlugin, () -> {
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

    public static boolean isSapling(Material type) {
        return saplings.contains(type);
    }

    public static boolean isGrounded(World world, int x, int y, int z) {
        try {
            Material type = world.getBlockAt(x, y - 1, z).getType();
            return type.equals(Material.DIRT) || type.equals(Material.GRASS) || type.equals(Material.COARSE_DIRT);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            return false;
        }
    }
}
