# Spring Boot Starters for Google Client Libraries

Please see below for a list of additional preview starters provided, with dependencies and auto-configurations to work with corresponding Google Client Libraries.

These starters are not included in the Spring Framework on Google Cloud Bill of Materials (BOM), 
and should be explicitly added as dependencies. For example, to use the starter for Cloud Natural Language, 
add the following dependency to your `pom.xml`.

```xml
  <dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-language-spring-starter</artifactId>
    <version>{starters-version}-preview</version>
  </dependency>
```

| Client Library | Starter Maven Artifact |
|----------------| -----------------------|
|[java-accessapproval](https://github.com/googleapis/google-cloud-java/blob/v/java-accessapproval/README.md)|com.google.cloud:google-cloud-accessapproval-spring-starter|
|[java-accesscontextmanager](https://github.com/googleapis/google-cloud-java/blob/v/java-accesscontextmanager/README.md)|com.google.cloud:google-identity-accesscontextmanager-spring-starter|
|[java-aiplatform](https://github.com/googleapis/google-cloud-java/blob/v/java-aiplatform/README.md)|com.google.cloud:google-cloud-aiplatform-spring-starter|
|[java-api-gateway](https://github.com/googleapis/google-cloud-java/blob/v/java-api-gateway/README.md)|com.google.cloud:google-cloud-api-gateway-spring-starter|
|[java-apigee-connect](https://github.com/googleapis/google-cloud-java/blob/v/java-apigee-connect/README.md)|com.google.cloud:google-cloud-apigee-connect-spring-starter|
|[java-appengine-admin](https://github.com/googleapis/google-cloud-java/blob/v/java-appengine-admin/README.md)|com.google.cloud:google-cloud-appengine-admin-spring-starter|
|[java-artifact-registry](https://github.com/googleapis/google-cloud-java/blob/v/java-artifact-registry/README.md)|com.google.cloud:google-cloud-artifact-registry-spring-starter|
|[java-asset](https://github.com/googleapis/google-cloud-java/blob/v/java-asset/README.md)|com.google.cloud:google-cloud-asset-spring-starter|
|[java-assured-workloads](https://github.com/googleapis/google-cloud-java/blob/v/java-assured-workloads/README.md)|com.google.cloud:google-cloud-assured-workloads-spring-starter|
|[java-automl](https://github.com/googleapis/google-cloud-java/blob/v/java-automl/README.md)|com.google.cloud:google-cloud-automl-spring-starter|
|[java-bigqueryconnection](https://github.com/googleapis/google-cloud-java/blob/v/java-bigqueryconnection/README.md)|com.google.cloud:google-cloud-bigqueryconnection-spring-starter|
|[java-bigquerydatatransfer](https://github.com/googleapis/google-cloud-java/blob/v/java-bigquerydatatransfer/README.md)|com.google.cloud:google-cloud-bigquerydatatransfer-spring-starter|
|[java-bigqueryreservation](https://github.com/googleapis/google-cloud-java/blob/v/java-bigqueryreservation/README.md)|com.google.cloud:google-cloud-bigqueryreservation-spring-starter|
|[java-billing](https://github.com/googleapis/google-cloud-java/blob/v/java-billing/README.md)|com.google.cloud:google-cloud-billing-spring-starter|
|[java-billingbudgets](https://github.com/googleapis/google-cloud-java/blob/v/java-billingbudgets/README.md)|com.google.cloud:google-cloud-billingbudgets-spring-starter|
|[java-binary-authorization](https://github.com/googleapis/google-cloud-java/blob/v/java-binary-authorization/README.md)|com.google.cloud:google-cloud-binary-authorization-spring-starter|
|[java-channel](https://github.com/googleapis/google-cloud-java/blob/v/java-channel/README.md)|com.google.cloud:google-cloud-channel-spring-starter|
|[java-cloudbuild](https://github.com/googleapis/google-cloud-java/blob/v/java-cloudbuild/README.md)|com.google.cloud:google-cloud-build-spring-starter|
|[java-contact-center-insights](https://github.com/googleapis/google-cloud-java/blob/v/java-contact-center-insights/README.md)|com.google.cloud:google-cloud-contact-center-insights-spring-starter|
|[java-container](https://github.com/googleapis/google-cloud-java/blob/v/java-container/README.md)|com.google.cloud:google-cloud-container-spring-starter|
|[java-containeranalysis](https://github.com/googleapis/google-cloud-java/blob/v/java-containeranalysis/README.md)|com.google.cloud:google-cloud-containeranalysis-spring-starter|
|[java-data-fusion](https://github.com/googleapis/google-cloud-java/blob/v/java-data-fusion/README.md)|com.google.cloud:google-cloud-data-fusion-spring-starter|
|[java-datacatalog](https://github.com/googleapis/google-cloud-java/blob/v/java-datacatalog/README.md)|com.google.cloud:google-cloud-datacatalog-spring-starter|
|[java-dataplex](https://github.com/googleapis/google-cloud-java/blob/v/java-dataplex/README.md)|com.google.cloud:google-cloud-dataplex-spring-starter|
|[java-dataproc-metastore](https://github.com/googleapis/google-cloud-java/blob/v/java-dataproc-metastore/README.md)|com.google.cloud:google-cloud-dataproc-metastore-spring-starter|
|[java-dataproc](https://github.com/googleapis/google-cloud-java/blob/v/java-dataproc/README.md)|com.google.cloud:google-cloud-dataproc-spring-starter|
|[java-datastream](https://github.com/googleapis/google-cloud-java/blob/v/java-datastream/README.md)|com.google.cloud:google-cloud-datastream-spring-starter|
|[java-debugger-client](https://github.com/googleapis/google-cloud-java/blob/v/java-debugger-client/README.md)|com.google.cloud:google-cloud-debugger-client-spring-starter|
|[java-deploy](https://github.com/googleapis/google-cloud-java/blob/v/java-deploy/README.md)|com.google.cloud:google-cloud-deploy-spring-starter|
|[java-dialogflow](https://github.com/googleapis/google-cloud-java/blob/v/java-dialogflow/README.md)|com.google.cloud:google-cloud-dialogflow-spring-starter|
|[java-dlp](https://github.com/googleapis/google-cloud-java/blob/v/java-dlp/README.md)|com.google.cloud:google-cloud-dlp-spring-starter|
|[java-dms](https://github.com/googleapis/google-cloud-java/blob/v/java-dms/README.md)|com.google.cloud:google-cloud-dms-spring-starter|
|[java-document-ai](https://github.com/googleapis/google-cloud-java/blob/v/java-document-ai/README.md)|com.google.cloud:google-cloud-document-ai-spring-starter|
|[java-domains](https://github.com/googleapis/google-cloud-java/blob/v/java-domains/README.md)|com.google.cloud:google-cloud-domains-spring-starter|
|[java-essential-contacts](https://github.com/googleapis/google-cloud-java/blob/v/java-essential-contacts/README.md)|com.google.cloud:google-cloud-essential-contacts-spring-starter|
|[java-eventarc](https://github.com/googleapis/google-cloud-java/blob/v/java-eventarc/README.md)|com.google.cloud:google-cloud-eventarc-spring-starter|
|[java-filestore](https://github.com/googleapis/google-cloud-java/blob/v/java-filestore/README.md)|com.google.cloud:google-cloud-filestore-spring-starter|
|[java-functions](https://github.com/googleapis/google-cloud-java/blob/v/java-functions/README.md)|com.google.cloud:google-cloud-functions-spring-starter|
|[java-game-servers](https://github.com/googleapis/google-cloud-java/blob/v/java-game-servers/README.md)|com.google.cloud:google-cloud-game-servers-spring-starter|
|[java-gkehub](https://github.com/googleapis/google-cloud-java/blob/v/java-gkehub/README.md)|com.google.cloud:google-cloud-gkehub-spring-starter|
|[java-gsuite-addons](https://github.com/googleapis/google-cloud-java/blob/v/java-gsuite-addons/README.md)|com.google.cloud:google-cloud-gsuite-addons-spring-starter|
|[java-iam-admin](https://github.com/googleapis/google-cloud-java/blob/v/java-iam-admin/README.md)|com.google.cloud:google-iam-admin-spring-starter|
|[java-iamcredentials](https://github.com/googleapis/google-cloud-java/blob/v/java-iamcredentials/README.md)|com.google.cloud:google-cloud-iamcredentials-spring-starter|
|[java-ids](https://github.com/googleapis/google-cloud-java/blob/v/java-ids/README.md)|com.google.cloud:google-cloud-ids-spring-starter|
|[java-iot](https://github.com/googleapis/google-cloud-java/blob/v/java-iot/README.md)|com.google.cloud:google-cloud-iot-spring-starter|
|[java-language](https://github.com/googleapis/google-cloud-java/blob/v/java-language/README.md)|com.google.cloud:google-cloud-language-spring-starter|
|[java-managed-identities](https://github.com/googleapis/google-cloud-java/blob/v/java-managed-identities/README.md)|com.google.cloud:google-cloud-managed-identities-spring-starter|
|[java-memcache](https://github.com/googleapis/google-cloud-java/blob/v/java-memcache/README.md)|com.google.cloud:google-cloud-memcache-spring-starter|
|[java-monitoring-dashboards](https://github.com/googleapis/google-cloud-java/blob/v/java-monitoring-dashboards/README.md)|com.google.cloud:google-cloud-monitoring-dashboard-spring-starter|
|[java-network-management](https://github.com/googleapis/google-cloud-java/blob/v/java-network-management/README.md)|com.google.cloud:google-cloud-network-management-spring-starter|
|[java-networkconnectivity](https://github.com/googleapis/google-cloud-java/blob/v/java-networkconnectivity/README.md)|com.google.cloud:google-cloud-networkconnectivity-spring-starter|
|[java-notebooks](https://github.com/googleapis/google-cloud-java/blob/v/java-notebooks/README.md)|com.google.cloud:google-cloud-notebooks-spring-starter|
|[java-optimization](https://github.com/googleapis/google-cloud-java/blob/v/java-optimization/README.md)|com.google.cloud:google-cloud-optimization-spring-starter|
|[java-orchestration-airflow](https://github.com/googleapis/google-cloud-java/blob/v/java-orchestration-airflow/README.md)|com.google.cloud:google-cloud-orchestration-airflow-spring-starter|
|[java-orgpolicy](https://github.com/googleapis/google-cloud-java/blob/v/java-orgpolicy/README.md)|com.google.cloud:google-cloud-orgpolicy-spring-starter|
|[java-os-config](https://github.com/googleapis/google-cloud-java/blob/v/java-os-config/README.md)|com.google.cloud:google-cloud-os-config-spring-starter|
|[java-os-login](https://github.com/googleapis/google-cloud-java/blob/v/java-os-login/README.md)|com.google.cloud:google-cloud-os-login-spring-starter|
|[java-policy-troubleshooter](https://github.com/googleapis/google-cloud-java/blob/v/java-policy-troubleshooter/README.md)|com.google.cloud:google-cloud-policy-troubleshooter-spring-starter|
|[java-profiler](https://github.com/googleapis/google-cloud-java/blob/v/java-profiler/README.md)|com.google.cloud:google-cloud-profiler-spring-starter|
|[java-recaptchaenterprise](https://github.com/googleapis/google-cloud-java/blob/v/java-recaptchaenterprise/README.md)|com.google.cloud:google-cloud-recaptchaenterprise-spring-starter|
|[java-recommender](https://github.com/googleapis/google-cloud-java/blob/v/java-recommender/README.md)|com.google.cloud:google-cloud-recommender-spring-starter|
|[java-redis](https://github.com/googleapis/google-cloud-java/blob/v/java-redis/README.md)|com.google.cloud:google-cloud-redis-spring-starter|
|[java-resource-settings](https://github.com/googleapis/google-cloud-java/blob/v/java-resource-settings/README.md)|com.google.cloud:google-cloud-resource-settings-spring-starter|
|[java-retail](https://github.com/googleapis/google-cloud-java/blob/v/java-retail/README.md)|com.google.cloud:google-cloud-retail-spring-starter|
|[java-scheduler](https://github.com/googleapis/google-cloud-java/blob/v/java-scheduler/README.md)|com.google.cloud:google-cloud-scheduler-spring-starter|
|[java-security-private-ca](https://github.com/googleapis/google-cloud-java/blob/v/java-security-private-ca/README.md)|com.google.cloud:google-cloud-security-private-ca-spring-starter|
|[java-securitycenter](https://github.com/googleapis/google-cloud-java/blob/v/java-securitycenter/README.md)|com.google.cloud:google-cloud-securitycenter-spring-starter|
|[java-service-control](https://github.com/googleapis/google-cloud-java/blob/v/java-service-control/README.md)|com.google.cloud:google-cloud-service-control-spring-starter|
|[java-service-management](https://github.com/googleapis/google-cloud-java/blob/v/java-service-management/README.md)|com.google.cloud:google-cloud-service-management-spring-starter|
|[java-service-usage](https://github.com/googleapis/google-cloud-java/blob/v/java-service-usage/README.md)|com.google.cloud:google-cloud-service-usage-spring-starter|
|[java-servicedirectory](https://github.com/googleapis/google-cloud-java/blob/v/java-servicedirectory/README.md)|com.google.cloud:google-cloud-servicedirectory-spring-starter|
|[java-shell](https://github.com/googleapis/google-cloud-java/blob/v/java-shell/README.md)|com.google.cloud:google-cloud-shell-spring-starter|
|[java-speech](https://github.com/googleapis/google-cloud-java/blob/v/java-speech/README.md)|com.google.cloud:google-cloud-speech-spring-starter|
|[java-storage-transfer](https://github.com/googleapis/google-cloud-java/blob/v/java-storage-transfer/README.md)|com.google.cloud:google-cloud-storage-transfer-spring-starter|
|[java-talent](https://github.com/googleapis/google-cloud-java/blob/v/java-talent/README.md)|com.google.cloud:google-cloud-talent-spring-starter|
|[java-tasks](https://github.com/googleapis/google-cloud-java/blob/v/java-tasks/README.md)|com.google.cloud:google-cloud-tasks-spring-starter|
|[java-texttospeech](https://github.com/googleapis/google-cloud-java/blob/v/java-texttospeech/README.md)|com.google.cloud:google-cloud-texttospeech-spring-starter|
|[java-tpu](https://github.com/googleapis/google-cloud-java/blob/v/java-tpu/README.md)|com.google.cloud:google-cloud-tpu-spring-starter|
|[java-trace](https://github.com/googleapis/google-cloud-java/blob/v/java-trace/README.md)|com.google.cloud:google-cloud-trace-spring-starter|
|[java-video-intelligence](https://github.com/googleapis/google-cloud-java/blob/v/java-video-intelligence/README.md)|com.google.cloud:google-cloud-video-intelligence-spring-starter|
|[java-video-transcoder](https://github.com/googleapis/google-cloud-java/blob/v/java-video-transcoder/README.md)|com.google.cloud:google-cloud-video-transcoder-spring-starter|
|[java-vmmigration](https://github.com/googleapis/google-cloud-java/blob/v/java-vmmigration/README.md)|com.google.cloud:google-cloud-vmmigration-spring-starter|
|[java-vpcaccess](https://github.com/googleapis/google-cloud-java/blob/v/java-vpcaccess/README.md)|com.google.cloud:google-cloud-vpcaccess-spring-starter|
|[java-webrisk](https://github.com/googleapis/google-cloud-java/blob/v/java-webrisk/README.md)|com.google.cloud:google-cloud-webrisk-spring-starter|
|[java-websecurityscanner](https://github.com/googleapis/google-cloud-java/blob/v/java-websecurityscanner/README.md)|com.google.cloud:google-cloud-websecurityscanner-spring-starter|
|[java-workflow-executions](https://github.com/googleapis/google-cloud-java/blob/v/java-workflow-executions/README.md)|com.google.cloud:google-cloud-workflow-executions-spring-starter|
|[java-workflows](https://github.com/googleapis/google-cloud-java/blob/v/java-workflows/README.md)|com.google.cloud:google-cloud-workflows-spring-starter|
