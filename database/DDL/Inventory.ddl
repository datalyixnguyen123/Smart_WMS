
CREATE TABLE INVENTORY_BALANCE(balance_id uuid NOT NULL, location_id uuid NOT NULL, sku_id uuid NOT NULL, lot_id uuid, lpn_id uuid, quantity_on_hand numeric(12, 4) DEFAULT 0 NOT NULL, quantity_allocated numeric(12, 4) DEFAULT 0 NOT NULL, quantity_picked numeric(12, 4) DEFAULT 0 NOT NULL, quantity_in_transit numeric(12, 4) DEFAULT 0 NOT NULL, quantity_hold numeric(12, 4) DEFAULT 0 NOT NULL, quantity_damaged numeric(12, 4) DEFAULT 0 NOT NULL, quantity_blocked numeric(12, 4) DEFAULT 0 NOT NULL, inventory_status varchar(30) NOT NULL DEFAULT 'AVAILABLE', received_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP, last_movement_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(50), updated_by varchar(50), version int4 NOT NULL DEFAULT 0, PRIMARY KEY (balance_id),
CONSTRAINT chk_balance_status CHECK (inventory_status IN ('AVAILABLE', 'HOLD', 'QC', 'DAMAGED', 'RETURN', 'BLOCKED', 'EXPIRED')),
CONSTRAINT chk_balance_states CHECK (quantity_on_hand >= quantity_allocated + quantity_picked + quantity_hold + quantity_damaged),
CONSTRAINT chk_balance_version CHECK (version >= 0),
CONSTRAINT chk_balance_rules CHECK (quantity_on_hand >= 0 AND quantity_allocated >= 0 AND quantity_picked >= 0 AND quantity_in_transit >= 0 AND quantity_hold >= 0 AND quantity_damaged >= 0 AND quantity_blocked >= 0),
CONSTRAINT chk_balance_quantity CHECK (quantity_on_hand >= quantity_allocated)
);
COMMENT ON COLUMN INVENTORY_BALANCE.quantity_on_hand IS ' The quantity of items located on the shelf at the moment.';
COMMENT ON COLUMN INVENTORY_BALANCE.quantity_allocated IS 'The quantity "locked" or reserved by the system for picking against outbound orders.';
COMMENT ON COLUMN INVENTORY_BALANCE.inventory_status IS 'Decide whether the items are permitted to be taken away for sale or exporting.';

CREATE TABLE INVENTORY_LOT(lot_id uuid NOT NULL, sku_id uuid NOT NULL, lot_number varchar(50) NOT NULL, manufacturing_date date, expiry_date date, PRIMARY KEY (lot_id));

CREATE TABLE LPN(lpn_id uuid NOT NULL, location_id uuid, lpn_code varchar(50) NOT NULL UNIQUE, lpn_type varchar(30) NOT NULL, lpn_status varchar(50) NOT NULL DEFAULT 'AVAILABLE', created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (lpn_id), CONSTRAINT chk_lpn_status CHECK (lpn_status IN ('AVAILABLE', 'ALLOCATED', 'STAGED', 'IN_TRANSIT')), CONSTRAINT chk_lpn_type CHECK (lpn_type IN ('PALLET', 'BOX', 'TOTE')));

CREATE TABLE LPN_DETAIL(lpn_detail_id uuid NOT NULL, sku_id uuid NOT NULL, lpn_id uuid NOT NULL, lot_id uuid NOT NULL, quantity_contained numeric(18, 4) NOT NULL, created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (lpn_detail_id), CONSTRAINT chk_lpn_detail_quantity CHECK (quantity_contained > 0));

