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

-- INSERT INTO lllc_app_role_mapping(role, permission_code, description)
-- VALUES
--
-- -- =====================================================
-- -- SUPER_ADMIN
-- -- Full system access with permission to manage all modules, users, roles, clients, staff, and configurations
-- -- =====================================================
--
--     ('SUPER_ADMIN', 'FULL_ACCESS', 'Full system access with permission to manage all modules'),
--     ('SUPER_ADMIN', 'VIEW_SYSTEM_ADMINISTRATION', 'View System Administration Sidebar'),
--
-- -- =====================================================
-- -- CASE_MANAGER
-- -- All except ADMINISTRATION
-- -- =====================================================
--
--     ('CASE_MANAGER', 'VIEW_CASE_MANAGEMENT', 'View Case Management Sidebar'),
--     ('CASE_MANAGER', 'VIEW_CLIENT_PROFILE', 'View client profiles'),
--     ('CASE_MANAGER', 'CREATE_CLIENT', 'Create client records'),
--     ('CASE_MANAGER', 'UPDATE_CLIENT', 'Update client information'),
--     ('CASE_MANAGER', 'VIEW_PROGRESS_REPORT', 'View client progress reports'),
--
--     ('CASE_MANAGER', 'VIEW_THERAPY_SESSION', 'View therapy sessions'),
--     ('CASE_MANAGER', 'CREATE_THERAPY_NOTE', 'Create therapy notes'),
--     ('CASE_MANAGER', 'UPDATE_TREATMENT_PLAN', 'Update treatment plans'),
--     ('CASE_MANAGER', 'MANAGE_THERAPY_SCHEDULE', 'Manage therapy schedules'),
--
--     ('CASE_MANAGER', 'VIEW_STAFF_DIRECTORY', 'View staff directory'),
--     ('CASE_MANAGER', 'MANAGE_STAFF_SCHEDULE', 'Manage staff schedules'),
--     ('CASE_MANAGER', 'MANAGE_LICENSE', 'Manage staff licenses'),
--
--     ('CASE_MANAGER', 'VIEW_CALENDAR_ASSESSMENT', 'View Client Calendar and Assessment'),
--     ('CASE_MANAGER', 'VIEW_CLIENT_ASSESSMENT_SCHEDULE', 'View Client Assessment Schedule'),
--     ('CASE_MANAGER', 'VIEW_CLIENT_SESSION_SCHEDULE', 'View Client Session Schedule'),
--     ('CASE_MANAGER', 'VIEW_NEURODEV_ASSESSMENT', 'View Neurodevelopmental Assessment'),
--
--     ('CASE_MANAGER', 'VIEW_INVOICE', 'View invoices'),
--     ('CASE_MANAGER', 'CREATE_INVOICE', 'Create invoices'),
--     ('CASE_MANAGER', 'RECEIVE_PAYMENT', 'Receive payments'),
--     ('CASE_MANAGER', 'MANAGE_INSURANCE_CLAIM', 'Manage insurance claims'),
--     ('CASE_MANAGER', 'MANAGE_EXPENSE', 'Manage expenses'),
--
--     ('CASE_MANAGER', 'ACCESS_FINANCE_MODULE', 'Access finance module'),
--     ('CASE_MANAGER', 'VIEW_FINANCE_DASHBOARD', 'View finance dashboard'),
--     ('CASE_MANAGER', 'VIEW_REVENUE', 'View revenue'),
--     ('CASE_MANAGER', 'MANAGE_BUDGET', 'Manage budget'),
--     ('CASE_MANAGER', 'VIEW_CASH_FLOW', 'View cash flow'),
--     ('CASE_MANAGER', 'MANAGE_PAYROLL', 'Manage payroll'),
--     ('CASE_MANAGER', 'VIEW_FINANCIAL_STATEMENT', 'View financial statements'),
--     ('CASE_MANAGER', 'VIEW_AUDIT_HISTORY', 'View audit history'),
--
-- -- =====================================================
-- -- THERAPIST
-- -- CLIENT + SCHEDULING limited access
-- -- =====================================================
--
--     -- Client Management
--     ('THERAPIST', 'VIEW_BEHAVIORAL_THERAPY', 'View Behavioral Therapy Sidebar'),
--     ('THERAPIST', 'VIEW_CLIENT_PROFILE', 'View client profiles'),
--     ('THERAPIST', 'VIEW_PROGRESS_REPORT', 'View client progress reports'),
--     ('THERAPIST', 'MANAGE_PROGRESS_REPORT', 'Manage client progress reports'),
--
--     -- Scheduling
--     ('THERAPIST', 'VIEW_CALENDAR', 'View calendar'),
--     ('THERAPIST', 'VIEW_SCHEDULE', 'View Schedule'),
--     ('THERAPIST', 'MANAGE_ATTENDANCE', 'Manage Attendance'),
--
-- -- =====================================================
-- -- SECRETARY
-- -- All except STAFF and ADMINISTRATION
-- -- =====================================================
--
--     -- Client Management
--     ('SECRETARY', 'DISPLAY_CLIENT_MANAGEMENT', 'View client management sidebar'),
--     ('SECRETARY', 'VIEW_CLIENT_PROFILE', 'View client profiles'),
--     ('SECRETARY', 'CREATE_CLIENT', 'Create client records'),
--     ('SECRETARY', 'UPDATE_CLIENT', 'Update client information'),
--
--     -- Scheduling
--     ('SECRETARY', 'DISPLAY_SCHEDULING', 'Display scheduling sidebar'),
--     ('SECRETARY', 'VIEW_CALENDAR', 'View calendar'),
--     ('SECRETARY', 'CREATE_CLIENT_ASSESSMENT_SCHEDULE', 'Create Client Assessment Schedule'),
--     ('SECRETARY', 'CREATE_CLIENT_SESSION_SCHEDULE', 'Create Client Session Schedule'),
--     ('SECRETARY', 'CREATE_NEURODEV_ASSESSMENT_SCHEDULE', 'Create Neurodevelopmental Assessment Schedule'),
--
--     -- Reports
--     ('SECRETARY', 'DISPLAY_REPORTS', 'Display report sidebar'),
--     ('SECRETARY', 'VIEW_FINANCIAL_REPORT', 'View financial report'),
--
--     -- Billing / SECRETARY
--     ('SECRETARY', 'DISPLAY_BILLING', 'Display billing sidebar'),
--     ('SECRETARY', 'MANAGE_PAYMENT', 'Manage payments'),
--     ('SECRETARY', 'MANAGE_EXPENSE', 'Manage expenses'),
--
--     -- Finance
--     ('SECRETARY', 'DISPLAY_FINANCE', 'Display finance sidebar'),
--     ('SECRETARY', 'ACCESS_FINANCE_MODULE', 'Access finance module'),
--     ('SECRETARY', 'VIEW_FINANCE_DASHBOARD', 'View finance dashboard'),
--     ('SECRETARY', 'VIEW_REVENUE', 'View revenue'),
--     ('SECRETARY', 'MANAGE_BUDGET', 'Manage budget'),
--     ('SECRETARY', 'VIEW_CASH_FLOW', 'View cash flow'),
--     ('SECRETARY', 'MANAGE_PAYROLL', 'Manage payroll'),
--     ('SECRETARY', 'VIEW_FINANCIAL_STATEMENT', 'View financial statements'),
--     ('SECRETARY', 'VIEW_AUDIT_HISTORY', 'View audit history'),
--
-- -- =====================================================
-- -- HR
-- -- STAFF + ADMINISTRATION + Payroll
-- -- =====================================================
--
--     ('HR', 'VIEW_HR_MANAGEMENT', 'View HR Management Sidebar'),
--     ('HR', 'VIEW_STAFF_DIRECTORY', 'View staff directory'),
--     ('HR', 'MANAGE_STAFF_SCHEDULE', 'Manage staff schedules'),
--     ('HR', 'MANAGE_LICENSE', 'Manage staff licenses'),
--
--     ('HR', 'MANAGE_USERS', 'Manage users'),
--     ('HR', 'MANAGE_ROLES', 'Manage roles'),
--     ('HR', 'MANAGE_BRANCH', 'Manage branches'),
--     ('HR', 'SYSTEM_CONFIGURATION', 'Manage system configuration'),
--
--     ('HR', 'MANAGE_PAYROLL', 'Manage payroll');