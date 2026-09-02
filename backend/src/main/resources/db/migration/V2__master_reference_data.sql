
-- Date created: 2026-08-15
-- Author: Dat Nguyen

INSERT INTO master.Unit_Of_Measure(uom_id, uom_code, uom_name, uom_type, symbol, decimal_places)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'PCS', 'Piece', 'QUANTITY', 'pcs', 0),
    ('10000000-0000-0000-0000-000000000002', 'BOX', 'Box', 'QUANTITY', 'box', 0),
    ('10000000-0000-0000-0000-000000000003', 'CTN', 'Carton', 'QUANTITY', 'ctn', 0),
    ('10000000-0000-0000-0000-000000000004', 'PLT', 'Pallet', 'QUANTITY', 'plt', 0),
    ('10000000-0000-0000-0000-000000000005', 'PAK', 'Pack', 'QUANTITY', 'pck', 0),

    ('10000000-0000-0000-0000-000000000010', 'KG', 'Kilogram', 'WEIGHT', 'kg', 3),
    ('10000000-0000-0000-0000-000000000011', 'G', 'Gram', 'WEIGHT', 'g', 0),
    ('10000000-0000-0000-0000-000000000012', 'TON', 'Metric Ton', 'WEIGHT', 't', 3),

    ('10000000-0000-0000-0000-000000000020', 'CBM', 'Cubic Meter', 'VOLUME', 'm3', 3),
    ('10000000-0000-0000-0000-000000000021', 'L', 'Liter', 'VOLUME', 'l', 2),

    ('10000000-0000-0000-0000-000000000030', 'M', 'Meter', 'LENGTH', 'm', 3),
    ('10000000-0000-0000-0000-000000000031', 'CM', 'Centimeter', 'LENGTH', 'cm', 2),
    ('10000000-0000-0000-0000-000000000032', 'MM', 'Millimeter', 'LENGTH', 'mm', 0)

ON CONFLICT (uom_id) DO NOTHING;

-- Lower value indicates higher operational priority
INSERT INTO master.ABC_CLASS(abc_code, display_name, description, min_percentage, max_percentage, priority, color_code, cycle_count_frequency_days, target_service_level, max_pick_distance)
VALUES
      (
          'CLASS_A', 'Class A - High Value Contribution', 'Items contributing to the top 80% of cumulative annual consumption value. Prioritize storage location and conduct frequent inventory cycle counts.',
          0.00, 80.00, 1, '#FF4D4F',
          30, 99.00, 50
      ),
      (
          'CLASS_B', 'Class B - Medium Value', 'Items contributing to 80%-95% of cumulative transaction value.',
          80.00, 95.00, 2, '#FFA940',
          90, 95.00, 150
      ),
      (
          'CLASS_C', 'Class C - Low Value/Slow Moving', 'Items contributing to the remaining 5% of cumulative transaction value.',
          95.00, 100.00, 3, '#73D13D',
          180, 90.00, 300
      )
    ON CONFLICT (abc_code) DO NOTHING;


INSERT INTO master.STORAGE_CONSTRAINT(constraint_id, constraint_code, constraint_name, storage_type, temp_min, temp_max, humidity_max)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'DRY_STD', 'Dry goods (ambient temperature)', 'RACK', NULL, 30.0, 70.0),
    ('20000000-0000-0000-0000-000000000002', 'COOL_TEMP', 'Temperature-controlled goods', 'RACK', 15.0, 25.0, NULL),
    ('20000000-0000-0000-0000-000000000003', 'COLD_FROZEN', 'Frozen goods', 'FLOOR', -25.0, -18.0, NULL),
    ('20000000-0000-0000-0000-000000000004', 'HAZMAT', 'Chemicals / Hazardous goods', 'HAZMAT_RACK', NULL, NULL, NULL),
    ('20000000-0000-0000-0000-000000000005', 'HIGH_VALUE', 'High-value goods', 'SECURE_VAULT', NULL, NULL, NULL),
    ('20000000-0000-0000-0000-000000000006', 'BULK_HEAVY', 'Bulky or heavy goods', 'FLOOR', NULL, NULL, NULL)
ON CONFLICT (constraint_id) DO NOTHING;


-- The 90/85/80 is default max utilization
INSERT INTO master.STORAGE_POLICY (policy_id, policy_code, policy_name, picking_strategy, putaway_strategy, max_utilization, description, is_active, created_at, updated_at, version)
VALUES
    ('30000000-0000-0000-0000-000000000001', 'FIFO_STANDARD', 'Standard (First-In, First-Out)', 'FIFO', 'FIXED_LOCATION', 90.00, 'Standard FIFO picking policy for inventory without expiry requirements.',TRUE,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('30000000-0000-0000-0000-000000000002', 'FEFO_EXPIRY', 'Shortest Expiry First (Food/Pharmaceuticals)', 'FEFO', 'FIXED_LOCATION', 85.00, 'FEFO picking policy for products with expiry dates such as food, pharmaceuticals, etc.',TRUE,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('30000000-0000-0000-0000-000000000003', 'LIFO_STANDARD', 'Standard(Last-In, First-Out)', 'LIFO', 'FIXED_LOCATION', 80.00, 'LIFO picking policy for inventory where lastest received stock should be picked first.',TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('30000000-0000-0000-0000-000000000004', 'DYNAMIC_SLOT', 'Dynamic position coordination', 'FIFO', 'EMPTY_LOCATION', 85.00, 'Dynamic putaway policy that assigns suitable available storage locations.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
    ON CONFLICT (policy_id) DO NOTHING;

INSERT INTO master.VELOCITY_CLASS (velocity_code, display_name, description, min_daily_pick, max_daily_pick, min_monthly_pick, priority, color_code, replenishment_trigger_percentage)
VALUES
    ('FAST', 'Fast-moving inventory', 'SKUs with high outbound frequency. Prioritize accessibility and replenishment capabilities.', 20, NULL, 600, 1,'#FF4D4F', 70.00),
    ('MEDIUM', 'Medium-moving inventory', 'SKUs with medium outbound frequency.', 5, NULL, 100, 2,'#FFA940', 60.00),
    ('SLOW', 'Slow-moving inventory', 'SKUs with low outbound frequency. No need to prioritize accessibility and replenishment capabilities.', 1, 4, 30, 3,'#73D13D', 40.00),
    ('NON_MOVING', 'Non-moving inventory', 'SKUs with no or negligible inventory issue transactions during the evaluation period.', 0, 0, 0, 4,'#BFBFBF', NULL)
ON CONFLICT (velocity_code) DO NOTHING;









