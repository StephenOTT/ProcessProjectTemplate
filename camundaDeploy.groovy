@Grapes([
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.5.3'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7'),
  @GrabConfig(systemClassLoader=true)
])
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.content.InputStreamBody
import org.apache.http.entity.mime.content.StringBody
import groovyx.net.http.*


void sendMultiPartFile(String multipartImageFile) {
  def http = new HTTPBuilder("http://www.localhost:8081/engine-rest/deployment/create")

  http.request(Method.POST) { req ->

  requestContentType: "multipart/form-data"

  MultipartEntity multiPartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)


  // Adding another string parameter "city"

  multiPartContent.addPart("deployment-name", "jenkins deployment")
  multiPartContent.addPart("enable-duplicate-filtering", false)
  multiPartContent.addPart("deploy-changed-only", false)
  multiPartContent.addPart("mybpmn.bpmn", new InputStreamBody(multipartImageFile.inputStream, multipartImageFile.contentType, multipartImageFile.originalFilename))

  req.setEntity(multiPartContent)

  response.success = { resp ->

        if (resp.statusLine.statusCode == 200) {

                  // response handling

                   }
            }
      }
}

sendMultiPartFile(readFile('bpmn/pay_taxes.bpmn'))
