package com.kmp.aeroparker.application.manager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@ExtendWith(MockitoExtension.class)
public class AmazonS3FileManagerTest
{
	@Mock
	private AmazonS3 s3; 
	@InjectMocks
	private AmazonS3FileManager s3Client;

	@Test
	public void testDownloadFile() throws IOException
	{
		final String tempDirectory = System.getProperty("java.io.tmpdir");
		String bucketName = "bucket name";
		String key = "key";
		File file = new File(tempDirectory, key);

		S3Object s3Obj = mock(S3Object.class);
		S3ObjectInputStream s3InputStream = mock(S3ObjectInputStream.class);
		byte[] read_buf = new byte[1024];

		when(s3.getObject(anyString(), anyString())).thenReturn(s3Obj);
		when(s3Obj.getObjectContent()).thenReturn(s3InputStream);
		when(s3InputStream.read(any())).thenReturn(2).thenReturn(0);
		assertNotNull(s3InputStream.read(read_buf));
		assertNotNull(s3Client.downloadFile(bucketName, key));
		file.delete();
	}
}