package haxidenti.chopito;

import com.sun.org.apache.bcel.internal.ExceptionConst;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

class CWorld {
    private World world;

    CWorld(World world) {
        this.world = world;
    }

    public void setBlock(int x, int y, int z, Material type) {
        try {
            Block block = world.getBlockAt(x, y, z);
            block.setType(type, false);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
        }
    }

    public Material getBlock(int x, int y, int z) {
        try {
            Block block = world.getBlockAt(x, y, z);
            return block.getType();
        } catch (Exception e) {
            return Material.AIR;
        }
    }

    public Collection<ItemStack> destroyBlockAndDrop(int x, int y, int z) {
        try {
            Block block = world.getBlockAt(x, y, z);
            Collection<ItemStack> drops = block.getDrops();
            block.setType(Material.AIR);
            return drops;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            return Collections.emptyList();
        }
    }
}
