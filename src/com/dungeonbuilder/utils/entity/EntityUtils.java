package com.dungeonbuilder.utils.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Illager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.dungeonbuilder.utils.item.EnumDisplayName;
import com.dungeonbuilder.utils.logger.Logs;
import com.dungeonbuilder.utils.nbt.CustomNBT;
import com.dungeonbuilder.utils.nbt.StringArrayPersistentDataType;
import com.dungeonbuilder.utils.serialization.info.CustomMobInfo;
import com.dungeonbuilder.utils.serialization.info.CustomMobInfo.SettableField;
import com.dungeonbuilder.utils.serialization.info.Info;
import com.dungeonbuilder.utils.serialization.info.Info.InfoType;
import com.dungeonbuilder.utils.serialization.info.ItemInfo;
import com.dungeonbuilder.utils.serialization.info.SimpleInfo;
import com.dungeonbuilder.utils.serialization.info.UnsetInfo;
import com.dungeonbuilder.utils.threads.Threads;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityCod;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.animal.EntityDolphin;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityMushroomCow;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntityPanda;
import net.minecraft.world.entity.animal.EntityParrot;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.EntityPolarBear;
import net.minecraft.world.entity.animal.EntityPufferFish;
import net.minecraft.world.entity.animal.EntityRabbit;
import net.minecraft.world.entity.animal.EntitySalmon;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.animal.EntitySnowman;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.EntityTropicalFish;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.EntityHorse;
import net.minecraft.world.entity.animal.horse.EntityHorseDonkey;
import net.minecraft.world.entity.animal.horse.EntityHorseMule;
import net.minecraft.world.entity.animal.horse.EntityHorseSkeleton;
import net.minecraft.world.entity.animal.horse.EntityHorseZombie;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.entity.animal.horse.EntityLlamaTrader;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.EntityBlaze;
import net.minecraft.world.entity.monster.EntityCaveSpider;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityDrowned;
import net.minecraft.world.entity.monster.EntityEnderman;
import net.minecraft.world.entity.monster.EntityEndermite;
import net.minecraft.world.entity.monster.EntityEvoker;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityGiantZombie;
import net.minecraft.world.entity.monster.EntityGuardian;
import net.minecraft.world.entity.monster.EntityGuardianElder;
import net.minecraft.world.entity.monster.EntityIllagerIllusioner;
import net.minecraft.world.entity.monster.EntityMagmaCube;
import net.minecraft.world.entity.monster.EntityPhantom;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.monster.EntityPillager;
import net.minecraft.world.entity.monster.EntityRavager;
import net.minecraft.world.entity.monster.EntityShulker;
import net.minecraft.world.entity.monster.EntitySilverfish;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntitySkeletonStray;
import net.minecraft.world.entity.monster.EntitySkeletonWither;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.entity.monster.EntityStrider;
import net.minecraft.world.entity.monster.EntityVex;
import net.minecraft.world.entity.monster.EntityVindicator;
import net.minecraft.world.entity.monster.EntityWitch;
import net.minecraft.world.entity.monster.EntityZoglin;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.monster.EntityZombieHusk;
import net.minecraft.world.entity.monster.EntityZombieVillager;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.EntityVillagerTrader;

public class EntityUtils {

	public static final int SLIME_DEFAULT_SIZE = 4, SLIME_MAX_SIZE = 30;

	private static final String MUSHROOM_COW_DISPLAY_NAME = "Mooshroom";

	public static String getDisplayEntityNameFromType(EntityType entityType) {
		if (entityType.equals(EntityType.MUSHROOM_COW)) {
			return MUSHROOM_COW_DISPLAY_NAME;
		}
		return EnumDisplayName.of(entityType.toString());
	}

	public static EntityType getEntityTypeFromDisplayName(String displayName) throws IllegalArgumentException {
		if (displayName.equalsIgnoreCase(MUSHROOM_COW_DISPLAY_NAME)) {
			return EntityType.MUSHROOM_COW;
		}
		// Special case for Snowman since the spawn egg is named "Snow Golem"
		if (displayName.equalsIgnoreCase("SNOW_GOLEM")) {
			return EntityType.SNOWMAN;
		}
		return EntityType.valueOf(displayName);
	}

