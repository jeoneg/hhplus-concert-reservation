DELETE FROM hhplus.seat;

INSERT INTO hhplus.seat (id, place_id, price, status, created_at, modified_at)
VALUES (1, 1, 130000, 'AVAILABLE', NOW(), NOW()),
       (2, 1, 130000, 'AVAILABLE', NOW(), NOW()),
       (3, 1, 130000, 'AVAILABLE', NOW(), NOW()),
       (4, 1, 130000, 'AVAILABLE', NOW(), NOW()),
       (5, 1, 130000, 'AVAILABLE', NOW(), NOW());