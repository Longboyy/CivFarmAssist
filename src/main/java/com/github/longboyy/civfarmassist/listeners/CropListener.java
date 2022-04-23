package com.github.longboyy.civfarmassist.listeners;

import com.github.longboyy.civfarmassist.CivFarmAssist;
import com.github.longboyy.civfarmassist.crops.Crop;
import com.github.longboyy.civfarmassist.crops.CropDrop;
import com.github.longboyy.civfarmassist.crops.CropManager;
import com.github.longboyy.civfarmassist.tasks.ReplantTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Cocoa;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.citadel.ReinforcementManager;
import vg.civcraft.mc.citadel.model.Reinforcement;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;
import vg.civcraft.mc.namelayer.permission.PermissionType;

import java.util.List;

public class CropListener implements Listener {
    private final CivFarmAssist plugin;
    private final CropManager cropManager;

    public CropListener(CivFarmAssist plugin, CropManager cropManager){
        this.plugin = plugin;
        this.cropManager = cropManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCropBreak(BlockBreakEvent event){
        Block block = event.getBlock();

        Crop crop = cropManager.getCrop(block);
        if(crop == null){
            return;
        }

        ReinforcementManager reinManager = Citadel.getInstance().getReinforcementManager();
        Reinforcement reinforcement = reinManager.getReinforcement(block);
        if(reinforcement != null && !reinforcement.hasPermission(event.getPlayer(), PermissionType.getPermission("BREAK_CROPS"))){
            event.setCancelled(true);
            return;
        }

        BlockData blockData = event.getBlock().getBlockData();
        if(blockData instanceof Directional){
            Directional directional = (Directional) blockData;
            event.getPlayer().sendMessage(Component.text(directional.getFacing().toString()));
            Reinforcement rein = reinManager.getReinforcement(block.getRelative(directional.getFacing()));
            if(rein != null && !rein.hasPermission(event.getPlayer(), PermissionType.getPermission("BREAK_CROPS"))){
                event.setCancelled(true);
                return;
            }
        }else{
            for(BlockFace face : crop.getValidDirections()){
                Reinforcement rein = reinManager.getReinforcement(block.getRelative(face));
                if(rein != null && !rein.hasPermission(event.getPlayer(), PermissionType.getPermission("BREAK_CROPS"))){
                    event.setCancelled(true);
                    return;
                }
            }
        }

        event.setDropItems(false);

        if(!plugin.getSettingsManager().getAutoReplant(event.getPlayer().getUniqueId()) || !crop.isAutoReplant()){
            dropCrop(crop, block);
            return;
        }

        applyReplant(event, crop);
    }

    private void applyReplant(BlockBreakEvent event, Crop crop){
        Block block = event.getBlock();
        ItemStack cropSeedItem = crop.getSeed().getItem();
        PlayerInventory playerInv = event.getPlayer().getInventory();
        ItemMap seeds = new ItemMap(cropSeedItem);

        if(seeds.isContainedIn(playerInv)){
            seeds.removeSafelyFrom(playerInv);

            BlockData blockData = event.getBlock().getBlockData();
            if(block.getType() == Material.COCOA){
                new ReplantTask(block, plugin, crop, event.getPlayer(), (Cocoa)blockData).runTaskLater(plugin, 5L);
            }else{
                new ReplantTask(block, plugin, crop, event.getPlayer(), null).runTaskLater(plugin, 5L);
            }

        }else if(crop.isHarvestAlsoSeed()){
            ItemMap harvestSeeds = new ItemMap(crop.getHarvest().getItem());
            if(harvestSeeds.isContainedIn(playerInv)) {
                harvestSeeds.removeSafelyFrom(playerInv);

                //event.getPlayer().sendMessage(Component.text(block.getType().toString()));

                BlockData blockData = event.getBlock().getBlockData();
                if(block.getType() == Material.COCOA){
                    //event.setCancelled(true);
                    new ReplantTask(block, plugin, crop, event.getPlayer(), (Cocoa)blockData).runTaskLater(plugin, 5L);
                }else{
                    new ReplantTask(block, plugin, crop, event.getPlayer(), null).runTaskLater(plugin, 5L);
                }
            }
        }

        dropCrop(crop, block);
    }

    /*
    private void fuckIHateCocoa(Block block){
        new BukkitRunnable(){
            @Override
            public void run() {
                Cocoa cocoa = (Cocoa) block.getBlockData();
                cocoa.setAge(0);
                block.setBlockData(cocoa, true);
            }
        }.runTaskLater(plugin, 5L);
    }
     */

    private void dropCrop(Crop crop, Block block){
        World world = block.getWorld();
        CropDrop seedDrop = crop.getSeed();
        CropDrop harvestDrop = crop.getHarvest();
        int seedAmount = getRandomNumber(seedDrop.getMinDrop(), seedDrop.getMaxDrop());
        int harvestAmount = getRandomNumber(harvestDrop.getMinDrop(), harvestDrop.getMaxDrop());

        if(isRipe(block)) {
            ItemMap harvestMap = new ItemMap(harvestDrop.getItem());
            harvestMap.multiplyContent(harvestAmount);
            if (harvestAmount > 0) {
                dropItems(harvestMap, world, block.getLocation());
            }
            if(crop.isDropSeedsWhenRipe()){
                ItemMap seedMap = new ItemMap(seedDrop.getItem());
                seedMap.multiplyContent(seedAmount);
                if(seedAmount > 0) {
                    dropItems(seedMap, world, block.getLocation());
                }
            }
        }else{
            ItemMap seedMap = new ItemMap(seedDrop.getItem());
            seedMap.multiplyContent(seedAmount);
            if(seedAmount > 0) {
                dropItems(seedMap, world, block.getLocation());
            }
        }
    }

    private void dropItems(ItemMap map, World world, Location location){
        List<ItemStack> items = map.getItemStackRepresentation();
        for(ItemStack item : items){
            world.dropItemNaturally(location, item);
        }
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private boolean isRipe(Block block){
        BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Ageable)){
            return false;
        }
        Ageable age = (Ageable) blockData;
        return (age.getAge() == age.getMaximumAge());
    }
}
