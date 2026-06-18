CREATE TABLE `company` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `site_id` int NOT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'0',
  `sequence_prefix` varchar(45) NOT NULL,
  `sequence_start` varchar(45) NOT NULL,
  `sequence_current` varchar(225) DEFAULT NULL,
  `type` varchar(45) DEFAULT 'Parking',
  `all_ancillaries` bit(1) DEFAULT b'0'
);

INSERT INTO company (id, name, site_id, enabled, sequence_prefix, sequence_start, sequence_current, `type`, all_ancillaries) 
VALUES(1, 'sub', 1, 1, 'seq', '1', 'seq1', 'Subscription', 0);

CREATE TABLE `payment_invoice` (
  `id` int NOT NULL,
  `payment_id` int NOT NULL,
  `reference` varchar(225) NOT NULL,
  `s3_key` varchar(225) DEFAULT ' ',
  `type` int NOT NULL DEFAULT '1',
  `invoice_guid` varchar(255) DEFAULT '',
  `invoice_created` datetime DEFAULT NULL
);

INSERT INTO payment_invoice (id, payment_id, reference, s3_key, `type`, invoice_guid, invoice_created)
VALUES(1, 1, 'ref1', '1', 1, 'guid', '2024-10-17 10:11:45');

CREATE TABLE `payments` (
  `id` int NOT NULL,
  `transactionId` varchar(255) NOT NULL,
  `type` int NOT NULL DEFAULT '1',
  `reference` varchar(255) NOT NULL,
  `amount` varchar(255) NOT NULL DEFAULT '0.00',
  `amountRefunded` varchar(255) NOT NULL DEFAULT '0.00',
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `migration` int NOT NULL DEFAULT '0',
  `carParkId` int DEFAULT NULL
);

INSERT INTO payments (id, transactionId, `type`, reference, amount, amountRefunded, created, migration, carParkId) 
VALUES(1, 'abc123', 1, '', '10.00', '0.00', '2024-10-17 10:11:45', 0, 0);

CREATE TABLE `company_subscription` (
  `id` int NOT NULL,
  `company_id` int NOT NULL,
  `sub_product_id` int NOT NULL
);

INSERT INTO company_subscription (id, company_id, sub_product_id) VALUES(1, 1, 1);