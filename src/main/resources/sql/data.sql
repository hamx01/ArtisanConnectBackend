INSERT INTO clients (email, first_name, image, last_name, password, role)
VALUES
    ('dignissim.tempor.arcu@aol.ca', 'Diana', 'null', 'Harrison', 'password', 'USER'),
    ('john.doe@example.com', 'John', 'null', 'Doe', 'password123', 'ADMIN'),
    ('jane.smith@example.com', 'Jane', 'null', 'Smith', 'securepass', 'USER'),
    ('michael.brown@example.com', 'Michael', 'null', 'Brown', 'mypassword', 'USER'),
    ('emily.jones@example.com', 'Emily', 'null', 'Jones', 'passw0rd', 'USER');


INSERT INTO notice (title, description, client_id, price, category, status, publish_date) VALUES
    ('Ręcznie robiona biżuteria', 'Unikalna biżuteria wykonana ręcznie z najwyższej jakości materiałów.', 5, 150.00, 'Jewelry', 'ACTIVE', '2023-10-01'),
    ('Drewniany stół', 'Solidny stół wykonany z litego drewna dębowego.', 3, 1200.00, 'Furniture', 'ACTIVE', '2023-09-15'),
    ('Ceramiczna waza', 'Piękna waza ceramiczna, idealna na prezent.', 2, 300.00, 'Ceramics', 'INACTIVE', '2023-08-20'),
    ('Obraz olejny', 'Obraz olejny przedstawiający krajobraz górski.', 4, 800.00, 'Painting', 'ACTIVE', '2023-07-10'),
    ('Skórzany portfel', 'Ręcznie wykonany portfel ze skóry naturalnej.', 1, 250.00, 'Leatherwork', 'ACTIVE', '2023-06-05');