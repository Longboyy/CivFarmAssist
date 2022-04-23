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
import org.bukkit.craftbukkit.v1_16_R3.block.impl.CraftCocoa;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.CocoaPlant;
import org.bukkit.scheduler.BukkitRunnable;

public class ReplantTask extends BukkitRunnable {

    private final CivFarmAssist plugin;
    private final Block block;
    private final Crop crop;
    private final Player player;
    private Cocoa cocoa;

    public ReplantTask(Block block, CivFarmAssist plugin, Crop crop, Player player, Cocoa cocoa){
        this.plugin = plugin;
        this.block = block;
        this.crop = crop;
        this.player = player;
        this.cocoa = cocoa;

        if(this.cocoa != null){
            this.cocoa.setAge(0);
            this.cocoa.setFacing(cocoa.getFacing());
        }
    }

    @Override
    public void run() {
        //plugin.getLogger().info("REPLANT TASK: " + block.getType().toString());


        /*
        if(block.getType() == Material.COCOA){
            //handleCocoa();
        }else{
            handleReplant();
        }
         */

        handleReplant();

    }

    /*
    private void handleCocoa(){
        Material plantedOn = this.crop.getPlantedOn().getType();
        for(BlockFace face : crop.getValidDirections()){
            if(this.block.getRelative(face).getType() == plantedOn){
                //Cocoa cocoaData = (Cocoa) data;
                //CocoaPlant plant = (CocoaPlant) this.block.getState();

                this.block.setType(Material.COCOA);
                //this.block.setBlockData(cocoa);

                //new UpdateCocoaTask(this.block, face).runTaskLater(plugin, 5L);
                //cocoaData.getFacing()
                return;
            }
        }
    }

     */

    private void handleReplant(){
        Material plantedOn = this.crop.getPlantedOn().getType();
        for(BlockFace face : crop.getValidDirections()){
            Block faceBlock = this.block.getRelative(face);
            if(faceBlock.getType() == plantedOn){
                if(this.cocoa != null){
                    this.block.setType(Material.COCOA);
                    this.cocoa.setAge(0);
                    this.block.setBlockData(this.cocoa, true);
                }else {
                    this.block.setType(crop.getCrop().getType());
                    this.block.setBlockData(this.setCropAge());
                }

                BlockPlaceEvent bpe = new BlockPlaceEvent(this.block, this.block.getState(), faceBlock, this.player.getActiveItem(), this.player, true, EquipmentSlot.HAND);
                plugin.getServer().getPluginManager().callEvent(bpe);
                return;
            }
        }
    }

    private BlockData setCropAge(){
        Ageable age = (Ageable) this.block.getBlockData().clone();
        age.setAge(0);
        return age;
    }
}
