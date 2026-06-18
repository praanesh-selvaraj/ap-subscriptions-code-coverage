package com.kmp.aeroparker.application.model.interfaces;

import com.kmp.aeroparker.application.model.EmailDispatcherParameters;

public interface EmailDispatcher
{
	void sendEmail(EmailDispatcherParameters emailDispatcherObj );
}