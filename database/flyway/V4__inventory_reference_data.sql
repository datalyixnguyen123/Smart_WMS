
-- Date created: 2026-08-31
-- Author: Dat Nguyen

INSERT INTO DISCREPANCY_REASON (reason_id, reason_code, reason_name, description, is_active)
VALUES
    (gen_random_uuid(), 'RC_DAMAGE_TRANSIT', 'Damage In Transit', 'Stock damaged during internal transfer or material handling.', TRUE),
    (gen_random_uuid(), 'RC_EXPIRED_STOCK', 'Expired Stock', 'Product reached shelf life expiration date and is no longer sellable.', TRUE),
    (gen_random_uuid(), 'RC_MISCOUNT_SURPLUS', 'Physical Surplus', 'Physical count exceeds system recorded quantity during cycle count.', TRUE),
    (gen_random_uuid(), 'RC_MISCOUNT_DEFICIT', 'Physical Deficit', 'Physical count is less than system recorded quantity during cycle count.', TRUE),
    (gen_random_uuid(), 'RC_QC_REJECT', 'Quality Control Failure', 'Failed inbound inspection or routine quality assessment.', TRUE),
    (gen_random_uuid(), 'RC_UNEXPLAINED_SHRINKAGE', 'Unexplained Shrinkage', 'Unexplained inventory loss during audit.', TRUE),
    (gen_random_uuid(), 'RC_ADMIN_CORRECTION', 'Administrative Error', 'Manual system adjustment due to data entry mistakes.', TRUE),
    (gen_random_uuid(), 'RC_SUPPLIER_SHORTAGE', 'Supplier Shortage', 'Quantity received is less than the quantity expected from the suppliers shipment documents.', TRUE)
ON CONFLICT (reason_code) DO NOTHING;











