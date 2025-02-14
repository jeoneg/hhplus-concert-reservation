DELETE FROM hhplus.seat;

INSERT INTO hhplus.seat (id, place_id, price, status, version, created_at, modified_at)
VALUES (1, 1, 130000, 'AVAILABLE', 0, NOW(), NOW()),
       (2, 1, 130000, 'AVAILABLE', 0, NOW(), NOW()),
       (3, 1, 130000, 'AVAILABLE', 0, NOW(), NOW()),
       (4, 1, 130000, 'AVAILABLE', 0, NOW(), NOW()),
       (5, 1, 130000, 'AVAILABLE', 0, NOW(), NOW());