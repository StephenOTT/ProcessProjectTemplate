# General Patterns

The following is a list of patterns that should be followed:

1. Files names and paths should **not** contain spaces or any non-URL friendly character.
1. Camunda `deployment-name` and `deployment-source` should not contain any spaces or non-URL friendly character.
1. BPMN, DMN, and CMMN process files should be separated into their respective root folders.
1. The contents of the BPMN, DMN, and CMMN folders can be structured however the developer thinks fits the structure of the project.
1. Reusable scripts should be stored in the `resources/scripts` folder.
1. The README.md file should be maintained with the latest project information at all times.
1. The `deploy.json` file in the `deployment` object supports all fields in the Camunda `/deployment/create` API
1. The `config.json` file is used to store common configurations used by one or many process files.  The structure of this JSON file is up to the developer.
1. The  `/resources` folder should contain all files that are used by process files.  Examples could be: FreeMarker templates, images, PDFs, text files, json files, etc.
1. The `/resources/forms` folder is generally used to store the Camunda Embedded Angular Form's `.html` files which are used in Camunda Tasklist.  This folder can be omitted or repurposed as required.  Other use cases, can be 
