--Version:1
CREATE TABLE gym (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	uuid VARCHAR,
	name VARCHAR,
	lat VARCHAR,
	lon VARCHAR,
	datetime TIMESTAMP DEFAULT (datetime('now'))
);
--
CREATE TABLE user (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	uuid VARCHAR,
	name VARCHAR,
	password VARCHAR,
	email VARCHAR,
	gym VARCHAR,
	datetime TIMESTAMP DEFAULT (datetime('now'))
);
--
CREATE TABLE rute (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	uuid VARCHAR,
	name VARCHAR,
	coordinates VARCHAR,
	image_url VARCHAR,
	author VARCHAR,
	gym VARCHAR,
	datetime TIMESTAMP DEFAULT (datetime('now'))
);
--
CREATE TABLE rating (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	uuid VARCHAR,
	rating VARCHAR,
	author VARCHAR,
	rute VARCHAR,
	datetime TIMESTAMP DEFAULT (datetime('now'))
);
--
CREATE TABLE comment (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	uuid VARCHAR,
	text VARCHAR,
	author VARCHAR,
	rute VARCHAR,
	datetime TIMESTAMP DEFAULT (datetime('now'))
);
--
INSERT INTO gym (uuid, name, lat, lon) VALUES ("john", 'ÅK', 0.0, 0.0);
--
INSERT INTO user (uuid, name, password, email, gym) VALUES ("hansi", 'Jens', 'pass', 'email@email.com', "john");
--
