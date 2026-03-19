INSERT INTO organization (id, organization_key, name, owner)
VALUES (1, 'DL', 'Diamond League', 'simon@martinelli.ch');

INSERT INTO series (id, name, logo, hidden, locked, organization_id)
VALUES (1, 'Diamond League 2022', null, false, false, 1);

INSERT INTO competition (id, name, competition_date, always_first_three_medals, medal_percentage, locked, series_id)
VALUES (1, 'Athletissima', '2022-08-26', true, 0, false, 1);

INSERT INTO club (id, abbreviation, name, organization_id)
VALUES (1, 'STB', 'Stadtturnverein Bern', 1);
INSERT INTO club (id, abbreviation, name, organization_id)
VALUES (2, 'LSU', 'Louisiana State University', 1);

INSERT INTO athlete (id, first_name, last_name, gender, year_of_birth, club_id, organization_id)
VALUES (1000, 'Armand', 'Duplantis', 'm', 1999, 2, 1);
INSERT INTO athlete (id, first_name, last_name, gender, year_of_birth, club_id, organization_id)
VALUES (1001, 'Simon', 'Martinelli', 'm', 1975, 1, 1);

INSERT INTO event (id, abbreviation, name, gender, event_type, a, b, c, organization_id)
VALUES (1, '100m', '100 Meter', 'm', 'RUNNING', 25.4347, 18.0, 1.81, 1);
INSERT INTO event (id, abbreviation, name, gender, event_type, a, b, c, organization_id)
VALUES (2, 'WEI', 'Weitsprung', 'm', 'JUMPING', 0.14354, 220.0, 1.40, 1);

INSERT INTO category (id, abbreviation, name, gender, year_from, year_to, series_id)
VALUES (1, 'MAN', 'Maenner', 'm', 1900, 2010, 1);

INSERT INTO category_athlete (category_id, athlete_id)
VALUES (1, 1000);
INSERT INTO category_athlete (category_id, athlete_id)
VALUES (1, 1001);

INSERT INTO category_event (category_id, event_id, position)
VALUES (1, 1, 1);
INSERT INTO category_event (category_id, event_id, position)
VALUES (1, 2, 2);

INSERT INTO result (id, position, result, points, athlete_id, category_id, competition_id, event_id)
VALUES (1, 1, '10.55', 850, 1000, 1, 1, 1);
INSERT INTO result (id, position, result, points, athlete_id, category_id, competition_id, event_id)
VALUES (2, 2, '6.50', 720, 1000, 1, 1, 2);
INSERT INTO result (id, position, result, points, athlete_id, category_id, competition_id, event_id)
VALUES (3, 1, '11.20', 700, 1001, 1, 1, 1);
INSERT INTO result (id, position, result, points, athlete_id, category_id, competition_id, event_id)
VALUES (4, 2, '5.80', 600, 1001, 1, 1, 2);
