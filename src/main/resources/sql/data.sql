INSERT INTO roles (id, rolename)
VALUES
    (1, 'USER'),
    (2, 'ADMIN');

INSERT INTO clients (email, first_name, last_name, password, role_id)
VALUES
    ('dignissim.tempor.arcu@aol.ca', 'Diana', 'Harrison', '', 1),
    ('john.doe@example.com', 'John', 'Doe', '', 2),
    ('jane.smith@example.com', 'Jane', 'Smith', '', 1),
    ('michael.brown@example.com', 'Michael', 'Brown', '', 1),
    ('emily.jones@example.com', 'Emily', 'Jones', '', 1);


INSERT INTO notice (title, description, client_id, price, category, status, publish_date) VALUES
    ('Ręcznie robiona biżuteria', 'Unikalna biżuteria wykonana ręcznie z najwyższej jakości materiałów.', 5, 150.00, 'Jewelry', 'ACTIVE', '2023-10-01'),
    ('Drewniany stół', 'Solidny stół wykonany z litego drewna dębowego.', 3, 1200.00, 'Furniture', 'ACTIVE', '2023-09-15'),
    ('Ceramiczna waza', 'Piękna waza ceramiczna, idealna na prezent.', 2, 300.00, 'Ceramics', 'INACTIVE', '2023-08-20'),
    ('Obraz olejny', 'Obraz olejny przedstawiający krajobraz górski.', 4, 800.00, 'Painting', 'ACTIVE', '2023-07-10'),
    ('Skórzany portfel', 'Ręcznie wykonany portfel ze skóry naturalnej.', 1, 250.00, 'Leatherwork', 'ACTIVE', '2023-06-05');


insert into attributes (name) values
                                  ('Kolor'),
                                  ('Materiał');

-- Kolory
insert into attribute_values (value, id_attribute)  values
                                                        ('Zielony', 1),
                                                        ('Czerwony', 1),
                                                        ('Niebieski', 1),
                                                        ('Żółty', 1),
                                                        ('Biały', 1),
                                                        ('Czarny', 1),
                                                        ('Różowy', 1),
                                                        ('Szary', 1),
                                                        ('Fioletowy', 1),
                                                        ('Pomarańczowy', 1),
                                                        ('Inny', 1);

-- Materiały
insert into attribute_values (value, id_attribute)  values
                                                        ('Bawełna', 2),
                                                        ('Wełna', 2),
                                                        ('Syntetyk', 2),
                                                        ('Skóra', 2),
                                                        ('Len', 2),
                                                        ('Jedwab', 2),
                                                        ('Poliester', 2),
                                                        ('Akryl', 2),
                                                        ('Wiskoza', 2),
                                                        ('Nylon', 2),
                                                        ('Inny', 2);

insert into attributes_notice (id_value, notice_id) values
                                                        (1, 1), -- Ręcznie robiona biżuteria - Zielony
                                                        (22, 1),
                                                        (2, 2), -- Drewniany stół - Czerwony
                                                        (3, 3), -- Ceramiczna waza - Niebieski
                                                        (4, 4), -- Obraz olejny - Żółty
                                                        (5, 5); -- Skórzany portfel - Biały
