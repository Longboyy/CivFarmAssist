package com.github.longboyy.civfarmassist.tasks;

import com.github.longboyy.civfarmassist.CivFarmAssist;
import com.github.longboyy.civfarmassist.crops.Crop;
import com.github.longboyy.civfarmassist.crops.CropManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cocoa;
import org.bukkit.scheduler.BukkitRunnable;

public class ReplantTask extends BukkitRunnable {

    private final CivFarmAssist plugin;
    private final Block block;
    private final Crop crop;

    public ReplantTask(Block block, CivFarmAssist plugin, Crop crop){
        this.plugin = plugin;
        this.block = block;
        this.crop = crop;
    }

    @Override
    public void run() {
        plugin.getLogger().info("REPLANT TASK: " + block.getType().toString());

        if(block.getType() == Material.COCOA){
            handleCocoa();
        }else{
            handleReplant();
        }

    }

    private void handleCocoa(){
        Material plantedOn = this.crop.getPlantedOn().getType();
        for(BlockFace face : crop.getValidDirections()){
            if(this.block.getRelative(face).getType() == plantedOn){
                Cocoa cocoaData = (Cocoa) this.block.getBlockData().clone();
                cocoaData.setAge(0);
                this.block.setType(Material.COCOA);
                this.block.setBlockData(cocoaData);
                return;
            }
        }
    }

    private void handleReplant(){
        Material plantedOn = this.crop.getPlantedOn().getType();
        for(BlockFace face : crop.getValidDirections()){
            if(this.block.getRelative(face).getType() == plantedOn){
                this.block.setType(crop.getCrop().getType());
                this.setCropAge();
            }
        }
    }

    private BlockData setCropAge(){
        Ageable age = (Ageable) this.block.getBlockData().clone();
        age.setAge(0);
        return age;
    }
}
