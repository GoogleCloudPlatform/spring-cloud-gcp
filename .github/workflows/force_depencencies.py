
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
  xml.register_namespace('m', maven_namespace)
  parsed = xml.parse(pom_file)
  root = parsed.getroot()
  dependencyManagement = root.find('maven:dependencyManagement', namespace_map)
  print(dependencyManagement)
  for child in root:
    print(child.tag, child.attrib)
  pdb.set_trace()
