CREATE TABLE `subscription_reference_sequence` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` int NOT NULL DEFAULT '100000',
  `site_id` int NOT NULL,
  PRIMARY KEY (`id`)
);

insert into subscription_reference_sequence (id, `value`, site_id) value (1,1,1);

CREATE TABLE `ab_sequence` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` int NOT NULL DEFAULT '100000',
  `siteId` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO ab_sequence (id, value,siteId,name) VALUES (1,100,2,'bookings');
