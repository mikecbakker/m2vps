USE autobv;

DROP TABLE scraper_autotrader_data;
CREATE TABLE scraper_autotrader_data (
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	created DATETIME DEFAULT CURRENT_TIMESTAMP,
	html VARCHAR(20000) DEFAULT NULL,
	web_link TEXT DEFAULT NULL,
	province VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (id)
);

DROP TABLE scraper_cars_data;
CREATE TABLE scraper_cars_data (
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	created DATETIME DEFAULT CURRENT_TIMESTAMP,
	html VARCHAR(20000) DEFAULT NULL,
	web_link TEXT DEFAULT NULL,
  PRIMARY KEY (id)
);

DROP TABLE scraper_automart_data;
CREATE TABLE scraper_automart_data (
	id BIGINT(20) NOT NULL AUTO_INCREMENT,
	created DATETIME DEFAULT CURRENT_TIMESTAMP,
	html VARCHAR(20000) DEFAULT NULL,
	web_link TEXT DEFAULT NULL,
  PRIMARY KEY (id)
);

DROP TABLE normalize_vehicle_data;
CREATE TABLE normalize_vehicle_data (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  source_entity VARCHAR(50) NOT NULL,
  source_entity_id BIGINT(20) NOT NULL,
  manufacturer VARCHAR(50) NOT NULL,
  model VARCHAR(50) NOT NULL,
  sub_model VARCHAR(50) NOT NULL,
  year VARCHAR(4) NOT NULL,
  mileage BIGINT(20) NOT NULL,
  price double NOT NULL,
  weblink VARCHAR(255) NOT NULL,
  province VARCHAR(50) NOT NULL,
  expired boolean DEFAULT 0,
  duplicate boolean DEFAULT 0,
  outlier boolean DEFAULT 0,
  last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

DROP TABLE normalize_session_info;
CREATE TABLE normalize_session_info (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  scraper_entity VARCHAR(5),
  last_entity_id BIGINT(20) NOT NULL,
  last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);




















