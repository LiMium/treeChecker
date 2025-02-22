# treeChecker
Check a direc-tree of files for red flags using OpenAI API compatible service

It is a simple program that iterates recursively through a directory hierarchy and analyses each file (if its name matches
the provided regex), by passing its contents along with the prompt to an LLM.

See sample.sh for an example of how to invoke this.

Note: You will need to either download treeChecker.jar from the releases, or build the project with IntelliJ.
