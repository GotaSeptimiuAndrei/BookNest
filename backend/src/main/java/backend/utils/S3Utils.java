package backend.utils;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

public class S3Utils {

	public static String saveFileToS3Bucket(S3Client s3Client, String bucketName, MultipartFile file) {
		try {
			String fileName = file.getOriginalFilename();
			String contentType = file.getContentType();
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.contentType(contentType)
				.key(fileName)
				.build();

			RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

			s3Client.putObject(putObjectRequest, requestBody);

			return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not upload the file to S3: " + e.getMessage());
		}
	}

	public static void deleteFileFromS3Bucket(S3Client s3Client, String bucketName, String fileUrl) {
		if (fileUrl == null || fileUrl.isBlank())
			return;

		String prefix = "https://" + bucketName + ".s3.amazonaws.com/";
		String key = fileUrl.startsWith(prefix) ? fileUrl.substring(prefix.length()) : fileUrl;

		DeleteObjectRequest delReq = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();

		s3Client.deleteObject(delReq);
	}

}
