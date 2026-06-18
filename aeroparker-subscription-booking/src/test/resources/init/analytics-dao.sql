CREATE TABLE `affiliate_analytics_new` (
  `id` int NOT NULL AUTO_INCREMENT,
  `affiliateId` int DEFAULT NULL,
  `enabled` int DEFAULT NULL,
  `location` int DEFAULT NULL,
  `steps` varchar(225) DEFAULT NULL,
  `content` text,
  PRIMARY KEY (`id`),
  KEY `affiliatenewanalyticstoaffiliates_idx` (`affiliateId`)
);

INSERT INTO affiliate_analytics_new (id, affiliateId, enabled, location, steps, content) VALUES(5945, 211, 1, 3, '39', '<script>');

CREATE TABLE `subscription_tracking` (
  `id` int NOT NULL AUTO_INCREMENT,
  `subscription_id` int NOT NULL,
  `booking_reference` varchar(255) DEFAULT '',
  `booking_total` decimal(13,2) DEFAULT '0.00',
  `product_id` int DEFAULT NULL,
  `product_name` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_subscription_tracking_subscription_id` (`subscription_id`),
  KEY `idx_subscription_tracking_product_id` (`product_id`)
);