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
pipeline {
  agent any
  parameters {
    string(name: 'CAMUNDA_URL', defaultValue: 'http://172.17.0.1:8081', description: 'URL of Camunda Instance.')
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

          for (Map.Entry<Integer, Integer> file : files.entrySet()) {
            echo "Key =  ${file.getKey()}, Value = ${file.getValue()}";
            echo fileExists(file.getValue())
          }

          for (Map.Entry<Integer, Integer> file : files.entrySet()) {
            if (fileExists(file.getValue())) {
              echo "${file.getKey()}:${file.getValue()} FOUND"
            } else {
              error("${file.getKey()}:${file.getValue()} CANNOT BE FOUND")
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

          try {
            deployConfig = readJSON file: 'deploy.json'
          } catch (Exception e) {
            error("Cannot read deploy.json file\nError:\n${e}")
          }
          try {
            deploymentObject = deployConfig['deployment']
          } catch (Exception e) {
            error("Cannot read deploy.json property: deployment\nError:\n${e}")
          }

          for (Map.Entry<Integer, Integer> param : deploymentObject.entrySet()) {
            if (param.getKey() != "files") {
              if (param.getKey().toString().contains(' ')) {
                error("Argument key: \"${param.getKey()}\" contains one or more spaces. Arguments keys cannot contain spaces.")
              } else if (param.getValue().toString().contains(' ')) {
                 error("Argument value \"${param.getValue}\" contains one or more spaces. Argument values cannot contain spaces.")
              }
              echo "Deployment parameter: ${param.getKey()}=${param.getValue}"
              fields << "--form-string ${param.getKey()}=${param.getValue}"
            }
          }

          echo "-------------------------------------------------------"
          echo "Building cURL File Parameters"

          echo "Files to be deployed:"
          def files = null
          try {
            files = deployConfig['deployment']['files']
          } catch (Exception e){
            error("Cannot read deploy.json property: deployment.files\nError:\n${e}")
          }

          echo files.toString()

          for (Map.Entry<Integer, Integer> file : files.entrySet()) {
            if (file.getKey().toString().contains(' ')) {
              error("Argument key: \"${file.getKey()}\" contains one or more spaces. File names (argument keys) cannot contain spaces.")
            } else if (file.getValue().toString().contains(' ')) {
              error("Argument value: \"${file.getValue()}\" contains one or more spaces. File paths (argument values) cannot contain spaces.")
            } else {
              fields << "-F ${file.getKey()}=@${file.getValue()}"
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
          def curlOutput = "curl -X POST --url ${CAMUNDA_URL}/engine-rest/deployment/create -H Accept:application/json ${CAMUNDA_PARAMETERS} -w \"%{http_code}\""
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
