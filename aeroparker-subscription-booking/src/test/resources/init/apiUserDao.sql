CREATE TABLE `api_user` (
  `id` int NOT NULL,
  `aeroparker` bit(1) NOT NULL DEFAULT b'0',
  `site_id` int DEFAULT NULL,
  `all_sites` bit(1) NOT NULL DEFAULT b'0',
  `name` varchar(255) NOT NULL,
  `key` char(36) NOT NULL,
  `secret` char(36) NOT NULL,
  `schema` varchar(255) NOT NULL,
  `barrier_client_endpoint` varchar(500) NOT NULL DEFAULT '',
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `reports_default_limit` int NOT NULL DEFAULT '25',
  `reports_maximum_limit` int NOT NULL DEFAULT '50'
);

INSERT INTO api_user (id, aeroparker, site_id, all_sites, name, `key`, secret, `schema`, barrier_client_endpoint, created, modified, username, password, reports_default_limit, reports_maximum_limit) 
VALUES(1, 1, 1, 1, 'name', 'key', 'secret', 'schema', 'abc', '2024-01-29 13:38:27', '2024-01-29 13:38:27', 'username', 'password', 25, 50);