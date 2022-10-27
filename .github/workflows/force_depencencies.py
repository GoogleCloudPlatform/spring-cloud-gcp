
import sys
import json
import re

import pdb

repository = sys.argv[1]
version_json_file = sys.argv[1]

print(f"Repository: {repository}")
print(f"Version JSON file: {version_json_file}")

forced_dependencies = {}
with open(version_json_file) as f:
  forced_dependencies = json.load(f)

pom_file_name = "pom.xml"
with open(pom_file_name, "r") as pom_file:
  pom_file_content = pom_file.read()

# Find the proper indent for the file. Using the same indent character as the
# file makes the resulting pom.xml natural-looking.
indent = ''
indent_match = re.search(r'^(\s+)<', pom_file_content, re.MULTILINE)
if indent_match:
  indent = indent_match.group(1)

section_to_add = ""
base_indent_count = 3
for forced_dependency in forced_dependencies:
  elements = forced_dependency.split(':')
  group_id = elements[0]
  artifact_id = elements[1]
  forced_version = forced_dependencies[forced_dependency]
  if artifact_id.endswith('-bom'):
    section_to_add += f'''
    {indent*base_indent_count}<dependency>
    {indent*(base_indent_count + 1)}<groupId>{group_id}</groupId>
    {indent*(base_indent_count + 1)}<artifactId>{artifact_id}</artifactId>
    {indent*(base_indent_count + 1)}<version>{forced_version}</version>
    {indent*(base_indent_count + 1)}<type>pom</type>
    {indent*(base_indent_count + 1)}<scope>import</scope>
    {indent*base_indent_count}</dependency>
    '''
  else:
    section_to_add += f'''
    {indent*base_indent_count}<dependency>
    {indent*(base_indent_count + 1)}<groupId>{group_id}</groupId>
    {indent*(base_indent_count + 1)}<artifactId>{artifact_id}</artifactId>
    {indent*(base_indent_count + 1)}<version>{forced_version}</version>
    {indent*base_indent_count}</dependency>
    '''

regex = re.compile(r"^.*x-cross-repo-check-dependency-mark.*$", re.MULTILINE)
# pdb.set_trace()
pom_file_content = regex.sub(section_to_add, pom_file_content)

with open(pom_file_name, "w") as pom_file:
  pom_file.write(pom_file_content)

