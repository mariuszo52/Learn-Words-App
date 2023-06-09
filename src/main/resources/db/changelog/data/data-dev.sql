insert into category (name) values ('Zwierzęta');
insert into category (name) values ('Dom');
INSERT INTO word (polish_name, translation, image_link, sentence, priority, category_id)
VALUES ('Kot', 'Cat', 'https://tiny.pl/cxpp5', 'My cat likes to play with a ball.', 1, 1);

INSERT INTO word (polish_name, translation, image_link, sentence, priority, category_id)
VALUES ('Pies', 'Dog', 'https://tiny.pl/cxppk', 'I take my dog for a walk every morning.', 1, 1);

INSERT INTO word (polish_name, translation, image_link, sentence, priority, category_id)
VALUES ('Samochód', 'Car', 'https://tiny.pl/cxplq', 'I need to buy a new car.', 1, 2);

INSERT INTO word (polish_name, translation, image_link, sentence, priority, category_id)
VALUES ('Książka', 'Book', 'https://tiny.pl/cxplg', 'I enjoy reading a good book in my free time.', 1, 2);

INSERT INTO word (polish_name, translation, image_link, sentence, priority, category_id)
VALUES ('Telefon', 'Phone', 'https://tiny.pl/cxplw', 'I forgot my phone at home.', 1, 2);


INSERT INTO user_role (name, description) VALUES ('ADMIN', 'Może robić wszystko');
INSERT INTO user_role (name, description) VALUES ('USER', 'Może podstawowe rzeczy.');
INSERT INTO users (email, password, user_role_id, repeated_words, repeated_words_today, days_in_a_row,
                   last_login, learned_words, words_to_learn, register_date, all_time, time_today, new_words,
                   new_words_week, is_account_not_locked)
VALUES ('admin@example.com', '{bcrypt}$2y$10$vNM.ApypTgWsDJAPfIQEWeb61UNbImxv9tQJH.MMP4Hbmhu6HumDC',
        1 , 0, 0, 0, '1970-01-01 00:00:00', 0, 0, '1970-01-01 00:00:00', 0, 0, 0, 0, true);
INSERT INTO users (email, password, user_role_id, repeated_words, repeated_words_today, days_in_a_row,
                   last_login, learned_words, words_to_learn, register_date, all_time, time_today, new_words,
                   new_words_week, is_account_not_locked)
VALUES ('user@example.com', '{bcrypt}$2y$10$vNM.ApypTgWsDJAPfIQEWeb61UNbImxv9tQJH.MMP4Hbmhu6HumDC',
        2 , 0, 0, 0, '1970-01-01 00:00:00', 0, 0, '1970-01-01 00:00:00', 0, 0, 0, 0, true);
UPDATE category SET user_id = 1 WHERE user_id IS NULL;
insert into language (name) values ('angielski');
insert into language (name) values ('niemiecki');
insert into language (name) values ('hiszpanski');
insert into language (name) values ('włoski');
insert into language (name) values ('francuski');
insert into language (name) values ('czeski');
insert into language (name) values ('rosyjski');
insert into language (name) values ('japoński');
insert into language (name) values ('chiński');
insert into language (name) values ('portugalski');
insert into language (name) values ('holenderski');
insert into language (name) values ('szwedzki');
insert into language (name) values ('norweski');
insert into language (name) values ('arabski');
insert into language (name) values ('koreański');
insert into language (name) values ('turecki');

UPDATE word set language_id = 1 WHERE language_id is null;
UPDATE word set user_id = 1 WHERE user_id is null;



