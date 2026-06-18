package com.kmp.aeroparker.application.container;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.MountableFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ITMySQLContainer extends MySQLContainer<ITMySQLContainer>
{
	private final static String IMAGE_VERSION = "mysql:8.0.30";
	// Singleton container.
	private static ITMySQLContainer container;

	private ITMySQLContainer()
	{
		super(IMAGE_VERSION);
	}

	public static ITMySQLContainer getInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/setup.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getInvoiceInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/invoice.sql"),
							"/docker-entrypoint-initdb.d/invoice.sql");
		}
		return container;
	}

	public static ITMySQLContainer getInstanceApiUser()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/apiUserDao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getAdditionalDetails()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/additonal-details.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getSiteInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/site-dao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getContactInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/contact-dao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getSequenceInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/sequence-dao.sql"),
							"/docker-entrypoint-initdb.d/sequence-dao.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getVehiclelookupInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/vehicle-lookup-dao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getBookingInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/booking-dao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getQuotaInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/quota-dao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}
	
	public static ITMySQLContainer getAnalyticsInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/analytics-dao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	public static ITMySQLContainer getPromotionInstance()
	{
		if (container == null)
		{
			container = new ITMySQLContainer().withLogConsumer(new Slf4jLogConsumer(log))
					.withCopyFileToContainer(MountableFile.forClasspathResource("init/promotion-dao.sql"),
							"/docker-entrypoint-initdb.d/setup.sql")
					.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS))
					.withStartupAttempts(1);
		}
		return container;
	}

	@Override
	public void start()
	{
		super.start();
		System.setProperty("DB_URL", container.getJdbcUrl());
		System.setProperty("DB_USERNAME", container.getUsername());
		System.setProperty("DB_PASSWORD", container.getPassword());
	}

	@Override
	public void stop()
	{
		// Do nothing, JVM handles shut down.
	}
}
