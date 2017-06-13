//-------------------------------------------------------------------------
// The following build provides the ability to deploy Camunda process files to the Camunda REST API
// The build has the following requirements:
// 1. Pipeline Utility Steps Plugin (https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin)
// 2. The following Script Approvals(https://wiki.jenkins-ci.org/display/JENKINS/Script+Security+Plugin/#ScriptSecurityPlugin-ScriptApproval)
//    will be required:
//        1. method org.apache.commons.collections.KeyValue getKey
//        2. method org.apache.commons.collections.KeyValue getValue
//-------------------------------------------------------------------------
// cURL was used to make the HTTP POST multipart/form-data request to Camunda.
// cUrl was used because the jenkins http-request plugin does not support multipart/form-data POST, and binary data POST.
//-------------------------------------------------------------------------
// NOTE: Jenkins Minimum Version Requirement: 2.46.3
//-------------------------------------------------------------------------
pipeline {
  agent any
  parameters {
    string(name: 'CAMUNDA_URL', defaultValue: 'http://172.17.0.1:8081', description: 'URL of Camunda Instance.')
    booleanParam(name: 'USE_BASIC_AUTH', defaultValue: false, description: 'Check this box if you want to use Camunda Basic Auth.')
    string(name: 'CAMUNDA_USERNAME', defaultValue: 'default_username', description: 'Camunda Basic Auth Username: Must not be empty if you use Basic Auth.')
    password(name: 'CAMUNDA_PASSWORD', defaultValue: 'default_password', description: 'Camunda Basic Auth Password: Must not be empty if you use Basic Auth.  WARNING: Passwords are exposed in the console output of this build script!!')
  }
  stages {
    stage('Validate Deployment Files:') {
      steps {
        script {
          def exists = fileExists 'deploy.json'
          echo "-------------------------------------------------------"
          echo "Looking for deploy.json:"
          if (exists) {
            echo 'deploy.json found'
          } else {
            error("deploy.json cannot be found")
          }
          echo "-------------------------------------------------------"
        }
        script {
          def deployConfig = null
          def files = null
          try{
            deployConfig = readJSON file: 'deploy.json'
          } catch (Exception e) {
            error("Cannot read deploy.json file\nError:\n${e}")
          }
          try{
            files = deployConfig['deployment']['files']
          } catch (Exception e) {
            error("Cannot read deploy.json property: deployment.files\nError:\n${e}")
          }

          echo "-------------------------------------------------------"
          echo "Looking if each file listed in deploy.json exists:"
          for (e in files) {
            if (fileExists("${e.value}")) {
              echo "${e.key}:${e.value} FOUND"
            } else {
              error("${e.key}:${e.value} CANNOT BE FOUND")
            }
          }
        }
      }
    }
    stage('Build cURL Arguments') {
      steps {
        script {
          def fields = []
          echo "-------------------------------------------------------"
          echo "Building cURL base parameters:"
          def deployConfig = null
          def deploymentObject = null

          echo "Checking for deploy.json:"
          try {
            deployConfig = readJSON file: 'deploy.json'
          } catch (Exception e) {
            error("Cannot read deploy.json file\nError:\n${e}")
          }
          echo "-------------------------------------------------------"
          echo "Checking for deploy.json's deployment object:"
          try {
            deploymentObject = deployConfig['deployment']
          } catch (Exception e) {
            error("Cannot read deploy.json property: deployment\nError:\n${e}")
          }

          echo "-------------------------------------------------------"
          echo "Check if Basic Auth values are provided."
          if (params['USE_BASIC_AUTH'] == true){
            if (params['CAMUNDA_USERNAME'] != "default_username"){
              if (params['CAMUNDA_PASSWORD'].toString() != "default_password"){
                echo "Basic Auth enabled and values have been provided. Building -u argument."
                def basicAuth = "-u ${params.CAMUNDA_USERNAME}:${params.CAMUNDA_PASSWORD}"
                fields << basicAuth
              } else {
                error("Basic Auth Password is not set.")
              }
            } else {
              error("Basic Auth Username is not set.")
            }
          } else {
            echo "Basic Auth is not enabled."
          }

          echo "-------------------------------------------------------"
          echo "Checking deployment object structure and building --form-string arguments:"
          for (e in deploymentObject) {
            if (e.key != "files") {
              if (e.key.toString().contains(' ')) {
                error("Argument key: \"${e.key}\" contains one or more spaces. Arguments keys cannot contain spaces.")
              } else if (e.value.toString().contains(' ')) {
                 error("Argument value \"${e.value}\" contains one or more spaces. Argument values cannot contain spaces.")
              }
              echo "Deployment parameter: ${e.key}=${e.value}"
              fields << "--form-string ${e.key}=${e.value}"
            }
          }

          echo "-------------------------------------------------------"
          echo "Building -F arguments:"

          echo "Files to be deployed:"
          def files = null
          try {
            files = deployConfig['deployment']['files']
          } catch (Exception e){
            error("Cannot read deploy.json property: deployment.files\nError:\n${e}")
          }

          echo files.toString()

          for (e in files) {
            if (e.key.toString().contains(' ')) {
              error("Argument key: \"${e.key}\" contains one or more spaces. File names (argument keys) cannot contain spaces.")
            } else if (e.value.toString().contains(' ')) {
              error("Argument value: \"${e.value}\" contains one or more spaces. File paths (argument values) cannot contain spaces.")
            } else {
              fields << "-F ${e.key}=@${e.value}"
            }
          }

          echo "-------------------------------------------------------"
          echo "Building Concatinated Arguments"
          def output = fields.join(" ")
          env.CAMUNDA_PARAMETERS = output
        }
        script {
          echo "-------------------------------------------------------"
          echo "Building full cURL string:"
          def curlOutput = "curl -X POST --url ${params.CAMUNDA_URL}/engine-rest/deployment/create -H Accept:application/json ${CAMUNDA_PARAMETERS} -w \"%{http_code}\""
          echo "Final cURL string:"
          echo curlOutput
          echo "-------------------------------------------------------"
          echo "Saving cURL string into env variable \"CAMUNDA_CURL\":"
          env.CAMUNDA_CURL = curlOutput
          echo "-------------------------------------------------------"
        }
      }
    }
    stage('Deploy to Camunda') {
      steps {
        sh '''
          echo "-------------------------------------------------------\nDEPLOYING to Camunda:\n-------------------------------------------------------"
          response=$(${CAMUNDA_CURL})
          if [ $response != 200 ]
          then
            echo "-------------------------------------------------------\nERROR: Did not receive Status Code 200 from Camunda\n-------------------------------------------------------"
            exit 1
          else
            echo "-------------------------------------------------------\nSUCCESS: Received Status Code 200: Successfully Deployed to Camunda\n-------------------------------------------------------"
          fi
        '''
      }
    }
  }
}
