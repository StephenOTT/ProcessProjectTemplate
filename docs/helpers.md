# Helper Code-Snippets and Scripts

The following a collection of code-snippets and scripts that are commonly used.

All examples are Javascript based.

## Load Config.js as a Process Variable


```javascript
'use strict';
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
  if (typeof(persist) == 'undefined') {
    persist = false;
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