	public static Material toSpawnEgg(EntityType entityType) {
		try {
			return Material
					.valueOf(EntityUtils.getDisplayEntityNameFromType(entityType).toUpperCase().replaceAll(" ", "_")
							+ "_SPAWN_EGG");
		} catch (IllegalArgumentException e) {
			if (entityType.equals(EntityType.SNOWMAN)) {
				return Material.SNOWBALL;
			}
			return Material.BARRIER;
		}
	}

	public static EntityLiving bukkitToNMSType(LivingEntity entity) {
		return ((CraftLivingEntity) entity).getHandle();
	}

	public static LivingEntity nmsToBukkitType(EntityLiving entity) {
		return (LivingEntity) entity.getBukkitEntity();
	}

	public static Optional<UUID> getCustomMobId(LivingEntity entity) {
		String customMobIdStr = entity.getPersistentDataContainer().get(CustomNBT.CUSTOM_MOB_UUID_KEY,
				PersistentDataType.STRING);
		if (customMobIdStr == null) {
			return Optional.empty();
		}
		return Optional.of(UUID.fromString(customMobIdStr));
	}

	public static Optional<UUID> getPlaythroughContextId(LivingEntity entity) {
		String playthroughContextIdStr = entity.getPersistentDataContainer()
				.get(CustomNBT.CUSTOM_MOB_PLAYTHROUGH_ID_KEY, PersistentDataType.STRING);
		if (playthroughContextIdStr == null) {
			return Optional.empty();
		}
		return Optional.of(UUID.fromString(playthroughContextIdStr));
	}

	public static boolean hasFireThorns(LivingEntity entity) {
		return entity.getPersistentDataContainer().getOrDefault(CustomNBT.CUSTOM_MOB_PLAYTHROUGH_FIRETHORNS,
				PersistentDataType.BYTE, (byte) 0) == 1;
	}

	/**
	 * The time in ticks it tacks for the entity created to be teleported out of the
	 * mob factory.
	 */
	public static final long MOB_FACTORY_TELEPORT_TIMER = 5L;

	/**
	 * The location where mobs will initially be spawned prior to being teleported
	 * to their spawn location. This is needed because clearing entity equipment too
	 * early will cause the equipment to still display until the entity is unloaded
	 * and reloaded for the player (i.e. walking out of range).
	 */
	public static final Location MOB_FACTORY_LOCATION = new Location(null, 0, 0, 0);

	/**
	 * Spawns a "regular" sized version of every entity. Does not clear armor and
	 * hand contents.
	 */
	public static LivingEntity spawnCleanBukkitEntity(Location location, EntityType entityType) {
		Location factoryLocation = MOB_FACTORY_LOCATION.clone();
		factoryLocation.setWorld(location.getWorld());
		LivingEntity bukkitEntity = (LivingEntity) location.getWorld().spawnEntity(factoryLocation, entityType,
				SpawnReason.CUSTOM);
		// By default, prevent entity from picking up items
		bukkitEntity.setCanPickupItems(false);
		// Prevent babies from spawning by random chance
		if (bukkitEntity instanceof Zombie) {
			Zombie zombie = (Zombie) bukkitEntity;
			zombie.setAdult();
		} else if (bukkitEntity instanceof Ageable) {
			Ageable ageable = (Ageable) bukkitEntity;
			ageable.setAdult();
		} else if (bukkitEntity instanceof Slime) {
			// Magma cubes included
			Slime slime = (Slime) bukkitEntity;
			slime.setSize(SLIME_DEFAULT_SIZE);
		}
		// Prevent hostile mobs from being removed
		bukkitEntity.setRemoveWhenFarAway(false);
		bukkitEntity.getEquipment().clear();
		Threads.runMainLater(() -> {
			if (bukkitEntity == null || bukkitEntity.isDead()) {
				return;
			}
			// Add 0.5 to spawn at center of block instead of top left coordinate.
			EntityUtils.teleportSafely(bukkitEntity, location.clone().add(0.5, 0, 0.5));
		}, EntityUtils.MOB_FACTORY_TELEPORT_TIMER);
		return bukkitEntity;
	}

