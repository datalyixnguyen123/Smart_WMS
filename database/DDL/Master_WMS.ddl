

CREATE TABLE WAREHOUSE (warehouse_id uuid NOT NULL, warehouse_code varchar(50) NOT NULL UNIQUE, warehouse_name varchar(255) NOT NULL, warehouse_type varchar(30) NOT NULL, warehouse_address text, warehouse_status varchar(30) DEFAULT 'ACTIVE' NOT NULL, timezone varchar(30) NOT NULL, total_area numeric(12,2), capacity_volume numeric(12, 2), latitude numeric(9,6), longitude numeric(9,6), operating_start_time time, operating_end_time time, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (warehouse_id), CONSTRAINT chk_WAREHOUSE_version CHECK (version >= 0), CONSTRAINT chk_WAREHOUSE_dimension CHECK ((total_area IS NULL OR total_area >= 0) AND (capacity_volume IS NULL OR capacity_volume >= 0)), CONSTRAINT chk_WAREHOUSE_warehouse_type CHECK (warehouse_type IN ('DRY', 'COLD', 'BONDED', 'CROSS_DOCK', 'HAZMAT')), CONSTRAINT chk_WAREHOUSE_warehouse_status CHECK (warehouse_status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')), CONSTRAINT chk_WAREHOUSE_latitude CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90), CONSTRAINT chk_WAREHOUSE_longitude CHECK(longitude IS NULL OR longitude BETWEEN -180 AND 180), CONSTRAINT chk_WAREHOUSE_operating_time CHECK(operating_start_time IS NULL OR operating_end_time IS NULL OR operating_start_time < operating_end_time));
COMMENT ON COLUMN WAREHOUSE.warehouse_address IS 'physical warehouse address';
COMMENT ON COLUMN WAREHOUSE.warehouse_status IS 'ACTIVE, INACTIVE, MAINTENANCE';
COMMENT ON COLUMN WAREHOUSE.timezone IS 'operation timezone(default: UTC)';
COMMENT ON COLUMN WAREHOUSE.total_area IS 'total floor area of the warehouse(unit: m^2)';
COMMENT ON COLUMN WAREHOUSE.capacity_volume IS 'total max volume of warehouse(unit: m^3)';
COMMENT ON COLUMN WAREHOUSE.created_at IS 'the time the warehouse was created on the system';
COMMENT ON COLUMN WAREHOUSE.updated_at IS 'recent updated timestamp';
COMMENT ON COLUMN WAREHOUSE.version IS 'Prevents configuration data conflicts in the repository';

CREATE TABLE ZONE (zone_id uuid NOT NULL, warehouse_id uuid NOT NULL, constraint_id uuid NOT NULL, zone_code varchar(50) NOT NULL, zone_name varchar(255) NOT NULL, zone_type varchar(30) NOT NULL, zone_status varchar(30) DEFAULT 'ACTIVE' NOT NULL, temperature_type varchar(50) DEFAULT 'AMBIENT' NOT NULL, default_temperature numeric(5,2), allow_mixing bool DEFAULT FALSE NOT NULL, priority int2 DEFAULT 1, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (zone_id), CONSTRAINT chk_ZONE_version CHECK (version >= 0), CONSTRAINT chk_ZONE_priority CHECK (priority > 0), CONSTRAINT chk_ZONE_zone_type CHECK (zone_type IN ('RECEIVING', 'STORAGE', 'PICKING', 'PACKING', 'SHIPPING', 'RETURN')), CONSTRAINT chk_ZONE_temperature_type CHECK (temperature_type IN ('AMBIENT', 'CHILLED', 'FROZEN', 'DEEP_FROZEN', 'CONTROLLED')), CONSTRAINT chk_ZONE_zone_status CHECK (zone_status IN ('ACTIVE', 'LOCKED')));
COMMENT ON COLUMN ZONE.zone_status IS 'ACTIVE, LOCKED';
COMMENT ON COLUMN ZONE.version IS 'Prevents configuration data conflicts in the repository';


CREATE TABLE EQUIPMENT (equip_id uuid NOT NULL, zone_id uuid NOT NULL, equip_code varchar(50) NOT NULL, equip_type varchar(50) NOT NULL, equip_name varchar(255) NOT NULL, max_weight_capacity numeric(10, 2), max_lift_height numeric(5, 2), equip_status varchar(30) NOT NULL, description text, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (equip_id), CONSTRAINT chk_EQUIPMENT_equip_status CHECK (equip_status IN ('AVAILABLE', 'MAINTENANCE', 'IN_USE', 'OUT_OF_SERVICE')), CONSTRAINT chk_EQUIPMENT_max_weight_capacity CHECK (max_weight_capacity IS NULL OR max_weight_capacity >= 0), CONSTRAINT chk_EQUIPMENT_equip_type CHECK (equip_type IN ('FORKLIFT', 'PALLET_JACK', 'AGV', 'AMR', 'TROLLEY', 'REACH_TRUCK', 'RF_SCANNER')), CONSTRAINT chk_EQUIPMENT_max_lift_height CHECK (max_lift_height IS NULL OR max_lift_height >= 0));
COMMENT ON COLUMN EQUIPMENT.equip_type IS 'FORKLIFT, REACH_TRUCK, PALLET_JACK, RF_SCANNER etc';
COMMENT ON COLUMN EQUIPMENT.max_weight_capacity IS 'Check if the vehicle has the capacity to lift this heavy pallet of goods.';
COMMENT ON COLUMN EQUIPMENT.max_lift_height IS 'Maximum lifting height (m): Used to compare against the height of the rack when working at upper levels.';
COMMENT ON COLUMN EQUIPMENT.equip_status IS 'ACTIVE, INACTIVE, MAINTENANCE, AVAILABLE';


CREATE TABLE LOCATION (location_id uuid NOT NULL, zone_id uuid NOT NULL, location_code varchar(50) NOT NULL UNIQUE, location_name varchar(255) NOT NULL, location_status varchar(30) DEFAULT 'AVAILABLE' NOT NULL, aisle_code varchar(20) NOT NULL, rack_code varchar(20) NOT NULL, shelf_code varchar(20) NOT NULL, bin_code varchar(20) NOT NULL, coord_x numeric(8, 2), coord_y numeric(8, 2), coord_z numeric(8, 2), capacity_volume numeric(12, 4) DEFAULT 0 NOT NULL, capacity_weight numeric(12, 3) DEFAULT 0 NOT NULL, max_weight numeric(12,3), max_volume numeric(12,3), occupied_volume numeric(12, 4) NOT NULL, occupied_weight numeric(12, 3) NOT NULL, is_pickable bool DEFAULT 'TRUE' NOT NULL, is_putaway bool DEFAULT 'TRUE' NOT NULL, last_inventory_date date, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (location_id), CONSTRAINT chk_LOCATION_dimension_uom CHECK (capacity_volume >= 0 AND capacity_weight >= 0 AND occupied_volume >= 0 AND occupied_weight >= 0), CONSTRAINT chk_LOCATION_version CHECK (version >= 0), CONSTRAINT chk_LOCATION_location_status CHECK (location_status IN ('AVAILABLE', 'BLOCKED', 'OCCUPIED', 'MAINTENANCE', 'RESERVED', 'DAMAGED')), CONSTRAINT chk_LOCATION_coordinate CHECK (coord_x >= 0 AND coord_y >= 0 AND coord_z >= 0), CONSTRAINT chk_LOCATION_max CHECK(max_weight > 0 AND max_volume > 0));
COMMENT ON COLUMN LOCATION.location_status IS 'AVAILABLE, OCCUPIED, BLOCKED';
COMMENT ON COLUMN LOCATION.coord_x IS 'X-Coodinate on the warehouse map';
COMMENT ON COLUMN LOCATION.coord_y IS 'Y-Coodinate on the warehouse map';
COMMENT ON COLUMN LOCATION.coord_z IS 'Z-Coodinate on the warehouse map';
COMMENT ON COLUMN LOCATION.capacity_volume IS 'Maximum volume of the current cell(unit:m^3)';
COMMENT ON COLUMN LOCATION.capacity_weight IS 'Maximum load capacity of the cell(unit: kg)'; 
COMMENT ON COLUMN LOCATION.occupied_volume IS 'The current volume occupied by the item';
COMMENT ON COLUMN LOCATION.occupied_weight IS 'The current weight occupied by the item';
COMMENT ON COLUMN LOCATION.is_pickable IS 'Checks if the cell allows for direct pickup';
COMMENT ON COLUMN LOCATION.is_putaway IS 'Checks if the cell allows the recommendation algorithm to stock items';
COMMENT ON COLUMN LOCATION.version IS 'Prevents configuration data conflicts in the repository';

//*

CREATE TABLE PRODUCT (product_id uuid NOT NULL, product_code varchar(50) NOT NULL UNIQUE, category_id uuid NOT NULL, brand_id uuid NOT NULL, product_name varchar(255) NOT NULL, country_of_origin varchar(100) NOT NULL, manufacturer varchar(100) NOT NULL, hs_code varchar(50), product_status varchar(30) NOT NULL, image_url text, shelf_life_days int4, created_at timestamp with timezone NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(50), updated_at timestamp with timezone DEFAULT CURRENT_TIMESTAMP, updated_by varchar(50), PRIMARY KEY (product_id), CONSTRAINT chk_PRODUCT_product_status CHECK(product_status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED')), CONSTRAINT chk_PRODUCT_shelf_life_days CHECK(shelf_life_days >= 0));
COMMENT ON COLUMN PRODUCT.image_url IS 'stores file path(image path, users avatar, etc)';


CREATE TABLE PRODUCT_BRAND (brand_id uuid NOT NULL, brand_code varchar(50) NOT NULL UNIQUE, brand_name varchar(255) NOT NULL, logo_url text, website_url text, description text, is_active bool DEFAULT 'TRUE' NOT NULL, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, version int4 NOT NULL, PRIMARY KEY (brand_id), CONSTRAINT chk_BRAND_version CHECK (version >= 0));
COMMENT ON COLUMN PRODUCT_BRAND.brand_code IS 'APPL, SS, NKE etc';
COMMENT ON COLUMN PRODUCT_BRAND.description IS 'BRAND DESCRIPTION';
COMMENT ON COLUMN PRODUCT_BRAND.is_active IS 'Operational status. If set to false, the system will prevent the entry or creation of new products under this brand.';


CREATE TABLE PRODUCT_CATEGORY (category_id uuid NOT NULL, parent_category_id uuid, category_code varchar(50) NOT NULL UNIQUE, category_name varchar(100) NOT NULL, description text, category_status varchar(30) DEFAULT 'ACTIVE' NOT NULL, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (category_id), CONSTRAINT chk_CATEGORY_version CHECK (version >= 0), CONSTRAINT chk_CATEGORY_category_status CHECK (category_status IN ('ACTIVE', 'INACTIVE')));
COMMENT ON COLUMN PRODUCT_CATEGORY.category_code IS 'A unique abbreviated category code (e.g., ELEC, PHONE, SMARTPHONE). Used for synchronization with ERP or POS systems.';
COMMENT ON COLUMN PRODUCT_CATEGORY.description IS 'category description';
COMMENT ON COLUMN PRODUCT_CATEGORY.category_status IS 'ACTIVE, INACTIVE';
COMMENT ON COLUMN PRODUCT_CATEGORY.version IS 'Optimistic Locking';

//*
CREATE TABLE SKU (sku_id uuid NOT NULL, product_id uuid NOT NULL, sku_code varchar(50) NOT NULL UNIQUE, sku_name varchar(255) NOT NULL, barcode varchar(50) UNIQUE, base_uom_id uuid NOT NULL, policy_id uuid NOT NULL, constraint_id uuid NOT NULL, sku_length numeric(10, 3), sku_width numeric(10, 3), sku_height numeric(10, 3), sku_weight numeric(10, 3), sku_volume numeric(10, 3), SKU_status varchar(30) DEFAULT 'ACTIVE' NOT NULL, packaging_material varchar(50), tie int4, high int4, stacking_limit int4, is_fragile bool DEFAULT FALSE NOT NULL, is_hazardous bool DEFAULT FALSE NOT NULL, packaging_level varchar(30), created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(50), updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_by varchar(50), abc_code varchar(10), velocity_code varchar(10), version int4 DEFAULT 0 NOT NULL, PRIMARY KEY (sku_id), CONSTRAINT chk_SKU_packaging_level CHECK (packaging_level IN ('BASE_UNIT', 'INNER_PACK', 'CASE', 'PALLET', 'DISPLAY')), CONSTRAINT chk_SKU_version CHECK (version >= 0), CONSTRAINT chk_SKU_tie CHECK (tie IS NULL OR tie > 0), CONSTRAINT chk_SKU_SKU_status CHECK (SKU_status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED') ), CONSTRAINT chk_SKU_high CHECK (high IS NULL OR high > 0), CONSTRAINT chk_SKU_stacking_limit CHECK (stacking_limit IS NULL OR stacking_limit >= 0), 
CONSTRAINT chk_SKU_dimension 
	CHECK
	(
		(sku_length IS NULL OR sku_length > 0)
    AND (sku_width IS NULL OR sku_width > 0)
    AND (sku_height IS NULL OR sku_height > 0)
    AND (sku_weight IS NULL OR sku_weight > 0)
    AND (sku_volume IS NULL OR sku_volume > 0)
	)
);

COMMENT ON CONSTRAINT chk_SKU_tie ON SKU IS 'Check the maximum number of cartons that can be stacked on a single layer of a pallet.';
COMMENT ON CONSTRAINT chk_SKU_high ON SKU IS 'Checks the maximum number of layers of shipping cases that can be stacked vertically on a single pallet';
COMMENT ON CONSTRAINT chk_SKU_stacking_limit ON SKU IS 'Checks the maximum number of cartons or pallets that may be stacked on top of one another to prevent crushing or damaging the goods underneath';
COMMENT ON COLUMN SKU.SKU_status IS 'ACTIVE, INACTIVE';
COMMENT ON COLUMN SKU.tie IS 'The number of boxes placed in a single flat layer on the pallet';
COMMENT ON COLUMN SKU.high IS ' The number of vertical layers (or tiers) stacked on top of one another.';
COMMENT ON COLUMN SKU.stacking_limit IS ' the maximum quantity, weight, or number of items that can be placed on top of one another';
COMMENT ON COLUMN SKU.is_fragile IS '"Fragile" flag: If set to true, the system alerts staff during picking/packing to wrap the item in bubble wrap and apply warning tape';
COMMENT ON COLUMN SKU.is_hazardous IS 'Hazardous Materials flag: Applies to chemical, flammable, or explosive goods. The WMS uses this flag to prevent the SKU from being stored in standard rack locations, requiring it instead to be placed in a designated segregated storage area';
COMMENT ON COLUMN SKU.packaging_level IS 'INNER, OUTER, MASTER';
COMMENT ON COLUMN SKU.barcode IS 'Global primary barcode (EAN/UPC) at the product level if available';


CREATE TABLE Unit_Of_Measure (uom_id uuid NOT NULL, uom_code varchar(20) NOT NULL UNIQUE, uom_name varchar(100) NOT NULL, uom_type varchar(30) NOT NULL, is_active bool DEFAULT 'TRUE' NOT NULL, symbol varchar(10), decimal_places smallint DEFAULT 0 NOT NULL, description text, PRIMARY KEY (uom_id), CONSTRAINT chk_UOM_uom_type CHECK (uom_type IN ('QUANTITY', 'WEIGHT', 'VOLUME', 'LENGTH')), CONSTRAINT chk_UOM_decimal_places CHECK(decimal_places >= 0 AND decimal_places <= 6));
COMMENT ON COLUMN Unit_Of_Measure.uom_code IS 'Unique abbreviation for the unit of measurement (used for quick display on the interface and invoices).';
COMMENT ON COLUMN Unit_Of_Measure.uom_type IS 'Enum value:
quantity (boxes, cartons);
weight (Kg, g, ton);
dimension (m, cm, mm);
volume (volume, Litres)';
COMMENT ON COLUMN Unit_Of_Measure.is_active IS 'indicates whether this unit of measurement can be used';

CREATE TABLE Unit_Of_Measure_Conversion (conversion_id uuid NOT NULL, from_uom_id uuid NOT NULL, to_uom_id uuid NOT NULL, sku_id uuid NOT NULL, conversion_factor numeric(18, 6) NOT NULL, conversion_length numeric(10, 4), conversion_width numeric(10, 4), conversion_weight numeric(10, 4), conversion_height numeric(10, 4), conversion_volume numeric(10, 4), PRIMARY KEY (conversion_id), CONSTRAINT chk_UOM_conversion_factor CHECK (conversion_factor > 0), CONSTRAINT chk_UOM_conversion_dimension CHECK ((conversion_length IS NULL OR conversion_length > 0) AND (conversion_width IS NULL OR conversion_width > 0) AND (conversion_weight IS NULL OR conversion_weight > 0) AND (conversion_height IS NULL OR conversion_height > 0) AND (conversion_volume IS NULL OR conversion_volume > 0), CONSTRAINT chk_from_to CHECK(from_uom_id <> to_uom_id));

CREATE TABLE SKU_supplier (SKU_supplier_id uuid NOT NULL, sku_id uuid NOT NULL, partner_id uuid NOT NULL, contract_number varchar(100), lead_time_days int4 NOT NULL, minimum_order_qty numeric(12, 4) NOT NULL, priority int2 DEFAULT 1 NOT NULL, is_active bool DEFAULT 'TRUE' NOT NULL, PRIMARY KEY (SKU_supplier_id), CONSTRAINT chk_SKU_supplier_priority CHECK (priority > 0), CONSTRAINT chk_SKU_supplier_lead_time_days CHECK (lead_time_days >= 0), CONSTRAINT chk_SKU_supplier_minimum_order_qty CHECK (minimum_order_qty > 0));
COMMENT ON COLUMN SKU_supplier.lead_time_days IS 'The number of days from the issuance of the Purchase Order until the goods arrive at the receiving warehouse (Staging Area).';
COMMENT ON COLUMN SKU_supplier.minimum_order_qty IS 'the smallest number of units a supplier or manufacturer is willing to sell to a buyer in a single transaction.';
COMMENT ON COLUMN SKU_supplier.is_active IS 'The validity status of this supply agreement';

CREATE TABLE BUSINESS_PARTNER (partner_id uuid NOT NULL, partner_code varchar(50) NOT NULL UNIQUE, partner_name varchar(255) NOT NULL, partner_type varchar(50) NOT NULL, tax_code varchar(50), email varchar(100), phone_number varchar(20), address varchar(255), delivery_address text, is_active bool DEFAULT 'TRUE' NOT NULL, created_at timestamp with time zone NOT NULL, updated_at timestamp with time zone NOT NULL, version int4 NOT NULL, PRIMARY KEY (partner_id), CONSTRAINT chk_PARTNER_version CHECK (version >= 0), CONSTRAINT chk_BUSINESS_PARTNER_partner_type CHECK (partner_type IN ('SUPPLIER', 'CUSTOMER', 'CARRIER', 'BOTH'))));
COMMENT ON COLUMN BUSINESS_PARTNER.partner_type IS 'SUPPLIER, CARRIER, CUSTOMER, BOTH';
COMMENT ON COLUMN BUSINESS_PARTNER.tax_code IS 'Uses for printing invoices and supporting documents along with inventory slips';
COMMENT ON COLUMN BUSINESS_PARTNER.is_active IS 'If set to false, the system will prevent the creation of Goods Receipt/Issue documents involving this partner (e.g., due to debt or the termination of the partnership).';



CREATE TABLE STORAGE_CONFIGURATION (config_id uuid NOT NULL, config_key varchar(100) NOT NULL UNIQUE, config_value text NOT NULL, config_group varchar(50) NOT NULL, value_type varchar(20) NOT NULL, description text, is_editable bool DEFAULT 'TRUE' NOT NULL, updated_at timestamp with time zone NOT NULL, updated_by varchar(30), PRIMARY KEY (config_id), CONSTRAINT chk_CONFIGURATION_config_group CHECK (config_group IN ('SYSTEM', 'INBOUND', 'OUTBOUND', 'INVENTORY', 'INTEGRATION') ), CONSTRAINT chk_CONFIGURATION_value_type CHECK (value_type IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN')));
COMMENT ON COLUMN STORAGE_CONFIGURATION.config_key IS 'Used to reference the setting in backend code (e.g., ALLOW_NEGATIVE_INVENTORY, PICKING_STRATEGY, DEFAULT_CURRENCY).';
COMMENT ON COLUMN STORAGE_CONFIGURATION.config_value IS 'Configuration value: Stores the actual configuration value (e.g., false, FIFO, VND). Set the type to TEXT to allow for flexible storage of strings, numbers, or JSON.';
COMMENT ON COLUMN STORAGE_CONFIGURATION.config_group IS 'Configuration grouping: Helps the frontend group settings by tab on the configuration screen (e.g., INBOUND, OUTBOUND, SYSTEM, NOTIFICATION).';
COMMENT ON COLUMN STORAGE_CONFIGURATION.updated_by IS 'Record which user account modified this configuration (for audit trail purposes).';


CREATE TABLE STORAGE_CONSTRAINT (constraint_id uuid NOT NULL, constraint_code varchar(50) NOT NULL, constraint_name varchar(100) NOT NULL, temp_min numeric(4, 1), temp_max numeric(4, 1), humidity_max numeric(4, 1), max_stack_weight numeric(10,3), max_stack_height numeric(10,3), is_allowed_staking bool DEFAULT 'TRUE' NOT NULL, special_instructions text, storage_type varchar(30) NOT NULL, PRIMARY KEY (constraint_id), CONSTRAINT chk_CONSTRAINT_storage_type CHECK (storage_type IN ('RACK', 'SHELF', 'BULK', 'BIN', 'FLOW_RACK', 'CANTILEVER')), CONSTRAINT chk_CONSTRAINT_humidity_max CHECK (humidity_max >= 0 AND humidity_max <= 100), CONSTRAINT chk_CONSTRAINT_temp CHECK(temp_min <= temp_max), CONSTRAINT chk_CONSTRAINT_max_stack_weight_height CHECK(max_stack_weight > 0 AND max_stack_height > 0));
COMMENT ON COLUMN STORAGE_CONSTRAINT.temp_min IS 'minimum required temperature';
COMMENT ON COLUMN STORAGE_CONSTRAINT.temp_max IS 'maximum required temperature';
COMMENT ON COLUMN STORAGE_CONSTRAINT.humidity_max IS 'maximum allowed humidity score';
COMMENT ON COLUMN STORAGE_CONSTRAINT.special_instructions IS 'Detailed guidelines for staff when slotting (e.g., avoid direct sunlight, do not place next to strong-smelling goods)';
COMMENT ON COLUMN STORAGE_CONSTRAINT.storage_type IS 'SHELF/PALLET/BIN etc';


CREATE TABLE STORAGE_POLICY (policy_id uuid NOT NULL, policy_code varchar(50) NOT NULL UNIQUE, policy_name varchar(100) NOT NULL, picking_strategy varchar(50) NOT NULL, putaway_strategy varchar(50) NOT NULL, max_utilization numeric(5, 2) DEFAULT 100.00 NOT NULL, priority smallint, description text, is_active bool DEFAULT 'TRUE' NOT NULL, created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP, version int4 NOT NULL DEFAULT 0, PRIMARY KEY (policy_id), CONSTRAINT chk_POLICY_picking_strategy CHECK (picking_strategy IN ('FIFO', 'LIFO', 'FEFO')), CONSTRAINT chk_POLICY_max_utilization CHECK (max_utilization >= 0 AND max_utilization <= 100), CONSTRAINT chk_POLICY_version CHECK (version >= 0), CONSTRAINT chk_POLICY_putaway_strategy CHECK (putaway_strategy IN ('FIXED_LOCATION', 'CONSOLIDATION', 'EMPTY_LOCATION', 'FAST_MOVING')), CONSTRAINT chk_POLICY_priority CHECK(priority > 0));
COMMENT ON COLUMN STORAGE_POLICY.policy_name IS 'Clear policy name: Displayed on the configuration interface (e.g., Prioritize fast-moving goods, Hazardous chemicals, Extra-heavy items on bottom shelves).';
COMMENT ON COLUMN STORAGE_POLICY.putaway_strategy IS 'Put-away strategy (Enum/String): Defines the stock consolidation algorithm(eg consolidate, empty-location, fixed-location)';

CREATE TABLE ATTRIBUTE(attribute_id uuid PRIMARY KEY, attribute_code varchar(50) UNIQUE NOT NULL, attribute_name varchar(100) NOT NULL, data_type varchar(30) NOT NULL, is_required bool DEFAULT FALSE, created_at timestamp with timezone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with timezone, CONSTRAINT chk_option_value_not_blank CHECK(length(trim(attribute_name)) > 0), CONSTRAINT chk_attribute_data_type CHECK (data_type IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'DATE', 'DATETIME', 'ENUM', 'JSON'));
CREATE TABLE ATTRIBUTE_OPTION(option_id uuid PRIMARY KEY, attribute_id uuid NOT NULL, option_code varchar(50) NOT NULL, display_name varchar(100) NOT NULL, display_order int4 NOT NULL DEFAULT 1, is_active bool DEFAULT TRUE, created_at timestamp with timezone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with time zone, CONSTRAINT unq_attribute_option UNIQUE (attribute_id, option_code), CONSTRAINT unq_attribute_display_name UNIQUE(attribute_id, display_name), CONSTRAINT chk_display_name CHECK(length(trim(display_name)) > 0), CONSTRAINT chk_display_order CHECK (display_order >= 0));
CREATE TABLE SKU_ATTRIBUTE(sku_attribute_id uuid PRIMARY KEY, sku_id uuid NOT NULL, attribute_id uuid NOT NULL, attribute_value varchar(255) NOT NULL, CONSTRAINT fk_sku_attribute_sku FOREIGN KEY(sku_id) REFERENCES SKU(sku_id), CONSTRAINT fk_sku_attribute FOREIGN KEY(attribute_id), CONSTRAINT unq_sku_attribute UNIQUE(sku_id, attribute_id));

CREATE TABLE ABC_CLASS (abc_code varchar(10) PRIMARY KEY NOT NULL, display_name varchar(50) NOT NULL, description text, min_percentage numeric(5,2), max_percentage numeric(5,2), priority smallint NOT NULL, color_code varchar(20), cycle_count_frequency_days int4 NOT NULL, target_service_level numeric(5,2), int4 NOT NULL, max_pick_distance int4, is_active bool NOT NULL DEFAULT TRUE, created_at timestamp with timezone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with timezone DEFAULT CURRENT_TIMESTAMP, version int4 NOT NULL DEFAULT 0, CONSTRAINT chk_abc_priority CHECK(priority > 0), CONSTRAINT chk_abc_percentage  
	CHECK (
		(min_percentage IS NULL OR (min_percentage >= 0 AND min_percentage <= 100))
		AND
		(max_percentage IS NULL OR (max_percentage >= 0 AND max_percentage <= 100))
        AND
        (
            min_percentage IS NULL
            OR max_percentage IS NULL
            OR min_percentage <= max_percentage
        )
    ), 
	CONSTRAINT chk_abc_cycle_frequency CHECK(cycle_count_frequency_days > 0), CONSTRAINT chk_abc_pick_distance CHECK(max_pick_distance IS NULL OR max_pick_distance >= 0), CONSTRAINT chk_abc_service_level CHECK (target_service_level IS NULL OR (target_service_level >= 0 AND target_service_level <= 100)), CONSTRAINT chk_abc_version CHECK (version >= 0)
);

CREATE TABLE VELOCITY_CLASS (velocity_code varchar(20) PRIMARY KEY, display_name varchar(50) NOT NULL, description text, min_daily_pick numeric(10,2), max_daily_pick numeric(10,2), min_monthly_pick numeric(10,2), max_monthly_pick numeric(10,2), priority smallint NOT NULL, color_code varchar(20), is_active bool NOT NULL DEFAULT TRUE, created_at timestamp with timezone NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp with timezone DEFAULT CURRENT_TIMESTAMP, CONSTRAINT chk_velocity_priority CHECK (priority > 0), CONSTRAINT chk_velocity_daily_pick CHECK ((min_daily_pick IS NULL OR min_daily_pick > 0) AND (max_daily_pick IS NULL OR max_daily_pick > 0) AND (min_daily_pick <= max_daily_pick)), CONSTRAINT chk_velocity_monthly_pick CHECK((min_monthly_pick IS NULL OR min_monthly_pick > 0) AND (max_monthly_pick IS NULL OR max_monthly_pick > 0) AND (min_monthly_pick<=max_monthly_pick)));

ALTER TABLE SKU_supplier ADD CONSTRAINT "fk_sku_supplier_partner_id" FOREIGN KEY(partner_id)REFERENCES BUSINESS_PARTNER(partner_id);
ALTER TABLE SKU_supplier ADD CONSTRAINT "fk_sku_supplier_id" FOREIGN KEY(sku_id)REFERENCES SKU(sku_id);

ALTER TABLE EQUIPMENT ADD CONSTRAINT "fk_equipment_zone" FOREIGN KEY(zone_id)REFERENCES ZONE(zone_id);

ALTER TABLE Unit_Of_Measure_Conversion ADD CONSTRAINT "fk_uom_from_uom_id" FOREIGN KEY(from_uom_id) REFERENCES Unit_Of_Measure(uom_id);
ALTER TABLE Unit_Of_Measure_Conversion ADD CONSTRAINT "fk_uom_to_uom_id" FOREIGN KEY(to_uom_id) REFERENCES Unit_Of_Measure(uom_id);
ALTER TABLE Unit_Of_Measure_Conversion ADD CONSTRAINT "fk_uom_conversion_sku_id" FOREIGN KEY(sku_id)REFERENCES SKU(sku_id);


ALTER TABLE PRODUCT ADD CONSTRAINT "fk_product_brand_id" FOREIGN KEY(brand_id)REFERENCES PRODUCT_BRAND(brand_id);
ALTER TABLE PRODUCT ADD CONSTRAINT "fk_product_category_id" FOREIGN KEY(category_id) REFERENCES PRODUCT_CATEGORY(category_id);


ALTER TABLE LOCATION ADD CONSTRAINT "fk_location_zone_id" FOREIGN KEY(zone_id) REFERENCES ZONE(zone_id);

ALTER TABLE SKU ADD CONSTRAINT "fk_sku_product" FOREIGN KEY(product_id) REFERENCES PRODUCT(product_id);
ALTER TABLE SKU ADD CONSTRAINT "fk_sku_uom" FOREIGN KEY(base_uom_id) REFERENCES Unit_Of_Measure(uom_id);
ALTER TABLE SKU ADD CONSTRAINT "fk_sku_policy" FOREIGN KEY(policy_id) REFERENCES STORAGE_POLICY(policy_id);
ALTER TABLE SKU ADD CONSTRAINT "fk_sku_constraint" FOREIGN KEY(constraint_id) REFERENCES STORAGE_CONSTRAINT(constraint_id);
ALTER TABLE SKU ADD CONSTRAINT "fk_sku_abc" FOREIGN KEY(abc_code) REFERENCES ABC_CLASS(abc_code);
ALTER TABLE SKU ADD CONSTRAINT "fk_sku_velocity" FOREIGN KEY(velocity_code) REFERENCES VELOCITY_CLASS(velocity_code);

ALTER TABLE ZONE ADD CONSTRAINT "fk_zone_warehouse_id" FOREIGN KEY(warehouse_id) REFERENCES WAREHOUSE(warehouse_id);
ALTER TABLE ZONE ADD CONSTRAINT "fk_zone_constraint" FOREIGN KEY(constraint_id)REFERENCES STORAGE_CONSTRAINT(constraint_id);
ALTER TABLE ZONE ADD CONSTRAINT "fk_zone_unique_key" UNIQUE(warehouse_id, zone_code);


