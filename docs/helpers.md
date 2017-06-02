# Helper Code-Snippets and Scripts

The following a collection of code-snippets and scripts that are commonly used.

All examples are Javascript based.

## Load Config.js into memory and into a Process Variable(optional)

```javascript
/**
 * Load configuration file as a SPIN JSON variable in-memory and optionally as a process variable.
 *
 * @param string fileName The name of the configuration file in the deployment.
 * @param string key The top level JSON key in the configuration file that will be saved, and other keys/objects are omitted.
 * @param boolean persist Whether to save the configuration as a process variable.
 * @return SPIN JSON Object
 */
function loadConfig(fileName, key, persist)
{
  'use strict';

  if (typeof(persist) == 'undefined') {
    persist = false;
  }

  if (typeof(key) == 'undefined') {
    key = null;
  }

  var processDefinitionId = execution.getProcessDefinitionId();
  var deploymentId = execution.getProcessEngineServices().getRepositoryService().getProcessDefinition(processDefinitionId).getDeploymentId();
  var resource = execution.getProcessEngineServices().getRepositoryService().getResourceAsStream(deploymentId, fileName);

  var Scanner = Java.type('java.util.Scanner');

  var scannerResource = new Scanner(resource, 'UTF-8');

  var configText = scannerResource.useDelimiter('\\Z').next();
  scannerResource.close();

  var configAsJson = S(configText);

  if (key === null) {
    var config = configAsJson;
  } else {
    if (!configAsJson.hasProp(key)) {
      throw 'Key "' + key + '" does not exist.';
    }
    var config = configAsJson.prop(key);
  }

  if (persist) {
    execution.setVariable('_config', config);
  }

  return config;
}

loadConfig('config.json', 'myProcess', true);
// loadConfig('config.json');
// loadConfig('config.json', null, true);
// loadConfig('config.json', null, false);
// loadConfig('config.json', 'myprocess');
// loadConfig('config.json', 'myprocess', true);
// loadConfig('config.json', 'myprocess', false);
```


## Trigger/Throw a Message within a Process

As of Camunda 7.7, in order to throw a message from one process to another using the internal Java API, you must use an expression.
The following is an example of using a expression to trigger a message:

```
${execution.getProcessEngineServices().getRuntimeService().createMessageCorrelation("work").correlateWithResult()}
```
See the [Camunda Runtime Service (Java API) Docs](https://docs.camunda.org/javadoc/camunda-bpm-platform/7.7/org/camunda/bpm/engine/RuntimeService.html) for more options.

## Load Deployment Resource into memory

```javascript
/**
 * Load deployment resource as a String in-memory.
 *
 * @param string fileName The name of the deployment resource file.
 * @return string The deployment resource as a String.
 */
function getResourceAsString(fileName)
{
  'use strict';

  var processDefinitionId = execution.getProcessDefinitionId();
  var deploymentId = execution.getProcessEngineServices().getRepositoryService().getProcessDefinition(processDefinitionId).getDeploymentId();
  var resource = execution.getProcessEngineServices().getRepositoryService().getResourceAsStream(deploymentId, fileName);

  var Scanner = Java.type('java.util.Scanner');

  var scannerResource = new Scanner(resource, 'UTF-8');

  var resourceAsString = scannerResource.useDelimiter('\\Z').next();
  scannerResource.close();

  return resourceAsString;
}

var resource = getResourceAsString('emailTemplate.ftl');
```

## Render FreeMarker template into memory

### Javascript

Version 1, as a variable:

```javascript

var renderedTemplate = function()
{
  'use strict';

  var placeholderValues = {
    "firstName": "John",
    "lastName": "Smith"
  }

  var ScriptEngine = new JavaImporter(javax.script);

  with (ScriptEngine) {
    var manager = new ScriptEngineManager();
    var engine = manager.getEngineByName('freemarker');

    var bindings = engine.createBindings();
    bindings.put('placeholders', placeholderValues);

    var rendered = engine.eval(content, bindings);

    return rendered;
  }
}
```

Version 2, As a function:

```javascript
/**
 * Evaluate/Render a FreeMarker template
 *
 * @param string content The string content of a FreeMarker template.
 * @param string object The KeyValue object/JSON object for placeholder bindings.
 * @return string The rendered FreeMarker template.
 */
function renderFreeMarkerTemplate(content, placeholderValues)
{
  'use strict';

  var ScriptEngine = new JavaImporter(javax.script);

  with (ScriptEngine) {
    var manager = new ScriptEngineManager();
    var engine = manager.getEngineByName('freemarker');

    var bindings = engine.createBindings();
    bindings.put('placeholders', placeholderValues);

    var rendered = engine.eval(content, bindings);

    return rendered;
  }
}

var placeholderValues = {
   "firstName": "John",
   "lastName": "Smith"
}

var renderedTemplate = renderFreeMarkerTemplate(content, placeholderValues);

```
where `content` is the string content of a FreeMarker template file.


### FreeMarker Template

```FreeMarker
This is a sample FreeMarker template file.
My First Name: ${placeholders.firstName}
My Last Name: ${placeholders.lastName}
```
