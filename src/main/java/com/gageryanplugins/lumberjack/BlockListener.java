package com.gageryanplugins.lumberjack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockListener implements Listener {

    private Lumberjack plugin;
    private List<Material> allowedTools;

    public BlockListener(Lumberjack plugin) {
        this.plugin = plugin;
        allowedTools = new ArrayList<>();

        // Load allowed tools from configuration file
        this.plugin.getConfig().getStringList("tools").forEach(tool -> {

            if(!isMaterialValid(tool)) {
                Bukkit.getConsoleSender().sendMessage("Error while loading tools list; Following tool skipped: " + tool);
                Bukkit.getConsoleSender().sendMessage("Please verify the material name and correct it.");
                return;
            }

            allowedTools.add(Material.valueOf(tool));
        });
    }

    @EventHandler
    public void onLogBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Check if player has permission
        if (!player.hasPermission("lumberjack.use")) return; //TODO: Add no permissions message Ryan: Why? That would get annoying very quickly

        // Check if tool is allowed
        if (!allowedTools.contains(player.getInventory().getItemInMainHand().getType())) return;

        // Check if Material is log
        if (!isLog(event.getBlock().getType())) return;

        // Break block
        breakChain(event.getBlock());
    }

    public void breakChain(Block block) {
        String ty, t = plugin.getConfig().getString("type");

        //TODO: Improve config value check
        if (t.equals("classic") || t.equals("classic-leaves") || t.equals("full") || t.equals("full-noleaves")) {
            ty = t;
        } else {
            return;
        }

        // Break initial block
        block.breakNaturally();

        cut(block, BlockFace.UP);
        cut(block, BlockFace.DOWN);

        if (ty.equals("classic-leaves") || ty.equals("full")) {
            cut(block, BlockFace.UP);
            cut(block, BlockFace.DOWN);
            cut(block, block.getX() + 1, block.getY(), block.getZ());
            cut(block, block.getX() - 1, block.getY(), block.getZ());
            cut(block, block.getX(), block.getY(), block.getZ() - 1);
            cut(block, block.getX(), block.getY(), block.getZ() + 1);
        }

        if (ty.equals("full") || ty.equals("full-noleaves")) {
            Block block1 = block.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
            Block block2 = block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);

            cut(block, block.getFace(block1));
            cut(block, block.getFace(block2));

            cut(block, block.getX() + 2, block.getY(), block.getZ());
            cut(block, block.getX() - 2, block.getY(), block.getZ());
            cut(block, block.getX(), block.getY(), block.getZ() - 2);
            cut(block, block.getX(), block.getY(), block.getZ() + 2);
        }
    }

    public void cut(Block block, BlockFace blockFace) {
        cut(block, blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
    }

    public void cut(Block block, int x, int y, int z) {
        block = block.getRelative(x, y, z);
        if (!areLeaves(block.getType())) return;
        if (!isLog(block.getType())) return;
        breakChain(block);
    }

    // Returns boolean if material is of type Log or not.
    public boolean isLog(Material material) {
        switch (material) {
            case OAK_LOG:
            case STRIPPED_OAK_LOG:
            case SPRUCE_LOG:
            case STRIPPED_SPRUCE_LOG:
            case BIRCH_LOG:
            case JUNGLE_LOG:
            case STRIPPED_JUNGLE_LOG:
            case ACACIA_LOG:
            case STRIPPED_ACACIA_LOG:
            case DARK_OAK_LOG:
            case STRIPPED_DARK_OAK_LOG:
                return true;
        }

        return false;
    }

    // Returns boolean if material is of type Leaves or not.
    public boolean areLeaves(Material material) {
        switch (material) {
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                return true;
        }

        return false;
    }

    private boolean isMaterialValid(String name) {
        for (Material value : Material.values()) {
            if(value.name().equals(name)) return true;
        }

        return false;
    }
}