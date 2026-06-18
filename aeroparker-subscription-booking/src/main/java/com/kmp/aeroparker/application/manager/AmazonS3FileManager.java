package com.kmp.aeroparker.application.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AmazonS3FileManager
{
	private final AmazonS3 s3client;

	private static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
	private static final int READ_BUF_SIZE = 1024;
	private static final int BASE_READ_LEN = 0;

	public File downloadFile(String bucketName, String key) throws IOException
	{
		log.debug("Downloading file with key {} from S3", key);

		File file = new File(TEMP_DIRECTORY, key);

		S3Object object = s3client.getObject(bucketName, key);
		S3ObjectInputStream s3InputStream = object.getObjectContent();
		FileOutputStream out = new FileOutputStream(file, false);

		try
		{
			byte[] read_buf = new byte[READ_BUF_SIZE];
			int read_len = BASE_READ_LEN;

			while ((read_len = s3InputStream.read(read_buf)) > BASE_READ_LEN)
			{
				out.write(read_buf, 0, read_len);
			}
		}
		finally
		{
			s3InputStream.close();
			out.close();
		}

		return file;
	}
}
