
ALTER TABLE extra_charge_tax DROP tax_percentage;
ALTER TABLE extra_charge_tax ADD tax_percentage double null default 0.0;
