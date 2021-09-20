# thanakorn-firebase-be

The Google Cloud Endpoints that serves as a backend for the POC of Firebase authentication.

## Requirements and Setups
- The GCP and the Firebase projects must be created.
- The Firebase project must add the GCP project.
- The Email link authentication must be enabled in the Firebase project.
- The service account must be generated from Firebase console; it is used by the server for Admin SDK functionalities (e.g., getting and creating users).
- Add frontend host (e.g., thanakorn-firebase-fe.uk.r.appspot.com) to authorized domain in the Firebase project to allow sending login link email from the server.

## Build & Deployment
- gcloud config set project thanakorn-firebase-be
- mvn clean package appengine:deploy 
- mvn endpoints-framework:openApiDocs 
- gcloud endpoints services deploy target/openapi-docs/openapi.json