CREATE TABLE INVENTORY_ADJUSTMENT(adjustment_id uuid NOT NULL, warehouse_id uuid NOT NULL, reference_id uuid, reason_id uuid NOT NULL, adjustment_code varchar(50) NOT NULL UNIQUE, adjustment_type varchar(50) NOT NULL, adjustment_status varchar(50) NOT NULL DEFAULT 'DRAFT', adjustment_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP, description text, reference_type varchar(30) NOT NULL, requested_by varchar(50) NOT NULL, approved_by varchar(50), approved_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(50), updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_by varchar(50), version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (adjustment_id), CONSTRAINT chk_adjustment_approved CHECK ((approved_by IS NULL AND approved_at IS NULL) OR (approved_by IS NOT NULL AND approved_at IS NOT NULL)),
CONSTRAINT chk_adjustment_reference CHECK ((reference_type IN ('CYCLE_COUNT', 'DAMAGE_REPORT', 'QA_INSPECTION', 'ERP_SYNC', 'NONE')) AND (CASE WHEN reference_type = 'NONE' THEN (reference_id IS NULL) ELSE (reference_id IS NOT NULL) END)),
CONSTRAINT chk_adjustment_status CHECK (adjustment_status IN ('DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'POSTED', 'CANCELLED')),
CONSTRAINT chk_adjustment_type CHECK(adjustment_type IN ('CYCLE_COUNT_DISCREPANCY', 'DAMAGE_SCRAP', 'THEFT_LOSS', 'ADMIN_CORRECTION'))
);
COMMENT ON COLUMN INVENTORY_ADJUSTMENT.requested_by IS 'Username of the person who discovered the inventory discrepancy and submitted the proposal.';
COMMENT ON COLUMN INVENTORY_ADJUSTMENT.approved_by IS 'Username of the warehouse manager or chief accountant who approved the requests';

CREATE TABLE INVENTORY_ADJUSTMENT_DETAIL(adjustment_detail_id uuid NOT NULL, adjustment_id uuid NOT NULL, location_id uuid NOT NULL, lot_id uuid NOT NULL, lpn_id uuid NOT NULL, quantity_before numeric(12, 4) DEFAULT 0 NOT NULL, quantity_after numeric(12, 4) DEFAULT 0 NOT NULL, adjusted_quantity numeric(12, 4) DEFAULT 0 NOT NULL, created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (adjustment_detail_id), CONSTRAINT chk_adjustment_detail_before_after CHECK (quantity_before >= 0 AND quantity_after >= 0), CONSTRAINT chk_adjusted_quantity CHECK (adjusted_quantity <> 0));
COMMENT ON COLUMN INVENTORY_ADJUSTMENT_DETAIL.adjusted_quantity IS 'adjusted_quantity = quantity_before - quantity_after';

CREATE TABLE CYCLE_COUNT(cycle_count_id uuid NOT NULL, warehouse_id uuid NOT NULL, cycle_count_code varchar(50) NOT NULL UNIQUE, count_type varchar(30) NOT NULL, count_status varchar(30) NOT NULL, scheduled_date date NOT NULL, started_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, completed_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, assigned_to varchar(100), approved_by varchar(100), approved_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, notes text, created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, created_by varchar(100) NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, updated_by varchar(100), version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (cycle_count_id), CONSTRAINT chk_cycle_count_count_type CHECK (count_type IN ('ABC_BASED', 'LOCATION_BASED', 'SKU_BASED', 'RANDOM', 'DISCREPANCY_TRIGGERED')), CONSTRAINT chk_cycle_count_count_status CHECK (count_status IN ('PLANNED', 'IN_PROGRESS', 'PENDING_APPROVAL', 'APPROVED', 'CANCELLED', 'COMPLETED')));
COMMENT ON COLUMN CYCLE_COUNT.assigned_to IS 'ID/Username of the worker assigned to conduct the inventory.';
COMMENT ON COLUMN CYCLE_COUNT.approved_by IS 'ID/Username of the manager approving the discrepancy.';

CREATE TABLE CYCLE_COUNT_DETAIL (detail_id uuid NOT NULL, cycle_count_id uuid NOT NULL, reason_id uuid, location_id uuid NOT NULL, sku_id uuid NOT NULL, lot_id uuid NOT NULL, lpn_id uuid NOT NULL, system_quantity numeric(18, 4) DEFAULT 0 NOT NULL, counted_quantity numeric(18, 4) DEFAULT 0, recounted_quantity numeric(18, 4) DEFAULT 0, variance_quantity numeric(18, 4) DEFAULT 0, system_quantity_snapshot_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, detail_status varchar(30) NOT NULL, counted_by varchar(100), counted_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (detail_id), CONSTRAINT chk_cycle_count_detail_status CHECK (detail_status IN ('PENDING', 'COUNTED', 'RECOUNT_REQUESTED', 'APPROVED')),
CONSTRAINT chk_cycle_count_detail_quantity CHECK (system_quantity >= 0 AND counted_quantity >= 0 AND recounted_quantity >= 0)
);
COMMENT ON COLUMN CYCLE_COUNT_DETAIL.system_quantity IS 'The quantity recorded by the WMS at the start of the stock-take, in order to compare with the quantity physically counted by staff.';
COMMENT ON COLUMN CYCLE_COUNT_DETAIL.system_quantity_snapshot_at IS 'System quantity snapshot time';
COMMENT ON COLUMN CYCLE_COUNT_DETAIL.counted_quantity IS 'the actual quantity counted by the staff during the first count';
COMMENT ON COLUMN CYCLE_COUNT_DETAIL.variance_quantity IS 'The variance quantity between the actual quantity and the system quantity(variance_quantity = actual_quantity - system_quantity)';

CREATE TABLE INVENTORY_RESERVATION(reservation_id uuid NOT NULL, warehouse_id uuid NOT NULL, location_id uuid, sku_id uuid NOT NULL, lot_id uuid, lpn_id uuid, reference_doc_id uuid NOT NULL, reservation_code varchar(50) NOT NULL, reservation_type varchar(50) NOT NULL, reservation_status varchar(50) NOT NULL DEFAULT 'ACTIVE', reserved_quantity numeric(12, 4) DEFAULT 0 NOT NULL, allocated_quantity numeric(12, 4) DEFAULT 0 NOT NULL, picked_quantity numeric(12, 4) DEFAULT 0 NOT NULL, reference_doc_type varchar(30) NOT NULL, reserved_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, expires_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, released_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, fulfilled_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(50) NOT NULL, updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_by varchar(50), version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (reservation_id),
CONSTRAINT chk_reserved_quantity CHECK (reserved_quantity >= 0 AND allocated_quantity >= 0 AND picked_quantity >= 0),
CONSTRAINT chk_reservation_reference_type CHECK (reference_doc_type IN ('SO', 'TO')),
CONSTRAINT chk_reservation_type CHECK((reservation_type IN ('CART_RESERVATION', 'PICKING_ALLOCATION', 'REPLENISHMENT_BLOCK', 'INTERNAL_QC_HOLD')) AND(CASE WHEN reservation_type = 'CART_RESERVATION' THEN(expires_at IS NOT NULL AND location_id IS NULL) WHEN reservation_type = 'PICKING_ALLOCATION' THEN (expires_at IS NULL AND location_id IS NOT NULL) WHEN reservation_type IN ('REPLENISHMENT_BLOCK', 'INTERNAL_QC_HOLD') THEN (location_id IS NOT NULL) ELSE TRUE END)),
CONSTRAINT chk_reservation_status CHECK (reservation_status IN ('ACTIVE', 'RELEASED', 'FULFILLED', 'CANCELLED', 'HOLD'))
);
COMMENT ON COLUMN INVENTORY_RESERVATION.location_id IS 'The specific bin/rack where the stock is held (set to NULL if held at the central warehouse level).';
COMMENT ON COLUMN INVENTORY_RESERVATION.reference_doc_id IS 'Order ID or warehouse transfer ID for reconciliation purposes.';
COMMENT ON COLUMN INVENTORY_RESERVATION.reserved_at IS 'The time at which the hold order takes effect.';
COMMENT ON COLUMN INVENTORY_RESERVATION.expires_at IS 'Stock reservation expiration time ';

CREATE TABLE DISCREPANCY_REASON(reason_id uuid NOT NULL, reason_code varchar(50) NOT NULL UNIQUE, reason_name varchar(100) NOT NULL, description text, is_active bool DEFAULT 'TRUE' NOT NULL, created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, version int4 NOT NULL DEFAULT 0, PRIMARY KEY (reason_id));

CREATE TABLE INVENTORY_SNAPSHOT(snapshot_id uuid NOT NULL, warehouse_id uuid NOT NULL, location_id uuid NOT NULL, sku_id uuid NOT NULL, lot_id uuid, lpn_id uuid, snapshot_date date NOT NULL, snapshot_timestamp timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, snapshot_type varchar(30) NOT NULL, quantity_on_hand numeric(18, 4) DEFAULT 0 NOT NULL, allocated_quantity numeric(18, 4) DEFAULT 0 NOT NULL, available_quantity numeric(18, 4) DEFAULT 0 NOT NULL, hold_quantity numeric(12, 4) DEFAULT 0 NOT NULL, damaged_quantity numeric(12, 4) DEFAULT 0 NOT NULL, created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (snapshot_id),
CONSTRAINT chk_inventory_snapshot_quantity CHECK ((quantity_on_hand >= 0) AND (allocated_quantity >= 0) AND (available_quantity >= 0) AND (hold_quantity >= 0) AND (damaged_quantity >= 0)),
CONSTRAINT chk_inventory_snapshot_type CHECK (snapshot_type IN ('DAILY_CLOSING', 'MONTH_END', 'YEAR_END', 'CYCLE_COUNT', 'STOCK_TAKE', 'SHIFT_CHANGE', 'ON_DEMAND', 'PRE_MIGRATION'))
);
COMMENT ON COLUMN INVENTORY_SNAPSHOT.quantity_on_hand IS 'the total physical quantity of inventory';
COMMENT ON COLUMN INVENTORY_SNAPSHOT.allocated_quantity IS 'The total on-hand quantity allocated to outbound demands.';
COMMENT ON COLUMN INVENTORY_SNAPSHOT.available_quantity IS 'quantity_available = quantity_on_hand - quantity_allocated - quantity_hold - quantity_damaged';
COMMENT ON COLUMN INVENTORY_SNAPSHOT.hold_quantity IS 'The quantity currently placed on quality hold or are currently under segregation/quarantine.';

CREATE TABLE INVENTORY_TRANSACTION(transaction_id uuid NOT NULL, sku_id uuid NOT NULL, lot_id uuid, lpn_id uuid, from_location_id uuid, to_location_id uuid, reason_id uuid, reference_doc_id uuid, operator_id uuid, transaction_code varchar(50) UNIQUE NOT NULL, transaction_type varchar(50) NOT NULL, transaction_subtype varchar(50) NOT NULL, quantity_change numeric(12, 4) NOT NULL, reference_doc_type varchar(30), transaction_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(50) NOT NULL, updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_by varchar(50) NOT NULL, remarks text, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (transaction_id),
CONSTRAINT chk_transaction_reference_doc_type CHECK (reference_doc_type IN ('PO', 'SO', 'TO', 'CC', 'RO')),
CONSTRAINT chk_transaction_subtype CHECK (transaction_subtype IN ('PURCHASE_RECEIPT', 'SHIPMENT', 'PICKING', 'PUTAWAY', 'MOVING', 'DAMAGE', 'LOST', 'FOUND', 'CYCLE_COUNT', 'RETURN_SUPPLIER', 'EXPIRED')),
CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('RECEIPT', 'ISSUE', 'TRANSFER', 'ADJUSTMENT', 'COUNT', 'RETURN', 'QC', 'REPLENISHMENT', 'HOLD', 'RELEASE')),
CONSTRAINT chk_transaction_quantity_change CHECK (quantity_change > 0),
CONSTRAINT chk_transaction_location_logic CHECK ((from_location_id IS NOT NULL OR to_location_id IS NOT NULL) AND (from_location_id <> to_location_id))
);
COMMENT ON COLUMN INVENTORY_TRANSACTION.reference_doc_type IS ' "PO" (Purchase Order), "SO" (Sales Order), "TO" (Transfer Order), "CC" (Cycle Count), "RO" (Return Order) ';
COMMENT ON COLUMN INVENTORY_TRANSACTION.reference_doc_id IS 'The ID of the goods receipt or goods issue note that generated the transaction (for trace logging purposes)';
COMMENT ON COLUMN INVENTORY_TRANSACTION.operator_id IS 'Tracking of who moved the pallet and who was responsible for the loss of goods.';
COMMENT ON COLUMN INVENTORY_TRANSACTION.reason_id IS 'For processing adjustment orders purposes';
COMMENT ON COLUMN INVENTORY_TRANSACTION.created_by IS 'Username or ID of the employee (or robot) executing the command.';
COMMENT ON COLUMN INVENTORY_TRANSACTION.remarks IS 'Additional notes';

CREATE TABLE INVENTORY_TRANSFER(transfer_id uuid NOT NULL, from_warehouse_id uuid NOT NULL, to_warehouse_id uuid NOT NULL, from_location_id uuid NOT NULL, to_location_id uuid NOT NULL, transfer_code varchar(50) NOT NULL UNIQUE, transfer_type varchar(50) NOT NULL, priority int4 DEFAULT 1 NOT NULL, transfer_status varchar(50) NOT NULL, scheduled_date date NOT NULL, started_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, completed_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, requested_by varchar(100) NOT NULL, assigned_to varchar(100), approved_by varchar(100), approved_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, notes text, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(100) NOT NULL, updated_by varchar(100), version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (transfer_id),
CONSTRAINT chk_transfer_location_logic CHECK (from_location_id <> to_location_id),
CONSTRAINT chk_transfer_priority CHECK (priority > 0),
CONSTRAINT chk_transfer_status CHECK (transfer_status IN ('DRAFT', 'PLANNED', 'RELEASED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
CONSTRAINT chk_transfer_type CHECK (transfer_type IN ('INTERNAL_LOCATION', 'INTER_WAREHOUSE', 'REPLENISHMENT')),
CONSTRAINT chk_transfer_approval_state CHECK ((CASE WHEN transfer_status IN('RELEASED', 'IN_PROGRESS', 'COMPLETED') THEN (approved_by IS NOT NULL AND approved_at IS NOT NULL) ELSE TRUE END))
);
COMMENT ON COLUMN INVENTORY_TRANSFER.priority IS ' 1="URGENT", 2="HIGH", 3="NORMAL", 4="LOW" ';

CREATE TABLE INVENTORY_TRANSFER_DETAIL(detail_id uuid NOT NULL, transfer_id uuid NOT NULL, from_location_id uuid NOT NULL, to_location_id uuid NOT NULL, sku_id uuid NOT NULL, lot_id uuid NOT NULL, from_lpn_id uuid, to_lpn_id uuid, requested_quantity numeric(18, 4) DEFAULT 0 NOT NULL, transferred_quantity numeric(18, 4) DEFAULT 0 NOT NULL, detail_status varchar(30) NOT NULL, notes text, transferred_by varchar(100), transferred_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY(detail_id),
CONSTRAINT chk_transfer_detail_location_logic CHECK (from_location_id <> to_location_id),
CONSTRAINT chk_transfer_detail_status CHECK(detail_status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'PARTIALLY_TRANSFERRED')),
CONSTRAINT chk_transfer_detail_quantity CHECK ((requested_quantity > 0) AND (transferred_quantity >= 0) AND (transferred_quantity <= requested_quantity)),
CONSTRAINT chk_transfer_detail_execution CHECK ((CASE WHEN detail_status = 'PENDING' THEN(transferred_by IS NULL AND transferred_at IS NULL) WHEN detail_status IN('IN_PROGRESS', 'PARTIALLY_TRANSFERRED', 'COMPLETED') THEN (transferred_by IS NOT NULL AND transferred_at IS NOT NULL) ELSE TRUE END))
);

CREATE TABLE REPLENISHMENT_RULE(rule_id uuid NOT NULL, sku_id uuid NOT NULL, source_zone_id uuid NOT NULL, target_zone_id uuid NOT NULL, target_location_id uuid NOT NULL, rule_code varchar(50) NOT NULL UNIQUE, rule_name varchar(100) NOT NULL, description text, replenishment_type varchar(30) DEFAULT 'MIN_MAX' NOT NULL, min_quantity numeric(12, 4) DEFAULT 0 NOT NULL, max_quantity numeric(12, 4) DEFAULT 0 NOT NULL, trigger_type varchar(30) NOT NULL, trigger_percentage numeric(5, 2) DEFAULT 0, target_percentage numeric(5, 2), priority int2 DEFAULT 1 NOT NULL, is_active bool DEFAULT 'TRUE' NOT NULL, created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (rule_id),
CONSTRAINT chk_replenish_priority CHECK (priority > 0),
CONSTRAINT chk_replenish_zone_logic CHECK (source_zone_id <> target_zone_id),
CONSTRAINT chk_replenish_quantity CHECK ((min_quantity > 0) AND (max_quantity > 0) AND (min_quantity <= max_quantity)),
CONSTRAINT chk_replenish_trigger_type CHECK (trigger_type IN('PERCENTAGE', 'MIN_QUANTITY')),
CONSTRAINT chk_replenish_trigger_percentage CHECK ((trigger_percentage > 0) AND (trigger_percentage <= 100)),
CONSTRAINT chk_replenish_type CHECK (replenishment_type IN('MIN_MAX', 'TOP_OFF', 'DEMAND_DRIVEN')),
CONSTRAINT chk_replenish_target_percentage CHECK ((target_percentage > 0) AND (target_percentage <= 100))
);

ALTER TABLE INVENTORY_BALANCE ADD CONSTRAINT "fk_balance_location_id" FOREIGN KEY(location_id) REFERENCES LOCATION(location_id);
ALTER TABLE INVENTORY_BALANCE ADD CONSTRAINT "fk_balance_sku_id" FOREIGN KEY(sku_id) REFERENCES SKU(sku_id);
ALTER TABLE INVENTORY_BALANCE ADD CONSTRAINT "fk_balance_lot_id" FOREIGN KEY(lot_id) REFERENCES INVENTORY_LOT(lot_id);
ALTER TABLE INVENTORY_BALANCE ADD CONSTRAINT "fk_balance_lpn_id" FOREIGN KEY (lpn_id) REFERENCES LPN(lpn_id);
ALTER TABLE INVENTORY_BALANCE ADD CONSTRAINT "uk_inventory_balance" UNIQUE NULLS NOT DISTINCT(location_id, sku_id, lot_id, lpn_id, inventory_status);

ALTER TABLE INVENTORY_LOT ADD CONSTRAINT "fk_lot_sku_id" FOREIGN KEY (sku_id) REFERENCES SKU(sku_id);
ALTER TABLE INVENTORY_LOT ADD CONSTRAINT "uk_lot_sku_number" UNIQUE(sku_id, lot_number);

ALTER TABLE LPN ADD CONSTRAINT "fk_lpn_location_id" FOREIGN KEY (location_id) REFERENCES LOCATION(location_id);

ALTER TABLE LPN_DETAIL ADD CONSTRAINT "fk_lpn_detail_lpn_id" FOREIGN KEY (lpn_id) REFERENCES LPN(lpn_id);
ALTER TABLE LPN_DETAIL ADD CONSTRAINT "fk_lpn_detail_lot_id" FOREIGN KEY(lot_id) REFERENCES INVENTORY_LOT(lot_id);
ALTER TABLE LPN_DETAIL ADD CONSTRAINT "fk_lpn_detail_sku_id" FOREIGN KEY(sku_id) REFERENCES SKU(sku_id);
ALTER TABLE LPN_DETAIL ADD CONSTRAINT "uk_lpn_detail_lpn_lot_sku" UNIQUE(lpn_id, lot_id, sku_id);

ALTER TABLE INVENTORY_ADJUSTMENT ADD CONSTRAINT "fk_adjustment_warehouse_id" FOREIGN KEY(warehouse_id) REFERENCES WAREHOUSE(warehouse_id);
ALTER TABLE INVENTORY_ADJUSTMENT ADD CONSTRAINT "fk_adjustment_reason_id" FOREIGN KEY(reason_id) REFERENCES DISCREPANCY_REASON(reason_id);

ALTER TABLE INVENTORY_ADJUSTMENT_DETAIL ADD CONSTRAINT "fk_adjustment_detail_adjustment_id" FOREIGN KEY(adjustment_id) REFERENCES INVENTORY_ADJUSTMENT(adjustment_id);
ALTER TABLE INVENTORY_ADJUSTMENT_DETAIL ADD CONSTRAINT "fk_adjustment_detail_lpn_id" FOREIGN KEY(lpn_id) REFERENCES LPN(lpn_id);
ALTER TABLE INVENTORY_ADJUSTMENT_DETAIL ADD CONSTRAINT "fk_adjustment_detail_lot_id" FOREIGN KEY(lot_id) REFERENCES INVENTORY_LOT(lot_id);
ALTER TABLE INVENTORY_ADJUSTMENT_DETAIL ADD CONSTRAINT "fk_adjustment_detail_location_id" FOREIGN KEY(location_id) REFERENCES LOCATION(location_id);

ALTER TABLE CYCLE_COUNT ADD CONSTRAINT "fk_cycle_count_warehouse_id" FOREIGN KEY(warehouse_id) REFERENCES WAREHOUSE(warehouse_id);

ALTER TABLE CYCLE_COUNT_DETAIL ADD CONSTRAINT "fk_cycle_count_detail_cycle_count_id" FOREIGN KEY(cycle_count_id) REFERENCES CYCLE_COUNT(cycle_count_id);
ALTER TABLE CYCLE_COUNT_DETAIL ADD CONSTRAINT "fk_cycle_count_detail_location_id" FOREIGN KEY(location_id) REFERENCES LOCATION(location_id);
ALTER TABLE CYCLE_COUNT_DETAIL ADD CONSTRAINT "fk_cycle_count_detail_sku_id" FOREIGN KEY(sku_id) REFERENCES SKU(sku_id);
ALTER TABLE CYCLE_COUNT_DETAIL ADD CONSTRAINT "fk_cycle_count_detail_lpn_id" FOREIGN KEY(lpn_id) REFERENCES LPN(lpn_id);
ALTER TABLE CYCLE_COUNT_DETAIL ADD CONSTRAINT "fk_cycle_count_detail_lot_id" FOREIGN KEY(lot_id) REFERENCES INVENTORY_LOT(lot_id);
ALTER TABLE CYCLE_COUNT_DETAIL ADD CONSTRAINT "fk_cycle_count_detail_reason_id" FOREIGN KEY(reason_id) REFERENCES DISCREPANCY_REASON(reason_id);

ALTER TABLE INVENTORY_RESERVATION ADD CONSTRAINT "fk_reservation_warehouse_id" FOREIGN KEY (warehouse_id) REFERENCES WAREHOUSE(warehouse_id);
ALTER TABLE INVENTORY_RESERVATION ADD CONSTRAINT "fk_reservation_location_id" FOREIGN KEY (location_id) REFERENCES LOCATION(location_id);
ALTER TABLE INVENTORY_RESERVATION ADD CONSTRAINT "fk_reservation_sku_id" FOREIGN KEY (sku_id) REFERENCES SKU(sku_id);
ALTER TABLE INVENTORY_RESERVATION ADD CONSTRAINT "fk_reservation_lot_id" FOREIGN KEY (lot_id) REFERENCES INVENTORY_LOT(lot_id);
ALTER TABLE INVENTORY_RESERVATION ADD CONSTRAINT "fk_reservation_lpn_id" FOREIGN KEY (lpn_id) REFERENCES LPN(lpn_id);

ALTER TABLE INVENTORY_SNAPSHOT ADD CONSTRAINT "fk_snapshot_location_id" FOREIGN KEY(location_id) REFERENCES LOCATION(location_id);
ALTER TABLE INVENTORY_SNAPSHOT ADD CONSTRAINT "fk_snapshot_warehouse_id" FOREIGN KEY(warehouse_id) REFERENCES WAREHOUSE(warehouse_id);
ALTER TABLE INVENTORY_SNAPSHOT ADD CONSTRAINT "fk_snapshot_sku_id" FOREIGN KEY(sku_id) REFERENCES SKU(sku_id);
ALTER TABLE INVENTORY_SNAPSHOT ADD CONSTRAINT "fk_snapshot_lpn_id" FOREIGN KEY(lpn_id) REFERENCES LPN(lpn_id);
ALTER TABLE INVENTORY_SNAPSHOT ADD CONSTRAINT "fk_snapshot_lot_id" FOREIGN KEY(lot_id) REFERENCES INVENTORY_LOT(lot_id);
ALTER TABLE INVENTORY_SNAPSHOT ADD CONSTRAINT "uk_inventory_snapshot" UNIQUE NULLS NOT DISTINCT(sku_id, lot_id, lpn_id, location_id, snapshot_date, snapshot_type);

ALTER TABLE INVENTORY_TRANSACTION ADD CONSTRAINT "fk_transaction_sku_id" FOREIGN KEY(sku_id) REFERENCES SKU(sku_id);
ALTER TABLE INVENTORY_TRANSACTION ADD CONSTRAINT "fk_transaction_from_location_id" FOREIGN KEY(from_location_id) REFERENCES LOCATION(location_id);
ALTER TABLE INVENTORY_TRANSACTION ADD CONSTRAINT "fk_transaction_to_location_id" FOREIGN KEY(to_location_id) REFERENCES LOCATION(location_id);
ALTER TABLE INVENTORY_TRANSACTION ADD CONSTRAINT "fk_transaction_lpn_id" FOREIGN KEY(lpn_id) REFERENCES LPN(lpn_id);
ALTER TABLE INVENTORY_TRANSACTION ADD CONSTRAINT "fk_transaction_lot_id" FOREIGN KEY(lot_id) REFERENCES INVENTORY_LOT(lot_id);

ALTER TABLE INVENTORY_TRANSFER ADD CONSTRAINT "fk_transfer_from_warehouse_id" FOREIGN KEY(from_warehouse_id) REFERENCES WAREHOUSE(warehouse_id);
ALTER TABLE INVENTORY_TRANSFER ADD CONSTRAINT "fk_transfer_to_warehouse_id" FOREIGN KEY(to_warehouse_id) REFERENCES WAREHOUSE(warehouse_id);
ALTER TABLE INVENTORY_TRANSFER ADD CONSTRAINT "fk_transfer_from_location_id" FOREIGN KEY(from_location_id) REFERENCES LOCATION(location_id);
ALTER TABLE INVENTORY_TRANSFER ADD CONSTRAINT "fk_transfer_to_location_id" FOREIGN KEY(to_location_id) REFERENCES LOCATION(location_id);

ALTER TABLE INVENTORY_TRANSFER_DETAIL ADD CONSTRAINT "fk_transfer_detail_transfer_id" FOREIGN KEY(transfer_id) REFERENCES INVENTORY_TRANSFER(transfer_id);
ALTER TABLE INVENTORY_TRANSFER_DETAIL ADD CONSTRAINT "fk_transfer_detail_from_lpn_id" FOREIGN KEY(from_lpn_id) REFERENCES LPN(lpn_id);
ALTER TABLE INVENTORY_TRANSFER_DETAIL ADD CONSTRAINT "fk_transfer_detail_to_lpn_id" FOREIGN KEY(to_LPN_id) REFERENCES LPN(lpn_id);
ALTER TABLE INVENTORY_TRANSFER_DETAIL ADD CONSTRAINT "fk_transfer_detail_lot_id" FOREIGN KEY(lot_id) REFERENCES INVENTORY_LOT(lot_id);
ALTER TABLE INVENTORY_TRANSFER_DETAIL ADD CONSTRAINT "fk_transfer_detail_sku_id" FOREIGN KEY(sku_id) REFERENCES SKU(sku_id);
ALTER TABLE INVENTORY_TRANSFER_DETAIL ADD CONSTRAINT "fk_transfer_detail_from_location_id" FOREIGN KEY(from_location_id) REFERENCES LOCATION(location_id);
ALTER TABLE INVENTORY_TRANSFER_DETAIL ADD CONSTRAINT "fk_transfer_detail_to_location_id" FOREIGN KEY(to_location_id) REFERENCES LOCATION(location_id);

ALTER TABLE REPLENISHMENT_RULE ADD CONSTRAINT "fk_replenish_sku_id" FOREIGN KEY(sku_id) REFERENCES SKU(sku_id);
ALTER TABLE REPLENISHMENT_RULE ADD CONSTRAINT "fk_replenish_target_location_id" FOREIGN KEY(target_location_id) REFERENCES LOCATION(location_id);
ALTER TABLE REPLENISHMENT_RULE ADD CONSTRAINT "fk_replenish_target_zone_id" FOREIGN KEY(target_zone_id) REFERENCES ZONE(zone_id);
ALTER TABLE REPLENISHMENT_RULE ADD CONSTRAINT "fk_replenish_source_zone_id" FOREIGN KEY (source_zone_id) REFERENCES ZONE(zone_id);


CREATE INDEX "idx_inventory_balance_sku" ON INVENTORY_BALANCE(sku_id);
CREATE INDEX "idx_inventory_balance_location" ON INVENTORY_BALANCE(location_id);

CREATE INDEX "idx_inventory_balance_picking" ON INVENTORY_BALANCE(sku_id, location_id)
INCLUDE (quantity_on_hand, quantity_allocated)
WHERE inventory_status = 'AVAILABLE'
AND (quantity_on_hand - quantity_allocated) > 0;

CREATE INDEX "idx_inventory_balance_allocation" ON INVENTORY_BALANCE(sku_id)
INCLUDE(location_id, lot_id, quantity_on_hand, quantity_allocated, received_date)
WHERE inventory_status = 'AVAILABLE'
AND (quantity_on_hand - quantity_allocated) > 0;

CREATE INDEX "idx_inventory_reservation_active" ON INVENTORY_RESERVATION(sku_id, location_id)
WHERE reservation_status = 'ACTIVE';

CREATE INDEX "idx_inventory_transaction_sku_time" ON INVENTORY_TRANSACTION(sku_id, transaction_time DESC);
CREATE INDEX "idx_inventory_transaction_time_brin" ON INVENTORY_TRANSACTION
USING BRIN(transaction_time);

CREATE INDEX "idx_inventory_transaction_from_location" ON INVENTORY_TRANSACTION(from_location_id, transaction_time DESC)
WHERE from_location_id IS NOT NULL;

CREATE INDEX "idx_inventory_transaction_to_location" ON INVENTORY_TRANSACTION(to_location_id, transaction_time DESC)
WHERE to_location_id IS NOT NULL;

CREATE INDEX "idx_inventory_transaction_lot_time" ON INVENTORY_TRANSACTION(lot_id, transaction_time DESC)
WHERE lot_id IS NOT NULL;

CREATE INDEX "idx_inventory_adjustment_detail_adjustment" ON INVENTORY_ADJUSTMENT_DETAIL(adjustment_id);

CREATE INDEX "idx_cycle_count_detail_header" ON CYCLE_COUNT_DETAIL(cycle_count_id);

CREATE INDEX "idx_inventory_warehouse_snapshot_date" ON INVENTORY_SNAPSHOT(warehouse_id, snapshot_date);

CREATE INDEX "idx_inventory_reservation_ref_active" ON INVENTORY_RESERVATION(reference_doc_id)
WHERE reservation_status = 'ACTIVE';

CREATE INDEX "idx_inventory_transfer_detail_transfer" ON INVENTORY_TRANSFER_DETAIL(transfer_id);









