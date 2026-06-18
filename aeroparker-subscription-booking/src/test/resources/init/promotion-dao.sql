-- Promotion DAO test data setup

-- Table: subscription_session_promotion
-- Stores temporary promotion data during the booking session
CREATE TABLE `subscription_session_promotion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL COMMENT 'fk to subscription_guid',
  `promo_code` varchar(255) NOT NULL,
  `promo_id` int DEFAULT NULL COMMENT 'fk to promotions',
  `promo_code_id` int DEFAULT NULL,
  `discount_amount` decimal(13,2) DEFAULT NULL,
  `discounted_price` decimal(13,2) DEFAULT NULL,
  `valid` bit(1) DEFAULT b'0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_subscription_session_promotion_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Test data for subscription_session_promotion
INSERT INTO subscription_session_promotion (id, guid, promo_code, promo_id, promo_code_id, discount_amount, discounted_price, valid, created_at)
VALUES (1, 'test-guid-123', 'SAVE10', 100, 200, 10.00, 90.00, b'1', '2024-01-01 10:00:00');

INSERT INTO subscription_session_promotion (id, guid, promo_code, promo_id, promo_code_id, discount_amount, discounted_price, valid, created_at)
VALUES (2, 'test-guid-456', 'SAVE20', 101, 201, 20.00, 80.00, b'1', '2024-01-02 11:00:00');

INSERT INTO subscription_session_promotion (id, guid, promo_code, promo_id, promo_code_id, discount_amount, discounted_price, valid, created_at)
VALUES (3, 'test-guid-invalid', 'INVALID', NULL, NULL, NULL, NULL, b'0', '2024-01-03 12:00:00');


-- Table: subscription_promo_booking
-- Stores permanent promotion data linked to completed bookings
CREATE TABLE `subscription_promo_booking` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sub_booking_id` int NOT NULL,
  `discount` decimal(13,2) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `promotion_id` int NOT NULL,
  `promo_code_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_subscription_promo_booking_sub_booking_id` (`sub_booking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Test data for subscription_promo_booking
INSERT INTO subscription_promo_booking (id, sub_booking_id, discount, code, promotion_id, promo_code_id)
VALUES (1, 1000, 10.00, 'SAVE10', 100, 200);

INSERT INTO subscription_promo_booking (id, sub_booking_id, discount, code, promotion_id, promo_code_id)
VALUES (2, 1001, 20.00, 'SAVE20', 101, 201);


-- Table: promotions_promo_codes
-- Master table for all available promotion codes
CREATE TABLE `promotions_promo_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `promotionId` int NOT NULL,
  `siteId` int NOT NULL,
  `code` varchar(255) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expiryDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `uses` int NOT NULL DEFAULT '0',
  `maximumUses` int NOT NULL DEFAULT '0',
  `discountAmount` decimal(13,2) DEFAULT NULL,
  `discountPercentage` decimal(5,2) DEFAULT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_promotions_promo_codes_code` (`code`),
  KEY `idx_promotions_promo_codes_promotionId` (`promotionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Test data for promotions_promo_codes
INSERT INTO promotions_promo_codes (id, promotionId, code, created, expiryDate, uses, maximumUses, discountAmount, discountPercentage, enabled, siteId)
VALUES (200, 100, 'SAVE10', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 5, 100, 10.00, NULL, b'1', 1);

INSERT INTO promotions_promo_codes (id, promotionId, code, created, expiryDate, uses, maximumUses, discountAmount, discountPercentage, enabled, siteId)
VALUES (201, 101, 'SAVE20', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 10, 200, 20.00, NULL, b'1', 1);

INSERT INTO promotions_promo_codes (id, promotionId, code, created, expiryDate, uses, maximumUses, discountAmount, discountPercentage, enabled, siteId)
VALUES (202, 102, 'PERCENT15', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 0, 50, NULL, 15.00, b'1', 1);

INSERT INTO promotions_promo_codes (id, promotionId, code, created, expiryDate, uses, maximumUses, discountAmount, discountPercentage, enabled, siteId)
VALUES (203, 103, 'EXPIRED', '2023-01-01 00:00:00', '2023-12-31 23:59:59', 0, 10, 5.00, NULL, b'0', 1);
