package com.dungeonbuilder.utils.aws.s3;

import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;

import com.dungeonbuilder.utils.aws.AWS;
import com.dungeonbuilder.utils.aws.AWS.AWSFailureException;
import com.dungeonbuilder.utils.aws.AWS.AWSListResponse;
import com.dungeonbuilder.utils.serialization.info.Dungeon;
import com.dungeonbuilder.utils.serialization.info.Info;
import com.dungeonbuilder.utils.serialization.info.event.EventInfo;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class EventsBucket {

	public static final String EVENTS_BUCKET = "all-events-prod";

//	private static String getS3FolderPrefix(InfoType eventType) {
//		switch (eventType) {
//		case EVENT_CAUSE:
//			return "causes";
//		case EVENT:
//			return "events";
//		default:
//			Logs.notifyWarning("S3 Folder prefix for InfoType: " + eventType + " returning error_invalid!");
//			return "error_invalid";
//		}
//	}

	public static String getFolderPrefix(UUID ownerId, UUID dungeonId) {
		return ownerId.toString() + "/" + dungeonId.toString() + "/";
	}

	public static String getFolderPrefix(Dungeon dungeon) {
		return getFolderPrefix(dungeon.getOwnerId(), dungeon.getId());
	}

	public static String getS3Key(UUID ownerId, UUID dungeonId, UUID eventId) {
		return getFolderPrefix(ownerId, dungeonId) + eventId.toString();
	}

	public static String getS3Key(Dungeon dungeon, UUID eventId) {
		return getS3Key(dungeon.getOwnerId(), dungeon.getId(), eventId);
	}

	public static String getS3Key(Dungeon dungeon, EventInfo info) {
		return getS3Key(dungeon.getOwnerId(), dungeon.getId(), info.getUUID());
	}

	public static void upload_BLOCKING(Dungeon dungeon, EventInfo info) throws AWSFailureException {
		try {
			AWS.s3.putObject(PutObjectRequest.builder().bucket(EVENTS_BUCKET).key(getS3Key(dungeon, info)).build(),
					RequestBody.fromString(info.serialize().toString()));
		} catch (S3Exception e) {
			throw new AWSFailureException(e);
		}
	}

	public static Optional<JsonObject> downloadRawObject_BLOCKING(String s3Key, Dungeon dungeon)
			throws AWSFailureException {
		try {
			ResponseInputStream<GetObjectResponse> objectStream = AWS.s3
					.getObject(GetObjectRequest.builder().bucket(EVENTS_BUCKET).key(s3Key).build());
			String content = CharStreams.toString(new InputStreamReader(objectStream, Charsets.UTF_8));
			return Optional.of(new JsonParser().parse(content).getAsJsonObject());
		} catch (NoSuchKeyException nske) {
			return Optional.empty();
		} catch (S3Exception s3e) {
			throw new AWSFailureException(s3e);
		} catch (Exception e) {
//			Anthony TODO this exception could be caused directly by Info.deserialize having an issue reading invalid json.
			e.printStackTrace();
			return Optional.empty();
		}
	}

	/**
	 * This method blocks until the file contents are read.
	 * 
	 * @param s3Key
	 * @throws AWSFailureException
	 */
	public static Optional<Info> download_BLOCKING(String s3Key, Dungeon dungeon) throws AWSFailureException {
		try {
			Optional<JsonObject> rawObject = downloadRawObject_BLOCKING(s3Key, dungeon);
			if (rawObject.isEmpty())
				return Optional.empty();
			return Info.deserialize(rawObject.get(), dungeon);
		} catch (NoSuchKeyException nske) {
			return Optional.empty();
		} catch (S3Exception s3e) {
			throw new AWSFailureException(s3e);
		} catch (Exception e) {
//			Anthony TODO this exception could be caused directly by Info.deserialize having an issue reading invalid json.
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public static void delete_BLOCKING(String s3Key) throws AWSFailureException {
		try {
			AWS.s3.deleteObject(DeleteObjectRequest.builder().bucket(EVENTS_BUCKET).key(s3Key).build());
		} catch (S3Exception e) {
			throw new AWSFailureException(e);
		}
	}

	/**
	 * Returns a list containing the Custom Mob UUIDs stored in AWS.
	 * 
	 * @param s3Folder
	 * @return
	 * @throws AWSFailureException
	 */
	public static AWSListResponse list_BLOCKING(String s3Folder, int limit) throws AWSFailureException {
		return AWS.list_BLOCKING(EVENTS_BUCKET, s3Folder, limit, Optional.empty());
	}

	/**
	 * Returns a list containing the Custom Mob UUIDs stored in AWS.
	 * 
	 * @param s3Folder
	 * @return
	 * @throws AWSFailureException
	 */
	public static AWSListResponse list_BLOCKING(String s3Folder, int limit, String continuationToken)
			throws AWSFailureException {
		return AWS.list_BLOCKING(EVENTS_BUCKET, s3Folder, limit, Optional.of(continuationToken));
	}
}
