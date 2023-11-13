package com.dungeonbuilder.utils.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.block.data.type.Switch;

public enum LeverRotation {
	ROT_0(0), ROT_90(90), ROT_180(180), ROT_270(270), ROT_INVALID_FACE(0), ROT_INVALID_DIR(0);

	private int degrees;

	LeverRotation(int degrees) {
		this.degrees = degrees;
	}

	public static LeverRotation opposite(LeverRotation of) {
		switch (of) {
		case ROT_0:
			return ROT_180;
		case ROT_90:
			return ROT_270;
		case ROT_180:
			return ROT_0;
		case ROT_270:
			return ROT_90;
		default:
			return of;
		}
	}

	public static LeverRotation of(Block lever) {
		Switch leverAsSwitch = (Switch) lever.getBlockData();
		if (!leverAsSwitch.getAttachedFace().equals(AttachedFace.FLOOR)) {
			return LeverRotation.ROT_INVALID_FACE;
		}
		boolean toggled = leverAsSwitch.isPowered();
		BlockFace facingDir = leverAsSwitch.getFacing();
		LeverRotation ret = ROT_INVALID_DIR;
		switch (facingDir) {
		case NORTH:
			ret = ROT_0;
			break;
		case SOUTH:
			ret = ROT_180;
			break;
		case EAST:
			ret = ROT_90;
			break;
		case WEST:
			ret = ROT_270;
			break;
		default:
			ret = LeverRotation.ROT_INVALID_DIR;
		}
		return toggled ? ret : opposite(ret);
	}

	public int getDegrees() {
		return this.degrees;
	}
}
