-- Siseruum (INDOOR)
INSERT INTO RESTAURANT_TABLE (capacity, zone, x, y, corner_table, window_table, kids_area_table) VALUES
(2, 'INDOOR', 1, 1, true, true, false),
(2, 'INDOOR', 5, 1, false, true, false),
(4, 'INDOOR', 9, 1, false, true, false),
(4, 'INDOOR', 13, 1, false, false, true),
(6, 'INDOOR', 1, 4, false, false, false),
(8, 'INDOOR', 6, 4, false, false, false);

-- Terrass (TERRACE)
INSERT INTO RESTAURANT_TABLE (capacity, zone, x, y, corner_table, window_table, kids_area_table) VALUES
(2, 'TERRACE', 1, 8, false, false, false),
(2, 'TERRACE', 5, 8, false, false, false),
(4, 'TERRACE', 9, 8, false, false, true),
(6, 'TERRACE', 13, 8, true, false, false);

-- Privaatruum (PRIVATE)
INSERT INTO RESTAURANT_TABLE (capacity, zone, x, y, corner_table, window_table, kids_area_table) VALUES
(4, 'PRIVATE', 1, 12, true, false, false),
(6, 'PRIVATE', 5, 12, true, false, false),
(8, 'PRIVATE', 11, 12, false, false, false);