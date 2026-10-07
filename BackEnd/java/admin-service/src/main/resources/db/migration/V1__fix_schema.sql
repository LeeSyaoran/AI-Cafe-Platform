-- Fix database schema for admin-service
-- This migration adds default values to existing columns

-- Add default values for promotions table
ALTER TABLE promotions ALTER COLUMN company_id SET DEFAULT gen_random_uuid();
UPDATE promotions SET company_id = COALESCE(company_id, gen_random_uuid()) WHERE company_id IS NULL;
ALTER TABLE promotions ALTER COLUMN company_id SET NOT NULL;

ALTER TABLE promotions ALTER COLUMN type SET DEFAULT 'percentage';
UPDATE promotions SET type = COALESCE(type, 'percentage') WHERE type IS NULL;
ALTER TABLE promotions ALTER COLUMN type SET NOT NULL;

-- Add default values for rewards table
ALTER TABLE rewards ALTER COLUMN category SET DEFAULT 'discount';
UPDATE rewards SET category = COALESCE(category, 'discount') WHERE category IS NULL;
ALTER TABLE rewards ALTER COLUMN category SET NOT NULL;

ALTER TABLE rewards ALTER COLUMN company_id SET DEFAULT gen_random_uuid();
UPDATE rewards SET company_id = COALESCE(company_id, gen_random_uuid()) WHERE company_id IS NULL;
ALTER TABLE rewards ALTER COLUMN company_id SET NOT NULL;
