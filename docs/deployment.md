# Deployment

There are several ways to deploy Camunda process files.  This project template follows a specific pattern that supported automated deployment with any deployment system that can preform HTTP requests to the Camunda REST API.

## Manual Deployment

1. [Postman Collection](ProcessProjectRenameMe.postman_collection)
1. [Postman Environment Variables](CamundaServer.postman_environment)

## `deploy.json`

The `deploy.json` file describes the [Camunda Process Deployment](https://docs.camunda.org/manual/7.7/reference/rest/deployment/post-deployment/).

The structure is as follows:
Example:

```json
{
  "deployment": {
    "deployment-name": "Process_Project_Template_ABC123",
    "enable-duplicate-filtering": false,
    "deploy-changed-only": false,
    "deployment-source": "Jenkins_Automated_Deployment",
    "tenant-id": "123456789",
    "files": {
      "config.json": "resources/config.json",
      "pay_taxes.bpmn": "bpmn/pay_taxes.bpmn"
    }
  },
  "migration": {
    "perform_migration": false,
    "plan": {}
  }
}
```

The `deployment` object contains the request body parameters of the Camunda [Deployment CreateAPI endpoint ](https://docs.camunda.org/manual/7.7/reference/rest/deployment/post-deployment/).  

The inner `files` object contains the key/value mapping of binary files that will be uploaded to camunda during the deployment.

The pattern for the `files` object is `"filename_as_saved_in_Camunda.extension":"path/to/file/in/repository.extension"`.  Example: `"config.json":"resources/config.json"`.

The `migration` object is for future use related to Camunda [Migrations](https://docs.camunda.org/manual/7.7/reference/rest/migration/), but is not currently functional/implemented.


## Automated Deployment

### Jenkins

A [Jenkins file](../Jenkinsfile) is provided in the root of this project that is a template for preforming Automated Deployments from a SCM repository such as a GitHub, GitLab, or BitBucket repository.

The typical use case is as follows:

1. Update your project files.
1. Create a "Release"
1. Web-hook is sent from the SCM repository to Jenkins
1. Jenkins downloads the release, processes the project files, and deploys the relevant files to Camunda through Camunda's REST API.
1. You can preform builds based on Branches in the SCM Repository if you configure Jenkins for this.
1. Is it recommended that Pull Request practices are followed.
1. The Camunda URL is set as a build parameter allowing Build-Time configuration of the Destination Camunda server.  This is useful for when you develop on a model similar to Dev-Stage-Prod.

#### Jenkinsfile details

1. The Jenkinsfile follows a multiple Stage, multiple step pipeline.
1. [BlueOcean](https://jenkins.io/projects/blueocean/) is recommended to simplify the setup process.
1. See the Requirements below to ensure you can run the build.

**Requirements:**

1. [Pipeline utility steps plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin): This allows the reading and parsing of the `deploy.json` file by jenkins.
1. The following [Script Approvals](https://wiki.jenkins-ci.org/display/JENKINS/Script+Security+Plugin/#ScriptSecurityPlugin-ScriptApproval) will be required:
    1. `method org.apache.commons.collections.KeyValue getKey`
    1. `method org.apache.commons.collections.KeyValue getValue`

#### Current Limitations

1. It is assumed that Jenkins can connect to the Camunda API without Basic Auth Protection.
1. Camunda Deployment-Name and Camunda Deployment-Source values must not contain spaces (this is checked during the build and will fail the build if spaces are found).
1. The JSON response from Camunda is not currently parsed correctly by Jenkins cURL implementation.  As a result any spaces in the values will cause the response to be cut off.
