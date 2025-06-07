INSERT INTO roles (id, rolename)
VALUES
    (1, 'USER'),
    (2, 'ADMIN');

INSERT INTO clients (email, first_name, last_name, password, role_id)
VALUES
    ('dignissim.tempor.arcu@aol.ca', 'Diana', 'Harrison', 'password', 1),
    ('john.doe@example.com', 'John', 'Doe', 'password123', 2),
    ('jane.smith@example.com', 'Jane', 'Smith', 'securepass', 1),
    ('michael.brown@example.com', 'Michael', 'Brown', 'mypassword', 1),
    ('emily.jones@example.com', 'Emily', 'Jones', 'passw0rd', 1);


INSERT INTO notice (title, description, client_id, price, category, status, publish_date) VALUES
    ('Ręcznie robiona biżuteria', 'Unikalna biżuteria wykonana ręcznie z najwyższej jakości materiałów.', 5, 150.00, 'Jewelry', 'ACTIVE', '2023-10-01'),
    ('Drewniany stół', 'Solidny stół wykonany z litego drewna dębowego.', 3, 1200.00, 'Furniture', 'ACTIVE', '2023-09-15'),
    ('Ceramiczna waza', 'Piękna waza ceramiczna, idealna na prezent.', 2, 300.00, 'Ceramics', 'INACTIVE', '2023-08-20'),
    ('Obraz olejny', 'Obraz olejny przedstawiający krajobraz górski.', 4, 800.00, 'Painting', 'ACTIVE', '2023-07-10'),
    ('Skórzany portfel', 'Ręcznie wykonany portfel ze skóry naturalnej.', 1, 250.00, 'Leatherwork', 'ACTIVE', '2023-06-05');