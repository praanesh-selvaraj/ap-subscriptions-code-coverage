package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.SequenceDao;

@ExtendWith(MockitoExtension.class)
class SequenceServiceTest
{
	@Mock
	private SequenceDao dao;
	@InjectMocks
	private SequenceService service;

	@Test
	void testGetNextId()
	{
		when(dao.getNextId(anyInt())).thenReturn(12334);
		assertThat(service.getNextId(2)).isNotNull()
				.isEqualTo(12334);
	}

	@Test
	void testGetNextId_Sequence_Null()
	{
		when(dao.getNextId(anyInt())).thenReturn(null);
		assertThat(service.getNextId(2)).isZero();
	}

	@Test
	void testGetNextId_Invalid_Param()
	{
		assertThat(service.getNextId(0)).isZero();
	}
}