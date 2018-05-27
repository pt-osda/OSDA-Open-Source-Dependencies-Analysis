INSERT INTO project(name) VALUES ('First Project');

INSERT INTO build(name, timestamp, tag) VALUES ('First Project', '2018-01-01T20:00:00+00:00', 'First Tag') , ('First Project', '2018-01-02T20:00:00+00:00', 'Second Tag');

INSERT INTO dependency(main_version, id, vulnerabilities_count, description, timestamp, project) VALUES ('1.0.0', 'lodash', 1, 'Lodash modular utilities.','2018-01-01T20:00:00+00:00', 'First Project');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description, timestamp, project) VALUES ('2.3.3', 'mocha', 0, 'A simple, flexible, fun test framework','2018-01-01T20:00:00+00:00', 'First Project');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description, timestamp, project) VALUES ('2.3.4', 'rimraf', 0, 'A deep deletion module for node (like `rm -rf`)', '2018-01-02T20:00:00+00:00', 'First Project');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description, timestamp, project) VALUES ('3.0.0', 'spdx-correct', 0, 'Correct invalid SPDX expressions', '2018-01-02T20:00:00+00:00', 'First Project');
INSERT INTO dependency(main_version, id, vulnerabilities_count, description, timestamp, project) VALUES ('5.5.0', 'semver', 1, 'The semantic version parser used by npm.', '2018-01-02T20:00:00+00:00', 'First Project');

INSERT INTO license(spdx_id) VALUES ('MIT');
INSERT INTO license(spdx_id) VALUES ('BSD-3-Clause-No-Nuclear-Warranty');
INSERT INTO license(spdx_id) VALUES ('GPL-3.0-only');
INSERT INTO license(spdx_id) VALUES ('LGPL-3.0-only');
INSERT INTO license(spdx_id) VALUES ('EFL-2.0');

INSERT INTO dependency_license(license_spdx_id, dependency_main_version, dependency_id, source, dependency_timestamp, dependency_project) VALUES
  ('MIT', '1.0.0', 'lodash', 'Found in file','2018-01-01T20:00:00+00:00', 'First Project'),
  ('BSD-3-Clause-No-Nuclear-Warranty', '2.3.3', 'mocha', 'Found in package.json','2018-01-01T20:00:00+00:00', 'First Project'),
  ('GPL-3.0-only', '2.3.4', 'rimraf', 'Found in GitHub', '2018-01-02T20:00:00+00:00', 'First Project'),
  ('LGPL-3.0-only', '3.0.0', 'spdx-correct', 'Found in file', '2018-01-02T20:00:00+00:00', 'First Project'),
  ('EFL-2.0', '5.5.0', 'semver', 'Found in file', '2018-01-02T20:00:00+00:00', 'First Project');

INSERT INTO vulnerability(id, description, info, title)
    VALUES (12345, 'Test vulnerability', '{"First Reference", "Second Reference""}', 'DDos'),
      (67890, 'Vulnerability Test', '{"Third Reference", "Fourth Reference"}', 'DDos');

INSERT INTO dependency_vulnerability(vulnerability_id, dependency_main_version, dependency_id, version_criteria, dependency_timestamp, dependency_project)
    VALUES (12345, '1.0.0', 'lodash', '<1.5.0','2018-01-01T20:00:00+00:00', 'First Project'),
      (67890, '5.5.0', 'semver', '>1.5.0', '2018-01-02T20:00:00+00:00', 'First Project');
