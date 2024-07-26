
-- Migration: docs
-- Created at: 2024-07-25 16:55:24

BEGIN;

CREATE TABLE  "document"(
    id BIGSERIAL PRIMARY KEY NOT NULL , 
    title VARCHAR NOT NULL,
    body VARCHAR NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    updatedAt TIMESTAMP NOT NULL,
    authorId BIGINT NOT NULL FOREIGN KEY REFERENCES USER
);

COMMIT;
 
    
    