package com.github.longboyy.civfarmassist.listeners;

import com.github.longboyy.civfarmassist.CivFarmAssist;
import com.github.longboyy.civfarmassist.crops.Crop;
import com.github.longboyy.civfarmassist.crops.CropManager;
import com.github.longboyy.civfarmassist.tasks.ReplantTask;
import net.civex4.nobilityitems.NobilityBlock;
import net.civex4.nobilityitems.NobilityItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.citadel.ReinforcementManager;
import vg.civcraft.mc.citadel.model.Reinforcement;
import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;
import vg.civcraft.mc.namelayer.permission.PermissionType;

import java.util.Iterator;

public class CropListener implements Listener {
    private final CivFarmAssist plugin;
    private final CropManager cropManager;

    public CropListener(CivFarmAssist plugin, CropManager cropManager){
        this.plugin = plugin;
        this.cropManager = cropManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCropBreak(BlockBreakEvent event){
        if(!plugin.getSettingsManager().getAutoReplant(event.getPlayer().getUniqueId())){
            return;
        }
        plugin.getLogger().info("Setting passed");

        Block block = event.getBlock();
        /*
        if(!cropManager.hasCrop(block)){
            plugin.getLogger().info("was not crop");
            return;
        }
        plugin.getLogger().info("passed crop check");
         */


        Crop crop = cropManager.getCrop(block);
        if(crop == null){
            plugin.getLogger().info("crop didn't exist");
            return;
        }
        plugin.getLogger().info("crop check passed");

        ReinforcementManager reinManager = Citadel.getInstance().getReinforcementManager();
        Reinforcement reinforcement = reinManager.getReinforcement(block);
        if(reinforcement != null && !reinforcement.hasPermission(event.getPlayer(), PermissionType.getPermission("BREAK_CROPS"))){
            return;
        }
        plugin.getLogger().info("reinforcement check passed");

        applyReplant(event, crop);
    }

    private void applyReplant(BlockBreakEvent event, Crop crop){
        event.setCancelled(true);
        ItemStack cropSeedItem = crop.getSeed().getItem();

        PlayerInventory playerInv = event.getPlayer().getInventory();

        ItemMap seeds = new ItemMap(cropSeedItem);
        if(!seeds.isContainedIn(playerInv)){
            return;
        }
        plugin.getLogger().info("seed check passed");

        if(isRipe(event.getBlock())){
            seeds.removeSafelyFrom(playerInv);
            new ReplantTask(event.getBlock(), plugin, crop).runTaskLater(plugin, 5L);
            plugin.getLogger().info("replanting");
        }
    }

    private ItemMap getItemsByItemMeta(ItemMap map, Material material, ItemMeta meta){
        ItemMap result = new ItemMap();
        ItemMap filteredMap = map.getStacksByMaterial(material);
        for(ItemStack item : filteredMap.getItemStackRepresentation()){
            ItemMeta itemMeta = item.getItemMeta();
            if(itemMeta.equals(meta)){
                result.addItemStack(item);
            }
        }
        return result;
    }

    /*
    private ItemMap getStacksByMaterial(ItemMap map, Material m) {
        ItemMap result = new ItemMap();
        Iterator var3 = this.items.keySet().iterator();

        while(var3.hasNext()) {
            ItemStack is = (ItemStack)var3.next();
            if (is.getType() == m) {
                result.addItemAmount(is.clone(), (Integer)this.items.get(is));
            }
        }

        return result;
    }
     */

    private boolean isRipe(Block block){
        BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Ageable)){
            return false;
        }
        Ageable age = (Ageable) blockData;
        return (age.getAge() == age.getMaximumAge());
    }
}
