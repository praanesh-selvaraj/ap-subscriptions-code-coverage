CREATE TABLE `currencies` (
  `id` int NOT NULL,
  `code` varchar(3) NOT NULL,
  `symbol` varchar(10) NOT NULL,
  `htmlSymbol` varchar(20) NOT NULL,
  `isoCode` varchar(3) DEFAULT NULL,
  `currencyNumber` varchar(3) DEFAULT NULL,
  `conversionRate` decimal(19,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
);

INSERT INTO currencies (id, code, symbol, htmlSymbol, isoCode, currencyNumber, conversionRate) VALUES(1, 'USD', '$', '&#36;', NULL, NULL, 1.2637);
INSERT INTO currencies (id, code, symbol, htmlSymbol, isoCode, currencyNumber, conversionRate) VALUES(16, 'EUR', '€', '&euro;', 'EUR', '978', 1.1617);

CREATE TABLE `languages` (
  `id` int NOT NULL,
  `languageName` varchar(100) NOT NULL,
  `languageCode` varchar(45) NOT NULL,
  `displayCode` varchar(45) NOT NULL,
  `displayName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO languages (id, languageName, languageCode, displayCode, displayName) VALUES(1, 'English (United Kingdom)', 'en-gb', 'en-gb', 'en');
INSERT INTO languages (id, languageName, languageCode, displayCode, displayName) VALUES(8, 'English (United States)', 'en-us', 'en-us', NULL);
INSERT INTO languages (id, languageName, languageCode, displayCode, displayName) VALUES(3, 'German', 'de', 'de', NULL);

CREATE TABLE `locations` (
  `id` int NOT NULL,
  `name` varchar(50) NOT NULL,
  `decimalSymbol` varchar(25) NOT NULL,
  `numDigitsAfterDecimal` int NOT NULL,
  `digitGroupSymbol` varchar(25) NOT NULL,
  `digitGrouping` varchar(25) NOT NULL,
  `negNumFormat` varchar(25) NOT NULL,
  `displayLeadingZero` int NOT NULL,
  `currencySymbol` varchar(25) NOT NULL DEFAULT '£',
  `currencyPositiveFormat` varchar(25) NOT NULL,
  `currencyNegativeFormat` varchar(25) NOT NULL,
  `currencyDecimalPlaces` int NOT NULL,
  `currencyGroupingSymbol` varchar(25) NOT NULL,
  `currencyDecimalSymbol` varchar(25) NOT NULL,
  `currencyHidePennies` int NOT NULL,
  `dateFormat` varchar(25) NOT NULL,
  `longDateFormat` varchar(25) NOT NULL,
  `longDateTimeFormat` varchar(45) NOT NULL DEFAULT '',
  `dateFormatSQL` varchar(15) NOT NULL DEFAULT '%d/%m/%Y',
  `dateFormatForReports` varchar(25) NOT NULL DEFAULT 'yyyy-MM-dd',
  `timeShort` varchar(25) NOT NULL,
  `timeLong` varchar(25) NOT NULL,
  `time24hr` int NOT NULL,
  `currency` int NOT NULL DEFAULT '1',
  `isoCode` varchar(2) DEFAULT NULL,
  `calendarStartDay` tinyint(1) NOT NULL DEFAULT '0',
  `countryCallingCode` varchar(5) DEFAULT NULL,
  `sqlLocationCode` varchar(5) DEFAULT 'en_GB',
  `drinkingAge` int DEFAULT NULL,
  `defaultLanguageId` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_locations_name` (`name`),
  KEY `fk_locations_currency_idx` (`currency`),
  KEY `fk_locations_languages_defaultLanguageId_idx` (`defaultLanguageId`),
  CONSTRAINT `fk_locations_currency` FOREIGN KEY (`currency`) REFERENCES `currencies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_locations_languages_defaultLanguageId` FOREIGN KEY (`defaultLanguageId`) REFERENCES `languages` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO locations (id, name, decimalSymbol, numDigitsAfterDecimal, digitGroupSymbol, digitGrouping, negNumFormat, displayLeadingZero, currencySymbol, currencyPositiveFormat, currencyNegativeFormat, currencyDecimalPlaces, currencyGroupingSymbol, currencyDecimalSymbol, currencyHidePennies, dateFormat, longDateFormat, longDateTimeFormat, dateFormatSQL, dateFormatForReports, timeShort, timeLong, time24hr, currency, isoCode, calendarStartDay, countryCallingCode, sqlLocationCode, drinkingAge, defaultLanguageId) VALUES(19, 'United States', '.', 2, ',', '123,456,789', '-1.1', 1, '&#36;', '&pound;1.1', '-&pound;1.1', 2, ',', '.', 0, 'MM/dd/yyyy', 'MM/dd/yyyy', '', '%m/%d/%Y', 'yyyy-MM-dd', 'h:mm tt', 'h:mm:ss tt', 0, 1, NULL, 0, '', 'en_US', NULL, 1);
INSERT INTO locations (id, name, decimalSymbol, numDigitsAfterDecimal, digitGroupSymbol, digitGrouping, negNumFormat, displayLeadingZero, currencySymbol, currencyPositiveFormat, currencyNegativeFormat, currencyDecimalPlaces, currencyGroupingSymbol, currencyDecimalSymbol, currencyHidePennies, dateFormat, longDateFormat, longDateTimeFormat, dateFormatSQL, dateFormatForReports, timeShort, timeLong, time24hr, currency, isoCode, calendarStartDay, countryCallingCode, sqlLocationCode, drinkingAge, defaultLanguageId) VALUES(16, 'Germany', '.', 2, '.', '123,456,789', '-1.1', 1, '&#128;', '1.1&pound;', '- 1.1&pound;', 2, '.', ',', 0, 'dd.MM.yyyy', 'dd.MM.yyyy', '', '%d.%m.%Y', 'yyyy-MM-dd', 'hh:mm tt', 'hh:mm:ss tt', 1, 16, NULL, 1, '', 'de_DE', NULL, NULL);

CREATE TABLE `ab_sites` (
  `id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `siteType` varchar(10) NOT NULL DEFAULT 'AIRPORT',
  `numberOfTerminals` int NOT NULL DEFAULT '1',
  `isEnabled` smallint NOT NULL DEFAULT '0',
  `url` varchar(255) DEFAULT NULL,
  `paxPerWeek` int NOT NULL DEFAULT '0',
  `locationId` int NOT NULL,
  `timezone` varchar(63) NOT NULL DEFAULT 'Etc/GMT',
  `secondsFromGMT` int NOT NULL DEFAULT '0',
  `defaultStateCode` varchar(45) DEFAULT '',
  `defaultLanguageId` int DEFAULT NULL,
  `isUserAccountsEnabled` int NOT NULL,
  `passwordChangeDays` smallint NOT NULL DEFAULT '30',
  `isDesignaPinEnabled` int NOT NULL DEFAULT '0',
  `alertEmails` varchar(60) DEFAULT NULL,
  `barrierSystemServerId` int DEFAULT '0',
  `facilityName` varchar(255) DEFAULT NULL,
  `useDummyData` int NOT NULL DEFAULT '0',
  `vatRate` decimal(13,2) NOT NULL DEFAULT '20.00',
  `thirdPartyAvailabilityCheckEnabled` int NOT NULL DEFAULT '0',
  `additionalParkingAvailabilityFields` bit(1) DEFAULT b'0',
  `lengthOfStayType` smallint NOT NULL DEFAULT '1',
  `allowConsolidatorEmailResend` int NOT NULL DEFAULT '0',
  `defaultAffiliateId` int DEFAULT NULL,
  `productCodeMandatoryCheckEnabled` bit(1) NOT NULL DEFAULT b'0',
  `locationName` varchar(60) NOT NULL DEFAULT 'Terminal',
  `enableSendingFeedback` bit(1) NOT NULL DEFAULT b'0',
  `apiReserveBooking` bit(1) NOT NULL DEFAULT b'0',
  `apiTakePayment` bit(1) NOT NULL DEFAULT b'0',
  `apiPreventOverlappingBookings` bit(1) NOT NULL DEFAULT b'0',
  `airportCode` varchar(3) NOT NULL DEFAULT '',
  `defaultLocationName` varchar(255) DEFAULT '',
  `includeSoldOutInApi` bit(1) NOT NULL DEFAULT b'0',
  `preventDuplicateBookings` bit(1) NOT NULL DEFAULT b'0',
  `passwordReuseLimitAdmin` smallint NOT NULL DEFAULT '4',
  `passwordChangeDaysCallCentre` smallint NOT NULL DEFAULT '30',
  `passwordReuseLimitCallCentre` smallint NOT NULL DEFAULT '4',
  `enableCountryCodesFlagInput` bit(1) DEFAULT b'0',
  `passwordChangeDaysAgency` smallint NOT NULL DEFAULT '30',
  `passwordReuseLimitAgency` smallint NOT NULL DEFAULT '4',
  `bucketCalculationMethod` smallint NOT NULL DEFAULT '1',
  `apiEnableRequiredFields` bit(1) NOT NULL DEFAULT b'0',
  `includeProductCategoriesOnParkingAvailability` bit(1) NOT NULL DEFAULT b'0',
  `includePaymentMethodsInParkingAvailability` bit(1) NOT NULL DEFAULT b'0',
  `apiEnableIncludeGrace` bit(1) NOT NULL DEFAULT b'0',
  `apiSkipAvailabilityWithinWindow` bit(1) NOT NULL DEFAULT b'0',
  `validatePromotionOnAmendment` bit(1) NOT NULL DEFAULT b'0',
  `apiReserveTicketsWhenKeyIsValid` bit(1) NOT NULL DEFAULT b'0',
  `ccDefaultSearchDate` smallint NOT NULL DEFAULT '0',
  `showAllWhiteLabelAffiliateBookings` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `fk_sites_locationId_idx` (`locationId`),
  KEY `fk_sites_defaultLanguageId_idx` (`defaultLanguageId`),
  CONSTRAINT `fk_sites_defaultLanguageId` FOREIGN KEY (`defaultLanguageId`) REFERENCES `languages` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sites_locationId` FOREIGN KEY (`locationId`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);

INSERT INTO ab_sites (id, title, siteType, numberOfTerminals, isEnabled, url, paxPerWeek, locationId, timezone, secondsFromGMT, defaultStateCode, defaultLanguageId, isUserAccountsEnabled, passwordChangeDays, isDesignaPinEnabled, alertEmails, barrierSystemServerId, facilityName, useDummyData, vatRate, thirdPartyAvailabilityCheckEnabled, additionalParkingAvailabilityFields, lengthOfStayType, allowConsolidatorEmailResend, defaultAffiliateId, productCodeMandatoryCheckEnabled, locationName, enableSendingFeedback, apiReserveBooking, apiTakePayment, apiPreventOverlappingBookings, airportCode, defaultLocationName, includeSoldOutInApi, preventDuplicateBookings, passwordReuseLimitAdmin, passwordChangeDaysCallCentre, passwordReuseLimitCallCentre, enableCountryCodesFlagInput, passwordChangeDaysAgency, passwordReuseLimitAgency, bucketCalculationMethod, apiEnableRequiredFields, includeProductCategoriesOnParkingAvailability, includePaymentMethodsInParkingAvailability, apiEnableIncludeGrace, apiSkipAvailabilityWithinWindow, validatePromotionOnAmendment, apiReserveTicketsWhenKeyIsValid, ccDefaultSearchDate, showAllWhiteLabelAffiliateBookings) VALUES(17, 'Berlin', 'AIRPORT', 1, 0, 'www.berlin-airport.de/', 0, 16, 'Etc/GMT', 0, '', 3, 1, 30, 0, '', 0, '', 0, 20.00, 0, 0, 1, 0, 30, 0, 'Terminal', 0, 0, 0, 0, '', '', 0, 0, 4, 30, 4, 0, 30, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO ab_sites (id, title, siteType, numberOfTerminals, isEnabled, url, paxPerWeek, locationId, timezone, secondsFromGMT, defaultStateCode, defaultLanguageId, isUserAccountsEnabled, passwordChangeDays, isDesignaPinEnabled, alertEmails, barrierSystemServerId, facilityName, useDummyData, vatRate, thirdPartyAvailabilityCheckEnabled, additionalParkingAvailabilityFields, lengthOfStayType, allowConsolidatorEmailResend, defaultAffiliateId, productCodeMandatoryCheckEnabled, locationName, enableSendingFeedback, apiReserveBooking, apiTakePayment, apiPreventOverlappingBookings, airportCode, defaultLocationName, includeSoldOutInApi, preventDuplicateBookings, passwordReuseLimitAdmin, passwordChangeDaysCallCentre, passwordReuseLimitCallCentre, enableCountryCodesFlagInput, passwordChangeDaysAgency, passwordReuseLimitAgency, bucketCalculationMethod, apiEnableRequiredFields, includeProductCategoriesOnParkingAvailability, includePaymentMethodsInParkingAvailability, apiEnableIncludeGrace, apiSkipAvailabilityWithinWindow, validatePromotionOnAmendment, apiReserveTicketsWhenKeyIsValid, ccDefaultSearchDate, showAllWhiteLabelAffiliateBookings) VALUES(27, 'Shannon Airport', 'AIRPORT', 1, 1, 'http://www.shannonairport.ie/', 0, 19, 'Etc/GMT', 0, 'AL', 8, 1, 0, 0, '', 0, '', 0, 20.00, 0, 0, 1, 0, 1045, 0, 'Terminal', 0, 0, 0, 0, '', '', 0, 0, 4, 30, 4, 0, 30, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);

CREATE TABLE `ab_contacts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `registered` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` datetime NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `failedLogins` int NOT NULL DEFAULT '0',
  `emailAddress` varchar(255) NOT NULL,
  `profileImageUrl` varchar(450) DEFAULT NULL,
  `title` varchar(10) DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `telNo` varchar(50) DEFAULT NULL,
  `mobileNo` varchar(50) DEFAULT NULL,
  `faxNo` varchar(50) DEFAULT NULL,
  `occupation` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `abtaNo` varchar(100) DEFAULT NULL,
  `address1` varchar(255) NOT NULL DEFAULT '',
  `address2` varchar(255) DEFAULT NULL,
  `town` varchar(255) DEFAULT NULL,
  `county` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `postcode` varchar(50) DEFAULT NULL,
  `historicId` int DEFAULT NULL,
  `siteId` int DEFAULT '1',
  `activated` int DEFAULT NULL,
  `languageId` int DEFAULT '1',
  `tcsOptIn` int NOT NULL DEFAULT '0',
  `emailOptIn` int NOT NULL DEFAULT '0',
  `smsOptIn` int NOT NULL DEFAULT '0',
  `parkingOptIn` int NOT NULL DEFAULT '0',
  `marketingOptIn` datetime DEFAULT NULL,
  `transactionalOptOut` datetime DEFAULT NULL,
  `accountCreated` datetime DEFAULT NULL,
  `isPayAsYouGoActivated` int NOT NULL DEFAULT '0',
  `payAsYouGoActivatedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contacts_siteId_idx` (`siteId`),
  KEY `idx_contacts_siteId_email` (`siteId`,`emailAddress`),
  KEY `idx_contacts_emailaddress` (`emailAddress`),
  KEY `idx_ab_contacts_registered` (`registered`),
  CONSTRAINT `fk_contacts_siteId` FOREIGN KEY (`siteId`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO ab_contacts (id, registered, modified, password, failedLogins, emailAddress, profileImageUrl, title, firstName, lastName, telNo, mobileNo, faxNo, occupation, company, department, abtaNo, address1, address2, town, county, country, postcode, historicId, siteId, activated, languageId, tcsOptIn, emailOptIn, smsOptIn, parkingOptIn, marketingOptIn, transactionalOptOut, accountCreated, isPayAsYouGoActivated, payAsYouGoActivatedDate) VALUES(17514, '2016-06-01 14:31:05', '2016-06-21 14:28:56', NULL, 0, '', NULL, 'Mr', 'Noman', 'bbb', '00000000000', '', NULL, NULL, NULL, NULL, NULL, '1 Test Road', '', 'Stockport', '', NULL, 'SK1 1AA', NULL, 17, NULL, NULL, 0, 0, 0, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO ab_contacts (id, registered, modified, password, failedLogins, emailAddress, profileImageUrl, title, firstName, lastName, telNo, mobileNo, faxNo, occupation, company, department, abtaNo, address1, address2, town, county, country, postcode, historicId, siteId, activated, languageId, tcsOptIn, emailOptIn, smsOptIn, parkingOptIn, marketingOptIn, transactionalOptOut, accountCreated, isPayAsYouGoActivated, payAsYouGoActivatedDate) VALUES(17515, '2016-06-01 14:31:05', '2016-06-21 14:28:56', NULL, 0, '', NULL, 'Mr', 'Noman', 'bbb', '00000000000', '', NULL, NULL, NULL, NULL, NULL, '1 Test Road', '', 'Stockport', '', NULL, 'SK1 1AA', NULL, 17, NULL, NULL, 0, 0, 0, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO ab_contacts (id, registered, modified, password, failedLogins, emailAddress, profileImageUrl, title, firstName, lastName, telNo, mobileNo, faxNo, occupation, company, department, abtaNo, address1, address2, town, county, country, postcode, historicId, siteId, activated, languageId, tcsOptIn, emailOptIn, smsOptIn, parkingOptIn, marketingOptIn, transactionalOptOut, accountCreated, isPayAsYouGoActivated, payAsYouGoActivatedDate) VALUES(51023, '2016-06-01 14:31:05', '2016-06-21 14:28:56', NULL, 0, '', NULL, 'Mr', 'Noman', 'bbb', '00000000000', '', NULL, NULL, NULL, NULL, NULL, '1 Test Road', '', 'Stockport', '', NULL, 'SK1 1AA', NULL, 17, NULL, NULL, 0, 0, 0, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO ab_contacts (id, registered, modified, password, failedLogins, emailAddress, profileImageUrl, title, firstName, lastName, telNo, mobileNo, faxNo, occupation, company, department, abtaNo, address1, address2, town, county, country, postcode, historicId, siteId, activated, languageId, tcsOptIn, emailOptIn, smsOptIn, parkingOptIn, marketingOptIn, transactionalOptOut, accountCreated, isPayAsYouGoActivated, payAsYouGoActivatedDate) VALUES(51460, '2016-06-01 14:31:05', '2016-06-21 14:28:56', NULL, 0, '', NULL, 'Mr', 'zarrar', 'bbb', '00000000000', '', NULL, NULL, NULL, NULL, NULL, '1 Test Road', '', 'Stockport', '', NULL, 'SK1 1AA', NULL, 17, NULL, NULL, 0, 0, 0, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO ab_contacts (id, registered, modified, password, failedLogins, emailAddress, profileImageUrl, title, firstName, lastName, telNo, mobileNo, faxNo, occupation, company, department, abtaNo, address1, address2, town, county, country, postcode, historicId, siteId, activated, languageId, tcsOptIn, emailOptIn, smsOptIn, parkingOptIn, marketingOptIn, transactionalOptOut, accountCreated, isPayAsYouGoActivated, payAsYouGoActivatedDate) VALUES(51461, '2016-06-01 14:31:05', '2016-06-21 14:28:56', NULL, 0, 'derrick.feehi@aeroparker.com', NULL, 'Mr', 'Derrick', 'bbb', '00000000000', '', NULL, NULL, NULL, NULL, NULL, '1 Test Road', '', 'Stockport', '', NULL, 'SK1 1AA', NULL, 27, NULL, NULL, 0, 0, 0, 0, NULL, NULL, NULL, 0, NULL);



CREATE TABLE `ab_contacts_activation_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `contactId` int NOT NULL,
  `activationCode` varchar(50) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `emailOptIn` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `fk_contacts_contactId_idx` (`contactId`),
  CONSTRAINT `fk_contacts_contactId` FOREIGN KEY (`contactId`) REFERENCES `ab_contacts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO ab_contacts_activation_codes (id, contactId, activationCode, created, emailOptIn) VALUES(121, 17514, '4ac2c138ac224837ad77bdf537ed61e4', '2015-01-30 13:44:41', 0);
INSERT INTO ab_contacts_activation_codes (id, contactId, activationCode, created, emailOptIn) VALUES(122, 17515, '4ac2c138ac224837ad77bdf537ed61e4', '2015-01-30 13:44:41', 0);

CREATE TABLE `contact_hashed_password` (
  `contact_id` int NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  PRIMARY KEY (`contact_id`),
  UNIQUE KEY `id_UNIQUE` (`contact_id`),
  CONSTRAINT `fk_chp_contactid` FOREIGN KEY (`contact_id`) REFERENCES `ab_contacts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO contact_hashed_password (contact_id, hashed_password) VALUES(51460, '$2a$10$LIE1cFQvbX5UYcqxxuodH.tpcfWyYfS/joiB81Ggmp7jAEDLKL2aW');

CREATE TABLE `subscription_contact_membership` (
  `id` int NOT NULL AUTO_INCREMENT,
  `contact_id` int NOT NULL,
  `membership_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_subscription_contact_membership_contact_id` (`contact_id`) USING BTREE,
  KEY `idx_subscription_contact_membership_membership_id` (`membership_id`) USING BTREE,
  CONSTRAINT `fk_subscription_contact_membership_ab_contacts_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `ab_contacts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO subscription_contact_membership (id, contact_id, membership_id) VALUES(1, 51460, '1');