# Copyright 2022 Google LLC
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA

import sys
import json
import re

repository = sys.argv[1]
version_json_file = sys.argv[2]

print(f"Repository: {repository}")
print(f"Version JSON file: {version_json_file}")

forced_dependencies = {}
with open(version_json_file) as f:
    forced_dependencies = json.load(f)

print(f"forced dependencies: {forced_dependencies}")

pom_file_name = "pom.xml"
with open(pom_file_name, "r") as pom_file:
    pom_file_content = pom_file.read()

# Find the proper indent for the file. Using the same indent character as the
# file makes the resulting pom.xml natural-looking.
indent = ''
indent_match = re.search(r'^(\s+)<', pom_file_content, re.MULTILINE)
if indent_match:
    indent = indent_match.group(1)

# Add dependency elements in the dependencyManagement section of the root pom
# files. This is where Spring Cloud GCP manages dependencies.
section_to_add = ""
base_indent_count = 5
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
pom_file_updated_content = regex.sub(section_to_add, pom_file_content)

# Ensure the content of new the pom.xml is different; otherwise fail
if pom_file_updated_content == pom_file_content:
    print('The file is the same:')
    print(pom_file_content)
    sys.exit(1)

with open(pom_file_name, "w") as pom_file:
    pom_file.write(pom_file_updated_content)
