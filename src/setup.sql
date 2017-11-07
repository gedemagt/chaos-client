--Version:1
CREATE TABLE rutes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR,
    image_url VARCHAR,
    points VARCHAR,
    creator VARCHAR,
    location INTEGER,
    FOREIGN KEY(location) REFERENCES locations(id),
    FOREIGN KEY(creator) REFERENCES users(id)
);

CREATE TABLE locations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR,
    lat VARCHAR,
    lon VARCHAR
);

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR
);
--
