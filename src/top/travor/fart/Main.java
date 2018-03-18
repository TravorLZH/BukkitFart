package top.travor.fart;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

/**
 * Created by liut21 on 3/16/2018.
 */
public class Main extends JavaPlugin implements Listener,CommandExecutor{
    @Override
    public void onEnable(){
        getLogger().info("Fart enabled!");
        getCommand("fart").setExecutor(this);
        getServer().getPluginManager().registerEvents(this,this);
    }
    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
        if(cmd.getLabel().equalsIgnoreCase("fart")){
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED+"You can only execute this command as player");
                return true;
            }
            Player pl=(Player)sender;
            pl.setMetadata("fart",new FixedMetadataValue(this,getMeta(pl,"fart")!=null? !getMeta(pl,"fart").asBoolean() : true));
            return true;
        }
        return false;
    }
    MetadataValue getMeta(Metadatable m,String meta){
        List<MetadataValue> list=m.getMetadata(meta);
        if(list==null){
            return null;
        }
        for(MetadataValue val:list){
            if(val.getOwningPlugin()==this){
                return val;
            }
        }
        return null;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent ev){
        if(getMeta(ev.getEntity(),"fart")!=null && getMeta(ev.getEntity(),"fart").asBoolean()){
            ev.setDeathMessage(ev.getEntity().getName()+" farts to death");
        }
        ev.getEntity().setMetadata("fart",new FixedMetadataValue(this,false));
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev){
        Player pl=ev.getPlayer();
        Location loc=pl.getLocation();
        if(pl.isSneaking()){
            pl.setMetadata("fart",new FixedMetadataValue(this,false));
        }
        if(getMeta(pl,"fart")!=null && getMeta(pl,"fart").asBoolean()){
            loc.getBlock().setType(Material.FIRE);
            pl.getWorld().dropItem(loc,new ItemStack(Material.INK_SACK,1,(short)3));
            pl.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,1),true);
            Vector vector = pl.getVelocity();
            double rotX = (double)loc.getYaw();
            vector.setX(-Math.sin(Math.toRadians(rotX))*2);
            vector.setZ(Math.cos(Math.toRadians(rotX))*2);
            pl.setVelocity(vector);
            pl.getWorld().playSound(pl.getLocation(),Sound.EXPLODE,10f,new Random().nextFloat());
        }
    }
}
