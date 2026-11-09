CREATE TABLE IF NOT EXISTS Application(
    path VARCHAR PRIMARY KEY,
    product_name VARCHAR, 
    tracked BOOLEAN
);

CREATE TABLE IF NOT EXISTS Category(
    name VARCHAR PRIMARY KEY, 
    colour VARCHAR,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS ApplicationHasCategory(
    path VARCHAR,
    category VARCHAR,
    deleted_at TIMESTAMP WITH TIME ZONE,
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
    start_time TIMESTAMP WITH TIME ZONE,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (path, category, start_time),
    FOREIGN KEY (path, category) REFERENCES ApplicationHasCategory(path, category)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);