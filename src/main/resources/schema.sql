CREATE SCHEMA IF NOT EXISTS little_leap_learning_center_db;

CREATE SEQUENCE IF NOT EXISTS little_leap_learning_center_db.app_user_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS little_leap_learning_center_db.app_user_client_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS little_leap_learning_center_db.payroll_id_seq
    START WITH 1
    INCREMENT BY 1;

-- Remove GRANT statements or only execute manually if needed