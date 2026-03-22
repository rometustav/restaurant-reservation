-- Siseruum (INDOOR)
INSERT INTO RESTAURANT_TABLE (capacity, zone, x, y, corner_seat, window_seat, kids_area_seat) VALUES
(2, 'INDOOR', 1, 1, true, true, false),
(2, 'INDOOR', 5, 1, false, true, false),
(4, 'INDOOR', 9, 1, false, true, false),
(4, 'INDOOR', 1, 3, false, false, true),
(6, 'INDOOR', 7, 3, false, false, false),
(8, 'INDOOR', 1, 5, false, false, false);

-- Terrass (TERRACE)
INSERT INTO RESTAURANT_TABLE (capacity, zone, x, y, corner_seat, window_seat, kids_area_seat) VALUES
(2, 'TERRACE', 1, 8, false, false, false),
(2, 'TERRACE', 5, 8, false, false, false),
(4, 'TERRACE', 9, 8, false, false, true),
(6, 'TERRACE', 1, 10, true, false, false);

-- Privaatruum (PRIVATE)
INSERT INTO RESTAURANT_TABLE (capacity, zone, x, y, corner_seat, window_seat, kids_area_seat) VALUES
(4, 'PRIVATE', 1, 13, true, false, false),
(6, 'PRIVATE', 7, 13, true, false, false),
(8, 'PRIVATE', 1, 15, false, false, false);