	public static LivingEntity spawnPedestalBukkitEntity(Location location, EntityType entityType) {
		LivingEntity bukkitEntity = EntityUtils.spawnCleanBukkitEntity(location, entityType);
		bukkitEntity.setAI(false);
		bukkitEntity.setGravity(false);
		bukkitEntity.setInvulnerable(true);
		return bukkitEntity;
	}

	/**
	 * 
	 * @param field
	 * @param value        to be applied (Note: this can potentially be an
	 *                     {@link UnsetInfo})!
	 * @param bukkitEntity
	 * @param nmsEntity
	 * @return
	 */
	public static boolean bukkitApplySettableFieldValue(SettableField field, Info value, LivingEntity bukkitEntity,
			EntityLiving nmsEntity) {
		AttributeInstance ai;
		switch (field) {
			case ATTACK_DAMAGE:
				ai = getAttributeInstanceInjectIfNeeded(Attribute.GENERIC_ATTACK_DAMAGE, bukkitEntity);
				ai.setBaseValue(((SimpleInfo<Double>) value).get());
				return true;
			case ATTACK_ENTITY:
				EntityUtils.nmsAddGoalAttackEntityType(((SimpleInfo<EntityType>) value).get(), 1, nmsEntity);
				return true;
			case ATTACK_KNOCKBACK:
				ai = getAttributeInstanceInjectIfNeeded(Attribute.GENERIC_ATTACK_KNOCKBACK, bukkitEntity);
				ai.setBaseValue(((SimpleInfo<Double>) value).get());
				return true;
			case BABY_MODE:
				if (bukkitEntity instanceof Ageable) {
					if (((SimpleInfo<Boolean>) value).get()) {
						((Ageable) bukkitEntity).setBaby();
					} else {
						((Ageable) bukkitEntity).setAdult();
					}
					return true;
				}
				return false;
			case BOOTY_CHEST:
				if (bukkitEntity instanceof ChestedHorse) {
					((ChestedHorse) bukkitEntity).setCarryingChest(((SimpleInfo<Boolean>) value).get());
					return true;
				}
				return false;
			case CUSTOM_NAME:
				if (value.type().equals(InfoType.UNSET)) {
					bukkitEntity.setCustomName("");
					bukkitEntity.setCustomNameVisible(false);
				} else {
					bukkitEntity.setCustomName(((SimpleInfo<String>) value).get());
					bukkitEntity.setCustomNameVisible(true);
				}
				return true;
			case FIRE_THORNS:
				if (((SimpleInfo<Boolean>) value).get()) {
					bukkitEntity.getPersistentDataContainer().set(CustomNBT.CUSTOM_MOB_PLAYTHROUGH_FIRETHORNS,
							PersistentDataType.BYTE, (byte) 1);
				}
				return true;
			case GLIDE_ENTRANCE:
				if (((SimpleInfo<Boolean>) value).get()) {
					ItemStack originalChestItem = bukkitEntity.getEquipment().getChestplate();
					bukkitEntity.getEquipment().setChestplate(new ItemStack(Material.ELYTRA, 1));
					bukkitEntity.setGliding(true);
					UUID entityId = bukkitEntity.getUniqueId();
					Threads.runMainRepeating(new BukkitRunnable() {
						@Override
						public void run() {
							Entity laterEntity = Bukkit.getEntity(entityId);
							if (laterEntity == null || !laterEntity.isValid()
									|| !(laterEntity instanceof LivingEntity)) {
								super.cancel();
								return;
							}
							LivingEntity laterLivingEntity = (LivingEntity) laterEntity;
							if (laterLivingEntity.isOnGround()) {
								laterLivingEntity.getEquipment().setChestplate(originalChestItem);
								System.out.println("SETTING TO " + originalChestItem.getType());
								super.cancel();
							}
						}
					}, 20L, 20L);
				}
				return true;
			case INVINCIBLE:
				if (((SimpleInfo<Boolean>) value).get()) {
					bukkitEntity.getPersistentDataContainer().set(CustomNBT.CUSTOM_MOB_PLAYTHROUGH_UNDAMAGABLE,
							PersistentDataType.BYTE, (byte) 1);
				}
				return true;
			case HEAD_BANNER:
				if (bukkitEntity instanceof Skeleton || bukkitEntity instanceof Zombie
						|| bukkitEntity instanceof Villager
						|| bukkitEntity instanceof Illager) {

					bukkitEntity.getEquipment().setHelmet(((ItemInfo) value).get());
					return true;
				}
				return false;
			case HEALTH:
				ai = getAttributeInstanceInjectIfNeeded(Attribute.GENERIC_MAX_HEALTH, bukkitEntity);
				ai.setBaseValue(((SimpleInfo<Double>) value).get());
				return true;
			case HORSE_ARMOR:
				if (bukkitEntity instanceof Horse) {
					if (value.type().equals(InfoType.UNSET)) {
						((Horse) bukkitEntity).getInventory().setArmor(null);
						return true;
					} else {
						((Horse) bukkitEntity).getInventory().setArmor(((ItemInfo) value).get());
						return true;
					}
				}
				return false;
			case PEDESTAL_MODE:
				break;
			case ITEM_PICKUP:
				bukkitEntity.setCanPickupItems(((SimpleInfo<Boolean>) value).get());
				break;
			case PIG_SADDLED:
				if (bukkitEntity instanceof Steerable) {
					((Steerable) bukkitEntity).setSaddle(((SimpleInfo<Boolean>) value).get());
					return true;
				}
				return false;
			case SLIME_SIZE:
				if (bukkitEntity instanceof Slime) {
					((Slime) bukkitEntity)
							.setSize(Math.min(((SimpleInfo<Integer>) value).get(), EntityUtils.SLIME_MAX_SIZE));
					return true;
				}
				return false;
		}
		return false;
	}

