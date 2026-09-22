CREATE TABLE IF NOT EXISTS Application(
    path VARCHAR PRIMARY KEY,
    product_name VARCHAR, 
    tracked BOOLEAN
);

CREATE TABLE IF NOT EXISTS Category(
    name VARCHAR PRIMARY KEY, 
    colour VARCHAR
);

CREATE TABLE IF NOT EXISTS ApplicationHasCategory(
    path VARCHAR,
    category VARCHAR,
    PRIMARY KEY (path, category),
    FOREIGN KEY (path) REFERENCES Application(path)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (category) REFERENCES Category(name)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS ApplicationCategoryUsage(
    path VARCHAR,
    category VARCHAR,
    start_time TIMESTAMP,
    end_time TIMESTAMP NOT NULL,
    PRIMARY KEY (path, category, start_time),
    FOREIGN KEY (path, category) REFERENCES ApplicationHasCategory(path, category)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);