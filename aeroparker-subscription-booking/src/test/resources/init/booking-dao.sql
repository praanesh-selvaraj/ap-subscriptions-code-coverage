CREATE TABLE `languages`
(
    `id`           int          NOT NULL AUTO_INCREMENT,
    `languageName` varchar(100) NOT NULL,
    `languageCode` varchar(45)  NOT NULL,
    `displayCode`  varchar(45)  NOT NULL,
    `displayName`  varchar(45) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `currencies`
(
    `id`             int         NOT NULL AUTO_INCREMENT,
    `code`           varchar(3)  NOT NULL,
    `symbol`         varchar(10) NOT NULL,
    `htmlSymbol`     varchar(20) NOT NULL,
    `isoCode`        varchar(3)     DEFAULT NULL,
    `currencyNumber` varchar(3)     DEFAULT NULL,
    `conversionRate` decimal(19, 4) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `locations`
(
    `id`                     int         NOT NULL AUTO_INCREMENT,
    `name`                   varchar(50) NOT NULL,
    `decimalSymbol`          varchar(25) NOT NULL,
    `numDigitsAfterDecimal`  int         NOT NULL,
    `digitGroupSymbol`       varchar(25) NOT NULL,
    `digitGrouping`          varchar(25) NOT NULL,
    `negNumFormat`           varchar(25) NOT NULL,
    `displayLeadingZero`     int         NOT NULL,
    `currencySymbol`         varchar(25) NOT NULL DEFAULT '£',
    `currencyPositiveFormat` varchar(25) NOT NULL,
    `currencyNegativeFormat` varchar(25) NOT NULL,
    `currencyDecimalPlaces`  int         NOT NULL,
    `currencyGroupingSymbol` varchar(25) NOT NULL,
    `currencyDecimalSymbol`  varchar(25) NOT NULL,
    `currencyHidePennies`    int         NOT NULL,
    `dateFormat`             varchar(25) NOT NULL,
    `longDateFormat`         varchar(25) NOT NULL,
    `longDateTimeFormat`     varchar(45) NOT NULL DEFAULT '',
    `dateFormatSQL`          varchar(15) NOT NULL DEFAULT '%d/%m/%Y',
    `dateFormatForReports`   varchar(25) NOT NULL DEFAULT 'yyyy-MM-dd',
    `timeShort`              varchar(25) NOT NULL,
    `timeLong`               varchar(25) NOT NULL,
    `time24hr`               int         NOT NULL,
    `currency`               int         NOT NULL DEFAULT '1',
    `isoCode`                varchar(2)           DEFAULT NULL,
    `calendarStartDay`       tinyint(1)  NOT NULL DEFAULT '0',
    `countryCallingCode`     varchar(5)           DEFAULT NULL,
    `sqlLocationCode`        varchar(5)           DEFAULT 'en_GB',
    `drinkingAge`            int                  DEFAULT NULL,
    `defaultLanguageId`      int                  DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_locations_name` (`name`),
    KEY `fk_locations_currency_idx` (`currency`),
    KEY `fk_locations_languages_defaultLanguageId_idx` (`defaultLanguageId`),
    CONSTRAINT `fk_locations_currency` FOREIGN KEY (`currency`) REFERENCES `currencies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_locations_languages_defaultLanguageId` FOREIGN KEY (`defaultLanguageId`) REFERENCES `languages` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `ab_sites`
(
    `id`                                            int            NOT NULL AUTO_INCREMENT,
    `title`                                         varchar(255)   NOT NULL,
    `siteType`                                      varchar(10)    NOT NULL DEFAULT 'AIRPORT',
    `numberOfTerminals`                             int            NOT NULL DEFAULT '1',
    `isEnabled`                                     smallint       NOT NULL DEFAULT '0',
    `url`                                           varchar(255)            DEFAULT NULL,
    `paxPerWeek`                                    int            NOT NULL DEFAULT '0',
    `locationId`                                    int            NOT NULL,
    `timezone`                                      varchar(63)    NOT NULL DEFAULT 'Etc/GMT',
    `secondsFromGMT`                                int            NOT NULL DEFAULT '0',
    `defaultStateCode`                              varchar(45)             DEFAULT '',
    `defaultLanguageId`                             int                     DEFAULT NULL,
    `isUserAccountsEnabled`                         int            NOT NULL,
    `passwordChangeDays`                            smallint       NOT NULL DEFAULT '30',
    `isDesignaPinEnabled`                           int            NOT NULL DEFAULT '0',
    `alertEmails`                                   varchar(60)             DEFAULT NULL,
    `barrierSystemServerId`                         int                     DEFAULT '0',
    `facilityName`                                  varchar(255)            DEFAULT NULL,
    `useDummyData`                                  int            NOT NULL DEFAULT '0',
    `vatRate`                                       decimal(13, 2) NOT NULL DEFAULT '20.00',
    `thirdPartyAvailabilityCheckEnabled`            int            NOT NULL DEFAULT '0',
    `additionalParkingAvailabilityFields`           bit(1)                  DEFAULT b'0',
    `lengthOfStayType`                              smallint       NOT NULL DEFAULT '1',
    `allowConsolidatorEmailResend`                  int            NOT NULL DEFAULT '0',
    `defaultAffiliateId`                            int                     DEFAULT NULL,
    `productCodeMandatoryCheckEnabled`              bit(1)         NOT NULL DEFAULT b'0',
    `locationName`                                  varchar(60)    NOT NULL DEFAULT 'Terminal',
    `enableSendingFeedback`                         bit(1)         NOT NULL DEFAULT b'0',
    `apiReserveBooking`                             bit(1)         NOT NULL DEFAULT b'0',
    `apiTakePayment`                                bit(1)         NOT NULL DEFAULT b'0',
    `apiPreventOverlappingBookings`                 bit(1)         NOT NULL DEFAULT b'0',
    `airportCode`                                   varchar(3)     NOT NULL DEFAULT '',
    `defaultLocationName`                           varchar(255)            DEFAULT '',
    `includeSoldOutInApi`                           bit(1)         NOT NULL DEFAULT b'0',
    `preventDuplicateBookings`                      bit(1)         NOT NULL DEFAULT b'0',
    `passwordReuseLimitAdmin`                       smallint       NOT NULL DEFAULT '4',
    `passwordChangeDaysCallCentre`                  smallint       NOT NULL DEFAULT '30',
    `passwordReuseLimitCallCentre`                  smallint       NOT NULL DEFAULT '4',
    `enableCountryCodesFlagInput`                   bit(1)                  DEFAULT b'0',
    `passwordChangeDaysAgency`                      smallint       NOT NULL DEFAULT '30',
    `passwordReuseLimitAgency`                      smallint       NOT NULL DEFAULT '4',
    `bucketCalculationMethod`                       smallint       NOT NULL DEFAULT '1',
    `apiEnableRequiredFields`                       bit(1)         NOT NULL DEFAULT b'0',
    `includeProductCategoriesOnParkingAvailability` bit(1)         NOT NULL DEFAULT b'0',
    `includePaymentMethodsInParkingAvailability`    bit(1)         NOT NULL DEFAULT b'0',
    `apiEnableIncludeGrace`                         bit(1)         NOT NULL DEFAULT b'0',
    `apiSkipAvailabilityWithinWindow`               bit(1)         NOT NULL DEFAULT b'0',
    `validatePromotionOnAmendment`                  bit(1)         NOT NULL DEFAULT b'0',
    `apiReserveTicketsWhenKeyIsValid`               bit(1)         NOT NULL DEFAULT b'0',
    `ccDefaultSearchDate`                           smallint       NOT NULL DEFAULT '0',
    `showAllWhiteLabelAffiliateBookings`            bit(1)         NOT NULL DEFAULT b'0',
    `apiSendMoreInfoAsPlainText`                    bit(1)         NOT NULL DEFAULT b'0',
    `apiReturnRollupPrice`                          bit(1)         NOT NULL DEFAULT b'0',
    `incrementMembershipIdPerBooking`               bit(1)         NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    KEY `fk_sites_locationId_idx` (`locationId`),
    KEY `fk_sites_defaultLanguageId_idx` (`defaultLanguageId`),
    CONSTRAINT `fk_sites_defaultLanguageId` FOREIGN KEY (`defaultLanguageId`) REFERENCES `languages` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_sites_locationId` FOREIGN KEY (`locationId`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `ab_affiliates`
(
    `id`                                      int            NOT NULL AUTO_INCREMENT,
    `code`                                    varchar(30)    NOT NULL,
    `name`                                    varchar(255)   NOT NULL,
    `companyName`                             varchar(255)   NOT NULL,
    `type`                                    int            NOT NULL DEFAULT '0',
    `defaultPromoCode`                        varchar(255)   NOT NULL,
    `siteid`                                  int            NOT NULL,
    `isEnabled`                               int            NOT NULL DEFAULT '0',
    `bookingReferenceFormat`                  varchar(30)    NOT NULL DEFAULT '{S}{R}{P}{I}',
    `bookingReferencePrefix`                  varchar(5)     NOT NULL DEFAULT 'W',
    `bookingSequenceName`                     varchar(80)    NOT NULL DEFAULT 'bookings',
    `paymentPropertiesNode`                   varchar(50)             DEFAULT NULL,
    `bookingFee`                              decimal(13, 2) NOT NULL DEFAULT '0.00',
    `bookingFeeType`                          smallint       NOT NULL DEFAULT '1',
    `refundBookingFeeOnCancellation`          int            NOT NULL DEFAULT '0',
    `debitCardFee`                            decimal(13, 2) NOT NULL DEFAULT '0.00',
    `debitCardFeeType`                        smallint       NOT NULL DEFAULT '0',
    `creditCardFee`                           decimal(13, 2) NOT NULL DEFAULT '0.00',
    `creditCardFeeType`                       smallint       NOT NULL DEFAULT '0',
    `paypalFee`                               decimal(13, 2) NOT NULL DEFAULT '0.00',
    `paypalUsePercent`                        int            NOT NULL DEFAULT '0',
    `refundCardFeeOnCancellation`             int            NOT NULL DEFAULT '0',
    `cancellationCharge`                      decimal(13, 2) NOT NULL DEFAULT '0.00',
    `cancellationChargeType`                  smallint       NOT NULL DEFAULT '1',
    `amendCancel_minHoursBeforeDateOfArrival` int            NOT NULL DEFAULT '0',
    `amendCancel_ignoreTimeOfDay`             smallint       NOT NULL DEFAULT '0',
    `bookingDateType`                         smallint       NOT NULL DEFAULT '1',
    `vatRate`                                 decimal(13, 2) NOT NULL,
    `stateTaxRate`                            decimal(13, 2) NOT NULL DEFAULT '0.00',
    `cityTax`                                 decimal(13, 2)          DEFAULT '0.00',
    `vatNo`                                   varchar(50)    NOT NULL,
    `vatAddress`                              varchar(255)   NOT NULL,
    `vatCompany`                              varchar(255)   NOT NULL,
    `smsAssistancePrice`                      decimal(13, 2) NOT NULL DEFAULT '-1.00',
    `crmType`                                 int            NOT NULL DEFAULT '0',
    `enable3DSecure`                          smallint       NOT NULL DEFAULT '0',
    `secure3DPurchaseDesc`                    varchar(80)             DEFAULT NULL,
    `secure3DWebsiteUrl`                      varchar(255)            DEFAULT NULL,
    `callCentreNumber`                        varchar(50)    NOT NULL,
    `isUserAccountsEnabled`                   int            NOT NULL DEFAULT '0',
    `isSaveForLaterEnabled`                   int            NOT NULL DEFAULT '0',
    `isSaveForLaterReminderEnabled`           int            NOT NULL DEFAULT '0',
    `isSocialLoginEnabled`                    int            NOT NULL DEFAULT '0',
    `isDataCashEnabled`                       int            NOT NULL DEFAULT '0',
    `isPaypalEnabled`                         int            NOT NULL DEFAULT '0',
    `isNetaxeptEnabled`                       int            NOT NULL DEFAULT '0',
    `isMainAffiliate`                         int            NOT NULL DEFAULT '0',
    `cardPaymentsEnabled`                     int            NOT NULL DEFAULT '0',
    `payPalEnabled`                           int            NOT NULL DEFAULT '0',
    `isArchived`                              tinyint(1)     NOT NULL DEFAULT '0',
    `isReferAFriendEnabled`                   int            NOT NULL DEFAULT '0',
    `isStripePaymentEnabled`                  int                     DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `code_UNIQUE` (`code`),
    KEY `siteid` (`siteid`),
    KEY `fk_affils_bookingSequenceName_idx` (`bookingSequenceName`),
    KEY `idx_affils_code` (`code`),
    CONSTRAINT `fk_affils_siteId` FOREIGN KEY (`siteid`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `ab_contacts`
(
    `id`                      int          NOT NULL AUTO_INCREMENT,
    `registered`              timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified`                datetime     NOT NULL,
    `password`                varchar(100)          DEFAULT NULL,
    `failedLogins`            int          NOT NULL DEFAULT '0',
    `emailAddress`            varchar(255) NOT NULL,
    `profileImageUrl`         varchar(450)          DEFAULT NULL,
    `title`                   varchar(10)           DEFAULT NULL,
    `firstName`               varchar(255)          DEFAULT NULL,
    `lastName`                varchar(255)          DEFAULT NULL,
    `telNo`                   varchar(50)           DEFAULT NULL,
    `mobileNo`                varchar(50)           DEFAULT NULL,
    `faxNo`                   varchar(50)           DEFAULT NULL,
    `occupation`              varchar(255)          DEFAULT NULL,
    `company`                 varchar(255)          DEFAULT NULL,
    `department`              varchar(255)          DEFAULT NULL,
    `abtaNo`                  varchar(100)          DEFAULT NULL,
    `address1`                varchar(255) NOT NULL DEFAULT '',
    `address2`                varchar(255)          DEFAULT NULL,
    `town`                    varchar(255)          DEFAULT NULL,
    `county`                  varchar(255)          DEFAULT NULL,
    `country`                 varchar(255)          DEFAULT NULL,
    `postcode`                varchar(50)           DEFAULT NULL,
    `historicId`              int                   DEFAULT NULL,
    `siteId`                  int                   DEFAULT '1',
    `activated`               int                   DEFAULT NULL,
    `languageId`              int                   DEFAULT '1',
    `tcsOptIn`                int          NOT NULL DEFAULT '0',
    `emailOptIn`              int          NOT NULL DEFAULT '0',
    `smsOptIn`                int          NOT NULL DEFAULT '0',
    `parkingOptIn`            int          NOT NULL DEFAULT '0',
    `marketingOptIn`          datetime              DEFAULT NULL,
    `transactionalOptOut`     datetime              DEFAULT NULL,
    `accountCreated`          datetime              DEFAULT NULL,
    `isPayAsYouGoActivated`   int          NOT NULL DEFAULT '0',
    `payAsYouGoActivatedDate` datetime              DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_contacts_siteId_idx` (`siteId`),
    KEY `idx_contacts_siteId_email` (`siteId`, `emailAddress`),
    KEY `idx_contacts_emailaddress` (`emailAddress`),
    KEY `idx_ab_contacts_registered` (`registered`),
    CONSTRAINT `fk_contacts_siteId` FOREIGN KEY (`siteId`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking`
(
    `id`             int            NOT NULL AUTO_INCREMENT,
    `reference`      varchar(50)    NOT NULL,
    `created`        datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `affiliate_id`   int            NOT NULL,
    `contact_id`     int            NOT NULL,
    `grand_total`    decimal(13, 2) NOT NULL DEFAULT '0.00',
    `vat_amount`     decimal(13, 2) NOT NULL DEFAULT '0.00',
    `amended_date`   datetime                DEFAULT NULL,
    `cancelled`      bit(1)         NOT NULL DEFAULT b'0',
    `cancelled_date` datetime                DEFAULT NULL,
    `booking_fee`    decimal(13, 2) NOT NULL DEFAULT '0.00',
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_ab_affiliates_affiliate_id` (`affiliate_id`),
    KEY `idx_subscription_booking_ab_contacts_contact_id` (`contact_id`),
    KEY `idx_subscription_booking_reference` (`reference`),
    CONSTRAINT `fk_subscription_booking_ab_affiliates_affiliate_id` FOREIGN KEY (`affiliate_id`) REFERENCES `ab_affiliates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subscription_booking_ab_contacts_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `ab_contacts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_reservation_data`
(
    `id`                              int          NOT NULL AUTO_INCREMENT,
    `guid`                            varchar(100) NOT NULL,
    `title`                           varchar(4)   NOT NULL DEFAULT '',
    `phone_number`                    varchar(45)  NOT NULL DEFAULT '',
    `address1`                        varchar(200) NOT NULL DEFAULT '',
    `address2`                        varchar(200) NOT NULL DEFAULT '',
    `town`                            varchar(128) NOT NULL DEFAULT '',
    `county`                          varchar(128) NOT NULL DEFAULT '',
    `country`                         varchar(128) NOT NULL DEFAULT '',
    `postcode`                        varchar(16)  NOT NULL DEFAULT '',
    `first_name`                      varchar(255) NOT NULL DEFAULT '',
    `last_name`                       varchar(255) NOT NULL DEFAULT '',
    `email_address`                   varchar(200) NOT NULL DEFAULT '',
    `company_name`                    varchar(255) NOT NULL DEFAULT '',
    `tax_identification_number`       varchar(255) NOT NULL DEFAULT '',
    `tax_receipt`                     varchar(255) NOT NULL DEFAULT '',
    `company_vat_registration_number` varchar(255) NOT NULL DEFAULT '',
    `country_receipt`                 varchar(255) NOT NULL DEFAULT '',
    `county_receipt`                  varchar(255) NOT NULL DEFAULT '',
    `town_receipt`                    varchar(255) NOT NULL DEFAULT '',
    `postcode_receipt`                varchar(255) NOT NULL DEFAULT '',
    `address_2_receipt`               varchar(255) NOT NULL DEFAULT '',
    `address_1_receipt`               varchar(255) NOT NULL DEFAULT '',
    `car_registration`                varchar(50)  NOT NULL DEFAULT '',
    `car_make`                        varchar(45)  NOT NULL DEFAULT '',
    `car_model`                       varchar(45)  NOT NULL DEFAULT '',
    `car_country`                     varchar(45)  NOT NULL DEFAULT '',
    `car_colour`                      varchar(45)  NOT NULL DEFAULT '',
    `current_language_id`             int                   DEFAULT NULL,
    `invoice_requested`               bit(1)       NOT NULL DEFAULT b'0',
    `country_code_receipt`            char(2)      NOT NULL DEFAULT '',
    `referrer_membership_id`          varchar(255)          DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_reservation_data_guid` (`guid`) USING BTREE,
    KEY `fk_sub_booking_reservation_data_languages_current_langauge_id` (`current_language_id`),
    CONSTRAINT `fk_sub_booking_reservation_data_languages_current_langauge_id` FOREIGN KEY (`current_language_id`) REFERENCES `languages` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_guid`
(
    `id`   int                            NOT NULL AUTO_INCREMENT,
    `guid` char(36) CHARACTER SET utf8mb3 NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_purchase_data`
(
    `id`            int      NOT NULL AUTO_INCREMENT,
    `customer_guid` char(36) NOT NULL,
    `purchase_data` longtext NOT NULL,
    `affiliate_id`  int      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_subscription_purchase_data_customer_guid` (`customer_guid`),
    KEY `fk_subscription_purchase_data_affiliate_id_ab_affiliates_id_idx` (`affiliate_id`),
    CONSTRAINT `fk_subscription_purchase_data_affiliate_id_ab_affiliates_id` FOREIGN KEY (`affiliate_id`) REFERENCES `ab_affiliates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_guid_booking`
(
    `id`                            int NOT NULL AUTO_INCREMENT,
    `subscription_guid_id`          int NOT NULL,
    `subscription_booking_id`       int NOT NULL,
    `subscription_purchase_data_id` int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_subs_guid_booking_subs_guid_subscription_guid_id` (`subscription_guid_id`),
    KEY `idx_subs_guid_booking_subs_booking_subscription_booking_id` (`subscription_booking_id`),
    KEY `fk_subs_guid_booking_subs_guid_subs_purchase_data_id_idx` (`subscription_purchase_data_id`),
    CONSTRAINT `fk_subs_guid_booking_subs_booking_subscription_booking_id` FOREIGN KEY (`subscription_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subs_guid_booking_subs_guid_subs_purchase_data_id` FOREIGN KEY (`subscription_purchase_data_id`) REFERENCES `subscription_purchase_data` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subs_guid_booking_subs_guid_subscription_guid_id` FOREIGN KEY (`subscription_guid_id`) REFERENCES `subscription_guid` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_customer_details`
(
    `id`             int          NOT NULL AUTO_INCREMENT,
    `created`        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `sub_booking_id` int          NOT NULL,
    `title`          varchar(4)            DEFAULT NULL,
    `first_name`     varchar(255) NOT NULL,
    `last_name`      varchar(255) NOT NULL,
    `email_address`  varchar(200)          DEFAULT NULL,
    `phone_number`   varchar(45)           DEFAULT NULL,
    `house_number`   varchar(45)           DEFAULT NULL,
    `address1`       varchar(200)          DEFAULT NULL,
    `address2`       varchar(200)          DEFAULT NULL,
    `town`           varchar(128)          DEFAULT NULL,
    `county`         varchar(128)          DEFAULT NULL,
    `country`        varchar(128)          DEFAULT NULL,
    `postcode`       varchar(16)           DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_customer_details_sub_booking_id` (`sub_booking_id`),
    KEY `idx_subscription_booking_customer_details_email_address` (`email_address`),
    CONSTRAINT `idx_sub_booking_cust_details_sub_booking_sub_booking_id` FOREIGN KEY (`sub_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `ab_carparks`
(
    `id`                      int            NOT NULL AUTO_INCREMENT,
    `name`                    varchar(255)   NOT NULL,
    `terminal`                int            NOT NULL DEFAULT '-1',
    `distance`                decimal(13, 2) NOT NULL DEFAULT '0.00',
    `barriertype`             int                     DEFAULT NULL,
    `barriercode`             varchar(50)             DEFAULT NULL,
    `siteid`                  int            NOT NULL,
    `isEnabled`               smallint       NOT NULL DEFAULT '1',
    `latitude`                varchar(45)             DEFAULT NULL,
    `longitude`               varchar(45)             DEFAULT NULL,
    `isCarParkPaymentEnabled` smallint       NOT NULL DEFAULT '0',
    `displayCarParkPSF`       smallint       NOT NULL DEFAULT '0',
    `useActualExit`           smallint       NOT NULL DEFAULT '0',
    `carParkCode`             varchar(255)   NOT NULL DEFAULT '',
    `isValetParking`          bit(1)         NOT NULL DEFAULT b'0',
    `mapPoi`                  varchar(1000)  NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    KEY `fk_siteid` (`siteid`),
    CONSTRAINT `fk_carprk_siteId` FOREIGN KEY (`siteid`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_product`
(
    `id`                          int          NOT NULL AUTO_INCREMENT,
    `name`                        varchar(255) NOT NULL,
    `site_id`                     int          NOT NULL,
    `car_park_id`                 int                   DEFAULT NULL,
    `enabled`                     bit(1)       NOT NULL DEFAULT b'0',
    `all_affiliates_checked`      bit(1)                DEFAULT NULL,
    `multiple_car_parks_selected` bit(1)                DEFAULT b'0',
    `all_car_parks_selected`      bit(1)                DEFAULT b'0',
    `hide_car_park`               bit(1)       NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    KEY `idx_subscription_product_car_park_id` (`car_park_id`),
    KEY `fk_subscription_product_ab_sites_id_idx` (`site_id`),
    CONSTRAINT `fk_subscription_product_ab_carparks_id` FOREIGN KEY (`car_park_id`) REFERENCES `ab_carparks` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subscription_product_ab_sites_id` FOREIGN KEY (`site_id`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_item`
(
    `id`                   int            NOT NULL AUTO_INCREMENT,
    `sub_booking_id`       int            NOT NULL,
    `product_id`           int            NOT NULL,
    `car_park_id`          int            NOT NULL,
    `car_park_name`        varchar(45)    NOT NULL,
    `product_display_name` varchar(45)    NOT NULL,
    `created`              datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `period_type`          varchar(45)    NOT NULL,
    `vat_rate`             decimal(13, 2) NOT NULL DEFAULT '0.00',
    `sub_total`            decimal(13, 2) NOT NULL DEFAULT '0.00',
    `vat_amount`           decimal(13, 2) NOT NULL DEFAULT '0.00',
    `booking_fee`          decimal(13, 2) NOT NULL DEFAULT '0.00',
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_item_sub_booking_sub_booking_id` (`sub_booking_id`),
    KEY `idx_subscription_booking_item_sub_booking_product_id` (`product_id`),
    KEY `idx_subscription_booking_item_ab_carparks_car_park_id` (`car_park_id`),
    CONSTRAINT `fk_subscription_booking_item_ab_carparks_car_park_id` FOREIGN KEY (`car_park_id`) REFERENCES `ab_carparks` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subscription_booking_item_sub_booking_sub_booking_id` FOREIGN KEY (`sub_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subscription_booking_item_sub_product_product_id` FOREIGN KEY (`product_id`) REFERENCES `subscription_product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_season_ticket`
(
    `id`           int            NOT NULL AUTO_INCREMENT,
    `start_date`   date           NOT NULL,
    `end_date`     date                    DEFAULT NULL,
    `item_id`      int            NOT NULL,
    `period_term`  varchar(45)             DEFAULT NULL,
    `minimum_term` varchar(45)             DEFAULT NULL,
    `price`        decimal(13, 2) NOT NULL DEFAULT '0.00',
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_season_ticket_sub_booking_item_item` (`item_id`),
    CONSTRAINT `fk_subscription_booking_season_ticket_sub_booking_item_item_id` FOREIGN KEY (`item_id`) REFERENCES `subscription_booking_item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_recurring_ticket`
(
    `id`                int            NOT NULL AUTO_INCREMENT,
    `start_date`        date           NOT NULL,
    `minimum_term_date` date                    DEFAULT NULL,
    `item_id`           int            NOT NULL,
    `minimum_term`      varchar(45)             DEFAULT NULL,
    `price`             decimal(13, 2) NOT NULL DEFAULT '0.00',
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_recurring_ticket_sub_booking_item_item` (`item_id`),
    CONSTRAINT `fk_subscription_booking_rec_ticket_sub_booking_item_item_id` FOREIGN KEY (`item_id`) REFERENCES `subscription_booking_item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `site_subscription_recurring_payment`
(
    `id`      int NOT NULL AUTO_INCREMENT,
    `site_id` int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_site_subscription_recurring_payment_ab_sites_site_id_idx` (`site_id`),
    CONSTRAINT `fk_site_subscription_recurring_payment_ab_sites_site_id` FOREIGN KEY (`site_id`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `payment_gateway_types`
(
    `id`                     int          NOT NULL AUTO_INCREMENT,
    `type`                   varchar(255) NOT NULL,
    `adminJsp`               longtext     NOT NULL,
    `paymentServlet`         longtext     NOT NULL,
    `paymentJsp`             longtext     NOT NULL,
    `paymentInformationJsp`  longtext     NOT NULL,
    `refundInformationJsp`   longtext     NOT NULL,
    `urlBuilderClass`        longtext     NOT NULL,
    `urlParametersClass`     longtext     NOT NULL,
    `refundProcessorClass`   longtext     NOT NULL,
    `refundJsp`              longtext     NOT NULL,
    `addSavedCardJsp`        varchar(255) NOT NULL,
    `apiPaymentServlet`      longtext,
    `savedCardServlet`       varchar(255) DEFAULT NULL,
    `subscriptionPaymentJsp` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (1, 'Braintree', 'EditBraintreeGateway.jsp', 'BraintreePaymentHandler', 'BraintreePayment.jsp',
        'BraintreePaymentInformation.jsp', 'BraintreeRefundInformation.jsp', 'BraintreeURLBuilder',
        'BraintreeURLParameters', 'BraintreeRefundProcessor', 'BraintreeRefund.jsp', '', NULL,
        'BraintreeSavedCardHandler', NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (2, 'PayPal', 'EditPayPalGateway.jsp', 'PayPalPaymentHandler', 'PayPalPayment.jsp', '', '', 'PayPalURLBuilder',
        'PayPalURLParameters', 'PayPalRefundProcessor', '', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (3, 'Sagepay', 'EditSagepayGateway.jsp', 'SagepayPaymentHandler', 'SagepayPayment.jsp',
        'SagepayPaymentInformation.jsp', 'SagepayRefundInformation.jsp', 'SagepayURLBuilder', 'SagepayURLParameters',
        'SagepayRefundProcessor', 'SagepayRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (4, 'Evo', 'EditEvoGateway.jsp', 'EvoPaymentHandler', 'EvoPayment.jsp', 'EvoPaymentInformation.jsp',
        'EvoRefundInformation.jsp', 'EvoURLBuilder', 'EvoURLParameters', 'EvoRefundProcessor', 'EvoRefund.jsp', '',
        NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (5, 'DataCash', 'EditDataCashGateway.jsp', 'DataCashPaymentHandler', 'DataCashPayment.jsp',
        'DataCashPaymentInformation.jsp', 'DataCashRefundInformation.jsp', 'DataCashURLBuilder',
        'DataCashURLParameters', 'DataCashRefundProcessor', 'DataCashRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (6, 'EService', 'EditEServiceGateway.jsp', 'EServicePaymentHandler', 'EServicePayment.jsp',
        'EServicePaymentInformation.jsp', 'EserviceRefundInformation.jsp', 'EServiceURLBuilder',
        'EServiceURLParameters', 'EServiceRefundProcessor', 'EServiceRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (7, 'Wirecard', 'EditWirecardGateway.jsp', 'WirecardPaymentHandler', 'WirecardPayment.jsp',
        'WirecardInformation.jsp', 'WirecardRefundInformation.jsp', 'WirecardURLBuilder', 'WirecardURLParameters',
        'WirecardRefundProcessor', 'WirecardRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (8, 'Borgun', 'EditBorgunGateway.jsp', 'BorgunPaymentHandler', 'BorgunPayment.jsp', 'BorgunInformation.jsp',
        'BorgunRefundInformation.jsp', 'BorgunURLBuilder', 'BorgunURLParameters', 'BorgunRefundProcessor',
        'BorgunRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (9, 'PayPalPro', 'EditPayPalProGateway.jsp', 'PayPalProPaymentHandler', 'PayPalProPayment.jsp',
        'PayPalProInformation.jsp', 'PayPalProRefundInformation.jsp', 'PayPalProURLBuilder', 'PayPalProURLParameters',
        'PayPalProRefundProcessor', 'PayPalProRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (10, 'Ogone', 'EditOgoneGateway.jsp', 'OgonePaymentHandler', 'OgonePayment.jsp', 'OgoneInformation.jsp',
        'OgoneRefundInformation.jsp', 'OgoneURLBuilder', 'OgoneURLParameters', 'OgoneRefundProcessor',
        'OgoneRefund.jsp', 'OgoneAddSaveCard.jsp', 'OgoneAPIPayments', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (11, 'Przelewy24', 'EditPrzelewy24Gateway.jsp', 'Przelewy24PaymentHandler', 'Przelewy24Payment.jsp',
        'Przelewy24Information.jsp', 'Przelewy24RefundInformation.jsp', 'Przelewy24URLBuilder',
        'Przelewy24URLParameters', 'Przelewy24RefundProcessor', 'Przelewy24Refund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (12, 'Advam', 'EditAdvamGateway.jsp', 'AdvamPaymentHandler', 'AdvamPayment.jsp', 'AdvamPaymentInformation.jsp',
        'AdvamRefundInformation.jsp', 'AdvamURLBuilder', 'AdvamURLParameters', 'AdvamRefundProcessor',
        'AdvamRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (13, 'ThreeC', 'Edit3cGateway.jsp', 'ThreeCPaymentHandler', '3cPayment.jsp', '3cPaymentInformation.jsp',
        '3cRefundInformation.jsp', 'ThreeCURLBuilder', 'ThreeCURLParameters', 'ThreeCRefundProcessor', '3cRefund.jsp',
        '', 'ThreeCAPIPayments', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (14, 'Cybersource', 'EditCybersourceGateway.jsp', 'CybersourcePaymentHandler', 'CybersourcePayment.jsp',
        'CybersourcePaymentInformation.jsp', 'CybersourceRefundInformation.jsp', 'CybersourceURLBuilder',
        'CybersourceURLParameters', 'CybersourceRefundProcessor', 'CybersourceRefund.jsp',
        'FlexMicroformAddSaveCard.jsp', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (15, 'EMS', 'EditEMSGateway.jsp', 'EMSPaymentHandler', 'EMSPayment.jsp', 'EMSPaymentInformation.jsp',
        'EMSRefundInformation.jsp', 'EMSURLBuilder', 'EMSURLParameters', 'EMSRefundProcessor', 'EMSRefund.jsp', ' ',
        'EMSAPIPayments', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (16, 'WirecardV2', 'EditWirecardV2Gateway.jsp', 'WirecardV2PaymentHandler', 'WirecardV2Payment.jsp',
        'WirecardV2Information.jsp', 'WirecardV2RefundInformation.jsp', 'WirecardV2URLBuilder',
        'WirecardV2URLParameters', 'WirecardV2RefundProcessor', 'WirecardV2Refund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (17, 'Heartland', 'EditHeartlandGateway.jsp', 'HeartlandPaymentHandler', 'HeartlandPayment.jsp',
        'HeartlandInformation.jsp', 'HeartlandRefundInformation.jsp', 'HeartlandURLBuilder', 'HeartlandURLParameters',
        'HeartlandRefundProcessor', 'HeartlandRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (18, 'Payeezy', 'EditPayeezyGateway.jsp', 'PayeezyPaymentHandler', 'PayeezyPayment.jsp',
        'PayeezyPaymentInformation.jsp', 'PayeezyRefundInformation.jsp', 'PayeezyURLBuilder', 'PayeezyURLParameters',
        'PayeezyRefundProcessor', 'PayeezyRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (19, 'Concardis', 'EditConcardisGateway.jsp', 'ConcardisPaymentHandler', 'ConcardisPayment.jsp',
        'ConcardisPaymentInformation.jsp', 'ConcardisRefundInformation.jsp', 'ConcardisURLBuilder',
        'ConcardisUrlParameters', 'ConcardisRefundProcessor', 'ConcardisRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (20, 'Payone', 'EditPayoneGateway.jsp', 'PayonePaymentHandler', 'PayonePayment.jsp',
        'PayonePaymentInformation.jsp', 'PayoneRefundInformation.jsp', 'PayoneURLBuilder', 'PayoneUrlParameters',
        'PayoneRefundProcessor', 'PayoneRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (21, 'Flex Microform', 'EditFlexMicroformGateway.jsp', 'FlexMicroformPaymentHandler', 'FlexMicroformPayment.jsp',
        'FlexMicroformInformation.jsp', 'FlexMicroformRefundInformation.jsp', 'FlexMicroformURLBuilder',
        'FlexMicroformURLParameters', 'FlexMicroformRefundProcessor', 'FlexMicroformRefund.jsp', '''''', NULL, NULL,
        NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (22, 'Klix', 'EditKlixGateway.jsp', 'KlixPaymentHandler', 'KlixPayment.jsp', 'KlixInformation.jsp',
        'KlixRefundInformation.jsp', 'KlixURLBuilder', 'KlixURLParameters', 'KlixRefundProcessor', 'KlixRefund.jsp', '',
        NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (23, 'Windcave', 'EditWindcaveGateway.jsp', 'WindcavePaymentHandler', 'WindcavePayment.jsp',
        'WindcaveInformation.jsp', 'WindcaveRefundInformation.jsp', 'WirecardURLBuilder', 'WirecardURLParameters',
        'WindcaveRefundProcessor', 'WindcaveRefund.jsp', '', 'WindcaveAPIPaymentHandler', 'WindcaveSavedCardHandler',
        NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (24, 'Nets', 'EditNetsGateway.jsp', 'NetsPaymentHandler', 'NetsPayment.jsp', 'NetsInformation.jsp',
        'NetsRefundInformation.jsp', 'NetsURLBuilder', 'NetsURLParameters', 'NetsRefundProcessor', 'NetsRefund.jsp', '',
        'NetsAPIPayments', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (25, 'CybersourceV2', 'EditCybersourceV2Gateway.jsp', 'CybersourceV2PaymentHandler', 'CybersourcePayment.jsp',
        'CybersourcePaymentInformation.jsp', 'CybersourceRefundInformation.jsp', 'CybersourceV2URLBuilder',
        'CybersourceV2URLParameters', 'CybersourceV2RefundProcessor', 'CybersourceRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (26, 'NMI', 'EditNMIGateway.jsp', 'NMIPaymentHandler', 'NMIPayment.jsp', 'NMIInformation.jsp',
        'NMIRefundInformation.jsp', 'NMIUrlBuilder', 'NMIUrlParameters', 'NMIRefundProcessor', 'NMIRefund.jsp', '',
        NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (27, 'Advam Hosted Fields', 'EditAdvamHostedFieldsGateway.jsp', 'AdvamHostedFieldsPaymentHandler',
        'AdvamHostedFieldsPayment.jsp', 'AdvamHostedFieldsInformation.jsp', 'AdvamHostedFieldsRefundInformation.jsp',
        'AdvamHostedFieldsUrlBuilder', 'AdvamHostedFieldsUrlParameters', 'AdvamHostedFieldsRefundProcessor',
        'AdvamHostedFieldsRefund.jsp', '', 'AdvamHostedFieldsAPIPaymentHandler', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (28, 'Stripe', 'EditStripeGateway.jsp', 'StripePaymentHandler', 'StripePayment.jsp', 'StripeInformation.jsp',
        'StripeRefundInformation.jsp', 'StripeUrlBuilder', 'StripeUrlParameters', 'StripeRefundProcessor',
        'StripeRefund.jsp', '', NULL, NULL, 'StripePaymentSubscription.jsp');
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (29, 'Omnevo', 'EditOmnevoGateway.jsp', 'OmnevoPaymentHandler', 'OmnevoPayment.jsp', 'OmnevoInformation.jsp',
        'OmnevoRefundInformation.jsp', 'OmnevoUrlBuilder', 'OmnevoUrlParameters', 'OmnevoRefundProcessor',
        'OmnevoRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (30, 'NIC', 'EditNICGateway.jsp', 'NICPaymentHandler', 'NICPayment.jsp', 'NICInformation.jsp',
        'NICRefundInformation.jsp', 'NICUrlBuilder', 'NICUrlParameters', 'NICRefundProcessor', 'NICRefund.jsp', '',
        NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (31, 'Computop', 'EditComputopGateway.jsp', 'ComputopPaymentHandler', 'ComputopPayment.jsp',
        'ComputopInformation.jsp', 'ComputopRefundInformation.jsp', 'ComputopUrlBuilder', 'ComputopUrlParameters',
        'ComputopRefundProcessor', 'ComputopRefund.jsp', '', NULL, NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (32, 'SaltPay', 'EditSaltPayGateway.jsp', 'SaltPayPaymentHandler', 'SaltPayPayment.jsp',
        'SaltPayPaymentInformation.jsp', 'SaltPayRefundInformation.jsp', 'SaltPayURLBuilder', 'SaltPayURLParameters',
        'SaltPayRefundProcessor', 'SaltPayRefund.jsp', '', '', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (33, 'Datatrans', 'EditDatatransGateway.jsp', 'DatatransPaymentHandler', 'DatatransPayment.jsp',
        'DatatransPaymentInformation.jsp', 'DatatransRefundInformation.jsp', 'DatatransURLBuilder',
        'DatatransURLParameters', 'DatatransRefundProcessor', 'DatatransRefund.jsp', '', '', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (34, 'EMS Online', 'EditEMSOnlineGateway.jsp', 'EMSOnlinePaymentHandler', 'EMSOnlinePayment.jsp',
        'EMSOnlinePaymentInformation.jsp', 'EMSOnlineRefundInformation.jsp', 'EMSOnlineURLBuilder',
        'EMSOnlineURLParameters', 'EMSOnlineRefundProcessor', 'EMSOnlineRefund.jsp', '', '', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (35, 'Bankart', 'EditBankartGateway.jsp', 'BankartPaymentHandler', 'BankartPayment.jsp',
        'BankartPaymentInformation.jsp', 'BankartRefundInformation.jsp', 'BankartURLBuilder', 'BankartURLParameters',
        'BankartRefundProcessor', 'BankartRefund.jsp', '', '', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (36, 'PayoneV2', 'EditPayoneV2Gateway.jsp', 'PayoneV2PaymentHandler', 'PayoneV2Payment.jsp',
        'PayoneV2PaymentInformation.jsp', 'PayoneV2RefundInformation.jsp', 'PayoneV2URLBuilder',
        'PayoneV2URLParameters', 'PayoneV2RefundProcessor', 'PayoneV2Refund.jsp', '', '', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (37, 'Paytrail', 'EditPaytrailGateway.jsp', 'PaytrailPaymentHandler', 'PaytrailPayment.jsp',
        'PaytrailPaymentInformation.jsp', 'PaytrailRefundInformation.jsp', 'PaytrailURLBuilder',
        'PaytrailURLParameters', 'PaytrailRefundProcessor', 'PaytrailRefund.jsp', '', '', NULL, NULL);
INSERT INTO payment_gateway_types (id, `type`, adminJsp, paymentServlet, paymentJsp, paymentInformationJsp,
                                   refundInformationJsp, urlBuilderClass, urlParametersClass, refundProcessorClass,
                                   refundJsp, addSavedCardJsp, apiPaymentServlet, savedCardServlet,
                                   subscriptionPaymentJsp)
VALUES (38, 'Swedbank', 'EditSwedbankGateway.jsp', 'SwedbankPaymentHandler', 'SwedbankPayment.jsp',
        'SwedbankPaymentInformation.jsp', 'SwedbankRefundInformation.jsp', 'SwedbankURLBuilder',
        'SwedbankURLParameters', 'SwedbankRefundProcessor', 'SwedbankRefund.jsp', '', '', NULL, NULL);

CREATE TABLE `payments`
(
    `id`             int          NOT NULL AUTO_INCREMENT,
    `transactionId`  varchar(255) NOT NULL,
    `type`           int          NOT NULL DEFAULT '1',
    `reference`      varchar(255) NOT NULL,
    `amount`         varchar(255) NOT NULL DEFAULT '0.00',
    `amountRefunded` varchar(255) NOT NULL DEFAULT '0.00',
    `created`        datetime              DEFAULT CURRENT_TIMESTAMP,
    `migration`      int          NOT NULL DEFAULT '0',
    `carParkId`      int                   DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `id_UNIQUE` (`id`),
    UNIQUE KEY `uq_payments_reference_transactionid` (`reference`, `transactionId`),
    KEY `fk_payments_type_idx` (`type`),
    KEY `idx_payments_reference` (`reference`),
    KEY `idx_payments_reference_transactionid_type` (`reference`, `transactionId`, `type`),
    KEY `carParkId` (`carParkId`),
    CONSTRAINT `fk_payments_type` FOREIGN KEY (`type`) REFERENCES `payment_gateway_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`carParkId`) REFERENCES `ab_carparks` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `partial_payments`
(
    `id`              int          NOT NULL AUTO_INCREMENT,
    `transaction_id`  varchar(255) NOT NULL,
    `type`            int          NOT NULL,
    `reference`       varchar(255) NOT NULL,
    `amount`          varchar(255) NOT NULL DEFAULT '0.00',
    `amount_refunded` varchar(255) NOT NULL DEFAULT '0.00',
    `created`         datetime              DEFAULT CURRENT_TIMESTAMP,
    `migration`       int          NOT NULL DEFAULT '0',
    `carpark_id`      int                   DEFAULT NULL,
    `parent_payment`  int          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `id_UNIQUE` (`id`),
    KEY `fk_partial_payment_payment_gateway_type_payment_type_idx` (`type`),
    KEY `fk_partial_payment_ab_carparks_carParkId_idx` (`carpark_id`),
    KEY `fk_partial_payment_payment_parentPayment_idx` (`parent_payment`),
    KEY `idx_partial_payments_reference` (`reference`),
    CONSTRAINT `fk_partial_payment_ab_carparks_carParkId` FOREIGN KEY (`carpark_id`) REFERENCES `ab_carparks` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_partial_payment_payment_gateway_type_type` FOREIGN KEY (`type`) REFERENCES `payment_gateway_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_partial_payment_payment_parentPayment` FOREIGN KEY (`parent_payment`) REFERENCES `payments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `payments_custom_fields`
(
    `id`    int          NOT NULL AUTO_INCREMENT,
    `field` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `id_UNIQUE` (`id`),
    KEY `idx_paycustval_field` (`field`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `payments_custom_values`
(
    `id`               int          NOT NULL AUTO_INCREMENT,
    `field`            int DEFAULT NULL,
    `value`            varchar(255) NOT NULL,
    `paymentId`        int          NOT NULL,
    `partialPaymentId` int DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `id_UNIQUE` (`id`),
    UNIQUE KEY `field_paymentId_UNIQUE` (`paymentId`, `partialPaymentId`, `field`),
    KEY `fk_payCustVal_field_idx` (`field`),
    KEY `fk_payCustVal_payId_idx` (`paymentId`),
    KEY `fk_payCustVal_partialPayId_idx` (`partialPaymentId`),
    KEY `idx_pcv_partialPayId_field` (`partialPaymentId`, `field`),
    CONSTRAINT `fk_payCustVal_field` FOREIGN KEY (`field`) REFERENCES `payments_custom_fields` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_payCustVal_partialPayId` FOREIGN KEY (`partialPaymentId`) REFERENCES `partial_payments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_payCustVal_payId` FOREIGN KEY (`paymentId`) REFERENCES `payments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_payment`
(
    `id`             int NOT NULL AUTO_INCREMENT,
    `sub_booking_id` int NOT NULL,
    `payment_id`     int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `subscription_booking_payment_sub_booking_sub_booking_id_idx` (`sub_booking_id`),
    KEY `subscription_booking_payment_payments_payment_id_idx` (`payment_id`),
    CONSTRAINT `subscription_booking_payment_payments_payment_id` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `subscription_booking_payment_sub_booking_sub_booking_id` FOREIGN KEY (`sub_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_receipt_details`
(
    `id`                              int          NOT NULL AUTO_INCREMENT,
    `sub_booking_id`                  int          NOT NULL,
    `company_name`                    varchar(255) NOT NULL DEFAULT '',
    `tax_identification_number`       varchar(255) NOT NULL DEFAULT '',
    `address_1_receipt`               varchar(255) NOT NULL DEFAULT '',
    `address_2_receipt`               varchar(255) NOT NULL DEFAULT '',
    `postcode_receipt`                varchar(255) NOT NULL DEFAULT '',
    `town_receipt`                    varchar(255) NOT NULL DEFAULT '',
    `county_receipt`                  varchar(255) NOT NULL DEFAULT '',
    `country_receipt`                 varchar(255) NOT NULL DEFAULT '',
    `company_vat_registration_number` varchar(255) NOT NULL DEFAULT '',
    `tax_receipt`                     varchar(255) NOT NULL DEFAULT '',
    `country_code_receipt`            char(2)      NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_receipt_details_sub_booking_id` (`sub_booking_id`),
    CONSTRAINT `fk_subscription_booking_receipt_details_booking_id` FOREIGN KEY (`sub_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_vehicle_details`
(
    `id`               int         NOT NULL AUTO_INCREMENT,
    `sub_booking_id`   int         NOT NULL,
    `car_registration` varchar(50) NOT NULL DEFAULT '',
    `car_make`         varchar(45)          DEFAULT NULL,
    `car_model`        varchar(45)          DEFAULT NULL,
    `car_country`      varchar(45)          DEFAULT NULL,
    `car_colour`       varchar(45)          DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_subscription_booking_vehicle_details_booking_id` (`sub_booking_id`),
    CONSTRAINT `fk_subscription_booking_vehicle_details_ab_booking_id` FOREIGN KEY (`sub_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_scheduled_recurring_payment`
(
    `id`                             int            NOT NULL AUTO_INCREMENT,
    `payment_id`                     int            NOT NULL,
    `site_id`                        int                     DEFAULT NULL,
    `affiliate_id`                   int            NOT NULL,
    `contact_id`                     int            NOT NULL,
    `sub_booking_id`                 int                     DEFAULT NULL,
    `amount`                         decimal(13, 2) NOT NULL,
    `last_payment_date`              date                    DEFAULT NULL,
    `upcoming_payment_date`          date           NOT NULL,
    `subscription_booking_reference` varchar(50)    NOT NULL,
    `enabled`                        bit(1)         NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    KEY `fk_sub_scheduled_recurring_payment_payments_payment_id_idx` (`payment_id`),
    KEY `fk_sub_scheduled_recurring_payment_ab_affiliates_aff_id_idx` (`affiliate_id`),
    KEY `fk_sub_scheduled_recurring_payment_ab_sites_site_id_idx` (`site_id`),
    KEY `fk_sub_scheduled_recurring_payment_ab_contacts_contact_id_idx` (`contact_id`),
    KEY `fk_sub_scheduled_recurring_payment_sub_booking_booking_id_idx` (`sub_booking_id`),
    CONSTRAINT `fk_sub_scheduled_recurring_payment_ab_affiliates_aff_id` FOREIGN KEY (`affiliate_id`) REFERENCES `ab_affiliates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_sub_scheduled_recurring_payment_ab_contacts_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `ab_contacts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_sub_scheduled_recurring_payment_ab_sites_site_id` FOREIGN KEY (`site_id`) REFERENCES `ab_sites` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_sub_scheduled_recurring_payment_payments_payment_id` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_sub_scheduled_recurring_payment_sub_booking_booking_id` FOREIGN KEY (`sub_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE OR REPLACE ALGORITHM = UNDEFINED VIEW `subscription_all_booking_data` AS
select `subscription_booking`.`reference`                                                      AS `subscription_reference`,
       `subscription_booking_item`.`sub_total`                                                 AS `subscription_price`,
       `subscription_booking`.`grand_total`                                                    AS `subscription_total`,
       ifnull(`payment_payment_type`.`value`, `payment_card_type`.`value`)                     AS `payment_method`,
       `payment_card_number`.`value`                                                           AS `card_number`,
       ifnull(`payment_card_expiry_date`.`value`,
              concat(`payment_card_expiry_month`.`value`, `payment_card_expiry_year`.`value`)) AS `card_expiry_date`,
       `subscription_product`.`name`                                                           AS `product_name`,
       `subscription_product`.`id`                                                             AS `product_id`,
       ifnull(`subscription_booking_recurring_ticket`.`start_date`,
              `subscription_booking_season_ticket`.`start_date`)                               AS `start_date`,
       ifnull(`subscription_booking_recurring_ticket`.`minimum_term_date`,
              `subscription_booking_season_ticket`.`end_date`)                                 AS `end_date`,
       ifnull(`subscription_booking_recurring_ticket`.`minimum_term`,
              `subscription_booking_season_ticket`.`minimum_term`)                             AS `minimum_term_length`,
       `subscription_scheduled_recurring_payment`.`upcoming_payment_date`                      AS `next_payment_date`,
       `ab_carparks`.`name`                                                                    AS `carpark_name`,
       `subscription_booking_item`.`period_type`                                               AS `subscription_type`,
       concat(`subscription_booking_customer_details`.`first_name`, ' ',
              `subscription_booking_customer_details`.`last_name`)                             AS `customer_name`,
       `subscription_booking_customer_details`.`email_address`                                 AS `customer_email`,
       `subscription_booking_customer_details`.`phone_number`                                  AS `customer_mobile_number`,
       `subscription_booking_customer_details`.`address1`                                      AS `customer_address_1`,
       `subscription_booking_customer_details`.`address2`                                      AS `customer_address_2`,
       `subscription_booking_customer_details`.`town`                                          AS `customer_town`,
       `subscription_booking_customer_details`.`county`                                        AS `customer_county`,
       `subscription_booking_customer_details`.`postcode`                                      AS `customer_postcode`,
       `subscription_booking_customer_details`.`country`                                       AS `customer_country`,
       `subscription_booking_receipt_details`.`company_name`                                   AS `customer_company_name`,
       `subscription_booking_vehicle_details`.`car_registration`                               AS `customer_vehicle_reg`,
       `subscription_booking_vehicle_details`.`car_make`                                       AS `customer_vehicle_make`,
       `subscription_booking_vehicle_details`.`car_model`                                      AS `customer_vehicle_model`,
       `subscription_booking_vehicle_details`.`car_colour`                                     AS `customer_vehicle_colour`
from (((((((((((((((((`subscription_booking`
    left join `subscription_booking_item` on
    ((`subscription_booking`.`id` = `subscription_booking_item`.`sub_booking_id`)))
    left join `subscription_product` on
    ((`subscription_product`.`id` = `subscription_booking_item`.`product_id`)))
    left join `ab_carparks` on
    ((`ab_carparks`.`id` = `subscription_product`.`car_park_id`)))
    left join `subscription_booking_receipt_details` on
    ((`subscription_booking_receipt_details`.`sub_booking_id` = `subscription_booking`.`id`)))
    left join `subscription_booking_customer_details` on
    ((`subscription_booking_customer_details`.`sub_booking_id` = `subscription_booking`.`id`)))
    left join `subscription_booking_vehicle_details` on
    ((`subscription_booking_vehicle_details`.`sub_booking_id` = `subscription_booking`.`id`)))
    left join `subscription_booking_payment` on
    ((`subscription_booking_payment`.`sub_booking_id` = `subscription_booking`.`id`)))
    left join `subscription_booking_recurring_ticket` on
    ((`subscription_booking_recurring_ticket`.`item_id` = `subscription_booking_item`.`id`)))
    left join `subscription_booking_season_ticket` on
    ((`subscription_booking_season_ticket`.`item_id` = `subscription_booking_item`.`id`)))
    left join `subscription_scheduled_recurring_payment` on
    ((`subscription_scheduled_recurring_payment`.`subscription_booking_reference` =
      `subscription_booking`.`reference`)))
    left join `payments` on
    ((`payments`.`id` = `subscription_booking_payment`.`payment_id`)))
    left join `payments_custom_values` `payment_card_number` on
    (((`payment_card_number`.`paymentId` = `payments`.`id`) and
      (`payment_card_number`.`field` = (select `payments_custom_fields`.`id`
                                        from `payments_custom_fields`
                                        where (`payments_custom_fields`.`field` = 'cardNumber'))))))
    left join `payments_custom_values` `payment_card_expiry_date` on
    (((`payment_card_expiry_date`.`paymentId` = `payments`.`id`) and
      (`payment_card_expiry_date`.`field` = (select `payments_custom_fields`.`id`
                                             from `payments_custom_fields`
                                             where (`payments_custom_fields`.`field` = 'cardExpiryDate'))))))
    left join `payments_custom_values` `payment_card_type` on
    (((`payment_card_type`.`paymentId` = `payments`.`id`) and
      (`payment_card_type`.`field` = (select `payments_custom_fields`.`id`
                                      from `payments_custom_fields`
                                      where (`payments_custom_fields`.`field` = 'cardType'))))))
    left join `payments_custom_values` `payment_payment_type` on
    (((`payment_payment_type`.`paymentId` = `payments`.`id`) and
      (`payment_payment_type`.`field` = (select `payments_custom_fields`.`id`
                                         from `payments_custom_fields`
                                         where (`payments_custom_fields`.`field` = 'paymentType'))))))
    left join `payments_custom_values` `payment_card_expiry_month` on
    (((`payment_card_expiry_month`.`paymentId` = `payments`.`id`) and
      (`payment_card_expiry_month`.`field` = (select `payments_custom_fields`.`id`
                                              from `payments_custom_fields`
                                              where (`payments_custom_fields`.`field` = 'cardExpiryMonth'))))))
    left join `payments_custom_values` `payment_card_expiry_year` on
    (((`payment_card_expiry_year`.`paymentId` = `payments`.`id`) and
      (`payment_card_expiry_year`.`field` = (select `payments_custom_fields`.`id`
                                             from `payments_custom_fields`
                                             where (`payments_custom_fields`.`field` = 'cardExpiryYear'))))));

CREATE TABLE `feature_flag`
(
    `key`   varchar(255) NOT NULL,
    `value` varchar(768) NOT NULL,
    PRIMARY KEY (`key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_encrypted`
(
    `id`                      int NOT NULL AUTO_INCREMENT,
    `subscription_booking_id` int          DEFAULT NULL,
    `encrypted_reference`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_subscription_booking_encrypted_subscription_booking_id` (`subscription_booking_id`),
    KEY `idx_subscription_booking_encrypted_encrypted_reference` (`encrypted_reference`) USING BTREE,
    CONSTRAINT `fk_subscription_booking_encrypted_subscription_booking_id` FOREIGN KEY (`subscription_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_discounted_renewal`
(
    `id`                         int            NOT NULL AUTO_INCREMENT,
    `discounted_renewal_enabled` bit(1)         NOT NULL DEFAULT b'0',
    `discount_type`              varchar(100)   NOT NULL DEFAULT 'fixed',
    `discount_amount`            decimal(13, 2) NOT NULL DEFAULT '0.00',
    `renewal_days`               int                     DEFAULT NULL,
    `subscription_product_id`    int            NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_subscription_discounted_renewal_subscription_product` (`subscription_product_id`),
    KEY `idx_subscription_product_id` (`subscription_product_id`),
    CONSTRAINT `fk_subscription_discounted_renewal_subscription_product` FOREIGN KEY (`subscription_product_id`) REFERENCES `subscription_product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 36
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_custom_value`
(
    `id`         int          NOT NULL AUTO_INCREMENT,
    `booking_id` int          NOT NULL,
    `custom_key` varchar(255) NOT NULL,
    `value`      text,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_scv_booking_id_custom_key` (`booking_id`, `custom_key`),
    KEY `idx_subscription_custom_value_booking_id` (`booking_id`),
    CONSTRAINT `fk_subscription_custom_value_subscription_booking_booking_id` FOREIGN KEY (`booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `subscription_booking_membership`
(
    `id`                      int         NOT NULL AUTO_INCREMENT,
    `subscription_booking_id` int         NOT NULL,
    `membership_id`           varchar(45) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_sbm_subscription_booking_subscription_booking_id` (`subscription_booking_id`),
    KEY `idx_subscription_booking_membership_membership_id` (`membership_id`),
    CONSTRAINT `fk_sbm_subscription_booking_subscription_booking_id` FOREIGN KEY (`subscription_booking_id`) REFERENCES `subscription_booking` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

-- Languages
INSERT INTO languages (id, languageName, languageCode, displayCode, displayName)
VALUES (1, 'English (United Kingdom)', 'en-gb', 'en-gb', 'en');

-- Currencies
INSERT INTO currencies (id, code, symbol, htmlSymbol, isoCode, currencyNumber, conversionRate)
VALUES (1, 'GBP', '£', '£', NULL, '826', 1.0000);
-- Locations
INSERT INTO locations (id, name, decimalSymbol, numDigitsAfterDecimal, digitGroupSymbol, digitGrouping, negNumFormat,
                       displayLeadingZero, currencySymbol, currencyPositiveFormat, currencyNegativeFormat,
                       currencyDecimalPlaces, currencyGroupingSymbol, currencyDecimalSymbol, currencyHidePennies,
                       dateFormat, longDateFormat, longDateTimeFormat, dateFormatSQL, dateFormatForReports, timeShort,
                       timeLong, time24hr, currency, isoCode, calendarStartDay, countryCallingCode, sqlLocationCode,
                       drinkingAge, defaultLanguageId)
VALUES (1, 'United Kingdom', '.', 2, ',', '123,456,789', '-1.1', 1, '&pound;', '&pound;1.1', '-&pound;1.1', 2, ',',
        '.', 0, 'dd/MM/yyyy', 'EE, dd/MM/yyyy', '', '%d/%m/%Y', 'yyyy-MM-dd', 'hh:mm tt', 'hh:mm:ss tt', 1, 1, NULL, 1,
        '+44', 'en_GB', 18, 1);

-- Sites
INSERT INTO ab_sites (id, title, siteType, numberOfTerminals, isEnabled, locationId, timezone, secondsFromGMT,
                      isUserAccountsEnabled)
VALUES (1, 'Main Site', 'AIRPORT', 1, 1, 1, 'Etc/GMT', 0, 1),
       (12, 'Main Site', 'AIRPORT', 1, 1, 1, 'Etc/GMT', 0, 1),
       (17, 'Main Site', 'AIRPORT', 1, 1, 1, 'Etc/GMT', 0, 1);


-- Affiliates
INSERT INTO ab_affiliates (id, code, name, companyName, defaultPromoCode, siteid, vatRate, vatNo, vatAddress,
                           vatCompany, callCentreNumber, isEnabled)
VALUES (72, 'AFF72', 'Affiliate 72', 'Company 72', 'PROMO72', 1, 20.00, 'GB123456789', '72 Street', 'VAT Co 72',
        '0123456789', 1),
       (29, 'AFF29', 'Affiliate 29', 'Company 29', 'PROMO29', 1, 20.00, 'GB123456780', '29 Street', 'VAT Co 29',
        '0123456790', 1);

-- Contacts
INSERT INTO ab_contacts (id, modified, emailAddress, address1, siteId)
VALUES (40812, NOW(), 'derrick.feehi@aeroparker.com', 'Street A', 1);

-- Subscription bookings
INSERT INTO subscription_booking (id, reference, created, affiliate_id, contact_id, grand_total, vat_amount,
                                  amended_date, cancelled, cancelled_date, booking_fee)
VALUES (29, 'SNWSC100150', '2019-08-29 16:48:38', 72, 40812, 10.00, 1.87, NULL, 0, NULL, 0.00),
       (24, 'BOOK27', '2019-08-29 16:48:38', 72, 40812, 10.00, 1.87, NULL, 0, NULL, 0.00),
       (72, 'BOOK72', '2019-08-29 16:48:38', 72, 40812, 10.00, 1.87, NULL, 0, NULL, 0.00),
       (75, 'BOOK75', '2019-08-29 16:48:38', 72, 40812, 10.00, 1.87, NULL, 0, NULL, 0.00),
       (44, 'BOOK44', '2019-08-29 16:48:38', 72, 40812, 10.00, 1.87, NULL, 0, NULL, 0.00),
       (33, 'BOOK33', '2019-08-29 16:48:38', 72, 40812, 10.00, 1.87, NULL, 0, NULL, 0.00),
       (49, 'BOOK49', '2019-08-29 16:48:38', 72, 40812, 10.00, 1.87, NULL, 0, NULL, 0.00);

-- Car parks
INSERT INTO ab_carparks (id, name, terminal, siteid, carParkCode)
VALUES (81, 'Main Car Park', 1, 1, 'CP81');

-- Products
INSERT INTO subscription_product (id, name, site_id, car_park_id, enabled)
VALUES (13, 'Test Product', 1, 81, b'1');

-- Booking Item
INSERT INTO subscription_booking_item (id, sub_booking_id, product_id, car_park_id, car_park_name, product_display_name,
                                       period_type)
VALUES (19, 75, 13, 81, 'CarParkName', 'DisplayName2', 'MONTHLY'),
       (31, 33, 13, 81, 'CarParkName', 'DisplayName2', 'MONTHLY'),
       (49, 49, 13, 81, 'CarParkName', 'DisplayName2', 'MONTHLY'),
       (50, 24, 13, 81, 'CarParkName', 'DisplayName2', 'MONTHLY');


-- Encrypted References
INSERT INTO subscription_booking_encrypted (id, subscription_booking_id, encrypted_reference)
VALUES (1, 24, 'C02BFD12B3641A0E4FB6AFED1F11473A'),
       (2, 72, '123');

-- GUIDs
INSERT INTO subscription_guid (id, guid)
VALUES (17, '793f5b8b-65b5-4854-b872-dd1c50350ed8'),
       (13, '13-guid'),
       (26, '26-guid');


-- Purchase Data
INSERT INTO subscription_purchase_data (id, customer_guid, purchase_data, affiliate_id)
VALUES (288, '13-guid', '{\"productId\":49}', 72),
       (1, '3482270b-5765-4f70-8425-d3ea2a29315a', '[{\"productId\":26,\"startDate\":\"28.10.19\"}]', 29);

-- Guid Bookings
INSERT INTO subscription_guid_booking (id, subscription_guid_id, subscription_booking_id, subscription_purchase_data_id)
VALUES (1, 26, 24, 1);

-- Booking Customer Details
INSERT INTO subscription_booking_customer_details (id, sub_booking_id, first_name, last_name, title, postcode, email_address)
VALUES (1, 44, 'Derrick', 'Feehi', 'Mr', 'M40 5RJ', 'derrick.feehi@aeroparker.com');

-- Season Tickets
INSERT INTO subscription_booking_season_ticket (id, start_date, end_date, item_id, period_term, minimum_term, price)
VALUES (1, '2019-08-31', '2019-09-30', 19, 'FIXED', 'FIVE_MONTHS', 0.00);

-- Recurring Tickets
INSERT INTO subscription_booking_recurring_ticket (id, start_date, minimum_term_date, item_id, minimum_term, price)
VALUES (1, '2019-09-25', '2019-11-25', 50, 'THREE_MONTHS', 0.00);

-- Recurring Payment by Site
INSERT INTO site_subscription_recurring_payment (id, site_id)
VALUES (1, 17);

-- Payment
INSERT INTO payments (id, transactionId, `type`, reference, amount, amountRefunded, created, migration, carParkId)
VALUES (7999, 'by8d7h', 1, 'TEST', '35.99', '35.99', '2016-04-21 18:14:21', 0, NULL),
       (8122, 'daf34f', 1, 'TEST', '35.99', '35.99', '2016-04-21 18:14:21', 0, NULL);

-- Booking Payments
INSERT INTO subscription_booking_payment (id, sub_booking_id, payment_id)
VALUES (1, 75, 8122);

-- Subscription Schedule Recurring Payment
INSERT INTO subscription_scheduled_recurring_payment (id, payment_id, site_id, affiliate_id, contact_id, sub_booking_id,
                                                      amount, last_payment_date, upcoming_payment_date,
                                                      subscription_booking_reference, enabled)
VALUES (1, 7999, 1, 72, 40812, 49, 95.00, NULL, '2019-12-18', 'DTMWSC101310', 1);

-- Reservation Data
INSERT INTO subscription_booking_reservation_data (id, guid, title, phone_number, address1, address2, town, county,
                                                   country, postcode, first_name, last_name, email_address,
                                                   company_name, tax_identification_number, tax_receipt,
                                                   company_vat_registration_number, country_receipt, county_receipt,
                                                   town_receipt, postcode_receipt, address_2_receipt, address_1_receipt,
                                                   car_registration, car_make, car_model, car_country, car_colour,
                                                   current_language_id, invoice_requested, country_code_receipt,
                                                   referrer_membership_id)
VALUES (1, 'guid', 'Mr', '554154111', '45', '', '45454', '454545', 'Belarus', '414141',
        'Muneeb', 'Khalid', 'muneeb.khalid@aeroparker.com', '545454', '111', '', '', '', '', '', '', '121121212',
        '1212121212', '', '', '', '', '', NULL, 0, '', NULL);

-- Discounted Renewal
INSERT INTO subscription_discounted_renewal (id, discounted_renewal_enabled, discount_type, discount_amount,
                                             renewal_days, subscription_product_id)
VALUES (1, 0, 'fixed', 40.00, NULL, 13);