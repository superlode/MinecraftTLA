package chiblocker;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HighJump {
	
	private int jumpheight =  1;
	private long cooldown = 10000;
	
	private Map<String, Long> cooldowns = new HashMap<String, Long>();

	public HighJump(Player p){
		jump(p);
	}

	private void jump(Player p) {
		if (cooldowns.containsKey(p.getName())){
			if (cooldowns.get(p.getName()) + cooldown >= System.currentTimeMillis()){
				if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
					return;
				Vector vec = p.getVelocity();
				vec.setY(jumpheight);
				p.setVelocity(vec);
				cooldowns.put(p.getName(), System.currentTimeMillis());
				return;
			}
		} else {
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
				return;
		Vector vec = p.getVelocity();
		vec.setY(jumpheight);
		p.setVelocity(vec);
		cooldowns.put(p.getName(), System.currentTimeMillis());
		return;
		}
	}
}