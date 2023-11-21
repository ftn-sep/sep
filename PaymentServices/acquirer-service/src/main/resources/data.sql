INSERT INTO card(card_holder_name, expire_date, pan, security_code) -- 5209304131859138 pan (sha512)
VALUES ('Prodavac Prodavcevic', '11/26', 'e68605f7d3ca3c3ea161ddca15b1ee33f136dbfce711f22b504a17b14adbf3f8d47e3736799b0b422f477904b6613d348d06f5d981651bcda39669bd698efbd7',
        '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra');

INSERT INTO card(card_holder_name, expire_date, pan, security_code) -- 5209302889842835 pan (sha512)
VALUES ('Pera Peric', '05/28', '8801b1beadaf01971f2dbbe309567d4f440917e46772db1995048995b8c9a09e26cccc11efc039893f4157102a6748362bafeb890458474df6c47e06cea60696',
        '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra');


INSERT INTO bank_account(balance, card_id, merchant_id, merchant_password, account_number)
VALUES (1000., 1, 'kjk-sfa2-242', 'ghgsjk21', '1000123456789');


INSERT INTO bank_account(balance, card_id, merchant_id, merchant_password, account_number)
VALUES (500., 2, null, null, '1000987654321');

INSERT INTO bank_bin(bin) -- procredit mastercard
VALUES (520930)