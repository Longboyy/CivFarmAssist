package com.github.longboyy.civfarmassist.crops;

import com.github.longboyy.civfarmassist.CFAConfigManager;
import com.github.longboyy.civfarmassist.CivFarmAssist;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;
import vg.civcraft.mc.civmodcore.util.ConfigParsing;

import java.util.ArrayList;
import java.util.List;

public class Crop {

    private ItemStack crop;
    private ItemStack plantedOn;
    private CropDrop seed;
    private CropDrop harvest;

    // VALID DIRECTIONS ARE RELATIVE TO THE CROP - FOR EXAMPLE `DOWN` MEANS SEARCH BELOW CROP FOR `plantedOn`
    private List<BlockFace> validDirections;

    public Crop(ItemStack crop, ItemStack plantedOn, CropDrop seed, CropDrop harvest, List<BlockFace> validDirections){
        this.crop = crop;
        this.plantedOn = plantedOn;
        this.seed = seed;
        this.harvest = harvest;
        this.validDirections = validDirections;
    }

    public ItemStack getCrop() {
        return crop;
    }

    public ItemStack getPlantedOn() {
        return plantedOn;
    }

    public CropDrop getSeed() {
        return seed;
    }

    public CropDrop getHarvest() {
        return harvest;
    }

    public List<BlockFace> getValidDirections() {
        return validDirections;
    }

    public static Crop parseCrop(ConfigurationSection config){
        if(config == null){
            return null;
        }

        /*
        ItemMap cropMap = ConfigParsing.parseItemMap(config.getConfigurationSection("crop"));
        if(cropMap == null || !(cropMap.getTotalUniqueItemAmount() > 0)){
            return null;
        }
        ItemStack cropItem = cropMap.getItemStackRepresentation().get(0);
        cropItem.setAmount(1);

        ItemMap plantedOnMap = ConfigParsing.parseItemMap(config.getConfigurationSection("plantedOn"));
        if(plantedOnMap == null || !(plantedOnMap.getTotalUniqueItemAmount() > 0)){
            return null;
        }
        ItemStack plantedOnItem = plantedOnMap.getItemStackRepresentation().get(0);
        plantedOnItem.setAmount(1);
         */

        ItemStack cropItem = CFAConfigManager.parseFirstItem(config.getConfigurationSection("crop"));
        ItemStack plantedOnItem = CFAConfigManager.parseFirstItem(config.getConfigurationSection("plantedOn"));

        CropDrop seedDrop = CropDrop.parseCropDrop(config.getConfigurationSection("seed"));
        if(seedDrop == null){
            return null;
        }
        CivFarmAssist.getInstance(CivFarmAssist.class).getLogger().info("Seed drop parsing");

        CropDrop harvestDrop = CropDrop.parseCropDrop(config.getConfigurationSection("harvest"));
        if(harvestDrop == null){
            return null;
        }
        CivFarmAssist.getInstance(CivFarmAssist.class).getLogger().info("Harvest drop parsing");

        List<String> directions = config.getStringList("validDirections");

        List<BlockFace> blockDirections = new ArrayList<>();
        if(directions.size() > 0){
            for(String direction : directions){
                BlockFace face = BlockFace.valueOf(direction);
                blockDirections.add(face);
            }
        }else{
            blockDirections.add(BlockFace.DOWN);
        }
        CivFarmAssist.getInstance(CivFarmAssist.class).getLogger().info("directions parsing");

        return new Crop(cropItem, plantedOnItem, seedDrop, harvestDrop, blockDirections);
    }

}
