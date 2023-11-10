ALTER TABLE telegram_user
    ADD COLUMN IF NOT EXISTS status TEXT DEFAULT 'WAITING_FOR_APPROVE',
    ADD COLUMN IF NOT EXISTS role TEXT DEFAULT 'USER';

ALTER TABLE telegram_user
    RENAME COLUMN bot_state TO state;