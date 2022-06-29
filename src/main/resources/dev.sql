INSERT INTO doctors (id, name, surname, title)
VALUES (1, 'Jelani', 'Wise', 'Dr'),
       (2, 'Magee', 'Grant', 'Dr'),
       (3, 'Audra', 'Woods', 'Dr'),
       (4, 'Cherokee', 'Little', 'Dr'),
       (5, 'Sasha', 'Carson', 'Dr');


INSERT INTO customers (id, name, surname, pin)
VALUES (1, 'Tucker', 'Case', '1234'),
       (2, 'Anjolie', 'Padilla', '1234'),
       (3, 'Roth', 'Garrison', '1234'),
       (4, 'Glenna', 'Hanson', '1234'),
       (5, 'Flynn', 'Owens', '1234'),
       (6, 'Dominic', 'Jones', '1234'),
       (7, 'Guinevere', 'Cochran', '1234'),
       (8, 'Brianna', 'Smith', '1234'),
       (9, 'Thaddeus', 'Kinney', '1234'),
       (10, 'Glenna', 'Carey', '1234'),
       (11, 'Quynn', 'Noel', '1234'),
       (12, 'Kimberly', 'Chambers', '1234'),
       (13, 'Raphael', 'Bates', '1234'),
       (14, 'Alice', 'Lucas', '1234'),
       (15, 'Libby', 'Potter', '1234');


INSERT INTO appointments (id, note, scheduled_date, scheduled_time, timestamp, customer_id, doctor_id)
VALUES (1, 'APPOINTMENT1', '2022-01-01', '12:00', '2022-01-01 12:00', 1, 1),
       (2, 'APPOINTMENT2', '2022-01-02', '12:00', '2022-01-02 12:00', 2, 2),
       (3, 'APPOINTMENT3', '2022-01-03', '12:00', '2022-01-03 12:00', 3, 3),
       (4, 'APPOINTMENT4', '2022-01-04', '16:00', '2022-01-04 12:00', 4, 4),
       (5, 'APPOINTMENT5', '2022-01-05', '12:00', '2022-01-05 12:00', 5, 5),
       (6, 'APPOINTMENT6', '2022-01-06', '12:00', '2022-01-06 12:00', 6, 1),
       (7, 'APPOINTMENT7', '2022-01-07', '12:00', '2022-01-07 12:00', 7, 2),
       (8, 'APPOINTMENT8', '2022-01-08', '12:00', '2022-01-08 12:00', 8, 3),
       (9, 'APPOINTMENT9', '2022-01-09', '16:00', '2022-01-09 12:00', 9, 4),
       (10, 'APPOINTMENT10', '2022-01-10', '12:00', '2022-01-10 12:00', 10, 5),
       (11, 'APPOINTMENT11', '2022-01-11', '12:00', '2022-01-11 12:00', 11, 1),
       (12, 'APPOINTMENT12', '2022-01-12', '12:00', '2022-01-12 12:00', 12, 2),
       (13, 'APPOINTMENT13', '2022-01-13', '12:00', '2022-01-13 12:00', 13, 3),
       (14, 'APPOINTMENT14', '2022-01-14', '16:00', '2022-01-14 12:00', 14, 4),
       (15, 'APPOINTMENT15', '2022-01-15', '12:00', '2022-01-15 12:00', 15, 5);


INSERT INTO visit_details (visit_price, visit_duration_in_minutes, opening_at, closing_at, doctor_id)
VALUES (150, 20, '08:00', '16:00', 1),
       (210, 60, '09:00', '15:00', 2),
       (410, 40, '12:00', '20:00', 3),
       (510, 30, '16:00', '00:00', 4),
       (160, 50, '10:00', '18:00', 5);
