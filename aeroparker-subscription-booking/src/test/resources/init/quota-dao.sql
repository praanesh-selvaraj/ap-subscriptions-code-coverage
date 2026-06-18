CREATE TABLE `quotas_subscription` (
  `id` int NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `maximum_occupancy_enabled` bit(1) NOT NULL DEFAULT b'0',
  `maximum_occupancy_value` int NOT NULL DEFAULT '0',
  `all_products_enabled` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
);

INSERT INTO quotas_subscription (id, site_id, name, maximum_occupancy_enabled, maximum_occupancy_value, all_products_enabled) VALUES(1, 1, 'test', 1, 1, 0);

CREATE TABLE `quotas_subscription_product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `subscription_quota_id` int NOT NULL,
  `subscription_product_id` int NOT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO quotas_subscription_product (id, subscription_quota_id, subscription_product_id) VALUES(1, 1, 1);

CREATE TABLE `ab_affiliates` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(30) NOT NULL,
  `name` varchar(255) NOT NULL,
  `companyName` varchar(255) NOT NULL,
  `type` int NOT NULL DEFAULT '0',
  `defaultPromoCode` varchar(255) NOT NULL,
  `siteid` int NOT NULL,
  `isEnabled` int NOT NULL DEFAULT '0',
  `bookingReferenceFormat` varchar(30) NOT NULL DEFAULT '{S}{R}{P}{I}',
  `bookingReferencePrefix` varchar(5) NOT NULL DEFAULT 'W',
  `bookingSequenceName` varchar(80) NOT NULL DEFAULT 'bookings',
  `paymentPropertiesNode` varchar(50) DEFAULT NULL,
  `bookingFee` decimal(13,2) NOT NULL DEFAULT '0.00',
  `bookingFeeType` smallint NOT NULL DEFAULT '1',
  `refundBookingFeeOnCancellation` int NOT NULL DEFAULT '0',
  `debitCardFee` decimal(13,2) NOT NULL DEFAULT '0.00',
  `debitCardFeeType` smallint NOT NULL DEFAULT '0',
  `creditCardFee` decimal(13,2) NOT NULL DEFAULT '0.00',
  `creditCardFeeType` smallint NOT NULL DEFAULT '0',
  `paypalFee` decimal(13,2) NOT NULL DEFAULT '0.00',
  `paypalUsePercent` int NOT NULL DEFAULT '0',
  `refundCardFeeOnCancellation` int NOT NULL DEFAULT '0',
  `cancellationCharge` decimal(13,2) NOT NULL DEFAULT '0.00',
  `cancellationChargeType` smallint NOT NULL DEFAULT '1',
  `amendCancel_minHoursBeforeDateOfArrival` int NOT NULL DEFAULT '0',
  `amendCancel_ignoreTimeOfDay` smallint NOT NULL DEFAULT '0',
  `bookingDateType` smallint NOT NULL DEFAULT '1',
  `vatRate` decimal(13,2) NOT NULL,
  `stateTaxRate` decimal(13,2) NOT NULL DEFAULT '0.00',
  `cityTax` decimal(13,2) DEFAULT '0.00',
  `vatNo` varchar(50) DEFAULT NULL,
  `vatAddress` varchar(255) DEFAULT NULL,
  `vatCompany` varchar(255) DEFAULT NULL,
  `smsAssistancePrice` decimal(13,2) NOT NULL DEFAULT '-1.00',
  `crmType` int NOT NULL DEFAULT '0',
  `enable3DSecure` smallint NOT NULL DEFAULT '0',
  `secure3DPurchaseDesc` varchar(80) DEFAULT NULL,
  `secure3DWebsiteUrl` varchar(255) DEFAULT NULL,
  `callCentreNumber` varchar(50) DEFAULT NULL,
  `isUserAccountsEnabled` int NOT NULL DEFAULT '0',
  `isSaveForLaterEnabled` int NOT NULL DEFAULT '0',
  `isSaveForLaterReminderEnabled` int NOT NULL DEFAULT '0',
  `isSocialLoginEnabled` int NOT NULL DEFAULT '0',
  `isDataCashEnabled` int NOT NULL DEFAULT '0',
  `isPaypalEnabled` int NOT NULL DEFAULT '0',
  `isNetaxeptEnabled` int NOT NULL DEFAULT '0',
  `isMainAffiliate` int NOT NULL DEFAULT '0',
  `cardPaymentsEnabled` int NOT NULL DEFAULT '0',
  `payPalEnabled` int NOT NULL DEFAULT '0',
  `isArchived` tinyint(1) NOT NULL DEFAULT '0',
  `isReferAFriendEnabled` int NOT NULL DEFAULT '0',
  `isStripePaymentEnabled` int DEFAULT '0',
  PRIMARY KEY (`id`)
);

INSERT INTO ab_affiliates (id, code, name, companyName, `type`, defaultPromoCode, siteid, isEnabled, bookingReferenceFormat, bookingReferencePrefix, bookingSequenceName, paymentPropertiesNode, bookingFee, bookingFeeType, refundBookingFeeOnCancellation, debitCardFee, debitCardFeeType, creditCardFee, creditCardFeeType, paypalFee, paypalUsePercent, refundCardFeeOnCancellation, cancellationCharge, cancellationChargeType, amendCancel_minHoursBeforeDateOfArrival, amendCancel_ignoreTimeOfDay, bookingDateType, vatRate, stateTaxRate, cityTax, vatNo, vatAddress, vatCompany, smsAssistancePrice, crmType, enable3DSecure, secure3DPurchaseDesc, secure3DWebsiteUrl, callCentreNumber, isUserAccountsEnabled, isSaveForLaterEnabled, isSaveForLaterReminderEnabled, isSocialLoginEnabled, isDataCashEnabled, isPaypalEnabled, isNetaxeptEnabled, isMainAffiliate, cardPaymentsEnabled, payPalEnabled, isArchived, isReferAFriendEnabled, isStripePaymentEnabled) VALUES(1, 'Demo', 'Avalon City', 'Avalon City Company Limited', 1, '', 1, 0, '{S}{R}{P}{I}', 'D', 'bookings', '', 0.00, 1, 0, 0.00, 1, 3.00, 2, 0.00, 0, 0, 5.00, 1, 3, 0, 1, 20.00, 0.00, 50.00, '123456789', 'Avalon City, Fictionland, AB12 3CE', 'Avalon City', -1.00, 0, 0, 'Avalon City', 'http://demo.aeroparker.com', '0844 8877 747', 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0);

CREATE TABLE `subscription_product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `site_id` int NOT NULL,
  `car_park_id` int DEFAULT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'0',
  `all_affiliates_checked` bit(1) DEFAULT NULL,
  `multiple_car_parks_selected` bit(1) DEFAULT b'0',
  `all_car_parks_selected` bit(1) DEFAULT b'0',
  `hide_car_park` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`)
);

INSERT INTO subscription_product (id, name, site_id, car_park_id, enabled, all_affiliates_checked, multiple_car_parks_selected, all_car_parks_selected, hide_car_park) VALUES(1, 'Long Stay 1 month', 1, 64, 1, 1, 0, 0, 0);

CREATE TABLE `subscription_booking` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reference` varchar(50) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `affiliate_id` int NOT NULL,
  `contact_id` int NOT NULL,
  `grand_total` decimal(13,2) NOT NULL DEFAULT '0.00',
  `vat_amount` decimal(13,2) NOT NULL DEFAULT '0.00',
  `amended_date` datetime DEFAULT NULL,
  `cancelled` bit(1) NOT NULL DEFAULT b'0',
  `cancelled_date` datetime DEFAULT NULL,
  `booking_fee` decimal(13,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`)
);

INSERT INTO subscription_booking (id, reference, created, affiliate_id, contact_id, grand_total, vat_amount, amended_date, cancelled, cancelled_date, booking_fee) VALUES(1, 'SNWSC100520', '2019-08-29 16:48:38', 1, 796873, 10.00, 1.87, NULL, 0, NULL, 0.00);

CREATE TABLE `subscription_booking_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sub_booking_id` int NOT NULL,
  `product_id` int NOT NULL,
  `car_park_id` int NOT NULL,
  `car_park_name` varchar(45) NOT NULL,
  `product_display_name` varchar(45) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `period_type` varchar(45) NOT NULL,
  `vat_rate` decimal(13,2) NOT NULL DEFAULT '0.00',
  `sub_total` decimal(13,2) NOT NULL DEFAULT '0.00',
  `vat_amount` decimal(13,2) NOT NULL DEFAULT '0.00',
  `booking_fee` decimal(13,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`)
 );
  
