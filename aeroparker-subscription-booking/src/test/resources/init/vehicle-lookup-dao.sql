CREATE TABLE `vehiclelookup_affiliate_logins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `affiliate` int NOT NULL,
  `lookup_service` int NOT NULL,
  `unrec_plate_validation` bit(1) NOT NULL DEFAULT b'0',
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `lookup_mode` varchar(45) DEFAULT 'AUTO',
  `isActive` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
);

INSERT INTO vehiclelookup_affiliate_logins (id, affiliate, lookup_service, unrec_plate_validation, username, password, url, lookup_mode, isActive) VALUES(1, 211, 1, 0, '', '', '', 'AUTO', 0);
INSERT INTO vehiclelookup_affiliate_logins (id, affiliate, lookup_service, unrec_plate_validation, username, password, url, lookup_mode, isActive) VALUES(2, 211, 2, 1, 'username', 'password', 'url', 'AUTO', 1);