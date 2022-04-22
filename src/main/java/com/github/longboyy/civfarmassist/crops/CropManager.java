package com.github.longboyy.civfarmassist.crops;

import com.github.longboyy.civfarmassist.CivFarmAssist;
import net.civex4.nobilityitems.NobilityBlock;
import net.civex4.nobilityitems.NobilityItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;
import vg.civcraft.mc.civmodcore.playersettings.PlayerSettingAPI;
import vg.civcraft.mc.civmodcore.playersettings.gui.MenuSection;
import vg.civcraft.mc.civmodcore.util.ConfigParsing;

import java.util.HashMap;

public class CropManager {

    private final CivFarmAssist plugin;

    private HashMap<ItemStack, Crop> crops;

    public CropManager(CivFarmAssist plugin){
        this.plugin = plugin;
        this.crops = new HashMap<>();
    }

    public Crop getCrop(Block block){
        if(!plugin.isNobilityEnabled()){
            ItemStack lookupItem = new ItemStack(block.getType(), 1);
            return crops.get(lookupItem);
        }

        BlockData blockData = block.getBlockData();
        NobilityBlock nobilityBlock = NobilityItems.getBlock(blockData);
        if(nobilityBlock == null || !nobilityBlock.hasItem()){
            ItemStack lookupItem = new ItemStack(block.getType(), 1);
            return crops.get(lookupItem);
        }
        ItemStack lookupItem = nobilityBlock.getItem().getItemStack(1);
        return crops.get(lookupItem);
    }

    public boolean hasCrop(Block block){
        if(!plugin.isNobilityEnabled()){
            ItemStack lookupItem = new ItemStack(block.getType(), 1);
            return crops.containsKey(lookupItem);
        }

        BlockData blockData = block.getBlockData();
        NobilityBlock nobilityBlock = NobilityItems.getBlock(blockData);
        if(nobilityBlock == null || !nobilityBlock.hasItem()){
            ItemStack lookupItem = new ItemStack(block.getType(), 1);
            return crops.containsKey(lookupItem);
        }
        ItemStack lookupItem = nobilityBlock.getItem().getItemStack(1);
        return crops.containsKey(lookupItem);
    }

    public void registerCrop(Crop crop){
        if(crop == null){
            plugin.getLogger().warning("Attempted to initialize null crop");
            return;
        }
        ItemStack cropItem = crop.getCrop();
        crops.put(cropItem, crop);
    }
}