	public static LivingEntity spawnCustomMob(Location spawnLoc, CustomMobInfo mobInfo) {
		Info pedestalModeInfo = mobInfo.getField(SettableField.PEDESTAL_MODE);
		boolean pedestalMode = false;
		if (!pedestalModeInfo.type().equals(InfoType.UNSET)) {
			SimpleInfo<Boolean> pedestalModeSimpleInfo = (SimpleInfo<Boolean>) mobInfo
					.getField(SettableField.PEDESTAL_MODE);
			pedestalMode = pedestalModeSimpleInfo.get();
		}
		LivingEntity ret = pedestalMode ? EntityUtils.spawnPedestalBukkitEntity(spawnLoc, mobInfo.getEntityType())
				: EntityUtils.spawnCleanBukkitEntity(spawnLoc, mobInfo.getEntityType());
		EntityLiving retNMS = EntityUtils.bukkitToNMSType(ret);
		List<String> dropsList = mobInfo.getCustomDrops().stream().map(dropRepresentation -> {
			return dropRepresentation.serialize().toString();
		}).toList();
		ret.getPersistentDataContainer().set(CustomNBT.PLAYTHROUGH_DROPS,
				StringArrayPersistentDataType.STRING_ARRAY_TYPE, dropsList.toArray(new String[dropsList.size()]));
		for (int i = 0; i < mobInfo.getEquipment().length; i++) {
			ItemStack equipment = ItemInfo.getItemFrom(mobInfo.getEquipment()[i]);
			if (equipment == null)
				continue;
			EquipmentSlot es = null;
			switch (i) {
				case 0:
					es = EquipmentSlot.HEAD;
					break;
				case 1:
					es = EquipmentSlot.CHEST;
					break;
				case 2:
					es = EquipmentSlot.LEGS;
					break;
				case 3:
					es = EquipmentSlot.FEET;
					break;
				case 4:
					es = EquipmentSlot.HAND;
					break;
			}
			if (es != null) {
				ret.getEquipment().setItem(es, equipment);
			}
		}
		mobInfo.getPotionEffects().values().forEach(potionRepresentation -> {
			ret.addPotionEffect(potionRepresentation.potionEffect());
		});
		// Apply settable fields last since Glide Entrance needs to know the possible
		// CHEST equipment.
		for (SettableField settableField : mobInfo.getKeySet()) {
			Info fieldInfo = mobInfo.getField(settableField);
			bukkitApplySettableFieldValue(settableField, fieldInfo, ret, retNMS);
		}
		return ret;
	}

