INSERT INTO card(card_holder_name, expire_date, pan, security_code) -- pan 5321915888070539
VALUES ('Mitar Miric', '05/28', '2f84f8ede614c975de2ed7c2af2fe5209b930c868c3fac47fcabe139d53bd287b5c5befe110127100ad468bfc8c9a3e5c6768fa009bd899dfd75009a52cc6b79',
        '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra');

INSERT INTO bank_account(balance, card_id, account_number)
VALUES (1000000., 1, '2000123456789');