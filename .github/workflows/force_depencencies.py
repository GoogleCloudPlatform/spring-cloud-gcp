
import sys
import json

import pdb

import xml.etree.ElementTree as xml

version_json_file = sys.argv[1]

forced_dependencies = {}
with open(version_json_file) as f:
  forced_dependencies = json.load(f)

pom_file_name = "pom.xml"
with open(pom_file_name) as pom_file:
  maven_namespace = "http://maven.apache.org/POM/4.0.0"
  namespace_map = {
    'maven': maven_namespace
  }
#  xml.register_namespace('m', maven_namespace)
  parsed = xml.parse(pom_file)
  root = parsed.getroot()
  dependency_management = root.find('maven:dependencyManagement', namespace_map)
  dependency_management_dependencies = dependency_management.find('maven:dependencies', namespace_map)
  for forced_dependency in forced_dependencies:
    forced_version = forced_dependencies[forced_dependency]
    elements = forced_dependency.split(':')
    group_id = elements[0]
    artifact_id = elements[1]
    dependency_element = xml.Element('dependency')
    group_id_element = xml.Element('groupId')
    dependency_element.append(group_id_element)
    group_id_element.text = group_id
    artifact_id_element = xml.Element('artifactId')
    dependency_element.append(artifact_id_element)
    artifact_id_element.text = artifact_id
    version_element = xml.Element('version')
    version_element.text = forced_version
    dependency_element.append(version_element)
    dependency_management_dependencies.insert(0, dependency_element)
    pdb.set_trace()
  for child in root:
    print(child.tag, child.attrib)
  parsed.write("pom_updated.xml")

