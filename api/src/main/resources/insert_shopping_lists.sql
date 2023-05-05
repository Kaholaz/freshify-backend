USE freshify_dev;
START TRANSACTION;
INSERT INTO shopping_list_entry (checked, count, suggested, added_by_id, household_id, type_id)
VALUES (_binary '\0', 1, _binary '\0', 1, 1, 295),
       (_binary '', 1, _binary '\0', 1, 1, 783),
       (_binary '\0', 1, _binary '\0', 1, 1, 830),
       (_binary '\0', 1, _binary '\0', 1, 1, 70),
       (_binary '', 1, _binary '\0', 1, 1, 16),
       (_binary '\0', 1, _binary '\0', 1, 1, 966);
COMMIT;
