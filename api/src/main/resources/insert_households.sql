USE freshify_dev;
START TRANSACTION;
INSERT INTO household (name) VALUES ('Hjem');

INSERT INTO household_member VALUES (1,1,'SUPERUSER'),(1,2,'USER');
COMMIT;
