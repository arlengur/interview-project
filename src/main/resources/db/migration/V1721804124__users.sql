
-- Migration: users
-- Created at: 2024-07-24 16:55:24

BEGIN;

CREATE TABLE  "user"(
    id BIGSERIAL PRIMARY KEY NOT NULL , 
    "name" VARCHAR NOT NULL,
    title VARCHAR NOT NULL
);

COMMIT;
 
    
    