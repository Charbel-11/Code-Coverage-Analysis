import java.util.ArrayList;

public class Profiler {
	private static CoverageHelper helper = new CoverageHelper();
	// EECE 334: INSERT CODE HERE
	private static String curBlock;
	public static int methodCount, basicBlockCount;
	
	static Thread shutdownHook;
	
	static class ProfilerShutdownHandler extends Thread implements Runnable {	
        public ProfilerShutdownHandler() {
		    super();
		    curBlock = "#";
		    methodCount = 0;
        }
	
        public void run() {
		    System.out.println("Shutdown hook running");
		    
		    //Generating the DOT code
		    System.out.println("\n--- Generating DOT code ----\n");
		    System.out.println("digraph{");
		    for(MethodCall mc:helper.coveredMethodCalls)
		    	if(helper.coveredMethods.contains(mc.caller) && helper.coveredMethods.contains(mc.callee) && mc.toString().indexOf("<init>")<0)
		    		System.out.println(mc.asDOTEntry());
	        System.out.println("}\n");
	        
			double percentage = (double)(helper.coveredMethods.size()+1)/methodCount; //+1 for init
			percentage*=100;
			System.out.println(percentage + "% of the methods were covered");
			
			percentage = (double)helper.coveredBasicBlocks.size()/basicBlockCount; 
			percentage*=100;
			System.out.println(percentage + "% of the basic blocks were covered\n");
			
		    System.out.println("digraph{");
		    for(BasicBlockEdge be : helper.coveredBasicBlockEdges)
		    	if(helper.coveredBasicBlocks.contains(be.source) && helper.coveredBasicBlocks.contains(be.target))
		    		System.out.println(be.toString());
	        System.out.println("}");
        }
	}
	
	public static void simulateStart() {
		System.out.println("Starting Profiler");
		Runtime.getRuntime().addShutdownHook(shutdownHook = new ProfilerShutdownHandler());
	}
	
	static {
		simulateStart();
    }
		 
	public static CoverageHelper getCoverageHelper() {return helper;}
	
	public static void handleMethodEntry(String className, String methodName, String methodSignature) {
		// EECE 334: INSERT CODE HERE
		String curMethod = className+'|'+methodName+'|'+methodSignature;
		helper.coverMethod(curMethod);
	}
	
	public static void handleMethodInvoke(
			String srcClassName,
			String srcMethodName,
			String srcMethodSignature,
			String dstClassName,
			String dstMethodName,
			String dstMethodSignature
			) {
		
		// EECE 334: INSERT CODE HERE
		String caller = srcClassName+'|'+srcMethodName+'|'+srcMethodSignature;
		String callee = dstClassName+'|'+dstMethodName+'|'+dstMethodSignature;	
		MethodCall curCall = new MethodCall(caller, callee);
		
		if (!helper.coveredMethodCalls.contains(curCall)) { helper.coverMethodCall(curCall); return ;}
		
	    for(MethodCall mc:helper.coveredMethodCalls) {
	    	if (mc.equals(curCall)) { mc.incrementCount(); break; }
	    }
	}
	
	public static void handleBasicBlockEntry(String className, String methodName, String methodSignature, String leaderIndex) {
		// EECE 334: INSERT CODE HERE
		String nextBlock = className+'|'+methodName+'|'+methodSignature+'|'+leaderIndex;
		helper.coverBasicBlock(nextBlock);
		
		if (curBlock != "#") {
			BasicBlockEdge curEdge = new BasicBlockEdge(curBlock, nextBlock);		
			if (!helper.coveredBasicBlockEdges.contains(curEdge)) { helper.coverBasicBlockEdge(curEdge); }
			else {
			    for(BasicBlockEdge be :helper.coveredBasicBlockEdges) {
			    	if (be.equals(curEdge)) { be.incrementCount(); break; }
			    }
			}
		}
		curBlock = nextBlock;
	}
	
	public static void handleLineNumber(String className, String methodName, String methodSignature, String line) {
		//System.out.println("---------- Reached line " + line);
	}
	
}
