package com.dungeonbuilder.utils.nbt;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author https://pastebin.com/JJnLDU2V
 */
public class StringArrayPersistentDataType implements PersistentDataType<byte[], String[]> {

	public static final StringArrayPersistentDataType STRING_ARRAY_TYPE = new StringArrayPersistentDataType();

	private static final Charset charset = StandardCharsets.UTF_8;

	@Override
	public Class<byte[]> getPrimitiveType() {
		return byte[].class;
	}

	@Override
	public Class<String[]> getComplexType() {
		return String[].class;
	}

	@Override
	public byte[] toPrimitive(String[] strings, PersistentDataAdapterContext itemTagAdapterContext) {
		byte[][] allStringBytes = new byte[strings.length][];
		int total = 0;
		for (int i = 0; i < allStringBytes.length; i++) {
			byte[] bytes = strings[i].getBytes(charset);
			allStringBytes[i] = bytes;
			total += bytes.length;
		}
		ByteBuffer buffer = ByteBuffer.allocate(total + allStringBytes.length * 4); // stores integers
		for (byte[] bytes : allStringBytes) {
			buffer.putInt(bytes.length);
			buffer.put(bytes);
		}
		return buffer.array();
	}

	@Override
	public String[] fromPrimitive(byte[] bytes, PersistentDataAdapterContext itemTagAdapterContext) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		ArrayList<String> list = new ArrayList<>();
		while (buffer.remaining() > 0) {
			if (buffer.remaining() < 4)
				break;
			int stringLength = buffer.getInt();
			if (buffer.remaining() < stringLength)
				break;
			byte[] stringBytes = new byte[stringLength];
			buffer.get(stringBytes);
			list.add(new String(stringBytes, charset));
		}
		return list.toArray(new String[list.size()]);
	}
}