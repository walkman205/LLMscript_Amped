This is the guidance for how to use the script for running prompts for the collection of collaborative questions. If you have any questions, email me: yw3060@nyu.edu

The public github repo URL: https://github.com/walkman205/LLMscript_Amped

Here is a list of applications you need to have:
IDEA: https://www.jetbrains.com/idea/download/

JDK: https://www.oracle.com/java/technologies/downloads/

Maven: in the github

Step 0: Download necessary files (One time)

Download the IDEA and JDK 8 (not JDK 1.8) using the URL above, it doesn’t matter where you download it. IDEA will find the JDK environment.

Download the three folders from Git, the “GPTUtil” folder is the project; the “maven” thing will be used for the coding environment; the “example questions” is for testing the project.

Step 1: Get maven environment (One time)  

Open IDEA, go to “File”->”Open”, select the path to pom.xml

Right click on the “pom.xml”, you should see something like “Add this file as maven”, reload the project, when you see a blue “m” before pom.xml, it works. (If you don’t see the option, create a new empty project using maven as the generator, then try again.)

Go to the path	“GPTUtil/src/main/java/com.gpt/GPTApplication” in IDEA, open the file. You will see a “M” on the right bar. Click it, then click the gear; find the “Maven settings”.

Make sure you have a “Maven3 home path”. (usually it will automatically download);

Change the “User settings file” to “…/apache-maven-3.6.3/conf/settings.xml” (from git);

Make sure “Local repository” exists, or create one. (can be empty, for the dependencies). Now, refresh the “maven setting” page, wait and it will download the dependencies.

Go to the path	“.../example questions/keys.txt”, enter your own API key for the GPT models; local model, cloud model.

Click the “run/play” button on the mid-top for “GPT Application”. It should work!


Step 2: (when you can successfully run “GPTApplication”)

Go to the URL in your browser: http://127.0.0.1:9999/index.html

Copy the path “.../example questions/questions/”. It will find the files in the txt format. Then, do the same for the pre prompts.

Click Run. After a while, find the report in the “example questions” folder.

