USE freshify_dev;
START TRANSACTION;
INSERT INTO item (bought, last_changed, remaining, status, suggested, added_by_id, household_id, type_id)
VALUES ('2023-05-05 21:21:09.576000', '2023-05-05 21:21:39.517000', 0.25, 'USED', _binary '\0', 1, 1, 70),
       ('2023-05-05 21:24:18.178000', '2023-05-05 21:24:18.178000', 1, 'INVENTORY', _binary '\0', 1, 1, 599),
       ('2023-05-05 21:24:18.197000', '2023-05-05 21:24:18.197000', 1, 'INVENTORY', _binary '\0', 1, 1, 8);
COMMIT;
