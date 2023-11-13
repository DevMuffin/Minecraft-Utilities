package com.dungeonbuilder.utils.item;

import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.lang.WordUtils;

public class LoreUtils {
	public static Stream<String> wrapped(String lore) {
		String wrappedString = WordUtils.wrap(lore, 30);
		return Arrays.stream(wrappedString.split(System.lineSeparator()));
	}
}
