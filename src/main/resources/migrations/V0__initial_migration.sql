CREATE SCHEMA IF NOT EXISTS translation;

CREATE TABLE IF NOT EXISTS translation.translation_requests (
    id                  UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    user_ip             text,
    source_language     text,
    target_language     text,
    input               text,
    result              text
);