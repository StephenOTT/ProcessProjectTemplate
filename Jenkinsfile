// Version 1.0
//-------------------------------------------------------------------------
// The following build provides the ability to deploy Camunda process files to the Camunda REST API
// The build has the following requirements:
// 1. Pipeline Utility Steps Plugin (https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin)
// 2. The following Script Approvals(https://wiki.jenkins-ci.org/display/JENKINS/Script+Security+Plugin/#ScriptSecurityPlugin-ScriptApproval)
//    will be required:
//        1. method org.apache.commons.collections.KeyValue getKey
//        2. method org.apache.commons.collections.KeyValue getValue
//        3. new java.util.AbstractMap$SimpleImmutableEntry java.lang.Object java.lang.Object
//-------------------------------------------------------------------------
// cURL was used to make the HTTP POST multipart/form-data request to Camunda.
// cUrl was used because the jenkins http-request plugin does not support multipart/form-data POST, and binary data POST.
//-------------------------------------------------------------------------
// NOTE: Jenkins Minimum Version Requirement: 2.46.3
//-------------------------------------------------------------------------
pipeline {
  agent any
  parameters {
    string(name: 'CAMUNDA_URL', defaultValue: 'CAMUNDA-URL-GOES-HERE', description: 'URL of Camunda Instance.')
    choice(name: 'CAMUNDA_ENV', choices: 'dev\naccept', description: 'What Config Environment Files should be used?')
    booleanParam(name: 'USE_BASIC_AUTH', defaultValue: false, description: 'Check this box if you want to use Camunda Basic Auth.')
    string(name: 'CAMUNDA_USERNAME', defaultValue: 'default_username', description: 'Camunda Basic Auth Username: Must not be empty if you use Basic Auth.')
    password(name: 'CAMUNDA_PASSWORD', defaultValue: 'default_password', description: 'Camunda Basic Auth Password: Must not be empty if you use Basic Auth.  WARNING: Passwords are exposed in the console output of this build script!!')
  }
  stages {
    stage('Validate Deployment Files:') {
      steps {
        script {
          // def hipchatPROJECT_NAME = '$PROJECT_NAME'
          // def hipchatBUILD_URL = '$BUILD_URL'
          // def hipchatBUILD_NUMBER = '$BUILD_NUMBER'
          // def hipchatHIPCHAT_CHANGES_OR_CAUSE = '$HIPCHAT_CHANGES_OR_CAUSE'
          // hipchatSend(
          //   message: """
          //     <strong>Build STARTED</strong><img src="https://dujrsrsgsd3nh.cloudfront.net/img/emoticons/zoidberg-1417754444@2x.png" alt="Zoidberg" height="40" width="40"><small>Woop woop woop woop!</small>
          //     <br>
          //     Camunda Process Project Deployment ( ${env.CAMUNDA_URL} ):
          //     <br>
          //     Job Name: <strong>${hipchatPROJECT_NAME}</strong>
          //     <br>
          //     <a href=\"${hipchatBUILD_URL}\">Build ${hipchatBUILD_NUMBER}</a> was ${hipchatHIPCHAT_CHANGES_OR_CAUSE}
          //     """,
          //   color: "YELLOW",
          //   failOnError: true,
          //   notify: true,
          //   room: "dev-camunda"
          // )
          
          def exists = fileExists "deploy_${params['CAMUNDA_ENV']}.json"
          echo "-------------------------------------------------------"
          echo "Looking for deploy_${params['CAMUNDA_ENV']}.json:"
          if (exists) {
            echo "deploy_${params['CAMUNDA_ENV']}.json found"
          } else {
            // sendErrorMessageToHipchat("Cannot find deploy_${params['CAMUNDA_ENV']}.json file.")
            error("deploy_${params['CAMUNDA_ENV']}.json cannot be found")
          }
          echo "-------------------------------------------------------"
        }
        script {
          def deployConfig = null
          def files = null
          try{
            deployConfig = readJSON file: "deploy_${params['CAMUNDA_ENV']}.json"
          } catch (Exception e) {
            // sendErrorMessageToHipchat("Cannot read deploy_${params['CAMUNDA_ENV']}.json file.<br>Error:<br>${e}")
            error("Cannot read deploy_${params['CAMUNDA_ENV']}.json file.\nError:\n${e}")
          }
          try{
            files = deployConfig['deployment']['files']
          } catch (Exception e) {
            // sendErrorMessageToHipchat("Cannot read deploy_${params['CAMUNDA_ENV']}.json property: deployment.files<br>Error:<br>${e}")
            error("Cannot read deploy_${params['CAMUNDA_ENV']}.json property: deployment.files\nError:\n${e}")
          }

          echo "-------------------------------------------------------"
          echo "Looking if each file listed in deploy_${params['CAMUNDA_ENV']}.json exists:"

          for (def e in mapToList(files)) {
            if (fileExists("${e.value}")) {
              echo "${e.key}:${e.value} FOUND"
            } else {
              // sendErrorMessageToHipchat("deploy_${params['CAMUNDA_ENV']}.json deployment.files: ${e.key}:${e.value} CANNOT BE FOUND")
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

          echo "Readingg deploy_${params['CAMUNDA_ENV']}.json"
          try {
            deployConfig = readJSON file: "deploy_${params['CAMUNDA_ENV']}.json"
          } catch (Exception e) {
            // sendErrorMessageToHipchat("Cannot read deploy_${params['CAMUNDA_ENV']}.json file.<br>Error:<br>${e}")
            error("Cannot read deploy_${params['CAMUNDA_ENV']}.json file\nError:\n${e}")
          }
          echo "-------------------------------------------------------"
          echo "Reading deploy_${params['CAMUNDA_ENV']}.json's deployment object:"
          try {
            deploymentObject = deployConfig['deployment']
          } catch (Exception e) {
            // sendErrorMessageToHipchat("Cannot read deploy_${params['CAMUNDA_ENV']}.json property: deployment<br>Error:<br>${e}")
            error("Cannot read deploy_${params['CAMUNDA_ENV']}.json property: deployment\nError:\n${e}")
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
                // sendErrorMessageToHipchat("Basic Auth Password is not set.")
                error("Basic Auth Password is not set.")
              }
            } else {
              // sendErrorMessageToHipchat("Basic Auth Username is not set.")
              error("Basic Auth Username is not set.")
            }
          } else {
            echo "Basic Auth is not enabled."
          }

          echo "-------------------------------------------------------"
          echo "Checking deployment object structure and building --form-string arguments:"
          for (e in mapToList(deploymentObject)) {
            if (e.key != "files") {
              if (e.key.toString().contains(' ')) {
                // sendErrorMessageToHipchat("deploy_${params['CAMUNDA_ENV']}.json deployment object: Argument key: \"${e.key}\" contains one or more spaces. Arguments keys cannot contain spaces.")
                error("Argument key: \"${e.key}\" contains one or more spaces. Arguments keys cannot contain spaces.")
              } else if (e.value.toString().contains(' ')) {
                  // sendErrorMessageToHipchat("deploy_${params['CAMUNDA_ENV']}.json deployment object: Argument value \"${e.value}\" contains one or more spaces. Argument values cannot contain spaces.")
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
          } catch (Exception e) {
            // sendErrorMessageToHipchat("Cannot read deploy_${params['CAMUNDA_ENV']}.json property: deployment.files<br>Error:<br>${e}")
            error("Cannot read deploy_${params['CAMUNDA_ENV']}.json property: deployment.files\nError:\n${e}")
          }

          echo files.toString()

          for (e in mapToList(files)) {
            if (e.key.toString().contains(' ')) {
              // sendErrorMessageToHipchat("deploy_${params['CAMUNDA_ENV']}.json deployment.files object: Argument key: \"${e.key}\" contains one or more spaces. File names (argument keys) cannot contain spaces.")
              error("Argument key: \"${e.key}\" contains one or more spaces. File names (argument keys) cannot contain spaces.")
            } else if (e.value.toString().contains(' ')) {
              // sendErrorMessageToHipchat("deploy_${params['CAMUNDA_ENV']}.json deployment.files object: Argument value: \"${e.value}\" contains one or more spaces. File paths (argument values) cannot contain spaces.")
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
          def curlOutput = "curl -X POST --write-out \"StatusCode=%{http_code}\" --url ${params.CAMUNDA_URL}/engine-rest/deployment/create -H Accept:application/json ${CAMUNDA_PARAMETERS}"
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
        script{
          def camundaResponse = null
          try{
          echo "Deploying to Camunda"
          camundaResponse = sh (
              script: '${CAMUNDA_CURL}',
              returnStdout: true
          )
          echo "Camunda Response:\n${camundaResponse}"
          } catch (Exception e) {
            // sendErrorMessageToHipchat("Something went wrong when starting using cURL:<br>${e}")
            error("Something went wrong when starting using cURL:\n${e}")
          }
          if (camundaResponse.contains("StatusCode=200") != true){
            // sendErrorMessageToHipchat("Did not Receive Status Code 200 From Camunda:<br><code>${camundaResponse}</code>")
            error("Did not Receive Status Code 200 From Camunda.")
          }
        }
        script {
          // echo "-------------------------------------------------------"
          // echo "Posting results to HipChat"
          // def hipchatPROJECT_NAME = '$PROJECT_NAME'
          // def hipchatBUILD_URL = '$BUILD_URL'
          // def hipchatBUILD_NUMBER = '$BUILD_NUMBER'
          // def hipchatHIPCHAT_CHANGES_OR_CAUSE = '$HIPCHAT_CHANGES_OR_CAUSE'
          // def hipchatBUILD_DURATION = '$BUILD_DURATION'
          // hipchatSend(
          //   message: """
          //     <strong>Build COMPLETE</strong><img src="https://dujrsrsgsd3nh.cloudfront.net/img/emoticons/goodnews-1417752451@2x.png" alt="Good News Everyone!" height="40" width="40"><small>Good News Everyone!</small>
          //     <br>
          //     Camunda Process Project Deployment ( ${env.CAMUNDA_URL} ):
          //     <br>
          //     Job Name: <strong>${hipchatPROJECT_NAME}</strong>
          //     <br>
          //     <a href=\"${hipchatBUILD_URL}\">Build ${hipchatBUILD_NUMBER}</a> was ${hipchatHIPCHAT_CHANGES_OR_CAUSE}
          //     <br>
          //     Build Duration: ${hipchatBUILD_DURATION}
          //     """,
          //   color: "GREEN",
          //   failOnError: true,
          //   notify: true,
          //   room: "MY-HIPCHAT-ROOM-GOES-HERE"
          // )
          echo "-------------------------------------------------------"
        }
      }
    }
  }
}

// Used because of Jenkins bug that does allow Loops to iterate over maps.
@NonCPS
def mapToList(depmap) {
  def dlist = []
  for (def entry2 in depmap) {
    dlist.add(new java.util.AbstractMap.SimpleImmutableEntry(entry2.key, entry2.value))
  }
  dlist
}

// def sendErrorMessageToHipchat(errorMessage) {
//   def projectName = '$PROJECT_NAME'
//   def buildUrl = '$BUILD_URL'
//   def buildNumber = '$BUILD_NUMBER'
//   def buildChanges = '$HIPCHAT_CHANGES_OR_CAUSE'
//   def buildDuration = '$BUILD_DURATION'

//   hipchatSend(
//     message: """
//       <strong>Build FAILURE</strong><img src="https://dujrsrsgsd3nh.cloudfront.net/img/emoticons/paddlin-1417756794@2x.png" alt="Thats a paddlin" height="40" width="40"><small>That's a paddlin'</small>
//       <br>
//       Camunda Process Project Deployment ( ${env.CAMUNDA_URL} ):
//       <br>
//       Job Name: <strong>${projectName}</strong>
//       <br>
//       <a href=\"${buildUrl}\">Build ${buildNumber}</a> was ${buildChanges}
//       <br>
//       Build Duration: ${buildDuration}
//       <br>
//       <strong>Error Message:</strong> ${errorMessage}
//       """,
//     color: "RED",
//     failOnError: true,
//     notify: true,
//     room: "MY-HIPCHAT-ROOM-GOES-HERE"
//   )
}
