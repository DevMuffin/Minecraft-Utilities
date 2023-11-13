package com.dungeonbuilder.utils.java;

import java.util.UUID;

public class UUIDDisplay {

	/**
	 * Create a visually short (but not necessarily unique) representation of an
	 * object that has a unique id property.
	 * 
	 * @param id
	 * @return
	 */
	public static String shorten(UUID id) {
		String idStr = id.toString();
//		Start with the last 4 chars of id, then add a dash
		String shortened = idStr.substring(idStr.length() - 4) + "-";
		String[] sections = idStr.split("-");
		for (int i = 0; i < 4; i++) {
//			Add the first character of each section (excluding the final section [since there are 5 sections to a UUID] - where we already took the 4 characters for the initial part of the String).
			String portion = sections[i];
			shortened += portion.charAt(0);
		}
		return shortened;
	}

	/**
	 * Create a visually short (but not necessarily unique) representation of an
	 * object that has multiple unique id properties to take advantage of.
	 * 
	 * @param id1
	 * @param id2
	 * @return
	 */
	public static String shorten(UUID id1, UUID id2) {
		String idStr = id1.toString();
		String id2Str = id2.toString().replaceAll("-", "");
//		Start with the last 4 chars of id
		String shortened = idStr.substring(idStr.length() - 4) + "-";
		for (int i = 0; i < 4; i++) {
//			Use the first 4 characters of idStr as an index into id2Str - and add those characters.
			int index = (idStr.charAt(i) - '0') % id2Str.length();
			shortened += id2Str.charAt(index);
		}
		return shortened;
	}
}
