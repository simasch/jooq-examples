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
