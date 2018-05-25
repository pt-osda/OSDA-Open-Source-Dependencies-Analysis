INSERT INTO project(name) VALUES ('First Project') , ('Second Project');

INSERT INTO build(name, timestamp, tag) VALUES ('First Project', '2018-01-01T20:00:00+00:00', 'First Tag') , ('First Project', '2018-01-02T20:00:00+00:00', 'Second Tag');
INSERT INTO build(name, timestamp, tag) VALUES ('Second Project', '2018-02-01T20:00:00+00:00', 'First Tag') , ('Second Project', '2018-02-03T20:00:00+00:00', 'Second Tag');

INSERT INTO dependency(main_version, id, vulnerabilities_count, description) VALUES ('1.0.0', 'lodash', 1, 'Lodash modular utilities.');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description) VALUES ('2.3.3', 'mocha', 0, 'A simple, flexible, fun test framework');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description) VALUES ('2.3.4', 'rimraf', 0, 'A deep deletion module for node (like `rm -rf`)');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description) VALUES ('3.0.0', 'spdx-correct', 0, 'Correct invalid SPDX expressions');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description) VALUES ('5.5.0', 'semver', 1, 'The semantic version parser used by npm.');

INSERT INTO build_dependency(build_timestamp, build_name, dependency_main_version, dependency_id)
VALUES ('2018-01-01T20:00:00+00:00', 'First Project', '1.0.0', 'lodash'),
  ('2018-01-01T20:00:00+00:00', 'First Project', '2.3.3', 'mocha');

INSERT INTO build_dependency(build_timestamp, build_name, dependency_main_version, dependency_id)
VALUES ('2018-01-02T20:00:00+00:00', 'First Project', '2.3.4', 'rimraf'),
  ('2018-01-02T20:00:00+00:00', 'First Project', '3.0.0', 'spdx-correct'),
  ('2018-01-02T20:00:00+00:00', 'First Project', '5.5.0', 'semver');

INSERT INTO build_dependency(build_timestamp, build_name, dependency_main_version, dependency_id)
VALUES ('2018-02-01T20:00:00+00:00', 'Second Project', '1.0.0', 'lodash'),
  ('2018-02-01T20:00:00+00:00', 'Second Project', '3.0.0', 'spdx-correct');

INSERT INTO build_dependency(build_timestamp, build_name, dependency_main_version, dependency_id)
VALUES ('2018-02-03T20:00:00+00:00', 'Second Project', '2.3.4', 'rimraf'),
  ('2018-02-03T20:00:00+00:00', 'Second Project', '2.3.3', 'mocha'),
  ('2018-02-03T20:00:00+00:00', 'Second Project', '5.5.0', 'semver');

INSERT INTO license(spdx_id) VALUES ('MIT');
INSERT INTO license(spdx_id) VALUES ('BSD-3-Clause-No-Nuclear-Warranty');
INSERT INTO license(spdx_id) VALUES ('GPL-3.0-only');
INSERT INTO license(spdx_id) VALUES ('LGPL-3.0-only');
INSERT INTO license(spdx_id) VALUES ('EFL-2.0');

INSERT INTO dependency_license(license_spdx_id, dependency_main_version, dependency_id, source) VALUES
  ('MIT', '1.0.0', 'lodash', 'Found in file'),
  ('BSD-3-Clause-No-Nuclear-Warranty', '2.3.3', 'mocha', 'Found in package.json'),
  ('GPL-3.0-only', '2.3.4', 'rimraf', 'Found in GitHub'),
  ('LGPL-3.0-only', '3.0.0', 'spdx-correct', 'Found in file'),
  ('EFL-2.0', '5.5.0', 'semver', 'Found in file');

INSERT INTO vulnerability(id, description, info, title)
    VALUES (12345, 'Test vulnerability', '{"First Reference", "Second Reference""}', 'DDos'),
      (67890, 'Vulnerability Test', '{"Third Reference", "Fourth Reference"}', 'DDos');

INSERT INTO dependency_vulnerability(vulnerability_id, dependency_main_version, dependency_id, version_criteria)
    VALUES (12345, '1.0.0', 'lodash', '<1.5.0'),
      (67890, '5.5.0', 'semver', '>1.5.0');