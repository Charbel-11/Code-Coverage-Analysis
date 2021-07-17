# Background
Code coverage analysis is a quality assurance task that aims at identifying the program elements that get executed during testing. 
This allows developers to identify untested sections and perform further testing accordingly. Different types of program elements 
can be used for this purpose. In this project, we consider methods, method call pairs, basic blocks, and basic block edges.

# Description
I was provided with a skeleton to perform the 4 types of coverage, a class to be tested, and 4 JUnit tests to validate my code. I was required to complete the missing parts of
InstrumenterClassVisitor.java and Profiler.java (look for “INSERT CODE HERE”). In addition, I had to:
1)	Display the dynamic call graph as a directed graph whose edges are labeled with the counts of each call.
2)	Print the percentage of the methods that were covered
3)	Print the percentage of the basic-blocks that were covered
4)	Display the dynamic CFG. The edges should be labeled with the counts of edge executions.

# Project Structure
* BasicBlockEdge.java: A class representing basic block edges. Note that a basic block profile element is represented as a string of the form "className|methodName|leaderIndex" where leaderIndex is the index of the leader instruction in the list of instructions of the corresponding method
* BasicBlockGeneratorMethodVisitor.java: This class is used to identify the basic blocks of each method
* Class1.java: The class to be tested
* CoverageHelper.java: A helper class that is used by the profiler to keep track of the covered program elements
* Instrumenter.java: The class that initiates instrumentation
* InstrumenterClassVisitor.java: Contains both classVisitor and methodVisitor
* MethodCall.java: A class representing method calls. Note that a method profile element is represented as a string of the form "className|methodName|methodSignature"
* Profiler.java: The main profiler class
* test/Class1TestEx.java: The test class containing 4 JUnit tests
