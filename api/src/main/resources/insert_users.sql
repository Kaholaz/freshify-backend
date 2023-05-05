USE freshify_dev;
START TRANSACTION;
INSERT INTO _user (email, first_name, password)
VALUES ('super@user.com', 'Super', '$2a$10$j4r2Y6zAisMnaSmSWgr2VuA2Z2rfYkcQDGVZveu74Kr8rjMZQSJ3G'),
       ('normal@user.no', 'Normal', '$2a$10$b2LZR.howPWC59ZCdw054O.HVZEgDBM4V9spD6FmgEoPZTwldAHw6');
COMMIT;