	public static Optional<Double> bukkitReadAttribute(LivingEntity entity, Attribute attribute) {
		AttributeInstance ai = entity.getAttribute(attribute);
		if (ai == null) {
			return Optional.empty();
		}
		return Optional.of(ai.getBaseValue());
	}

	public static AttributeInstance getAttributeInstanceInjectIfNeeded(Attribute attribute, LivingEntity entity) {
		AttributeInstance ai = entity.getAttribute(attribute);
		if (ai == null) {
			entity.registerAttribute(attribute);
			ai = entity.getAttribute(attribute);
		}
		return ai;
	}

	/**
	 * Teleport while preventing fall damage and the like.
	 * @param entity
	 * @param location
	 */
	public static void teleportSafely(Entity entity, Location location) {
		entity.setVelocity(new Vector(0, 0, 0));
		entity.setFallDistance(0f);
		entity.teleport(location);
	}

	/**
	 * Note: alter the attack damage attribute of the entity after calling this
	 * method if it did not already attack previously If the damage attribute is not
	 * altered, it will take on the default attack damage value
	 * 
	 * @return whether the method was successful or not
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean nmsAddGoalAttackEntityType(EntityType attackType, int goalPriority, EntityLiving entity) {
		if (!(entity instanceof EntityCreature)) {
			return false;
		}
		LivingEntity bukkitEntity = EntityUtils.nmsToBukkitType(entity);
		// The goals being added require that the entity has an attack damage value
		// specified.
		AttributeInstance attack = bukkitEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		if (attack == null) {
			bukkitEntity.registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		}
		Optional<Class<? extends EntityLiving>> nmsClassOpt = EntityUtils.bukkitTypeToNMSClass(attackType);
		if (nmsClassOpt.isEmpty()) {
			return false;
		}
		EntityCreature creature = (EntityCreature) entity;

		// Easy Obfuscation Mapping checker (NMS):
		// https://nms.screamingsandals.org/1.20/net/minecraft/world/entity/Mob.html

		// Goal Selector
		// bR 1.18.1
		// bQ 1.18.2
		// bS 1.19
		// bO 1.20
		creature.bO.a(goalPriority, new PathfinderGoalMeleeAttack(creature, 1.0D, true));
		// Target Selector
		// bS 1.18.1
		// bR 1.18.2
		// bT 1.19
		// bP 1.20
		creature.bP.a(goalPriority, new PathfinderGoalNearestAttackableTarget(creature, nmsClassOpt.get(), true));
		return true;
	}

	public static void kill(LivingEntity entity) {
		entity.setHealth(0);
		entity.remove();
	}

	public static EntityType deserializeEntityType(String serialized) {
		// If EntityType values are ever changed, "fix" old data here.
		return EntityType.valueOf(serialized);
	}

	public static String serializeEntityType(EntityType entityType) {
		// If EntityType values are ever changed, "fix" old data here.
		return entityType.name();
	}

	/**
	 * Make sure this list is updated for every mob that can be spawned using an egg
	 * / with our mob spawning system. See net.minecraft.server.EntityTypes for
	 * Entity class names (ctrl F whichever you are looking for). NMS (currently
	 * 1.19)
	 */
	public static Optional<Class<? extends EntityLiving>> bukkitTypeToNMSClass(EntityType entityType) {
		switch (entityType) {
			// Pre 1.17
			case AREA_EFFECT_CLOUD:
				return Optional.empty();
			case ARMOR_STAND:
				return Optional.empty();
			case ARROW:
				return Optional.empty();
			case BAT:
				return Optional.of(EntityBat.class);
			case BEE:
				return Optional.of(EntityBee.class);
			case BLAZE:
				return Optional.of(EntityBlaze.class);
			case BOAT:
				return Optional.empty();
			case CAT:
				return Optional.of(EntityCat.class);
			case CAVE_SPIDER:
				return Optional.of(EntityCaveSpider.class);
			case CHICKEN:
				return Optional.of(EntityChicken.class);
			case COD:
				return Optional.of(EntityCod.class);
			case COW:
				return Optional.of(EntityCow.class);
			case CREEPER:
				return Optional.of(EntityCreeper.class);
			case DOLPHIN:
				return Optional.of(EntityDolphin.class);
			case DONKEY:
				return Optional.of(EntityHorseDonkey.class);
			case DRAGON_FIREBALL:
				return Optional.empty();
			case DROPPED_ITEM:
				return Optional.empty();
			case DROWNED:
				return Optional.of(EntityDrowned.class);
			case EGG:
				return Optional.empty();
			case ELDER_GUARDIAN:
				return Optional.of(EntityGuardianElder.class);
			case ENDERMAN:
				return Optional.of(EntityEnderman.class);
			case ENDERMITE:
				return Optional.of(EntityEndermite.class);
			case ENDER_CRYSTAL:
				return Optional.empty();
			case ENDER_DRAGON:
				return Optional.of(EntityEnderDragon.class);
			case ENDER_PEARL:
				return Optional.empty();
			case ENDER_SIGNAL:
				return Optional.empty();
			case EVOKER:
				return Optional.of(EntityEvoker.class);
			case EVOKER_FANGS:
				return Optional.empty();
			case EXPERIENCE_ORB:
				return Optional.empty();
			case FALLING_BLOCK:
				return Optional.empty();
			case FIREBALL:
				return Optional.empty();
			case FIREWORK:
				return Optional.empty();
			case FISHING_HOOK:
				return Optional.empty();
			case FOX:
				return Optional.of(EntityFox.class);
			case GHAST:
				return Optional.of(EntityGhast.class);
			case GIANT:
				return Optional.of(EntityGiantZombie.class);
			case GUARDIAN:
				return Optional.of(EntityGuardian.class);
			case HOGLIN:
				return Optional.of(EntityHoglin.class);
			case HORSE:
				return Optional.of(EntityHorse.class);
			case HUSK:
				return Optional.of(EntityZombieHusk.class);
			case ILLUSIONER:
				return Optional.of(EntityIllagerIllusioner.class);
			case IRON_GOLEM:
				return Optional.of(EntityIronGolem.class);
			case ITEM_FRAME:
				return Optional.empty();
			case LEASH_HITCH:
				return Optional.empty();
			case LIGHTNING:
				return Optional.empty();
			case LLAMA:
				return Optional.of(EntityLlama.class);
			case LLAMA_SPIT:
				return Optional.empty();
			case MAGMA_CUBE:
				return Optional.of(EntityMagmaCube.class);
			case MINECART:
				return Optional.empty();
			case MINECART_CHEST:
				return Optional.empty();
			case MINECART_COMMAND:
				return Optional.empty();
			case MINECART_FURNACE:
				return Optional.empty();
			case MINECART_HOPPER:
				return Optional.empty();
			case MINECART_MOB_SPAWNER:
				return Optional.empty();
			case MINECART_TNT:
				return Optional.empty();
			case MULE:
				return Optional.of(EntityHorseMule.class);
			case MUSHROOM_COW:
				return Optional.of(EntityMushroomCow.class);
			case OCELOT:
				return Optional.of(EntityOcelot.class);
			case PAINTING:
				return Optional.empty();
			case PANDA:
				return Optional.of(EntityPanda.class);
			case PARROT:
				return Optional.of(EntityParrot.class);
			case PHANTOM:
				return Optional.of(EntityPhantom.class);
			case PIG:
				return Optional.of(EntityPig.class);
			case PIGLIN:
				return Optional.of(EntityPiglin.class);
			case PIGLIN_BRUTE:
				return Optional.of(EntityPiglinBrute.class);
			case PILLAGER:
				return Optional.of(EntityPillager.class);
			case PLAYER:
				return Optional.of(EntityPlayer.class);
			case POLAR_BEAR:
				return Optional.of(EntityPolarBear.class);
			case PRIMED_TNT:
				return Optional.empty();
			case PUFFERFISH:
				return Optional.of(EntityPufferFish.class);
			case RABBIT:
				return Optional.of(EntityRabbit.class);
			case RAVAGER:
				return Optional.of(EntityRavager.class);
			case SALMON:
				return Optional.of(EntitySalmon.class);
			case SHEEP:
				return Optional.of(EntitySheep.class);
			case SHULKER:
				return Optional.of(EntityShulker.class);
			case SHULKER_BULLET:
				return Optional.empty();
			case SILVERFISH:
				return Optional.of(EntitySilverfish.class);
			case SKELETON:
				return Optional.of(EntitySkeleton.class);
			case SKELETON_HORSE:
				return Optional.of(EntityHorseSkeleton.class);
			case SLIME:
				return Optional.of(EntitySlime.class);
			case SMALL_FIREBALL:
				return Optional.empty();
			case SNOWBALL:
				return Optional.empty();
			case SNOWMAN:
				return Optional.of(EntitySnowman.class);
			case SPECTRAL_ARROW:
				return Optional.empty();
			case SPIDER:
				return Optional.of(EntitySpider.class);
			case SPLASH_POTION:
				return Optional.empty();
			case SQUID:
				return Optional.of(EntitySquid.class);
			case STRAY:
				return Optional.of(EntitySkeletonStray.class);
			case STRIDER:
				return Optional.of(EntityStrider.class);
			case THROWN_EXP_BOTTLE:
				return Optional.empty();
			case TRADER_LLAMA:
				return Optional.of(EntityLlamaTrader.class);
			case TRIDENT:
				return Optional.empty();
			case TROPICAL_FISH:
				return Optional.of(EntityTropicalFish.class);
			case TURTLE:
				return Optional.of(EntityTurtle.class);
			case UNKNOWN:
				return Optional.empty();
			case VEX:
				return Optional.of(EntityVex.class);
			case VILLAGER:
				return Optional.of(EntityVillager.class);
			case VINDICATOR:
				return Optional.of(EntityVindicator.class);
			case WANDERING_TRADER:
				return Optional.of(EntityVillagerTrader.class);
			case WITCH:
				return Optional.of(EntityWitch.class);
			case WITHER:
				return Optional.of(EntityWither.class);
			case WITHER_SKELETON:
				return Optional.of(EntitySkeletonWither.class);
			case WITHER_SKULL:
				return Optional.empty();
			case WOLF:
				return Optional.of(EntityWolf.class);
			case ZOGLIN:
				return Optional.of(EntityZoglin.class);
			case ZOMBIE:
				return Optional.of(EntityZombie.class);
			case ZOMBIE_HORSE:
				return Optional.of(EntityHorseZombie.class);
			case ZOMBIE_VILLAGER:
				return Optional.of(EntityZombieVillager.class);
			case ZOMBIFIED_PIGLIN:
				return Optional.of(EntityPigZombie.class);
			// 1.17
			case AXOLOTL:
				return Optional.of(Axolotl.class);
			case GLOW_ITEM_FRAME:
				return Optional.empty();
			case GLOW_SQUID:
				return Optional.of(GlowSquid.class);
			case GOAT:
				return Optional.of(Goat.class);
			case MARKER:
				return Optional.empty();
			// 1.18
			// No new additions
			// 1.19
			case ALLAY:
				return Optional.of(Allay.class);
			case CHEST_BOAT:
				return Optional.empty();
			case FROG:
				return Optional.of(Frog.class);
			case TADPOLE:
				return Optional.of(Tadpole.class);
			case WARDEN:
				return Optional.of(Warden.class);
			default:
				break;
		}
		Logs.notifyWarningNMS("EntityUtils Bukkit Type to NMS class is missing the mapping for " + entityType.name()
				+ "! The warning should never be displayed unless the switch statement is incomplete!  Please fill it in completely.");
		return Optional.empty();
	}
}
