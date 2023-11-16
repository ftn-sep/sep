INSERT INTO card(card_holder_name, expire_date, pan, security_code)
VALUES ('Prodavac Prodavcevic', '11/26', '1111532586329128', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra');

INSERT INTO card(card_holder_name, expire_date, pan, security_code)
VALUES ('Pera Peric', '05/28', '1111654812357894', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra');


INSERT INTO bank_account(balance, card_id, merchant_id, merchant_password, account_number)
VALUES (1000., 1, 'kjk-sfa2-242', 'ghgsjk21', '1000123456789');


INSERT INTO bank_account(balance, card_id, merchant_id, merchant_password, account_number)
VALUES (500., 2, null, null, '1000987654321');