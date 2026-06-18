CREATE TABLE `payment_invoice` (
  `id` int NOT NULL AUTO_INCREMENT,
  `payment_id` int NOT NULL,
  `reference` varchar(225) NOT NULL,
  `s3_key` varchar(225) DEFAULT ' ',
  `type` int NOT NULL DEFAULT '1',
  `invoice_guid` varchar(255) DEFAULT '',
  `invoice_created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO payment_invoice (id, payment_id, reference, s3_key, `type`, invoice_guid, invoice_created) VALUES(1, 534209, '1', ' ', 1, '123', NULL);