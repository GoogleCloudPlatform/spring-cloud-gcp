# script for manual use: convert asciidoc to markdown

  # some replacements to fix code links with {project-root}
  sed -i "s/{project-root}/..\/..\/..\/../g" ../asciidoc/firestore.adoc
  sed -i "s/{project-root}/..\/..\/..\/../g" ../asciidoc/pubsub.adoc
  sed -i "s/{project-root}/..\/..\/..\/../g" ../asciidoc/spring-integration-pubsub.adoc


  function convertascii
  {
  local name="$1"
  sudo asciidoctor -b docbook ../asciidoc/$1.adoc
  sudo pandoc -f docbook -t gfm ../asciidoc/$1.xml -o $1.md --shift-heading-level-by=1
  }

  convertascii  getting-started
  convertascii  core
  convertascii  storage
  convertascii  sql
  convertascii  pubsub
  convertascii  spring-integration
  convertascii  spring-stream
  convertascii  spring-cloud-bus-pubsub
  convertascii  trace
  convertascii  logging
  convertascii  metrics
  convertascii  spanner
  convertascii  datastore
  convertascii  firestore
  convertascii  memorystore
  convertascii  bigquery
  convertascii  security-iap
  convertascii  security-firebase
  convertascii  vision
  convertascii  secretmanager
  convertascii  kms
  convertascii  config
  convertascii  cloudfoundry
  convertascii  kotlin
  convertascii  configuration
  convertascii  migration-guide-1.x
  convertascii  migration-guide-3.x

  convertascii  appendix
  convertascii  first-page
