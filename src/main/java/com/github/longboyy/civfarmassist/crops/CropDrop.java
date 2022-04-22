package com.github.longboyy.civfarmassist.crops;

import com.github.longboyy.civfarmassist.CFAConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;
import vg.civcraft.mc.civmodcore.util.ConfigParsing;

public class CropDrop {

    private int minDrop;
    private int maxDrop;
    private ItemStack item;
    private boolean canFortune;

    public CropDrop(int minDrop, int maxDrop, ItemStack item, boolean canFortune){
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
        this.item = item;
        this.canFortune = canFortune;
    }

    public int getMinDrop(){
        return minDrop;
    }

    public int getMaxDrop(){
        return maxDrop;
    }

    public ItemStack getItem(){
        return item;
    }

    public boolean canFortune(){
        return canFortune;
    }

    public static CropDrop parseCropDrop(ConfigurationSection config){
        if(config == null){
            return null;
        }

        /*
        ItemMap item = ConfigParsing.parseItemMap(config.getConfigurationSection("item"));
        if(item == null || !(item.getTotalUniqueItemAmount() > 0)){
            return null;
        }
         */
        //ItemStack itemStack = item.getItemStackRepresentation().get(0);
        ItemStack itemStack = CFAConfigManager.parseFirstItem(config.getConfigurationSection("item"));


        int minDrop = config.getInt("minDrop", 1);
        int maxDrop = config.getInt("maxDrop", 1);
        boolean canFortune = config.getBoolean("canFortune", false);

        return new CropDrop(minDrop, maxDrop, itemStack, canFortune);
    }
}
