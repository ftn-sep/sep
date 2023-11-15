INSERT INTO card(card_holder_name, expire_date, pan, security_code)
VALUES ('Prodavac Prodavcevic', '11/26', '1111532586329128', '123');

INSERT INTO card(card_holder_name, expire_date, pan, security_code)
VALUES ('Pera Peric', '05/28', '1111654812357894', '123');


INSERT INTO bank_account(balance, card_id, merchant_id, merchant_password)
VALUES (1000., 1, 'kjk-sfa2-242', 'ghgsjk21');

INSERT INTO bank_account(balance, card_id, merchant_id, merchant_password)
VALUES (500., 2, null, null);