INSERT INTO subscription_booking_item (id, sub_booking_id, product_id, car_park_id, car_park_name, product_display_name, created, period_type, vat_rate, sub_total, vat_amount, booking_fee) VALUES(1, 1, 33, 93, 'Long Term 1', 'Silver', '2019-12-16 11:43:22', 'FIXED', 23.00, 20.00, 3.74, 0.00);
 
CREATE TABLE `subscription_booking_season_ticket` (
  `id` int NOT NULL AUTO_INCREMENT,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `item_id` int NOT NULL,
  `period_term` varchar(45) DEFAULT NULL,
  `minimum_term` varchar(45) DEFAULT NULL,
  `price` decimal(13,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ;

INSERT INTO subscription_booking_season_ticket (id, start_date, end_date, item_id, period_term, minimum_term, price) VALUES(1, '2025-01-01', '2025-12-01, 1, 'FIXED', 'TWEVLE_MONTHs', 0.00);

CREATE OR REPLACE
VIEW `subscription_booking_ticket` AS
select
    ifnull(`sbst`.`start_date`, `sbrt`.`start_date`) AS `start_date`,
    ifnull(`sbst`.`end_date`, `sbrt`.`minimum_term_date`) AS `end_date`,
    ifnull(`sbst`.`item_id`, `sbrt`.`item_id`) AS `item_id`,
    ifnull(`sbst`.`minimum_term`, `sbrt`.`minimum_term`) AS `minimum_term`,
    ifnull(`sbst`.`price`, `sbrt`.`price`) AS `price`
from
    (((`kmp_advancebooker`.`subscription_booking` `sb`
left join `kmp_advancebooker`.`subscription_booking_item` `sbi` on
    ((`sb`.`id` = `sbi`.`sub_booking_id`)))
left join `kmp_advancebooker`.`subscription_booking_season_ticket` `sbst` on
    ((`sbi`.`id` = `sbst`.`item_id`)))
left join `kmp_advancebooker`.`subscription_booking_recurring_ticket` `sbrt` on
    ((`sbi`.`id` = `sbrt`.`item_id`)));