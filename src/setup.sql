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
CREATE TABLE image (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	uuid VARCHAR,
	url VARCHAR,
	datetime TIMESTAMP DEFAULT (datetime('now'))
);
--
CREATE TABLE rute (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	uuid VARCHAR,
	name VARCHAR,
	coordinates VARCHAR,
	image VARCHAR,
	author VARCHAR,
	gym VARCHAR,
  grade VARCHAR,
	datetime TIMESTAMP DEFAULT (datetime('now')),
	edit TIMESTAMP DEFAULT (datetime('now'))
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
