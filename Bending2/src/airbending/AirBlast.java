package airbending;

import java.util.concurrent.ConcurrentHashMap;

import main.Bending;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.AvatarState;
import tools.Tools;

public class AirBlast {

	public static ConcurrentHashMap<Integer, AirBlast> instances = new ConcurrentHashMap<Integer, AirBlast>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	private static int ID = Integer.MIN_VALUE;
	static final int maxticks = 10000;

	public static double speed = 25;
	public static double range = 20;
	public static double affectingradius = 2;
	public static double pushfactor = 1;
	// public static long interval = 2000;
	public static byte full = 0x0;

	private Location location;
	private Location origin;
	private Vector direction;
	private Player player;
	private int id;
	private double speedfactor;
	private int ticks = 0;

	// private long time;

	public AirBlast(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
				return;
			}
		}
		timers.put(player, System.currentTimeMillis());
		this.player = player;
		location = player.getEyeLocation();
		origin = player.getEyeLocation();
		direction = player.getEyeLocation().getDirection().normalize();
		location = location.add(direction.clone());
		id = ID;
		instances.put(id, this);
		if (ID == Integer.MAX_VALUE)
			ID = Integer.MIN_VALUE;
		ID++;
		// time = System.currentTimeMillis();
	}

	public boolean progress() {
		speedfactor = speed * (Bending.time_step / 1000.);

		ticks++;

		if (ticks > maxticks) {
			instances.remove(id);
			return false;
		}

		// if (player.isSneaking()
		// && Tools.getBendingAbility(player) == Abilities.AirBlast) {
		// new AirBlast(player);
		// }

		Block block = location.getBlock();
		for (Block testblock : Tools.getBlocksAroundPoint(location,
				affectingradius)) {
			if (testblock.getType() == Material.FIRE) {
				testblock.setType(Material.AIR);
			}
		}
		if (block.getType() != Material.AIR) {
			if (block.getType() == Material.STATIONARY_LAVA) {
				block.setType(Material.OBSIDIAN);
				instances.remove(id);
			} else if (block.getType() == Material.LAVA) {
				if (block.getData() == full) {
					block.setType(Material.OBSIDIAN);
				} else {
					block.setType(Material.COBBLESTONE);
				}
				instances.remove(id);
			}
			return false;
		}

		if (location.distance(origin) > range) {
			instances.remove(id);
			return false;
		}

		for (Entity entity : Tools.getEntitiesAroundPoint(location,
				affectingradius)) {
			affect(entity);
		}

		advanceLocation();

		return true;
	}

	private void advanceLocation() {
		location.getWorld().playEffect(location, Effect.SMOKE, 1);
		location = location.add(direction.clone().multiply(speedfactor));
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	private void affect(Entity entity) {
		if (entity.getEntityId() != player.getEntityId()) {
			if (AvatarState.isAvatarState(player)) {
				entity.setVelocity(direction.clone().multiply(
						AvatarState.getValue(pushfactor)));
			} else {
				entity.setVelocity(direction.clone().multiply(pushfactor));
			}
			entity.setFallDistance(0);
		}
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.remove(id);
		}
	}
}