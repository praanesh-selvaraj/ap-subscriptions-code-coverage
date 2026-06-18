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

INSERT INTO currencies (id, code, symbol, htmlSymbol, isoCode, currencyNumber, conversionRate) VALUES(2, 'GBP', '&pound;', '&pound;', NULL, '826', 1.0000);

CREATE TABLE `languages` (
  `id` int NOT NULL,
  `languageName` varchar(100) NOT NULL,
  `languageCode` varchar(45) NOT NULL,
  `displayCode` varchar(45) NOT NULL,
  `displayName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO languages (id, languageName, languageCode, displayCode, displayName) VALUES(1, 'English (United Kingdom)', 'en-gb', 'en-gb', 'en');

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

INSERT INTO locations (id, name, decimalSymbol, numDigitsAfterDecimal, digitGroupSymbol, digitGrouping, negNumFormat, displayLeadingZero, currencySymbol, currencyPositiveFormat, currencyNegativeFormat, currencyDecimalPlaces, currencyGroupingSymbol, currencyDecimalSymbol, currencyHidePennies, dateFormat, longDateFormat, longDateTimeFormat, dateFormatSQL, dateFormatForReports, timeShort, timeLong, time24hr, currency, isoCode, calendarStartDay, countryCallingCode, sqlLocationCode, drinkingAge, defaultLanguageId) VALUES(14, 'United Kingdom', '.', 2, ',', '123,456,789', '-1.1', 1, '&pound;', '&pound;1.1', '-&pound;1.1', 2, ',', '.', 0, 'dd/MM/yyyy', 'EEEE, dd/MM/yyyy', '', '%d/%m/%Y', 'yyyy-MM-dd', 'hh:mm tt', 'hh:mm:ss tt', 1, 2, NULL, 1, '+44', 'en_GB', NULL, 1);

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

INSERT INTO ab_sites (id, title, siteType, numberOfTerminals, isEnabled, url, paxPerWeek, locationId, timezone, secondsFromGMT, defaultStateCode, defaultLanguageId, isUserAccountsEnabled, passwordChangeDays, isDesignaPinEnabled, alertEmails, barrierSystemServerId, facilityName, useDummyData, vatRate, thirdPartyAvailabilityCheckEnabled, additionalParkingAvailabilityFields, lengthOfStayType, allowConsolidatorEmailResend, defaultAffiliateId, productCodeMandatoryCheckEnabled, locationName, enableSendingFeedback, apiReserveBooking, apiTakePayment, apiPreventOverlappingBookings, airportCode, defaultLocationName, includeSoldOutInApi, preventDuplicateBookings, passwordReuseLimitAdmin, passwordChangeDaysCallCentre, passwordReuseLimitCallCentre, enableCountryCodesFlagInput, passwordChangeDaysAgency, passwordReuseLimitAgency, bucketCalculationMethod, apiEnableRequiredFields, includeProductCategoriesOnParkingAvailability, includePaymentMethodsInParkingAvailability, apiEnableIncludeGrace, apiSkipAvailabilityWithinWindow, validatePromotionOnAmendment, apiReserveTicketsWhenKeyIsValid, ccDefaultSearchDate, showAllWhiteLabelAffiliateBookings) VALUES(2, 'AeroParker', 'AIRPORT', 1, 1, 'www.aeroparker.com', 450000, 14, 'Etc/GMT', 0, '', 1, 1, 30, 0, '', 0, '', 0, 20.00, 0, 0, 2, 0, 361, 0, 'Terminal', 0, 0, 0, 0, '', '', 0, 0, 4, 30, 4, 0, 30, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);

CREATE TABLE `payment_step_fields` (
  `id` int NOT NULL,
  `label` varchar(100) NOT NULL DEFAULT '',
  `name` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
);

INSERT INTO payment_step_fields (id, label, name) VALUES(1, 'Title', 'title');
INSERT INTO payment_step_fields (id, label, name) VALUES(2, 'First Name', 'fname');
INSERT INTO payment_step_fields (id, label, name) VALUES(3, 'Last Name', 'lname');
INSERT INTO payment_step_fields (id, label, name) VALUES(4, 'Email', 'email');
INSERT INTO payment_step_fields (id, label, name) VALUES(5, 'Mobile Number', 'telno');
INSERT INTO payment_step_fields (id, label, name) VALUES(6, 'Postcode', 'postcode');
INSERT INTO payment_step_fields (id, label, name) VALUES(7, 'Address 1', 'addr1');
INSERT INTO payment_step_fields (id, label, name) VALUES(8, 'Address 2', 'addr2');
INSERT INTO payment_step_fields (id, label, name) VALUES(9, 'Town', 'town');
INSERT INTO payment_step_fields (id, label, name) VALUES(10, 'I would like a tax receipt', 'taxReceipt');
INSERT INTO payment_step_fields (id, label, name) VALUES(11, 'Company', 'company');
INSERT INTO payment_step_fields (id, label, name) VALUES(12, 'County', 'county');
INSERT INTO payment_step_fields (id, label, name) VALUES(13, 'Address 1 - Receipt', 'addr1Receipt');
INSERT INTO payment_step_fields (id, label, name) VALUES(14, 'Address 2 - Receipt', 'addr2Receipt');
INSERT INTO payment_step_fields (id, label, name) VALUES(15, 'Postcode - Receipt', 'postcodeReceipt');
INSERT INTO payment_step_fields (id, label, name) VALUES(16, 'Town - Receipt', 'townReceipt');
INSERT INTO payment_step_fields (id, label, name) VALUES(17, 'County - Receipt', 'countyReceipt');
INSERT INTO payment_step_fields (id, label, name) VALUES(18, 'Tax Identification Number', 'taxIdenNum');
INSERT INTO payment_step_fields (id, label, name) VALUES(19, 'Outgoing Flight No.', 'out');
INSERT INTO payment_step_fields (id, label, name) VALUES(20, 'Destination', 'destination');
INSERT INTO payment_step_fields (id, label, name) VALUES(21, 'Country', 'country');
INSERT INTO payment_step_fields (id, label, name) VALUES(22, 'Country - Receipt', 'countryReceipt');
INSERT INTO payment_step_fields (id, label, name) VALUES(23, 'Airline', 'airline');
INSERT INTO payment_step_fields (id, label, name) VALUES(24, 'Reason For Travel', 'reason');
INSERT INTO payment_step_fields (id, label, name) VALUES(25, 'Car Registration', 'carReg');
INSERT INTO payment_step_fields (id, label, name) VALUES(26, 'Car Make', 'make');
INSERT INTO payment_step_fields (id, label, name) VALUES(27, 'Car Model', 'model');
INSERT INTO payment_step_fields (id, label, name) VALUES(28, 'Car Colour', 'colour');
INSERT INTO payment_step_fields (id, label, name) VALUES(29, 'Outgoing Airline', 'outAirline');
INSERT INTO payment_step_fields (id, label, name) VALUES(30, 'Return Flight No.', 'retFlightNo');
INSERT INTO payment_step_fields (id, label, name) VALUES(31, 'Return Airline', 'retAirline');
INSERT INTO payment_step_fields (id, label, name) VALUES(32, 'Country', 'carCountryCode');
INSERT INTO payment_step_fields (id, label, name) VALUES(33, 'House Number Addition', 'houseNumAdd');
INSERT INTO payment_step_fields (id, label, name) VALUES(34, 'House Name/Number', 'houseNameNum');
INSERT INTO payment_step_fields (id, label, name) VALUES(35, 'Car State', 'carState');
INSERT INTO payment_step_fields (id, label, name) VALUES(36, 'Confirm Email', 'confirmEmail');
INSERT INTO payment_step_fields (id, label, name) VALUES(37, 'Mr', 'title_Mr');
INSERT INTO payment_step_fields (id, label, name) VALUES(38, 'Miss', 'title_Miss');
INSERT INTO payment_step_fields (id, label, name) VALUES(39, 'Mrs', 'title_Mrs');
INSERT INTO payment_step_fields (id, label, name) VALUES(40, 'Ms', 'title_Ms');
INSERT INTO payment_step_fields (id, label, name) VALUES(41, 'Dr', 'title_Dr');
INSERT INTO payment_step_fields (id, label, name) VALUES(42, 'Car Registration Year', 'carRegYear');
INSERT INTO payment_step_fields (id, label, name) VALUES(43, 'Car Pollution', 'carPol');
INSERT INTO payment_step_fields (id, label, name) VALUES(44, 'Country Codes', 'countryCodes');
INSERT INTO payment_step_fields (id, label, name) VALUES(45, 'Travellers', 'travellers');
INSERT INTO payment_step_fields (id, label, name) VALUES(46, 'Final Destination', 'finalDestination');
INSERT INTO payment_step_fields (id, label, name) VALUES(47, 'Company VAT Registration Number', 'companyVatRegistrationNumber');
INSERT INTO payment_step_fields (id, label, name) VALUES(48, 'Passenger First Name', 'passengerFirstName');
INSERT INTO payment_step_fields (id, label, name) VALUES(49, 'Passenger Last Name', 'passengerLastName');
INSERT INTO payment_step_fields (id, label, name) VALUES(50, 'Social Security Number', 'socialSecurityNumber');
INSERT INTO payment_step_fields (id, label, name) VALUES(51, 'Date Of Birth', 'dateOfBirth');
INSERT INTO payment_step_fields (id, label, name) VALUES(52, 'Purchase Membership For Someone Else', 'purchaseMembershipForSomeoneElse');
INSERT INTO payment_step_fields (id, label, name) VALUES(53, 'Referred By Friend', 'referredByFriend');

CREATE TABLE `payment_step_fields_subscriptions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL,
  `field_id` int NOT NULL,
  `mandatory` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_payment_step_fields_sub_ab_sites_site_id_idx` (`site_id`),
  KEY `fk_payment_step_fields_sub_payment_step_fields_field_id_idx` (`field_id`),
  CONSTRAINT `fk_payment_step_fields_sub_ab_sites_site_id` FOREIGN KEY (`site_id`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_payment_step_fields_sub_payment_step_fields_field_id` FOREIGN KEY (`field_id`) REFERENCES `payment_step_fields` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 2, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 3, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 8, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 9, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 12, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 17, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 18, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 19, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 24, 1);
INSERT INTO payment_step_fields_subscriptions (site_id, field_id, mandatory) VALUES(2, 25, 1);

CREATE TABLE `subscription_membership_sequence` (
  `id` int NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL,
  `value` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_subscription_membership_sequence_site_id` (`site_id`) USING BTREE,
  CONSTRAINT `fk_subscription_membership_sequence_ab_sites_site_id` FOREIGN KEY (`site_id`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO subscription_membership_sequence (site_id, value) VALUES(2, 13);