ALTER TABLE auth.refresh_token
    ALTER COLUMN token TYPE TEXT;

ALTER TABLE auth.refresh_token
    ALTER COLUMN jti TYPE TEXT;
