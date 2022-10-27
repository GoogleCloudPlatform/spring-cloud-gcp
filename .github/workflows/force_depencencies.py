
import sys
import json
import re

import pdb

version_json_file = sys.argv[1]

forced_dependencies = {}
with open(version_json_file) as f:
  forced_dependencies = json.load(f)

section_to_add = ""
for forced_dependency in forced_dependencies:
  elements = forced_dependency.split(':')
  group_id = elements[0]
  artifact_id = elements[1]
  forced_version = forced_dependencies[forced_dependency]
  if artifact_id.endswith('-bom'):
    section_to_add += f'''
          <dependency>
            <groupId>{group_id}</groupId>
            <artifactId>{artifact_id}</artifactId>
            <version>{forced_version}</version>
            <type>pom</type>
            <scope>import</scope>
          </dependency>
    '''
  else:
    section_to_add += f'''
          <dependency>
            <groupId>{group_id}</groupId>
            <artifactId>{artifact_id}</artifactId>
            <version>{forced_version}</version>
          </dependency>
    '''

pom_file_name = "pom.xml"
with open(pom_file_name, "r") as pom_file:
  pom_file_content = pom_file.read()
regex = re.compile(r"^.*x-cross-repo-check-dependency-mark.*$")
pdb.set_trace()
pom_file_content = regex.sub(section_to_add, pom_file_content)

with open(pom_file_name, "w") as pom_file:
  pom_file.write(pom_file_content)

