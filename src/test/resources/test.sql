INSERT INTO doctors (id, name, surname, title)
VALUES (1, 'DOCTOR1', 'SURNAME1', 'DR'),
       (2, 'DOCTOR2', 'SURNAME2', 'DR');

INSERT INTO customers (id, pin, name, surname)
VALUES (1, 1234, 'CUSTOMER1', 'SURNAME1'),
       (2, 1234, 'CUSTOMER2', 'SURNAME2');


INSERT INTO appointments (id, note, scheduled_date, scheduled_time, timestamp, customer_id, doctor_id)
VALUES (1, 'APPOINTMENT1', '2022-01-21', '12:00:00', '2022-01-21 12:00:00', 1, 1),
       (2, 'APPOINTMENT2', '2022-01-22', '12:00:00', '2022-01-22 12:00:00', 1, 2),
       (3, 'APPOINTMENT3', '2022-01-23', '12:00:00', '2022-01-23 12:00:00', 2, 1),
       (4, 'APPOINTMENT4', '2022-01-24', '12:00:00', '2022-01-24 12:00:00', 2, 2);

INSERT INTO visit_details (visit_price, visit_duration_in_minutes, opening_at, closing_at, doctor_id)
VALUES (150, 30, '08:00', '16:00', 1),
       (210, 60, '16:00', '00:00', 2);
