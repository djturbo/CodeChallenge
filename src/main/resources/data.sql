INSERT INTO TRANSACTION (
reference,
  iban,
  date,
  amount,
  fee,
  description) 
  VALUES
  ('222', 'IBAN1', CURRENT_TIMESTAMP, 330.2, 3.6, 'Descripción de la transacción');
INSERT INTO TRANSACTION (
  reference,
  iban,
  date,
  amount,
  fee,
  description) 
  VALUES
  ('111', 'IBAN1', CURRENT_TIMESTAMP, 25.4, 2.8, 'Otra de la transacción');