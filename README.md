# CPSC454 Final Project
Created By:  
Gustavo Vazquez - gvazquez23@csu.fullerton.edu  
Smit Patel - smitpatel64@csu.fullerton.edu  
Sunny To - sunny14539@csu.fullerton.edu  

## Requirements
To run the program, you will need:  
1. The Eclipse IDE - https://www.eclipse.org/ide/
1. AWS Toolkit for Eclipse - https://aws.amazon.com/eclipse/
1. The Open-Source CP-ABE Library - https://github.com/junwei-wang/cpabe
1. A Database on DynamoDB - (Copy the one from Slide 8 of the Final Presentaion PPT)

## Setup
Use a Linux environment to download the CP-ABE library. Once downloaded, open Eclipse and create a new project. For the code to work properly, please name the java file `AmazonDynamoDBSample`. To import the library, right click on the project and hit `properties`. Then, navagate to `Java Build Path` and hit `Add External JARs...`. The jar files that should be included are:  
1. `cpabe-api-1.0.2.jar`
1. `cpabe-demo-1.0.2.jar`
1. `jpbc-api-1.2.1.jar`
1. `jpbc-plaf-1.2.1.jar`

On the toolbar, there should be an orange cube indicating AWS Toolkit for Eclipse. Hit the dropdown button and click `Preferences...`. Create a default profile and use the security credentials from you AWS account. Hit `Apply and Close` to save.

## Run the Program
To run the program, make sure your DynamoDB is in the region `us-west-2` on both the AWS browser and on AWS Toolkit for Eclipse. Hit the `Run` button on the IDE and a GUI should pop up. To make this work correctly, the second attribute should be either `student` or `staff`. Hitting the `Get Info` button will give the data of the user if the attributes allign with the policy set.
